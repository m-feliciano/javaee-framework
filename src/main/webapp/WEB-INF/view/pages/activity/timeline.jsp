<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>
<%@ include file="/WEB-INF/routes/history-routes.jspf" %>

<%
    IHttpResponse<?> httpResponse = (IHttpResponse<?>) request.getAttribute("response");
    List<?> activities = (List<?>) httpResponse.body();
    request.setAttribute("activities", activities);
%>

<title>Activity Timeline</title>

<style>
    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
        flex-wrap: wrap;
        gap: 1rem;
    }

    .page-header h2 {
        margin: 0;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .view-switcher {
        display: flex;
        gap: 0.5rem;
    }

    .view-switcher .btn {
        padding: 0.5rem 1rem;
        text-decoration: none;
        border-radius: 6px;
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        transition: all 0.2s;
    }

    .view-switcher .btn-active {
        background: var(--primary-color);
        color: white;
    }

    .view-switcher .btn-inactive {
        background: transparent;
        color: var(--text-muted);
        border: 1px solid var(--border-color);
    }

    .view-switcher .btn-inactive:hover {
        background: var(--secondary-color);
        color: white;
    }

    @media (max-width: 768px) {
        .page-header {
            flex-direction: column;
            align-items: flex-start;
        }

        .view-switcher {
            width: 100%;
        }

        .view-switcher .btn {
            flex: 1;
            justify-content: center;
        }
    }

    @media (prefers-color-scheme: dark) {
        .view-switcher .btn-inactive {
            border-color: var(--border-color-dark);
        }

        .view-switcher .btn-inactive:hover {
            background: #6c757d;
        }
    }
</style>

<div class="content">
    <div class="main">
        <div class="container-fluid">
        <div class="page-header">
            <h2>
                <i class="bi bi-clock-history"></i>
                Activity Timeline
            </h2>

            <div class="view-switcher">
                <%
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

                    String endDate = sdf.format(cal.getTime());
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
                    String startDate = sdf.format(cal.getTime());
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 7);

                    java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("ddMMyyyy");
                    String endDateDisplay = displayFormat.format(cal.getTime());
                    cal.add(java.util.Calendar.DAY_OF_MONTH, -7);

                    String startDateDisplay = displayFormat.format(cal.getTime());
                    String currentStartDate = request.getParameter("startDate");
                    String currentEndDate = request.getParameter("endDate");

                    if (currentStartDate == null) currentStartDate = startDate;
                    if (currentEndDate == null) currentEndDate = endDate;
                %>
                <a href="${baseLink}${version}/activity/timeline?startDate=<%= currentStartDate %>&endDate=<%= currentEndDate %>"
                   class="btn btn-active"
                   title="Viewing: <%= startDateDisplay %> to <%= endDateDisplay %>">
                    <i class="bi bi-diagram-3"></i> Timeline
                </a>
                <a href="${baseLink}${version}/activity/history" class="btn btn-inactive">
                    <i class="bi bi-list-ul"></i> List View
                </a>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <jsp:include page="/WEB-INF/view/components/date-range-filter.jsp">
                    <jsp:param name="action" value="${baseLink}${version}/activity/timeline"/>
                    <jsp:param name="onclear" value="${baseLink}${version}/activity/timeline"/>
                    <jsp:param name="startDate" value="${param.startDate}"/>
                    <jsp:param name="endDate" value="${param.endDate}"/>
                </jsp:include>

            </div>
        </div>
        <jsp:include page="/WEB-INF/view/components/timeline.jsp">
            <jsp:param name="activities" value="${activities}"/>
        </jsp:include>
        </div>

    </div>
</div>

<script>
    setInterval(function() {
        location.reload();
    }, 30000);
</script>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>

