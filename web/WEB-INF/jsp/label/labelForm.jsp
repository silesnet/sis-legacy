<%@ include file="/WEB-INF/jsp/inc/formHeader.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="labelForm.title"/></h1>

<form method="POST" action="${ctx}/label/edit.html">
<s:nestedPath path="label">

<%-- IMPORTANT! sent object id with post 2 proper retrieve backing object. --%>
<c:if test="${!isNew}">
	<input type="hidden" name="labelId" value="${label.id}" />
</c:if>

<table class="editForm">
	<%-- Name --%>
	<app:formInputLine path="name" label="label.name" required="true" size="25" />
</table>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Form submit buttons --%>
<app:formActionButtons confirmDeleteMsg="editService.confirmDelete" />
	
</s:nestedPath>
</form>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>