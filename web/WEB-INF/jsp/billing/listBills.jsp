<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1>
<c:choose>
<c:when test="${empty listBillsTitle}">
	<fmt:message key="listBills.title"/>
</c:when>
<c:otherwise>
	<fmt:message key="${listBillsTitle}"/>&nbsp;${invoicing.name}
</c:otherwise>
</c:choose>
</h1>

<form method="post" name="billsListForm" action="${ctx}/billing/edit.html?_navPushUrl=1">
<input type="hidden" name="action" value="mainBilling">

<c:set var="yes"><fmt:message key="app.label.yes"/></c:set>
<c:set var="no"><fmt:message key="app.label.no"/></c:set>

<display:table name="${billsList}" id="row" class="billsList" pagesize="100" requestURI="${ctx}/billing/view.html?action=${billingTablePagingAction}" export="true" >
	<display:column media="html" titleKey="app.select">
		<input type="checkbox" name="selectedBills_${row.id}" /></display:column>
	<display:column titleKey="Bill.fNumber" property="number" sortable="true" headerClass="sortable" />

	<display:column media="html" titleKey="Customer.fName" property="customerName" sortable="true" headerClass="sortable" maxLength="23"
		url="/customer/view.html?action=showDetail&_navPushUrl=1" paramId="customerId" paramProperty="customerId" />
	<display:column media="csv xml excel pdf" titleKey="Customer.fName" property="customerName" />

	<display:column titleKey="Bill.fPeriod" property="period.periodString" sortable="true" headerClass="sortable" sortProperty="period.from.time"/>
	<display:column titleKey="Bill.fIsConfirmed.short">
		${row.isConfirmed ? yes : no }</display:column>
	<display:column titleKey="Bill.fIsSent.short">
		${row.isSent ? yes : no }</display:column>
	<display:column titleKey="Bill.fIsDelivered.short">
		${row.isDelivered ? yes : no }</display:column>
	<display:column titleKey="Bill.totalPrice.short" class="priceColumn" sortable="true" headerClass="sortable" sortProperty="totalPrice">
		<fmt:formatNumber value="${row.totalPrice}" type="number" groupingUsed="true" maxFractionDigits="0" /></display:column>
	<display:column media="html" titleKey="app.action">
		<a href="${ctx}/billing/view.html?action=detailBill&billId=${row.id}&_navPushUrl=1">
		<img src="${ctx}/img/form/view_detailed.png" /></a>
		<a href="${ctx}/billing/view.html?action=emailBill&billId=${row.id}&_navPushUrl=1"
			onclick="return confirm('<fmt:message key="listBills.confirm.resend"/>')">
		<img src="${ctx}/img/form/email.png" /></a>
		<a href="${ctx}/billing/view.html?action=printBillTxt&billId=${row.id}">
		<img src="${ctx}/img/form/fileprint.png" /></a></display:column>
			
</display:table>

</form>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Actions --%>
<script type="text/javascript">
	function submitAction(action) {
		document.billsListForm.elements['action'].value=action;
		document.billsListForm.submit();
	}
</script>

<table><tr><td>
<div class="actionLinks">
<span class="actionLink"><a href="${ctx}/billing/view.html?action=mainBilling&invoicingId=${invoicing.id}">
	<fmt:message key="listBills.action.mainBilling" /></a></span>
<br />
<span class="actionLink"><a href="javascript:checkAll('billsListForm');">
	<fmt:message key="app.action.selectAll" /></a></span>
<span class="actionLink"><a href="javascript:uncheckAll('billsListForm');">
	<fmt:message key="app.action.unselectAll" /></a></span>
<span class="actionLink"><a href="javascript:inv_checkAll('billsListForm');">
	<fmt:message key="app.action.inverseselectAll" /></a></span>
<br />
<span class="actionLink"><a href="javascript:submitAction('confirmBill');">
	<fmt:message key="listBills.action.confirm" /></a></span>
<span class="actionLink"><a href="javascript:submitAction('unconfirmBill');">
	<fmt:message key="listBills.action.unconfirm" /></a></span>
<span class="actionLink"><a href="javascript:submitAction('deliverBill');">
	<fmt:message key="listBills.action.deliver" /></a></span>
<br />
<span class="actionLink"><a href="javascript:submitAction('emailBills');" 
	onclick="return confirm('<fmt:message key="mainBilling.confirm.sendDirect"/>')">
	<fmt:message key="listBills.action.emailBills" /></a></span>
<span class="actionLink"><a href="javascript:submitAction('printBillsTxt');">
	<fmt:message key="listBills.action.printBillsTxt" /></a></span>

</div>
</td></tr></table>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>