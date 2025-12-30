<%@ include file="/WEB-INF/routes/product-routes.jspf" %>
<%@ page import="com.servletstack.adapter.in.web.dto.IHttpResponse" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<%
    request.setAttribute("categories", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<div class="content">
    <div class="main">
        <div class="container-narrow">
            <h2 class="mb-4">Create Product</h2>

            <form action="${baseLink}${version}${ createProduct }" method="post" class="csrf-form grid-form-wide"
                  enctype="multipart/form-data">

                <div class="form-group">
                    <label for="inputName" class="form-label">NAME</label>
                    <input type="text" name="name" class="form-control" id="inputName"
                           placeholder="Product name" required minlength="4"/>
                </div>

                <div class="form-group">
                    <label for="inputDescription" class="form-label">DESCRIPTION</label>
                    <textarea name="description" class="form-control" id="inputDescription"
                              placeholder="Product description" required></textarea>
                </div>

                <div class="grid-container grid-2-cols grid-gap-md">
                    <div class="form-group">
                        <label for="inputCategory" class="form-label">CATEGORY</label>
                        <select name="category.id" class="form-control" id="inputCategory" required>
                            <option value="${null}" selected>${"< SELECT >"}</option>
                            <c:forEach items="${ categories }" var="category">
                                <option value="${category.id}">${category.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="inputPrice" class="form-label">PRICE</label>
                        <input name="price" class="form-control" id="inputPrice"
                               placeholder="R$ 1000.00" min="0" max="999999" step="any"
                               pattern="^\s*(?:[1-9]\d{0,2}(?:\,\d{3})*|0)(?:.\d{1,2})?$"
                               title="Currency should only contain numbers and (comma/doc) e.g. 1000,00"
                               required/>
                    </div>
                </div>

                <div class="form-group">
                    <label for="inputImage" class="form-label">PRODUCT IMAGE</label>
                    <input type="file" name="file" class="form-control" id="inputImage" accept="image/*"/>
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