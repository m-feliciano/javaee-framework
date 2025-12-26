<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="tag" tagdir="/WEB-INF/tags" %>
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
    ">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="referrer" content="strict-origin-when-cross-origin">
    <meta name="theme-color" content="#1976d2">
    <meta name="description" content="Sistema empresarial de gestao de produtos, inventario e categorias.">
    <meta name="author" content="Feliciano">
    <meta name="robots" content="index, follow">
    <meta name="keywords" content="gestao, produtos, inventario, sistema empresarial, dashboard">

    <title><c:out value="${headerTitle}" default="ServletStack"/></title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          integrity="sha384-4LISF5TTJX/fLmGSxO53rV4miRxdg84mZsxmO8Rx5jGtp/LbrixFETvWa5a6sESd"
          crossorigin="anonymous"/>

    <link rel="apple-touch-icon" sizes="180x180" href="<c:url value='/resources/assets/favicon/apple-touch-icon.png'/>"/>
    <link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/resources/assets/favicon/favicon-32x32.png'/>"/>
    <link rel="icon" type="image/png" sizes="16x16" href="<c:url value='/resources/assets/favicon/favicon-16x16.png'/>"/>
    <link rel="shortcut icon" href="<c:url value='/resources/assets/favicon/favicon.ico'/>"/>
    <link rel="manifest" href="<c:url value='/resources/assets/favicon/site.webmanifest'/>"/>

    <!-- CSS Files -->
    <c:set var="mainCssUrl"><tag:assetPath name="main.css"/></c:set>
    <link rel="stylesheet" href="${mainCssUrl}"/>
    <!-- JavaScript Files -->
    <c:set var="mainJsUrl"><tag:assetPath name="main.js"/></c:set>
    <script src="${mainJsUrl}" defer></script>
    <c:set var="designJsUrl"><tag:assetPath name="design-system.js"/></c:set>
    <script src="${designJsUrl}" defer></script>
    <c:set var="csrfUrl"><tag:assetPath name="csrf-util.js"/></c:set>
    <script src="${csrfUrl}" defer></script>
</head>
<body>

<jsp:include page="/WEB-INF/view/components/navbar.jsp"/>