<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="java.util.*,entities.Product"%>

<c:url value="/product?action=ListProduct" var="listProduct" />
<c:url value="/product?action=DeleteProduct" var="deleteProduct" />
<c:url value="/product?action=NewProduct" var="newProduct" />
<fmt:setLocale value="pt-BR" scope="application"/>

<jsp:include page="../../components/header.jsp" />
<div class="main">
	<div class="d-flex flex-row-reverse mb-2">
		<a type="button" href="${ newProduct }" class="btn btn btn-success">New</a>
	</div>
	<c:if test="${ empty products }">
		<p>No one new product created.</p>
	</c:if>
	<c:if test="${ not empty products }">
		<div class="row">
			<div class="col-12">
				<table class="table table-striped table-bordered table-hover">
					<caption>${products.size()} records found</caption>
					<thead class="thead-dark">
						<tr>
							<th scope="col">#</th>
							<th scope="col">NAME</th>
							<th scope="col">DESCRIPTION</th>
							<th scope="col">PRICE</th>
							<th scope="col">REGISTER</th>
							<th scope="col">ACTIONS</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${ products }" var="product">	
							<fmt:formatDate value="${product.registerDate }" pattern="dd/MM/yyyy" var="releaseDate" />		
							<fmt:formatNumber value = "${product.price}" type = "currency" minFractionDigits="2" var="parsedPrice"/>
							<tr>
								<th width="10%" scope="row">${ product.id }</th>
								<td width=25% >${ product.name }</td>
								<td width=30%>${ product.description }</td>
								<td width=10%>${ parsedPrice }</td>							
								<td width=10%>${ releaseDate }</td>
								<td width=15%>
									<a type="button" href="${ listProduct }&id=${ product.id }" class="btn btn-primary">
										<i class="bi bi-eye"></i>
									</a> 
									<a type="button" href="${ deleteProduct }&id=${ product.id }" class="btn btn-danger">
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
<jsp:include page="../../components/footer.jsp" />