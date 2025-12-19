const CsrfUtil = (function() {
    const CSRF_COOKIE_NAME = 'XSRF-TOKEN';
    const CSRF_HEADER_NAME = 'X-XSRF-TOKEN';

    const getToken = () => {
        const match = document.cookie.match(new RegExp('(^| )' + CSRF_COOKIE_NAME + '=([^;]+)'));
        return match ? decodeURIComponent(match[2]) : null;
    }

    const injectTokenAndSubmit = (form) => {
        const token = getToken();
        if (token) {
            form.insertAdjacentHTML(
                'beforeend',
                `<input type="hidden" name="${CSRF_HEADER_NAME}" value="${token}">`
            );
        }
        form.submit();
    }

    function attachToForms() {
        document.addEventListener('DOMContentLoaded', function() {
            const regex = /^csrf.*form$/i;

            document.querySelectorAll('form').forEach(function (form) {
                const matches = Array.from(form.classList)
                    .some(className => regex.test(className));

                if (!matches) return;

                form.addEventListener('submit', function (e) {
                    e.preventDefault();

                    if (this.classList.contains('csrf-delete-form')) {
                        if (!confirm('Are you sure you want to delete this item?')) {
                            return;
                        }
                    }

                    if (this.classList.contains('csrf-upload-form')) {
                        if (!confirm('Are you sure you want to upload this file?')) {
                            return;
                        }
                    }

                    injectTokenAndSubmit(this);
                });
            });
        });
    }

    attachToForms();

    return {};
})();

window.CsrfUtil = CsrfUtil;