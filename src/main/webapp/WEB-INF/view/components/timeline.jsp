<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/routes/history-routes.jspf" %>

<div class="timeline-dashboard">
    <c:choose>
        <c:when test="${empty activities}">
            <div class="alert alert-info timeline-empty">
                <i class="bi bi-calendar-x"></i>
                <p>No activities found in the selected period.</p>
            </div>
        </c:when>
        <c:otherwise>
            <c:set var="successCount" value="0"/>
            <c:set var="failedCount" value="0"/>
            <c:set var="totalTime" value="0"/>
            <jsp:useBean id="dateCountMap" class="java.util.LinkedHashMap" scope="page"/>
            <jsp:useBean id="dateSuccessMap" class="java.util.LinkedHashMap" scope="page"/>
            <jsp:useBean id="dateFailedMap" class="java.util.LinkedHashMap" scope="page"/>

            <%
                List<?> objects = new ArrayList<>(((List<?>) request.getAttribute("activities")));
                java.util.Collections.reverse(objects);
                request.setAttribute("activitiesSort", objects);
            %>

            <c:forEach items="${activitiesSort}" var="activity">
                <c:if test="${activity.status == 'SUCCESS'}">
                    <c:set var="successCount" value="${successCount + 1}"/>
                </c:if>
                <c:if test="${activity.status == 'FAILED'}">
                    <c:set var="failedCount" value="${failedCount + 1}"/>
                </c:if>
                <c:if test="${not empty activity.executionTimeMs}">
                    <c:set var="totalTime" value="${totalTime + activity.executionTimeMs}"/>
                </c:if>
                <c:set var="currentCount" value="${dateCountMap[activity.timestampFormatted]}"/>
                <c:if test="${empty currentCount}">
                    <c:set var="currentCount" value="0"/>
                </c:if>
                <c:set target="${dateCountMap}" property="${activity.timestampFormatted}" value="${currentCount + 1}"/>

                <c:if test="${activity.status == 'SUCCESS'}">
                    <c:set var="currentSuccess" value="${dateSuccessMap[activity.timestampFormatted]}"/>
                    <c:if test="${empty currentSuccess}">
                        <c:set var="currentSuccess" value="0"/>
                    </c:if>
                    <c:set target="${dateSuccessMap}" property="${activity.timestampFormatted}" value="${currentSuccess + 1}"/>
                </c:if>
                <c:if test="${activity.status == 'FAILED'}">
                    <c:set var="currentFailed" value="${dateFailedMap[activity.timestampFormatted]}"/>
                    <c:if test="${empty currentFailed}">
                        <c:set var="currentFailed" value="0"/>
                    </c:if>
                    <c:set target="${dateFailedMap}" property="${activity.timestampFormatted}" value="${currentFailed + 1}"/>
                </c:if>
            </c:forEach>

            <div class="activity-chart-container">
                <div class="chart-header">
                    <h3>
                        <i class="bi bi-bar-chart-line"></i>
                        Overview
                    </h3>
                    <div class="chart-legend">
                        <span class="legend-item legend-total">
                            <span class="legend-dot"></span> Total
                        </span>
                        <span class="legend-item legend-success">
                            <span class="legend-dot"></span> Success
                        </span>
                        <span class="legend-item legend-failed">
                            <span class="legend-dot"></span> Failed
                        </span>
                    </div>
                </div>

                <div class="chart-wrapper">
                    <div class="chart-grid-lines">
                        <span class="grid-line" style="bottom: 25%;" data-label="25%"></span>
                        <span class="grid-line" style="bottom: 50%;" data-label="50%"></span>
                        <span class="grid-line" style="bottom: 75%;" data-label="75%"></span>
                        <span class="grid-line" style="bottom: 100%;" data-label="100%"></span>
                    </div>

                    <div class="activity-chart">
                        <svg class="line-chart-svg" viewBox="0 0 100 100" preserveAspectRatio="none">
                            <defs>
                                <linearGradient id="lineGradientTotal" x1="0%" y1="0%" x2="0%" y2="100%">
                                    <stop offset="0%" style="stop-color:#0d6efd;stop-opacity:0.2"></stop>
                                    <stop offset="100%" style="stop-color:#0d6efd;stop-opacity:0"></stop>
                                </linearGradient>
                                <linearGradient id="lineGradientSuccess" x1="0%" y1="0%" x2="0%" y2="100%">
                                    <stop offset="0%" style="stop-color:#198754;stop-opacity:0.2"></stop>
                                    <stop offset="100%" style="stop-color:#198754;stop-opacity:0"></stop>
                                </linearGradient>
                                <linearGradient id="lineGradientFailed" x1="0%" y1="0%" x2="0%" y2="100%">
                                    <stop offset="0%" style="stop-color:#dc3545;stop-opacity:0.2"></stop>
                                    <stop offset="100%" style="stop-color:#dc3545;stop-opacity:0"></stop>
                                </linearGradient>
                            </defs>

                            
                            <c:set var="maxCount" value="0"/>
                            <c:forEach items="${dateCountMap}" var="entry">
                                <c:if test="${entry.value > maxCount}">
                                    <c:set var="maxCount" value="${entry.value}"/>
                                </c:if>
                            </c:forEach>

                            
                            <c:set var="pathDataTotal" value=""/>
                            <c:set var="areaDataTotal" value=""/>
                            <c:set var="pathDataSuccess" value=""/>
                            <c:set var="areaDataSuccess" value=""/>
                            <c:set var="pathDataFailed" value=""/>
                            <c:set var="areaDataFailed" value=""/>
                            <c:set var="totalDates" value="${fn:length(dateCountMap)}"/>
                            <c:set var="index" value="0"/>

                            <c:forEach items="${dateCountMap}" var="entry">
                                <c:set var="dateKey" value="${entry.key}"/>
                                <c:set var="totalCount" value="${entry.value}"/>
                                <c:set var="daySuccessCount" value="${dateSuccessMap[dateKey]}"/>
                                <c:set var="dayFailedCount" value="${dateFailedMap[dateKey]}"/>

                                <c:if test="${empty daySuccessCount}">
                                    <c:set var="daySuccessCount" value="0"/>
                                </c:if>
                                <c:if test="${empty dayFailedCount}">
                                    <c:set var="dayFailedCount" value="0"/>
                                </c:if>

                                <c:set var="heightPercentTotal" value="${100 - (totalCount * 100) / maxCount}"/>
                                <c:set var="heightPercentSuccess" value="${100 - (daySuccessCount * 100) / maxCount}"/>
                                <c:set var="heightPercentFailed" value="${100 - (dayFailedCount * 100) / maxCount}"/>
                                <c:set var="xPercent" value="${(index * 100) / (totalDates - 1)}"/>

                                <c:choose>
                                    <c:when test="${index == 0}">
                                        <c:set var="pathDataTotal" value="M ${xPercent} ${heightPercentTotal}"/>
                                        <c:set var="areaDataTotal" value="M ${xPercent} 100 L ${xPercent} ${heightPercentTotal}"/>
                                        <c:set var="pathDataSuccess" value="M ${xPercent} ${heightPercentSuccess}"/>
                                        <c:set var="areaDataSuccess" value="M ${xPercent} 100 L ${xPercent} ${heightPercentSuccess}"/>
                                        <c:set var="pathDataFailed" value="M ${xPercent} ${heightPercentFailed}"/>
                                        <c:set var="areaDataFailed" value="M ${xPercent} 100 L ${xPercent} ${heightPercentFailed}"/>
                                    </c:when>
                                    <c:otherwise>
                                        
                                        <c:set var="pathDataTotal" value="${pathDataTotal} L ${xPercent} ${heightPercentTotal}"/>
                                        <c:set var="areaDataTotal" value="${areaDataTotal} L ${xPercent} ${heightPercentTotal}"/>
                                        <c:set var="pathDataSuccess" value="${pathDataSuccess} L ${xPercent} ${heightPercentSuccess}"/>
                                        <c:set var="areaDataSuccess" value="${areaDataSuccess} L ${xPercent} ${heightPercentSuccess}"/>
                                        <c:set var="pathDataFailed" value="${pathDataFailed} L ${xPercent} ${heightPercentFailed}"/>
                                        <c:set var="areaDataFailed" value="${areaDataFailed} L ${xPercent} ${heightPercentFailed}"/>
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="index" value="${index + 1}"/>
                            </c:forEach>

                            <c:set var="areaDataTotal" value="${areaDataTotal} L 100 100 Z"/>
                            <c:set var="areaDataSuccess" value="${areaDataSuccess} L 100 100 Z"/>
                            <c:set var="areaDataFailed" value="${areaDataFailed} L 100 100 Z"/>

                            <path d="${areaDataTotal}" fill="url(#lineGradientTotal)" opacity="0.4"></path>
                            <path d="${areaDataSuccess}" fill="url(#lineGradientSuccess)" opacity="0.4"></path>
                            <path d="${areaDataFailed}" fill="url(#lineGradientFailed)" opacity="0.4"></path>
                            <path d="${pathDataTotal}" fill="none" stroke="#0d6efd" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" opacity="0.9"></path>
                            <path d="${pathDataSuccess}" fill="none" stroke="#198754" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="5,3" opacity="0.9"></path>
                            <path d="${pathDataFailed}" fill="none" stroke="#dc3545" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="2,2" opacity="0.9"></path>
                        </svg>

                        <c:set var="index" value="0"/>
                        <c:forEach items="${dateCountMap}" var="entry">
                            <c:set var="dateKey" value="${entry.key}"/>
                            <c:set var="totalCount" value="${entry.value}"/>
                            <c:set var="successCountDay" value="${dateSuccessMap[dateKey]}"/>
                            <c:set var="failedCountDay" value="${dateFailedMap[dateKey]}"/>
                            <c:if test="${empty successCountDay}">
                                <c:set var="successCountDay" value="0"/>
                            </c:if>
                            <c:if test="${empty failedCountDay}">
                                <c:set var="failedCountDay" value="0"/>
                            </c:if>

                            <c:set var="heightPercentTotal" value="${(totalCount * 100) / maxCount}"/>
                            <c:set var="heightPercentSuccess" value="${(successCountDay * 100) / maxCount}"/>
                            <c:set var="heightPercentFailed" value="${(failedCountDay * 100) / maxCount}"/>
                            <c:set var="xPercent" value="${(index * 100) / (totalDates - 1)}"/>

                            <div class="chart-point-wrapper" style="left: ${xPercent}%; bottom: ${heightPercentTotal}%;">
                                <div class="chart-point chart-point-total"
                                     data-value="${totalCount}"
                                     title="${dateKey}: Total ${totalCount}">
                                    <span class="point-label">Total: ${totalCount}</span>
                                </div>
                            </div>

                            <c:if test="${successCountDay > 0}">
                                <div class="chart-point-wrapper" style="left: ${xPercent}%; bottom: ${heightPercentSuccess}%;">
                                    <div class="chart-point chart-point-success"
                                         data-value="${successCountDay}"
                                         title="${dateKey}: Success ${successCountDay}">
                                        <span class="point-label">Success: ${successCountDay}</span>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test="${failedCountDay > 0}">
                                <div class="chart-point-wrapper" style="left: ${xPercent}%; bottom: ${heightPercentFailed}%;">
                                    <div class="chart-point chart-point-failed"
                                         data-value="${failedCountDay}"
                                         title="${dateKey}: Failed ${failedCountDay}">
                                        <span class="point-label">Failed: ${failedCountDay}</span>
                                    </div>
                                </div>
                            </c:if>

                            <div class="chart-date-label-wrapper" style="left: ${xPercent}%;">
                                <span class="chart-date-label">${dateKey}</span>
                            </div>

                            <c:set var="index" value="${index + 1}"/>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <div class="timeline-stats">
                <div class="stat-card">
                    <div class="stat-icon stat-icon-primary">
                        <i class="bi bi-activity"></i>
                    </div>
                    <div class="stat-content">
                        <h4>${fn:length(activities)}</h4>
                        <p>Total Activities</p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon stat-icon-success">
                        <i class="bi bi-check-circle"></i>
                    </div>
                    <div class="stat-content">
                        <h4>${successCount}</h4>
                        <p>Success</p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon stat-icon-danger">
                        <i class="bi bi-x-circle"></i>
                    </div>
                    <div class="stat-content">
                        <h4>${failedCount}</h4>
                        <p>Failed</p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon stat-icon-warning">
                        <i class="bi bi-clock"></i>
                    </div>
                    <div class="stat-content">
                        <h4>
                            <c:choose>
                                <c:when test="${totalTime >= 1000}">
                                    <fmt:formatNumber value="${totalTime / 1000}" maxFractionDigits="2"/>s
                                </c:when>
                                <c:otherwise>
                                    ${totalTime}ms
                                </c:otherwise>
                            </c:choose>
                        </h4>
                        <p>Total Time</p>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<style>
    .timeline-dashboard {
        margin-top: 1.5rem;
    }
    .activity-chart-container {
        background: var(--card-background);
        border: 1px solid var(--border-color);
        border-radius: 8px;
        padding: 3.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    }

    .chart-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 2.5rem;
        flex-wrap: wrap;
        gap: 1rem;
    }

    .chart-header h3 {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 600;
        color: var(--text-color);
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .chart-legend {
        display: flex;
        gap: 1.5rem;
        flex-wrap: wrap;
    }

    .legend-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        color: var(--text-muted);
    }

    .legend-dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
    }

    .legend-total .legend-dot {
        background: linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%);
    }

    .legend-success .legend-dot {
        background: #198754;
    }

    .legend-failed .legend-dot {
        background: #dc3545;
    }

    .chart-wrapper {
        position: relative;
        width: 100%;
    }

    .chart-grid-lines {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 40px;
        pointer-events: none;
        z-index: 1;
    }

    .grid-line {
        position: absolute;
        left: 0;
        right: 0;
        height: 1px;
        background: var(--border-color);
        opacity: 0.4;
    }

    .grid-line::before {
        content: attr(data-label);
        position: absolute;
        left: -40px;
        top: -8px;
        font-size: 0.7rem;
        color: var(--text-muted);
        font-weight: 500;
    }

    .grid-line:last-child {
        opacity: 0.6;
    }

    .activity-chart {
        width: 100%;
        height: 200px;
        margin-left: 45px;
    }

    .line-chart-svg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 1;
    }

    .chart-point-wrapper {
        position: absolute;
        transform: translate(-50%, 0);
        z-index: 2;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .chart-point {
        width: 12px;
        height: 12px;
        border: 3px solid white;
        border-radius: 50%;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;
    }

    .chart-point-total {
        background: #0d6efd;
        box-shadow: 0 2px 6px rgba(13, 110, 253, 0.3);
        z-index: 3;
    }

    .chart-point-success {
        background: #198754;
        box-shadow: 0 2px 6px rgba(25, 135, 84, 0.3);
        z-index: 4;
    }

    .chart-point-failed {
        background: #dc3545;
        box-shadow: 0 2px 6px rgba(220, 53, 69, 0.3);
        z-index: 5;
    }

    .chart-point-total:hover {
        width: 18px;
        height: 18px;
        background: #0a58ca;
        box-shadow: 0 4px 12px rgba(13, 110, 253, 0.6);
        transform: scale(1.2);
    }

    .chart-point-success:hover {
        width: 18px;
        height: 18px;
        background: #146c43;
        box-shadow: 0 4px 12px rgba(25, 135, 84, 0.6);
        transform: scale(1.2);
    }

    .chart-point-failed:hover {
        width: 18px;
        height: 18px;
        background: #b02a37;
        box-shadow: 0 4px 12px rgba(220, 53, 69, 0.6);
        transform: scale(1.2);
    }

    .point-label {
        position: absolute;
        top: -25px;
        left: 50%;
        transform: translateX(-50%);
        font-size: 0.75rem;
        font-weight: 700;
        color: var(--text-color);
        background: rgba(255, 255, 255, 0.95);
        padding: 2px 6px;
        border-radius: 4px;
        white-space: nowrap;
        opacity: 0;
        transition: opacity 0.2s;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        pointer-events: none;
    }

    .chart-point:hover .point-label {
        opacity: 1;
    }

    .chart-date-label-wrapper {
        position: absolute;
        bottom: -30px;
        transform: translateX(-50%);
        z-index: 1;
    }

    .chart-date-label {
        font-size: 0.7rem;
        color: var(--text-muted);
        white-space: nowrap;
    }
    .timeline-stats {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
        margin-bottom: 2rem;
    }

    .stat-card {
        background: var(--card-background);
        border: 1px solid var(--border-color);
        border-radius: 8px;
        padding: 1.25rem;
        display: flex;
        align-items: center;
        gap: 1rem;
        transition: transform 0.2s, box-shadow 0.2s;
    }

    .stat-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .stat-icon {
        width: 50px;
        height: 50px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.5rem;
    }

    .stat-icon-primary {
        background: rgba(13, 110, 253, 0.1);
        color: #0d6efd;
    }

    .stat-icon-success {
        background: rgba(25, 135, 84, 0.1);
        color: #198754;
    }

    .stat-icon-danger {
        background: rgba(220, 53, 69, 0.1);
        color: #dc3545;
    }

    .stat-icon-warning {
        background: rgba(255, 193, 7, 0.1);
        color: #ffc107;
    }

    .stat-content h4 {
        margin: 0;
        font-size: 1.75rem;
        font-weight: 700;
        color: var(--text-color);
    }

    .stat-content p {
        margin: 0;
        font-size: 0.9rem;
        color: var(--text-muted);
    }

    .timeline-empty {
        text-align: center;
        padding: 3rem 1rem;
    }

    .timeline-empty i {
        font-size: 3rem;
        margin-bottom: 1rem;
        display: block;
    }
    @media (max-width: 768px) {
        .timeline-stats {
            grid-template-columns: 1fr;
        }

        .chart-header {
            flex-direction: column;
            align-items: flex-start;
        }

        .chart-legend {
            width: 100%;
            justify-content: space-around;
        }

        .chart-grid-lines {
            bottom: 35px;
        }

        .grid-line::before {
            left: -35px;
            font-size: 0.65rem;
        }

        .activity-chart {
            height: 150px;
            margin-left: 35px;
        }

        .chart-point {
            width: 10px;
            height: 10px;
            border-width: 2px;
        }

        .chart-point:hover {
            width: 14px;
            height: 14px;
        }

        .point-label {
            font-size: 0.7rem;
            top: -22px;
        }

        .chart-date-label {
            font-size: 0.65rem;
        }

        .timeline::before {
            left: 15px;
        }

        .timeline-item {
            padding-left: 45px;
        }

        .timeline-marker {
            left: 5px;
            width: 22px;
            height: 22px;
            font-size: 0.75rem;
        }

        .timeline-header {
            flex-direction: column;
        }

        .timeline-badges {
            width: 100%;
        }
    }
    @media (prefers-color-scheme: dark) {
        .activity-chart-container {
            background: var(--card-background-dark);
            border-color: var(--border-color-dark);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
        }

        .grid-line {
            background: var(--border-color-dark);
            opacity: 0.5;
        }

        .grid-line:last-child {
            opacity: 0.7;
        }

        .line-chart-svg path[stroke] {
            stroke: #58a6ff;
        }

        .line-chart-svg path[stroke="#198754"] {
            stroke: #3fb950;
        }

        .line-chart-svg path[stroke="#dc3545"] {
            stroke: #f85149;
        }

        .chart-point-total {
            background: #58a6ff;
            border-color: #0d1117;
            box-shadow: 0 2px 6px rgba(88, 166, 255, 0.4);
        }

        .chart-point-total:hover {
            background: #1f6feb;
            box-shadow: 0 4px 12px rgba(88, 166, 255, 0.7);
        }

        .chart-point-success {
            background: #3fb950;
            border-color: #0d1117;
            box-shadow: 0 2px 6px rgba(63, 185, 80, 0.4);
        }

        .chart-point-success:hover {
            background: #2ea043;
            box-shadow: 0 4px 12px rgba(63, 185, 80, 0.7);
        }

        .chart-point-failed {
            background: #f85149;
            border-color: #0d1117;
            box-shadow: 0 2px 6px rgba(248, 81, 73, 0.4);
        }

        .chart-point-failed:hover {
            background: #da3633;
            box-shadow: 0 4px 12px rgba(248, 81, 73, 0.7);
        }

        .point-label {
            background: rgba(22, 27, 34, 0.95);
            color: #e6edf3;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        }

        .stat-card {
            background: var(--card-background-dark);
            border-color: var(--border-color-dark);
        }

        .stat-card:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
        }

        .stat-icon-primary {
            background: rgba(88, 166, 255, 0.15);
            color: #58a6ff;
        }

        .stat-icon-success {
            background: rgba(63, 185, 80, 0.15);
            color: #3fb950;
        }

        .stat-icon-danger {
            background: rgba(248, 81, 73, 0.15);
            color: #f85149;
        }

        .stat-icon-warning {
            background: rgba(219, 163, 52, 0.15);
            color: #dba334;
        }
    }
</style>