# Servlets (Full Stack Java Web Application)

This project is a Java/JSP web application, designed for product management.

It uses MVC architecture and was developed using the following stack:
- Java (JDK 17)
- Habernate (ORM) 
- Tomcat 9 (Server)
- PostgreSQL (Database)

## About developing this project
I'm not a JSP developer, actually I work with Java/Nodejs Angular, but I can say that I learned a lot from this project.

Any questions, suggestions, problem deploying the application, please contact me at `marcelo@tuta.io`.

## Layout

### Login

#### server/loginView?action=loginForm
![App login page](https://i.ibb.co/R0xM6Ps/Screenshot-2022-07-17-034301.png)
<br>

### Home
#### server/loginView?action=list
![App home page](https://i.ibb.co/LQmByN2/temp.png)
<br>

### Product
#### server/loginView?action=list&id=2
![App prolist list page](https://i.ibb.co/1fy8JtG/Screenshot.png)
<br>

### User
#### server/loginView?action=list
![App prolist list page](https://i.ibb.co/nBbGMtG/temp.png)
<br>

### 404
#### server/loginView?action=list&id=some_invalid_id
![App not found page](https://i.postimg.cc/Sx8D8GZP/Screenshot-2024-08-10-174059.png)
<br>

## Packages
```
E:.
├───main
│   ├───java
│   │   └───com
│   │       └───dev
│   │           └───servlet
│   │               ├───builders
│   │               ├───controllers
│   │               ├───dao
│   │               ├───domain
│   │               │   └───enums
│   │               ├───dto
│   │               ├───filter
│   │               ├───interfaces
│   │               ├───mapper
│   │               ├───providers
│   │               ├───utils
│   │               └───view
│   │                   └───base
│   ├───resources
│   │   └───META-INF
│   │       └───sql
│   └───webapp
│       ├───assets
│       ├───css
│       ├───META-INF
│       ├───web
│       │   └───WEB-INF
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
        └───servlets
            └───utils
```

## Database example
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
## create table

##etc
```

The scripts to create the database are in the `resources/META-INF/sql` folder

The database connection is set in the `resources/META-INF/persistence.xml` file
