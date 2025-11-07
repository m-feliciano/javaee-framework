<%@ page import="com.dev.servlet.core.response.IHttpResponse" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>
<%@ include file="/WEB-INF/routes/history-routes.jspf" %>

<%
    IHttpResponse<?> httpResponse = (com.dev.servlet.core.response.IHttpResponse<?>) request.getAttribute("response");
    request.setAttribute("pageable", httpResponse.body());
%>

<title>Activity History</title>

<style>
    .activity-table {
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .badge-SUCCESS {
        background-color: #28a745;
        color: white;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 0.85em;
    }

    .badge-FAILED {
        background-color: #dc3545;
        color: white;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 0.85em;
    }

    .badge-PENDING {
        background-color: #ffc107;
        color: black;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 0.85em;
    }

    .badge-http-method {
        background-color: #2196F3;
        color: white;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 0.85em;
        font-weight: 600;
    }

    @media (prefers-color-scheme: dark) {
        .badge-http-method {
            background-color: #58a6ff;
            color: #0d1117;
        }
    }

    .btn-view-json {
        padding: 4px 12px;
        font-size: 0.9em;
    }

    .activity-timestamp {
        white-space: nowrap;
    }
</style>

<div class="main">
    <div class="container-fluid">
        <div class="row mb-3">
            <div class="col-12">
                <h2>
                    <i class="bi bi-clock-history"></i>
                    Activity History
                </h2>
            </div>
        </div>

        <jsp:include page="/WEB-INF/view/components/search.jsp">
            <jsp:param name="placeholder" value="Search activity"/>
            <jsp:param name="action" value="${baseLink}${version}/activity/search"/>
            <jsp:param name="onclear" value="${baseLink}${version}/activity/history"/>
            <jsp:param name="limit" value="${ pageable.getPageSize() }"/>
            <jsp:param name="showCategory" value="false"/>
            <jsp:param name="sort" value="timestamp"/>
            <jsp:param name="searchType" value="name"/>
            <jsp:param name="order" value="desc"/>
        </jsp:include>

        <c:choose>
            <c:when test="${empty pageable.content}">
                <div class="alert alert-info">
                    <i class="bi bi-info-circle"></i> No activities found.
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover table-bordered activity-table">
                        <thead class="thead-dark">
                        <tr>
                            <th>Action</th>
                            <th>Status</th>
                            <th>Entity</th>
                            <th>HTTP Method</th>
                            <th>Timestamp</th>
                            <th>Execution Time</th>
                            <th>Details</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${pageable.content}" var="activity">
                            <fmt:formatDate value="${activity.timestamp}" pattern="dd/MM/yyyy HH:mm:ss"
                                            var="formattedTimestamp"/>
                            <tr>
                                <td>
                                    <strong><c:out value="${activity.action}"/></strong>
                                    <c:if test="${not empty activity.endpoint}">
                                        <br><small class="text-muted">
                                        <c:out value="${activity.endpoint}"/>
                                    </small>
                                    </c:if>
                                </td>
                                <td>
                                        <span class="badge-${activity.status}">
                                            <c:out value="${activity.status}"/>
                                        </span>
                                    <c:if test="${not empty activity.httpStatusCode}">
                                        <br><small class="text-muted">HTTP ${activity.httpStatusCode}</small>
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${not empty activity.entityType}">
                                        <small>
                                            <c:out value="${activity.entityType}"/>
                                            <c:if test="${not empty activity.entityId}">
                                                #<c:out value="${fn:substring(activity.entityId, 0, 8)}"/>
                                            </c:if>
                                        </small>
                                    </c:if>
                                    <c:if test="${empty activity.entityType}">
                                        <small class="text-muted">-</small>
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${not empty activity.httpMethod}">
                                            <span class="badge-http-method">
                                                <c:out value="${activity.httpMethod}"/>
                                            </span>
                                    </c:if>
                                </td>
                                <td class="activity-timestamp">${formattedTimestamp}</td>
                                <td>
                                    <c:if test="${not empty activity.executionTimeMs}">
                                        ${activity.executionTimeMs} ms
                                    </c:if>
                                    <c:if test="${empty activity.executionTimeMs}">
                                        <small class="text-muted">-</small>
                                    </c:if>
                                </td>
                                <td>
                                    <a href="${baseLink}${version}/activity/history/${activity.id}"
                                       class="btn btn-sm btn-primary btn-view-json">
                                        <i class="bi bi-file-earmark-code"></i> JSON
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <jsp:include page="/WEB-INF/view/components/pagination.jsp">
                    <jsp:param name="pageable" value="${pageable}"/>
                </jsp:include>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>

