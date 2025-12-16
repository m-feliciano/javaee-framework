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

<div class="content">
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
</div>

<c:set var="prettyJsonUrl"><tag:assetPath name="pretty-json.js"/></c:set>
<script src="${prettyJsonUrl}" defer></script>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>
