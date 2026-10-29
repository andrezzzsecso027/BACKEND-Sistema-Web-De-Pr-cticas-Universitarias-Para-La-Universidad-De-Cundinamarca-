# 🎓 UdecPrac — Plataforma de Gestión de Prácticas Profesionales (UdeC)

Plataforma Full-Stack diseñada para conectar a los estudiantes de la **Universidad de Cundinamarca** con empresas aliadas para el desarrollo de sus prácticas académicas y profesionales, bajo la supervisión de la Coordinación de Prácticas.

---

## 🏗️ 1. Arquitectura del Sistema

### Backend (`backend-udecprac`) — Arquitectura Hexagonal (Puertos y Adaptadores)
El backend está construido en **Java 17** con **Spring Boot** siguiendo estrictamente **Arquitectura Hexagonal**, desacoplando el núcleo de negocio de los frameworks y servicios externos:

* **`domain/model`**: Entidades puras de negocio (`Student`, `Company`, `Offer`, `Postulation`, `Practice`, `Comment`, `Notification`).
* **`domain/port/in`**: Puertos de entrada (Interfaces de Casos de Uso).
* **`domain/port/out`**: Puertos de salida (Contratos de persistencia, almacenamiento en la nube y mensajería).
* **`application/usecase`**: Implementación de las reglas de negocio y casos de uso.
* **`infrastructure/adapter/in/web`**: Adaptadores primarios REST (`AdminController`, `CompanyController`, `OfferController`, `PostulationController`, `AuthController`).
* **`infrastructure/adapter/out`**: Adaptadores secundarios hacia **PostgreSQL (Neon.tech)**, **Cloudinary API** y **API Externa de Correo**.

### Frontend (`frontend-udecprac`) — Angular Standalone & Clean Code
* Desarrollado con **Angular** y **TypeScript** empleando componentes *Standalone*, *Reactive Forms*, *Guards* por rol (`estudiante`, `empresa`, `administrador`), interceptores JWT y control de cierre de sesión automático por inactividad.

---

## 🧱 2. Aplicación de los Principios SOLID

| Principio | Implementación en UdecPrac | Evidencia en Código |
| :--- | :--- | :--- |
| **S — Single Responsibility Principle** *(Responsabilidad Única)* | Cada Caso de Uso tiene una única responsabilidad de negocio y una sola razón para cambiar, evitando clases monolíticas. | `VerifyCompanyUseCaseImpl.java` (solo aprueba/inhabilita empresas), `CloseOfferByAdminUseCaseImpl.java` (solo cierra ofertas no aptas), `ModerateCommentUseCaseImpl.java` (solo modera reseñas). |
| **O — Open/Closed Principle** *(Abierto/Cerrado)* | El sistema está abierto a la extensión de nuevos proveedores externos pero cerrado a la modificación de la lógica de negocio mediante el uso de Puertos. | `FileStoragePort.java`: permite cambiar `CloudinaryStorageAdapter` por AWS S3 sin modificar ningún Caso de Uso. |
| **L — Liskov Substitution Principle** *(Sustitución de Liskov)* | Cualquier implementación de un puerto de salida puede ser sustituida por otra (o por un `@Mock` en pruebas) respetando el contrato esperado. | Sustitución de `CompanyRepositoryAdapter` y `OfferRepositoryAdapter` en `CasosDeUsoNegocioTest.java`. |
| **I — Interface Segregation Principle** *(Segregación de Interfaces)* | Los puertos de entrada están divididos en interfaces específicas por acción en lugar de una interfaz genérica gigante. | Paquete `domain/port/in/`: `VerifyCompanyUseCase`, `UpdatePasswordUseCase`, `ModerateCommentUseCase`. |
| **D — Dependency Inversion Principle** *(Inversión de Dependencias)* | La capa de aplicación (`application/usecase`) no depende de JPA, Cloudinary ni detalles de infraestructura; depende exclusivamente de abstracciones (`domain/port/out`). | Inyección por constructor de interfaces `*RepositoryPort` en todos los `*UseCaseImpl`. |

---

## 🌐 3. Metodología de los 12 Factores (*The Twelve-Factor App*)

1. **I. Código Base (*Codebase*):** Repositorios versionados en Git (`backend-udecprac` y `frontend-udecprac`) con flujo de ramas por funcionalidad (`develop`, `feature/*`).
2. **II. Dependencias (*Dependencies*):** Declaración explícita y aislada mediante **`build.gradle`** (Gradle) en el backend y **`package.json`** (NPM) en el frontend.
3. **III. Configuración (*Config*):** Separación estricta de credenciales sensibles mediante variables de entorno en archivo **`.env`** (`DB_URL`, `CLOUDINARY_URL`, `RESEND_API_KEY`, `JWT_SECRET`) inyectadas en **`src/main/resources/application.properties`**.
4. **IV. Servicios de Respaldo (*Backing Services*):** Conexión desacoplada a servicios en la nube como recursos adjuntos: **PostgreSQL Serverless en Neon.tech**, **Cloudinary REST API** y **Servicio de Correo transaccional**.
5. **V. Construir, Desplegar, Ejecutar (*Build, Release, Run*):** Automatización mediante pipelines de **GitHub Actions (`.github/workflows/`)** y empaquetado reproducible con **`Dockerfile`**.
6. **VI. Procesos (*Processes*):** Arquitectura **100% Stateless (Sin estado)**. La sesión no se almacena en memoria del servidor sino mediante tokens firmados **JWT** validados en `JwtAuthenticationFilter.java`.
7. **VII. Asignación de Puertos (*Port Binding*):** El backend se auto-contiene en un servidor Tomcat embebido expuesto mediante configuración en el puerto **`8081`** (`server.port=8081`).
8. **VIII. Concurrencia (*Concurrency*):** Escalabilidad horizontal habilitada gracias al diseño *Stateless* y gestión de conexiones concurrentes mediante el pool **HikariCP**.
9. **IX. Desechabilidad (*Disposability*):** Arranque rápido y cierre seguro de transacciones mediante anotaciones `@Transactional` y gestión de ciclo de vida de Spring Boot.
10. **X. Paridad Desarrollo/Producción (*Dev/Prod Parity*):** Uso del mismo motor de base de datos en la nube (PostgreSQL Neon.tech) y almacenamiento (Cloudinary) entre los entornos de desarrollo y pre-producción.
11. **XI. Registros (*Logs*):** Gestión centralizada de eventos mediante **SLF4J / Logback**, emitiendo flujos continuos por consola estándar (`stdout`) y archivo rotativo en **`logs/udecprac-backend.log`**.
12. **XII. Procesos de Administración (*Admin Processes*):** Ejecución de tareas administrativas aisladas como migración de esquema JPA, generación de reportes de cobertura **JaCoCo** y auditoría con **SonarQube**.

---

## 🔌 4. Integración de APIs Externas

1. **Cloudinary API (`CloudinaryStorageAdapter.java`):**
    * Almacenamiento seguro en la nube de **Logos corporativos**, **Certificados PDF de NIT / Cámara de Comercio** y **Hojas de Vida (CV) en PDF** de los estudiantes, retornando URLs seguras (`https://res.cloudinary.com/...`).
2. **API Externa de Correo (`ExternalApiEmailAdapter.java`):**
    * Envío de códigos de verificación de registro (2FA), notificaciones de aprobación/rechazo de empresas y avisos de selección en ofertas de práctica.

---

## 📋 5. Auditoría de Logs y Pruebas Automatizadas

### Visualización de Logs
Los registros de actividad y trazabilidad se emiten en tiempo real en la consola del servidor y se almacenan en:
```bash
logs/udecprac-backend.log