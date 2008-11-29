<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="required" %>
<%@ attribute name="valueType" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="i18n" %>
<%@ attribute name="enum" %>

<app:formLine label="${label}" path="${path}" required="${required}" valueType="${valueType}" styleClass="${styleClass}">
		<c:choose>
			<c:when test="${!empty enum}">
				<html:input path="${path}.id" type="hidden" />
			</c:when>
			<c:otherwise>
				<html:input path="${path}" type="hidden" />
			</c:otherwise>
		</c:choose>
	<s:bind path="${path}">
		<c:choose>
			<c:when test="${!empty i18n}">
				<fmt:message key="${status.value}" />
			</c:when>
			<c:otherwise>
				${status.value}
			</c:otherwise>
		</c:choose>
	</s:bind>
</app:formLine>
