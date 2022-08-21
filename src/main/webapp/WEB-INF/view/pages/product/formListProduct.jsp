<%@ page import="java.util.*,domain.Product" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:url value="/product" var="productLink"/>
<c:url value="/product?action=list" var="listProducts"/>
<c:url value="/product?action=edit" var="editProduct"/>

<fmt:setLocale value="pt-BR" scope="application"/>
<fmt:formatNumber value="${product.price}" type="currency" minFractionDigits="2" var="parsedNumber"/>
<fmt:parseDate value="${product.registerDate}" type="date" pattern="yyyy-MM-dd" var="parsedDate"/>
<fmt:formatDate value="${parsedDate}" type="date" pattern="dd/MM/yyyy" var="stdDate"/>

<jsp:include page="../../components/header.jsp"/>
<div class="main">
    <form action="${ productLink }" method="post">
        <div class="col-md-6">
            <div class="mb-3">
                <div class="row">
                    <div class="col-md-6">
                        <label for="inputId" class="form-label">ID</label>
                        <input type="text" name="id" class="form-control text-right" id="inputId"
                               value="${ product.id }" readonly="readonly"/>
                    </div>
                    <div class="col-md-6">
                        <label for="inputRegisterDate" class="form-label">REGISTER</label>
                        <input type="text" name="register" class="form-control text-right" id="inputRegisterDate"
                               value="${ stdDate }" required readonly="readonly"/>
                    </div>
                </div>
            </div>
            <c:if test="${ not empty product.url }">
                <div class="text-center p-3">
                    <img class="img-square fit-img" src="${ product.url }" alt="Image of product ${ product.name }">
                </div>
            </c:if>

            <div class="mb-3">
                <label for="inputName" class="form-label">NAME</label>
                <input type="text" name="name" class="form-control" id="inputName"
                       placeholder="Product name" value="${ product.name }" autocomplete="name"
                       minlength="4" readonly="readonly" required/>
            </div>
            <div class="mb-3">
                <label for="inputDescription" class="form-label">DESCRIPTION</label>
                <textarea name="description" class="form-control" id="inputDescription"
                          placeholder="Simple Description" readonly="readonly" rows="4" cols="auto"
                          required>${ product.description }</textarea>
            </div>
            <div class="mb-4">
                <div class="row justify-content-end">
                    <div class="col-md-6">
                        <label for="inputCategory" class="form-label">CATEGORY</label>
                        <input type="text" name="category" class="form-control text-center" id="inputCategory"
                               value="${ product.category.name }" autocomplete="name" readonly required/>
                    </div>
                    <div class="col-md-6">
                        <label for="inputPrice" class="form-label">PRICE</label>
                        <input name="price" class="form-control text-right" id="inputPrice"
                               placeholder="price" value="${parsedNumber}" required readonly="readonly"/>
                    </div>
                </div>
            </div>
            <!-- action -->
            <div class="row justify-content-end mr-0">
                <a type="button" href="${ editProduct }&id=${ product.id }" class="btn btn-success mr-2">
                    Edit <i class="bi bi-pencil-square"></i>
                </a>
                <a type="button" href="${ listProducts }" class="btn btn-light">Go back</a>
            </div>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="../../components/footer.jsp"/>