FROM java:8-alpine
MAINTAINER Pedro Amaral <pedroamaral@ice.ufjf.br>

ADD target/uberjar/reward_system.jar /reward_system/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/reward_system/app.jar"]
