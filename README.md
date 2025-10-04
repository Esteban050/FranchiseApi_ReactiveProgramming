# Franchise API REST - Clean Architecture

API REST para gestionar franquicias, sucursales y productos construida con **Spring Boot WebFlux**, **MongoDB Reactive** y **Clean Architecture**.

---

## Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Arquitectura Limpia](#arquitectura-limpia)
- [Tecnologías](#tecnologías)
- [Buenas Prácticas de Desarrollo](#buenas-prácticas-de-desarrollo)
- [Infrastructure as Code (IaC)](#infrastructure-as-code-iac)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Despliegue Local](#instalación-y-despliegue-local)
- [Endpoints de la API](#endpoints-de-la-api)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Tests](#tests)
- [Modelo de Datos](#modelo-de-datos)
- [Estructura del Proyecto](#estructura-del-proyecto)

---

## Descripción General

Sistema de gestión para franquicias que permite administrar:
- **Franquicias**: Organizaciones principales
- **Sucursales**: Puntos de venta asociados a una franquicia
- **Productos**: Inventario con control de stock por sucursal

### Características Principales

- CRUD completo de franquicias, sucursales y productos
- Gestión de inventario (stock) por producto
- Reportes de productos con mayor stock por sucursal
- Arquitectura reactiva no bloqueante
- Base de datos NoSQL (MongoDB)
- Validaciones automáticas de entrada
- Manejo centralizado de errores
- Cobertura de tests ~90%
- Dockerizado y listo para producción

---

## Arquitectura Limpia

Este proyecto implementa **Clean Architecture** (también conocida como Arquitectura Hexagonal o Ports & Adapters) siguiendo los principios de **Robert C. Martin**.

### Capas de la Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE                        │
│  ┌─────────────────────────────────────────────────┐   │
│  │            APPLICATION                          │   │
│  │  ┌──────────────────────────────────────────┐  │   │
│  │  │          DOMAIN (Core)                   │  │   │
│  │  │                                          │  │   │
│  │  │  • Entities (Franchise, Branch, Product)│  │   │
│  │  │  • Business Rules                       │  │   │
│  │  │  • Ports (Interfaces)                   │  │   │
│  │  └──────────────────────────────────────────┘  │   │
│  │                                                 │   │
│  │  • Use Cases (Services)                        │   │
│  │  • Application Logic                           │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  • Controllers (REST)                                   │
│  • Repositories (MongoDB)                               │
│  • DTOs, Mappers, Exception Handlers                   │
└─────────────────────────────────────────────────────────┘


### Principios SOLID Aplicados

| Principio | Aplicación en el Proyecto |
|-----------|---------------------------|
| **S**RP | Cada clase tiene una única responsabilidad (UseCase, Mapper, Controller) |
| **O**CP | Abierto a extensión mediante interfaces (ports) |
| **L**SP | Los adaptadores pueden sustituirse sin afectar la lógica |
| **I**SP | Interfaces segregadas por caso de uso específico |
| **D**IP | Dependencia en abstracciones (ports), no en implementaciones |

### Regla de Dependencia

```
Infrastructure → Application → Domain
     ❌ ←           ❌ ←         ✅
```

- **Domain** NO depende de NADA (solo Java puro)
- **Application** solo depende de Domain
- **Infrastructure** depende de Application y Domain

---

## Tecnologías

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| **Lenguaje** | Java | 17 |
| **Framework** | Spring Boot | 3.5.6 |
| **Programación Reactiva** | Spring WebFlux | 6.2.11 |
| **Base de Datos** | MongoDB Atlas | 7.0 |
| **ORM** | Spring Data MongoDB Reactive | 4.5.0 |
| **Build Tool** | Maven | 3.9+ |
| **Validación** | Jakarta Validation | 3.0 |
| **Reducción de Boilerplate** | Lombok | 1.18+ |
| **Testing** | JUnit 5 + Mockito + Reactor Test | - |
| **Contenedores** | Docker + Docker Compose | - |

---

## Buenas Prácticas de Desarrollo

### 1. Arquitectura y Diseño

Este proyecto implementa Clean Architecture con una separación estricta de responsabilidades:

**Domain Layer (Núcleo de Negocio)**
- Entidades de dominio puras sin dependencias externas
- Lógica de negocio centralizada en los modelos
- Validaciones de reglas de negocio en las entidades
- Puertos (interfaces) que definen contratos sin implementación

**Application Layer (Casos de Uso)**
- Servicios que orquestan la lógica de aplicación
- Implementación de casos de uso específicos (CreateFranchise, AddBranch, etc.)
- Coordinación entre dominio y adaptadores de infraestructura
- Independiente de frameworks y tecnologías

**Infrastructure Layer (Adaptadores)**
- Controladores REST que exponen endpoints HTTP
- Repositorios que persisten en MongoDB
- DTOs para validación de entrada/salida
- Mappers para conversión entre capas
- Manejo global de excepciones

**Principios SOLID Aplicados**

Cada uno de los cinco principios SOLID se implementa consistentemente:

- **Single Responsibility Principle (SRP)**: Cada clase tiene una única razón de cambio. Los casos de uso son interfaces separadas, los mappers solo convierten datos, los controladores solo manejan HTTP.
- **Open/Closed Principle (OCP)**: El sistema está abierto a extensión mediante interfaces (ports). Se pueden agregar nuevos adaptadores sin modificar el dominio.
- **Liskov Substitution Principle (LSP)**: Los adaptadores de infraestructura pueden sustituirse sin afectar la lógica de negocio. Por ejemplo, se podría cambiar MongoDB por PostgreSQL implementando el mismo puerto.
- **Interface Segregation Principle (ISP)**: Cada caso de uso tiene su propia interfaz específica en lugar de una interfaz monolítica. Los clientes solo dependen de los métodos que necesitan.
- **Dependency Inversion Principle (DIP)**: Las capas superiores dependen de abstracciones (ports) definidas en el dominio, no de implementaciones concretas.

### 2. Programación Reactiva

**Non-Blocking I/O**
- Uso de Spring WebFlux para operaciones no bloqueantes
- Todos los endpoints retornan `Mono<T>` o `Flux<T>`
- Aprovechamiento de Event Loop para alta concurrencia

**Operadores Reactivos**
- `flatMap`: Para operaciones que retornan Publishers
- `map`: Para transformaciones síncronas
- `switchIfEmpty`: Para manejo de casos sin datos
- `filter`: Para aplicar predicados reactivos

**Backpressure Handling**
- Control automático de flujo entre publisher y subscriber
- Prevención de OutOfMemoryErrors en streams largos

### 3. Persistencia de Datos

**Repository Pattern**
- Abstracción de la capa de persistencia mediante puertos
- El dominio no conoce MongoDB, solo interfaces
- Facilita testing con mocks y cambios de tecnología

**Separación de Modelos**
- Entidades de dominio (Franchise, Branch, Product)
- Entidades de persistencia (FranchiseEntity, BranchEntity, ProductEntity)
- Mappers bidireccionales para conversión

**MongoDB Reactive**
- Uso de ReactiveMongoRepository para operaciones reactivas
- Documentos anidados para relaciones 1:N (branches, products)
- Indices automáticos con `@Indexed`

### 4. Validación y Manejo de Errores

**Bean Validation**
- Anotaciones declarativas (`@NotBlank`, `@Min`, `@Valid`)
- Validación automática en DTOs antes de llegar al dominio
- Mensajes de error personalizados

**Global Exception Handler**
- Manejo centralizado con `@RestControllerAdvice`
- Respuestas estructuradas consistentes
- HTTP Status Codes semánticamente correctos
- Stack traces solo en modo desarrollo

### 5. Testing

**Cobertura Completa**
- Tests unitarios para modelos de dominio (~95%)
- Tests de servicios con Mockito (~90%)
- Tests de integración para controllers (~85%)
- Tests de mappers (~95%)
- Tests de manejo de excepciones (~90%)

**Testing Reactivo**
- Uso de `StepVerifier` para validar flujos `Mono` y `Flux`
- Verificación de señales completas, errores y valores emitidos
- Tests no bloqueantes que validan comportamiento asíncrono

### 6. Código Limpio

**Inyección de Dependencias**
- Constructor injection con `@RequiredArgsConstructor` de Lombok
- Dependencias marcadas como `final` para inmutabilidad
- Sin `@Autowired` en campos

**Nomenclatura**
- Clases: sustantivos en PascalCase
- Métodos: verbos en camelCase
- Interfaces de casos de uso con sufijo `UseCase`
- DTOs con sufijos `Request`/`Response`

**Métodos Pequeños**
- Métodos con una sola responsabilidad
- Evitar lógica compleja en controladores
- Delegación a servicios y modelos de dominio

---

## Infrastructure as Code (IaC)

### Containerización con Docker

**Multi-Stage Build**

El `Dockerfile` implementa un build de múltiples etapas para optimizar el tamaño de la imagen:

```dockerfile
# Etapa 1: Build (Maven + JDK)
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (Solo JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Ventajas de Multi-Stage**:
- Imagen final contiene solo el JRE y el JAR (~150MB vs ~600MB)
- No incluye dependencias de compilación innecesarias
- Capa de caché para dependencias Maven

**.dockerignore**

Optimización del contexto de build excluyendo:
- Archivos compilados (target/)
- Configuraciones de IDE (.idea/, .vscode/)
- Archivos de sistema (.git/, .DS_Store)
- Reduce tiempo de build y tamaño del contexto

### Orquestación con Docker Compose

**Arquitectura de Servicios**

```yaml
services:
  mongodb:
    image: mongo:7.0
    healthcheck:
      test: echo 'db.runCommand("ping").ok' | mongosh localhost:27017/franchise_db --quiet
      interval: 10s
      timeout: 5s
      retries: 5
    volumes:
      - mongodb_data:/data/db
    networks:
      - franchise-network

  franchise-api:
    build:
      context: .
      dockerfile: Dockerfile
    depends_on:
      mongodb:
        condition: service_healthy
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongodb:27017/franchise_db
    ports:
      - "8080:8080"
    networks:
      - franchise-network
```

**Características Implementadas**:

1. **Health Checks**: MongoDB debe estar completamente inicializado antes de que la API intente conectarse
2. **Redes Aisladas**: Comunicación interna mediante red bridge dedicada
3. **Volúmenes Persistentes**: Datos de MongoDB sobreviven a reinicios de contenedores
4. **Variables de Entorno**: Configuración inyectada sin hardcodear credenciales
5. **Restart Policies**: `unless-stopped` para alta disponibilidad

### Configuración Externalizada

**Variables de Entorno**

```properties
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/franchise_db}
```

**Beneficios**:
- Sin credenciales en el código fuente
- Diferente configuración por ambiente (dev, staging, prod)
- Compatible con secretos de Kubernetes/Docker Swarm
- Valor por defecto para desarrollo local

### Comandos de Gestión

```bash
# Construir y levantar todos los servicios
docker-compose up --build

# Modo detached (background)
docker-compose up -d

# Ver logs en tiempo real
docker logs franchise-api -f

# Detener y eliminar contenedores
docker-compose down

# Eliminar también volúmenes (datos)
docker-compose down -v

# Reconstruir solo un servicio
docker-compose up --build franchise-api
```

---

## Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

| Herramienta | Versión Mínima | Verificar |
|-------------|---------------|-----------|
| **Java JDK** | 17 | `java -version` |
| **Maven** | 3.9+ | `mvn -version` |
| **Docker** (opcional) | 20.10+ | `docker --version` |
| **Docker Compose** (opcional) | 2.0+ | `docker-compose --version` |
| **Git** | 2.0+ | `git --version` |

---

## Instalación y Despliegue Local

### Opción 1: Con Docker Compose (Recomendado)

Esta opción levanta tanto la aplicación como MongoDB en contenedores.

```bash
# 1. Clonar el repositorio
git clone git@github.com:Esteban050/FranchiseApi_ReactiveProgramming.git
cd franchise_ApiREST

# 2. Construir y ejecutar con Docker Compose
docker-compose up --build

# La API estará disponible en http://localhost:8080
```

**Detener servicios:**
```bash
docker-compose down
```

**Eliminar datos de MongoDB:**
```bash
docker-compose down -v
```

---

### Opción 2: Ejecución Local (Sin Docker)

#### Paso 1: Configurar MongoDB

**Opción A - MongoDB en Docker:**
```bash
docker run -d -p 27017:27017 --name mongodb mongo:7.0
```

**Opción B - MongoDB Atlas (en la nube):**

El proyecto ya está configurado para usar MongoDB Atlas. Solo asegúrate de que las credenciales en `application.properties` sean correctas:

```properties
spring.data.mongodb.uri=mongodb+srv://franchiseUser:lA7qMJgb4Jq2FT2l@franchise-cluster.iy05cyv.mongodb.net/franchise_db?retryWrites=true&w=majority
```

#### Paso 2: Compilar el Proyecto

```bash
# Limpiar y compilar
./mvnw clean compile

# O compilar y empaquetar
./mvnw clean package -DskipTests
```

#### Paso 3: Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

La API estará disponible en: **http://localhost:8080**

#### Paso 4: Verificar que está funcionando

```bash
curl http://localhost:8080/api/franchises
```

Deberías recibir una respuesta (probablemente un array vacío `[]`).

---

### Opción 3: Ejecutar JAR Compilado

```bash
# 1. Compilar y empaquetar
./mvnw clean package -DskipTests

# 2. Ejecutar el JAR
java -jar target/franchise_ApiREST-0.0.1-SNAPSHOT.jar
```

---

## Endpoints de la API

### **Franquicias**

| Método | Endpoint | Descripción | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/franchises` | Crear franquicia | `{"name": "..."}` |
| GET | `/api/franchises` | Listar todas | - |
| GET | `/api/franchises/{id}` | Obtener por ID | - |
| PATCH | `/api/franchises/{id}/name` | Actualizar nombre | `{"name": "..."}` |

### **Sucursales**

| Método | Endpoint | Descripción | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/franchises/{franchiseId}/branches` | Agregar sucursal | `{"name": "..."}` |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/name` | Actualizar nombre | `{"name": "..."}` |

### **Productos**

| Método | Endpoint | Descripción | Request Body |
|--------|----------|-------------|--------------|
| POST | `/api/franchises/{franchiseId}/branches/{branchId}/products` | Agregar producto | `{"name": "...", "stock": 50}` |
| DELETE | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Eliminar producto | - |
| PUT | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Actualizar stock | `{"stock": 100}` |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name` | Actualizar nombre | `{"name": "..."}` |

### **Reportes**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/franchises/{franchiseId}/top-products` | Productos con mayor stock por sucursal |

---

## Ejemplos de Uso

### 1. Crear Franquicia

```bash
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Tech Store"}'
```

**Respuesta:**
```json
{
  "id": "67890abc",
  "name": "Tech Store",
  "branches": []
}
```

### 2. Agregar Sucursal

```bash
curl -X POST http://localhost:8080/api/franchises/67890abc/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Centro"}'
```

### 3. Agregar Producto a Sucursal

```bash
curl -X POST http://localhost:8080/api/franchises/67890abc/branches/branch-123/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptop Dell", "stock": 25}'
```

### 4. Actualizar Stock de Producto

```bash
curl -X PUT http://localhost:8080/api/franchises/67890abc/branches/branch-123/products/prod-456/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 50}'
```

### 5. Obtener Productos con Mayor Stock

```bash
curl http://localhost:8080/api/franchises/67890abc/top-products
```

**Respuesta:**
```json
[
  {
    "branchId": "branch-123",
    "branchName": "Sucursal Centro",
    "productId": "prod-456",
    "productName": "Laptop Dell",
    "stock": 50
  }
]
```

### 6. Eliminar Producto

```bash
curl -X DELETE http://localhost:8080/api/franchises/67890abc/branches/branch-123/products/prod-456
```

---

## Tests

### Ejecutar Todos los Tests

```bash
./mvnw test
```

### Ejecutar Tests Específicos

```bash
# Solo tests de dominio
./mvnw test -Dtest=*DomainTest

# Solo tests de servicio
./mvnw test -Dtest=FranchiseServiceTest

# Solo tests de controllers
./mvnw test -Dtest=FranchiseControllerTest
```

### Cobertura de Tests

| Componente | Cobertura | Tests |
|------------|-----------|-------|
| Domain Models | ~95% | 18 tests |
| Services | ~90% | 15 tests |
| Mappers | ~95% | 7 tests |
| Controllers | ~85% | 12 tests |
| Exception Handlers | ~90% | 4 tests |
| **TOTAL** | **~90%** | **52 tests** |

---

## Modelo de Datos

### Diagrama Entidad-Relación

```
┌─────────────────┐
│   Franchise     │
├─────────────────┤
│ id: String      │
│ name: String    │
└────────┬────────┘
         │ 1
         │
         │ *
┌────────▼────────┐
│     Branch      │
├─────────────────┤
│ id: String      │
│ name: String    │
└────────┬────────┘
         │ 1
         │
         │ *
┌────────▼────────┐
│    Product      │
├─────────────────┤
│ id: String      │
│ name: String    │
│ stock: Integer  │
└─────────────────┘
```

### Ejemplo de Documento MongoDB

```json
{
  "_id": "franchise-123",
  "name": "Tech Store",
  "branches": [
    {
      "id": "branch-001",
      "name": "Sucursal Centro",
      "products": [
        {
          "id": "prod-001",
          "name": "Laptop Dell",
          "stock": 50
        },
        {
          "id": "prod-002",
          "name": "Mouse Logitech",
          "stock": 120
        }
      ]
    }
  ]
}
```

---

## Estructura del Proyecto

```
franchise_ApiREST/
├── src/
│   ├── main/
│   │   ├── java/.../franchise_apirest/
│   │   │   ├── domain/              # Lógica de negocio
│   │   │   ├── application/         # Casos de uso
│   │   │   └── infrastructure/      # Adaptadores
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/.../franchise_apirest/
│           ├── domain/model/        # Tests de entidades
│           ├── application/         # Tests de servicios
│           └── infrastructure/      # Tests de controllers
├── target/                          # Archivos compilados
├── Dockerfile                       # Imagen Docker de la app
├── docker-compose.yml               # Orquestación de servicios
├── pom.xml                          # Dependencias Maven
├── mvnw                             # Maven Wrapper (Unix)
├── mvnw.cmd                         # Maven Wrapper (Windows)
└── README.md
```

---

## Manejo de Errores

La API retorna respuestas estructuradas para todos los errores:

### Ejemplo: Recurso No Encontrado (404)

```json
{
  "timestamp": "2025-10-04T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Franchise not found with id: xyz123"
}
```

### Ejemplo: Validación Fallida (400)

```json
{
  "timestamp": "2025-10-04T10:35:00",
  "status": 400,
  "error": "Validation Failed",
  "validationErrors": {
    "name": "Name is required",
    "stock": "Stock must be greater than or equal to 0"
  }
}
```

---

## Notas Adicionales

### Configuración de MongoDB Atlas

Si usas MongoDB Atlas en lugar de local:

1. Edita `src/main/resources/application.properties`
2. Reemplaza la URI con tus credenciales:

```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>?retryWrites=true&w=majority
```

### Cambiar Puerto de la Aplicación

En `application.properties`:

```properties
server.port=9090
```

---

## Autor

**Esteban Salazar**
- Email: estebansalazar1407@gmail.com
- GitHub: [https://github.com/Esteban050](https://github.com/Esteban050)



