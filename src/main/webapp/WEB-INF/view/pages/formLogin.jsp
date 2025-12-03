<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<%@ include file="/WEB-INF/fragments/head-loginform.jspf" %>

<%
    request.setAttribute("error", ((IHttpResponse<?>) request.getAttribute("response")).error());
    request.setAttribute("user", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<!DOCTYPE html>
<html lang="en">
<body>
<div class="login-page">
    <div class="login-container">
        <div class="login-header">
            <div class="login-logo">
                <i class="bi bi-shield-lock-fill"></i>
            </div>
            <h1>Welcome Back</h1>
            <p class="login-subtitle">Sign in to your account</p>
        </div>

        <div class="login-body">
            <c:if test="${not empty user and user.unconfirmedEmail}">
                <div class="alert-login alert-warning">
                    <i class="bi bi-envelope-exclamation-fill"></i>
                    <div class="alert-content" style="display:inline-block; margin-left:8px;">
                        Your account is not confirmed. Check your inbox for the confirmation link.
                        <form method="post" action="${baseLink}${version}/user/resend-confirmation" class="csrf-form"
                              style="display:inline;margin-left:8px;">
                            <input type="hidden" name="id" value="${user.id}"/>
                            <button type="submit" class="btn btn-link p-0">Resend confirmation email</button>
                        </form>
                    </div>
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert-login ${not empty error ? 'alert-danger' : 'alert-success'}">
                    <i class="bi ${not empty error ? 'bi-exclamation-circle-fill' : 'bi-check-circle-fill'}"></i>
                    <span><c:out value="${error}"/></span>
                </div>
            </c:if>

            <c:if test="${not empty user and user.created}">
                <div class="alert-login alert-success">
                    <i class="bi bi-check-circle-fill"></i>
                    Your account has been created successfully. Please check your email to confirm your account.
                </div>
            </c:if>

            <form action="${baseLink}${version}${login}" method="post" class="csrf-form login-form">
                <div class="form-group">
                    <label for="inputLogin" class="form-label">Email Address</label>
                    <div class="input-group">
                        <i class="bi bi-envelope input-group-icon"></i>
                        <input type="email"
                               id="inputLogin"
                               name="login"
                               class="form-control"
                               placeholder="your.email@company.com"
                               required
                               autocomplete="email">
                    </div>
                </div>

                <div class="form-group">
                    <label for="inputPassword" class="form-label">Password</label>
                    <div class="input-group">
                        <i class="bi bi-lock-fill input-group-icon"></i>
                        <input type="password"
                               id="inputPassword"
                               name="password"
                               class="form-control"
                               placeholder="Enter your password"
                               required
                               minlength="3"
                               autocomplete="current-password">
                    </div>
                </div>

                <div class="form-options">
                    <div class="form-check">
                        <input type="checkbox"
                               class="form-check-input"
                               id="rememberMe"
                               name="rememberMe">
                        <label class="form-check-label" for="rememberMe">
                            Remember me
                        </label>
                    </div>
                    <a href="#" class="forgot-password">Forgot password?</a>
                </div>

                <button type="submit" class="btn btn-primary btn-login">
                        <span class="btn-text">
                            <i class="bi bi-box-arrow-in-right"></i>
                            Sign In
                        </span>
                    <span class="btn-loading-spinner" style="display: none;">
                            <i class="bi bi-arrow-repeat"></i>
                            Signing in...
                        </span>
                </button>
            </form>
        </div>
        <div class="login-footer">
            <c:if test="${demoMode}">
                <p class="demo-mode-info">
                    <i class="bi bi-info-circle-fill"></i>
                    You are in Demo Mode. Use guest@guest / guest to log in
                </p>
            </c:if>
            <c:if test="${not demoMode}">
                <p>Don't have an account?
                    <a href="${baseLink}${version}${registerPage}">Sign up here</a>
                </p>
            </c:if>
        </div>
    </div>
</div>
</body>
<script>
    function onLoginSubmit(form) {
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            const btnText = submitBtn.querySelector('.btn-text');
            const btnLoading = submitBtn.querySelector('.btn-loading-spinner');

            if (btnText) btnText.style.display = 'none';
            if (btnLoading) btnLoading.style.display = 'inline-flex';
            submitBtn.disabled = true;
        }
        return true;
    }

    const pwdInput = document.getElementById('inputPassword');
    if (pwdInput) {
        let toggle = document.createElement('button');
        toggle.type = 'button';
        toggle.className = 'btn btn-icon btn-password-toggle';
        toggle.style.marginLeft = '8px';
        toggle.innerHTML = '<i class="bi bi-eye-fill"></i>';

        const parent = pwdInput.closest('.input-group');
        if (parent) parent.appendChild(toggle);

        toggle.addEventListener('click', function() {
            const icon = this.querySelector('i');
            if (pwdInput.type === 'password') {
                pwdInput.type = 'text';
                if (icon) {
                    icon.classList.remove('bi-eye-fill');
                    icon.classList.add('bi-eye-slash-fill');
                }
            } else {
                pwdInput.type = 'password';
                if (icon) {
                    icon.classList.remove('bi-eye-slash-fill');
                    icon.classList.add('bi-eye-fill');
                }
            }
            pwdInput.focus();
        });
    }
</script>
</html>