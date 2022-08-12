<%@ page import="java.util.*,domain.Product" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="pt-BR" scope="application"/>

<c:url value="/product?action=list" var="listProducts"/>
<c:url value="/product?action=delete" var="deleteProduct"/>
<c:url value="/product?action=new" var="newProduct"/>

<fmt:formatNumber value="${product.price}" type="currency" minFractionDigits="2" var="parsedPrice"/>

<jsp:include page="../../components/header.jsp"/>
<div class="main">
    <c:if test="${ empty products }">
        <div class="d-flex flex-row-reverse">
            <a type="button" href="${ newProduct }" class="btn btn-success">New</a>
            <span class="pr-1"></span>
            <a type="button" href="${listProducts}" class="btn btn-light">Clean</a>
        </div>
        <p>No one product found.</p>
    </c:if>
    <c:if test="${ not empty products }">
        <form class="form-inline d-flex flex-row-reverse mb-2 pr-2" action="${ listProducts }" method="post">
            <div class="mb-3 form-row">
                <div class="form-row mr-2">
                    <div class="form-check col mr-2">
                        <input class="form-check-input" type="radio" name="param" id="radioName"
                               value="name" checked>
                        <label class="form-check-label" for="radioName">
                            <span id="name">Name</span>
                        </label>
                    </div>
                    <div class="form-check col">
                        <input class="form-check-input" type="radio" name="param" id="radioDescription"
                               value="description">
                        <label class="form-check-label" for="radioDescription">
                            Description
                        </label>
                    </div>
                </div>
                <div>
                    <label for="search"></label>
                    <input id="search" type="text" name="value" class="form-control" placeholder="search" required
                           minlength="1"/>
                    <button type="submit" class="btn btn-primary">Search</button>
                    <a type="button" href="${listProducts}" class="btn btn-light">Clean</a>
                    <input type="hidden" name="action" value="list">
                </div>
            </div>
        </form>
        <div class="row">
            <div class="col-12">
                <table class="table table-striped table-bordered table-hover mb-0">
                    <caption class="pb-0">${products.size()} records found</caption>
                    <thead class="thead-dark">
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">IMAGE</th>
                        <th scope="col">NAME</th>
                        <th scope="col">DESCRIPTION</th>
                        <th scope="col">PRICE</th>
                        <th scope="col">REGISTER</th>
                        <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${ products }" var="product">
                        <fmt:formatNumber value="${product.price}" type="currency" minFractionDigits="2"
                                          var="parsedPrice"/>
                        <tr>
                            <th class="w-7" scope="row">${ product.id }</th>
                            <td class="text-center w-10">
                                <a href="${ listProducts }&id=${ product.id }">
                                    <c:if test="${empty product.url }">
                                        <img class="img-square-min fit-img"
                                             src="<c:url value='/assets/no_image_available.png'/>"
                                             alt="no image available">
                                    </c:if>
                                    <c:if test="${not empty product.url }">
                                        <img class="img-square-min fit-img" src="${ product.url }"
                                             alt="Image of product ${ product.name }">
                                    </c:if>
                                </a>
                            </td>
                            <td class="w-20">
                                <div id="prod-name">${ product.name }</div>
                            </td>
                            <td class="w-25">
                                <div id="prod-desc">${ product.description }</div>
                            </td>
                            <td class="w-10">${ parsedPrice }</td>
                            <td class="w-10">${ product.registerDate }</td>
                            <td class="w-10">
                                <a type="button" href="${ listProducts }&id=${ product.id }" class="btn btn-primary">
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
                <div class="d-flex flex-row-reverse">
                    <a type="button" href="${ newProduct }" class="btn btn-success">New</a>
                </div>
            </div>
        </div>
    </c:if>
</div>
<jsp:include page="../../components/footer.jsp"/>