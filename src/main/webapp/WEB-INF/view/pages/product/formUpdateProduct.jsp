<%@ include file="../../components/common-imports.jsp" %>
<jsp:include page="../../components/header.jsp"/>

<fmt:parseDate value="${product.registerDate}" type="date" pattern="yyyy-MM-dd" var="parsedDate"/>
<fmt:formatDate value="${parsedDate}" type="date" pattern="dd-MM-yyyy" var="stdDate"/>

<div class="main">
    <form action="${ productLink }" method="post">
        <div class="col-md-6">
            <div class="mb-3">
                <div class="row">
                    <div class="col-md-6">
                        <label for="inputId" class="form-label">ID</label>
                        <input type="text" name="id" class="form-control text-right" id="inputId"
                               value="${ product.id }" readonly/>
                    </div>
                    <div class="col-md-6">
                        <label for="inputRegisterDate" class="form-label">REGISTER</label>
                        <input type="text" name="register" class="form-control text-right" id="inputRegisterDate"
                               value="${ stdDate }" required readonly/>
                    </div>
                </div>
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
                          placeholder="Simple Description" rows="4" cols="auto"
                          required>${ product.description }</textarea>
            </div>
            <div class="mb-4">
                <div class="row justify-content-end">
                    <div class="col-md-6">
                        <label for="inputCategory" class="form-label">CATEGORY</label>
                        <select name="category" class="form-control text-center" id="inputCategory" required>
                            <option value="${null}" selected>${"< SELECT >"}</option>
                            <c:forEach items="${ categories }" var="category">
                                <c:choose>
                                    <c:when test="${ category.id == product.category.id }">
                                        <option value="${ category.id }" selected>${ category.name }</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${ category.id }">${ category.name }</option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="inputPrice" class="form-label">PRICE</label>
                        <input name="price" class="form-control" id="inputPrice"
                               placeholder="1000,00" value="${ product.price }" min="0" max="10000" step="any"
                               pattern="^\s*(?:[1-9]\d{0,2}(?:\,\d{3})*|0)(?:.\d{1,2})?$"
                               title="Currency should only contain numbers and (comma/doc) e.g. 1000,00"
                               required/>
                    </div>
                </div>
            </div>
            <!-- action -->
            <div class="row justify-content-end mr-0">
                <input type="hidden" name="action" value="update">
                <button type="submit" class="btn btn-primary mr-2">Submit</button>
                <a type="button" href="${ productLink }?action=list&id=${product.id }" class="btn btn-light">Cancel</a>
            </div>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="../../components/footer.jsp"/>