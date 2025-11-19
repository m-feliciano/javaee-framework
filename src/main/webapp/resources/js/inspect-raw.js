(function () {
    'use strict';

    // show toast
    function showToast(message, type = 'success') {
        const toastEl = document.getElementById('toast');
        if (!toastEl) return;
        toastEl.textContent = message;
        toastEl.className = 'toast ' + (type === 'success' ? 'success' : 'warn');
        toastEl.style.display = 'block';
        setTimeout(() => {
            toastEl.style.display = 'none';
        }, 2000);
    }

    // copy button
    const copyBtn = document.getElementById('copyBtn');
    if (copyBtn) {
        copyBtn.addEventListener('click', function () {
            const toCopy = prettyStr;
            if (!toCopy) return showToast('No JSON to copy', 'warn');
            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(toCopy).then(function () {
                    showToast('JSON copied to clipboard!', 'success');
                }, function (err) {
                    showToast('Failed to copy text', 'warn');
                });
            } else {
                // fallback
                try {
                    const textarea = document.createElement('textarea');
                    textarea.value = toCopy;
                    document.body.appendChild(textarea);
                    textarea.select();
                    document.execCommand('copy');
                    document.body.removeChild(textarea);
                    showToast('JSON copied to clipboard!', 'success');
                } catch (e) {
                    showToast('Copy not supported', 'warn');
                }
            }
        });
    }

    // download link
    const downloadLink = document.getElementById('downloadLink');
    if (downloadLink) {
        downloadLink.addEventListener('click', function () {
            const raw = prettyStr || rawText || '';
            const blob = new Blob([raw], {type: 'application/json'});
            const url = URL.createObjectURL(blob);
            this.href = url;
        });
    }
})();

