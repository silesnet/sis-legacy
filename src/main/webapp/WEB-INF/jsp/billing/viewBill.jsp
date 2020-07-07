<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="detailBill.title"/></h1>

<s:nestedPath path="bill">

<c:set var="moneyStr"><fmt:message key="${money_key}" /></c:set>

<%-- Bill --%>
<table class="billsDetail">
	<app:viewTextLine path="number" label="Bill.fNumber" styleClass="odd" />
	<app:viewTextLine path="period.periodString" label="Bill.fPeriod" styleClass="even" />
	<app:viewTextLine path="customerName" label="Customer.fName" styleClass="odd" link="${ctx}/customer/view.html?action=showDetail&customerId=${bill.customerId}&_navPushUrl=1" />
	<app:viewTextLine path="billingDate" label="Bill.fBillingDate" styleClass="even" />
	<app:viewTextLine path="purgeDate" label="Bill.fPurgeDate" styleClass="odd" />
	<app:viewTextLine path="hashCode" label="Bill.fHashCode" styleClass="even" />
	<app:viewTextLine path="isConfirmed" label="Bill.fIsConfirmed" styleClass="odd" i18n="true" />
	<app:viewTextLine path="isSent" label="Bill.fIsSent" styleClass="even" i18n="true" />
	<app:viewTextLine path="isDelivered" label="Bill.fIsDelivered" styleClass="odd" i18n="true" />
	<app:viewTextLine path="isArchived" label="Bill.fIsArchived" styleClass="even" i18n="true" />
	<app:viewLine label="Bill.totalPrice" styleClass="odd" emphasis="true" valueType="${moneyStr}">
			<fmt:formatNumber value="${bill.netRounded}" type="number" groupingUsed="true" maxFractionDigits="2" />
	</app:viewLine>
	<app:viewTextLine path="vat" label="Bill.fVat" styleClass="even" valueType="%" />
</table>

<h3><fmt:message key="Bill.fItems" /></h3>
	<display:table name="${bill.items}" id="row" class="billsList">
		<display:column titleKey="app.fNo">
			${row_rowNum}</display:column>
		<display:column titleKey="BillItem.fText" property="text" />
		<display:column titleKey="BillItem.fAmount" property="amount" />
		<display:column titleKey="BillItem.fPrice" >
			<fmt:formatNumber value="${row.price}" type="number" groupingUsed="true" maxFractionDigits="0" />
		</display:column>
		<display:column titleKey="BillItem.linePrice">
			<fmt:formatNumber value="${row.net}" type="number" groupingUsed="true" maxFractionDigits="2" />
		</display:column>
	</display:table>

</s:nestedPath>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Actions --%>
<div class="actionLinks">
<span class="actionLink"><a href="${ctx}/billing/view.html?action=goBack">
	<fmt:message key="app.action.goBack" /></a></span>
<c:if test="${!bill.isArchived}">
<br />
<span class="actionLink"><a href="${ctx}/billing/edit.html?action=confirmBill&selectedBills_${bill.id}=1&_navPushUrl=1">
	<fmt:message key="listBills.action.confirm" /></a></span>
<span class="actionLink"><a href="${ctx}/billing/edit.html?action=unconfirmBill&selectedBills_${bill.id}=1&_navPushUrl=1">
	<fmt:message key="listBills.action.unconfirm" /></a></span>
<span class="actionLink"><a href="${ctx}/billing/edit.html?action=deliverBill&selectedBills_${bill.id}=1&_navPushUrl=1">
	<fmt:message key="listBills.action.deliver" /></a></span>
</c:if>
<br />
<form name="emailBillForm" method="get" action="${ctx}/billing/edit.html">
	<input type="hidden" name="action" value="emailBill" />
	<input type="hidden" name="billId" value="${bill.id}" />
	<input type="hidden" name="_navPushUrl" value="1" />
  <span class="mainBilling_formCaption"><fmt:message key="mainBilling.label.recipient" /></span>
  <input type="text" name="recipient" value="${customer.contact.email}" />
</form>
<span class="actionLink"><a href="javascript:document.emailBillForm.submit();"
	onclick="return confirm('<fmt:message key="listBills.confirm.resend"/>')">
	<fmt:message key="listBills.action.emailBill" /></a></span>
<%-- needs fix in BillingConroller.exportToWinDuo()
<span class="actionLink"><a href="${ctx}/billing/bill_${bill.number}.bwd?action=exportToWinduo&billId=${bill.id}">
	<fmt:message key="listBills.action.exportToWinduo" /></a></span>
--%>
<span class="actionLink"><a href="${ctx}/billing/edit.html?action=printBillTxt&billId=${bill.id}">
	<fmt:message key="listBills.action.printTxt" /></a></span>

</div>
<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>
