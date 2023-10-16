<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="common-imports.jsp"/>

<c:url value="/productView?action=list" var="listProducts"/>
<div class="page-wrap d-flex flex-row align-items-center bodyt">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-12 text-center">
                <span class="display-1 d-block">404</span>
                <div class="mb-4 lead">The item you are looking for was not found.</div>
                <a href="${listProducts}" class="btn btn-link">Back to Home</a>
            </div>
        </div>
    </div>
</div>