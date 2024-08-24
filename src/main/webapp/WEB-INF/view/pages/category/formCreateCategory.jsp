<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="main">
    <form action="${ createCategory }" method="post">
        <div class="col-md-6">
            <div class="mb-3">
                <label for="inputName" class="form-label">NAME</label>
                <input type="text" name="name" class="form-control" id="inputName" placeholder="name" required
                       minlength="4"/>
            </div>
            <!-- action -->

            <button type="submit" class="btn btn-primary">Submit</button>
            <a type="button" href="${ listCategories }" class="btn btn-light">
                Cancel
            </a>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>