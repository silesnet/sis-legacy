<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="items" required="true" type="java.util.List" %>
<%@ attribute name="required" %>
<%@ attribute name="valueType" %>
<%@ attribute name="size" %>
<%@ attribute name="styleClass" %>

<c:forEach var="item" items="${items}">
	<c:if test="${!empty itemsString}">
		<c:set var="itemsString" value="${itemsString};${item.name}" />
	</c:if>
	<c:if test="${empty itemsString}">
		<c:set var="itemsString" value="${item.name}" />
	</c:if>
</c:forEach>

<app:formLine label="${label}" path="${path}" required="${required}" valueType="${valueType}" styleClass="${styleClass}">
	<html:input path="${path}" comboBoxOptions="${itemsString}" size="${size}" />
</app:formLine>
