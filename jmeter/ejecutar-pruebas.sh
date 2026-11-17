#!/usr/bin/env bash
# Ejecuta las 3 partes de pruebas JMeter (v2 JWT) con Docker y genera los informes HTML.
# Requisitos: backend corriendo en el puerto 8081 contra la BD de PRUEBAS (5433),
#             usuarios admin.stress@udecprac.co y login.stress@empresa.com (clave Admin123*).
# Uso:  bash ejecutar-pruebas.sh [carpeta_salida] [partes...]
#       bash ejecutar-pruebas.sh informes-v2-jwt 1 2 3
set -u
cd "$(dirname "$0")"
OUT="${1:-informes-v2-jwt}"; shift || true
PARTES="${*:-1 2 3}"
mkdir -p "$OUT"
export MSYS_NO_PATHCONV=1

declare -A PLAN=( [1]="1-carga-escenarios-1-al-4.jmx" [2]="2-estres-escenarios-5-6-7-9.jmx" [3]="3-resistencia-escenario-8.jmx" )
declare -A REP=( [1]="reporte-parte1-carga" [2]="reporte-parte2-estres" [3]="reporte-parte3-resistencia" )

for p in $PARTES; do
  rm -rf "$OUT/${REP[$p]}" "$OUT/resultados-parte$p.jtl"
  echo ">>> Parte $p (${PLAN[$p]}) - $(date +%H:%M:%S)"
  docker run --rm -e JVM_XMS=1024 -e JVM_XMX=2048 -e JVM_XMN=512 \
    -v "$(pwd -W 2>/dev/null || pwd):/tests" -w /tests justb4/jmeter:5.5 \
    -n -t "${PLAN[$p]}" -Jhost=host.docker.internal -Jport=8081 \
    -l "$OUT/resultados-parte$p.jtl" -j "$OUT/jmeter-parte$p.log" \
    -e -o "$OUT/${REP[$p]}"
  echo "<<< Parte $p terminada - $(date +%H:%M:%S)"
  [ "$p" != "${PARTES##* }" ] && sleep 30   # pausa para que el backend se estabilice entre partes
done
