param(
    [switch] $Strict
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$statusPath = Join-Path $repoRoot "docs/RELEASE_STATUS.yaml"
$releaseDir = Join-Path $repoRoot "docs/releases"

if (-not (Test-Path $statusPath)) {
    throw "Arquivo nao encontrado: docs/RELEASE_STATUS.yaml"
}

if (-not (Test-Path $releaseDir)) {
    throw "Diretorio nao encontrado: docs/releases"
}

$linhasStatus = Get-Content $statusPath
$releasesStatus = @()
foreach ($linha in $linhasStatus) {
    if ($linha -match '^\s{2}(R\d+):') {
        $releasesStatus += $Matches[1]
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
    if ($conteudo -notmatch '(?m)^Status:\s*CONCLUIDA') {
        $avisos.Add("$release tem arquivo, mas nao declara 'Status: CONCLUIDA'")
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
