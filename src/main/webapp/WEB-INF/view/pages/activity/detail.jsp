<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="tag" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>
<%@ include file="/WEB-INF/routes/history-routes.jspf" %>

<%
    IHttpResponse<?> httpResponse = (IHttpResponse<?>) request.getAttribute("response");
    request.setAttribute("activity", httpResponse.body());
%>

<title>Activity Detail</title>

<style>
    :root {
        --muted: #6b7280;
        --accent: #0d6efd;
        --accent-2: #0a58ca;
        --success: #198754;
        --danger: #dc3545;
        --raw-bg: linear-gradient(180deg, #071025 0%, #08151c 60%);
        --raw-color: #cfe9ff;
        --raw-border: rgba(255, 255, 255, 0.04);
        --raw-box-shadow: inset 0 6px 18px rgba(2, 6, 23, 0.6);
        --raw-line-number-color: rgba(200, 215, 235, 0.22);
        --raw-key-color: #ffd7b5;
        --raw-string-color: #9ef6d2;
        --raw-number-color: #ffd6a5;
        --raw-boolean-color: #ffb86b;
        --raw-null-color: #9aa6b2;
    }

    .badge {
        display: inline-block;
        padding: 6px 8px;
        border-radius: 999px;
        font-size: .75rem;
        font-weight: 700
    }

    .raw-json {
        background: var(--raw-bg);
        color: var(--raw-color);
        padding: 18px 18px 18px 56px;
        border-radius: 10px;
        font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, "Roboto Mono", monospace;
        white-space: pre-wrap;
        word-break: break-word;
        overflow-wrap: anywhere;
        overflow: auto;
        max-height: 74vh;
        border: 1px solid var(--raw-border);
        box-shadow: var(--raw-box-shadow);
        counter-reset: line;
        position: relative;
    }

    .raw-json .line {
        display: block;
        position: relative;
        padding-left: 0;
    }

    .raw-json .line:before {
        content: counter(line);
        counter-increment: line;
        position: absolute;
        left: -48px;
        width: 40px;
        text-align: right;
        color: var(--raw-line-number-color);
        font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, "Roboto Mono", monospace;
        font-size: 12px;
        line-height: 1.5;
    }

    .raw-json .json-key {
        color: var(--raw-key-color);
    }

    .raw-json .json-string {
        color: var(--raw-string-color);
    }

    .raw-json .json-number {
        color: var(--raw-number-color);
    }

    .raw-json .json-boolean {
        color: var(--raw-boolean-color);
    }

    .raw-json .json-null {
        color: var(--raw-null-color);
        font-style: italic;
    }

    pre.raw-json, pre.raw-json code {
        background: transparent;
        color: inherit;
    }

    .btn {
        cursor: pointer;
        border-radius: 8px;
        padding: 8px 12px;
        font-weight: 700;
        border: 1px solid rgba(255, 255, 255, 0.03);
    }

    pre.raw-json, pre.raw-json code {
        white-space: pre-wrap;
        word-break: break-word;
        overflow-wrap: anywhere;
        max-width: 100%;
    }

    body .main .card .card-body #raw-container pre.raw-json,
    body .main .card .card-body #raw-container .raw-json {
        background: var(--raw-bg);
        color: var(--raw-color);
        box-shadow: var(--raw-box-shadow);
        border: 1px solid var(--raw-border);
    }

    body .main .card .card-body #raw-container pre.raw-json code,
    body .main .card .card-body #raw-container .raw-json * {
        background: transparent;
        color: inherit;
    }

    #raw-container {

        overflow: auto;
    }

    pre.raw-json {
        display: block;
        height: 100%;
        max-height: none;
        overflow: auto;
    }

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

    .raw-json {
        background: #2d2d2d;
        border-radius: 8px;
        padding: 15px;
        overflow-x: auto;
        max-height: 500px;
        margin: 0;
    }

    .raw-json code {
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
                            <span class="info-label">Took:</span>
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

        <c:if test="${not empty activity.requestPayload}">
            <div class="card detail-card">
                <div class="card-header bg-request">
                    <i class="bi bi-arrow-up-circle"></i> Request Payload
                </div>
                <div class="card-body">
                    <pre class="raw-json"
                         style="background: linear-gradient(180deg,#071025 0%,#08151c 60%);
                            color: #cfe9ff;
                            padding:18px 18px 18px 56px;
                            border-radius:10px;
                            box-shadow: inset 0 6px 18px rgba(2,6,23,0.6);
                            border:1px solid rgba(255,255,255,0.04);">
                        <c:out value="${activity.requestPayload}" escapeXml="true"/>
                    </pre>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty activity.responsePayload}">
            <div class="card detail-card">
                <div class="card-header bg-response">
                    <i class="bi bi-arrow-down-circle"></i> Response Payload
                </div>
                <div>
                    <div class="card-body">
                        <pre class="raw-json"
                             style="background: linear-gradient(180deg,#071025 0%,#08151c 60%);
                                    color: #cfe9ff;
                                    padding:18px 18px 18px 56px;
                                    border-radius:10px;
                                    box-shadow: inset 0 6px 18px rgba(2,6,23,0.6);
                                    border:1px solid rgba(255,255,255,0.04);">
                            <c:out value="${activity.responsePayload}" escapeXml="true"/>
                        </pre>
                    </div>
                </div>
            </div>
        </c:if>

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

<c:set var="prettyJsonUrl"><tag:assetPath name="pretty-json.js"/></c:set>
<script src="${prettyJsonUrl}" defer></script>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>
