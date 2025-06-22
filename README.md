# ffhs-ta2-kurrentdb-mongodb

Dies ist der Code zu meiner zweiten Transferarbeit an der [FFHS](https://ffhs.ch) im Rahmen meines Informatik-Studiums.

In der Arbeit geht es um einen Vergleich von zwei API-Implementierungen, die auf unterschiedlichen Datenbanken basieren:
MongoDB und KurrentDB. Die Implementierungen sind in Java geschrieben und nutzen Spring Boot, die API ist mittels
OpenAPI v3.0.3 spezifiziert.

Die weitere Dokumentation ist in Englisch gehalten, um Sprachwechsel zwischen Code und Dokumentation zu vermeiden.

## Documentation

See [docs](docs/README.md).

## Requirements

- Git
- Docker
- Java 21
- Maven
- Python 3.12+ (optional, for analyzing the results)

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/rafaelurben/ffhs-ta2-kurrentdb-mongodb.git
    cd ffhs-ta2-kurrentdb-mongodb
    ```
2. Start the infrastructure docker containers:
    ```bash
    docker compose -f docker-compose.yaml up -d --wait
    ```
3. Generate, compile and package the Java projects:
    ```bash
    mvn clean compile package
    ```
4. Start the API implementations in docker containers for resource isolation:
    ```bash
    docker compose -f docker-compose.yaml --profile implementations up -d --build --wait
    ```
5. Run the tests:
    ```bash
    mvn -f testing-client/pom.xml clean compile exec:java
    ```
6. (Optional) Install the Python dependencies:
    ```bash
    python -m pip install -r stats/requirements.txt
    ```
7. (Optional) Analyze the results:
    ```bash
    python stats/analyze.py
    ```

## Ports and services

| Service                                | Port                            |
|----------------------------------------|---------------------------------|
| API Implementation 1 (MongoDB)         | 8181                            |
| MongoDB database                       | 27017                           |
| MongoDB admin (mongo-express)          | [27018](http://localhost:27018) |
| API Implementation 2 (KurrentDB)       | 8182                            |
| KurrentDB database (incl. embedded UI) | [2113](http://localhost:2113)   |

## Uninstallation / Cleanup

1. To stop and remove the implementation Docker containers, run:
    ```bash
    docker compose -f docker-compose.yaml --profile implementations down
    ```
2. To stop and remove the infrastructure Docker containers including volumes, run:
    ```bash
    docker compose -f docker-compose.yaml down --volumes
    ```
