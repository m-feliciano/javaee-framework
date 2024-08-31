<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="main">
    <form>
        <div class="col-md-6">
            <div class="mb-3">
                <label for="inputId" class="form-label">ID</label>
                <input type="text" name="id" class="form-control col-md-3" id="inputId"
                       value="${ category.id }" readonly="readonly"/>
            </div>
            <div class="mb-3">
                <label for="inputName" class="form-label">NAME</label>
                <input type="text" name="name" class="form-control" id="inputName"
                       placeholder="name" value="${ category.name }" autocomplete="name" required minlength="4"
                       readonly="readonly"/>
            </div>

            <!-- action -->
            <a type="button" href="${ editCategory }/${ category.id }" class="btn btn-success">
                Edit <i class="bi bi-pencil-square"></i>
            </a>
            <a type="button" href="${ listCategories }" class="btn btn-light">
                Go back
            </a>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>