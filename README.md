# nofapp

## Description
This project is a backend for [nofapp-ui](https://github.com/zor07/nofapp-ui)

## Technologies
* This is a spring-boot project with PostgreSQL database
* API is secured with JWT authentication
* JPA is used as data access layer
* Liquibase is used as db migration tool
* For testing TestNG and testcontainers are used

## Requirements
For building and running the application you need:
* [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Maven 3](https://maven.apache.org/)
* [Docker](https://www.docker.com/)
* [docker-compose](https://docs.docker.com/compose/gettingstarted/)

## Running the application locally

Raise the database by running docker-compose file from root of the project:

```bash
docker-compose up -d
```

Run application.

There are several ways to run a Spring Boot application on your local machine. 
One way is to execute the main method in the `com.zor07.nofapp.NofappApplication` class from your IDE.

Alternatively you can use the Spring Boot Maven plugin like so:

```bash
mvn spring-boot:run
```

On first startup migration scripts will be run. After that two users will be created: 

| username | password | 
|----------|:--------:|
| admin    |  admin   | 
| demo     |   demo   | 

## Swagger

After application startup swagger-ui will be available on:

 [http://127.0.0.1:8888/swagger-ui/#/](http://127.0.0.1:8888/swagger-ui/#/)

# Deploying the application to Heroku
The easiest way to deploy the sample application to Heroku is to push commit to `release` branch. 

Application on heroku: https://nofapp-backend.herokuapp.com/
