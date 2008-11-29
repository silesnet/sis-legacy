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
<h1><s:message code="login.title"/></h1>
<form autocomplete="off" name="loginForm" action="<c:url value='j_acegi_security_check'/>" method="post">
<table>
	<tr><td><s:message code="login.field.loginName"/><td> <input type="text" name="j_username"/>
	<tr><td><s:message code="login.field.password"/><td><input type="password" name="j_password"/>
	<tr><td>&nbsp;<td><input type="submit" value="<s:message code="login.button.login"/>"/></td></tr>
</table>
</form>
</div></div>

<div id="SidebarWrapper"><div id="Sidebar">
</div></div>
<hr class="cleaner" />
</div>


</body>
</html>

<script type="text/javascript">
    document.forms["loginForm"].elements["j_username"].focus();
</script>
