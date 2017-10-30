# RESTful service

Simple REST API modelling money transfer between accounts.

Built using Jersey Framework and Grizzly HTTP server.

## Build and run

* *Build executable jar:* `mvn clean package`

* *Run service:* `java -Dlog4j.configurationFile=src/main/resources/log4j.xml -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager -jar target/restful-service-1.0-SNAPSHOT.jar`
