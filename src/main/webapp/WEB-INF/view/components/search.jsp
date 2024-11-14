<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>

<form class="form-inline d-flex flex-row-reverse mb20 mb-2 pr-2" action="${ action }" method="get">
    <div class="mb-3 form-row">
        <div class="form-row mr-4">
            <label for="inputCategory" hidden>Category</label>

            <select name="category" class="form-control text-center" id="inputCategory">
                <option value="" selected>All</option>

                <c:if test="${ not empty categories }">
                    <c:forEach items="${ categories }" var="cat">
                        <option value="${ cat.id }" ${ category eq cat.id ? 'selected' : '' }>${ cat.name }</option>
                    </c:forEach>
                </c:if>
            </select>
        </div>
        <div class="form-row mr-2">
            <div class="form-check col mr-2">
                <input class="form-check-input" type="radio" name="k" id="radioName"
                       value="name" ${ k eq 'name' or empty k ? 'checked' : '' }>
                <label class="form-check-label" for="radioName">
                    <span id="name">Name</span>
                </label>
            </div>
            <div class="form-check col">
                <input class="form-check-input" type="radio" name="k" id="radioDescription"
                       value="description" ${ k eq 'description' ? 'checked' : '' }>
                <label class="form-check-label" for="radioDescription">
                    Description
                </label>
            </div>
        </div>
        <div>
            <input type="text" name="q" class="form-control" id="inputSearchItem"
                   placeholder="Search" value="${q}" aria-describedby="searchHelp"
                   pattern=".{3,}" title="3 character minimum"
                   autocomplete="on" autofocus required />

            <button type="submit" class="btn btn-primary">Search</button>
        </div>
    </div>
</form>