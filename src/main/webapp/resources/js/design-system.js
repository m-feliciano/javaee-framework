/**
 * Sistema de Design Empresarial - Utilitários JavaScript
 * Funções auxiliares para componentes interativos
 */

(function(window, document) {
    'use strict';

    /**
     * Namespace principal
     */
    const DesignSystem = {

        /**
         * Inicializa todos os componentes
         */
        init: function() {
            this.initNavbar();
            this.initDropdowns();
            this.initModals();
            this.initToasts();
            this.initFormValidation();
            this.initTooltips();
        },

        /**
         * Navbar - Mobile toggle e active links
         */
        initNavbar: function() {
            const toggler = document.querySelector('.navbar-toggler');
            const navbarNav = document.querySelector('.navbar-nav');

            if (toggler && navbarNav) {
                toggler.addEventListener('click', function(e) {
                    e.stopPropagation();
                    navbarNav.classList.toggle('show');

                    // Anima o ícone do hamburguer
                    const icon = this.querySelector('.navbar-toggler-icon');
                    icon.style.transform = navbarNav.classList.contains('show')
                        ? 'rotate(90deg)'
                        : 'rotate(0deg)';
                });

                // Fecha o menu ao clicar fora
                document.addEventListener('click', function(e) {
                    const navbar = document.querySelector('.navbar');
                    if (!navbar.contains(e.target) && navbarNav.classList.contains('show')) {
                        navbarNav.classList.remove('show');
                        const icon = toggler.querySelector('.navbar-toggler-icon');
                        icon.style.transform = 'rotate(0deg)';
                    }
                });
            }

            // Marca o link ativo baseado na URL atual
            this.highlightActiveLink();
        },

        /**
         * Destaca o link ativo na navegação
         */
        highlightActiveLink: function() {
            const currentPath = window.location.pathname;
            const navLinks = document.querySelectorAll('.nav-link');

            navLinks.forEach(link => {
                const href = link.getAttribute('href');
                if (href && currentPath.includes(href) && href !== '#') {
                    link.classList.add('active');
                } else if (link.classList.contains('active') && !currentPath.includes(href)) {
                    link.classList.remove('active');
                }
            });
        },

        /**
         * Dropdowns
         */
        initDropdowns: function() {
            const dropdowns = document.querySelectorAll('.dropdown');

            dropdowns.forEach(dropdown => {
                const toggle = dropdown.querySelector('.dropdown-toggle');
                const menu = dropdown.querySelector('.dropdown-menu');

                if (toggle && menu) {
                    toggle.addEventListener('click', function(e) {
                        e.preventDefault();
                        e.stopPropagation();

                        // Fecha outros dropdowns abertos
                        document.querySelectorAll('.dropdown-menu.show').forEach(m => {
                            if (m !== menu) m.classList.remove('show');
                        });

                        menu.classList.toggle('show');
                    });
                }
            });

            // Fecha dropdown ao clicar fora
            document.addEventListener('click', function(e) {
                if (!e.target.closest('.dropdown')) {
                    document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                        menu.classList.remove('show');
                    });
                }
            });
        },

        /**
         * Modals
         */
        initModals: function() {
            // Abre modal
            document.querySelectorAll('[data-modal-target]').forEach(trigger => {
                trigger.addEventListener('click', function(e) {
                    e.preventDefault();
                    const modalId = this.getAttribute('data-modal-target');
                    const modal = document.getElementById(modalId);
                    if (modal) {
                        DesignSystem.openModal(modal);
                    }
                });
            });

            // Fecha modal
            document.querySelectorAll('.modal-close, [data-modal-close]').forEach(closer => {
                closer.addEventListener('click', function() {
                    const modal = this.closest('.modal');
                    if (modal) {
                        DesignSystem.closeModal(modal);
                    }
                });
            });

            // Fecha ao clicar no backdrop
            document.querySelectorAll('.modal-backdrop').forEach(backdrop => {
                backdrop.addEventListener('click', function() {
                    const modal = this.nextElementSibling;
                    if (modal && modal.classList.contains('modal')) {
                        DesignSystem.closeModal(modal);
                    }
                });
            });

            // Fecha com ESC
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') {
                    const openModal = document.querySelector('.modal.show');
                    if (openModal) {
                        DesignSystem.closeModal(openModal);
                    }
                }
            });
        },

        openModal: function(modal) {
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
        },

        closeModal: function(modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
        },

        /**
         * Toast notifications
         */
        initToasts: function() {
            this.toastContainer = this.createToastContainer();
        },

        createToastContainer: function() {
            let container = document.getElementById('toast-container');
            if (!container) {
                container = document.createElement('div');
                container.id = 'toast-container';
                container.style.cssText = `
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    z-index: 9999;
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                `;
                document.body.appendChild(container);
            }
            return container;
        },

        showToast: function(message, type = 'info', duration = 3000) {
            const toast = document.createElement('div');
            toast.className = `alert alert-${type}`;
            toast.style.cssText = `
                min-width: 300px;
                animation: slideInRight 0.3s ease-out;
                box-shadow: var(--shadow-lg);
            `;

            const icons = {
                success: 'bi-check-circle-fill',
                danger: 'bi-x-circle-fill',
                warning: 'bi-exclamation-triangle-fill',
                info: 'bi-info-circle-fill'
            };

            toast.innerHTML = `
                <i class="bi ${icons[type] || icons.info}"></i>
                <span>${message}</span>
            `;

            this.toastContainer.appendChild(toast);

            // Remove após duração
            setTimeout(() => {
                toast.style.animation = 'slideOutRight 0.3s ease-out';
                setTimeout(() => toast.remove(), 300);
            }, duration);
        },

        /**
         * Validação de formulários
         */
        initFormValidation: function() {
            const forms = document.querySelectorAll('form[data-validate]');

            forms.forEach(form => {
                form.addEventListener('submit', function(e) {
                    if (!DesignSystem.validateForm(this)) {
                        e.preventDefault();
                    }
                });

                // Validação em tempo real
                form.querySelectorAll('input, textarea, select').forEach(field => {
                    field.addEventListener('blur', function() {
                        DesignSystem.validateField(this);
                    });

                    field.addEventListener('input', function() {
                        if (this.classList.contains('is-invalid')) {
                            DesignSystem.validateField(this);
                        }
                    });
                });
            });
        },

        validateForm: function(form) {
            let isValid = true;
            const fields = form.querySelectorAll('input[required], textarea[required], select[required]');

            fields.forEach(field => {
                if (!this.validateField(field)) {
                    isValid = false;
                }
            });

            return isValid;
        },

        validateField: function(field) {
            const value = field.value.trim();
            let isValid = true;
            let message = '';

            // Required
            if (field.hasAttribute('required') && !value) {
                isValid = false;
                message = 'Este campo é obrigatório';
            }

            // Email
            if (isValid && field.type === 'email' && value) {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(value)) {
                    isValid = false;
                    message = 'Email inválido';
                }
            }

            // Min length
            if (isValid && field.hasAttribute('minlength')) {
                const minLength = parseInt(field.getAttribute('minlength'));
                if (value.length < minLength) {
                    isValid = false;
                    message = `Mínimo de ${minLength} caracteres`;
                }
            }

            // Max length
            if (isValid && field.hasAttribute('maxlength')) {
                const maxLength = parseInt(field.getAttribute('maxlength'));
                if (value.length > maxLength) {
                    isValid = false;
                    message = `Máximo de ${maxLength} caracteres`;
                }
            }

            // Pattern
            if (isValid && field.hasAttribute('pattern') && value) {
                const pattern = new RegExp(field.getAttribute('pattern'));
                if (!pattern.test(value)) {
                    isValid = false;
                    message = field.getAttribute('data-pattern-message') || 'Formato inválido';
                }
            }

            // Atualiza UI
            if (isValid) {
                field.classList.remove('is-invalid');
                const feedback = field.parentElement.querySelector('.invalid-feedback');
                if (feedback) feedback.remove();
            } else {
                field.classList.add('is-invalid');
                let feedback = field.parentElement.querySelector('.invalid-feedback');
                if (!feedback) {
                    feedback = document.createElement('div');
                    feedback.className = 'invalid-feedback';
                    field.parentElement.appendChild(feedback);
                }
                feedback.textContent = message;
            }

            return isValid;
        },

        /**
         * Tooltips simples
         */
        initTooltips: function() {
            document.querySelectorAll('[data-tooltip]').forEach(element => {
                const tooltipText = element.getAttribute('data-tooltip');

                element.addEventListener('mouseenter', function(e) {
                    const tooltip = document.createElement('div');
                    tooltip.className = 'tooltip-content';
                    tooltip.textContent = tooltipText;
                    tooltip.style.cssText = `
                        position: absolute;
                        bottom: calc(100% + 10px);
                        left: 50%;
                        transform: translateX(-50%);
                        background-color: var(--gray-900);
                        color: var(--white);
                        padding: 0.5rem 0.75rem;
                        border-radius: var(--border-radius-md);
                        font-size: var(--font-size-xs);
                        white-space: nowrap;
                        z-index: 9999;
                        pointer-events: none;
                    `;

                    element.style.position = 'relative';
                    element.appendChild(tooltip);
                });

                element.addEventListener('mouseleave', function() {
                    const tooltip = this.querySelector('.tooltip-content');
                    if (tooltip) tooltip.remove();
                });
            });
        },

        /**
         * Loading state para botões
         */
        setButtonLoading: function(button, loading = true) {
            if (loading) {
                button.dataset.originalText = button.innerHTML;
                button.classList.add('btn-loading');
                button.disabled = true;
            } else {
                if (button.dataset.originalText) {
                    button.innerHTML = button.dataset.originalText;
                }
                button.classList.remove('btn-loading');
                button.disabled = false;
            }
        },

        /**
         * Confirm dialog
         */
        confirm: function(message, callback) {
            if (window.confirm(message)) {
                callback();
            }
        }
    };

    // Expõe para uso global
    window.DesignSystem = DesignSystem;

    // Auto-inicializa quando o DOM está pronto
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => DesignSystem.init());
    } else {
        DesignSystem.init();
    }

})(window, document);

// Adiciona estilos de animação via JavaScript
const style = document.createElement('style');
style.textContent = `
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

