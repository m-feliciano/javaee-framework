<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url value="/category" var="linkCategoryServlet" />
<!-- header -->
<jsp:include page="../../components/header.jsp" />
<div class="main">
	<form action="${ linkCategoryServlet }" method="post">
		<div class="col-md-6">
			<div class="mb-3">
				<label for="inputName" class="form-label">NAME</label> 
				<input type="text" name="name" class="form-control" id="inputName" placeholder="name" required minlength="4" />
			</div>
			<!-- action -->
			<input type="hidden" name="action" value="CreateCategory">
			<button type="submit" class="btn btn-primary">Submit</button>
			<a type="button" href="${ linkCategoryServlet }?action=ListCategories" class="btn btn-light">
				Cancel
			</a>
		</div>
	</form>
</div>
<!-- footer -->
<jsp:include page="../../components/footer.jsp" />