<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="com.servletstack.adapter.in.web.dto.IServletResponse" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>
<%@ include file="/WEB-INF/routes/product-routes.jspf" %>

<%
    IServletResponse servletResponse = (IServletResponse) request.getAttribute("response");
    request.setAttribute("categories", servletResponse.getEntity("categories"));
    request.setAttribute("product", servletResponse.getEntity("product"));
%>

<div class="content">
    <div class="main">
        <div class="container-narrow">
            <h2 class="mb-4">Update Product</h2>

            <div class="grid-container grid-gap-lg" style="grid-template-columns: 1fr;">
                <!-- Product Form -->
                <div class="card">
                    <div class="card-header">
                        <h3>Product Information</h3>
                    </div>
                    <div class="card-body">
                        <form action="${baseLink}${version}${ updateProduct }/${product.id}" method="post" class="csrf-form grid-form-wide">
                            <div class="grid-container grid-2-cols grid-gap-md">
                                <div class="form-group">
                                    <label for="inputId" class="form-label">ID</label>
                                    <input type="text" name="id" class="form-control" id="inputId"
                                           value="${ product.id }" readonly/>
                                </div>
                                <div class="form-group">
                                    <label for="inputRegisterDate" class="form-label">REGISTER DATE</label>
                                    <input type="text" name="register" class="form-control"
                                           id="inputRegisterDate"
                                           value="${ product.registerDateFormatted }" required readonly/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="inputName" class="form-label">NAME</label>
                                <input type="text" name="name" class="form-control" id="inputName"
                                       placeholder="Product name" value="${ product.name }" autocomplete="name" required
                                       minlength="4"/>
                            </div>

                            <div class="form-group">
                                <label for="inputDescription" class="form-label">DESCRIPTION</label>
                                <textarea name="description" class="form-control" id="inputDescription"
                                          placeholder="Product description" rows="6"
                                          required>${product.description}</textarea>
                            </div>

                            <div class="grid-container grid-2-cols grid-gap-md">
                                <div class="form-group">
                                    <label for="inputCategory" class="form-label">CATEGORY</label>
                                    <select name="category.id" class="form-control" id="inputCategory" required>
                                        <option value="${null}">${"< SELECT >"}</option>
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
                                <div class="form-group">
                                    <label for="inputPrice" class="form-label">PRICE</label>
                                    <input name="price" class="form-control" id="inputPrice"
                                           placeholder="R$ 1000.00" value="${ product.price }" min="0" max="10000"
                                           step="any"
                                           pattern="^\s*(?:[1-9]\d{0,2}(?:\,\d{3})*|0)(?:.\d{1,2})?$"
                                           title="Currency should only contain numbers and (comma/doc) e.g. 1000,00"
                                           required/>
                                </div>
                            </div>

                            <div class="grid-container grid-2-cols grid-gap-md">
                                <jsp:include page="/WEB-INF/view/components/buttons/backButton.jsp"/>
                                <jsp:include page="/WEB-INF/view/components/buttons/saveButton.jsp"/>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Upload Image Form -->
                <div class="card">
                    <div class="card-header">
                        <h3>Product Image</h3>
                    </div>
                    <div class="card-body">
                        <form enctype="multipart/form-data" method="post"
                              action="${baseLink}/v2${uploadPicture}/${product.id}" class="csrf-upload-form grid-form">
                            <div class="form-group">
                                <label for="inputImage" class="form-label">Upload New Image</label>
                                <input type="file" name="file" class="form-control" id="inputImage" accept="image/*"/>
                            </div>
                            <jsp:include page="/WEB-INF/view/components/buttons/uploadButton.jsp"/>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>