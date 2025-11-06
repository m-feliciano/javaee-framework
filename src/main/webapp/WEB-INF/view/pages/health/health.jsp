<%@ page import="com.dev.servlet.core.response.IHttpResponse" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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

<style>
    .health-card {
        background: white;
        border-radius: 10px;
        padding: 25px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        margin-bottom: 20px;
    }

    .status-badge {
        display: inline-block;
        padding: 8px 20px;
        border-radius: 20px;
        font-weight: bold;
        font-size: 14px;
        text-transform: uppercase;
        letter-spacing: 1px;
    }

    .status-up {
        background: #d4edda;
        color: #155724;
        border: 2px solid #c3e6cb;
    }

    .status-down {
        background: #f8d7da;
        color: #721c24;
        border: 2px solid #f5c6cb;
    }

    .component-status {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 15px;
        margin: 10px 0;
        border-radius: 8px;
        background: #f8f9fa;
        border-left: 4px solid #6c757d;
    }

    .component-up {
        border-left-color: #28a745;
        background: #d4edda;
    }

    .component-down {
        border-left-color: #dc3545;
        background: #f8d7da;
    }

    .metric-box {
        text-align: center;
        padding: 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 10px;
        margin: 10px 0;
    }

    .metric-value {
        font-size: 32px;
        font-weight: bold;
        margin: 10px 0;
    }

    .metric-label {
        font-size: 14px;
        opacity: 0.9;
        text-transform: uppercase;
        letter-spacing: 1px;
    }

    .info-row {
        display: flex;
        justify-content: space-between;
        padding: 12px 0;
        border-bottom: 1px solid #e9ecef;
    }

    .info-row:last-child {
        border-bottom: none;
    }

    .info-label {
        font-weight: 600;
        color: #495057;
    }

    .info-value {
        color: #6c757d;
    }

    .progress-custom {
        height: 25px;
        border-radius: 8px;
        background: #e9ecef;
        overflow: hidden;
        position: relative;
    }

    .progress-bar-custom {
        height: 100%;
        background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: bold;
        font-size: 12px;
        transition: width 0.3s ease;
    }

    .progress-bar-warning {
        background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
    }

    .progress-bar-danger {
        background: linear-gradient(90deg, #fa709a 0%, #fee140 100%);
    }

    .page-header {
        margin-bottom: 30px;
    }

    .page-title {
        font-size: 28px;
        font-weight: 600;
        color: #212529;
        margin-bottom: 10px;
    }

    .page-subtitle {
        color: #6c757d;
        font-size: 14px;
    }

    .timestamp {
        font-size: 12px;
        color: #6c757d;
        font-style: italic;
    }
</style>

<div class="main">
    <div class="page-header">
        <h1 class="page-title">
            <i class="bi bi-heart-pulse"></i> System Health Status
        </h1>
        <p class="page-subtitle">Real-time monitoring of application health and performance metrics</p>
    </div>

    <!-- Main Status Card -->
    <div class="health-card">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <h3 class="mb-2">Overall Status</h3>
                <c:choose>
                    <c:when test="${isUp}">
                        <span class="status-badge status-up">
                            <i class="bi bi-check-circle"></i> HEALTHY
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge status-down">
                            <i class="bi bi-exclamation-triangle"></i> UNHEALTHY
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="text-end">
                <div class="timestamp">
                    <i class="bi bi-clock"></i>
                    Last check: <fmt:formatDate value="<%= new java.util.Date((Long)health.get(\"timestamp\")) %>"
                                               pattern="dd/MM/yyyy HH:mm:ss"/>
                </div>
            </div>
        </div>
    </div>

    <!-- System Information -->
    <div class="row">
        <div class="col-md-6">
            <div class="health-card">
                <h4 class="mb-3"><i class="bi bi-info-circle"></i> System Information</h4>

                <c:if test="${not empty health.version}">
                    <div class="info-row">
                        <span class="info-label">Version:</span>
                        <span class="info-value"><c:out value="${health.version}" escapeXml="true"/></span>
                    </div>
                </c:if>

                <c:if test="${not empty health.service}">
                    <div class="info-row">
                        <span class="info-label">Service:</span>
                        <span class="info-value"><c:out value="${health.service}" escapeXml="true"/></span>
                    </div>
                </c:if>

                <c:if test="${not empty health.environment}">
                    <div class="info-row">
                        <span class="info-label">Environment:</span>
                        <span class="info-value">
                            <span class="badge ${health.environment eq 'production' ? 'badge-danger' : 'badge-warning'}">
                                <c:out value="${health.environment}" escapeXml="true"/>
                            </span>
                        </span>
                    </div>
                </c:if>

                <c:if test="${not empty uptimeFormatted}">
                    <div class="info-row">
                        <span class="info-label">Uptime:</span>
                        <span class="info-value"><c:out value="${uptimeFormatted}" escapeXml="true"/></span>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Components Status -->
        <div class="col-md-6">
            <div class="health-card">
                <h4 class="mb-3"><i class="bi bi-puzzle"></i> Components</h4>

                <c:if test="${not empty components}">
                    <%
                        @SuppressWarnings("unchecked")
                        Map<String, String> components = (Map<String, String>) request.getAttribute("components");
                        for (Map.Entry<String, String> entry : components.entrySet()) {
                            request.setAttribute("componentName", entry.getKey());
                            request.setAttribute("componentStatus", entry.getValue());
                    %>
                        <div class="component-status ${componentStatus eq 'UP' ? 'component-up' : 'component-down'}">
                            <span style="font-weight: 600; text-transform: capitalize;">
                                <i class="bi ${componentStatus eq 'UP' ? 'bi-check-circle' : 'bi-x-circle'}"></i>
                                <c:out value="${componentName}" escapeXml="true"/>
                            </span>
                            <span class="badge ${componentStatus eq 'UP' ? 'badge-success' : 'badge-danger'}">
                                <c:out value="${componentStatus}" escapeXml="true"/>
                            </span>
                        </div>
                    <%
                        }
                    %>
                </c:if>

                <c:if test="${empty components}">
                    <p class="text-muted">No component information available</p>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Memory Metrics (if available) -->
    <c:if test="${not empty memory}">
        <%
            @SuppressWarnings("unchecked")
            Map<String, Object> memory = (Map<String, Object>) request.getAttribute("memory");
            request.setAttribute("usedMb", memory.get("used_mb"));
            request.setAttribute("maxMb", memory.get("max_mb"));
            request.setAttribute("usagePercent", memory.get("usage_percent"));
        %>

        <div class="health-card">
            <h4 class="mb-3"><i class="bi bi-memory"></i> Memory Usage</h4>

            <div class="row">
                <div class="col-md-4">
                    <div class="metric-box">
                        <div class="metric-label">Used Memory</div>
                        <div class="metric-value"><c:out value="${usedMb}"/> MB</div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="metric-box" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                        <div class="metric-label">Max Memory</div>
                        <div class="metric-value"><c:out value="${maxMb}"/> MB</div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="metric-box" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                        <div class="metric-label">Usage</div>
                        <div class="metric-value"><c:out value="${usagePercent}"/>%</div>
                    </div>
                </div>
            </div>

            <div class="mt-4">
                <div class="progress-custom">
                    <div class="progress-bar-custom ${usagePercent > 90 ? 'progress-bar-danger' : usagePercent > 70 ? 'progress-bar-warning' : ''}"
                         style="width: ${usagePercent}%">
                        <c:out value="${usagePercent}"/>%
                    </div>
                </div>
                <small class="text-muted mt-2 d-block">
                    <c:choose>
                        <c:when test="${usagePercent > 90}">
                            <i class="bi bi-exclamation-triangle text-danger"></i> Critical: Memory usage is very high
                        </c:when>
                        <c:when test="${usagePercent > 70}">
                            <i class="bi bi-exclamation-circle text-warning"></i> Warning: Memory usage is elevated
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-check-circle text-success"></i> Normal: Memory usage is healthy
                        </c:otherwise>
                    </c:choose>
                </small>
            </div>
        </div>
    </c:if>

    <!-- API Endpoints Info -->
    <div class="health-card">
        <h4 class="mb-3"><i class="bi bi-link-45deg"></i> Health Check Endpoints</h4>

        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>Endpoint</th>
                        <th>Purpose</th>
                        <th>Kubernetes Use</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><code>/api/v1/health/check</code></td>
                        <td>Overall health status</td>
                        <td><span class="badge badge-secondary">-</span></td>
                        <td>
                            <a href="${baseLink}${version}/health/check" class="btn btn-sm btn-primary">
                                <i class="bi bi-box-arrow-up-right"></i> Test
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td><code>/api/v1/health/ready</code></td>
                        <td>Readiness probe</td>
                        <td><span class="badge badge-info">readinessProbe</span></td>
                        <td>
                            <a href="${baseLink}${version}/health/ready" class="btn btn-sm btn-primary">
                                <i class="bi bi-box-arrow-up-right"></i> Test
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td><code>/api/v1/health/live</code></td>
                        <td>Liveness probe</td>
                        <td><span class="badge badge-success">livenessProbe</span></td>
                        <td>
                            <a href="${baseLink}${version}/health/live" class="btn btn-sm btn-primary">
                                <i class="bi bi-box-arrow-up-right"></i> Test
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    setInterval(function () {
        location.reload();
    }, 2000);
</script>

