<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<%@ include file="/WEB-INF/routes/category-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("category", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<div class="content">
    <div class="main">
        <div class="container-narrow">
            <div class="action-bar">
                <div class="action-bar-title">
                    <h2>Category Details</h2>
                    <p class="action-bar-subtitle">View category information</p>
                </div>
                <div class="action-buttons">
                    <a href="${baseLink}${version}${listCategory}" class="btn btn-secondary">
                        <i class="bi bi-arrow-left"></i> Back
                    </a>
                    <a href="${baseLink}${version}${editCategory}/${category.id}" class="btn btn-success">
                        <i class="bi bi-pencil-square"></i> Edit
                    </a>
                </div>
            </div>

            <div class="card">
                <div class="card-body">
                    <div class="grid-form">
                        <div class="form-group">
                            <label for="inputId" class="form-label">ID</label>
                            <input type="text" name="id" class="form-control" id="inputId"
                                   value="${ category.id }" readonly/>
                        </div>

                        <div class="form-group">
                            <label for="inputName" class="form-label">NAME</label>
                            <input type="text" name="name" class="form-control" id="inputName"
                                   value="${ category.name }" readonly/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>