<%@ page import="com.dev.servlet.adapter.in.web.dto.IServletResponse" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<%@ include file="/WEB-INF/routes/product-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    IServletResponse servletResponse = (IServletResponse) request.getAttribute("response");
    request.setAttribute("categories", servletResponse.getEntity("categories"));
    request.setAttribute("items", servletResponse.getEntity("items"));
%>

<title>Inventory</title>

<div class="content">
    <div class="main">
        <div class="action-bar">
            <div class="action-bar-title">
                <h2>Inventory</h2>
                <p class="action-bar-subtitle">Manage your inventory items</p>
            </div>
            <div class="action-buttons">
                <a href="${baseLink}${version}${ newItem }" class="btn btn-success">
                    <i class="bi bi-plus-circle"></i> New Item
                </a>
            </div>
        </div>

        <jsp:include page="/WEB-INF/view/components/search.jsp">
            <jsp:param name="placeholder" value="Search inventory"/>
            <jsp:param name="action" value="${baseLink}${version}${ searchInventory }"/>
            <jsp:param name="onclear" value="${baseLink}${version}${ listInventory }"/>
            <jsp:param name="searchType" value="description"/>
            <jsp:param name="limit" value="20"/>
        </jsp:include>

        <c:if test="${ empty items }">
            <div class="empty-state">
                <div class="empty-state-icon">
                    <i class="bi bi-inbox"></i>
                </div>
                <h3 class="empty-state-title">No Inventory Items Found</h3>
                <p class="empty-state-description">Start adding items to your inventory</p>
                <a href="${baseLink}${version}${ newItem }" class="btn btn-primary">
                    <i class="bi bi-plus-circle"></i> Create Item
                </a>
            </div>
        </c:if>

        <c:if test="${ not empty items }">
            <c:set var="total" value="${0}"/>
            <div class="card">
                <div class="table-responsive">
                    <table class="table table-striped table-bordered table-hover mb-0">
                        <thead class="thead-dark">
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">PRODUCT</th>
                            <th scope="col">QUANTITY</th>
                            <th scope="col">DESCRIPTION</th>
                            <th scope="col">PRICE</th>
                            <th scope="col">ACTIONS</th>
                        </tr>
                        </thead>
                        <tbody>

                        <c:forEach items="${ items }" var="inventory">
                            <c:set var="total"
                                   value="${total + inventory.getProduct().getPrice() * inventory.getQuantity()}"/>

                            <tr>
                                <th class="w-8" scope="row"><c:out value="${fn:substring(inventory.id, 0, 8)}" escapeXml="true"/></th>
                                <td class="w-20">
                                    <a style="text-decoration: none; color: inherit;"
                                       href="${baseLink}${version}${ listProduct }/${ inventory.getProduct().getId() }"
                                       target="_blank"><c:out value="${inventory.getProduct().getName()}" escapeXml="true"/></a>
                                </td>
                                <td class="w-10">${ inventory.quantity }</td>
                                <td class="w-30"><c:out value="${inventory.description}" escapeXml="true"/></td>
                                <td class="w-10">${  inventory.getProduct().priceFormatted }</td>
                                <td class="w-10">
                                    <div class="grid-container grid-auto-fit grid-gap-sm" style="grid-template-columns: repeat(auto-fit, minmax(40px, 1fr));">
                                        <a href="${baseLink}${version}${listInventory}/${inventory.id}"
                                           class="btn btn-auto btn-primary" title="View">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                        <form action="<c:url value='${baseLink}${version}${deleteItem}/${inventory.id}'/>"
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
                        <caption class="pb-0 caption">${items.size()} records | <fmt:formatNumber value="${total}"
                                type="currency" minFractionDigits="2" currencyCode="USD"/></caption>
                    </table>
                </div>
            </div>
        </c:if>
    </div>
</div>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>