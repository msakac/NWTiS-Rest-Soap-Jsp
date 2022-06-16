<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
<style><%@include file="/WEB-INF/css/style.css"%></style>
<title>Upravljanje poslužiteljem</title>
</head>
<body class="mb-5">

	<h1>Upravljanje poslužiteljem</h1>
	<a href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/pocetak" class="mb-5 link-success h4 ">Pocetak</a>
	<form action="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/upravljanjePosluziteljem" method="POST">
	    <div class="form-group mt-5 d-inline">
	    <h4>Odabir naredbe:</h4>
		    <select class="form-select" name="naredba">
		    	<option value="INIT">Inicijalizacija poslužitelja</option>
		    	<option value="QUIT">Prekid rada poslužitelja</option>
		    	<option value="LOAD">Učitavanje podataka</option>
		    	<option value="CLEAR">Brisanje podataka</option>
		    	<option value="STATUS">Status poslužitelja</option>
		    </select>
		</div>
	   	<div class="text-center">
  			<button type="submit" class="btn btn-success mt-3 ">Izvrši naredbu</button>
  		</div>
	</form> 
	<c:if test="${requestScope.odgovor != null}">
		<h4>Ispis:</h4>
		<div class="alert alert-success text-center" role="alert">
	  		${requestScope.odgovor}
		</div>
	</c:if> 
		<c:if test="${requestScope.greska != null}">
		<h4>Ispis:</h4>
		<div class="alert alert-danger text-center" role="alert">
	  		${requestScope.greska}
		</div>
	</c:if> 
</body>
</html>