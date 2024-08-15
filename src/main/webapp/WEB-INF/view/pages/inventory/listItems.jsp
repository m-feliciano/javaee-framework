<%@ include file="../../components/common-imports.jsp" %>

<jsp:include page="../../components/header.jsp"/>

<c:set var="total" value="${0}"/>
<div class="main">
    <form class="form-inline d-flex flex-row-reverse mb-2 pr-2" action="${ listInventories }" method="post">
        <div class="mb-3 form-row">
            <div class="form-row mr-4">
                <select name="category" class="form-control text-center" id="inputCategory">
                    <option value="${null}" selected>${"All"}</option>
                    <c:if test="${ not empty categories }">
                        <c:forEach items="${ categories }" var="category">
                            <option value="${ category.id }">${ category.name }</option>
                        </c:forEach>
                    </c:if>
                </select>
            </div>
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
                <input type="text" name="value" class="form-control" id="inputSearchItem"
                       placeholder="search" required minlength="1"/>
                <input type="hidden" name="action" value="listInventoriesByParam">
                <button type="submit" class="btn btn-primary">Search</button>
                <a type="button" href="${ listInventories }" class="btn btn-light">Clean</a>
            </div>
        </div>
    </form>

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
                        <th scope="col">PRODUCT NAME</th>
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
                                   href="${ listProducts }&id=${ item.getProduct().getId() }">${ item.getProduct().getName() }</a>
                            </td>
                            <td class="w-10">${ item.quantity }</td>
                            <td class="w-25">${ item.description }</td>
                            <td class="w-10">${ parsedPrice }</td>
                            <td class="w-10">
                                <a type="button" href="${ listInventories }&id=${ item.id }"
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
                    <caption class="pb-0">${items.size()} records found</caption>
                    <caption class="pb-0">Total <fmt:formatNumber value="${ total }"
                                                                  type="currency" minFractionDigits="2"/></caption>
                </table>
            </div>
        </div>
    </c:if>
    <div class="d-flex flex-row-reverse mt-0">
        <a type="button" href="${ newItem }" class="btn btn-success">New</a>
    </div>
</div>
<jsp:include page="../../components/footer.jsp"/>