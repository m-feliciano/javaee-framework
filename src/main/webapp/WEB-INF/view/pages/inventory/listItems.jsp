<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<c:set var="total" value="${0}"/>
<div class="main">
    <div class="d-flex list-inline flex-row-reverse">
        <a href="${ listInventories }" class="btn btn-light fit-content">Clear</a>

        <jsp:include page="/WEB-INF/view/components/search.jsp">
            <jsp:param name="action" value="${ listInventories }"/>
        </jsp:include>
    </div>

    <c:if test="${ empty items }">
        <p>Products not found.</p>
    </c:if>
    <c:if test="${ not empty items }">
        <div class="row">
            <div class="col-12">
                <table class="table table-striped table-bordered table-hover mb-0">
                    <thead class="thead-dark">
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">PRODUCT</th>
                        <th scope="col">QUANTITY</th>
                        <th scope="col">DESCRIPTION</th>
                        <th scope="col">PRICE</th>
                        <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${ items }" var="item">
                        <fmt:formatNumber value="${ item.getProduct().getPrice() * item.getQuantity() }"
                                          type="currency" minFractionDigits="2" var="parsedPrice"/>
                        <c:set var="total" value="${ total + item.getProduct().getPrice() * item.getQuantity() }"/>

                        <tr>
                            <th class="w-7" scope="row">${ item.id }</th>
                            <td class="w-20">
                                <a style="text-decoration: none; color: inherit;  padding: 2rem 0;"
                                   href="${ listProducts }/${ item.getProduct().getId() }"
                                   target="_blank">${ item.getProduct().getName() }</a>
                            </td>
                            <td class="w-10">${ item.quantity }</td>
                            <td class="w-25">${ item.description }</td>
                            <td class="w-10">${ parsedPrice }</td>
                            <td class="w-10">
                                <a type="button" href="${ listInventories }/${ item.id }"
                                   class="btn btn-primary">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a type="button" href="${ deleteItem }/${ item.id }"
                                   class="btn btn-danger">
                                    <i class="bi bi-trash3"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <caption class="pb-0">${items.size()} records</caption>
                    <caption class="pb-0">Total <fmt:formatNumber value="${ total }"
                                                                  type="currency" minFractionDigits="2"/></caption>
                </table>
            </div>
        </div>
    </c:if>
    <div class="d-flex flex-row-reverse mb20 mt-0">
        <a type="button" href="${ newItem }" class="btn btn-success">New</a>
    </div>
</div>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>