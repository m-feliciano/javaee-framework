<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url value="/company?action=listAll" var="companyServlet" />
<!-- header -->
<jsp:include page="components/header.jsp" />
<div class="container">
	<div class="title">
		<h3>Welcome</h3>
	</div>
	<div class="row">
		<a type="button" href="${ companyServlet }" class="btn btn-info">See companies</a>
	</div>
</div>
<!-- footer -->
<jsp:include page="components/footer.jsp" />