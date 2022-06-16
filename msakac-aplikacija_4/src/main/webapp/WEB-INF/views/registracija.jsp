<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
<style><%@include file="/WEB-INF/css/style.css"%></style>
<title>Registracija</title>
</head>
<body>

	<h1>Registracija</h1>
	<a href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/pocetak" class="mb-2 link-success h4">Pocetak</a>
	<c:if test="${requestScope.greska != null }">
		<div class="alert alert-danger" role="alert">
  		${requestScope.greska}
	</div>
	</c:if>
	<c:if test="${requestScope.uneseno != null}">	
		<div class="alert alert-success" role="alert">
	  		${requestScope.uneseno}
		</div>
	</c:if>

	<form class ="pt-2" action="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/registracija" method="POST">
		<div class="form-group">
    		<label for="korIme" class="mt-2">Korisničko ime</label>
    		<input type="text" class="form-control" name="korIme" placeholder="Korisničko ime" required>
    		<label for="lozinka" class="mt-2">Lozinka</label>
    		<input type="password" class="form-control" name="lozinka" placeholder="Lozinka" required>
    		<label for="ime" class="mt-2">Ime</label>
    		<input type="text" class="form-control" name="ime" placeholder="Ime" required>
    		<label for="prezime" class="mt-2">Prezime</label>
    		<input type="text" class="form-control" name="prezime" placeholder="Prezime" required>
    		<label for="email" class="mt-2">E-mail</label>
    		<input type="email" class="form-control" name="email" placeholder="email@mail.com" required>
  		</div>
  		<div class="text-center">
  			<button type="submit" class="btn btn-success mt-3 ">Registriraj se</button>
  		</div>
    </form>
      
</body>
</html>