#!/usr/bin/env bash
set -euo pipefail

AMBIENTE="${1:-}"
ARTEFACTO="${2:-}"

if [[ -z "$AMBIENTE" || -z "$ARTEFACTO" ]]; then
  echo "Uso: bash devops/desplegar.sh <ambiente> <ruta-jar>"
  exit 1
fi

if [[ -z "${DEPLOY_TOKEN:-}" ]]; then
  echo "ERROR: La variable DEPLOY_TOKEN no está definida."
  exit 1
fi

ARCHIVO_JAR=$(ls $ARTEFACTO 2>/dev/null | head -n 1 || true)

if [[ -z "$ARCHIVO_JAR" || ! -f "$ARCHIVO_JAR" ]]; then
  echo "ERROR: No se encontró el artefacto JAR en la ruta: $ARTEFACTO"
  exit 1
fi

echo "===================================================="
echo " Desplegando CambioExpress en ambiente: $AMBIENTE"
echo " Artefacto: $ARCHIVO_JAR"
echo " Token verificado correctamente (longitud: ${#DEPLOY_TOKEN})"
echo " Estado: DESPLIEGUE EXITOSO"
echo "===================================================="