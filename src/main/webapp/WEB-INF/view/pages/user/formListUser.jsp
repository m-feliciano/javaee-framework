<%@ include file="/WEB-INF/routes/user-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="content">
    <div class="main">
        <div class="container-narrow">
            <h2 class="mb-4">User Profile</h2>

            <div class="grid-container grid-gap-lg" style="grid-template-columns: 1fr;">
                <!-- Avatar Upload Card -->
                <div class="card">
                    <div class="card-header">
                        <h3>Profile Picture</h3>
                    </div>
                    <div class="card-body">
                        <div class="grid-container" style="place-items: center; margin-bottom: var(--spacing-4);">
                            <c:if test="${not empty user.imgUrl}">
                                <img src="${cdn}/${user.imgUrl}" alt="user" class="avatar-img rounded-circle"
                                     style="width: 200px; height: 200px; object-fit: cover;"/>
                            </c:if>
                            <c:if test="${empty user.imgUrl}">
                                <div style="width: 200px; height: 200px; border-radius: 50%; background: var(--gray-200); display: grid; place-items: center;">
                                    <i class="bi bi-person-circle" style="font-size: 8rem; color: var(--gray-400);"></i>
                                </div>
                            </c:if>
                        </div>

                        <form action="${baseLink}/v2${uploadUserPhoto}"
                              enctype="multipart/form-data"
                              method="post"
                              class="csrf-upload-form grid-form">
                            <div class="form-group">
                                <label for="inputImage" class="form-label">Upload New Profile Picture</label>
                                <input type="file" name="file" class="form-control" id="inputImage" accept="image/*"/>
                            </div>
                            <jsp:include page="/WEB-INF/view/components/buttons/uploadButton.jsp"/>
                        </form>
                    </div>
                </div>

                <!-- User Information Card -->
                <div class="card">
                    <div class="card-header">
                        <h3>Account Information</h3>
                    </div>
                    <div class="card-body">
                        <form action="<c:out value='${baseLink}${version}${ updateUser }' escapeXml='true'/>"
                              method="post"
                              class="csrf-form grid-form">
                            <div class="form-group">
                                <label for="inputLogin" class="form-label">EMAIL</label>
                                <input type="email" name="login" class="form-control" placeholder="E-mail"
                                       value="<c:out value='${ user.login }' escapeXml='true'/>" autocomplete="email"
                                       required/>
                            </div>

                            <div class="form-group">
                                <label for="inputPassword" class="form-label">PASSWORD</label>
                                <input type="password" name="password" class="form-control" autocomplete="off"
                                       id="inputPassword" placeholder="Enter new password" required/>
                            </div>

                            <c:if test="${not empty error }">
                                <div class="alert alert-danger" role="alert">
                                    <c:out value="${error}" escapeXml="true"/>
                                </div>
                            </c:if>

                            <div class="grid-container grid-2-cols grid-gap-md">
                                <jsp:include page="/WEB-INF/view/components/buttons/backButton.jsp"/>
                                <jsp:include page="/WEB-INF/view/components/buttons/saveButton.jsp"/>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/components/footer.jsp"/>
