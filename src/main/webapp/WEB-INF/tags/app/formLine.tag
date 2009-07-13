<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="required" %>
<%@ attribute name="valueType" %>
<%@ attribute name="styleClass" %>

<c:choose>
	<c:when test="${!empty styleClass}">
		<tr class="${styleClass}">
	</c:when>
	<c:otherwise>
		<tr>
	</c:otherwise>
</c:choose>
	<td>
	<c:if test="${!empty label}">
		<c:if test="${!empty required}"><span class="valueRequired">*&nbsp;</span></c:if>
		<c:if test="${empty required}"><span class="valueOptional">*&nbsp;</span></c:if>
		<fmt:message key="${label}" />&nbsp;
	</c:if>
	</td>
	<td><jsp:doBody />
		<c:if test="${!empty valueType}"><span class="valueType">${valueType}</span></c:if>
	</td>
	<td class="valueError"><html:errors path="${path}" /></td>
</tr>
