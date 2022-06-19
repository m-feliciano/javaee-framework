<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:url value="/product" var="productServlet" />
<c:url value="/product?action=EditProduct" var="editProduct" />
<fmt:setLocale value="pt-BR" scope="application"/>
<fmt:formatDate value="${ today }" pattern="yyyy-MM-dd" var="calendar" />
<fmt:formatDate value="${product.registerDate }" pattern="dd/MM/yyyy" var="releaseDate" />
<fmt:formatNumber value = "${product.price}" type = "currency" minFractionDigits="2" var="parsedNumber"/>
<!-- header -->
<jsp:include page="../../components/header.jsp" />
<div class="main">
	<form action="${ productServlet }" method="post">
		<div class="col-md-6">
			<div class="mb-3">
				<label for="inputId" class="form-label">ID</label> 
				<input type="text" name="id" class="form-control col-md-3" id="inputId"
					value="${ product.id }" readonly="readonly" />
			</div>
			<div class="mb-3">
				<label for="inputRegisterDate" class="form-label">REGISTER</label> 
				<input type="text" name="description" class="form-control" id="inputRegisterDate" 
					value="${ releaseDate }" required readonly="readonly"/>
			</div>
			<c:if test="${ not empty product.url }">
				<div class="mb-3"  style="text-align: center;">
					<img style="height: 400px; border: 1px solid #e0e0e0; box-shadow: 0 4px 4px -2px #989898;" src="${ product.url }" alt="">
				</div>
			</c:if>
			
			<div class="mb-3">
				<label for="inputName" class="form-label">NAME</label> 
				<input type="text" name="name" class="form-control" id="inputName"
					placeholder="Product name" value="${ product.name }" autocomplete="name" required minlength="4" readonly="readonly"/>
			</div>
			<div class="mb-3">
				<label for="inputDescription" class="form-label">DESCRIPTION</label> 
				<input type="text" name="description" class="form-control" id="inputDescription" 
					placeholder="Simple Description" value="${ product.description }" required readonly="readonly"/>
			</div>
			<div class="mb-3">
				<label for="inputPrice" class="form-label">PRICE</label> 			
				<input name="price" class="form-control" id="inputPrice"
					placeholder="price" value="${parsedNumber}" required readonly="readonly"/>
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
<jsp:include page="../../components/footer.jsp" />