<%@ include file="/WEB-INF/routes/user-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="content">
    <div class="main">
        <div class="row">
            <div class="col-md-12 mb-4">
                <div class="avatar align-center" style="width: 400px">
                    <c:if test="${not empty user.imgUrl}">
                        <img src="${cdn}/${user.imgUrl}" alt="user" class="avatar-img rounded-circle"/>
                    </c:if>
                </div>
                <div>
                    <form action="${baseLink}/v2${uploadUserPhoto}/${user.id}"
                          enctype="multipart/form-data"
                          method="post"
                          class="csrf-upload-form">
                        <label for="inputImage" class="form-label mt-2">PROFILE</label>
                        <div class="row">
                            <div class="col-md-18">
                                <input type="file" name="file" class="form-control" id="inputImage" accept="image/*"/>
                            </div>
                            <div class="col-md-6">
                                <jsp:include page="/WEB-INF/view/components/buttons/uploadButton.jsp"/>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="col-md-12" style="flex-grow: 1">
                <form action="<c:out value='${baseLink}${version}${ updateUser }/${user.id}' escapeXml='true'/>"
                      method="post"
                      class="csrf-form">
                    <div class="mb-5">
                        <label for="inputLogin" class="form-label">E-MAIL</label> <input
                            type="email" name="login" class="form-control" placeholder="E-mail"
                            value="<c:out value='${ user.login }' escapeXml='true'/>" autocomplete="email"
                            required/>
                    </div>
                    <div class="mb-5">
                        <label for="inputPassword" class="form-label">PASSWORD</label> <input
                            type="password" name="password" class="form-control" autocomplete="off"
                            id="inputPassword" placeholder="password" required/>
                    </div>

                    <c:if test="${not empty error }">
                        <div class="alert alert-danger" role="alert">
                            <c:out value="${error}" escapeXml="true"/>
                        </div>
                    </c:if>

                    <div class="row justify-content-end">
                        <jsp:include page="/WEB-INF/view/components/buttons/backButton.jsp"/>
                        <span>&nbsp;</span>
                        <jsp:include page="/WEB-INF/view/components/buttons/saveButton.jsp"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>
