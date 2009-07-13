<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="i18n" %>
<%@ attribute name="emphasis" %>
<%@ attribute name="valueType" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="link" %>
<%@ attribute name="target" %>

<app:viewLine label="${label}" emphasis="${emphasis}" valueType="${valueType}" styleClass="${styleClass}" link="${link}" target="${target}">
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
</app:viewLine>
