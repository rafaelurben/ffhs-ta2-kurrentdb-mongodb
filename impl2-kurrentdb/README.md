# Implementation 2 (KurrentDB)

The database access is configured using
the [KurrentDBConfig class](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl2kurrentdb/config/KurrentDBConfig.java), which
takes the connection string from the Spring [application.properties](src/main/resources/application.properties).

The database is accessed via
[StreamService](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl2kurrentdb/service/StreamService.java) and
[ProjectionService](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl2kurrentdb/service/ProjectionService.java) or
rather their implementations
[StreamServiceImpl](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl2kurrentdb/service/StreamServiceImpl.java) and
[ProjectionServiceImpl](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl2kurrentdb/service/ProjectionServiceImpl.java).

The server-side projections are defined in the [projections](src/main/resources/projections) folder and are applied on
application startup by
the [StartupListener](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl2kurrentdb/listeners/StartupListener.java), which
uses the `ProjectionService` to create the projections in KurrentDB.

The entry points for requests are defined in the 
[controller](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl2kurrentdb/controller) package, whose classes implement
the generated API interfaces. Exceptions are handled using a global 
[ControllerAdvice](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl2kurrentdb/controller/ControllerExceptionHandler.java)
and `SneakyThrows` to satisfy the API interface contract.
