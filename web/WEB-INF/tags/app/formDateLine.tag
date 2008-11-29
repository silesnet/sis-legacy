<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="required" %>
<%@ attribute name="valueType" %>
<%@ attribute name="size" %>
<%@ attribute name="styleClass" %>

<app:formLine label="${label}" path="${path}" required="${required}" valueType="${valueType}" styleClass="${styleClass}">
	<html:input path="${path}" size="${size}" />
	<img src="${ctx}/img/form/calendar.png" onclick="displayCalendar(document.forms[0]['${path}'],'dd.mm.yyyy',this)" class="datePicker" />
</app:formLine>
