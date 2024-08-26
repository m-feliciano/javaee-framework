<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="main">
    <form class="form-inline d-flex flex-row-reverse mb-2 pr-2" action="${ listProducts }" method="post">
        <div class="mb-3 form-row">
            <!-- combo category -->
            <div class="form-row mr-2">
                <c:if test="${ not empty categories }">
                    <div class="form-check col mr-2">
                        <select name="category" class="form-control text-center" id="inputCategory">
                            <option value="${ null }" selected>All</option>
                            <c:forEach items="${ categories }" var="category">
                                <option value="${ category.id }">${ category.name }</option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>
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
                        <caption class="pb-0">${pagination.totalRecords} records</caption>
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
                                    <a href="${ listProducts }&id=${ product.id }" target="_blank">
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
                </div>
            </div>

            <!-- TODO: Componentize this -->
            <c:if test="${ pagination.totalRecords gt pagination.pageSize }">
                <div class="col-12">
                    <div class="col-md-24">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <c:if test="${pagination.currentPage != 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="${listProducts}&page=${pagination.currentPage - 1}"
                                           style="color: #343a40;" aria-label="Previous">
                                            <span aria-hidden="true">&laquo;</span>
                                            <span class="sr-only">Previous</span>
                                        </a>
                                    </li>
                                </c:if>

                                <c:forEach begin="1" end="${pagination.getTotalPages()}" var="i">
                                    <c:choose>
                                        <c:when test="${pagination.currentPage eq i}">
                                            <li class="page-item active" aria-current="page">
                                                <a class="page-link" style="background-color: #343a40; border-color: #343a40"
                                                   disabled="true" tabindex="-1">${i}</a>
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="page-item">
                                                <a class="page-link" style="color: #343a40;" href="${listProducts}&page=${i}">${i}</a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${pagination.currentPage lt pagination.getTotalPages()}">
                                    <a class="page-link" href="${listProducts}&page=${pagination.currentPage + 1}"
                                       style="color: #343a40;" aria-label="Next">
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

    <div class="d-flex flex-row-reverse">
        <a type="button" href="${ newProduct }" class="btn btn-success">New</a>
    </div>
</div>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>