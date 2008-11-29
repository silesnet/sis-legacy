<%--
	- errors
	- 
	- Output a list of errors for the command or bean specified in the
	- 'path' attribute. The markup enclosing each error message can be
	- customized by editing this tag file directly.
	-
	- @param path the name of the field to bind to (required)
	--%>
<%@ include file="includes.tagf" %>
<%@ tag isELIgnored="false" %>
<%@ attribute name="path" required="true" %>
<spring:bind path="${path}">
	<c:if test="${status.error}">
		<ul class="errors">
			<c:forEach var="error" items="${status.errorMessages}"><li>${error}</li></c:forEach>
		</ul>
	</c:if>
</spring:bind>