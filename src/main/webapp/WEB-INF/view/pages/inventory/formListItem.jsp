<%@ include file="/WEB-INF/routes/inventory-routes.jspf" %>
<%@ page import="com.dev.servlet.adapter.in.web.dto.IHttpResponse" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("inventory", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<div class="content">
    <div class="main" style="display: flex">
    <div style="width: 50%">
        <div>
            <div class="mb-3">
                <label for="inputItemId" class="form-label">ID</label>
                <input type="text" name="id" class="form-control" id="inputItemId"
                       placeholder="ID" value="<c:out value='${inventory.id}' escapeXml='true'/>"
                       readonly="readonly" required minlength="1"/>
            </div>

            <div class="mb-3">
                <label for="inputProductId" class="form-label">PRODUCT ID</label>
                <input type="text" name="productId" class="form-control" id="inputProductId"
                       placeholder="ID" value="<c:out value='${inventory.product.id}' escapeXml='true'/>"
                       readonly="readonly" required minlength="1"/>
            </div>

            <div class="mb-3">
                <label for="inputQuantity" class="form-label">QUANTITY</label>
                <input type="number" name="quantity" class="form-control" id="inputQuantity"
                       placeholder="quantity" value="<c:out value='${inventory.quantity}' escapeXml='true'/>"
                       readonly="readonly" required minlength="1"/>
            </div>

            <div class="mb-3">
                <label for="inputDescription" class="form-label">DESCRIPTION</label>
                <textarea name="description" class="form-control" id="inputDescription"
                          placeholder="description" required readonly rows="6"
                          minlength="4"><c:out value='${inventory.description}' escapeXml='true'/></textarea>
            </div>

            <div class="align-end">
                <jsp:include page="/WEB-INF/view/components/buttons/customButton.jsp">
                    <jsp:param name="btnLabel" value="Back"/>
                    <jsp:param name="btnType" value="button"/>
                    <jsp:param name="btnClass" value="btn btn-light"/>
                    <jsp:param name="btnIcon" value="bi bi-arrow-left"/>
                    <jsp:param name="btnOnclick"
                               value="onclick='window.location.href=`${baseLink}${version}${ listInventory }`'"/>
                    <jsp:param name="btnId" value="id='backButton'"/>
                </jsp:include>

                <span class="mr-2"></span>

                <a type="button" href="${baseLink}${version}${ editItem }/${inventory.id}" class="btn btn-success">Edit
                    <i class="bi bi-pencil-square"></i>
                </a>
            </div>
        </div>
    </div>

    <div style="padding: inherit; width: 50%; display: flex; align-items: flex-start; justify-content: center;">
        <c:if test="${ not empty inventory and not empty inventory.product and not empty inventory.product.thumbUrl }">
            <img src="${cdn}/${inventory.product.thumbUrl}"
                 alt="Product Image"
                 loading="lazy"
                 style="max-width: 400px;"/>
        </c:if>
    </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>