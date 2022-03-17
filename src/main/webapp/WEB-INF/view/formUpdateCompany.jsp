<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:formatDate value="${company.releaseDate }" pattern="yyyy-MM-dd"
	var="releaseDate" />
<fmt:formatDate value="${ today }" pattern="yyyy-MM-dd" var="calendar" />

<c:url value="/company" var="companyServlet" />

<!-- header -->
<jsp:include page="components/header.jsp" />
<div class="container">
	<form action="${ companyServlet }" method="post">
		<div class="col-md-6">
			<div class="mb-3">
				<label for="inputId" class="form-label">Company ID</label> <input
					type="text" name="id" class="form-control col-md-3" id="inputId"
					value="${ company.id }" readonly="readonly" />
			</div>
			<div class="mb-3">
				<label for="inputName" class="form-label">Company name</label> <input
					type="text" name="name" class="form-control" id="inputName"
					placeholder="Company name" value="${ company.name }"
					autocomplete="name" required minlength="4" />
			</div>
			<div class="mb-3">
				<label for="inputDate" class="form-label">Date</label> <input
					type="date" name="date" class="form-control" id="inputDate"
					min="1800-01-01" max="${ calendar }" value="${ releaseDate }"
					placeholder="Release Date" required />
			</div>
			<!-- action -->
			<input type="hidden" name="action" value="UpdateCompany">
			<button type="submit" class="btn btn-primary">Submit</button>
			<a type="button" href="${ companyServlet }?action=ListCompanies"
				class="btn btn-light">Go back</a>
		</div>
	</form>
</div>
<!-- footer -->
<jsp:include page="components/footer.jsp" />