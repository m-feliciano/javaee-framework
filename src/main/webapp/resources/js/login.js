(function() {
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
})();
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
