<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="emphasis" %>
<%@ attribute name="valueType" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="link" %>
<%@ attribute name="target" %>

<c:choose>
	<c:when test="${!empty styleClass}">
		<tr class="${styleClass}">
	</c:when>
	<c:otherwise>
		<tr>
	</c:otherwise>
</c:choose>
	<td class="labelCell" >
	<c:if test="${!empty label}">
		<fmt:message key="${label}" />&nbsp;
	</c:if>
	</td>
	<td class="valueCell">
		<c:if test="${!empty emphasis}"><span class="valueEmphasis"></c:if>
		<c:if test="${!empty link}"><a href="${link}"
			<c:if test="${!empty target}"> target="${target}"</c:if>></c:if>
			<jsp:doBody /><c:if test="${!empty link}"></a></c:if>&nbsp;
		<c:if test="${!empty emphasis}"></span></c:if>
		<c:if test="${!empty valueType}"><span class="valueType">${valueType}</span></c:if>
	</td>
</tr>
