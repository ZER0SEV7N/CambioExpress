#!/usr/bin/env bash
# Despliegue SIMULADO de CambioExpress.
# Uso: bash devops/desplegar.sh <ambiente> <ruta-del-jar>
# El token llega en la variable de entorno DEPLOY_TOKEN (Jenkins o GitHub Secrets).
set -euo pipefail

AMBIENTE="${1:?Indica el ambiente: dev o cert}"
JAR="${2:?Indica la ruta del jar}"

if [[ "$AMBIENTE" != "dev" && "$AMBIENTE" != "cert" ]]; then
  echo "Ambiente no valido: $AMBIENTE (usa dev o cert)" >&2
  exit 1
fi

if [[ ! -f "$JAR" ]]; then
  echo "No se encontro el jar: $JAR" >&2
  exit 1
fi

if [[ -z "${DEPLOY_TOKEN:-}" ]]; then
  echo "Falta la variable DEPLOY_TOKEN" >&2
  exit 1
fi

echo "Desplegando $(basename "$JAR") en $AMBIENTE ..."
echo "Token recibido: ****${DEPLOY_TOKEN: -4}"
echo "Despliegue simulado en $AMBIENTE completado."
