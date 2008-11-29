<%--
	- forEachError
	- 
	- Iterate over the list of errors for the command or bean specified in the
	- 'path' attribute. The body of this tag is executed for each error.
	-
	- @param path the name of the field to bind to (required)
	- @param var the variable in which the error message will be exposed.
	--%>
<%@ include file="includes.tagf" %>
<%@ tag isELIgnored="false" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="var" required="true" rtexprvalue="false" %>
<%@ variable name-from-attribute="var" alias="errorMessage" %>
<spring:bind path="${path}">
	<c:forEach var="error" items="${status.errorMessages}">
		<c:set var="errorMessage" value="${error}" />
		<jsp:doBody />
	</c:forEach>
</spring:bind>