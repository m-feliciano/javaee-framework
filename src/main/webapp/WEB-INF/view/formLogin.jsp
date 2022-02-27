<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
<link href="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
	rel="stylesheet" id="bootstrap-css">
<link rel="stylesheet" href="<c:url value='/css/login.css'/>">
<!------ Include the above in your HEAD tag ---------->

<c:url value="/company" var="loginServlet" />

<div class="sidenav">
	<div class="login-main-text">
		<h2>
			Servlet<br> Login Page
		</h2>
		<p>Login or register from here to access.</p>
	</div>
</div>
<div class="main">
	<div class="col-md-6 col-sm-12">
		<div class="login-form">
			<form action="${ loginServlet }?action=Login" method="post">
				<div class="form-group">
					<label  for="inputEmail">Email</label> 
					<input type="text" 
						id="inputEmail"
						name="email" 
						class="form-control" 
						placeholder="Email" 
						required>
				</div>
				<div class="form-group">
					<label for="inputPassword">Password</label>
					<input type="password" 
						id="inputPassword" 
						name="password"
						class="form-control" 
						placeholder="Password" 
						required
						minlength="3">
				</div>
				<button type="submit" class="btn btn-black">Login</button>
				<button type="submit" class="btn btn-secondary" disabled>Register</button>
			</form>
		</div>
	</div>
</div>