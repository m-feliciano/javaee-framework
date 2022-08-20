<%@ page import="java.util.*,domain.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:url value="/user" var="userLink"/>
<c:url value="/user?action=edit" var="editUser"/>
<c:url value="/product?action=list" var="listProducts"/>

<fmt:setLocale value="pt-BR" scope="application"/>

<jsp:include page="../../components/header.jsp"/>
<div class="main">
    <form action="${ userLink }" method="post">
        <div class="col-md-6">
            <div class="row ml5 mb-3">
                <div class="avatar mr-3 mt5">
                    <img src="${user.getImgUrl() != null ? user.getImgUrl() : 'https://bootdey.com/img/Content/avatar/avatar2.png' }"
                         alt="user" class="avatar-img rounded-circle">
                </div>
                <div class="col-md-10">
                    <label for="imgUrl" class="form-label">URL Image</label>
                    <textarea rows="2" name="imgUrl" class="form-control" minlength="5"
                                maxlength="255" id="imgUrl" placeholder="URL">${user.imgUrl}</textarea>
                </div>
            </div>
            <div class="mb-3">
                <label for="inputEmail" class="form-label">E-MAIL</label>
                <input type="email" name="email" class="form-control" id="inputEmail"
                       placeholder="E-mail" value="${ user.login }" autocomplete="email"
                       required/>
            </div>
            <div class="mb-3">
                <label for="inputPassword" class="form-label">PASSWORD</label>
                <input type="password" name="password" class="form-control" id="inputPassword"
                       placeholder="password" value="${user.password}" required/>
            </div>

            <div class="mb-3">
                <label for="inputConfirmPaassword" class="form-label">CONFIRM PASSWORD</label>
                <input type="password" name="confirmPassword" class="form-control" id="inputConfirmPaassword"
                       placeholder="confirm password" required/>
            </div>
            <c:if test="${not empty invalid}">
                <div class="alert alert-danger hidden-alert" role="alert">
                    <c:out value="${invalid}"/>
                </div>
            </c:if>

            <!-- action -->
            <input type="hidden" name="action" value="update">
            <button type="submit" class="btn btn-primary">Submit</button>
            <a type="button" href="${ listProducts }" class="btn btn-light">
                Go back
            </a>
        </div>
    </form>
</div>
<!-- footer -->
<jsp:include page="../../components/footer.jsp"/>