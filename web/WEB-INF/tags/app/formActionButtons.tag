<%-- these are standard buttons, defined in sis-servlet.xml:actionMethodResolver
     if need additional action use hidden input named 'action' and set method
     onclick event method on submit button like this:
     onclick="formName.action.value='doSomethink'"--%>

<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ tag dynamic-attributes="attributes" isELIgnored="false" %>
<%@ attribute name="confirmDeleteMsg" %>
<html:attributes var="attrString" attributeMap="${attributes}" >
<div class="formButtons" ${attrString}>
	<table>
	<tr><td>
	<input type="submit" name="cancel" value="<fmt:message key="app.button.cancel" />" />
	</td></tr>
	<c:choose>
		<c:when test="${isNew}">
			<tr><td>
			<input type="submit" name="insert" value="<fmt:message key="app.button.insert" />" />
			</td></tr>
		</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${!empty confirmDeleteMsg}">
				<tr><td>
				<input type="submit" name="delete" value="<fmt:message key="app.button.delete" />"
			    	onclick="return confirm('<fmt:message key="${confirmDeleteMsg}"/>')" />
			    </td></tr>
			</c:when>
			<c:otherwise>
				<tr><td>
				<input type="submit" name="delete" value="<fmt:message key="app.button.delete" />" />
				</td></tr>
			</c:otherwise>
		</c:choose>
		<tr><td>
			<input type="submit" name="update" value="<fmt:message key="app.button.update" />" />
		</td></tr>
	</c:otherwise>
	</c:choose>
	</table>
</div>
</html:attributes>