# Full-Stack Java Web Application

This project is a comprehensive Java/JSP web application.
It follows the Model-View-Controller (MVC) architecture and uses the Java EE stack.
I've used the latest Java features and best practices to build this application.

## Table of Contents

- [Technology Stack](#tech-stack)
- [URL Design](#url-design)
- [Layout](#Some-layouts)
- [Packages](#packages)
- [Setup Instructions](#setup-instructions)
- [Notes](#notes)

## Tech Stack

- **Java (JDK 17)**: Core programming language.
- **Hibernate (ORM)**: Simplifies database interactions.
- **Tomcat 9 (Server)**: Web server and servlet container.
- **PostgreSQL (Database)**: Open-source relational database management system.
- **Criteria API**: Type-safe way to build database queries.

## URL Design
### URL Components:
- **`{context}`**: Application context path (e.g., `https://your-domain.com/api`).
- **`{version}`**: API version (e.g., `v1`).
- **`{path}`**: Controller path (e.g., `product`).
- **`{service}`**: Specific service or action (e.g., `list`).
- **`{query}`**: Optional query parameters (e.g., `?page=1&limit=5`).

### Examples:

#### GET Requests:
- `/api/v1/product/list` - List all products.
- `/api/v1/product/list/{id}` - Get product by ID.

#### POST Requests:
- `/api/v1/product/update/{id}` - Update a product.
- `/api/v1/product/delete/{id}` - Delete a product.

### Query Parameters:
- **Sorting**: `sort=<field>&order=<asc|desc>` (e.g., `sort=id&order=asc`).
- **Pagination**: `page=<page>&limit=<size>` (e.g., `page=1&limit=5`).
- **Search**: `q=<query>&k=<field>` (e.g., `q=macbook&k=name`).

### Notes:
- Default values for query parameters can be configured in the `app.properties` file.
- API versioning is included in the URL but not mapped to controllers. The default version is `v1`.

#### Example of controller

```java

// Example of a controller
@Controller(path = "/product")
public final class ProductController extends BaseController<Product, Long> {

    @RequestMapping(value = "/list")
    public IServletResponse list(Request request) {
        ProductModel model = this.getModel();

        request.query().getPageable().setRecords(model.findAll(request));
   
        Set<KeyPair> response = new HashSet<>();
        if (!CollectionUtils.isEmpty(request.query().getPageable().getRecords())) {
   
            Collection<ProductDTO> products = model.getAllPageable(request.query().getPageable())
                        .stream()
                        .map(ProductMapper::base)
                        .toList();
      
            BigDecimal totalPrice = model.calculateTotalPrice(request.query().getPageable().getRecords());
   
            response.add(KeyPair.of("products", products));
            response.add(KeyPair.of("totalPrice", totalPrice));
        }

        Collection<CategoryDTO> categories = getCategoryModel().getAllFromCache(request.token());
        response.add(KeyPair.of("categories", categories));

        return super.newServletResponse(response, super.forwardTo("listProducts"));
    }
}
```

#### Endpoint register user

```java
// POST ap1/v2/user/registerUser
@RequestMapping(
        value = "/registerUser",
        method = RequestMethod.POST,
        apiVersion = "v2",
        requestAuth = false,
        validators = {
                @Validator(values = "login", constraints = {
                        @Constraints(isEmail = true, message = "Login must be a valid email")
                }),
                @Validator(values = {"password", "confirmPassword"},
                        constraints = {
//                                @Constraints(minLength = 5, maxLength = 30, message = "Password must be between {0} and {1} characters")
                                @Constraints(minLength = 5, message = "Password must have at least {0} characters"),
                                @Constraints(maxLength = 30, message = "Password must have at most {0} characters"),
                        }),
        })
public IHttpResponse<Void> register(Request request) throws ServiceException {
    this.getModel().register(request);
    // Created
    return super.newHttpResponse(201, null, "redirect:/api/v1/login/form");
}
```

#### Endpoint delete user (Only admin)

```java
   // POST /user/delete/{id}
@RequestMapping(
        value = "/delete/{id}",
        method = RequestMethod.POST,
        roles = { // Only admin can delete
                PerfilEnum.ADMIN
        },
        validators = {
                @Validator(values = "id", constraints = {
                        @Constraints(min = 1, message = "ID must be greater than or equal to {0}")
                })
        })
public IHttpResponse<Void> delete(Request request) throws ServiceException {
    this.getModel().delete(request);

    return HttpResponse.ofNext(super.forwardTo("formLogin"));
}
```

#### Superclass methods

```java Superclass methods
   protected <U> IHttpResponse<U> newHttpResponse(int status, U response, String nextPath) {
        return HttpResponse.<U>newBuilder().statusCode(status).body(response).next(nextPath).build();
    }

   protected <U> IHttpResponse<U> okHttpResponse(U response, String nextPath) {
       // Uses newHttpResponse
   }
   
   // Response container
   protected IServletResponse newServletResponse(Set<KeyPair> response, String next) {
       return new IServletResponse() {
           @Override
           public int statusCode() {
               return 200;
           }
   
           @Override
           public Set<KeyPair> body() {
               return response;
           }
   
           @Override
           public String next() {
               return next;
           }
       };
   }
```

## Some layouts

### Home Page

#### `/product/?page=1&limit=3&sort=id&order=asc`

![App home page](./images/homepage.png)

Default values can be changed in the `app.properties` file.

### Product

#### `/product/list/{id}`

![App product list page](./images/product-list.png)

### Info Page

[comment]: <> (Found on the web, author unknown)
![Error](./images/cat_404.gif)

## Packages

```
├───main
│   ├───java
│   │   └───com
│   │       └───dev
│   │           └───servlet
│   │               ├───builders
│   │               ├───controllers    (Controllers)
│   │               │   └───router 
│   │               ├───dao            (Data Access Object)
│   │               ├───dto            (Data Transfer Object)
│   │               ├───filter         (Servlet Filter)
│   │               │   └───wrappers   (Request and Response Wrappers)
│   │               ├───interfaces     (Contracts)
│   │               ├───listeners 
│   │               ├───mapper         (Object Mapper)
│   │               ├───model          (Model)
│   │               │   └───shared 
│   │               ├───pojo           (Plain Old Java Object)
│   │               │   ├───domain     (Domain Classes)
│   │               │   ├───enums
│   │               │   └───records    (Immutable Data Classes)
│   │               ├───providers      (Services and Providers)
│   │               └───utils          (Utility Classes)
│   ├───resources
│   │   └───META-INF
│   │       └───sql                    (SQL Scripts)
│   └───webapp
│       ├───assets
│       │   └───images
│       ├───css
│       ├───js
│       ├───META-INF
│       ├───web
│       │   └───WEB-INF
│       └───WEB-INF
│           ├───fragments              (JSP Fragments)
│           ├───routes                 (JSP Routes)
│           └───view
│               ├───components         (Reusable Components)
│               │   └───buttons
│               └───pages              (JSP Pages)
│                   ├───category
│                   ├───inventory
│                   ├───product
│                   └───user
└───test
    └───java
        └───servlets
            └───auth
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

5. Setting up the database:
    - Run the scripts in the `resources/META-INF/sql` folder to create the tables and insert initial data.
    - Update the `persistence.xml` file with your database credentials.
    - Update the `app.properties` file as needed.
      <br><br>
6. Deploy the application to Tomcat:
    - Install Tomcat 9 on your machine.
    - Copy the generated WAR file to the Tomcat `webapps` directory.
    - Start the Tomcat server.
      <br><br>
7. Usage Instructions
    - Access the application at `<server>/<context-path>` (e.g., `http://localhost:8080/api/v1/login/form`).

## Notes

***Note***: This project was initially created years ago to learn Java EE, core Servlet/JSP, and JPA. It has been
updated to incorporate the latest Java features and best practices.

There is a lot of room for improvement,
like refactoring the frontend joining the files into a single one using `JSP fragments`,
and `JSTL` to render the content dynamically.

[Back to top](#full-stack-java-web-application)