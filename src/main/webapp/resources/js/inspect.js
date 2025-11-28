(function () {
    function initInspect() {
        const toggles = document.querySelectorAll('.toggle-methods');
        toggles.forEach(btn => {
            btn.addEventListener('click', function () {
                const ctrl = this.closest('.controller');
                if (!ctrl) return;
                const methods = ctrl.querySelector('.methods');
                if (!methods) return;
                methods.style.display = methods.style.display === 'none' ? '' : 'none';
            });
        });

        const methodsList = document.querySelectorAll('#controllers .methods');
        for (let i = 0; i < methodsList.length; i++) {
            if (i > 0) methodsList[i].style.display = 'none';
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initInspect);
    } else {
        initInspect();
    }
})();
