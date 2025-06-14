# ffhs-ta2-kurrentdb-mongodb

Transferarbeit 2 an der FFHS zum Thema KurrentDB vs MongoDB

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
