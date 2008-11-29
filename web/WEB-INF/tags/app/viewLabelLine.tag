<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="i18n" %>
<%@ attribute name="emphasis" %>
<%@ attribute name="valueType" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="link" %>

<s:bind path="${path}">
	<c:set var="labelStr" value="${status.value}" />
</s:bind>

<app:viewLine label="${label}" emphasis="${emphasis}" valueType="${valueType}" styleClass="${styleClass}" link="${link}">
	<c:if test="${labelStr != 'null'}">
	<s:bind path="${path}.name">
		<c:choose>
			<c:when test="${i18n == 'true'}">
				<fmt:message key="${status.value}" />
			</c:when>
			<c:otherwise>
				${status.value}
			</c:otherwise>
		</c:choose>
	</s:bind>
	</c:if>
</app:viewLine>
