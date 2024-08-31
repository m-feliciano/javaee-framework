<%@ include file="/WEB-INF/jspf/common-imports.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<div class="main">
    <form action="${ updateUser }" method="post">
        <div class="col-md-6">
            <div class="row ml5 mb-3 justify-center">
                <div class="avatar mr-3 align-center">
                    <c:if test="${not empty user.imgUrl }">
                        <img src="${user.imgUrl}" alt="user"
                             class="avatar-img rounded-circle">
                    </c:if>
                    <c:if test="${empty user.imgUrl }">
                        <img src="<c:url value='/assets/avatar2.png'/>" alt="user"
                             class="avatar-img rounded-circle">
                    </c:if>
                </div>
                <div class="col-md-9">
                    <label for="imgUrl" class="form-label">Image</label>
                    <textarea rows="4" name="imgUrl" class="form-control" minlength="5"
                              maxlength="140" id="imgUrl" placeholder="URL">${user.imgUrl}
                    </textarea>
                </div>
            </div>
            <div class="mb-3">
                <label for="inputEmail" class="form-label">E-MAIL</label> <input
                    type="email" name="email" class="form-control" id="inputEmail"
                    placeholder="E-mail" value="${ user.login }" autocomplete="email"
                    required/>
            </div>
            <div class="mb-3">
                <label for="inputPassword" class="form-label">PASSWORD</label> <input
                    type="password" name="password" class="form-control"
                    id="inputPassword" placeholder="password" value="${user.password}"
                    required/>
            </div>

            <!--  <div class="mb-3">
                <label for="inputConfirmPaassword" class="form-label">CONFIRM PASSWORD
                </label>
                <input type="password" name="confirmPassword"
                    class="form-control" id="inputConfirmPaassword"
                    placeholder="password" required />
            </div>
            -->
            <c:if test="${not empty invalid}">
                <div class="alert alert-danger hidden-alert" role="alert">
                    <c:out value="${invalid}"/>
                </div>
            </c:if>

            <!-- action -->
            <div class="row justify-content-end mr-0 mb20">
                <button type="submit" class="btn btn-primary mr-2">Save</button>
                <a type="button" href="${ listProducts }" class="btn btn-light">Go
                    back</a>
            </div>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>