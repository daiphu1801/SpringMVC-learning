# Skill: DDD Architecture Design and Folder Structure Verification

## Metadata
- **ID**: `ddd_architecture_design_skill`
- **Description**: Ensures that any new feature or refactored component strictly conforms to Domain-Driven Design (DDD) principles and Clean Architecture (Ports & Adapters) boundaries.
- **Triggers**: Executed before starting and upon completing the file structure design of any new Bounded Context or feature module.

## Prerequisites
- A defined Bounded Context name (e.g., `user`, `order`, `product`).
- Root package structure: `com.examp.springmvc.[context]`.

## Design Rules & Architecture Boundaries

### 1. Domain Layer (`.../[context]/domain/model`)
- **Rule**: Must contain only pure Java POJOs representing Entities, Value Objects, and Domain Events.
- **Dependency Rule**: **Zero dependencies** on external frameworks (No Spring annotations like `@Component`/`@Service`, no MyBatis annotations, no HTTP/Servlet imports).
- **Behavior**: Must encapsulate business validation logic (e.g., `.validate()`) and self-contained state mutations (e.g., `.activate()`).

### 2. Application Layer
- **Ports (`.../[context]/application/ports/output` or `/input`)**:
  - **Rule**: Output ports (interfaces) specify what the application needs to store/retrieve data (e.g., `UserPersistencePort`).
- **Use Cases (`.../[context]/application/usecase`)**:
  - **Rule**: Classes must represent a single, specific orchestration flow (e.g., `CreateUserUseCase`).
  - **Dependency Rule**: Depend only on Domain Model and Output Ports. Do not import controllers, persistence adapters, mapper entities, or specific database classes.

### 3. Infrastructure Layer
- **Persistence Adapter (`.../[context]/infrastructure/persistence/mybatis`)**:
  - **Rule**: Implements the Output Port interface (e.g., `UserPersistenceAdapter implements UserPersistencePort`).
  - **Mapping Rule**: Database tables must be mapped to distinct database entities (e.g., `UserDbEntity`). Do not expose `UserDbEntity` outside of the infrastructure package. Use a dedicated mapper/converter (e.g., `UserDataAccessMapper`) to map Domain models to DB entities.
- **Web Adapter (`.../[context]/infrastructure/web`)**:
  - **Rule**: Contains Spring Web MVC Controllers (e.g., `UserController`).
  - **Execution Rule**: Controllers must only communicate with the Application Layer via Use Cases. They are prohibited from calling Persistence Ports, Mappers, or Repositories directly.

---

## Success & Exit Criteria
The agent must verify that the new feature folder structure complies with the following layout:
```text
src/main/java/com/examp/springmvc/[context]
├── domain
│   └── model
│       └── [EntityName].java
├── application
│   ├── ports
│   │   └── output
│   │       └── [EntityName]PersistencePort.java
│   └── usecase
│       ├── Create[EntityName]UseCase.java
│       └── ...
└── infrastructure
    ├── persistence
    │   └── mybatis
    │       ├── [EntityName]DbEntity.java
    │       ├── [EntityName]Mapper.java
    │       ├── [EntityName]PersistenceAdapter.java
    │       └── [EntityName]DataAccessMapper.java
    └── web
        └── [EntityName]Controller.java
```

## Error Recovery & Verification Steps
- **Checking Dependency Leaks**: Scan import statements in `domain/` and `application/` folders. If any package starting with `org.springframework.web`, `org.apache.ibatis`, or `infrastructure` is found, refactor it out.
- **Bypassed UseCase Check**: Verify that `UserController` has constructor injections **only** for `*UseCase` classes. If a direct Mapper/Repository injection is found, refactor by introducing a Use Case.
