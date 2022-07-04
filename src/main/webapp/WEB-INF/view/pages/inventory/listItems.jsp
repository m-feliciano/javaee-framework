<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.*,domain.Inventory" %>

<c:url value="/inventory?action=ListItem" var="listItem"/>
<c:url value="/inventory?action=DeleteItem" var="deleteItem"/>
<c:url value="/inventory?action=NewItem" var="newItem"/>
<c:url value="/product?action=ListProduct" var="listProduct"/>
<%--<c:url value="/category?action=ListCategory" var="listCategory"/>--%>
<c:url value="/inventory?action=ListItemsByName" var="ListItemsByName"/>


<fmt:setLocale value="pt-BR" scope="application"/>

<jsp:include page="../../components/header.jsp"/>
<div class="main">
    <c:if test="${ empty items }">
        <p>Products not found.</p>
    </c:if>
    <c:if test="${ not empty items }">
        <form class="form-inline d-flex flex-row-reverse mb-2" action="${ ListItemsByName }" method="post">
            <div class="mb-3">
                <input type="text" name="name" class="form-control" id="inputSearchItem"
                       placeholder="name" required minlength="1"/>
                <input type="hidden" name="action" value="ListItemsByName">
                <button type="submit" class="btn btn-primary">Search</button>
                <a type="button"
                   href="${ listItems }?action=ListItems"
                   class="btn btn-light">Clean</a>
            </div>
        </form>
        <div class="row">
            <div class="col-12">
                <table class="table table-striped table-bordered table-hover mb-0">
                    <caption class="pb-0">${items.size()} records found</caption>
                    <thead class="thead-dark">
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">PRODUCT NAME</th>
                            <%--                        <th scope="col">CATEGORY</th>--%>
                            <%--                        <th scope="col">CATEGORY NAME</th>--%>
                        <th scope="col">QUANTITY</th>
                        <th scope="col">DESCRIPTION</th>
                        <th scope="col">PRICE</th>
                        <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${ items }" var="item">
                        <fmt:formatNumber value="${ item.price }" type="currency" minFractionDigits="2"
                                          var="parsedPrice"/>
                        <tr>
                            <th width="10%" scope="row">${ item.id }</th>
                            <td width="20%">
                                <a style="text-decoration: none; color: inherit;  padding: 2rem 0;"
                                   href="${ listProduct }&id=${ item.getProduct().getId() }">${ item.getProduct().getName() }</a>
                            </td>
                                <%--                            <td width="10%">${ listProduct.category.id }</td>--%>
                                <%--                            <td width="15%">--%>
                                <%--                                <a style="text-decoration: none; color: inherit; padding: 2rem 0;"--%>
                                <%--                                   href="${ listCategory }&id=${ item.categoryId }">${ item.categoryName }</a>--%>
                                <%--                            </td>--%>
                            <td width="10%">${ item.quantity }</td>
                            <td width="25%">${ item.description }</td>
                            <td width="10%">${ parsedPrice }</td>
                            <td width="15%">
                                <a type="button" href="${ listItem }&id=${ item.id }"
                                   class="btn btn-primary">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a type="button" href="${ deleteItem }&id=${ item.id }"
                                   class="btn btn-danger">
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
    <div class="d-flex flex-row-reverse mt-0">
        <a type="button" href="${ newItem }" class="btn btn-success">New</a>
    </div>
</div>
<jsp:include page="../../components/footer.jsp"/>