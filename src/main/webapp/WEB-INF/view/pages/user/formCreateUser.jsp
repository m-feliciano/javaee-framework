<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>

<!DOCTYPE html>
<html lang="en">
<%@ include file="/WEB-INF/jspf/head-loginForm.jspf" %> <!-- This is a fragment -->
<body>
<div class="register sidenav">
    <div class="register-main-text">
        <h2>
            Servlet<br> Register Page
        </h2>
        <p>Register from here to access.</p>
    </div>
</div>
<div class="main">
    <div class="col-md-6 col-sm-12">
        <div class="login-form">
            <form action="${registerUser}" method="post">
                <div class="form-group">
                    <label for="inputEmail">Email</label> <input type="email"
                                                                 value="${ email }" id="inputEmail" name="email"
                                                                 class="form-control hidden-alert" placeholder="Email"
                                                                 required>
                </div>
                <div class="form-group">
                    <label for="inputPassword">Password</label> <input type="password"
                                                                       id="inputPassword" name="password"
                                                                       class="form-control hidden-alert"
                                                                       placeholder="Password"
                                                                       required
                <%--                                                                       pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"--%>
                                                                       minlength="6">
                </div>
                <div class="form-group">
                    <label for="inputConfirmPassword">Confirm password</label> <input
                        type="password" id="inputConfirmPassword" name="confirmPassword"
                        class="form-control hidden-alert" placeholder="Confirm"
                        required
                <%--                                                                       pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"--%>
                        minlength="6">
                </div>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger hidden-alert" role="alert">
                        <c:out value="${error}"/>
                    </div>
                </c:if>
                <div>
                    <a type="button" href="${ loginPage }" class="btn btn-black">Login</a>
                    <span class="ml-1"></span>
                    <button type="submit" class="btn btn-blue">Register</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>