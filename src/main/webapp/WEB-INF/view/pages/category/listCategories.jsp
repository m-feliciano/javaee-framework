<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/routes/category-routes.jspf" %>
<%@ page import="com.servletstack.adapter.in.web.dto.IHttpResponse" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("categories", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<title>Categories</title>

<div class="content">
    <div class="main">
        <div class="action-bar">
            <div class="action-bar-title">
                <h2>Categories</h2>
                <p class="action-bar-subtitle">Manage product categories</p>
            </div>
            <div class="action-buttons">
                <a href="${baseLink}${version}${ newCategory }" class="btn btn-success">
                    <i class="bi bi-plus-circle"></i> New Category
                </a>
            </div>
        </div>

        <c:if test="${ empty categories }">
            <div class="empty-state">
                <div class="empty-state-icon">
                    <i class="bi bi-folder-x"></i>
                </div>
                <h3 class="empty-state-title">No Categories Found</h3>
                <p class="empty-state-description">Create your first category to organize your products.</p>
                <a href="${baseLink}${version}${ newCategory }" class="btn btn-primary">
                    <i class="bi bi-plus-circle"></i> Create Category
                </a>
            </div>
        </c:if>

        <c:if test="${ not empty categories }">
            <div class="card">
                <div class="table-responsive">
                    <table class="table table-striped table-bordered table-hover mb-0">
                        <caption class="pb-0 caption">${categories.size()} records</caption>
                        <thead class="thead-dark">
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">NAME</th>
                            <th scope="col">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${ categories }" var="category">
                            <input type="hidden" name="id" id="id${ category.id }" value="${ category.id }">
                            <tr>
                                <th class="w-8" scope="row"><c:out value="${fn:substring(category.id, 0, 8)}" escapeXml="true"/></th>
                                <td class="w-30"><c:out value="${category.name}" escapeXml="true"/></td>
                                <td class="w-10">
                                    <div class="grid-container grid-auto-fit grid-gap-sm" style="grid-template-columns: repeat(auto-fit, minmax(40px, 1fr));">
                                        <a href="${baseLink}${version}${listCategory}/${category.id}"
                                           class="btn btn-auto btn-primary" title="View">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                        <form action="<c:url value='${baseLink}${version}${deleteCategory}/${category.id}'/>"
                                              method="post" class="csrf-delete-form" style="margin: 0;">
                                            <button type="submit" class="btn btn-auto btn-danger" title="Delete">
                                                <i class="bi bi-trash3"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
    </div>
</div>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>