# Product Management Application

This project is a full Java/JSP web application designed for managing products. It follows the Model-View-Controller (MVC) architecture, ensuring a clear separation of concerns and facilitating maintainability and scalability.

## Table of Contents
- [Technology Stack](#technology-stack)
- [URL Design](#url-design)
- [Layout](#layout)
- [Packages](#packages)
- [Setup Instructions](#setup-instructions)
- [Usage Instructions](#usage-instructions)
- [Features in Progress](#features-in-progress)

## Technology Stack
- **Java (JDK 17)**: The core programming language.
- **Hibernate (ORM)**: An Object-Relational Mapping framework that simplifies database interactions.
- **Tomcat 9 (Server)**: A web server and servlet container used to deploy and run the application.
- **PostgreSQL (Database)**: An open-source relational database management system.
- **Criteria API**: A type-safe way to build queries for the database decoupling the queries from the underlying database.

## URL Design
- `{context}/view/{service}/{action}/{id|query}`

Example:
- `server/view/product/list/1`

## Layout

### Login
#### `/login/form`
![App login page](https://i.ibb.co/R0xM6Ps/Screenshot-2022-07-17-034301.png)

### Home Page
#### `/product/list?<query>`
![App home page](https://i.ibb.co/fFT7p2N/shopping-prod.png)

#### Tips:
- *Sorting*: `sort=<field>&order=<asc|desc>&page=<page>&limit=<size>`
- *Searching*: `q=<query>&k=<field>`

Sample URLs:
- `/product/list?page=1&limit=5&sort=id&order=desc`
- `/product/list?q=macbook+pro&k=name`

Default values can be changed in the `app.properties` file.

### Product
#### `/product/list/<id>`
![App product list page](https://i.ibb.co/1fy8JtG/Screenshot.png)

### User
#### `/user/list/<id>`
![App user list page](https://i.ibb.co/nBbGMtG/temp.png)

### Error page (in progress)
#### `/product/list/<invalid_id>`
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

## Setup Instructions
1. Clone the repository:
    ```sh
    git clone https://github.com/m-feliciano/servlets.git
    ```
2. Navigate to the project directory:
    ```sh
    cd servlets
    ```
3. Build the project using Maven:
    ```sh
    mvn clean install
    ```
4. Create a new database in PostgreSQL:
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
    # The scripts to create the database are in the `resources/META-INF/sql` folder.
    # The database connection is set in the `resources/META-INF/persistence.xml` file.
    ```

5. Deploy the application to Tomcat:
    - Install Tomcat 9 on your machine.
    - Copy the generated WAR file to the Tomcat `webapps` directory.
    - Start the Tomcat server.

## Usage Instructions
- Access the application at `http://localhost:8080/<context-path>`
- Use the provided URLs to navigate through the application.

## Features in Progress
- [x] Implement pagination
- [x] Export/Import data to CSV
- [ ] Decouple backend from frontend
- [ ] Dockerize the application
- [ ] Jasper Reports

[Back to top](#product-management-application)