<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="label" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="required" %>
<%@ attribute name="valueType" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="cols" %>
<%@ attribute name="rows" %>

<app:formLine label="${label}" path="${path}" required="${required}" valueType="${valueType}" styleClass="${styleClass}">
	<html:textarea path="${path}" cols="${cols}" rows="${rows}" />
</app:formLine>
