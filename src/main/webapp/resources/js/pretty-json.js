(function () {
    const rawList = document.getElementsByClassName('raw-json');
    if (!rawList) return;

    function escapeHtml(str) {
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }

    function jsonToHtmlLines(jsonStr) {
        try {
            const obj = JSON.parse(jsonStr);
            const pretty = JSON.stringify(obj, null, 2);
            const lines = pretty.split('\n');
            return lines.map(line => {
                let out = escapeHtml(line)
                    .replace(/(\s*)"([^"\\]+)":/g, '$1<span class="json-key">"$2"</span>:')
                    .replace(/: ("[^"\\]*")/g, ': <span class="json-string">$1</span>')
                    .replace(/: (\btrue\b|\bfalse\b)/g, ': <span class="json-boolean">$1</span>')
                    .replace(/: (null)/g, ': <span class="json-null">$1</span>')
                    .replace(/: ([-]?\d+[\d\.eE+-]*)/g, ': <span class="json-number">$1</span>');
                return '<span class="line">' + out + '</span>';
            }).join('');
        } catch (e) {
            return '<span class="line">' + escapeHtml(jsonStr) + '</span>';
        }
    }

    for (let pre of rawList) {
        const rawText = pre.textContent || pre.innerText || '';

        let prettyStr = rawText;
        let prettyHtml = '';
        try {
            const obj = JSON.parse(rawText);
            prettyStr = JSON.stringify(obj, null, 2);
            prettyHtml = jsonToHtmlLines(prettyStr);
        } catch (e) {
            prettyHtml = '<span class="line">' + escapeHtml(rawText) + '</span>';
        }

        pre.innerHTML = prettyHtml;

        try {
            if (window.hljs && typeof hljs.highlightElement === 'function') {
                try {
                    hljs.highlightElement(pre);
                } catch (e) {
                }
            }
        } catch (e) {
        }
    }

    function resizeRawContainer() {
        const container = document.getElementById('raw-container');
        if (!container) return;

        const actionBar = document.querySelector('.action-bar');
        const actionBarBottom = actionBar ? actionBar.getBoundingClientRect().bottom : 0;
        const bottomPadding = 32;

        const available = Math.max(200, Math.floor(window.innerHeight - actionBarBottom - bottomPadding));
        container.style.height = available + 'px';

        for (let pre of rawList) {
            pre.style.height = '100%';
            pre.style.maxHeight = '100%';
            pre.style.overflow = 'auto';

            const obs = new MutationObserver(() => resizeRawContainer());
            obs.observe(pre, {childList: true, subtree: true});
        }
    }

    resizeRawContainer();
    window.addEventListener('resize', resizeRawContainer);
})();