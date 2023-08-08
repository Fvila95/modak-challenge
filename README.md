# Modak Challenge

This project is a Java application based on Spring Boot that handles sending notifications and limits their use by user ID and notification type.

## Index
* [Getting Started](#getting-started)
* [Postman Collection](#postman-collection)
* [API Endpoints](#api-endpoints)
* [Built With](#built-with)

## Getting Started
To have a local copy up and running, follow these simple steps:

1. Install docker and docker-compose.

2. Clone the repository
```
git clone https://github.com/Fvila95/modak-challenge.git
```
3. Run the project with docker-compose using these two commands.
```
docker-compose build
docker-compose up
```

## Postman Collection
This project includes a Postman collection that contains an example call to the API endpoint. You can find the collection in the postman-collection folder. To use it, follow these steps:

1. Open Postman
2. Click on "Import"
3. Choose "Import from file" and navigate to the collection file in the `postman-collection` folder
4. Click "Import"

You should now see the Postman collection in your collection list and can use it to test the API endpoint.

## API Endpoints
This application includes the following endpoint

1. POST /modak-challenge/notifications: This endpoint accepts an object {"type": "status","userId": "user1","message": "Hello!"} and returns the notification sending status, whether it was successful or erroneous, along with a detailed message.

## Built With
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Docker](https://www.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)
* [Redis](https://redis.io/)
