# LinkNix API

API REST integradora para classificação de chamados de sistemas Help Desk mediante modelos de IA.

## PostgreSQL local

1. Cree una base de datos vacía llamada `linknix` desde pgAdmin.
2. Defina las credenciales en PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/linknix"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="su_contrasena_de_postgresql"
```

3. Inicie la aplicación:

```powershell
.\mvnw.cmd spring-boot:run
```

Flyway ejecutará automáticamente `V1__create_initial_schema.sql`. Hibernate utilizará `ddl-auto=validate` para comprobar que las entidades coincidan con el esquema, sin modificarlo automáticamente.

## Verificación en pgAdmin

Después de iniciar la aplicación, actualice el árbol de la base `linknix` y abra:

```text
Schemas > public > Tables
```

Deben aparecer las 12 tablas del dominio y la tabla `flyway_schema_history`.
