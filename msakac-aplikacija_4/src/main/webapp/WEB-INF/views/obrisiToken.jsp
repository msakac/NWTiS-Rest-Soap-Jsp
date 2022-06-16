<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@page import="java.io.*, java.util.Date, java.util.Enumeration, java.sql.Timestamp" %>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
 <jsp:useBean id="dateValue" class="java.util.Date"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
<style><%@include file="/WEB-INF/css/style.css"%></style>
<title>Obrisi token</title>
</head>
<body class="mb-5">

<h1>Obrisi token</h1>
	<c:if test="${requestScope.greska != null }">
		<div class="alert alert-danger" role="alert">
  		${requestScope.greska}
	</div>
	</c:if>
	<c:if test="${requestScope.obrisan != null}">	
		<div class="alert alert-success" role="alert">
	  		${requestScope.obrisan}
		</div>
	</c:if>
<a class="btn btn-success w-25 text-center" href="${pageContext.servletContext.contextPath}/mvc/admin_poslovi/pregledKorisnika" role="button">OK</a>

      
</body>
</html>
