<%@ page import="com.dev.servlet.core.response.IHttpResponse" %>
<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("productId", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<div class="main">
    <form action="${baseLink}${version}${ createItem }" method="post" class="csrf-form">
        <div class="col-md-6">
            <div class="mb-3">
                <label for="inputProductId" class="form-label">PRODUCT ID</label>
                <input type="text" name="productId" class="form-control" id="inputProductId"
                       value="<c:out value='${productId}' escapeXml='true'/>"
                       placeholder="ID" required minlength="1"/>
            </div>
            <div class="mb-3">
                <label for="inputQuantity" class="form-label">QUANTITY</label>
                <input type="number" name="quantity" class="form-control" id="inputQuantity"
                       placeholder="quantity" required minlength="1"/>
            </div>
            <div class="mb-3">
                <label for="inputDescription" class="form-label">DESCRIPTION</label>
                <input type="text" name="description" class="form-control" id="inputDescription"
                       placeholder="simple descripton"/>
            </div>

            <jsp:include page="/WEB-INF/view/components/buttons/backButton.jsp"/>
            <span class="mr-2"></span>
            <jsp:include page="/WEB-INF/view/components/buttons/saveButton.jsp"/>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>