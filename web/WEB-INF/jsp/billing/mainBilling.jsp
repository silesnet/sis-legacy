<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>
<c:if test="${empty country}">
	<c:set var="country" value="cz" />
</c:if>
<c:if test="${!empty invoicing }">
<h1><fmt:message key="mainBilling.title"/>&nbsp;${invoicing.name}</h1>
<table width="100%">
<tr><td>
<table>
	<tr><td><fmt:message key="mainBilling.status.invoicingDate" /></td><td>: <i18n:formatDate value="${invoicing.invoicingDate}" pattern="dd.MM.yyyy" /></td></tr>
	<tr><td><fmt:message key="mainBilling.status.numberingBase" /></td><td>: ${invoicing.numberingBase}</td></tr>
	<tr><td><fmt:message key="mainBilling.status.countToSend" /></td><td>: ${countToSend}</td></tr>
	<tr><td><fmt:message key="mainBilling.status.countSent" /></td><td>: ${countSent}</td></tr>
	<tr><td><fmt:message key="mainBilling.status.countUndelivered" /></td><td>: ${countUndelivered}</td></tr>
	<tr><td><fmt:message key="mainBilling.status.countDelivered" /></td><td>: ${countDelivered}</td></tr>
	<tr><td><b><fmt:message key="mainBilling.status.countAll" /></b></td><td>: <b>${countAll}</b></td></tr>
<!-- NOT IMPLEMENTED YET IN DAO, alway return 0
	<tr><td><b><fmt:message key="mainBilling.status.sumAll" /></b></td><td>: <b><i18n:formatNumber value="${sumAll}" pattern="###,###,##0" /></b>
		<c:if test="${invoicing.country == 'enum.country.cz'}">
			CZK
		</c:if>
		<c:if test="${invoicing.country == 'enum.country.pl'}">
			PLN
		</c:if>
	</td></tr>
 -->
 </table>
</td><td style="text-align: right;">
<script type="text/javascript">
  function submitChangeInvoicingForm(object) {
    document.changeInvoicingForm.submit();
  }
</script>
<form name="changeInvoicingForm" method="get" action="${ctx}/billing/view.html">
  <label><fmt:message key="mainBilling.label.changeInvoicing" /></label>
  <input type="hidden" name="action" value="mainBilling" />
  <select name="invoicingId" onChange="submitChangeInvoicingForm(this)" />
	<html:options items="${invoicings}" label="name" value="id" selected="${invoicing.id}" />
  </select>
</form>
</td></tr>
</table>

<h2><fmt:message key="mainBilling.header.billingAudit"/></h2>

<display:table name="${billingAudit}" id="row" class="auditList" pagesize="100" requestURI="${ctx}/billing/view.html?action=mainBilling" export="true">
	<display:setProperty name="export.amount" value="list" />
    <display:column titleKey="HistoryItem.fTimeStamp" sortable="true" headerClass="sortable" sortProperty="timeStamp.time"><fmt:formatDate value="${row.timeStamp}" pattern="dd.MM.yyyy HH:mm:ss"/></display:column>
    <display:column titleKey="HistoryItem.fUser" property="user.loginName" sortable="true" headerClass="sortable"/>

    <display:column media="html" titleKey="mainBilling.fCustomer" sortable="true" headerClass="sortable">
    	<c:if test="${!empty row.customerId}">
			<a href="${ctx}/customer/view.html?action=showDetail&customerId=${row.customerId}&_navPushUrl=1">
				${row.customerName}</a></c:if></display:column>
    <display:column media="csv xml excel pdf" titleKey="mainBilling.fCustomer" property="customerName" />

    <display:column media="html" titleKey="mainBilling.fMsg" maxLength="25" sortable="true" headerClass="sortable">
    	<c:if test="${!empty row.oldValue}">
		<fmt:message key="${row.oldValue}" /></c:if></display:column>
    <display:column media="csv xml excel pdf" titleKey="mainBilling.fMsg">
    	<c:if test="${!empty row.oldValue}">
		<fmt:message key="${row.oldValue}" /></c:if></display:column>

    <display:column titleKey="mainBilling.fStatus" sortable="true" headerClass="sortable">
    	<c:if test="${!empty row.newValue}">
		<c:choose>
	    	<c:when test="${!empty row.customerId}">
				<fmt:message key="${row.newValue}" /></c:when>
	    	<c:otherwise>
	    		${row.newValue}
	    	</c:otherwise>
    	</c:choose>
		</c:if></display:column>
</display:table>
</c:if>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Actions --%>
<table>
<tr><td>
<%-- Invoices filtering --%>
<script type="text/javascript">
	function clearForm(formName) {
		document.forms[formName].elements["_filter.bill.number"].value="";
		document.forms[formName].elements["_filter.bill.number"].focus();
	}
</script>
<form name="billFilterForm" method="post" action="${ctx}/billing/view.html?action=showList">
<table class="filterForm" >
	<tr><td><h1><fmt:message key="mainBilling.header.filter" /></h1></td></tr>

	<tr class="filterCaption"><td><fmt:message key="bill.number" /></td></tr>
	<tr><td><input type="text" name="_filter.bill.number" value="${sisFilterMap['bill.number']}"/></td></tr>

	<tr><td class="filterSubmit">
		<input type="button" value="<fmt:message key="app.button.reset" />"
			onclick="clearForm('billFilterForm');" />
		<input type="submit" value="<fmt:message key="app.button.filter" />"/></td></tr>
</table>
<script type="text/javascript">
	document.forms["billFilterForm"].elements["_filter.bill.number"].select();
	document.forms["billFilterForm"].elements["_filter.bill.number"].focus();
</script>
</form>

<div class="actions">
<c:if test="${!empty invoicing }">
<span class="actionLink"><a href="${ctx}/billing/view.html?action=showPreparedUnconfirmed&invoicingId=${invoicing.id}">
	<fmt:message key="mainBilling.action.showPreparedUnconfirmed" /> (${countUnconfirmed})</a></span>
<span class="actionLink"><a href="${ctx}/billing/view.html?action=showPreparedConfirmed&invoicingId=${invoicing.id}">
	<fmt:message key="mainBilling.action.showPreparedConfirmed" /> (${countConfirmed})</a></span>
<span class="actionLink"><a href="${ctx}/billing/view.html?action=showSentUndelivered&invoicingId=${invoicing.id}">
	<fmt:message key="mainBilling.action.showSentUndelivered" /> (${countUndelivered})</a></span>
<span class="actionLink"><a href="${ctx}/billing/view.html?action=showSentDelivered&invoicingId=${invoicing.id}">
	<fmt:message key="mainBilling.action.showSentDelivered" /> (${countDelivered})</a></span>
<span class="actionLink"><a href="${ctx}/billing/view.html?action=showSentMail&invoicingId=${invoicing.id}">
	<fmt:message key="mainBilling.action.showSentMail" /> (${countSnail})</a></span>
<br />	
</c:if>
<hr />
<div class="mainBilling_generateBills">
<form name="prepareBillsForm" method="get" action="${ctx}/billing/edit.html">
	<input type="hidden" name="action" value="prepareBills" />
	<input type="hidden" name="country" value="${country}" />
	<span class="mainBilling_formCaption"><fmt:message key="mainBilling.label.billingDate" /></span>
	<fmt:formatDate var="billingDateStr" value="${billingDate}" pattern="dd.MM.yyyy" />
	<input type="text" name="billingDate" value="${billingDateStr}" size="8" />
	<app:datePicker name="billingDate" form="'prepareBillsForm'" />
	<span class="mainBilling_formCaption"><fmt:message key="mainBilling.label.billsNumbering" /></span>
	<input type="text" name="billsNumbering" value="" size="12" />
</form>
<span class="actionLink"><a href="javascript:document.prepareBillsForm.submit();"
	onclick="return confirm('<fmt:message key="mainBilling.confirm.prepare"/>')">
	<fmt:message key="mainBilling.action.prepareBills" /> (${fn:toUpperCase(country)})</a></span>
</div>
<hr />
<!-- 
<span class="actionLink"><a href="${ctx}/billing/edit.html?action=sendConfirmed"
	onclick="return confirm('<fmt:message key="mainBilling.confirm.send"/>')">
	<fmt:message key="mainBilling.action.sendConfirmed" /></a></span>
-->
<div class="mainBilling_sendingStatus">
	<c:choose>
		<c:when test="${invoiceSendingEnabled}">
			<p><fmt:message key="mainBilling.status.sendingEnabled" /><br />
			<a href="${ctx}/billing/edit.html?action=toggleSendingInvoices&sendingInvoicesFlag=false&country=${country}&_navPushUrl=1"
				onclick="return confirm('<fmt:message key="mainBilling.confirm.sendingDisable"/>')">
				<fmt:message key="mainBilling.action.disableSending" /> (${fn:toUpperCase(country)})</a></p>
		</c:when>
		<c:otherwise>
			<p><fmt:message key="mainBilling.status.sendingDisabled" /><br />
			<a href="${ctx}/billing/edit.html?action=toggleSendingInvoices&sendingInvoicesFlag=true&country=${country}&_navPushUrl=1"
				onclick="return confirm('<fmt:message key="mainBilling.confirm.sendingEnable"/>')">
				<fmt:message key="mainBilling.action.enableSending" /> (${fn:toUpperCase(country)})</a></p>
		</c:otherwise>
	</c:choose>
</div>
<hr />
<c:if test="${!empty invoicing }">
<c:if test="${country == 'pl'}">
<span class="actionLink"><a href="${ctx}/billing/bills_pl_${billingMonth}.epp?action=exportSentToInsert&invoicingId=${invoicing.id}">
	<fmt:message key="mainBilling.action.exportSentToInsert" /></a></span>
<hr />
</c:if>
<span class="actionLink"><a href="${ctx}/billing/edit.html?action=resendUndelivered&invoicingId=${invoicing.id}"
	onclick="return confirm('<fmt:message key="mainBilling.confirm.resend"/>')">
	<fmt:message key="mainBilling.action.resendUndelivered" /></a></span>
<hr />
</c:if>
</div>
</td></tr>
</table>

<%@include file="/WEB-INF/jsp/inc/footer.jsp" %>