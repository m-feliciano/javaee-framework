<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("inventory", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<div class="content">
    <div class="main">
        <div class="container-narrow">
            <div class="action-bar">
                <div class="action-bar-title">
                    <h2>Inventory Item Details</h2>
                    <p class="action-bar-subtitle">View item information</p>
                </div>
                <div class="action-buttons">
                    <a href="${baseLink}${version}${ listInventory }" class="btn btn-secondary">
                        <i class="bi bi-arrow-left"></i> Back
                    </a>
                    <a href="${baseLink}${version}${ editItem }/${inventory.id}" class="btn btn-success">
                        <i class="bi bi-pencil-square"></i> Edit
                    </a>
                </div>
            </div>

            <div class="grid-container" style="grid-template-columns: 1fr auto; gap: var(--spacing-6); align-items: start;">
                <div class="card">
                    <div class="card-body">
                        <div class="grid-form">
                            <div class="form-group">
                                <label for="inputItemId" class="form-label">ID</label>
                                <input type="text" name="id" class="form-control" id="inputItemId"
                                       value="<c:out value='${inventory.id}' escapeXml='true'/>" readonly/>
                            </div>

                            <div class="form-group">
                                <label for="inputProductId" class="form-label">PRODUCT ID</label>
                                <input type="text" name="productId" class="form-control" id="inputProductId"
                                       value="<c:out value='${inventory.product.id}' escapeXml='true'/>" readonly/>
                            </div>

                            <div class="form-group">
                                <label for="inputQuantity" class="form-label">QUANTITY</label>
                                <input type="number" name="quantity" class="form-control" id="inputQuantity"
                                       value="<c:out value='${inventory.quantity}' escapeXml='true'/>" readonly/>
                            </div>

                            <div class="form-group">
                                <label for="inputDescription" class="form-label">DESCRIPTION</label>
                                <textarea name="description" class="form-control" id="inputDescription"
                                          readonly rows="6"><c:out value='${inventory.description}' escapeXml='true'/></textarea>
                            </div>
                        </div>
                    </div>
                </div>

                <c:if test="${ not empty inventory and not empty inventory.product and not empty inventory.product.thumbUrl }">
                    <div class="card" style="max-width: 400px;">
                        <div class="card-body text-center">
                            <img src="${cdn}/${inventory.product.thumbUrl}"
                                 alt="Product Image"
                                 loading="lazy"
                                 style="max-width: 300px;"
                                 class="img-fluid"/>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>