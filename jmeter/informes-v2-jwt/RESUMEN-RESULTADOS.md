# Pruebas de carga y estrés — UdecPrac (v2, seguridad JWT por rol)

Ejecución: 2026-11-17 · JMeter 5.5 (Docker `justb4/jmeter:5.5`) · Backend Spring Boot en `localhost:8081`
contra la **BD de pruebas** (`postgres-udecprac-test`, puerto 5433) · Equipo: 12 núcleos, 15 GB RAM.

## Qué cambió respecto a la versión del 15/11

| Cambio | Motivo |
|---|---|
| setUp que inicia sesión como **administrador** y **empresa** y envía `Authorization: Bearer <token>` | El backend ahora exige JWT por rol; sin token todo devolvía 401/403 |
| El flujo completo (E4) usa el token de **su propio login** | La guía pide Login → Consulta → Registro → Actualización → Consulta final |
| Registro de empresas con `urlPdfNit` | El certificado NIT en PDF ahora es obligatorio |
| Timeouts: conexión 5 s / respuesta 10 s | Antes una espera de 69 s contaba como éxito |
| Aserciones (código HTTP, token, JSON, persistencia) en todas las peticiones | La guía pide validar respuestas, no solo medir tiempos |
| Keep-alive en todas las peticiones | Evita la tormenta de conexiones TCP que generaba esperas de 35–69 s |
| Concurrencia real: tiempos de pensar (E1, E2), disparo sincronizado (E3), fases sostenidas de 60 s (E5), duración fija (E6, E9), ramp-up corto (E7) | En la v1 se configuraban 300 usuarios, pero nunca hubo más de 2–7 a la vez |
| Nuevo **E3.2 anti-duplicados** (10 registros simultáneos con el mismo NIT) | La guía pide "ausencia de duplicados" e "integridad de datos" |
| Nueva fase de **recuperación** después del pico en E9 | La guía pide evaluar la "capacidad de recuperación" |
| Monitoreo de CPU, memoria, heap e hilos del backend, y CPU y RAM de la BD (`recursos-parteN.csv`) | La guía pide observar el consumo de memoria, la CPU alta y la saturación de la BD |

## Resultados

"Conc." = usuarios realmente simultáneos medidos (`grpThreads`), no solo los configurados.

### Parte 1 — Carga (`reporte-parte1-carga/index.html`)

| Escenario | Conc. | Peticiones | Errores | Mediana | p95 | Validación |
|---|---|---|---|---|---|---|
| E1 Login concurrente (30 u, ramp-up 10 s, 5 loops) | 30 | 150 | 0 % | 144 ms | 211 ms | Token JWT en el 100 % |
| E2 Consulta masiva (50 u, ramp-up 15 s, 10 loops, 4 GET) | 50 | 2.000 | 0 % | 15–40 ms | 26–101 ms | Todas devuelven un listado JSON |
| E3.1 Registro concurrente (30 u simultáneos) | 30 | 30 | 0 % | 850 ms | 1.015 ms | 30 empresas en BD, 30 NIT y 30 correos distintos, sin huérfanos |
| E3.2 Mismo NIT ×10 simultáneos | 10 | 10 | 0 % | 252 ms | — | **1 × 201 y 9 × 409**; 1 sola empresa y 1 solo usuario en BD |
| E4 Flujo completo (15 u, 5 loops) | 15 | 375 | 0 % | 21–165 ms | 31–339 ms | La actualización se aplica y la oferta creada aparece en la consulta final |

### Parte 2 — Estrés (`reporte-parte2-estres/index.html`)

| Escenario | Conc. | Peticiones | Errores | Mediana | p95 | Rendimiento | CPU backend / BD |
|---|---|---|---|---|---|---|---|
| E5 F1 Login 50 u | 50 | 3.688 | 0 % | 759 ms | 893 ms | 60,6/s | 76 % / 6 % |
| E5 F2 Login 100 u | 100 | 3.756 | 0 % | 1.582 ms | 1.827 ms | 60,9/s | 78 % / 6 % |
| E5 F3 Login 200 u | 200 | 2.875 | 0 % | 3.113 ms | 4.377 ms | 45,3/s | 78 % / 8 % |
| E5 F4 Login 300 u | 300 | 4.100 | 0 % | 4.600 ms | 4.982 ms | 63,1/s | 77 % / 6 % |
| E6 Sobrecarga de consultas (200 u, 90 s) | 200 | 7.935 | 0 % | 1,7–1,8 s | 4,8 s | 86,3/s | 30 % / **72 %** |
| E7 CRUD (100 u × 20 loops: crear, actualizar, cerrar) | 100 | 6.000 | 0 % | 210–223 ms | 351–372 ms | 406,7/s | 41 % / **94 %** |
| E9 Pico extremo (1.000 u en 10 s, 60 s) | 1.000 | 6.688 | **77 %** | 10 s (timeout) | — | 95,1/s | 69 % / 35 % |
| E9 Recuperación (20 u, después del pico) | 20 | 1.036 | 0 % | 27–141 ms | 118–203 ms | 22,2/s | — |

### Parte 3 — Resistencia (`reporte-parte3-resistencia/index.html`)

| Escenario | Conc. | Peticiones | Errores | Mediana | p95 | Memoria |
|---|---|---|---|---|---|---|
| E8 50 u constantes × 10 min (3 GET, 1 s de ritmo) | 50 | 19.745 | 0 % | 161–795 ms | 723–1.784 ms | Heap en diente de sierra entre 120 y 360 MB (GC normal); memoria privada estable en 768 MB; hilos estables (~72) |

Tiempo promedio por minuto en E8: 386 · 452 · 877\* · 436 · 547 · 530 · 495 · 432 · 430 · 420 ms.
No hay degradación progresiva: los últimos minutos son los más rápidos. \*El pico del minuto 2 coincidió con una recolección de memoria y se recuperó solo.

## Conclusiones

1. **El límite del login está en unos 60 inicios de sesión por segundo.** Es un límite de CPU, porque BCrypt es deliberadamente costoso. De 50 a 300 usuarios el rendimiento se mantiene plano (~60/s) y el tiempo de respuesta crece de forma lineal (0,76 s → 4,6 s), que es la señal clásica de saturación. Hasta 300 usuarios no hay errores.
2. **Punto de falla (E9):** con 1.000 usuarios simultáneos el 77 % de las peticiones supera el timeout de 10 s (`SocketTimeoutException`). No hubo errores 500 ni caída del proceso. La falla es por **cola**: el pool de 200 hilos de Tomcat está lleno. El sistema degrada de forma controlada, y en cuanto baja la carga **se recupera de inmediato**: login a 141 ms y consultas a 27 ms, con 0 % de errores.
3. **La BD es el cuello de botella en las escrituras y en las consultas masivas.** Llega a 94 % de CPU en E7 y a 72 % en E6, mientras el backend se queda en 30–41 %. Aun así, no hubo pérdida de datos: las 2.000 ofertas de cada corrida de E7 se crearon con IDs distintos y todas quedaron cerradas.
4. **Los listados no tienen paginación.** `/api/admin/ofertas` ya devuelve 1,3 MB con 3.500 ofertas (el 15/11 eran 450 KB). Por eso E6 y E8 son más lentos que en la v1: el tiempo crece con el volumen de datos. Mejora recomendada: paginar (`Pageable`) y subir el pool de HikariCP (por defecto son 10 conexiones).
5. **Integridad ante concurrencia (corregido):** en la primera corrida, E3.2 dio 3 × 201 y 7 × 500. Dos registros "exitosos" habían **sobrescrito** la empresa del primero y dejado 2 usuarios huérfanos (ver `antes-correccion-nit/`). La causa era el `save()` de Spring Data con una llave asignada (el NIT), que hace *merge*. Se corrigió con un INSERT explícito (`persist` + `flush`) en `JPACompanyRepositoryAdapter.createCompany` y con el mapeo de `DataIntegrityViolationException` a 409. Resultado: 1 × 201, 9 × 409 y la BD queda íntegra.

## Archivos

- `reporte-parteN-*/index.html`: dashboards HTML de JMeter.
- `resultados-parteN.jtl`: muestras en crudo.
- `recursos-parteN.csv`: CPU, memoria, heap e hilos del backend, y CPU y RAM de la BD cada 5 s.
- `consola-parteN.txt` y `jmeter-parteN.log`: salida de la ejecución.
- `antes-correccion-nit/`: corrida de la Parte 1 con el error de NIT duplicado (evidencia del "antes") y la primera corrida de la Parte 2.

Para volver a ejecutar (con el backend en 8081 contra la BD de pruebas), desde la carpeta `jmeter/`:

```
powershell -ExecutionPolicy Bypass -File monitorear-recursos.ps1 -Salida informes-v2-jwt/recursos-parte1.csv   # en otra terminal
bash ejecutar-pruebas.sh informes-v2-jwt 1 2 3
```
