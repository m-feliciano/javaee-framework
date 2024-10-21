### Application Overview

This project is a full Java/JSP web application designed for managing products.
It follows the Model-View-Controller (MVC) architecture,
ensuring a clear separation of concerns and facilitating maintainability and scalability.

### Technology Stack
- **Java (JDK 17)**: The core programming language.
- **Hibernate (ORM)**: An Object-Relational Mapping framework that simplifies database interactions.
- **Tomcat 9 (Server)**: A web server and servlet container used to deploy and run the application.
- **PostgreSQL (Database)**: An open-source relational database management system.*
- **Criteria API**: A type-safe way to build queries for the database decoupling the queries from the underlying database.

*The application can be configured to work with other databases like MySQL, Oracle, etc.

## URL Design
- `{context}/view/{service}/{action}/{id|query}`

Example:
- `http://server/view/product/list/1`
- `http://server/view/product/list?page=1&page_size=5`

## Layout

### Login
#### `/view/login/form`
![App login page](https://i.ibb.co/R0xM6Ps/Screenshot-2022-07-17-034301.png)

### Home Page
#### `/view/product/list?page=<page>&page_size=<size>&sort=<field>&order=<asc|desc>`
Default values: `page=1`, `page_size=5`, `sort=id`, `order=desc` (can be changed in `app.properties`)

![App home page](https://i.ibb.co/fFT7p2N/shopping-prod.png)

### Product
#### `/view/product/list/<id>`
![App product list page](https://i.ibb.co/1fy8JtG/Screenshot.png)

### User
#### `/view/user/list/<id>`
![App user list page](https://i.ibb.co/nBbGMtG/temp.png)

### Error page (in progress)
#### `/view/product/list/<invalid_id>`
![App not found page](https://i.postimg.cc/Sx8D8GZP/Screenshot-2024-08-10-174059.png)

## Packages
```
├───main
│   ├───java
│   │   └───com
│   │       └───dev
│   │           └───servlet
│   │               ├───builders
│   │               ├───business        (services)
│   │               │   └───base 
│   │               ├───controllers
│   │               ├───dao             (infra)
│   │               ├───dto             (data transfer objects)
│   │               ├───filter          (servlet filters)
│   │               ├───interfaces      (contracts)
│   │               ├───listeners 
│   │               ├───mapper          (object mapping/transfers)
│   │               ├───pojo            (plain old java objects)
│   │               │   ├───enums
│   │               │   └───records     (Immutable objects)
│   │               ├───providers       (dependency injection, sevice locator etc.)
│   │               ├───transform
│   │               └───utils
│   ├───resources
│   │   └───META-INF
│   │       └───sql                     (database scripts, default data, etc.)
│   └───webapp
│       ├───assets                      (images, fonts, etc.)
│       ├───css                         (stylesheets)
│       ├───META-INF
│       ├───web
│       │   └───WEB-INF
│       └───WEB-INF
│           ├───jspf                    (JSP fragments)
│           └───view                    
│               ├───components          (JSP components like header, footer, etc.)
│               └───pages               (JSP pages)
│                   ├───category
│                   ├───inventory
│                   ├───product
│                   └───user
└───test
    └───java
        └───servlets
            └───utils

```

## Database Example
```docker
## create network
docker network create -d bridge <network-name>

## run container (example)
docker run --name <container-name> \
--network=<network-name> -p 5432:5432 \
-e "POSTGRES_USER=<user>" \
-e "POSTGRES_PASSWORD=<password>" \
-d postgres

## exec into container
docker exec -it <container-name> psql -U postgres
## create table

## etc
```

The scripts to create the database are in the `resources/META-INF/sql` folder.

The database connection is set in the `resources/META-INF/persistence.xml` file.

## Features in Progress
- [x] Implement pagination
- [x] Export/Import data to CSV
- [ ] Add filters
- [ ] Dockerize the application
- [ ] Jasper Reports

## Questions, Suggestions, Problems, or Improvements
Please contact me at `marcelo@tuta.io`.