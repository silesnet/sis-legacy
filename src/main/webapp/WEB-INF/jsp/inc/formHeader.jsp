<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ page language="Java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<title><s:message code="app.title"/></title>
	<link href="${ctx}/css/global.css" type="text/css" rel="stylesheet"/>

	<script type="text/javascript" src="${ctx}/js/safeSubmit.js"></script>
	<script type="text/javascript" src="${ctx}/js/calendar.js"></script>

	<%-- Include scripts if needed --%>
	<c:if test="${not empty scripts}">
		<c:forEach var="script" items="${scripts}">
			<c:out value='<script type="text/javascript" src="${ctx}/js/${script}">' escapeXml='false' />
			<c:out value='</script>' escapeXml='false' />
		</c:forEach>
	</c:if>
</head>

<body>

<!-- Header -->
<div id="HeaderWrapper"><div id="Header">
	<!-- Login info -->
	<h3><s:message code="app.loggedAs"/>&nbsp;<b><authz:authentication property="principal.username" /></b>
	  (<a href="${ctx}/logout.jsp"><s:message code="app.logout"/></a>|<!--
      --><a href="${ctx}/app/view.html?view=viewLastLogin"><s:message code="lastLogin.link"/></a>)</h3>

	<!-- Title -->
	<h1><s:message code="app.title"/> v<s:message code="app.version"/></h1>
	
	<!-- Menu -->
	<h2><%@ include file="/WEB-INF/jsp/inc/mainMenu.jsp" %></h2>

</div></div>


<!-- Body -->
<div id="BodyWrapper">

<!-- Main -->
<div id="MainWrapper"><div id="Main">

