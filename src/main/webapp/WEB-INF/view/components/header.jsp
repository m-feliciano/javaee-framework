<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="ISO-8859-1" %>
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

<c:url value="/product" var="linkProductServlet"/>
<c:url value="/category" var="linkCategoryServlet"/>
<c:url value="/inventory" var="linkInventoryServlet"/>

<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="collapse navbar-collapse nav-items" id="navbarTogglerDemo01">
        <a class="navbar-brand"
           href="${linkProductServlet}?action=ListProducts">
            <img src="<c:url value='/assets/logo.svg'/>" width="30" height="30"
                 class="d-inline-block align-top" alt="Bootstrap logo"><span class="title-logo ml-2">Shopping</span>
        </a>
        <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
            <li class="nav-item">
                <a class="nav-link" href="${linkProductServlet}?action=ListProducts">Products</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${linkCategoryServlet}?action=ListCategories">Categories</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${linkInventoryServlet}?action=ListItems">Inventory</a>
            </li>
        </ul>
        <a class="nav-link btn-logout" href="${linkProductServlet}?action=Logout">Logout</a>
    </div>
</nav>

<div class="content">