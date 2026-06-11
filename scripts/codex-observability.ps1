param(
    [Parameter(Position = 0)]
    [ValidateSet("record", "fail", "report", "checklist")]
    [string] $Mode = "report",

    [string] $Release = "",
    [string] $Task = "",
    [string] $Status = "EM_ANDAMENTO",
    [string] $Area = "governanca",
    [string] $Summary = "",
    [string] $Command = "",
    [string] $Result = "",

    [string] $Severity = "MEDIUM",
    [string] $FailureType = "unknown",
    [string] $Cause = "",
    [string] $Impact = "",
    [string] $Action = "",
    [switch] $Resolved
)

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$ObservabilityDir = Join-Path $RepoRoot "docs\codex\observability"
$ReportsDir = Join-Path $ObservabilityDir "reports"
$EventsPath = Join-Path $ObservabilityDir "events.jsonl"
$FailuresPath = Join-Path $ObservabilityDir "failures.jsonl"
$ReleaseStatusPath = Join-Path $RepoRoot "docs\RELEASE_STATUS.yaml"

function Ensure-ObservabilityPaths {
    New-Item -ItemType Directory -Force -Path $ObservabilityDir | Out-Null
    New-Item -ItemType Directory -Force -Path $ReportsDir | Out-Null
    if (-not (Test-Path $EventsPath)) {
        New-Item -ItemType File -Force -Path $EventsPath | Out-Null
    }
    if (-not (Test-Path $FailuresPath)) {
        New-Item -ItemType File -Force -Path $FailuresPath | Out-Null
    }
}

function Write-JsonLine {
    param(
        [string] $Path,
        [hashtable] $Data
    )

    $json = $Data | ConvertTo-Json -Compress -Depth 6
    for ($attempt = 1; $attempt -le 5; $attempt++) {
        try {
            Add-Content -Path $Path -Value $json -Encoding UTF8
            return
        }
        catch [System.IO.IOException] {
            if ($attempt -eq 5) {
                throw
            }
            Start-Sleep -Milliseconds (150 * $attempt)
        }
    }
}

function Read-JsonLines {
    param([string] $Path)

    if (-not (Test-Path $Path)) {
        return @()
    }

    $items = @()
    Get-Content -Path $Path | Where-Object { $_.Trim().Length -gt 0 } | ForEach-Object {
        try {
            $items += ($_ | ConvertFrom-Json)
        }
        catch {
            $items += [pscustomobject]@{
                timestamp = ""
                release = "UNKNOWN"
                task = ""
                status = "INVALID_JSON"
                area = "observability"
                summary = $_
                commands = @()
                result = "Linha invalida em JSONL"
            }
        }
    }
    return $items
}

function Get-ReleaseTasks {
    param([string] $ReleaseName)

    if (-not (Test-Path $ReleaseStatusPath)) {
        return @()
    }

    $lines = Get-Content -Path $ReleaseStatusPath
    $insideRelease = $false
    $insideTasks = $false
    $tasks = @()

    foreach ($line in $lines) {
        if ($line -match "^  [A-Z0-9]+:") {
            $insideRelease = ($line -match ("^  " + [regex]::Escape($ReleaseName) + ":"))
            $insideTasks = $false
            continue
        }

        if (-not $insideRelease) {
            continue
        }

        if ($line -match "^\s+tasks:") {
            $insideTasks = $true
            continue
        }

        if ($insideTasks -and $line -match "^\s+(TASK-[A-Z0-9-]+):\s+([A-Z_]+)") {
            $tasks += [pscustomobject]@{
                task = $Matches[1]
                status = $Matches[2]
            }
        }

        if ($insideTasks -and $line -match "^\s{4}[a-zA-Z_]+:" -and $line -notmatch "^\s+tasks:") {
            $insideTasks = $false
        }
    }

    return $tasks
}

function New-ReleaseReport {
    param([string] $ReleaseName)

    $events = Read-JsonLines -Path $EventsPath
    $failures = Read-JsonLines -Path $FailuresPath

    if ($ReleaseName) {
        $events = @($events | Where-Object { $_.release -eq $ReleaseName })
        $failures = @($failures | Where-Object { $_.release -eq $ReleaseName })
    }

    $completed = @($events | Where-Object { $_.status -eq "CONCLUIDA" }).Count
    $blocked = @($events | Where-Object { $_.status -eq "BLOQUEADA" -or $_.status -eq "FALHOU" }).Count
    $openFailures = @($failures | Where-Object { $_.resolved -ne $true }).Count
    $statusGroups = $events | Group-Object -Property status | Sort-Object Name

    $reportName = if ($ReleaseName) { "observability-$ReleaseName.md" } else { "observability-global.md" }
    $reportPath = Join-Path $ReportsDir $reportName

    $content = @()
    $content += "# Observability Report"
    $content += ""
    $content += "- Release: " + $(if ($ReleaseName) { $ReleaseName } else { "GLOBAL" })
    $content += "- GeneratedAt: " + (Get-Date).ToString("o")
    $content += "- Events: " + $events.Count
    $content += "- CompletedEvents: " + $completed
    $content += "- BlockedOrFailedEvents: " + $blocked
    $content += "- Failures: " + $failures.Count
    $content += "- OpenFailures: " + $openFailures
    $content += ""
    $content += "## Status Breakdown"
    $content += ""

    if ($statusGroups.Count -eq 0) {
        $content += "- No events registered."
    }
    else {
        foreach ($group in $statusGroups) {
            $content += "- " + $group.Name + ": " + $group.Count
        }
    }

    $content += ""
    $content += "## Recent Events"
    $content += ""

    $recentEvents = @($events | Select-Object -Last 10)
    if ($recentEvents.Count -eq 0) {
        $content += "- No recent events."
    }
    else {
        foreach ($event in $recentEvents) {
            $content += "- " + $event.timestamp + " | " + $event.task + " | " + $event.status + " | " + $event.summary
        }
    }

    $content += ""
    $content += "## Failures"
    $content += ""

    if ($failures.Count -eq 0) {
        $content += "- No failures registered."
    }
    else {
        foreach ($failure in @($failures | Select-Object -Last 10)) {
            $content += "- " + $failure.timestamp + " | " + $failure.task + " | " + $failure.severity + " | " + $failure.cause + " | resolved=" + $failure.resolved
        }
    }

    Set-Content -Path $reportPath -Value $content -Encoding UTF8
    Write-Host $reportPath
}

function New-ReleaseChecklist {
    param([string] $ReleaseName)

    if (-not $ReleaseName) {
        throw "Informe -Release para gerar checklist automatico."
    }

    $tasks = Get-ReleaseTasks -ReleaseName $ReleaseName
    $checklistPath = Join-Path $ReportsDir ("checklist-$ReleaseName.md")
    $content = @()
    $content += "# Release Checklist — " + $ReleaseName
    $content += ""
    $content += "- GeneratedAt: " + (Get-Date).ToString("o")
    $content += "- Source: docs/RELEASE_STATUS.yaml"
    $content += ""
    $content += "## Tasks"
    $content += ""

    if ($tasks.Count -eq 0) {
        $content += "- [ ] Nenhuma task encontrada para a release."
    }
    else {
        foreach ($item in $tasks) {
            $mark = if ($item.status -eq "CONCLUIDA") { "x" } else { " " }
            $content += "- [" + $mark + "] " + $item.task + " — " + $item.status
        }
    }

    $content += ""
    $content += "## Definition Of Done"
    $content += ""
    $content += "- [ ] Escopo respeitado."
    $content += "- [ ] Arquitetura revisada."
    $content += "- [ ] Testes obrigatorios registrados."
    $content += "- [ ] Docker/local validado ou justificativa registrada."
    $content += "- [ ] Falhas registradas em failures.jsonl quando aplicavel."
    $content += "- [ ] Eventos principais registrados em events.jsonl."
    $content += "- [ ] Commit/push registrados conforme politica atual."

    Set-Content -Path $checklistPath -Value $content -Encoding UTF8
    Write-Host $checklistPath
}

Ensure-ObservabilityPaths

switch ($Mode) {
    "record" {
        $commands = @()
        if ($Command) {
            $commands += $Command
        }

        Write-JsonLine -Path $EventsPath -Data @{
            timestamp = (Get-Date).ToString("o")
            release = $Release
            task = $Task
            status = $Status
            area = $Area
            summary = $Summary
            commands = $commands
            result = $Result
        }
        Write-Host "Evento registrado em $EventsPath"
    }
    "fail" {
        Write-JsonLine -Path $FailuresPath -Data @{
            timestamp = (Get-Date).ToString("o")
            release = $Release
            task = $Task
            severity = $Severity
            failureType = $FailureType
            cause = $Cause
            impact = $Impact
            action = $Action
            resolved = [bool] $Resolved
        }
        Write-Host "Falha registrada em $FailuresPath"
    }
    "report" {
        New-ReleaseReport -ReleaseName $Release
    }
    "checklist" {
        New-ReleaseChecklist -ReleaseName $Release
    }
}
