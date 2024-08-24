<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>

<!DOCTYPE html>
<html lang="en">
<%@ include file="/WEB-INF/jspf/head-loginForm.jspf" %> <!-- This is a fragment -->
<title>404 Not Found</title>
<body>
    <div class="page-wrap d-flex flex-row align-items-center bodyt">
        <div class="container">
            <!-- GIF -->
            <div class="row justify-content-center">
                <div class="col-md-12 text-center">
                    <figure class="snip0016">
                        <img src="<c:url value='/assets/404.gif'/>"
                             alt="Electrocuted caveman animation for 404 error page"
                             class="img-fluid"/>
                        <figcaption style="font-size: 10px; color: #999;">
                            Electric caveman 404 page - Made by <a href="https://dribbble.com/MarkusM">Markus Magnusson</a>
                        </figcaption>
                    </figure>
                </div>

                <div class="col-md-12 text-center">
                    <div class="mb-4 lead">The page you are looking for was not found.</div>
                    <a href="${listProducts}" class="btn btn-black" autofocus="autofocus">Back to Home</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>