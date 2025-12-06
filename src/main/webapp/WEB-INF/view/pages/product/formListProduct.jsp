<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/routes/product-routes.jspf" %>
<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("product", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<title>Product: <c:out value='${ product.name }' escapeXml='true'/></title>

<div class="content">
    <div class="main">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${baseLink}${version}${listProduct}">Products</a></li>
            <li class="breadcrumb-item active"><c:out value='${ product.name }' escapeXml='true'/></li>
        </ol>
    </nav>

    <div class="action-bar">
        <div class="action-bar-title">
            <h1><c:out value='${ product.name }' escapeXml='true'/></h1>
            <p class="action-bar-subtitle">Product Details</p>
        </div>
        <div class="action-buttons">
            <a href="${baseLink}${version}${listProduct}" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i>
                Back
            </a>
            <a href="${baseLink}${version}${editProduct}/${product.id}" class="btn btn-primary">
                <i class="bi bi-pencil-square"></i>
                Edit
            </a>
        </div>
    </div>

    <div class="row">
        <div class="col-12 col-lg-5">
            <div class="card">
                <div class="card-body text-center">
                    <c:choose>
                        <c:when test="${empty product.url}">
                            <img src="<c:url value='/resources/assets/no_image_available.png'/>"
                                 class="img-fluid"
                                 alt="No available"/>
                        </c:when>
                        <c:otherwise>
                            <img src="<c:out value='${product.url}' escapeXml='true'/>"
                                 class="img-fluid"
                                 alt="<c:out value='Product ${product.name}' escapeXml='true'/>"
                                 loading="lazy"/>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-7">
            <div class="card" style="margin-bottom: var(--spacing-6);">
                <div class="card-header">
                    <h3><i class="bi bi-info-circle"></i> Basic Information</h3>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-12 col-md-6" style="margin-bottom: var(--spacing-4);">
                            <label class="form-label"><strong>ID</strong></label>
                            <input type="text" class="form-control"
                                   value="<c:out value='${ product.id }' escapeXml='true'/>"
                                   readonly/>
                        </div>
                        <div class="col-12 col-md-6" style="margin-bottom: var(--spacing-4);">
                            <label class="form-label"><strong>Register Date</strong></label>
                            <input type="text" class="form-control"
                                   value="<c:out value='${ product.registerDateFormatted }' escapeXml='true'/>"
                                   readonly/>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-12 col-md-6" style="margin-bottom: var(--spacing-4);">
                            <label class="form-label"><strong>Category</strong></label>
                            <input type="text" class="form-control"
                                   value="<c:out value='${ product.category.name }' escapeXml='true'/>"
                                   readonly/>
                        </div>
                        <div class="col-12 col-md-6" style="margin-bottom: var(--spacing-4);">
                            <label class="form-label"><strong>Price</strong></label>
                            <input type="text" class="form-control"
                                   value="${product.priceFormatted}"
                                   readonly
                                   style="font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); color: var(--success);"/>
                        </div>
                    </div>

                    <c:if test="${not empty product.url and not product.url eq ''}">
                        <div style="margin-bottom: var(--spacing-4);">
                            <label class="form-label"><strong>Image URL</strong></label>
                            <div class="d-flex gap-2">
                                <input type="text" class="form-control"
                                       value="<c:out value='${product.url}' escapeXml='true'/>"
                                       readonly/>
                                <a href="${product.url}" target="_blank" class="btn btn-secondary">
                                    <i class="bi bi-box-arrow-up-right"></i>
                                </a>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <h3><i class="bi bi-card-text"></i> Description</h3>
                </div>
                <div class="card-body">
                    <div class="product-description">
                        <c:choose>
                            <c:when test="${not empty product.description}">
                                <p style="white-space: pre-wrap; line-height: var(--line-height-relaxed);"><c:out value='${product.description}' escapeXml='true'/></p>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted">
                                    <i class="bi bi-info-circle"></i> No description available
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </div>
</div>


<jsp:include page="/WEB-INF/view/components/footer.jsp"/>