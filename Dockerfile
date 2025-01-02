FROM --platform=$BUILDPLATFORM amazoncorretto:17-alpine3.20

RUN mkdir /app
VOLUME /app
VOLUME /logs
VOLUME /rack-health-ms

ADD target/rack-health-ms.jar /app/rack-health-ms.jar

CMD java -jar /app/rack-health-ms.jar -Dspring.profiles.active=${PROFILE}
EXPOSE 8080
