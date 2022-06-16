<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
<style><%@include file="/WEB-INF/css/style.css"%></style>
<title>Početna</title>
</head>
<body>
	<h1>Administrativni poslovi</h1>
	<div list-group>
		<a class="list-group-item list-group-item-action list-group-item-dark mt-2"
			href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/registracija">
				Registracija korisnika </a>
		<c:if test="${requestScope.token == null}">
			<a class="list-group-item list-group-item-action list-group-item-dark mt-2"
				href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/prijava">
					Prijava u Admin Panel </a>
		</c:if>
		<c:if test="${requestScope.token != null}">
			<a class="list-group-item list-group-item-action list-group-item-dark mt-2"
				href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/pregledKorisnika">
					Pregled korisnika</a>
			<a class="list-group-item list-group-item-action list-group-item-dark mt-2"
				href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/upravljanjePosluziteljem">
					Upravljanje poslužiteljem </a>
			<a class="list-group-item list-group-item-action list-group-item-primary mt-4 text-center"
				href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/odjava">
					Odjavi se </a>
		</c:if>
		<p class="mt-5 text-center">Token: ${requestScope.token}</p>
	</div>
</body>
</html>

	
	