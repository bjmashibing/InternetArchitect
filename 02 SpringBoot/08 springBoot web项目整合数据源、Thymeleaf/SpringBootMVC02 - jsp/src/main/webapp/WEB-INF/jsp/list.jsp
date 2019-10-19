<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>




<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
<body>

<table>

	<tr>
		<th>id</th>
		<th>name</th>
	</tr>
	
	<c:forEach items="${list}" var="item">
	
	<tr>
			<td>${item.id }</td>
			<td>${item.name }</td>
		
		</tr>
	
	</c:forEach>
	
</table>
</body>
</html>