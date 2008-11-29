<%--
	- input
	- 
	- Display an input field (default type="text") and bind it to the attribute
	- of a command or bean. If name and/or value attributes are specified,
	- they will be used instead of status.expression and/or status.value
	- respectively. A type attribute may also be used to override the
	- input tag type (the default is text).
	- Accepts dynamic attributes.
	-
	- @param path the name of the field to bind to (required)
	- @param name use this attribute to override the input name
	- @param value use this attribute to override the input value
	- @param type use this attribute to override the input type (i.e. hidden).
	--%>

<%@ tag dynamic-attributes="attributes" isELIgnored="false" %>
<%@ attribute name="readOnly"  %>	
<%@ include file="includes.tagf" %>
<%@ attribute name="path" required="true" %>
<spring:bind path="${path}">
	<html:attributes var="attrString" attributeMap="${attributes}" type="text" name="${status.expression}" value="${status.value}">
	<c:choose>
		<c:when test="${readOnly == 'true'}">
			<input ${attrString} readonly="readonly" />
		</c:when>
		<c:otherwise>
			<input ${attrString} />
		</c:otherwise>
	</c:choose>
		
		</html:attributes></spring:bind>