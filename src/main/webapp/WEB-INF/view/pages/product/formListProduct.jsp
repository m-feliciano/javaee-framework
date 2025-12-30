<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/routes/product-routes.jspf" %>
<%@ page import="com.servletstack.adapter.in.web.dto.IHttpResponse" %>
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
                <h1>
                    <i class="bi bi-box-seam"></i>
                    <c:out value='${ product.name }' escapeXml='true'/>
                </h1>
                <p class="action-bar-subtitle">
                    <span class="product-badge">
                        <i class="bi bi-tag"></i>
                        <c:out value='${ product.category.name }' escapeXml='true'/>
                    </span>
                </p>
            </div>
            <div class="action-buttons">
                <a href="${baseLink}${version}${listProduct}" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i>
                    Back to List
                </a>
                <a href="${baseLink}${version}${editProduct}/${product.id}" class="btn btn-primary">
                    <i class="bi bi-pencil-square"></i>
                    Edit Product
                </a>
            </div>
        </div>

        <div class="product-detail-container">
            <!-- Image Section -->
            <div class="product-image-section">
                <div class="product-image-card">
                    <div class="product-image-wrapper">
                        <c:choose>
                            <c:when test="${empty product.thumbUrl}">
                                <img src="<c:url value='/resources/assets/images/no_image_available.png'/>"
                                     alt="No image available"
                                     loading="lazy"/>
                            </c:when>
                            <c:otherwise>
                                <img src="${cdn}/${product.thumbUrl}"
                                     alt="<c:out value='${product.name}' escapeXml='true'/>"
                                     fetchpriority="high"
                                     loading="lazy"
                                     decoding="async"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Info Section -->
            <div class="product-info-section">
                <!-- Basic Information Card -->
                <div class="product-info-card">
                    <div class="card-header">
                        <h3>
                            <i class="bi bi-info-circle-fill"></i>
                            Basic Information
                        </h3>
                    </div>
                    <div class="card-body">
                        <div class="product-info-grid">
                            <div class="product-field">
                                <div class="product-field-label">
                                    <i class="bi bi-key"></i>
                                    Product ID
                                </div>
                                <div class="product-field-value readonly">
                                    <c:out value='${ product.id }' escapeXml='true'/>
                                </div>
                            </div>

                            <div class="product-field">
                                <div class="product-field-label">
                                    <i class="bi bi-calendar-event"></i>
                                    Registration Date
                                </div>
                                <div class="product-field-value readonly">
                                    <c:out value='${ product.registerDateFormatted }' escapeXml='true'/>
                                </div>
                            </div>

                            <div class="product-field">
                                <div class="product-field-label">
                                    <i class="bi bi-folder"></i>
                                    Category
                                </div>
                                <div class="product-field-value readonly">
                                    <c:out value='${ product.category.name }' escapeXml='true'/>
                                </div>
                            </div>

                            <div class="product-field">
                                <div class="product-field-label">
                                    <i class="bi bi-cash-coin"></i>
                                    Price
                                </div>
                                <div class="product-price">
                                    ${product.priceFormatted}
                                </div>
                            </div>
                        </div>

                        <c:if test="${not empty product.thumbUrl}">
                            <div class="product-field" style="margin-top: var(--spacing-5);">
                                <div class="product-field-label">
                                    <i class="bi bi-link-45deg"></i>
                                    Image URL
                                </div>
                                <div class="product-url-section">
                                    <input type="text"
                                           class="form-control"
                                           value="<c:out value='${product.thumbUrl}' escapeXml='true'/>"
                                           readonly/>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Description Card -->
                <div class="product-description-card">
                    <div class="card-header">
                        <h3>
                            <i class="bi bi-card-text"></i>
                            Product Description
                        </h3>
                    </div>
                    <div class="product-description-content">
                        <c:out value='${product.description.trim()}' escapeXml='true'/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<jsp:include page="/WEB-INF/view/components/footer.jsp"/>