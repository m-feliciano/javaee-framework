<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="components/header.jsp" />
<p>
	<c:if test="${ not empty companyName }">
		Company ${ companyName } successfully created.
	</c:if>
	<c:if test="${ empty companyName }">
		No one new company created.
	</c:if>
</p>
<jsp:include page="components/footer.jsp" />