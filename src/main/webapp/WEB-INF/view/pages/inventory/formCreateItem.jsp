<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url value="/inventory" var="linkInventoryServlet" />
<!-- header -->
<jsp:include page="../../components/header.jsp" />
<div class="main">
	<form action="${ linkInventoryServlet }" method="post">
		<div class="col-md-6">
			<div class="mb-3">
				<label for="inputProductId" class="form-label">PRODUCT ID</label> 
				<input type="number" name="productId" class="form-control" id="inputProductId" 
					placeholder="ID" required minlength="1" />
			</div>
			<div class="mb-3">
				<label for="inputCategoryId" class="form-label">CATEGORY ID</label> 
				<input type="number" name="categoryId" class="form-control" id="inputCategoryId" 
					placeholder="ID" required minlength="1" />
			</div>
			<div class="mb-3">
				<label for="inputQuantity" class="form-label">QUANTITY</label> 
				<input type="number" name="quantity" class="form-control" id="inputQuantity" 
					placeholder="quantity" required minlength="1" />
			</div>
			<div class="mb-3">
				<label for="inputDescription" class="form-label">DESCRIPTION</label> 
				<input type="text" name="description" class="form-control" id="inputDescription" 
					placeholder="simple descripton" required minlength="4" />
			</div>
			<!-- action -->
			<input type="hidden" name="action" value="CreateItem">
			<button type="submit" class="btn btn-primary">Submit</button>
			<a type="button" href="${ linkInventoryServlet }?action=ListItems" class="btn btn-light">
				Cancel
			</a>
		</div>
	</form>
</div>
<!-- footer -->
<jsp:include page="../../components/footer.jsp" />