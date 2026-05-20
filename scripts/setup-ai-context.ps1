<#
  setup-ai-context.ps1
  Creates LOCAL POINTERS to AGENTS.md (single source of context) on Windows.
  The pointers are in .gitignore — they are not versioned.

  Usage:
    ./scripts/setup-ai-context.ps1            # interactive
    ./scripts/setup-ai-context.ps1 claude     # Claude Code only
    ./scripts/setup-ai-context.ps1 all        # all

  Note: symlinks on Windows may require "Developer Mode" enabled or a shell
  with privileges. If it fails, a copy is created with a divergence warning.
#>
param([string]$Choice = "")

$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")

if (-not (Test-Path "AGENTS.md")) {
    Write-Error "AGENTS.md not found at the project root."
    exit 1
}

function New-Pointer($Target) {
    $dir = Split-Path $Target -Parent
    if ($dir -and -not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir | Out-Null }
    if ((Test-Path $Target) -and -not ((Get-Item $Target).LinkType)) {
        Write-Warning "'$Target' already exists and is not a symlink. Skipped."
        return
    }
    try {
        New-Item -ItemType SymbolicLink -Path $Target -Target (Resolve-Path "AGENTS.md") -Force | Out-Null
        Write-Host "OK: $Target -> AGENTS.md"
    } catch {
        Copy-Item "AGENTS.md" $Target -Force
        Write-Warning "Symlink failed; a COPY of $Target was created. Warning: it may diverge from AGENTS.md."
    }
}

if (-not $Choice) {
    Write-Host "Which tool do you use?"
    Write-Host "  1) Claude Code        (CLAUDE.md)"
    Write-Host "  2) GitHub Copilot     (.github/copilot-instructions.md)"
    Write-Host "  3) Cursor             (.cursorrules)"
    Write-Host "  4) All"
    $Choice = Read-Host "Option [1-4]"
}

switch ($Choice) {
    { $_ -in "1","claude" }  { New-Pointer "CLAUDE.md" }
    { $_ -in "2","copilot" } { New-Pointer ".github/copilot-instructions.md" }
    { $_ -in "3","cursor" }  { New-Pointer ".cursorrules" }
    { $_ -in "4","all" } {
        New-Pointer "CLAUDE.md"
        New-Pointer ".github/copilot-instructions.md"
        New-Pointer ".cursorrules"
    }
    default { Write-Error "Invalid option."; exit 1 }
}

Write-Host "Done. The source of truth is still AGENTS.md."
