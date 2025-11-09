<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1" %>

<fmt:setLocale value="pt-BR" scope="session"/>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta http-equiv="Content-Security-Policy" content="
      default-src 'self';
      style-src 'self' https://cdn.jsdelivr.net 'unsafe-inline';
      img-src 'self' data: https:;
      font-src 'self' https://cdn.jsdelivr.net;
      script-src 'self' https://cdn.jsdelivr.net 'unsafe-inline';
      connect-src 'self';
      object-src 'none';
      frame-ancestors 'none';
    ">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="referrer" content="strict-origin-when-cross-origin">
    <meta name="theme-color" content="#1976d2">
    <meta name="description" content="Sistema empresarial de gestao de produtos, inventario e categorias.">
    <meta name="author" content="Feliciano">
    <meta name="robots" content="index, follow">
    <meta name="keywords" content="gestao, produtos, inventario, sistema empresarial, dashboard">

    <!-- Bootstrap Icons -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          integrity="sha384-4LISF5TTJX/fLmGSxO53rV4miRxdg84mZsxmO8Rx5jGtp/LbrixFETvWa5a6sESd"
          crossorigin="anonymous"/>
    <link rel="stylesheet" href="<c:url value='/resources/css/variables.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/styles.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/navbar.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/components.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/fixes.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/dark-mode.css'/>">

    <script src="<c:url value='/resources/js/csrf-util.js'/>"></script>

    <title><c:out value="${headerTitle}" default="Store" escapeXml="true"/></title>
</head>
<body>

<jsp:include page="/WEB-INF/view/components/navbar.jsp"/>

<div class="content">

