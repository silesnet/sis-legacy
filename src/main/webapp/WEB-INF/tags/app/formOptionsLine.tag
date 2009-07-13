<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="labels" type="java.lang.Object" rtexprvalue="true" %>
<%@ attribute name="required" %>
<%@ attribute name="valueType" %>
<%@ attribute name="naOption" %>
<%@ attribute name="styleClass" %>

<s:bind path="${path}">
	<c:set var="labelValue" value="${status.value}" />
</s:bind>

<c:if test="${labelValue != 'null'}">
	<s:bind path="${path}">
		<c:set var="selectedVal" value="${status.value}" />
	</s:bind>
</c:if>

<app:formLine label="${label}" path="${path}" required="${required}" valueType="${valueType}" styleClass="${styleClass}">
	<html:select path="${path}">
		<c:if test="${! empty naOption}">
			<option value="0"><fmt:message key="app.label.notAvailable" /></option>
		</c:if>
		<html:options items="${labels}" label="name" value="name" selected="${selectedVal}" />
	</html:select>
</app:formLine>
