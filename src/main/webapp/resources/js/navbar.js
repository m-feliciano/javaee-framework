(function () {
    'use strict';
    document.addEventListener('DOMContentLoaded', function () {
        const toggler = document.querySelector('.navbar-toggler');
        const navbarNav = document.querySelector('.navbar-nav');

        if (toggler) {
            toggler.addEventListener('click', function () {
                navbarNav.classList.toggle('show');
            });
        }

        // Close mobile menu when clicking outside
        document.addEventListener('click', function (event) {
            const isClickInside = document.querySelector('.navbar').contains(event.target);
            if (!isClickInside && navbarNav.classList.contains('show')) {
                navbarNav.classList.remove('show');
            }
        });

        // Active link highlighting
        const currentPath = window.location.pathname;
        document.querySelectorAll('.nav-link').forEach(link => {
            if (link.getAttribute('href') && currentPath.includes(link.getAttribute('href'))) {
                link.classList.add('active');
            }
        });

        const clearAlerts = () => {
            fetch(`${location.origin}/api/v1/alert/clear`, {credentials: 'same-origin', method: 'POST'})
                .catch();
        }

        const poll = () => {
            fetch(`${location.origin}/api/v1/alert/list`, {credentials: 'same-origin'})
                .then(r => r.json())
                .then(data => {
                    if (Array.isArray(data)) {
                        data.forEach(a => showToast(a));
                        if (data.length) clearAlerts();
                    }
                }).catch();
        };
        setTimeout(poll, 300);
        setInterval(poll, 6000);
    });

    function showToast(data) {
        const container = document.getElementById('alerts-container');
        const el = document.createElement('div');
        el.className = 'alert-toast';
        el.textContent = data.message || (data.status || 'info');
        el.style.padding = '10px 14px';
        el.style.background = data.status === 'error' ? 'rgba(220,53,69,0.95)' : data.status === 'warning' ? 'rgba(234,178,14,0.95)' : 'rgba(40,167,69,0.95)';
        el.style.color = 'white';
        el.style.borderRadius = '6px';
        el.style.boxShadow = '0 4px 12px rgba(0,0,0,0.12)';
        el.style.fontSize = '0.95rem';
        el.style.opacity = '0';
        el.style.transform = 'translateY(-6px)';
        el.style.transition = 'all 200ms ease';

        container.prepend(el);
        requestAnimationFrame(() => {
            el.style.opacity = '1';
            el.style.transform = 'translateY(0)';
        });

        setTimeout(() => {
            el.style.opacity = '0';
            el.style.transform = 'translateY(-6px)';
            setTimeout(() => el.remove(), 300);
        }, 6000);
    }
})();