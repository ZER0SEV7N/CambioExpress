# CambioExpress - Taller CI/CD, DevOps y Pipelines

## Comandos
```
mvn clean verify                 # 10 pruebas unitarias + 8 de integracion + check JaCoCo (80 % lineas)
mvn -q test -Dtest=Validador     # puntaje del taller (meta: 20 / 20)
mvn spring-boot:run              # API en http://localhost:8080
```

## Archivos del pipeline
| Requerimiento | Archivo |
|---|---|
| RN-1 CI en GitHub Actions | `.github/workflows/ci.yml` |
| RN-2 Cobertura y pruebas | `pom.xml` (JaCoCo), `ci.yml` (reportes), `TipoCambioTest.java` |
| RN-3 Jenkinsfile estilo OCD | `Jenkinsfile` |
| RN-4 CD estilo Deploy Go | `.github/workflows/cicd.yml`, `pom.xml` (version 1.0.0) |

## Endpoints
- `GET  /api/tipos-cambio`
- `GET  /api/tipos-cambio/{moneda}` (404 si no existe)
- `POST /api/cotizaciones`  `{ "moneda": "USD", "operacion": "COMPRA", "monto": 100 }` -> 201
- `GET  /api/cotizaciones/{id}` (404 si no existe)
