<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tag" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/routes/category-routes.jspf" %>
<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<%@ include file="/WEB-INF/routes/product-routes.jspf" %>
<%@ include file="/WEB-INF/routes/user-routes.jspf" %>
<%@ include file="/WEB-INF/routes/auth-routes.jspf" %>
<%@ include file="/WEB-INF/routes/health-routes.jspf" %>
<%@ include file="/WEB-INF/routes/inspect-routes.jspf" %>

<nav class="navbar">
    <div class="navbar-container">
        <a class="navbar-brand" href="${baseLink}${version}${listProduct}">
            <img src="<c:url value='/resources/assets/logo.svg'/>" alt="Logo">
        </a>

        <button class="navbar-toggler" type="button" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" href="${baseLink}${version}${listProduct}">
                    <i class="bi bi-box-seam"></i>
                    <span>Products</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${baseLink}${version}${listCategory}">
                    <i class="bi bi-tag"></i>
                    <span>Categories</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${baseLink}${version}${listInventory}">
                    <i class="bi bi-building"></i>
                    <span>Inventory</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${baseLink}${version}/activity/history">
                    <i class="bi bi-clock-history"></i>
                    <span>History</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${baseLink}${version}${healthCheck}">
                    <i class="bi bi-heart-pulse"></i>
                    <span>Health</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${baseLink}${version}${inspectPage}">
                    <i class="bi bi-eye"></i>
                    <span>Docs</span>
                </a>
            </li>
            <li class="nav-item dropdown" id="notification-root">
                <a class="nav-link notification-toggle" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                    <i class="bi bi-bell" style="color: darkred"></i>
                    <span class="notif-badge" style="display:none"></span>
                </a>
                <div class="dropdown-menu notifications-menu" id="notifications-menu" aria-label="Notifications">
                    <div class="notifications-menu-inner">
                        <div class="notifications-empty">No notifications</div>
                    </div>
                    <div class="dropdown-divider"></div>
                    <div style="padding:8px; display:flex; gap:8px; justify-content:space-between;">
                        <button class="dropdown-item" id="mark-all-read-btn" type="button">Mark all read</button>
                        <button class="dropdown-item" id="clear-notif-btn" type="button">Clear</button>
                    </div>
                </div>
            </li>

            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button">
                    <div class="navbar-user">
                        <div class="user-avatar" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold;">
                            US
                        </div>
                        <div class="user-info">
                            <span class="user-name">${user.login.substring(0, user.login.indexOf('@'))}</span>
                            <span class="user-role">Administrador</span>
                        </div>
                    </div>
                </a>
                <div class="dropdown-menu">
                    <a class="dropdown-item" href="<c:url value='${baseLink}${version}${listUser}/${user.id}'/>" >
                        <i class="bi bi-person-circle"></i>
                        <span>My Profile</span>
                    </a>
                    <a class="dropdown-item" href="#">
                        <i class="bi bi-gear"></i>
                        <span>Settings</span>
                    </a>
                    <div class="dropdown-divider"></div>
                    <form action="${baseLink}${version}${logout}" method="post" class="csrf-form">
                        <button type="submit" class="dropdown-item">
                            <i class="bi bi-box-arrow-right"></i>
                            <span>Logout</span>
                        </button>
                    </form>
                </div>
            </li>
        </ul>
    </div>
</nav>
<c:set var="navbarUrl"><tag:assetPath name="navbar.js"/></c:set>
<script src="${navbarUrl}" defer></script>