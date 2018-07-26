<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="editService.title"/></h1>

<form name="editService" method="POST" action="${ctx}/service/edit.html">
<s:nestedPath path="service">

<%-- IMPORTANT! sent object id with post 2 proper retrieve backing object. --%>
<c:if test="${!isNew}">
	<input type="hidden" name="serviceId" value="${service.id}" />
</c:if>
<%-- IMPORTANT! need 2 bind pre set values that are not edited directly,
  -- applies only to NEW command objects, on existing objects,
  -- unedited values are set on binding from persistent srorage. --%>
<c:if test="${isNew}">
	<html:input type="hidden" path="customerId" />
</c:if>

<table>
<tr><td>

<h3><fmt:message key="editService.header.Parameters" /></h3>
<table class="editForm">
	<%-- Name --%>
	<app:formOptionsLine path="name" label="Service.fName" labels="${products}" required="true" />
	<%-- AdditionalName --%>
	<app:formInputLine path="additionalName" label="Service.additionalName" size="20" />
	<%-- Frequency --%>
	<app:formEnumLine path="frequency" label="Service.fFrequency" enums="${serviceFrequency}" required="true" />
	<%-- Period from --%>
	<app:formDateLine path="period.from" label="Service.fPeriod.fFrom" required="true" size="10" />
	<%-- Period to --%>
	<app:formDateLine path="period.to" label="Service.fPeriod.fTo" size="10" />
	<%-- Price --%>
	<fmt:message key="${money_label}" var="money_label_str" />
	<app:formInputLine path="price" label="Service.fPrice" required="true" size="7" valueType="${money_label_str}" />
	<app:formCheckboxLine path="includeDph" label="Service.fIncludeDph" required="true" />
	<%-- Info --%>
	<app:formTextareaLine path="info" label="Service.fInfo" cols="20" rows="3" />
</table>

</td><td>&nbsp;&nbsp;</td><td>

</td></tr></table>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Form submit buttons --%>
<app:formActionButtons confirmDeleteMsg="editService.confirmDelete" />
</s:nestedPath>
</form>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>