<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page
	import="java.util.*, servlets.entities.Company, servlets.Storage"%>

<jsp:include page="components/header.jsp" />

<h3>Companies:</h3>

<ul>
	<c:forEach items="${companies}" var="company">
	<fmt:formatDate value="${company.releaseDate }" pattern="dd/MM/yyyy" var="releaseDate"/>
		<li>${company.name} - ${ releaseDate }</li>
	</c:forEach>
</ul>
<jsp:include page="components/footer.jsp" />