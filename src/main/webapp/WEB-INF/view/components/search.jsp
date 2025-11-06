<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form class="form-inline d-flex flex-row-reverse mb20 pr-2 justify-center" action="${ param.action }" method="get">
    <div class="mb-3 form-row">
        <c:if test="${param.showCategory ne 'false'}">
            <div class="form-row mr-4">
                <label for="inputCategory" hidden>Category</label>
                <select name="category" class="form-control text-center" id="inputCategory">
                    <option value="" selected>All</option>
                    <c:if test="${ not empty categories }">
                        <c:forEach items="${ categories }" var="cat">
                            <option value="${ cat.id }" ${ category eq cat.id ? 'selected' : '' }>${cat.name}</option>
                        </c:forEach>
                    </c:if>
                </select>
            </div>
        </c:if>
        <c:if test="${param.showFilters ne 'false'}">
            <div class="form-row mr-2">
                <div class="form-check col mr-2">
                    <input class="form-check-input" type="radio" name="k" id="radioName"
                           value="name" ${param.k eq 'name' or empty param.k ? 'checked' : ''}>
                    <label class="form-check-label" for="radioName">
                        <span id="name">Name</span>
                    </label>
                </div>
                <div class="form-check col">
                    <input class="form-check-input" type="radio" name="k" id="radioDescription"
                           value="description" ${param.k eq 'description' ? 'checked' : ''}>
                    <label class="form-check-label" for="radioDescription">
                        Description
                    </label>
                </div>
            </div>
        </c:if>
        <div>
            <input type="text" name="q" class="form-control" id="inputSearchItem"
                   placeholder="${param.placeholder}" value="${empty param.k ? '' : param.q}"
                   aria-label="Search" aria-describedby="searchHelp"
                   pattern=".{3,}" title="3 character minimum"
                   style="width: 300px;" autocomplete="on" autofocus required/>

            <label hidden for="limit">limit</label>
            <input hidden name="limit" id="limit" value="${param.limit}">

            <c:if test="${param.showFilters eq 'false'}">
                <input type="hidden" name="k" value="name">
            </c:if>
            <c:if test="${not empty param.sort}">
                <input type="hidden" name="sort" value="${param.sort}">
            </c:if>

            <c:if test="${not empty param.order}">
                <input type="hidden" name="order" value="${param.order}">
            </c:if>

            <jsp:include page="/WEB-INF/view/components/buttons/customButton.jsp">
                <jsp:param name="btnIcon" value="bi bi-search"/>
                <jsp:param name="btnClass" value="btn btn-primary"/>
                <jsp:param name="btnLabel" value="Search"/>
                <jsp:param name="btnType" value="submit"/>
            </jsp:include>

            <a href="${ param.onclear }" class="btn btn-light">
                <i class="bi bi-x-circle"></i> Clear
            </a>
        </div>
    </div>
</form>