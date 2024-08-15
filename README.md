# Servlets (Full Stack Java Web Application)

This project is a Java/JSP web application, designed for product management.

It uses MVC architecture and was developed using the following stack:
- Java (JDK 17)
- Habernate (ORM) 
- Tomcat 9 (Server)
- PostgreSQL (Database)

## Layout

### Login

#### your_server/view/login?action=loginForm
![App login page](https://i.ibb.co/R0xM6Ps/Screenshot-2022-07-17-034301.png)
<br>

### Home
#### your_server/view/login?action=list
![App home page](https://i.ibb.co/LQmByN2/temp.png)
<br>

### Product
#### your_server/view/login?action=list&id=2
![App prolist list page](https://i.ibb.co/1fy8JtG/Screenshot.png)
<br>

### User
#### your_server/view/login?action=list
![App prolist list page](https://i.ibb.co/nBbGMtG/temp.png)
<br>

### 404
#### your_server/view/login?action=list&id=some_invalid_id
![App not found page](https://i.postimg.cc/Sx8D8GZP/Screenshot-2024-08-10-174059.png)
<br>

## Packages
```
├───main
│   ├───java
│   │   └───com
│   │       └───dev
│   │           └───servlet
│   │               ├───builders
│   │               ├───business
│   │               │   └───base
│   │               ├───controllers
│   │               ├───dao
│   │               ├───domain
│   │               │   └───enums
│   │               ├───dto
│   │               ├───filter
│   │               ├───interfaces
│   │               ├───mapper
│   │               ├───providers
│   │               └───utils
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

## Features in progress or to be implemented
- [x] Implement pagination
- [ ] Export/Import data to CSV
- [ ] Add filters
- [ ] Dockerize the application
- [ ] Jasper Reports

## Questions, suggestions, problems or improvements
Please contact me at `marcelo@tuta.io`.
