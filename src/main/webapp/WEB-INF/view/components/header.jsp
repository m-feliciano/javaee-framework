<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">	
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css"
	integrity="sha384-zCbKRCUGaJDkqS1kPbPd7TveP5iyJE0EjAuZQTgFLD2ylzuqKfdKlfG/eSrtxUkn"
	crossorigin="anonymous" />
<link rel="stylesheet" href="<c:url value='/css/styles.css'/>">

<title>Servlet CRUD</title>
</head>
<body>
	<nav class="navbar navbar-dark bg-dark mb-4">
		<a class="navbar-brand" 
			href="/"> 
			<img src="<c:url value='/assets/logo.svg'/>"
				width="30" 
				height="30" 
				class="d-inline-block align-top" 
				alt="Bootstrap logo"> Bootstrap
		</a>
	</nav>