<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:url value="/inventory" var="inventoryServlet"/>
<c:url value="/inventory?action=EditItem" var="editItem"/>

<fmt:setLocale value="pt-BR" scope="application"/>
<!-- header -->
<jsp:include page="../../components/header.jsp"/>
<div class="main">
    <form action="${ inventoryServlet }" method="post">
        <div class="col-md-6">
            <div class="mb-3">
                <label for="inputItemId" class="form-label">ID</label>
                <input type="number" name="id" class="form-control" id="inputItemId"
                       placeholder="ID" value="${ item.id }" readonly="readonly" required minlength="1"/>
            </div>
            <div class="mb-3">
                <label for="inputProductId" class="form-label">PRODUCT ID</label>
                <input type="number" name="productId" class="form-control" id="inputProductId"
                       placeholder="ID" value="${ item.getProduct().getId() }" readonly="readonly" required
                       minlength="1"/>
            </div>
            <div class="mb-3">
                <label for="inputQuantity" class="form-label">QUANTITY</label>
                <input type="number" name="quantity" class="form-control" id="inputQuantity"
                       placeholder="quantity" value="${ item.quantity }" readonly="readonly" required minlength="1"/>
            </div>
            <div class="mb-3">
                <label for="inputQuantity" class="form-label">DESCRIPTION</label>
                <input type="text" name="description" class="form-control" id="inputDescription"
                       placeholder="simple descripton" value="${ item.description }" readonly="readonly" required
                       minlength="4"/>
            </div>

            <!-- action -->
            <a type="button" href="${ editItem }&id=${ item.id }" class="btn btn-success">
                Edit <i class="bi bi-pencil-square"></i>
            </a>
            <a type="button" href="${ inventoryServlet }?action=ListItems" class="btn btn-light">
                Go back
            </a>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="../../components/footer.jsp"/>