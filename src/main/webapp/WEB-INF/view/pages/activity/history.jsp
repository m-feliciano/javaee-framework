<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>
<%@ include file="/WEB-INF/routes/history-routes.jspf" %>

<%
    IHttpResponse<?> httpResponse = (IHttpResponse<?>) request.getAttribute("response");
    request.setAttribute("pageable", httpResponse.body());
    request.setAttribute("listStatus", List.of("SUCCESS", "FAILED", "PENDING"));
%>

<title>Activity History</title>

<div class="content">
    <div class="main">
        <div class="container-fluid">
        <div class="page-header">
            <h2>
                <i class="bi bi-clock-history"></i>
                Activity History
            </h2>

            <div class="view-switcher">
                <%
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    String endDate = sdf.format(cal.getTime());
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
                    String startDate = sdf.format(cal.getTime());

                    java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("ddMMyyyy");
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 7);
                    String endDateDisplay = displayFormat.format(cal.getTime());

                    cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
                    String startDateDisplay = displayFormat.format(cal.getTime());
                %>
                <a href="${baseLink}${version}/activity/timeline?startDate=<%= startDate %>&endDate=<%= endDate %>"
                   class="btn btn-inactive"
                   title="Last 7 days: <%= startDateDisplay %> to <%= endDateDisplay %>">
                    <i class="bi bi-diagram-3"></i> Timeline
                </a>
                <a href="${baseLink}${version}/activity/history" class="btn btn-active">
                    <i class="bi bi-list-ul"></i> List View
                </a>
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
            <jsp:param name="listStatus" value="${listStatus}"/>
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
                            <th>Took</th>
                            <th>Details</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${pageable.content}" var="activity">
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
                                <td class="activity-timestamp">${activity.timestampFormatted}</td>
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
                    <jsp:param name="totalRecords" value="${pageable.getTotalElements()}"/>
                    <jsp:param name="currentPage" value="${pageable.getCurrentPage()}"/>
                    <jsp:param name="totalPages" value="${pageable.getTotalPages()}"/>
                    <jsp:param name="pageSize" value="${pageable.getPageSize()}"/>
                    <jsp:param name="sort" value="${pageable.getSort().getField()}"/>
                    <jsp:param name="direction" value="${pageable.getSort().getDirection().getValue()}"/>
                    <jsp:param name="k" value="${k}"/>
                    <jsp:param name="q" value="${q}"/>
                </jsp:include>
            </c:otherwise>
        </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>
