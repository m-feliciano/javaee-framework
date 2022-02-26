<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url value="/company" var="linkCompanyServlet" />
<!-- header -->
<jsp:include page="components/header.jsp" />
<form action="${ linkCompanyServlet }?action=create" method="post">
	<div class="col-md-6">
		<div class="mb-3">
			<label for="inputName" class="form-label">Company name</label> 
			<input type="text" name="name" 
				class="form-control" id="inputName" 
				placeholder="Company name" required minlength="4" />
		</div>
		<div class="mb-3">
			<label for="inputDate" class="form-label">Date</label> 
			<input type="date" name="date" 
				class="form-control" id="inputDate" 
				placeholder="Release Date" required />
		</div>
		<button type="submit" class="btn btn-primary">Submit</button>
	</div>
</form>
<!-- footer -->
<jsp:include page="components/footer.jsp" />