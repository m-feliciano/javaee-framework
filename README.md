# servlets (Jakarta Servlet)
Web Servlet Application (Full Java/JSP) for product control using MVC pattern, Slf4j for Logger, Java Reflection to build and process all requests and StopWatch to collecting and recording app metrics

## Layout

### Login

#### path example: http://server/login?action=loginForm
![App login page](https://i.ibb.co/R0xM6Ps/Screenshot-2022-07-17-034301.png)
<br>

### Home
#### path example: http://sever/product?action=list
![App home page](https://i.ibb.co/PZHJjKc/Screenshot-2022-08-20-153909.png)
<br>

### Product
#### path example: http://server/product?action=list&id=2
![App prolist list page](https://i.ibb.co/1fy8JtG/Screenshot.png)
<br>

### Not Found
#### path example: http://server/product?action=list&id=/some_invalid_id/
![App not found page](https://i.ibb.co/th8R564/Capture5.png)
<br>

## Logging
![Logging with slf4j](https://i.ibb.co/F6ZK5vz/Screenshot-2022-08-08-003934.png)
<br>

## Packages
```
C:.
├───main
│   ├───java
│   │   ├───application
│   │   ├───controllers
│   │   ├───dao
│   │   ├───domain
│   │   │   └───enums
│   │   ├───dto
│   │   ├───exceptions
│   │   ├───filter
│   │   ├───servlets
│   │   │   ├───base
│   │   │   ├───category
│   │   │   ├───inventory
│   │   │   ├───product
│   │   │   ├───user
│   │   │   └───utils
│   │   └───utils
│   │       └───cache
│   ├───resources
│   │   └───META-INF
│   └───webapp
│       ├───assets
│       ├───css
│       ├───META-INF
│       └───WEB-INF
│           └───view
│               ├───components
│               └───pages
│                   ├───category
│                   ├───inventory
│                   ├───product
│                   └───user
└───test
    └───java
        ├───controllers
        ├───servlets
        └───utils
```

## Script to create the database
```docker

## create nw
docker network create -d bridge servlet

## run container
docker run --name servlet \
--network=servlet -p 5432:5432 \
-e "POSTGRES_USER=postgres" \
-e "POSTGRES_PASSWORD=password" \
-d postgres

## exec 
docker exec -it servlet psql -U postgres
```