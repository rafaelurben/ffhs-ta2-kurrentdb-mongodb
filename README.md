# ffhs-ta2-kurrentdb-mongodb

Transferarbeit 2 an der FFHS zum Thema KurrentDB vs MongoDB

## Requirements

- Git
- Docker
- Java 21+
- Maven

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/rafaelurben/ffhs-ta2-kurrentdb-mongodb.git
    cd ffhs-ta2-kurrentdb-mongodb
    ```
2. Run the Docker containers:
    ```bash
    docker compose up -d
    ```
3. Compile and install the Java projects:
    ```bash
    mvn clean compile install
    ```

## Ports and services

| Service                                | Port                            |
|----------------------------------------|---------------------------------|
| API Implementation 1 (MongoDB)         | 8081                            |
| MongoDB database                       | 27017                           |
| MongoDB admin (mongo-express)          | [27018](http://localhost:27018) |
| API Implementation 2 (KurrentDB)       | 8082                            |
| KurrentDB database (incl. embedded UI) | [2113](http://localhost:2113)   |
