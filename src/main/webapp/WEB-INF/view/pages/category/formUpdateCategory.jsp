<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="main">
    <form action="${ updateCategory }" method="post">
        <div class="col-md-6">
            <div class="mb-3">
                <label for="inputId" class="form-label">ID</label>
                <input type="text" name="id" class="form-control col-md-3" id="inputId"
                       value="${ category.id }" readonly="readonly"/>
            </div>

            <div class="mb-3">
                <label for="inputName" class="form-label">NAME</label>
                <input type="text" name="name" class="form-control" id="inputName"
                       placeholder="name" value="${ category.name }" autocomplete="name" required minlength="4"/>
            </div>

            <!-- action -->

            <button type="submit" class="btn btn-primary">Submit</button>
            <a type="button" href="${ categoryLink }?action=list&id=${category.id }" class="btn btn-light">
                Cancel
            </a>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>