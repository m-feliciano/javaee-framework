<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="main">
    <form class="form-inline d-flex flex-row-reverse mb20 mb-2 pr-2" action="${ listProducts }" method="get">
        <div class="mb-3 form-row">
            <!-- combo category -->
            <div class="form-row mr-2">
                <c:if test="${ not empty categories }">
                    <div class="form-check col mr-2">
                        <select name="category" class="form-control text-center" id="inputCategory">
                            <option value="" selected>All</option>
                            <c:forEach items="${ categories }" var="category">
                                <option value="${ category.id }">${ category.name }</option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>
                <div class="form-check col mr-2">
                    <input class="form-check-input" type="radio" name="k" id="radioName" value="name" checked>
                    <label class="form-check-label" for="radioName">
                        Name
                    </label>
                </div>
                <div class="form-check col">
                    <input class="form-check-input" type="radio" name="k" id="radioDescription" value="description">
                    <label class="form-check-label" for="radioDescription">
                        Description
                    </label>
                </div>
            </div>
            <div>
                <label for="search"></label>
                <input id="search" type="text" name="s" class="form-control" placeholder="search" required minlength="1"/>
                <button type="submit" class="btn btn-primary">Search</button>
                <a type="button" href="${listProducts}" class="btn btn-light">Clean</a>
            </div>
        </div>
    </form>
    <c:if test="${ empty products }">
        <p>No one product found.</p>
    </c:if>

    <c:if test="${ not empty products }">
        <!-- Form/Filter list products -->
        <div class="row">
            <div class="col-12">
                <div class="table-responsive">
                    <table class="table table-striped table-bordered table-hover mb-0">
                        <caption class="pb-0">${query.getPagination().getTotalRecords()} records</caption>
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
                            <fmt:formatNumber value="${product.price}" type="currency" minFractionDigits="2" var="parsedPrice"/>
                            <tr>
                                <th class="w-5" scope="row">${ product.id }</th>
                                <td class="text-center w-8">
                                    <a href="${ listProducts }/${ product.id }" target="_blank">
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
                                <td class="w-30">
                                    <div id="prod-desc">${ product.description }</div>
                                </td>
                                <td class="w-10">${ parsedPrice }</td>
                                <td class="w-10">${ product.registerDate }</td>
                                <td class="w-10">
                                    <a type="button" href="${ listProducts }/${ product.id }" class="btn btn-primary">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    <a type="button" href="${ deleteProduct }/${ product.id }" class="btn btn-danger">
                                        <i class="bi bi-trash3"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- TODO: Componentize this -->
            <c:if test="${ query.getPagination().getTotalRecords() gt query.getPagination().getPageSize() }">
                <div class="col-12">
                    <div class="col-md-24">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <c:if test="${query.getPagination().getCurrentPage() gt 1}">
                                    <li class="page-item">
                                        <a class="page-link" style="color: #343a40;" aria-label="Previous"
                                           href="${listProducts}?page=${query.getPagination().getCurrentPage() - 1}&limit=${query.getPagination().getPageSize()}&sort=${query.getPagination().getSort().getValue()}&order=${query.getPagination().getOrder().getValue()}">
                                            <span aria-hidden="true">&laquo;</span>
                                            <span class="sr-only">Previous</span>
                                        </a>
                                    </li>
                                </c:if>

                                <c:forEach begin="1" end="${query.getPagination().getTotalPages()}" var="i">
                                    <c:choose>
                                        <c:when test="${query.getPagination().getCurrentPage() eq i}">
                                            <li class="page-item active" aria-current="page">
                                                <a class="page-link"
                                                   style="background-color: #343a40; border-color: #343a40"
                                                   disabled="true" tabindex="-1">${i}
                                                </a>
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="page-item">
                                                <a class="page-link" style="color: #343a40;"
                                                   href="${listProducts}?page=${i}&limit=${query.getPagination().getPageSize()}&sort=${query.getPagination().getSort().getValue()}&order=${query.getPagination().getOrder().getValue()}">${i}
                                                </a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${query.getPagination().getCurrentPage() lt query.getPagination().getTotalPages()}">
                                    <a class="page-link" style="color: #343a40;" aria-label="Next"
                                       href="${listProducts}?page=${query.getPagination().getCurrentPage() + 1}&limit=${query.getPagination().getPageSize()}&sort=${query.getPagination().getSort().getValue()}&order=${query.getPagination().getOrder().getValue()}">
                                        <span aria-hidden="true">&raquo;</span>
                                        <span class="sr-only">Next</span>
                                    </a>
                                </c:if>
                            </ul>
                        </nav>
                    </div>
                </div>
            </c:if>
        </div>
    </c:if>

    <div class="d-flex flex-row-reverse mb20">
        <a type="button" href="${ newProduct }" class="btn btn-success">New</a>
    </div>
</div>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>