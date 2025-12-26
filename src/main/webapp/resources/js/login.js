(function() {
    const pwdInput = document.getElementById('inputPassword');
    if (pwdInput) {
        const toggle = document.createElement('button');
        toggle.type = 'button';
        toggle.className = 'btn btn-icon btn-password-toggle';
        toggle.style.marginLeft = '8px';
        toggle.style.color = '#000';
        toggle.style.backgroundColor = '#e0e0e0';
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