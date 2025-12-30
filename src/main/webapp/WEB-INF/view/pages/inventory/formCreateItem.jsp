<%@ page import="com.servletstack.adapter.in.web.dto.IHttpResponse" %>
<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("product", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<div class="content">
    <div class="main">
        <div class="container-narrow">
            <h2 class="mb-4">Create Inventory Item</h2>

            <div class="grid-container" style="grid-template-columns: 1fr auto; gap: var(--spacing-6); align-items: start;">
                <form action="${baseLink}${version}${ createItem }" method="post" class="csrf-form grid-form">
                    <c:if test="${ not empty product and not empty product.id }">
                        <div class="form-group">
                            <label for="inputProductId" class="form-label">PRODUCT ID</label>
                            <input type="text" name="productId" class="form-control" id="inputProductId"
                                   value="<c:out value='${product.id}' escapeXml='true'/>" placeholder="ID" readonly/>
                        </div>
                        <div class="form-group">
                            <label for="inputProductName" class="form-label">PRODUCT NAME</label>
                            <input type="text" name="productName" class="form-control" id="inputProductName"
                                   value="<c:out value='${product.name}' escapeXml='true'/>" placeholder="name" disabled/>
                        </div>
                    </c:if>
                    <c:if test="${ empty product }">
                        <div class="form-group">
                            <label for="inputProductId" class="form-label">PRODUCT ID</label>
                            <input type="text" name="productId" class="form-control" id="inputProductId"
                                   placeholder="Product ID" required minlength="1"/>
                        </div>
                    </c:if>
                    <div class="form-group">
                        <label for="inputQuantity" class="form-label">QUANTITY</label>
                        <input type="number" name="quantity" class="form-control" id="inputQuantity"
                               placeholder="Quantity" required minlength="1"/>
                    </div>
                    <div class="form-group">
                        <label for="inputDescription" class="form-label">DESCRIPTION</label>
                        <input type="text" name="description" class="form-control" id="inputDescription"
                               placeholder="Item description"/>
                    </div>

                    <div class="grid-container grid-2-cols grid-gap-md">
                        <jsp:include page="/WEB-INF/view/components/buttons/backButton.jsp"/>
                        <jsp:include page="/WEB-INF/view/components/buttons/saveButton.jsp"/>
                    </div>
                </form>

                <c:if test="${ not empty product and not empty product.thumbUrl }">
                    <div class="card" style="max-width: 400px;">
                        <div class="card-body text-center">
                            <img src="${cdn}/${product.thumbUrl}"
                                 class="img-fluid"
                                 loading="lazy"
                                 style="max-width: 300px"
                                 alt="Product Image"/>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>