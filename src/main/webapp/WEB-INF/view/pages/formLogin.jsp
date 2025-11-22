<%@ page import="com.dev.servlet.core.response.IHttpResponse" %>
<!DOCTYPE html>

<%
    request.setAttribute("error", ((IHttpResponse<?>) request.getAttribute("response")).error());
%>

<html lang="en">
<%@ include file="/WEB-INF/fragments/head-loginform.jspf" %>
<body>
<div class="login-page">
    <div class="login-container">
        <!-- Header -->
        <div class="login-header">
            <div class="login-logo">
                <i class="bi bi-shield-lock-fill"></i>
            </div>
            <h1>Welcome Back</h1>
            <p class="login-subtitle">Sign in to your account</p>
        </div>

        <!-- Body -->
        <div class="login-body">
            <c:if test="${not empty error or not empty info}">
                <div class="alert-login ${not empty error ? 'alert-danger' : 'alert-success'}">
                    <i class="bi ${not empty error ? 'bi-exclamation-circle-fill' : 'bi-check-circle-fill'}"></i>
                    <span><c:out value="${error != null ? error : info}"/></span>
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

        <!-- Footer -->
        <div class="login-footer">
            <p>Don't have an account?
                <a href="${baseLink}${version}${registerPage}">Sign up here</a>
            </p>
        </div>
    </div>
</div>

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
</body>
</html>