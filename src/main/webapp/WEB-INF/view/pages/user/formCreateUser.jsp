<%@ include file="/WEB-INF/routes/user-routes.jspf" %>
<%@ include file="/WEB-INF/routes/auth-routes.jspf" %>

<html lang="en">
<%@ include file="/WEB-INF/fragments/head-loginform.jspf" %>
<body>
<div class="grid-page">
    <div class="content">
        <div class="main">
            <div class="container-narrow">
                <div class="login-container">
                    <h2 class="text-center mb-4">Sign up</h2>

                    <form action="<c:out value='${baseLink}${version}${registerUser}' escapeXml='true'/>"
                          method="post" class="csrf-form grid-form">

                        <div class="form-group">
                            <label for="inputLogin" class="form-label">Email</label>
                            <input type="text" id="inputLogin" name="login"
                                   value="<c:out value='${email}' escapeXml='true'/>"
                                   class="form-control" placeholder="Email" required>
                        </div>

                        <div class="form-group">
                            <label for="inputPassword" class="form-label">Password</label>
                            <input type="password" id="inputPassword" name="password"
                                   class="form-control" placeholder="Password" required minlength="6">
                        </div>

                        <div class="form-group">
                            <label for="inputConfirmPassword" class="form-label">Confirm password</label>
                            <input type="password" id="inputConfirmPassword" name="confirmPassword"
                                   class="form-control" placeholder="Confirm" required minlength="6">
                        </div>

                        <c:if test="${not empty error or not empty info}">
                            <div class="alert ${not empty error ? 'alert-danger' : 'alert-success'}" role="alert">
                                <c:out value="${error != null ? error : info}" escapeXml="true"/>
                            </div>
                        </c:if>

                        <button type="submit" class="btn btn-primary btn-block">Sign up</button>

                        <div class="text-center mt-3">
                            <button type="button" class="btn btn-link"
                                    onclick="window.location.href='<c:out value='${baseLink}${version}${loginPage}'
                                                                          escapeXml='true'/>'">
                                Already have an account? Login
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>