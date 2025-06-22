# Implementation 1 (MongoDB)

The database is configured using Spring [application.properties](src/main/resources/application.properties) and
MongoDB repositories are enabled using the `@EnableMongoRepositories` annotation in the main class
[Impl1MongodbApplication.java](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl1mongodb/Impl1MongodbApplication.java).

The database is accessed via the [repository](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl1mongodb/repository)
package, which contains the repository interfaces for the different entities.

The entities are defined in the [model](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl1mongodb/model) package.

The entry points for requests are defined in the 
[controller](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl1mongodb/controller) package, whose classes implement the
generated API interfaces. Exceptions are handled using a global 
[ControllerAdvice](src/main/java/ch/rafaelurben/edu/ffhs/ta2/impl1mongodb/controller/ControllerExceptionHandler.java)
and `SneakyThrows` to satisfy the API interface contract.
