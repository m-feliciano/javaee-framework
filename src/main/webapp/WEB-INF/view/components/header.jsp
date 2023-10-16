<%@ page contentType="text/html;charset=UTF-8" pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="ISO-8859-1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css"
          integrity="sha384-zCbKRCUGaJDkqS1kPbPd7TveP5iyJE0EjAuZQTgFLD2ylzuqKfdKlfG/eSrtxUkn"
          crossorigin="anonymous"/>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/popup.css'/>">
    <title>Servlet CRUD</title>
</head>

<c:url value="/productView" var="productLink"/>
<c:url value="/categoryView" var="categoryLink"/>
<c:url value="/inventoryView" var="inventoryLink"/>
<c:url value="/userView" var="userLink"/>
<c:url value="/loginView" var="loginLink"/>

<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="collapse navbar-collapse nav-items" id="navbarTogglerDemo01">
        <a class="navbar-brand"
           href="${productLink}?action=list">
            <img src="<c:url value='/assets/logo.svg'/>" width="30" height="30"
                 class="d-inline-block align-top" alt="Bootstrap logo"><span class="title-logo ml-2">Shopping</span>
        </a>
        <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
            <li class="nav-item">
                <a class="nav-link" href="${productLink}?action=list">Products</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${categoryLink}?action=list">Categories</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${inventoryLink}?action=list">Inventory</a>
            </li>
        </ul>
        <div class="d-flex">
            <div class="avatar mt-1 mx30">
                <c:if test="${not empty userLogged.getImgUrl() }">
                    <a href="${userLink}?action=list">
                        <img src="${userLogged.getImgUrl()}" alt="user" class="avatar-img rounded-circle">
                    </a>
                </c:if>
                <c:if test="${empty userLogged.getImgUrl() }">
                    <a href="${userLink}?action=list">
                        <img src="<c:url value='/assets/avatar2.png'/>" alt="user" class="avatar-img rounded-circle">
                    </a>
                </c:if>
            </div>
            <a class="nav-link btn-logout mr-2" href="${userLink}?action=list">Perfil</a>
        </div>
        <a class="nav-link btn-logout" href="${loginLink}?action=logout">Logout</a>
    </div>
</nav>

<div class="content">