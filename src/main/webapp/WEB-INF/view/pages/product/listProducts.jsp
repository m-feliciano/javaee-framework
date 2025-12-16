<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page import="com.dev.servlet.adapter.in.web.dto.IServletResponse" %>
<%@ include file="/WEB-INF/routes/product-routes.jspf" %>
<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    IServletResponse servletResponse = (IServletResponse) request.getAttribute("response");
    request.setAttribute("categories", servletResponse.getEntity("categories"));
    request.setAttribute("pageable", servletResponse.getEntity("pageable"));
    request.setAttribute("totalPrice", servletResponse.getEntity("totalPrice"));
%>

<title>Products</title>

<div class="content">
    <div class="main">
        <div class="action-bar">
            <div class="action-bar-title">
                <h2>Products</h2>
                <p class="action-bar-subtitle">Manage your product catalog</p>
            </div>
            <div class="action-buttons">
                <a href="${baseLink}${version}${ newProduct }" class="btn btn-success">
                    <i class="bi bi-plus-circle"></i> New Product
                </a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/view/components/search.jsp">
            <jsp:param name="placeholder" value="Search product"/>
            <jsp:param name="action" value="${baseLink}${version}${ searchProduct }"/>
            <jsp:param name="onclear" value="${baseLink}${version}${ listProduct }"/>
            <jsp:param name="limit" value="${ pageable.getPageSize() }"/>
            <jsp:param name="categories" value="${ categories }"/>
            <jsp:param name="searchType" value="name"/>
        </jsp:include>

        <c:if test="${ !pageable.getContent().iterator().hasNext() }">
            <div class="empty-state">
                <div class="empty-state-icon">
                    <i class="bi bi-box-seam"></i>
                </div>
                <h3 class="empty-state-title">No Products Found</h3>
                <p class="empty-state-description">Start adding products to your inventory.</p>
                <a href="${baseLink}${version}${ newProduct }" class="btn btn-primary">
                    <i class="bi bi-plus-circle"></i> Create Product
                </a>
            </div>
        </c:if>

        <c:if test="${ pageable.getContent().iterator().hasNext() }">
            <div class="card">
                <div class="table-responsive">
                    <table class="table table-striped table-bordered table-hover mb-0">
                        <thead class="thead-dark">
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">IMAGE</th>
                            <th scope="col">DESCRIPTION</th>
                            <th scope="col">PRICE</th>
                            <th scope="col">REGISTER</th>
                            <th scope="col">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${ pageable.getContent() }" var="product">
                            <tr>
                                <th class="w-8" scope="row">
                                    <c:out value="${fn:substring(product.id, 0, 8)}" escapeXml="true"/>
                                </th>
                                <td class="text-center w-15">
                                    <a href="<c:url value='${baseLink}${version}${ listProduct }/${ product.id }'/>" target="_blank">
                                        <c:choose>
                                            <c:when test="${not empty product.thumbUrl and product.thumbUrl ne ''}">
                                                <img src="${cdn}/${product.thumbUrl}"
                                                     class="img-thumbnail img-square-min"
                                                     alt="<c:out value='Product ${product.name}' escapeXml='true'/>"
                                                     fetchpriority="high"
                                                     loading="lazy"
                                                     decoding="async"
                                                     onerror="this.src='<c:url value='/resources/assets/no_image_available.png'/>'">
                                            </c:when>
                                            <c:otherwise>
                                                <img class="img-thumbnail img-square-min"
                                                     src="<c:url value='/resources/assets/no_image_available.png'/>"
                                                     alt="No image available">
                                            </c:otherwise>
                                        </c:choose>
                                    </a>
                                    <c:choose>
                                        <c:when test="${fn:length(product.name) > 25}">
                                            <p class="mt-1 mb-0"><c:out value="${fn:substring(product.name, 0, 25)}..." escapeXml="true"/></p>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="mt-1 mb-0"><c:out value="${product.name}" escapeXml="true"/></p>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="w-30">
                                    <c:choose>
                                        <c:when test="${fn:length(product.description) > 100}">
                                            <c:out value="${fn:substring(product.description, 0, 100)}..." escapeXml="true"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${product.description}" escapeXml="true"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="w-8">${ product.priceFormatted }</td>
                                <td class="w-10">${product.registerDateFormatted}</td>
                                <td class="w-15" style="padding: 15px">
                                    <div class="grid-container grid-auto-fit grid-gap-sm" style="grid-template-columns: repeat(auto-fit, minmax(40px, 1fr));">
                                        <a href="${baseLink}${version}${ listProduct }/${ product.id }"
                                           class="btn btn-auto btn-primary" title="View">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                        <form action="${baseLink}${version}${ searchInventory }" method="get" style="margin: 0;">
                                            <input type="hidden" name="k" value="product"/>
                                            <input type="hidden" name="q" value="<c:out value='${product.id}' escapeXml='true'/>"/>
                                            <button type="submit" class="btn btn-auto btn-info" title="Search Inventory">
                                                <i class="bi bi-search"></i>
                                            </button>
                                        </form>
                                        <form action="${baseLink}${version}${ newItem }" method="get" style="margin: 0;">
                                            <input type="hidden" name="productId" value="<c:out value='${product.id}' escapeXml='true'/>"/>
                                            <button type="submit" class="btn btn-auto btn-secondary" title="Add to Inventory">
                                                <i class="bi bi-box"></i>
                                            </button>
                                        </form>
                                        <form action="<c:url value='${baseLink}${version}${deleteProduct}/${product.id}'/>"
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
                        <caption class="pb-0 caption">${pageable.getTotalElements()} records |
                            <fmt:formatNumber value="${totalPrice}" type="currency" currencyCode="USD" minFractionDigits="2"/></caption>
                    </table>
                </div>
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
        </c:if>
    </div>
</div>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>