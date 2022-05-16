<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:formatDate value="${product.registerDate }" pattern="yyyy-MM-dd" var="releaseDate" />
<fmt:formatDate value="${ today }" pattern="yyyy-MM-dd" var="calendar" />

<c:url value="/product" var="productServlet" />
<c:url value="/product?action=EditProduct" var="editProduct" />
<!-- header -->
<jsp:include page="components/header.jsp" />
<div class="main">
	<form action="${ productServlet }" method="post">
		<div class="col-md-6">
			<div class="mb-3">
				<label for="inputId" class="form-label">Product ID</label> 
				<input type="text" name="id" class="form-control col-md-3" id="inputId"
					value="${ product.id }" readonly="readonly" />
			</div>
			<div class="mb-3">
				<label for="inputName" class="form-label">Product name</label> 
				<input type="text" name="name" class="form-control" id="inputName"
					placeholder="Product name" value="${ product.name }" autocomplete="name" required minlength="4" readonly="readonly"/>
			</div>
			<div class="mb-3">
				<label for="inputDescription" class="form-label">Product Description</label> 
				<input type="text" name="description" class="form-control" id="inputDescription" 
					placeholder="Simple Description" value="${ product.description }" required readonly="readonly"/>
			</div>
			<div class="mb-3">
				<label for="inputPrice" class="form-label">Product Price</label> 
				<input type="number" name="price" class="form-control" id="inputPrice" 
					placeholder="Price" value="${ product.price }" required readonly="readonly"/>
			</div>
			<!-- action -->
			<a type="button" href="${ editProduct }&id=${ product.id }" class="btn btn-success">
				Edit <i class="bi bi-pencil-square"></i>
			</a>
			<a type="button" href="${ productServlet }?action=ListProducts" class="btn btn-light" >
				Go back
			</a>
		</div>
	</form>
</div>
<!-- footer -->
<jsp:include page="components/footer.jsp" />