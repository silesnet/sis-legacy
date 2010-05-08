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
	<app:formOptionsLine path="name" label="Service.fName" labels="${serviceNames}" naOption="true" required="true" />
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
	<%-- Info --%>
	<app:formTextareaLine path="info" label="Service.fInfo" cols="20" rows="3" />
</table>

</td><td>&nbsp;&nbsp;</td><td>

<h3><fmt:message key="Service.fConnectivity" /></h3>
<table class="editForm">
	<%-- Connectivity download --%>
	<app:formInputLine path="connectivity.download" label="Service.fConnectivity.fDownload" size="5" />
	<%-- Connectivity upload --%>
	<app:formInputLine path="connectivity.upload" label="Service.fConnectivity.fUpload" size="5" />
	<%-- Bps --%>
	<tr><td>&nbsp;</td>
	<td><spring:bind path="connectivity.bps">
		<input type="radio" name="connectivity.bps" value="M" ${service.connectivity.bps == 'M' ? 'checked ' : ''} />Mbps&nbsp;
		<input type="radio" name="connectivity.bps" value="k" ${service.connectivity.bps != 'M' ? 'checked ' : ''} />kbps
	</spring:bind></td>
	</tr>
	<%-- Connectivity isAggregated --%>
	<app:formCheckboxLine path="connectivity.isAggregated" label="Service.fConnectivity.fIsAggregated" required="true" />
</table>

</td></tr></table>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Form submit buttons --%>
<app:formActionButtons confirmDeleteMsg="editService.confirmDelete" />

</s:nestedPath>
</form>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>