<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="main">
    <form action="${ createItem }" method="post">
        <div class="col-md-6">
            <div class="mb-3">
                <label for="inputProductId" class="form-label">PRODUCT ID</label>
                <input type="number" name="productId" class="form-control" id="inputProductId"
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
                       placeholder="simple descripton" required minlength="4"/>
            </div>
            <button type="submit" class="btn btn-primary">Save</button>
            <a type="button" href="${ inventoryLink }?action=list" class="btn btn-light">
                Cancel
            </a>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>