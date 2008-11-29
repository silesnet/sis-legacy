<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<title><s:message code="app.title"/></title>
	<link href="${ctx}/css/global.css" type="text/css" rel="stylesheet"/>
</head>

<body>
<!-- Header -->
<div id="HeaderWrapper"><div id="Header">
	<!-- Title -->
	<h1><s:message code="app.title"/></h1>
	<h2>&nbsp;</h2>
</div></div>


<!-- Body -->
<div id="BodyWrapper">

<!-- Main -->
<div id="MainWrapper"><div id="Main">
<h1><s:message code="logout.title"/></h1>

<h2><s:message code="logout.user"/><font color="red"> <authz:authentication operation="username"/></font>
 <s:message code="logout.loggedOut"/></h2>
<% session.invalidate(); %>
<p><a href="${ctx}/login.jsp"><s:message code="login.title"/></a></p>

</div></div>

<div id="SidebarWrapper"><div id="Sidebar">
</div></div>
<hr class="cleaner" />
</div>


</body>
</html>
