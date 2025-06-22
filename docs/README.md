# Documentation

For setup and usage instructions, see the [README](../README.md) in the root directory.

## Modules in this repository

- [specs](../specs): OpenAPI specifications for the API and OpenAPI Generator configuration.
- [impl1-mongodb](../impl1-mongodb): Implementation of the API using MongoDB.
- [impl2-kurrentdb](../impl2-kurrentdb): Implementation of the API using KurrentDB.
- [testing-client](../testing-client): A client for testing the API implementations.
- [stats](../stats): A simple python script to analyze the test results.

## Infrastructure

The different parts of the infrastructure are running as Docker containers.

- Both databases are using their respective official Docker images: `mongodb` and `kurrentdb`.
- For MongoDB, the `mongo-express` image is used to provide a web interface for simple administration.
- For KurrentDB, a simple embedded web interface is provided by the `kurrentdb` image itself.
- Both API implementations are built using their own Dockerfiles based on the `amazoncorretto:21.0.4-alpine3.18` image.
- The testing client is run directly on the host machine and accesses the API implementations using exposed ports.

![Deployment diagram](diagrams/deployment-diagram.png)

The infrastructure is defined using Docker Compose. The [docker-compose.yml](../docker-compose.yaml) file defines all
services, their configurations and resource limitations.

## Maven & OpenAPI Setup

The [`root` pom.xml](../pom.xml) is the parent POM for all modules in this repository. It includes all dependency
management and plugin management for the modules, including versions using `properties`.

The [`specs` pom.xml](../specs/pom.xml) inherits from the root POM and contains the OpenAPI Generator plugin
configuration to generate the
API client and server stubs. These are generated during the `compile` phase and installed in the `package` phase.

The implementation modules ([`impl1-mongodb` pom.xml](../impl1-mongodb/pom.xml) and
[`impl2-kurrentdb` pom.xml](../impl2-kurrentdb/pom.xml)) inherit from the root POM and includes the generated server
stub as a dependency.

The testing client module ([`testing-client`](../testing-client/pom.xml)) also inherits from the root POM and uses the 
generated API client to test the implementations.

![Maven dependency diagram](diagrams/maven-dependencies.png)

## Implementations

See the respective implementation modules for details on how they are set up:
- [impl1-mongodb](../impl1-mongodb/README.md)
- [impl2-kurrentdb](../impl2-kurrentdb/README.md)
