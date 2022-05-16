<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url value="/product" var="linkProductServlet" />
<!-- header -->
<jsp:include page="components/header.jsp" />
<div class="main">
	<form action="${ linkProductServlet }" method="post">
		<div class="col-md-6">
			<div class="mb-3">
				<label for="inputName" class="form-label">Product name</label> 
				<input type="text" name="name" class="form-control" id="inputName" placeholder="Product name" required minlength="4" />
			</div>
			<div class="mb-3">
				<label for="inputDescription" class="form-label">Description</label> 
				<input type="text" name="description" class="form-control" id="inputDescription" placeholder="Simple Description" required />
			</div>
			<div class="mb-3">
				<label for="inputPrice" class="form-label">Price</label> 
				<input type="number" name="price" class="form-control" id="inputPrice" placeholder="Price" 
					min="0.00" max="10000" step="10" required />
			</div>
			<!-- action -->
			<input type="hidden" name="action" value="CreateProduct">
			<button type="submit" class="btn btn-primary">Submit</button>
		</div>
	</form>
</div>
<!-- footer -->
<jsp:include page="components/footer.jsp" />