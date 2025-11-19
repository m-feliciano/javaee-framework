(function () {
    const search = document.getElementById('inspectSearch');
    const controllers = Array.from(document.querySelectorAll('#controllers .controller'));
    const normalize = (text) => (text || '').toString().toLowerCase();

    if (search) {
        search.addEventListener('input', function () {
            const q = normalize(this.value);
            controllers.forEach(ctrl => {
                const text = normalize(ctrl.innerText);
                ctrl.style.display = text.indexOf(q) !== -1 ? '' : 'none';
            });
        });
    }

    document.querySelectorAll('.toggle-methods').forEach(btn => {
        btn.addEventListener('click', function () {
            const ctrl = this.closest('.controller');
            const methods = ctrl.querySelector('.methods');
            if (!methods) return;
            methods.style.display = methods.style.display === 'none' ? '' : 'none';
        });
    });

    document.querySelectorAll('#controllers .methods')?.forEach((methods, index) => {
        if (index > 0) methods.style.display = 'none';
    });
})();