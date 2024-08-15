<%@ include file="../components/common-imports.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="ISO-8859-1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css"
          integrity="sha384-zCbKRCUGaJDkqS1kPbPd7TveP5iyJE0EjAuZQTgFLD2ylzuqKfdKlfG/eSrtxUkn"
          crossorigin="anonymous"/>
    <link rel="stylesheet" href="<c:url value='/css/login.css'/>">
    <title>Shopping</title>
</head>
<body>
<div class="sidenav">
    <div class="login-main-text">
        <h2>
            Servlet<br> Login Page
        </h2>
        <p>Login from here to access.</p>
    </div>
</div>
<div class="main">
    <div class="col-md-6 col-sm-12">
        <div class="login-form">
            <form action="${ loginLink }?action=login" method="post">
                <div class="form-group">
                    <label for="inputEmail">Email</label> <input type="email"
                                                                 id="inputEmail" name="email"
                                                                 value="${ email }"
                                                                 class="form-control hidden-alert"
                                                                 placeholder="Email" required>
                </div>
                <div class="form-group">
                    <label for="inputPassword">Password</label> <input type="password"
                                                                       id="inputPassword" name="password"
                                                                       class="form-control hidden-alert"
                                                                       placeholder="Password" required
                                                                       minlength="3">
                </div>
                <c:if test="${not empty invalid}">
                    <div class="alert alert-danger hidden-alert" role="alert">
                        <c:out value="${invalid}"/>
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success hidden-alert" role="alert">
                        <c:out value="${success}"/>
                    </div>
                </c:if>
                <div>
                    <button type="submit" class="btn btn-black">Login</button>
                    <span class="ml-1"></span>
                    <a type="button" href="${ loginLink }" class="btn btn-blue">Register</a>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>