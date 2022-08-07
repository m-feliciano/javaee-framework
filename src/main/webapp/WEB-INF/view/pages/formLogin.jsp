<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="common-imports.jsp"/>
<!------ Include the above in your HEAD tag ---------->

<c:url value="/product" var="loginPage"/>
<c:url value="/user?action=NewUser" var="registerPage"/>

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
            <form action="${ loginPage }?action=Login" method="post">
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
                    <a type="button" href="${ registerPage }" class="btn btn-blue">Register</a>
                </div>
            </form>
        </div>
    </div>
</div>