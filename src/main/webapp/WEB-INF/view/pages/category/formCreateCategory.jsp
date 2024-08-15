<%@ include file="../../components/common-imports.jsp" %>
<jsp:include page="../../components/header.jsp"/>

<div class="main">
    <form action="${ categoryLink }" method="post">
        <div class="col-md-6">
            <div class="mb-3">
                <label for="inputName" class="form-label">NAME</label>
                <input type="text" name="name" class="form-control" id="inputName" placeholder="name" required
                       minlength="4"/>
            </div>
            <!-- action -->
            <input type="hidden" name="action" value="create">
            <button type="submit" class="btn btn-primary">Submit</button>
            <a type="button" href="${ listCategories }" class="btn btn-light">
                Cancel
            </a>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="../../components/footer.jsp"/>