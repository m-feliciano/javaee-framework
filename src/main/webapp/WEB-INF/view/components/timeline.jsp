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

