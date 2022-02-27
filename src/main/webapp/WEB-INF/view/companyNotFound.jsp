<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
<link rel="stylesheet" href="<c:url value='/css/styles.css'/>">

<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<c:url value="/company?action=ListCompanies" var="listCompany" />
<div class="page-wrap d-flex flex-row align-items-center bodyt">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-12 text-center">
                <span class="display-1 d-block">404</span>
                <div class="mb-4 lead">The page you are looking for was not found.</div>
                <a href="${listCompany}" class="btn btn-link">Back to Home</a>
            </div>
        </div>
    </div>
</div>

<!-- 
<div class="d-flex justify-content-center" style="height: 18rem; align-items: center">
	<div class="align-items-center">
		<h3>PAGE NOT FOUND</h3>
	</div>
</div>
 -->