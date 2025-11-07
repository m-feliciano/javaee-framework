<%@ page import="com.dev.servlet.core.response.IHttpResponse" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>
<%@ include file="/WEB-INF/routes/history-routes.jspf" %>

<%
    IHttpResponse<?> httpResponse = (com.dev.servlet.core.response.IHttpResponse<?>) request.getAttribute("response");
    request.setAttribute("activity", httpResponse.body());
%>

<title>Activity Detail</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-tomorrow.min.css">
<style>
    .detail-card {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin-bottom: 20px;
    }

    .card-header {
        background-color: #343a40;
        color: white;
        font-weight: bold;
    }

    .card-header.bg-request {
        background-color: #17a2b8;
    }

    .card-header.bg-response {
        background-color: #28a745;
    }

    .card-header.bg-error {
        background-color: #dc3545;
    }

    .json-viewer {
        background: #2d2d2d;
        border-radius: 8px;
        padding: 15px;
        overflow-x: auto;
        max-height: 500px;
        margin: 0;
    }

    .json-viewer code {
        color: #f8f8f2;
        font-size: 0.9em;
    }

    .info-row {
        padding: 8px 0;
        border-bottom: 1px solid #eee;
    }

    .info-row:last-child {
        border-bottom: none;
    }

    .info-label {
        font-weight: bold;
        color: #555;
    }

    .badge-SUCCESS {
        background-color: #28a745;
    }

    .badge-FAILED {
        background-color: #dc3545;
    }

    .text-right {
        text-align: right !important;
    }

    .align-items-center {
        align-items: center !important;
        display: flex;
    }

    @media (max-width: 768px) {
        .text-right {
            text-align: left !important;
            margin-top: 10px;
        }

        .row.mb-4 {
            flex-direction: column;
        }
    }
</style>

<div class="main">
    <div class="container-fluid">
        <div class="row mb-4 align-items-center">
            <div class="col-md-18">
                <h2>
                    <i class="bi bi-file-earmark-code"></i>
                    Activity Detail: <c:out value="${activity.action}"/>
                </h2>
            </div>
        </div>

        <div class="card detail-card">
            <div class="card-header">
                <i class="bi bi-info-circle"></i> General Information
            </div>
            <div class="card-body">
                <div class="row" style="justify-content: space-evenly">
                    <div class="col-md-12">
                        <div class="info-row">
                            <span class="info-label">Status:</span>
                            <span class="badge badge-${activity.status}">
                                ${activity.status}
                            </span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">HTTP Status Code:</span>
                            <c:choose>
                                <c:when test="${not empty activity.httpStatusCode}">
                                    ${activity.httpStatusCode}
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="info-row">
                            <span class="info-label">HTTP Method:</span>
                            <c:choose>
                                <c:when test="${not empty activity.httpMethod}">
                                    <span class="badge badge-secondary">${activity.httpMethod}</span>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Entity Type:</span>
                            <c:choose>
                                <c:when test="${not empty activity.entityType}">
                                    ${activity.entityType}
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="info-row">
                            <span class="info-label">Endpoint:</span>
                            <c:choose>
                                <c:when test="${not empty activity.endpoint}">
                                    <code>${activity.endpoint}</code>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Execution Time:</span>
                            <c:choose>
                                <c:when test="${not empty activity.executionTimeMs}">
                                    ${activity.executionTimeMs} ms
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="info-row">
                            <span class="info-label">IP Address:</span>
                            <c:choose>
                                <c:when test="${not empty activity.ipAddress}">
                                    ${activity.ipAddress}
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Correlation ID:</span>
                            <c:choose>
                                <c:when test="${not empty activity.correlationId}">
                                    <small><code>${activity.correlationId}</code></small>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Request Payload Card -->
        <c:if test="${not empty activity.requestPayload}">
            <div class="card detail-card">
                <div class="card-header bg-request">
                    <i class="bi bi-arrow-up-circle"></i> Request Payload
                </div>
                <div class="card-body">
                    <pre class="json-viewer"><code class="language-json"><c:out
                            value="${activity.requestPayload}"/></code></pre>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty activity.responsePayload}">
            <div class="card detail-card">
                <div class="card-header bg-response">
                    <i class="bi bi-arrow-down-circle"></i> Response Payload
                </div>
                <div class="card-body">
                    <pre class="json-viewer"><code class="language-json"><c:out
                            value="${activity.responsePayload}"/></code></pre>
                </div>
            </div>
        </c:if>

        <!-- Error Message Card -->
        <c:if test="${not empty activity.errorMessage}">
            <div class="card detail-card">
                <div class="card-header bg-error">
                    <i class="bi bi-x-circle"></i> Error Message
                </div>
                <div class="card-body">
                    <div class="alert alert-danger mb-0">
                        <strong>Error:</strong> <c:out value="${activity.errorMessage}"/>
                    </div>
                </div>
            </div>
        </c:if>

        <div class="mt-4 text-right">
            <a href="${baseLink}${version}/activity/history" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to History
            </a>
        </div>
    </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-json.min.js"></script>
<script>
    document.querySelectorAll('.json-viewer code').forEach(function (block) {
        try {
            const jsonText = block.textContent;
            const jsonObj = JSON.parse(jsonText);
            block.textContent = JSON.stringify(jsonObj, null, 2);
            Prism.highlightElement(block);
        } catch (e) {
            console.log('JSON formatting skipped');
        }
    });
</script>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>

