<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.dev.servlet.core.response.IServletResponse" %>
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

<div class="main">
    <jsp:include page="/WEB-INF/view/components/search.jsp">
        <jsp:param name="placeholder" value="Search product"/>
        <jsp:param name="action" value="${baseLink}${version}${ searchProduct }"/>
        <jsp:param name="onclear" value="${baseLink}${version}${ listProduct }"/>
        <jsp:param name="limit" value="${ pageable.getPageSize() }"/>
        <jsp:param name="categories" value="${ categories }"/>
        <jsp:param name="searchType" value="name"/>
    </jsp:include>

    <c:if test="${ !pageable.getContent().iterator().hasNext() }">
        <p>Products not found.</p>
    </c:if>

    <c:if test="${ pageable.getContent().iterator().hasNext() }">
        <div class="row">
            <div class="col-12">
                <div class="table-responsive">
                    <table class="table table-striped table-bordered table-hover mb-0">
                        <thead class="thead-dark">
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">IMAGE</th>
                            <th scope="col">NAME</th>
                            <th scope="col">DESCRIPTION</th>
                            <th scope="col">PRICE</th>
                            <th scope="col">REGISTER</th>
                            <th scope="col"></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${ pageable.getContent() }" var="product">
                            <tr>
                                <th class="w-8" scope="row"><c:out value="${fn:substring(product.id, 0, 8)}" escapeXml="true"/></th>
                                <td class="text-center w-20">
                                    <a href="<c:url value='${baseLink}${version}${ listProduct }/${ product.id }'/>" target="_blank">
                                        <c:choose>
                                            <c:when test="${not empty product.url and product.url ne ''}">
                                                <img class="img-thumbnail img-square-min"
                                                     src="<c:out value='${product.url}' escapeXml='true'/>"
                                                     alt="<c:out value='Product ${product.name}' escapeXml='true'/>"
                                                     loading="lazy"
                                                     onerror="this.src='<c:url value='/resources/assets/no_image_available.png'/>'">
                                            </c:when>
                                            <c:otherwise>
                                                <img class="img-thumbnail img-square-min"
                                                     src="<c:url value='/resources/assets/no_image_available.png'/>"
                                                     alt="No image available">
                                            </c:otherwise>
                                        </c:choose>
                                    </a>
                                </td>
                                <td class="w-14"><c:out value="${product.name}" escapeXml="true"/></td>
                                <td class="w-20">
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
                                <td class="w-19">
                                    <a href="${baseLink}${version}${ listProduct }/${ product.id }"
                                       class="btn btn-auto btn-primary">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    <form action="${baseLink}${version}${ searchInventory }" method="get"
                                          class="d-inline">
                                        <input type="hidden" name="k" value="product"/>
                                        <input type="hidden" name="q" value="<c:out value='${product.id}' escapeXml='true'/>"/>

                                        <button type="submit" class="btn btn-auto btn-info">
                                            <i class="bi bi-search"></i>
                                        </button>
                                    </form>
                                    <form class="d-inline" action="${baseLink}${version}${ newItem }" method="get">
                                        <button type="submit" class="btn btn-auto btn-secondary">
                                            <i class="bi bi-box"></i>
                                            <input type="hidden" name="productId" hidden
                                                   value="<c:out value='${product.id}' escapeXml='true'/>"/>
                                        </button>
                                    </form>
                                    <form action="<c:url value='${baseLink}${version}${deleteProduct}/${product.id}'/>" method="post" class="d-inline csrf-delete-form">
                                        <button type="submit" class="btn btn-auto btn-danger">
                                            <i class="bi bi-trash3"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                        <caption class="pb-0 caption">${pageable.getTotalElements()} records |
                            <fmt:formatNumber value="${totalPrice}" type="currency" minFractionDigits="2"/></caption>
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

                <jsp:param name="href" value="${baseLink}${version}${listProduct}"/>
            </jsp:include>
        </div>
    </c:if>

    <div class="d-flex flex-row-reverse mb20">
        <a type="button" href="${baseLink}${version}${ newProduct }" class="btn btn-success">
            <i class="bi bi-plus-circle"></i> New
        </a>
    </div>
</div>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>