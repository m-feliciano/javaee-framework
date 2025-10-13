<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1" %>

<fmt:setLocale value="pt-BR" scope="session"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta http-equiv="Content-Security-Policy" content="
      style-src 'self' https://cdn.jsdelivr.net 'unsafe-inline';
      font-src 'self' https://cdn.jsdelivr.net;
      script-src 'self' https://cdn.jsdelivr.net 'unsafe-inline';
      connect-src 'self';
      object-src 'none';
      frame-ancestors 'none';
    ">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="referrer" content="strict-origin-when-cross-origin">
    <meta name="theme-color" content="#0d6efd">
    <meta name="description" content="Área de login e cadastro do sistema de gestão de produtos.">
    <meta name="author" content="Feliciano">
    <meta name="robots" content="index, follow">
    <meta name="keywords" content="login, cadastro, autenticação, sistema, produtos">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css"
          integrity="sha384-zCbKRCUGaJDkqS1kPbPd7TveP5iyJE0EjAuZQTgFLD2ylzuqKfdKlfG/eSrtxUkn"
          crossorigin="anonymous"/>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <link rel="stylesheet" href="<c:url value='/resources/css/styles.css'/>">
    <title><c:out value="${headerTitle}" escapeXml="true"/></title>
</head>
<body>

<jsp:include page="/WEB-INF/view/components/navbar.jsp"/>

<div class="content">