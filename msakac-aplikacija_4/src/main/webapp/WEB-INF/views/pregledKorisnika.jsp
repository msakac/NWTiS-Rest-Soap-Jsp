<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
<style><%@include file="/WEB-INF/css/style.css"%></style>
<title>Pregled korisnika</title>
</head>
<body class="mb-5">

<h1>Pregled korisnika</h1>
<a href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/pocetak" class="mb-5 link-success h4 ">Pocetak</a>
	<table>
	<tr>
		<th>Korisnicko ime</th>
		<th>Ime</th>
		<th>Prezime</th>
		<th>E-mail</th>
		<c:if test="${requestScope.clanGrupe == 1}">
			<th>Obrisi token</th>
		</c:if>
	</tr>
	<c:forEach var="k" items="${requestScope.korisnici}">
	<tr>
		<td>${k.korIme}</td>
		<td>${k.ime}</td>
		<td>${k.prezime}</td>
		<td>${k.email}</td>
		<c:if test="${requestScope.clanGrupe == 1}">
			<td><a class="btn btn-danger text-center" href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/obrisiToken/${k.korIme}" role="button">Obriši token</a></td>
		</c:if>
	</tr>
	</c:forEach>
	</table>      
</body>
</html>