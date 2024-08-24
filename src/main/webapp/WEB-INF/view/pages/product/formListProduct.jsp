<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<fmt:formatNumber value="${product.price}" type="currency" minFractionDigits="2" var="parsedNumber"/>
<fmt:parseDate value="${product.registerDate}" type="date" pattern="yyyy-MM-dd" var="parsedDate"/>
<fmt:formatDate value="${parsedDate}" type="date" pattern="dd/MM/yyyy" var="stdDate"/>

<title>Product: ${ product.name }</title>

<div class="main">
    <form>
        <div class="col-md-6">
            <div class="mb-3">
                <div class="row">
                    <div class="col-md-6">
                        <label for="inputId" class="form-label">ID</label>
                        <input type="text" name="id" class="form-control text-right" id="inputId"
                               value="${ product.id }" readonly="readonly"/>
                    </div>
                    <div class="col-md-6">
                        <label for="inputRegisterDate" class="form-label">REGISTER</label>
                        <input type="text" name="register" class="form-control text-right" id="inputRegisterDate"
                               value="${ stdDate }" readonly/>
                    </div>
                </div>
            </div>
            <c:if test="${ not empty product.url }">
                <div class="text-center p-3">
                    <img class="img-square fit-img" src="${ product.url }" alt="Image of product ${ product.name }">
                </div>
            </c:if>

            <div class="mb-3">
                <label for="inputName" class="form-label">NAME</label>
                <input type="text" name="name" class="form-control" id="inputName"
                       placeholder="Product name" value="${ product.name }" autocomplete="name"
                       minlength="4" readonly/>
            </div>
            <div class="mb-3">
                <label for="inputDescription" class="form-label">DESCRIPTION</label>
                <textarea name="description" class="form-control" id="inputDescription"
                          placeholder="Simple Description" readonly rows="4" cols="auto">
                    ${ product.description }</textarea>
            </div>
            <div class="mb-4">
                <div class="row justify-content-end">
                    <div class="col-md-6">
                        <label for="inputCategory" class="form-label">CATEGORY</label>
                        <input type="text" name="category" class="form-control text-center" id="inputCategory"
                               value="${ product.category.name }" autocomplete="name" readonly/>
                    </div>
                    <div class="col-md-6">
                        <label for="inputPrice" class="form-label">PRICE</label>
                        <input name="price" class="form-control text-right" id="inputPrice"
                               placeholder="price" value="${parsedNumber}" readonly/>
                    </div>
                </div>
            </div>
            <div class="row justify-content-end mr-0">
                <a type="button" href="${ editProduct }&id=${ product.id }" class="btn btn-success mr-2">
                    Edit <i class="bi bi-pencil-square"></i>
                </a>
                <a type="button" href="${ listProducts }" class="btn btn-light">Go back</a>
            </div>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>