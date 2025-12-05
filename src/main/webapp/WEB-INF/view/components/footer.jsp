<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tag" tagdir="/WEB-INF/tags" %>

<footer class="footer">
    <div class="footer-content">
        <div class="footer-bottom">
            <div style="display: flex; justify-content: center; align-items: center; gap: var(--spacing-6); flex-wrap: wrap;">
                <a href="https://github.com/m-feliciano" target="_blank" rel="noopener noreferrer"
                   style="display: inline-flex; align-items: center; gap: var(--spacing-2); color: var(--gray-400); text-decoration: none; transition: color var(--transition-fast);"
                   onmouseover="this.style.color='var(--primary-400)'"
                   onmouseout="this.style.color='var(--gray-400)'">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                        <path d="M12 0C5.374 0 0 5.373 0 12c0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23A11.509 11.509 0 0112 5.803c1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576C20.566 21.797 24 17.3 24 12c0-6.627-5.373-12-12-12z"></path>
                    </svg>
                    <span style="font-size: var(--font-size-sm); font-weight: var(--font-weight-medium);">@m-feliciano</span>
                </a>

                <c:if test="${not empty systemVersion}">
                    <span style="color: var(--gray-500); font-size: var(--font-size-sm);">
                        <i class="bi bi-code-square" style="margin-right: var(--spacing-1);"></i>
                        Version <c:out value="${systemVersion}"/>
                    </span>
                </c:if>
            </div>
        </div>
        <!-- demo mode banner -->
        <c:if test="${demoMode}">
            <div class="footer-demo-mode-banner" style="margin-top: var(--spacing-6);
                padding: var(--spacing-4);
                background-color: var(--yellow-100);
                border: 1px solid var(--yellow-300);
                border-radius: var(--border-radius-md);
                color: var(--yellow-800);
                text-align: center;
                font-size: var(--font-size-sm);">
                <i class="bi bi-exclamation-triangle-fill" style="margin-right: var(--spacing-2);"></i>
                You are currently using the application in <strong>Demo Mode</strong>. Some features may be limited or disabled.
            </div>
        </c:if>
    </div>
</footer>

<c:set var="mainJsUrl"><tag:assetPath name="main.js"/></c:set>
<script src="${mainJsUrl}" defer></script>
</body>
</html>