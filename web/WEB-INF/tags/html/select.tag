<%--
	- select
	- 
	- Display a 'select' tag and bind it to the attribute of a command or
	- bean. The var attribute specifies a variable that may be used in the
	- body of the tag.
	- Accepts dynamic attributes.
	-
	- @param path the name of the field to bind to (required).
	- @param multiple use this attribute to create a multi-select control.
	-     The value of this attribute should also be "multiple".
	--%>
<%@ include file="includes.tagf" %>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="readOnly"  %>
<%@ variable name-given="selected" declare="false" %>
<spring:bind path="${path}">
	<html:attributes var="attrString" attributeMap="${attributes}" name="${status.expression}">
	<c:choose>
		<c:when test="${readOnly == 'true'}">
			<select ${attrString} disabled="disabled">
		</c:when>
		<c:otherwise>
			<select ${attrString}>
		</c:otherwise>
	</c:choose>
			<c:set var="selected" value="${status.value}" />
			<jsp:doBody />
		</select></html:attributes></spring:bind>