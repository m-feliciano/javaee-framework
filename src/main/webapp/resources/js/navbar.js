(function () {
    'use strict';

    const navbar = (() => {
        let alerts = new Map();
        let pollInterval = null;
        let pollMs = 6000;
        let pollTimeout = 300;
        let inited = false;

        const userName = (document.querySelector('.navbar-user .user-name')?.textContent || 'guest').trim();
        const alertsKey = `app_alerts_${userName}`;

        const cryptoRandomId = () => Math.random().toString(36).slice(2) + Date.now().toString(36);
        const decodeBase64 = (b64) => JSON.parse(decodeURIComponent(escape(atob(b64))));
        const encodeBase64 = (data) => btoa(unescape(encodeURIComponent(JSON.stringify(data))));

        function loadLocal() {
            const raw = localStorage.getItem(alertsKey);
            if (!raw) {
                localStorage.setItem(alertsKey, encodeBase64([]));
                return;
            }

            let list = null;
            try {
                const d = decodeBase64(raw);
                if (Array.isArray(d)) {
                    list = d;
                }
            } catch {
            }

            if (!list)
                try {
                    const j = JSON.parse(raw);
                    if (Array.isArray(j)) {
                        list = j;
                    }
                } catch {
                }
            list = list || [];
            list.forEach(a => {
                const id = a.id || cryptoRandomId();
                alerts.set(id, {...a, id, read: !!a.read});
            });
        }

        const persistLocal = () => localStorage.setItem(alertsKey, encodeBase64(alerts.values().toArray()));

        function renderAll() {
            const menuInner = document.querySelector('#notifications-menu .notifications-menu-inner');
            if (!menuInner) return;
            menuInner.innerHTML = '';

            const sorted = [...alerts.values()].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
            if (!sorted.length) {
                menuInner.innerHTML = '<div class="notifications-empty">No notifications</div>';
            } else {
                sorted.forEach(renderNotificationItem);
            }

            updateBadge();
        }

        function renderNotificationItem(alert) {
            const menuInner = document.querySelector('#notifications-menu .notifications-menu-inner');
            if (!menuInner) return;

            const item = document.createElement('div');
            item.className = `notification-item ${alert.read ? '' : 'unread'}`;
            item.dataset.notifId = alert.id;

            const icon = document.createElement('div');
            icon.className = `item-icon ${alert.read ? 'read' : 'unread'}`;
            icon.innerHTML = '<i class="bi bi-bell" style="color: darkred"></i>';

            const body = document.createElement('div');
            body.className = 'item-body';
            body.innerHTML =
                `<div class="item-title">${alert.message || alert.event || 'Notification'}</div>` +
                `<div class="item-ts">${new Date(alert.createdAt).toLocaleString()}</div>`;

            item.append(icon, body);

            item.addEventListener('click', () => {
                if (!alert.read) {
                    alert.read = true;
                    item.classList.remove('unread');
                    persistLocal();
                    updateBadge();
                }
            });

            menuInner.prepend(item);
        }

        function updateBadge() {
            const badge = document.querySelector('#notification-root .notif-badge');
            if (!badge) return;

            const unread = [...alerts.values()].filter(a => !a.read).length;
            if (unread > 0) {
                badge.style.display = 'inline-flex';
                badge.textContent = unread > 9 ? '9+' : String(unread);
            } else {
                badge.style.display = 'none';
            }
        }

        function pushAlerts(list) {
            if (!list.length) return;
            list.forEach(a => {
                const id = a.id || (a.id = cryptoRandomId());
                const old = alerts.get(id);
                alerts.set(id, old ? {...a, read: old.read} : {...a, read: false});
            });
            persistLocal();
            renderAll();
        }

        function clearLocal() {
            alerts.clear();
            persistLocal();
            renderAll();
        }

        function clearRemote() {
            fetch(`${location.origin}/api/v1/alert/clear`, {
                credentials: 'same-origin',
                method: 'POST'
            }).catch(() => {
            });
        }

        function pollOnce() {
            fetch(`${location.origin}/api/v1/alert/list`, {credentials: 'same-origin'})
                .then(r => r.json())
                .then(list => {
                    if (!Array.isArray(list)) return;
                    const newOnes = list.filter(a => !alerts.has(a.id));
                    pushAlerts(newOnes);
                }).catch(() => {
            });
        }

        function startPolling(ms) {
            stopPolling();
            pollMs = ms || pollMs;
            pollInterval = setInterval(pollOnce, pollMs);
            setTimeout(pollOnce, pollTimeout);
        }

        function stopPolling() {
            if (pollInterval) {
                clearInterval(pollInterval);
                pollInterval = null;
            }
        }

        function initNavbarUI() {
            const toggler = document.querySelector('.navbar-toggler');
            const navbarNav = document.querySelector('.navbar-nav');

            if (toggler && navbarNav) {
                toggler.addEventListener('click', () => navbarNav.classList.toggle('show'));
                document.addEventListener('click', ev => {
                    const nav = document.querySelector('.navbar');
                    if (nav && !nav.contains(ev.target) && navbarNav.classList.contains('show'))
                        navbarNav.classList.remove('show');
                });
            }

            const currentPath = window.location.pathname;
            document.querySelectorAll('.nav-link').forEach(link => {
                const href = link.getAttribute('href');
                if (href && currentPath.includes(href))
                    link.classList.add('active');
            });

            const notifRoot = document.getElementById('notification-root');
            const notifToggle = notifRoot?.querySelector('.notification-toggle');
            const notifMenu = document.getElementById('notifications-menu');

            if (notifToggle && notifMenu) {
                notifToggle.addEventListener('click', e => {
                    e.preventDefault();
                    notifMenu.classList.toggle('show');
                    notifToggle.setAttribute('aria-expanded', notifMenu.classList.contains('show'));
                });

                document.addEventListener('click', ev => {
                    if (notifRoot && !notifRoot.contains(ev.target) && notifMenu.classList.contains('show')) {
                        notifMenu.classList.remove('show');
                        notifToggle.setAttribute('aria-expanded', 'false');
                    }
                });
            }
        }

        function bindNotificationButtons() {
            const markAll = document.getElementById('mark-all-read-btn');
            const clearBtn = document.getElementById('clear-notif-btn');

            markAll?.addEventListener('click', () => {
                alerts.forEach(a => a.read = true);
                persistLocal();
                renderAll();
            });

            clearBtn?.addEventListener('click', () => {
                clearLocal();
                clearRemote();
            });
        }

        function init(options) {
            if (inited) return;
            inited = true;
            loadLocal();
            renderAll();
            initNavbarUI();
            bindNotificationButtons();
            startPolling(options?.pollMs);
        }

        return {init};
    })();

    document.addEventListener('DOMContentLoaded', () => navbar.init());
})();
