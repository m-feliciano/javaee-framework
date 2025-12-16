<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("inventory", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<div class="content">
    <div class="main">
        <div class="container-narrow">
            <h2 class="mb-4">Update Inventory Item</h2>

            <form action="${baseLink}${version}${updateItem}/${inventory.id}" method="post" class="csrf-form grid-form">
                <div class="form-group">
                    <label for="inputItemId" class="form-label">ID</label>
                    <input type="text" name="id" class="form-control" id="inputItemId"
                           placeholder="ID" value="${ inventory.id }" disabled/>
                </div>

                <div class="form-group">
                    <label for="inputProductId" class="form-label">PRODUCT ID</label>
                    <input type="text" name="product.id" class="form-control" id="inputProductId"
                           disabled placeholder="Product ID" value="${inventory.getProduct().getId() }"/>
                </div>

                <div class="form-group">
                    <label for="inputQuantity" class="form-label required">QUANTITY</label>
                    <input type="number" name="quantity" class="form-control" id="inputQuantity"
                           placeholder="Quantity" value="${ inventory.quantity }" required min="1"/>
                </div>

                <div class="form-group">
                    <label for="inputDescription" class="form-label">DESCRIPTION</label>
                    <textarea name="description" class="form-control" id="inputDescription"
                              placeholder="Item description" rows="6">${inventory.description}</textarea>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <div class="grid-container grid-2-cols grid-gap-md">
                    <jsp:include page="/WEB-INF/view/components/buttons/backButton.jsp"/>
                    <jsp:include page="/WEB-INF/view/components/buttons/saveButton.jsp"/>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>