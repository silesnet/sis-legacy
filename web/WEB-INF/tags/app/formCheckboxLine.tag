<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="required" %>
<%@ attribute name="valueType" %>
<%@ attribute name="styleClass" %>

<app:formLine label="${label}" path="${path}" required="${required}" valueType="${valueType}" styleClass="${styleClass}">
	<s:bind path="${path}">
		<input type="hidden" name="_${status.expression}" />
		<input type="checkbox" name="${status.expression}" value="true" ${status.value ? 'checked ' : ''} />
	</s:bind>
</app:formLine>
