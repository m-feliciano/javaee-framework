# servlets (Jakarta Servlet)
Web Servlet Application (Full Java/JSP) for product control using MVC pattern, Slf4j for Logger, Java Reflection to build and process all requests and StopWatch to collecting and recording app metrics

## Layout

### Login

#### path example: http://localhost:8080/login?action=loginForm
![App login page](https://i.ibb.co/R0xM6Ps/Screenshot-2022-07-17-034301.png)
<br>

### Home
#### path example: http://localhost:8080/product?action=list
![App home page](https://i.ibb.co/St3ZQHF/Screenshot-2022-08-08-015004.png)
<br>

### Product
#### path example: http://localhost:8080/product?action=list&id=2
![App prolist list page](https://i.ibb.co/yp6HW1q/Capture3.png)
<br>

### Not Found
#### path example: http://localhost:8080/product?action=list&id=/some_invalid_id/
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
