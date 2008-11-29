<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="enums" type="java.lang.Object" rtexprvalue="true" %>
<%@ attribute name="required" %>
<%@ attribute name="valueType" %>
<%@ attribute name="naOption" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="i18n" %>

<s:bind path="${path}">
	<c:set var="labelValue" value="${status.value}" />
</s:bind>

<c:if test="${labelValue != 'null'}">
	<s:bind path="${path}.id">
		<c:set var="selectedId" value="${status.value}" />
	</s:bind>
</c:if>

<app:formLine label="${label}" path="${path}" required="${required}" valueType="${valueType}" styleClass="${styleClass}">
	<html:select path="${path}">
		<c:if test="${! empty naOption}">
			<option value="0"><fmt:message key="app.label.notAvailable" /></option>
		</c:if>
		<c:choose>
			<c:when test="${i18n == 'false'}">
				<html:options items="${enums}" label="name" value="id" selected="${selectedId}" />
			</c:when>
			<c:otherwise>
				<html:optionsMsg items="${enums}" label="name" value="id" selected="${selectedId}" />
			</c:otherwise>
		</c:choose>
	</html:select>
</app:formLine>
