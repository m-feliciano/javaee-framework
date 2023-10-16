<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common-imports.jsp"/>
<!------ Include the above in your HEAD tag ---------->

<c:url value="/userView" var="path"/>
<c:url value="/loginView?action=loginForm" var="loginPage"/>

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
            <form action="${ path }?action=create" method="post">
                <div class="form-group">
                    <label for="inputEmail">Email</label> <input type="email"
                                                                 value="${ email }"
                                                                 id="inputEmail" name="email"
                                                                 class="form-control hidden-alert"
                                                                 placeholder="Email" required>
                </div>
             <div class="form-group">
                    <label for="inputPassword">Password</label> <input type="password"
                                                                       id="inputPassword" name="password"
                                                                       class="form-control hidden-alert"
                                                                       placeholder="Password" required
                                                                       readonly="readonly" disabled="disabled"
<%--                                                                       pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"--%>
                                                                       minlength="3">
                </div>
                <div class="form-group">
                    <label for="inputConfirmPassword">Confirm password</label> <input type="password"
                                                                       id="inputConfirmPassword" name="confirmPassword"
                                                                       class="form-control hidden-alert"
                                                                       placeholder="Confirm" required
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
                    <span class="ml-1"></span>
                    <button type="submit" class="btn btn-blue">Register</button>
                </div>
            </form>
        </div>
    </div>
</div>