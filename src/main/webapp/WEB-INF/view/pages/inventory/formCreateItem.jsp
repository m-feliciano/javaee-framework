<%@ page import="com.dev.servlet.web.response.IHttpResponse" %>
<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("product", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<div class="main" style="display: flex">
    <div style="width: 50%">
        <form action="${baseLink}${version}${ createItem }" method="post" class="csrf-form">
            <c:if test="${ not empty product and not empty product.id }">
                <div class="mb-3">
                    <label for="inputProductId" class="form-label">PRODUCT ID</label>
                    <input type="text" name="productId" class="form-control" id="inputProductId"
                           value="<c:out value='${product.id}' escapeXml='true'/>" placeholder="ID" readonly/>
                </div>
                <div class="mb-3">
                    <label for="inputProductName" class="form-label">NAME</label>
                    <input type="text" name="productName" class="form-control" id="inputProductName"
                           value="<c:out value='${product.name}' escapeXml='true'/>" placeholder="name" disabled/>
                </div>
            </c:if>
            <c:if test="${ empty product }">
                <div class="mb-3">
                    <label for="inputProductId" class="form-label">PRODUCT ID</label>
                    <input type="text" name="productId" class="form-control" id="inputProductId"
                           placeholder="product id" required minlength="1"/>
                </div>
            </c:if>
            <div class="mb-3">
                <label for="inputQuantity" class="form-label">QUANTITY</label>
                <input type="number" name="quantity" class="form-control" id="inputQuantity"
                       placeholder="quantity" required minlength="1"/>
            </div>
            <div class="mb-5">
                <label for="inputDescription" class="form-label">DESCRIPTION</label>
                <input type="text" name="description" class="form-control" id="inputDescription"
                       placeholder="simple descripton"/>
            </div>

            <jsp:include page="/WEB-INF/view/components/buttons/backButton.jsp"/>
            <span class="mr-2"></span>
            <jsp:include page="/WEB-INF/view/components/buttons/saveButton.jsp"/>
        </form>
    </div>
    <div style="padding: inherit">
        <c:if test="${ not empty product and not empty product.url }">
            <img src="<c:out value='${product.url}' escapeXml='true'/>" alt="Product Image"
                 style="max-width: 300px; max-height: 300px;"/>
        </c:if>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>