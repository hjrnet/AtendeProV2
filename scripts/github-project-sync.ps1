param(
    [string] $Repo = "hjrnet/AtendeProV2",
    [string] $ProjectTitle = "AtendePro Roadmap",
    [string[]] $Releases = @("R18", "R19", "R20", "R21", "R22", "R23", "R24", "R25", "R26", "R27", "R28"),
    [switch] $Apply
)

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$ReleaseStatusPath = Join-Path $RepoRoot "docs\RELEASE_STATUS.yaml"
$Owner = $Repo.Split("/")[0]

$Labels = @(
    @{ name = "release/R18"; color = "0E8A16"; description = "Release R18" },
    @{ name = "release/R19"; color = "0E8A16"; description = "Release R19" },
    @{ name = "vertical/nutri"; color = "1D76DB"; description = "Nutri Pro" },
    @{ name = "vertical/beauty"; color = "D93F0B"; description = "Beauty Pro" },
    @{ name = "vertical/growth"; color = "5319E7"; description = "Growth e comercial" },
    @{ name = "area/backend"; color = "0052CC"; description = "Backend" },
    @{ name = "area/frontend"; color = "FBCA04"; description = "Frontend web" },
    @{ name = "area/mobile"; color = "C5DEF5"; description = "Mobile" },
    @{ name = "area/qa"; color = "BFDADC"; description = "QA e validacao" },
    @{ name = "area/devops"; color = "006B75"; description = "DevOps" },
    @{ name = "area/docs"; color = "D4C5F9"; description = "Documentacao" },
    @{ name = "priority/P0"; color = "B60205"; description = "Prioridade critica" },
    @{ name = "priority/P1"; color = "D93F0B"; description = "Prioridade alta" },
    @{ name = "priority/P2"; color = "FBCA04"; description = "Prioridade normal" },
    @{ name = "status/blocked"; color = "B60205"; description = "Bloqueado" },
    @{ name = "codex/autopilot"; color = "5319E7"; description = "Executado por autopilot" },
    @{ name = "codex/multiagente"; color = "5319E7"; description = "Revisao multiagente" }
)

function Invoke-Gh {
    param([string[]] $Arguments)

    if (-not $Apply) {
        Write-Host ("DRY-RUN gh " + ($Arguments -join " "))
        return ""
    }

    $output = & gh @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "gh falhou: gh $($Arguments -join ' ')"
    }
    return $output
}

function Assert-GhReady {
    if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
        throw "GitHub CLI nao encontrado. Instale gh ou rode este script em ambiente com gh disponivel."
    }

    if ($Apply) {
        & gh auth status *> $null
        if ($LASTEXITCODE -ne 0) {
            throw "GitHub CLI nao autenticado. Rode: gh auth login --web --scopes `"repo,project`""
        }
    }
}

function Get-ReleasesFromStatus {
    if (-not (Test-Path $ReleaseStatusPath)) {
        throw "Arquivo nao encontrado: $ReleaseStatusPath"
    }

    $lines = Get-Content -Path $ReleaseStatusPath
    $releaseMap = @{}
    $currentRelease = $null
    $insideTasks = $false

    foreach ($line in $lines) {
        if ($line -match "^  (R[0-9]+):") {
            $currentRelease = $Matches[1]
            $insideTasks = $false
            if ($Releases -contains $currentRelease) {
                $releaseMap[$currentRelease] = [ordered]@{
                    name = $currentRelease
                    title = $currentRelease
                    status = ""
                    tasks = @()
                }
            }
            continue
        }

        if (-not $currentRelease -or -not ($Releases -contains $currentRelease)) {
            continue
        }

        if ($line -match '^\s+name:\s+"(.+)"') {
            $releaseMap[$currentRelease].title = $Matches[1]
            continue
        }

        if ($line -match "^\s+status:\s+([A-Z_]+)") {
            $releaseMap[$currentRelease].status = $Matches[1]
            continue
        }

        if ($line -match "^\s+tasks:") {
            $insideTasks = $true
            continue
        }

        if ($insideTasks -and $line -match "^\s+(TASK-[A-Z0-9-]+):\s+([A-Z_]+)") {
            $releaseMap[$currentRelease].tasks += [pscustomobject]@{
                id = $Matches[1]
                status = $Matches[2]
            }
        }
    }

    return $releaseMap
}

function Get-TaskTitle {
    param(
        [string] $TaskId,
        [string] $ReleaseTitle
    )

    if ($TaskId -like "TASK-NUTRI-*") {
        return "$TaskId — Nutri Pro"
    }
    if ($TaskId -like "TASK-BEAUTY-*") {
        return "$TaskId — Beauty Pro"
    }
    if ($TaskId -like "TASK-GROWTH-*") {
        return "$TaskId — Growth"
    }
    return "$TaskId — $ReleaseTitle"
}

function Get-TaskLabels {
    param(
        [string] $Release,
        [string] $TaskId,
        [string] $Status
    )

    $labels = @("release/$Release", "priority/P1", "codex/autopilot", "codex/multiagente")

    if ($TaskId -like "TASK-NUTRI-*") {
        $labels += "vertical/nutri"
        $labels += "area/backend"
        $labels += "area/frontend"
    }
    elseif ($TaskId -like "TASK-BEAUTY-*") {
        $labels += "vertical/beauty"
    }
    elseif ($TaskId -like "TASK-GROWTH-*") {
        $labels += "vertical/growth"
        $labels += "area/frontend"
    }

    if ($Status -eq "PENDENTE") {
        $labels += "priority/P1"
    }
    elseif ($Status -eq "CONCLUIDA") {
        $labels += "priority/P2"
    }

    return ($labels | Select-Object -Unique)
}

function Ensure-Labels {
    $labelsFinais = @($Labels)
    foreach ($release in $Releases) {
        $labelsFinais += @{
            name = "release/$release"
            color = "0E8A16"
            description = "Release $release"
        }
    }

    foreach ($label in ($labelsFinais | Sort-Object name -Unique)) {
        Invoke-Gh @("label", "create", $label.name, "--repo", $Repo, "--color", $label.color, "--description", $label.description, "--force") | Out-Null
    }
}

function Ensure-Milestone {
    param(
        [string] $Title,
        [string] $Description
    )

    if (-not $Apply) {
        Write-Host "DRY-RUN milestone ensure: $Title"
        return 0
    }

    $existing = gh api "repos/$Repo/milestones?state=all" --jq ".[] | select(.title == `"$Title`") | .number"
    if ($existing) {
        return [int] $existing
    }

    $created = gh api "repos/$Repo/milestones" -f "title=$Title" -f "description=$Description" --jq ".number"
    return [int] $created
}

function Ensure-Project {
    if (-not $Apply) {
        Write-Host "DRY-RUN project ensure: $ProjectTitle"
        return 0
    }

    $existing = gh project list --owner $Owner --format json | ConvertFrom-Json
    $project = $existing.projects | Where-Object { $_.title -eq $ProjectTitle } | Select-Object -First 1
    if ($project) {
        return [int] $project.number
    }

    gh project create --owner $Owner --title $ProjectTitle | Out-Null
    $updated = gh project list --owner $Owner --format json | ConvertFrom-Json
    $created = $updated.projects | Where-Object { $_.title -eq $ProjectTitle } | Select-Object -First 1
    if (-not $created) {
        throw "Project criado, mas numero nao encontrado pelo gh project list."
    }
    return [int] $created.number
}

function Ensure-Issue {
    param(
        [string] $TaskId,
        [string] $Title,
        [string] $Body,
        [string[]] $Labels,
        [string] $MilestoneTitle,
        [int] $ProjectNumber
    )

    if (-not $Apply) {
        Write-Host "DRY-RUN issue ensure: $Title"
        return
    }

    $search = gh issue list --repo $Repo --state all --search "$TaskId in:title" --json number,url,title | ConvertFrom-Json
    $issue = $search | Select-Object -First 1
    $labelCsv = $Labels -join ","

    if ($issue) {
        gh issue edit $issue.number --repo $Repo --title $Title --body $Body --add-label $labelCsv --milestone $MilestoneTitle | Out-Null
        $issueUrl = $issue.url
    }
    else {
        $issueUrl = gh issue create --repo $Repo --title $Title --body $Body --label $labelCsv --milestone $MilestoneTitle
    }

    if ($ProjectNumber -gt 0) {
        gh project item-add $ProjectNumber --owner $Owner --url $issueUrl | Out-Null
    }
}

Assert-GhReady
$releaseMap = Get-ReleasesFromStatus

Write-Host "Repositorio: $Repo"
Write-Host "Project: $ProjectTitle"
Write-Host "Modo: $(if ($Apply) { 'APPLY' } else { 'DRY-RUN' })"

Ensure-Labels
$projectNumber = Ensure-Project

foreach ($release in $Releases) {
    if (-not $releaseMap.Contains($release)) {
        continue
    }

    $releaseInfo = $releaseMap[$release]
    Ensure-Milestone -Title $releaseInfo.title -Description "Milestone sincronizada do AtendePro para $release." | Out-Null

    foreach ($task in $releaseInfo.tasks) {
        $title = Get-TaskTitle -TaskId $task.id -ReleaseTitle $releaseInfo.title
        $labels = Get-TaskLabels -Release $release -TaskId $task.id -Status $task.status
        $body = @"
## Origem

- Release: $release
- Status local: $($task.status)
- Fonte: docs/RELEASE_STATUS.yaml
- Project: $ProjectTitle

## Definition of Done

- [ ] Escopo respeitado
- [ ] Backend/frontend/mobile revisados conforme area
- [ ] Testes obrigatorios executados ou justificativa registrada
- [ ] Docker/local validado quando aplicavel
- [ ] Observability atualizado em docs/codex/observability
- [ ] Commit e push conforme politica atual

## Referencias locais

- docs/RELEASE_STATUS.yaml
- docs/TASKS.md
- docs/codex/OBSERVABILITY.md
"@

        Ensure-Issue -TaskId $task.id -Title $title -Body $body -Labels $labels -MilestoneTitle $releaseInfo.title -ProjectNumber $projectNumber
    }
}

Write-Host "Sincronizacao finalizada."
