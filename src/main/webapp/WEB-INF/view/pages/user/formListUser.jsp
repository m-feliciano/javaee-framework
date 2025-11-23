<%@ include file="/WEB-INF/routes/user-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="main">
    <form action="<c:out value='${baseLink}${version}${ updateUser }/${user.id}' escapeXml='true'/>" method="post" class="csrf-form">
        <div style="display: flex; margin: 60px 0;  gap: 30px; justify-content: center">
            <div>
                <div class="avatar align-center" style="width: 300px">
                    <c:if test="${not empty user.imgUrl and user.imgUrl ne ''}">
                        <img src="<c:out value='${user.imgUrl}' escapeXml='true'/>" alt="user" class="avatar-img rounded-circle"/>
                    </c:if>
                </div>
            </div>
            <div>
                <div class="mb-5">
                    <label for="imgUrl" class="form-label">Image</label>
                    <textarea name="imgUrl" id="imgUrl" class="form-control" rows="2" maxlength="140" cols="70"
                              placeholder="URL"><c:out value="${user.imgUrl}" escapeXml="true"/></textarea>
                </div>
                <div class="mb-5">
                    <label for="inputLogin" class="form-label">E-MAIL</label> <input
                        type="email" name="login" class="form-control" placeholder="E-mail"
                        value="<c:out value='${ user.login }' escapeXml='true'/>" autocomplete="email" required/>
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
            </div>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>