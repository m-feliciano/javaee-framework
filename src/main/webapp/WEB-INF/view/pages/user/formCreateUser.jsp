<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common-imports.jsp"/>
<!------ Include the above in your HEAD tag ---------->

<c:url value="/user" var="path"/>
<c:url value="/product" var="loginPage"/>

<div class="sidenav">
    <div class="login-main-text">
        <h2>
            Servlet<br> Register Page
        </h2>
        <p>Register from here to access.</p>
    </div>
</div>
<div class="main">
    <div class="col-md-6 col-sm-12">
        <div class="login-form">
            <form action="${ path }?action=CreateUser" method="post">
                <div class="form-group">
                    <label for="inputEmail">Email</label> <input type="email"
                                                                 id="inputEmail" name="email"
                                                                 class="form-control hidden-alert"
                                                                 placeholder="Email" required>
                </div>
                <div class="form-group">
                    <label for="inputPassword">Password</label> <input type="password"
                                                                       id="inputPassword" name="password"
                                                                       class="form-control hidden-alert"
                                                                       placeholder="Password" required
<%--                                                                       pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"--%>
                                                                       minlength="3">
                </div>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger hidden-alert" role="alert">
                        <c:out value="${error}"/>
                    </div>
                </c:if>
                <div>
                    <a type="button" href="${ loginPage }"  class="btn btn-black">Login</a>
                    <button type="submit" class="btn btn-secondary">Register</button>
                </div>
            </form>
        </div>
    </div>
</div>