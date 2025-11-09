const CsrfUtil = (function() {
    const CSRF_COOKIE_NAME = 'XSRF-TOKEN';
    const CSRF_HEADER_NAME = 'X-XSRF-TOKEN';

    const getToken = () => {
        const match = document.cookie.match(new RegExp('(^| )' + CSRF_COOKIE_NAME + '=([^;]+)'));
        return match ? decodeURIComponent(match[2]) : null;
    }

    const addTokenToHeaders = (headers = {}) => {
        const token = getToken();
        if (token) {
            headers[CSRF_HEADER_NAME] = token;
        }
        return headers;
    }

    function injectTokenAndSubmit(form) {
        const token = getToken();
        if (!token) {
            console.warn('CSRF token not found. Please refresh the page.');
            return;
        }

        let tokenInput = form.querySelector('input[name="' + CSRF_HEADER_NAME + '"]');
        if (!tokenInput) {
            tokenInput = document.createElement('input');
            tokenInput.type = 'hidden';
            tokenInput.name = CSRF_HEADER_NAME;
            form.appendChild(tokenInput);
        }
        tokenInput.value = token;

        form.submit();
    }

    function attachToForms() {
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('.csrf-form, .csrf-delete-form').forEach(form => {
                form.addEventListener('submit', function(e) {
                    e.preventDefault();

                    if (this.classList.contains('csrf-delete-form')) {
                        if (!confirm('Are you sure you want to delete this item?')) {
                            return;
                        }
                    }

                    injectTokenAndSubmit(this);
                });
            });
        });
    }

    function csrfFetch(url, options = {}) {
        const method = (options.method || 'GET').toUpperCase();
        if (['POST', 'PUT', 'DELETE'].includes(method)) {
            options.headers = addTokenToHeaders(options.headers || {});
        }
        return fetch(url, options);
    }

    attachToForms();

    return {
        getToken,
        addTokenToHeaders,
        fetch: csrfFetch
    };
})();

window.CsrfUtil = CsrfUtil;

