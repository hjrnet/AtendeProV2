param(
    [switch] $Strict
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$statusPath = Join-Path $repoRoot "docs/RELEASE_STATUS.yaml"
$releaseDir = Join-Path $repoRoot "docs/releases"
$roadmapPath = Join-Path $repoRoot "docs/ROADMAP_RELEASES.md"
$sessionStatePath = Join-Path $repoRoot "docs/codex/SESSION_STATE.md"

if (-not (Test-Path $statusPath)) {
    throw "Arquivo nao encontrado: docs/RELEASE_STATUS.yaml"
}

if (-not (Test-Path $releaseDir)) {
    throw "Diretorio nao encontrado: docs/releases"
}

$linhasStatus = Get-Content $statusPath
$releasesStatus = @()
$releaseStatusByName = @{}
$currentRelease = ""
foreach ($linha in $linhasStatus) {
    if ($linha -match '^\s{2}(R\d+):') {
        $currentRelease = $Matches[1]
        $releasesStatus += $currentRelease
        continue
    }

    if ($currentRelease -and $linha -match '^\s+status:\s+([A-Z_]+)') {
        $releaseStatusByName[$currentRelease] = $Matches[1]
    }
}

$releasesStatus = $releasesStatus | Sort-Object { [int]($_ -replace 'R', '') } -Unique
if ($releasesStatus.Count -eq 0) {
    throw "Nenhuma release encontrada em docs/RELEASE_STATUS.yaml"
}

$erros = New-Object System.Collections.Generic.List[string]
$avisos = New-Object System.Collections.Generic.List[string]
$secoesRecomendadas = @("## Objetivo", "## Tasks concluidas", "## Evidencias", "## Proxima release recomendada")

foreach ($release in $releasesStatus) {
    $arquivoRelease = Join-Path $releaseDir "$release.md"
    if (-not (Test-Path $arquivoRelease)) {
        $erros.Add("$release sem docs/releases/$release.md")
        continue
    }

    $conteudo = Get-Content $arquivoRelease -Raw
    $statusRelease = $releaseStatusByName[$release]
    if ($statusRelease -eq "CONCLUIDA" -and $conteudo -notmatch '(?m)^Status:\s*CONCLUIDA') {
        $avisos.Add("$release tem arquivo, mas nao declara 'Status: CONCLUIDA'")
    }
    elseif ($statusRelease -ne "CONCLUIDA" -and $conteudo -notmatch '(?m)^Status:\s*\S+') {
        $avisos.Add("$release tem arquivo, mas nao declara linha de status")
    }

    foreach ($secao in $secoesRecomendadas) {
        if ($conteudo -notlike "*$secao*") {
            $avisos.Add("$release sem secao recomendada '$secao'")
        }
    }
}

$arquivosRelease = Get-ChildItem $releaseDir -File -Filter 'R*.md' |
    Where-Object { $_.BaseName -match '^R\d+$' } |
    ForEach-Object { $_.BaseName }
foreach ($arquivo in $arquivosRelease) {
    if ($arquivo -notin $releasesStatus) {
        $avisos.Add("docs/releases/$arquivo.md existe, mas nao esta listado em RELEASE_STATUS.yaml")
    }
}

$recommendationSources = @()
if (Test-Path $roadmapPath) {
    $recommendationSources += [pscustomobject]@{
        Name = "docs/ROADMAP_RELEASES.md"
        Content = Get-Content $roadmapPath -Raw
    }
}
if (Test-Path $sessionStatePath) {
    $recommendationSources += [pscustomobject]@{
        Name = "docs/codex/SESSION_STATE.md"
        Content = Get-Content $sessionStatePath -Raw
    }
}

foreach ($source in $recommendationSources) {
    $matches = [regex]::Matches($source.Content, '(?i)Proxima\s+(?:release|etapa)\s+recomendada:\s*(R\d+)')
    foreach ($match in $matches) {
        $recommendedRelease = $match.Groups[1].Value.ToUpperInvariant()
        if ($recommendedRelease -notin $releasesStatus) {
            $erros.Add("$source.Name recomenda $recommendedRelease, mas $recommendedRelease nao esta listado em docs/RELEASE_STATUS.yaml")
        }
    }
}

if ($Strict -and $avisos.Count -gt 0) {
    foreach ($aviso in $avisos) {
        $erros.Add("STRICT: $aviso")
    }
}

Write-Host "Auditoria de documentacao de releases"
Write-Host "Releases no status: $($releasesStatus.Count)"
Write-Host "Arquivos docs/releases: $($arquivosRelease.Count)"

if ($avisos.Count -gt 0 -and -not $Strict) {
    Write-Host "Avisos:"
    foreach ($aviso in $avisos) {
        Write-Host "- $aviso"
    }
}

if ($erros.Count -gt 0) {
    Write-Host "Erros:"
    foreach ($erro in $erros) {
        Write-Host "- $erro"
    }
    exit 1
}

Write-Host "OK: todas as releases listadas possuem arquivo executivo em docs/releases."
