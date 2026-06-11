param(
    [string] $Repo = "hjrnet/AtendeProV2",
    [Parameter(Mandatory = $true)]
    [string] $Release,
    [string] $MilestoneTitle = "",
    [switch] $EnsureIssues,
    [switch] $Apply
)

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$ReleaseStatusPath = Join-Path $RepoRoot "docs\RELEASE_STATUS.yaml"

function Assert-GhReady {
    if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
        throw "GitHub CLI nao encontrado. Instale gh ou rode este script em ambiente com gh disponivel."
    }

    & gh auth status *> $null
    if ($LASTEXITCODE -ne 0) {
        throw "GitHub CLI nao autenticado. Rode: gh auth login --web --scopes `"repo,project`""
    }
}

function Get-ReleaseInfo {
    if (-not (Test-Path $ReleaseStatusPath)) {
        throw "Arquivo nao encontrado: $ReleaseStatusPath"
    }

    $insideRelease = $false
    $foundRelease = $false
    $insideTasks = $false
    $title = $Release
    $status = ""
    $tasks = @()

    foreach ($line in Get-Content -Path $ReleaseStatusPath) {
        if ($line -match "^\s{2}R[0-9]+:") {
            if ($insideRelease) {
                break
            }

            $insideRelease = ($line -match ("^\s{2}" + [regex]::Escape($Release) + ":"))
            if ($insideRelease) {
                $foundRelease = $true
            }
            $insideTasks = $false
            continue
        }

        if (-not $insideRelease) {
            continue
        }

        if ($line -match '^\s+name:\s+"(.+)"') {
            $title = $Matches[1]
            continue
        }

        if ($line -match "^\s+name:\s+(.+)") {
            $title = $Matches[1].Trim()
            continue
        }

        if ($line -match "^\s+status:\s+([A-Z_]+)") {
            $status = $Matches[1]
            continue
        }

        if ($line -match "^\s+tasks:") {
            $insideTasks = $true
            continue
        }

        if ($insideTasks -and $line -match "^\s+(TASK-[A-Z0-9-]+):\s+([A-Z_]+)") {
            $tasks += [pscustomobject]@{
                id = $Matches[1]
                status = $Matches[2]
            }
        }
    }

    if (-not $foundRelease) {
        throw "Release $Release nao encontrada em docs/RELEASE_STATUS.yaml."
    }

    [pscustomobject]@{
        release = $Release
        title = $title
        status = $status
        tasks = $tasks
    }
}

function Invoke-GhJson {
    param([string[]] $Arguments)

    $json = & gh @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "gh falhou: gh $($Arguments -join ' ')"
    }

    if (-not $json) {
        return $null
    }

    return $json | ConvertFrom-Json
}

function Get-Milestone {
    param([string] $Title)

    $milestones = Invoke-GhJson @("api", "repos/$Repo/milestones?state=all&per_page=100")
    $match = @($milestones | Where-Object { $_.title -eq $Title } | Select-Object -First 1)
    if ($match.Count -gt 0) {
        return $match[0]
    }

    $prefix = "$Release "
    $match = @($milestones | Where-Object { $_.title -like "$prefix*" } | Select-Object -First 1)
    if ($match.Count -gt 0) {
        return $match[0]
    }

    return $null
}

function Ensure-Milestone {
    param([string] $Title)

    $milestone = Get-Milestone -Title $Title
    if ($milestone) {
        return $milestone
    }

    if (-not $Apply) {
        Write-Host "DRY-RUN milestone create: $Title"
        return [pscustomobject]@{
            number = 0
            title = $Title
            state = "missing"
            open_issues = 0
            closed_issues = 0
        }
    }

    $number = & gh api -X POST "repos/$Repo/milestones" -f "title=$Title" -f "description=Milestone sincronizada do AtendePro para $Release." --jq ".number"
    if ($LASTEXITCODE -ne 0) {
        throw "Nao foi possivel criar milestone $Title."
    }

    return Get-Milestone -Title $Title
}

function Get-IssueByTask {
    param([string] $TaskId)

    $issues = Invoke-GhJson @("issue", "list", "--repo", $Repo, "--state", "all", "--json", "number,title,state,url,milestone", "--limit", "200")
    $match = @($issues | Where-Object { $_.title -like "$TaskId*" } | Sort-Object number | Select-Object -First 1)
    if ($match.Count -gt 0) {
        return $match[0]
    }

    return $null
}

function Ensure-IssueForTask {
    param(
        [pscustomobject] $Task,
        [string] $ReleaseTitle
    )

    $issue = Get-IssueByTask -TaskId $Task.id
    if ($issue) {
        return $issue
    }

    $title = "$($Task.id) - $ReleaseTitle"
    $body = @"
## Origem

- Release: $Release
- Status local: $($Task.status)
- Fonte: docs/RELEASE_STATUS.yaml

## Sincronizacao

Criada automaticamente por scripts/github-release-finalize.ps1.
"@

    if (-not $Apply) {
        Write-Host "DRY-RUN issue create: $title"
        return [pscustomobject]@{
            number = 0
            title = $title
            state = "OPEN"
            url = ""
        }
    }

    $url = & gh issue create --repo $Repo --title $title --body $body --milestone $ReleaseTitle
    if ($LASTEXITCODE -ne 0) {
        throw "Nao foi possivel criar issue $title."
    }

    $numeroCriado = [regex]::Match($url, "/issues/(\d+)$")
    if ($numeroCriado.Success) {
        return [pscustomobject]@{
            number = [int]$numeroCriado.Groups[1].Value
            title = $title
            state = "OPEN"
            url = $url
        }
    }

    return Get-IssueByTask -TaskId $Task.id
}

function Close-IssueIfCompleted {
    param(
        [pscustomobject] $Task,
        [pscustomobject] $Issue
    )

    if ($Task.status -ne "CONCLUIDA") {
        Write-Host "SKIP task nao concluida: $($Task.id) status=$($Task.status)"
        return
    }

    if ($Issue.state -eq "CLOSED") {
        Write-Host "OK issue ja fechada: #$($Issue.number) $($Issue.title)"
        return
    }

    $comment = "Sincronizacao AtendePro: $($Task.id) concluida em docs/RELEASE_STATUS.yaml; fechamento automatico por R27."
    if (-not $Apply) {
        Write-Host "DRY-RUN issue close: #$($Issue.number) $($Issue.title)"
        return
    }

    & gh issue close $Issue.number --repo $Repo --reason completed --comment $comment | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Nao foi possivel fechar issue #$($Issue.number)."
    }
}

function Close-MilestoneIfReady {
    param([string] $Title)

    $milestone = Get-Milestone -Title $Title
    if (-not $milestone) {
        if (-not $Apply) {
            Write-Host "DRY-RUN milestone close: $Title (milestone seria criado antes do fechamento)"
            return
        }

        throw "Milestone nao encontrada para fechamento: $Title."
    }

    if ($milestone.state -eq "closed") {
        Write-Host "OK milestone ja fechada: $Title"
        return
    }

    if (-not $Apply) {
        Write-Host "DRY-RUN milestone close: $Title"
        return
    }

    $openIssues = @()
    for ($tentativa = 1; $tentativa -le 5; $tentativa++) {
        $resultadoIssuesAbertas = Invoke-GhJson @("issue", "list", "--repo", $Repo, "--state", "open", "--milestone", $Title, "--json", "number,title", "--limit", "100")
        $openIssues = @($resultadoIssuesAbertas | Where-Object { $_ -and $_.number })
        if ($openIssues.Count -eq 0) {
            break
        }

        if ($tentativa -lt 5) {
            Start-Sleep -Seconds 2
        }
    }

    if (@($openIssues).Count -gt 0) {
        $openTitles = (@($openIssues) | ForEach-Object { "#$($_.number) $($_.title)" }) -join "; "
        throw "Milestone $Title ainda possui issues abertas: $openTitles"
    }

    & gh api -X PATCH "repos/$Repo/milestones/$($milestone.number)" -f state=closed | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Nao foi possivel fechar milestone $Title."
    }

    Write-Host "OK milestone fechada: $Title"
}

Assert-GhReady
$releaseInfo = Get-ReleaseInfo
$title = if ($MilestoneTitle) { $MilestoneTitle } else { $releaseInfo.title }
$milestone = Ensure-Milestone -Title $title

Write-Host "Repositorio: $Repo"
Write-Host "Release: $($releaseInfo.release)"
Write-Host "Status local: $($releaseInfo.status)"
Write-Host "Milestone: $title (#$($milestone.number))"
Write-Host "Modo: $(if ($Apply) { 'APPLY' } else { 'DRY-RUN' })"

foreach ($task in $releaseInfo.tasks) {
    $issue = Get-IssueByTask -TaskId $task.id
    if (-not $issue -and $EnsureIssues) {
        $issue = Ensure-IssueForTask -Task $task -ReleaseTitle $title
    }

    if (-not $issue) {
        Write-Host "WARN issue nao encontrada para $($task.id)"
        continue
    }

    Close-IssueIfCompleted -Task $task -Issue $issue
}

if ($releaseInfo.status -eq "CONCLUIDA") {
    Close-MilestoneIfReady -Title $title
}
else {
    Write-Host "SKIP milestone nao fechado porque status local e $($releaseInfo.status)."
}

Write-Host "Finalizacao GitHub concluida para $Release."
