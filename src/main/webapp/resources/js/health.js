// Health Page - Auto-refresh and Animations

(function() {
    'use strict';

    const AUTO_REFRESH_INTERVAL = 30000; // 30 segundos
    let autoRefreshTimer = null;
    let autoRefreshEnabled = false;

    // Inicialização
    function init() {
        setupAutoRefresh();
        animateStatCards();
        animateProgressBar();
        setupRefreshButton();
    }

    // Configurar auto-refresh
    function setupAutoRefresh() {
        console.log('Health monitoring initialized');
    }

    // Ativar/Desativar auto-refresh
    function toggleAutoRefresh() {
        if (autoRefreshEnabled) {
            stopAutoRefresh();
        } else {
            startAutoRefresh();
        }
    }

    function startAutoRefresh() {
        autoRefreshEnabled = true;
        autoRefreshTimer = setInterval(() => {
            console.log('Auto-refreshing health status...');
            location.reload();
        }, AUTO_REFRESH_INTERVAL);
        showNotification('Auto-refresh enabled (30s)', 'success');
    }

    function stopAutoRefresh() {
        autoRefreshEnabled = false;
        if (autoRefreshTimer) {
            clearInterval(autoRefreshTimer);
            autoRefreshTimer = null;
        }
        showNotification('Auto-refresh disabled', 'info');
    }

    // Animar stat cards
    function animateStatCards() {
        const statCards = document.querySelectorAll('.health-stat-card');
        statCards.forEach((card, index) => {
            setTimeout(() => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';

                requestAnimationFrame(() => {
                    card.style.transition = 'all 0.5s ease-out';
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                });
            }, index * 100);
        });
    }

    // Animar progress bar
    function animateProgressBar() {
        const progressFill = document.querySelector('.health-progress-fill');
        if (progressFill) {
            const targetWidth = progressFill.style.width;
            progressFill.style.width = '0%';

            setTimeout(() => {
                progressFill.style.width = targetWidth;
            }, 500);
        }
    }

    // Configurar botão de refresh
    function setupRefreshButton() {
        const refreshButton = document.querySelector('[onclick*="location.reload"]');
        if (refreshButton) {
            // Remover onclick inline
            refreshButton.removeAttribute('onclick');

            // Adicionar listener
            refreshButton.addEventListener('click', function(e) {
                e.preventDefault();

                // Adicionar animação de loading
                const icon = this.querySelector('i');
                if (icon) {
                    icon.classList.add('bi-arrow-clockwise');
                    icon.style.animation = 'spin 1s linear infinite';
                }

                // Refresh após pequeno delay
                setTimeout(() => {
                    location.reload();
                }, 300);
            });

            // Double-click para ativar auto-refresh
            refreshButton.addEventListener('dblclick', function(e) {
                e.preventDefault();
                toggleAutoRefresh();
            });
        }
    }

    // Mostrar notificação
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 80px;
            right: 20px;
            z-index: 9999;
            padding: 12px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            animation: slideInRight 0.3s ease-out;
        `;
        notification.innerHTML = `<i class="bi bi-info-circle"></i> ${message}`;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.3s ease-out';
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 3000);
    }

    // Adicionar CSS para animações
    const style = document.createElement('style');
    style.textContent = `
        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
        
        @keyframes slideInRight {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        @keyframes slideOutRight {
            from {
                transform: translateX(0);
                opacity: 1;
            }
            to {
                transform: translateX(100%);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);

    // Inicializar quando DOM estiver pronto
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    // Cleanup ao sair da página
    window.addEventListener('beforeunload', () => {
        if (autoRefreshTimer) {
            clearInterval(autoRefreshTimer);
        }
    });

})();


