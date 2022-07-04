# servlets
Web Servlet Application (Jakarta Servlet) for product control using MVC pattern, Slf4j for Logger and Java Reflection

## Layout

### Login

#### url example: http://localhost:8080/servlets/product?action=Login
![App login page](https://i.ibb.co/Hg39yDS/Capture2.png)
<br>

### Home
#### url example: http://localhost:8080/servlets/product?action=ListProducts
![App home page](https://i.ibb.co/0ZsvDjp/Capture.png)
<br>

### Product
#### url example: http://localhost:8080/servlets/product?action=ListProducts&id=2
![App prolist list page](https://i.ibb.co/yp6HW1q/Capture3.png)
<br>

### Not Found
#### url example: http://localhost:8080/servlets/product?action=ListProducts&id=<some_invalid_id>
![App not found page](https://i.ibb.co/th8R564/Capture5.png)
<br>

## Logging
![Logging with slf4j](https://i.ibb.co/DLLzvSw/Capture.png)
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
│   │   ├───filter
│   │   ├───infra
│   │   │   └───exceptions
│   │   ├───servlets
│   │   │   ├───category
│   │   │   ├───inventory
│   │   │   └───product
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
│                   └───product
└───test
    └───java
```