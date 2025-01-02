# Rack Health Microservice

The Rack Health Microservice queries the health status of all capabilities defined for a rack based on the rack capabilities provided in the [capabilities.json](rackhealthms/capabilities.json) file.
The microservice then aggregates the health status for all capabilities on the rack into a single health report to provide details into the overall health of the rack capabilities.
## Development Setup

Build using `mvn clean install`

Run using `java -jar target/rack-health-ms.jar`  

### Running Locally

`mvn spring-boot:run`

Once running, application will be locally accessible at http://localhost:8080/health
<br><br>

## Building

Build the project using `mvn clean install`.
Copy the built jar file into the corresponding directory structure as required by the Dockerfile.

    docker build -t="/rackhealthms" .


<br><br>

## Deploying

Copy the Capabilities.json file to rackhealthms/capabilities.json. Define the capabilities in this file based on the specific features of the rack, as shown below.

```
[
  {
    "name": "IR",
    "shorthand": "IRR"
  },
  {
    "name": "Power",
    "shorthand": "PWR"
  },
  {
    "name": "Video",
    "shorthand": "VID"
  },
  {
    "name": "Trace",
    "shorthand": "TCE"
  }
]
```

## Access the Swagger Documentation

The Swagger Documentation for the Rack health Microservice can be accessed at http://localhost:8080/health/swagger-ui.html when running locally. Default swagger path is **/swagger-ui.html**.


<br><br>

## NGINX Configuration

NGINX is used to support a unified path for communication to the rack microservices as well as communication between the rack microservices. NGINX configuration for rack-health-ms can be found at [rack-health-ms.conf](conf/rack-health-ms.conf). This configuration file is used to route requests to the rack health microservice.


<br><br>

### Rack Health Check

    GET http://localhost:8080/health/actuator/health 
