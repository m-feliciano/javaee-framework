<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="tag" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/routes/health-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    IHttpResponse<?> httpResponse = (IHttpResponse<?>) request.getAttribute("response");
    @SuppressWarnings("unchecked")
    Map<String, Object> health = (Map<String, Object>) httpResponse.body();
    request.setAttribute("health", health);

    String status = (String) health.get("status");
    request.setAttribute("status", status);
    request.setAttribute("isUp", "UP".equals(status));

    if (health.containsKey("components")) {
        request.setAttribute("components", health.get("components"));
    }

    if (health.containsKey("memory")) {
        request.setAttribute("memory", health.get("memory"));
    }

    if (health.containsKey("uptime")) {
        long uptime = (Long) health.get("uptime");
        long uptimeSeconds = uptime / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        request.setAttribute("uptimeFormatted", String.format("%dh %dm %ds", hours, minutes, seconds));
    }
%>

<title>System Health</title>

<div class="content">
    <div class="main">
        <div class="action-bar">
            <div class="action-bar-title">
                <h1><i class="bi bi-heart-pulse-fill"></i> System Health</h1>
                <p class="action-bar-subtitle">Real-time monitoring of application health and performance</p>
            </div>
            <div class="action-buttons">
                <button onclick="location.reload()" class="btn btn-secondary">
                    <i class="bi bi-arrow-clockwise"></i> Refresh
                </button>
            </div>
        </div>

        <div class="health-status-card">
            <h3 class="mb-3"><i class="bi bi-activity"></i> Overall Status</h3>
            <c:choose>
                <c:when test="${isUp}">
                    <span class="health-badge-large badge-success">
                        <i class="bi bi-check-circle-fill"></i> HEALTHY
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="health-badge-large badge-danger">
                        <i class="bi bi-exclamation-triangle-fill"></i> UNHEALTHY
                    </span>
                </c:otherwise>
            </c:choose>
            <p class="health-timestamp text-muted">
                <i class="bi bi-clock"></i>
                Last check: <fmt:formatDate value="<%= new java.util.Date((Long)health.get(\"timestamp\")) %>" pattern="dd/MM/yyyy HH:mm:ss"/>
            </p>
        </div>

        <div class="health-stats-grid">
            <div class="health-stat-card info">
                <div class="health-stat-header">
                    <span class="health-stat-title">
                        <i class="bi bi-info-circle-fill"></i> System Info
                    </span>
                    <div class="health-stat-icon">
                        <i class="bi bi-cpu"></i>
                    </div>
                </div>
                <div class="health-stat-body">
                    <c:if test="${not empty health.version}">
                        <div class="health-info-row">
                            <span class="health-info-label">Version:</span>
                            <span class="health-info-value"><c:out value="${health.version}"/></span>
                        </div>
                    </c:if>
                    <c:if test="${not empty health.service}">
                        <div class="health-info-row">
                            <span class="health-info-label">Service:</span>
                            <span class="health-info-value"><c:out value="${health.service}"/></span>
                        </div>
                    </c:if>
                    <c:if test="${not empty health.environment}">
                        <div class="health-info-row">
                            <span class="health-info-label">Environment:</span>
                            <span class="badge ${health.environment eq 'production' ? 'badge-danger' : 'badge-warning'}">
                                <c:out value="${health.environment}"/>
                            </span>
                        </div>
                    </c:if>
                    <c:if test="${not empty uptimeFormatted}">
                        <div class="health-info-row">
                            <span class="health-info-label">Uptime:</span>
                            <span class="health-info-value" style="color: var(--success); font-weight: 600;">
                                <c:out value="${uptimeFormatted}"/>
                            </span>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="health-stat-card ${isUp ? 'success' : 'danger'}">
                <div class="health-stat-header">
                    <span class="health-stat-title">
                        <i class="bi bi-puzzle-fill"></i> Components
                    </span>
                    <div class="health-stat-icon">
                        <i class="bi bi-puzzle"></i>
                    </div>
                </div>
                <div class="health-stat-body">
                    <c:if test="${not empty components}">
                        <%
                            @SuppressWarnings("unchecked")
                            Map<String, String> components = (Map<String, String>) request.getAttribute("components");
                            for (Map.Entry<String, String> entry : components.entrySet()) {
                                request.setAttribute("componentName", entry.getKey());
                                request.setAttribute("componentStatus", entry.getValue());
                        %>
                            <div class="health-info-row">
                                <span class="health-component-status">
                                    <i class="bi ${componentStatus eq 'UP' ? 'bi-check-circle-fill' : 'bi-x-circle-fill'} health-component-icon"
                                       style="color: ${componentStatus eq 'UP' ? 'var(--success)' : 'var(--danger)'}"></i>
                                    <span class="health-component-name"><c:out value="${componentName}"/></span>
                                </span>
                                <span class="badge ${componentStatus eq 'UP' ? 'badge-success' : 'badge-danger'}">
                                    <c:out value="${componentStatus}"/>
                                </span>
                            </div>
                        <%
                            }
                        %>
                    </c:if>
                    <c:if test="${empty components}">
                        <p class="text-muted mb-0">No component information available</p>
                    </c:if>
                </div>
            </div>
        </div>

        <c:if test="${not empty memory}">
            <%
                @SuppressWarnings("unchecked")
                Map<String, Object> memory = (Map<String, Object>) request.getAttribute("memory");
                request.setAttribute("usedMb", memory.get("used_mb"));
                request.setAttribute("maxMb", memory.get("max_mb"));
                request.setAttribute("usagePercent", memory.get("usage_percent"));
            %>

            <div class="card mb-4">
                <div class="card-header">
                    <h3><i class="bi bi-memory"></i> Memory Usage</h3>
                </div>
                <div class="card-body">
                    <div class="health-stats-grid" style="margin-bottom: var(--spacing-5);">
                        <div class="health-stat-card info">
                            <div class="health-stat-title">Used Memory</div>
                            <div class="health-stat-value"><c:out value="${usedMb}"/> MB</div>
                        </div>

                        <div class="health-stat-card warning">
                            <div class="health-stat-title">Max Memory</div>
                            <div class="health-stat-value"><c:out value="${maxMb}"/> MB</div>
                        </div>

                        <div class="health-stat-card ${usagePercent > 90 ? 'danger' : usagePercent > 70 ? 'warning' : 'success'}">
                            <div class="health-stat-title">Usage</div>
                            <div class="health-stat-value"><c:out value="${usagePercent}"/>%</div>
                        </div>
                    </div>

                    <div class="health-memory-progress">
                        <div class="health-progress-bar">
                            <div class="health-progress-fill ${usagePercent > 90 ? 'danger' : usagePercent > 70 ? 'warning' : 'success'}"
                                 style="width: ${usagePercent}%">
                                ${usagePercent}%
                            </div>
                        </div>

                        <div class="mt-4">
                            <c:choose>
                                <c:when test="${usagePercent > 90}">
                                    <div class="alert alert-danger">
                                        <i class="bi bi-exclamation-triangle-fill"></i>
                                        <strong>Critical:</strong> Memory usage is very high
                                    </div>
                                </c:when>
                                <c:when test="${usagePercent > 70}">
                                    <div class="alert alert-warning">
                                        <i class="bi bi-exclamation-circle-fill"></i>
                                        <strong>Warning:</strong> Memory usage is elevated
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-success">
                                        <i class="bi bi-check-circle-fill"></i>
                                        <strong>Normal:</strong> Memory usage is healthy
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <div class="card">
            <div class="card-header">
                <h3><i class="bi bi-link-45deg"></i> Health Check Endpoints</h3>
            </div>
            <div class="table-responsive">
                <table class="health-endpoints-table table table-hover mb-0">
                    <thead class="thead-dark">
                        <tr>
                            <th>Endpoint</th>
                            <th>Purpose</th>
                            <th>Kubernetes Use</th>
                            <th style="width: 100px;">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><code>/api/v1/health/check</code></td>
                            <td>Overall health status</td>
                            <td><span class="badge badge-secondary">General</span></td>
                            <td>
                                <a href="${baseLink}${version}/health/check" target="_blank" class="btn btn-sm btn-primary">
                                    <i class="bi bi-box-arrow-up-right"></i> Test
                                </a>
                            </td>
                        </tr>
                        <tr>
                            <td><code>/api/v1/health/ready</code></td>
                            <td>Readiness probe</td>
                            <td><span class="badge badge-info">readinessProbe</span></td>
                            <td>
                                <a href="${baseLink}${version}/health/ready" target="_blank" class="btn btn-sm btn-primary">
                                    <i class="bi bi-box-arrow-up-right"></i> Test
                                </a>
                            </td>
                        </tr>
                        <tr>
                            <td><code>/api/v1/health/live</code></td>
                            <td>Liveness probe</td>
                            <td><span class="badge badge-success">livenessProbe</span></td>
                            <td>
                                <a href="${baseLink}${version}/health/live" target="_blank" class="btn btn-sm btn-primary">
                                    <i class="bi bi-box-arrow-up-right"></i> Test
                                </a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<c:set var="healthJsUrl"><tag:assetPath name="health.js"/></c:set>
<script src="${healthJsUrl}" defer></script>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>
