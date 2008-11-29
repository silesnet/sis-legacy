<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="form" %>
<%@ attribute name="format" %>

<c:set var="formName" value="0" />
<c:if test="${!empty form}">
	<c:set var="formName" value="${form}" />
</c:if>
<c:set var="dateFormat" value="dd.mm.yyyy" />
<c:if test="${!empty format}">
	<c:set var="dateFormat" value="${format}" />
</c:if>
<img src="${ctx}/img/form/calendar.png" onclick="displayCalendar(document.forms[${formName}]['${name}'],'${dateFormat}',this)" class="datePicker" />