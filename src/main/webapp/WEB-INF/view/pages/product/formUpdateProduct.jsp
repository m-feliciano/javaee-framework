<%@ page import="java.util.*,domain.Product" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:url value="/product" var="productLink"/>
<fmt:parseDate  value="${product.registerDate}"  type="date" pattern="yyyy-MM-dd" var="parsedDate" />
<fmt:formatDate value="${parsedDate}" type="date" pattern="dd-MM-yyyy" var="stdDate" />


<jsp:include page="../../components/header.jsp"/>
<div class="main">
    <form action="${ productServlet }" method="post">
        <div class="col-md-6">
            <div class="mb-3">
                <label for="inputId" class="form-label">ID</label>
                <input type="text" name="id" class="form-control col-md-3" id="inputId"
                       value="${ product.id }" readonly="readonly"/>
            </div>

            <div class="mb-3">
                <label for="inputRegisterDate" class="form-label">REGISTER</label>
                <input type="text" name="register" class="form-control" id="inputRegisterDate"
                       value="${ stdDate }" required readonly="readonly"/>
            </div>
            <div class="mb-3">
                <label for="inputURL" class="form-label">IMAGE</label>
                <input type="text" name="url" class="form-control" id="inputURL"
                       placeholder="URL" value="${ product.url }"/>
            </div>
            <div class="mb-3">
                <label for="inputName" class="form-label">NAME</label>
                <input type="text" name="name" class="form-control" id="inputName"
                       placeholder="Product name" value="${ product.name }" autocomplete="name" required minlength="4"/>
            </div>
            <div class="mb-3">
                <label for="inputDescription" class="form-label">DESCRIPTION</label>
                <textarea name="description" class="form-control" id="inputDescription"
                          placeholder="Simple Description" rows="" cols="" required>${ product.description }</textarea>
            </div>
            <div class="mb-3">
                <label for="inputPrice" class="form-label">PRICE</label>
                <input name="price" class="form-control" id="inputPrice"
                       placeholder="1000,00" value="${ product.price }" min="0" max="10000" step="any"
                       pattern="^\s*(?:[1-9]\d{0,2}(?:\,\d{3})*|0)(?:.\d{1,2})?$"
                       title="Currency should only contain numbers and (comma/doc) e.g. 1000,00"
                       required/>
            </div>
            <!-- action -->
            <input type="hidden" name="action" value="UpdateProduct">
            <button type="submit" class="btn btn-primary">Submit</button>
            <a type="button" href="${ productLink }?action=ListProduct&id=${product.id }" class="btn btn-light">
                Cancel
            </a>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="../../components/footer.jsp"/>