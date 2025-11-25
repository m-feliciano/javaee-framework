<%@ page import="com.dev.servlet.core.response.IHttpResponse" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/routes/inspect-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<link rel="stylesheet" href="<c:url value='/resources/css/inspect.css'/>"/>

<%
    request.setAttribute("controllersList", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<title>Controller Inspector</title>

<div class="main">
    <div class="action-bar">
        <div class="action-bar-title">
            <h1><i class="bi bi-diagram-3-fill"></i> Controller Inspector</h1>
            <p class="action-bar-subtitle">Available Endpoints</p>
        </div>
        <div class="action-buttons">
            <a href="${baseLink}${version}${inspectControllers}" class="btn btn-primary" target="_blank">
                <i class="bi bi-box-arrow-up-right"></i>
                See Raw Data
            </a>
        </div>
    </div>

    <div class="card">
        <div class="card-body">
            <div id="loading" style="display:none">Loading...</div>
            <div id="error" style="display:none;color:red"></div>
            <div id="controllers">
                <c:forEach items="${controllersList}" var="ctrl">
                    <div class="controller" data-controller="<c:out value="${ctrl.name()}"/>">
                        <div class="controller-header">
                            <h3 class="controller-title">
                                <i class="bi bi-diagram-3"></i>
                                <span class="basepath"><c:out value="${ctrl.basePath()}"/></span>
                                <small class="text-muted">(<c:out value="${ctrl.name()}"/>)</small>
                            </h3>
                            <div class="controller-actions">
                                <button type="button" class="btn btn-sm btn-outline-secondary toggle-methods">
                                    <i class="bi bi-chevron-down"></i> Toggle Methods
                                </button>
                            </div>
                        </div>

                        <div class="methods">
                            <table class="table table-hover inspect-table">
                                <thead>
                                <tr>
                                    <th>Path</th>
                                    <th>HTTP</th>
                                    <th>JSON Type</th>
                                    <th>Auth</th>
                                    <th>Roles</th>
                                    <th>RESPONSE</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${ctrl.methods()}" var="m">
                                    <tr class="method-row"
                                        data-http="<c:out value="${m.httpMethod()}"/>"
                                        data-auth="<c:out value="${m.requireAuth()}"/>">

                                        <td data-label="Path"><code class="path"><c:out value="${m.path()}"/></code>
                                        </td>
                                        <td data-label="HTTP"><span class="badge bg-light text-dark"><c:out
                                                value="${m.httpMethod()}"/></span></td>
                                        <td data-label="JSON Type"><span class="badge bg-info json-type"><c:out
                                                value="${m.jsonType()}"/></span></td>
                                        <td data-label="Auth">
                                            <c:choose>
                                                <c:when test="${m.requireAuth()}">
                                                    <span class="badge bg-success">Auth</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">No Auth</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td data-label="Roles">
                                            <c:forEach items="${m.roles()}" var="r">
                                                <c:choose>
                                                    <c:when test="${r eq 'DEFAULT'}">
                                                        <span class="badge bg-secondary">Default</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-primary role-badge"><c:out
                                                                value="${r}"/></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </td>
                                        <td data-label="Return"><code class="return-type"><c:out
                                                value="${m.responseType()}"/></code></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value='/resources/js/inspect.js'/>"></script>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>
