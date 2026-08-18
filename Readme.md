Bank Legacy Batch
1. Descripción del proyecto
Este proyecto implementa una solución de migración de procesos batch para modernizar procesos legacy del Banco XYZ utilizando Spring Batch.
La solución procesa archivos CSV, aplica validaciones y transformaciones mediante `ItemProcessor` y persiste los resultados en una base de datos relacional MySQL.
Se implementan tres procesos principales:
Reporte de transacciones diarias.
Cálculo de intereses mensuales.
Generación de estados de cuenta anuales.
Proyecto desarrollado para Desarrollo Backend III (PBY2203).
2. Objetivos
Migrar procesos batch legacy hacia Spring Batch.
Leer información desde archivos CSV.
Procesar y validar datos mediante `ItemProcessor`.
Persistir información en MySQL.
Manejar inconsistencias mediante mecanismos de `skip` y `retry`.
Utilizar procesamiento por `chunk`.
Mantener trazabilidad mediante las tablas de metadatos de Spring Batch.
3. Tecnologías
Java 17
Spring Boot 3.5.3
Spring Batch
Maven
MySQL
MySQL Workbench
JUnit
Git / GitHub
Visual Studio Code
4. Estructura
```text
bank-legacy-batch/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/banco/bank_legacy_batch/
│   │   │       ├── config/
│   │   │       │   ├── TransaccionesJobConfig.java
│   │   │       │   ├── InteresesJobConfig.java
│   │   │       │   └── CuentasAnualesJobConfig.java
│   │   │       ├── model/
│   │   │       ├── processor/
│   │   │       └── exception/
│   │   └── resources/
│   │       ├── data/
│   │       │   ├── transacciones.csv
│   │       │   ├── intereses.csv
│   │       │   └── cuentas_anuales.csv
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```
5. Arquitectura
Cada proceso utiliza el patrón de procesamiento de Spring Batch:
```text
CSV
 │
 ▼
ItemReader
 │
 ▼
ItemProcessor
 │
 ▼
ItemWriter
 │
 ▼
MySQL
```
Los Steps utilizan procesamiento por `chunk`, permitiendo procesar registros en bloques y administrar las operaciones de escritura de forma transaccional.
6. Jobs implementados
6.1 Reporte de transacciones diarias
Configuración:
`TransaccionesJobConfig.java`
Entrada:
`data/transacciones.csv`
Salida:
`transacciones_procesadas`
El proceso lee las transacciones, transforma los campos al modelo `Transaccion`, ejecuta `TransaccionProcessor` y escribe los resultados procesados en MySQL.
6.2 Cálculo de intereses mensuales
Configuración:
`InteresesJobConfig.java`
Entrada:
`data/intereses.csv`
Salida:
`intereses_procesados`
El proceso lee las cuentas, transforma la información al modelo `Interes`, ejecuta `InteresProcessor` y persiste los resultados calculados.
6.3 Estados de cuenta anuales
Configuración:
`CuentasAnualesJobConfig.java`
Entrada:
`data/cuentas_anuales.csv`
Salida:
`cuentas_anuales_procesadas`
El proceso lee los registros anuales, ejecuta el processor correspondiente y persiste la información procesada para su consulta y auditoría.
7. Manejo de errores
Los Steps consideran que los datos provenientes de un sistema legacy pueden contener inconsistencias.
Se utilizan mecanismos de tolerancia de Spring Batch como:
```java
.faultTolerant()
.skip(Exception.class)
.skipLimit(10)
```
En el proceso de intereses también se utiliza:
```java
.retry(Exception.class)
.retryLimit(2)
```
Esto permite manejar errores durante el procesamiento sin detener necesariamente todo el Job.
Las métricas de lectura, escritura, filtrado y omisión pueden consultarse en las tablas de metadatos de Spring Batch.
8. Base de datos
Base de datos:
`bank_batch`
Tablas de salida:
```text
transacciones_procesadas
intereses_procesados
cuentas_anuales_procesadas
```
Spring Batch también utiliza tablas internas como:
```text
batch_job_execution
batch_step_execution
batch_step_execution_context
batch_job_execution_context
```
Estas tablas permiten consultar el estado y las métricas de las ejecuciones.
9. Configuración
Archivo:
`src/main/resources/application.properties`
Ejemplo:
```properties
spring.application.name=bank-legacy-batch

spring.datasource.url=jdbc:mysql://localhost:3306/bank_batch
spring.datasource.username=root
spring.datasource.password=1234567
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

spring.batch.jdbc.initialize-schema=always
spring.sql.init.mode=always

spring.batch.job.enabled=true
```
La contraseña debe adaptarse al entorno local antes de ejecutar el proyecto.
10. Ejecución
Desde la carpeta raíz:
```powershell
.\mvnw.cmd clean test
```
Una ejecución correcta debe finalizar con:
```text
BUILD SUCCESS
```
Para ejecutar la aplicación:
```powershell
.\mvnw.cmd spring-boot:run
```
Durante la ejecución se pueden revisar el Job, Step, registros leídos, filtrados, escritos, omitidos y el estado final.
11. Consultas de verificación
```sql
USE bank_batch;

SELECT * FROM transacciones_procesadas;
SELECT * FROM intereses_procesados;
SELECT * FROM cuentas_anuales_procesadas;
```
Métricas de Steps:
```sql
SELECT
    STEP_NAME,
    READ_COUNT,
    WRITE_COUNT,
    FILTER_COUNT,
    READ_SKIP_COUNT,
    PROCESS_SKIP_COUNT,
    WRITE_SKIP_COUNT,
    STATUS
FROM batch_step_execution
ORDER BY STEP_EXECUTION_ID DESC;
```
Ejecuciones de Jobs:
```sql
SELECT *
FROM batch_job_execution
ORDER BY JOB_EXECUTION_ID DESC;
```
12. Evidencia de ejecución
La entrega debe incluir capturas de:
ejecución del Job de transacciones;
ejecución del Job de intereses;
ejecución del Job de cuentas anuales;
resultados en MySQL Workbench;
métricas de `batch_step_execution`;
ejecución de pruebas con `BUILD SUCCESS`.
13. Pruebas
El proyecto cuenta con una prueba automatizada ejecutada mediante Maven.
Comando:
```powershell
.\mvnw.cmd clean test
```
Durante la implementación se obtuvo:
```text
Tests run: 1
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```
14. Control de versiones
El código fuente debe mantenerse en un repositorio GitHub perteneciente al estudiante e incluir el código, configuración, CSV, `pom.xml` y `README.md`.
15. Conclusión
La solución implementa la migración de tres procesos batch hacia Spring Batch utilizando `ItemReader`, `ItemProcessor` e `ItemWriter`.
Los procesos cuentan con Jobs y Steps independientes, procesamiento por chunks, persistencia en MySQL y mecanismos de tolerancia para manejar inconsistencias provenientes de datos legacy.
Las tablas de metadatos de Spring Batch permiten mantener la trazabilidad de las ejecuciones y facilitan la revisión de los procesos para fines de auditoría.