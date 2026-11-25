#!/usr/bin/env bash
# EN: runClient with GTNA (local main), AE2, ModernSpace, ModernManipulator, FTB Chunks and AE2 Wireless Terminals.
# PT: runClient com GTNA (main local), AE2, ModernSpace, ModernManipulator, FTB Chunks e AE2 Wireless Terminals.
# ZH: 带 GTNA（本地 main）、AE2、ModernSpace、ModernManipulator、FTB Chunks 与 AE2 无线终端的 runClient。
set -euo pipefail
P="$HOME/MineProjects"
cd "$(dirname "$0")/.."
exec ./gradlew runClient -PgtiaDevOptionalDependencies=true -PgtiaDevPlaytest=true \
  -PgtnaDevJar="$P/GregTech-Nexus-Addon/build/devlibs/gtna-0.6.2.jar" \
  -PmodernSpaceJar="$P/PersonalSpace/build/devlibs/modernspace-0.3.2.jar" \
  -PmodernManipulatorJar="$P/ModernManipulator/build/devlibs/modern-manipulator-0.2.0.jar" "$@"
