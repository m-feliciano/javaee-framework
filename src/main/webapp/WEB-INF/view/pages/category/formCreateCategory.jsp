<jsp:include page="/WEB-INF/view/components/header.jsp"/>
<%@ include file="/WEB-INF/routes/category-routes.jspf" %>

<div class="content">
    <div class="main">
        <div class="container-narrow">
            <h2 class="mb-4">Create Category</h2>

            <form action="${baseLink}${version}${createCategory}" method="post" class="csrf-form grid-form">
                <div class="form-group">
                    <label for="inputName" class="form-label">NAME</label>
                    <input type="text" name="name" class="form-control" id="inputName"
                           placeholder="Category name" required minlength="4"/>
                </div>

                <div class="grid-container grid-2-cols grid-gap-md">
                    <jsp:include page="/WEB-INF/view/components/buttons/backButton.jsp"/>
                    <jsp:include page="/WEB-INF/view/components/buttons/saveButton.jsp"/>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>