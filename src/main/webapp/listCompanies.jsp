<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="java.util.*, servlets.entities.Company,infra.Storage"%>

<c:url value="/listCompany" var="editCompanyServlet" />
<c:url value="/deleteCompany" var="deleteCompanyServlet" />

<jsp:include page="components/header.jsp" />
<div class="title">
	<h3>Companies</h3>
</div>
<div class="container">
	<c:if test="${ empty companies }">
		<p>No one new company created.</p>
	</c:if>
	<c:if test="${ not empty companies }">
  	<div class="row">
	    <div class="col-12">
	      <table class="table table-hover">
	      <caption>List of companies</caption>
	        <thead class="thead-dark">
	          <tr>
	            <th scope="col">#</th>
	            <th scope="col">Name</th>
	            <th scope="col">Release</th>
	            <th scope="col">Actions</th>
	          </tr>
	        </thead>
	        <tbody>
	        <c:forEach items="${ companies }" var="company">
	        <fmt:formatDate value="${company.releaseDate }" pattern="dd/MM/yyyy" var="releaseDate" />
	          <tr>
	            <th width="10%" scope="row">${ company.id }</th>
	            <td width=30%>${ company.name }</td>
	            <td width=30%>${ releaseDate }</td>
	            <td width=20%>
	              	<a type="button" class="btn btn-primary"><i class="bi bi-eye"></i></a>
              		<a type="button" href="${ editCompanyServlet }?id=${ company.id }" class="btn btn-success"><i class="bi bi-pencil-square"></i></a>
            		<a type="button" href="${ deleteCompanyServlet }?id=${ company.id }" class="btn btn-danger"><i class="bi bi-trash3"></i></a>
	            </td>
	          </tr>
	          </c:forEach>
	        </tbody>
	      </table>
	    </div>
	</div>
	</c:if>
</div>
<jsp:include page="components/footer.jsp" />