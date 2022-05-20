<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="java.util.*,entities.Category"%>

<c:url value="/category?action=ListCategory" var="listCategory" />
<c:url value="/category?action=DeleteCategory" var="deleteCategory" />
<c:url value="/category?action=NewCategory" var="newCategory" />
<fmt:setLocale value="pt-BR" scope="application"/>

<jsp:include page="../components/header.jsp" />
<div class="main">
	<div class="d-flex flex-row-reverse mb-2">
		<a type="button" href="${ newCategory }" class="btn btn-dark">Add category</a>
	</div>
	<c:if test="${ empty categories }">
		<p>No one new category created.</p>
	</c:if>
	<c:if test="${ not empty categories }">
		<div class="row">
			<div class="col-12">
				<table class="table table-striped table-bordered table-hover">
					<caption>List of categories</caption>
					<thead class="thead-dark">
						<tr>
							<th scope="col">#</th>
							<th scope="col">Name</th>
							<th scope="col">Actions</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${ categories }" var="category">	
							<tr>
								<th width="25%" scope="row">${ category.id }</th>
								<td width=50%>${ category.name }</td>
												
								<td width=25%>
									<a type="button" href="${ listCategory }&id=${ category.id }" class="btn btn-primary">
										<i class="bi bi-eye"></i>
									</a> 
									<a type="button" href="${ deleteCategory }&id=${ category.id }" class="btn btn-danger">
										<i class="bi bi-trash3"></i>
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
</div>
<jsp:include page="../components/footer.jsp" />