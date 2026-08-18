# Bank Legacy Batch


## Desarrollo Backend III - PBY2203


Proyecto desarrollado para la actividad de Semana 1 de Desarrollo Backend III.


El objetivo es modernizar tres procesos batch pertenecientes a un sistema legacy del Banco XYZ utilizando Spring Batch, reemplazando el procesamiento tradicional de archivos por un procesamiento estructurado mediante Jobs, Steps, ItemReader, ItemProcessor e ItemWriter.


---


## Objetivo del proyecto


Implementar un sistema de migración de procesos batch utilizando Spring Batch capaz de leer información desde archivos CSV, validar y transformar los datos y finalmente almacenarlos en una base de datos MySQL.


El proyecto implementa tres procesos principales:


1. Reporte de transacciones diarias.
2. Cálculo de intereses mensuales.
3. Generación de estados de cuenta anuales.


---


## Tecnologías utilizadas


- Java 17
- Spring Boot 3.5.3
- Spring Batch 5.2.2
- Maven
- MySQL
- MySQL Workbench
- Git
- GitHub
- CSV
- JUnit


---


## Estructura del proyecto


```text
bank-legacy-batch
│
├── Evidencias
│   ├── Screenshot 2026-08-17 221120.png
│   ├── Screenshot 2026-08-17 221149.png
│   ├── Screenshot 2026-08-17 221403.png
│   └── Screenshot 2026-08-17 221555.png
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.banco.bank_legacy_batch
│   │   │       ├── config
│   │   │       │   ├── BatchConfig.java
│   │   │       │   ├── TransaccionesJobConfig.java
│   │   │       │   ├── InteresesJobConfig.java
│   │   │       │   └── CuentasAnualesJobConfig.java
│   │   │       │
│   │   │       ├── model
│   │   │       │   ├── Transaccion.java
│   │   │       │   ├── Interes.java
│   │   │       │   └── CuentaAnual.java
│   │   │       │
│   │   │       ├── processor
│   │   │       │   ├── TransaccionProcessor.java
│   │   │       │   ├── InteresProcessor.java
│   │   │       │   └── CuentaAnualProcessor.java
│   │   │       │
│   │   │       ├── Exception
│   │   │       │   └── DatoInvalidoException.java
│   │   │       │
│   │   │       └── BankLegacyBatchApplication.java
│   │   │
│   │   └── resources
│   │       ├── Data
Procesos Batch implementados
1. Reporte de Transacciones Diarias

El Job transaccionesJob procesa el archivo:

src/main/resources/Data/transacciones.csv

El proceso utiliza:

FlatFileItemReader para leer el CSV.
TransaccionProcessor para validar y transformar los datos.
JdbcBatchItemWriter para guardar los registros procesados en MySQL.

Los registros pueden quedar con estados como:

PROCESADO

o:

PROCESADO_CON_OBSERVACIONES

Los resultados son almacenados en:

transacciones_procesadas
2. Cálculo de Intereses Mensuales

El Job interesesJob procesa:

src/main/resources/Data/intereses.csv

El proceso:

Lee los datos de las cuentas.
Procesa el tipo de cuenta.
Calcula el interés correspondiente.
Calcula el saldo final.
Guarda el resultado en MySQL.

Los resultados se almacenan en:

intereses_procesados

Los datos generados contienen información como:

Cuenta.
Nombre.
Saldo.
Edad.
Tipo.
Interés.
Saldo final.
Estado.
3. Generación de Estados de Cuenta Anuales

El Job cuentasAnualesJob procesa:

src/main/resources/Data/cuentas_anuales.csv

Este proceso permite compilar la información anual de las cuentas para generar información que puede ser utilizada para revisión y auditoría.

Los resultados se almacenan en:

cuentas_anuales_procesadas
Base de datos

El proyecto utiliza MySQL.

Base de datos:

bank_batch

Configuración utilizada durante el desarrollo:

spring.datasource.url=jdbc:mysql://localhost:3306/bank_batch
spring.datasource.username=root
spring.datasource.password=1234567
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

La contraseña utilizada durante el desarrollo local fue:

1234567

Nota de seguridad: esta contraseña corresponde al entorno local utilizado para desarrollar y ejecutar la actividad. En un entorno productivo no se recomienda almacenar contraseñas directamente en application.properties. Se recomienda utilizar variables de entorno o un sistema de gestión de secretos.

Configuración de Spring Batch

Spring Batch utiliza las tablas internas necesarias para registrar la ejecución de los Jobs.

La configuración utilizada es:

spring.batch.jdbc.initialize-schema=always
spring.sql.init.mode=always
spring.batch.job.enabled=true

Estas configuraciones permiten inicializar las tablas necesarias para administrar las ejecuciones de Spring Batch.

Manejo de errores

El proyecto contempla el manejo de datos incorrectos mediante:

Validaciones en los ItemProcessor.
Excepciones personalizadas.
faultTolerant().
skip(Exception.class).
skipLimit().
Estados de procesamiento para identificar registros con observaciones.

Esto permite que un registro con problemas no necesariamente detenga todo el procesamiento batch.

Procesamiento por chunks

Los Steps utilizan procesamiento por bloques mediante chunk.

Ejemplo:

.chunk(10)

Esto permite procesar los registros en grupos, mejorando el rendimiento y permitiendo administrar las transacciones de manera eficiente.

Ejecución del proyecto
Requisitos

Antes de ejecutar el proyecto se necesita tener instalado:

Java 17 o superior.
MySQL.
Maven o utilizar el Maven Wrapper incluido.
Git.
1. Crear la base de datos

En MySQL ejecutar:

CREATE DATABASE bank_batch;

Luego seleccionar:

USE bank_batch;

El proyecto contiene el archivo:

src/main/resources/schema.sql

que contiene la estructura necesaria para las tablas de resultados.

2. Configurar la conexión

Editar:

src/main/resources/application.properties

y configurar:

spring.datasource.url=jdbc:mysql://localhost:3306/bank_batch
spring.datasource.username=root
spring.datasource.password=1234567
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

La contraseña debe corresponder a la contraseña configurada para el usuario root de MySQL en el equipo donde se ejecute el proyecto.

3. Ejecutar las pruebas

Desde la carpeta raíz del proyecto:

.\mvnw.cmd clean test

El resultado esperado es:

BUILD SUCCESS
Ejecución de los Jobs

Para ejecutar nuevamente un Job utilizando un nuevo parámetro de ejecución:

Transacciones
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.batch.job.name=transaccionesJob run.id=10"
Intereses
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.batch.job.name=interesesJob run.id=11"
Cuentas anuales
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.batch.job.name=cuentasAnualesJob run.id=12"

El parámetro run.id permite realizar nuevas ejecuciones del Job sin reutilizar exactamente la misma instancia de ejecución registrada por Spring Batch.

Verificación de resultados

Los resultados pueden comprobarse desde MySQL Workbench:

USE bank_batch;


SELECT * FROM transacciones_procesadas;


SELECT * FROM intereses_procesados;


SELECT * FROM cuentas_anuales_procesadas;

También se pueden obtener los totales:

SELECT COUNT(*) AS total_transacciones
FROM transacciones_procesadas;


SELECT COUNT(*) AS total_intereses
FROM intereses_procesados;


SELECT COUNT(*) AS total_cuentas_anuales
FROM cuentas_anuales_procesadas;
Evidencias de ejecución

La carpeta Evidencias contiene capturas de pantalla que muestran:

Ejecución de los Jobs.
Procesamiento de los registros.
Resultados almacenados en MySQL.
Información generada por los procesos batch.
Resultado

Los tres procesos fueron implementados utilizando Spring Batch:

Proceso	Job	Entrada	Salida
Transacciones diarias	transaccionesJob	transacciones.csv	transacciones_procesadas
Intereses mensuales	interesesJob	intereses.csv	intereses_procesados
Estados de cuenta anuales	cuentasAnualesJob	cuentas_anuales.csv	cuentas_anuales_procesadas

El proyecto permite modernizar los procesos batch del sistema legacy mediante una arquitectura basada en lectura, procesamiento y escritura de datos utilizando Spring Batch.

Autor

Kevin

Desarrollo Backend III - PBY2203

DUOC UC

Repositorio

Proyecto disponible en GitHub:

https://github.com/Kevinlovera/bank-legacy-batch



### Después de reemplazar el README


En PowerShell, dentro de tu proyecto, ejecuta:


```powershell
git add Readme.md

Luego:

git commit -m "Actualizar README con documentacion del proyecto"

Y finalmente:

git push