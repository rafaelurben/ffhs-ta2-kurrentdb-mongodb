FROM amazoncorretto:21.0.4-alpine3.18
LABEL org.opencontainers.image.authors="Rafael Urben"
COPY target/impl2-kurrentdb-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
