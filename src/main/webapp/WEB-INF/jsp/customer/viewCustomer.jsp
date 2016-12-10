<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="viewCustomer.title"/></h1>

<table>
<tr><td width="45%" >

<%-- Contract --%>
<h3><fmt:message key="editCustomer.header.Contract" /><c:if test="${!isSynchronized}">*</c:if></h3>
<table class="customerDetail" width="100%">
<s:nestedPath path="customer">
	<app:viewTextLine path="contractNo" label="Customer.fContractNo" styleClass="odd" />
	<app:viewTextLine path="name" label="Customer.fName" emphasis="true" styleClass="even" />
	<app:viewTextLine path="supplementaryName" label="Customer.fSupplementaryName" styleClass="odd" />
	<app:viewTextLine path="publicId" label="${publicId_label}" styleClass="even" />
	<app:viewTextLine path="DIC" label="${dic_label}" styleClass="odd" />
	<app:viewTextLine path="symbol" label="Customer.fSymbol" styleClass="even" />
	<c:set var="mapLink" value="" />
	<c:if test="${customer.contact.address.country.name == 'enum.country.cz'}">
		<c:set var="mapLink" value="http://maps.google.cz/maps?hl=cs&amp;q=${customer.contact.address.street}, ${customer.contact.address.city}" />
	</c:if>
	<app:viewTextLine path="contact.address.street" label="Customer.fContact.fAddress.fStreet" link="${mapLink}" target="_blank" styleClass="odd" />
	<app:viewTextLine path="contact.address.city" label="Customer.fContact.fAddress.fCity" styleClass="even" />
	<app:viewTextLine path="contact.address.postalCode" label="Customer.fContact.fAddress.fPostalCode" styleClass="odd" />
	<app:viewTextLine path="contact.address.country.name" label="Customer.fContact.fAddress.fCountry" i18n="true" styleClass="even" />
	<app:viewTextLine path="contact.email" label="Customer.fContact.fEmail" link="mailto:${customer.contact.email}" styleClass="odd" />
	<app:viewTextLine path="contact.phone" label="Customer.fContact.fPhone" styleClass="even" />
	<app:viewTextLine path="contact.name" label="Customer.fContact.fName" styleClass="odd" />
	<app:viewTextLine path="connectionSpot" label="Customer.fConnectionSpot" styleClass="even" />
</s:nestedPath>
</table>

</td><td>&nbsp;&nbsp;</td><td>


<%-- BillingInfo --%>
<h3><fmt:message key="Customer.fBilling" /></h3>
<table class="customerDetail">
<s:nestedPath path="customer.billing">
	<app:viewTextLine path="lastlyBilled" label="Customer.fBilling.fLastlyBilled" styleClass="odd" />
	<app:viewTextLine path="frequency.name" label="Customer.fBilling.fFrequency" i18n="true" styleClass="even" />
	<app:viewTextLine path="isBilledAfter" label="Customer.fBilling.fIsBilledAfter" i18n="true" styleClass="odd" />
	<app:viewTextLine path="deliverByMail" label="Customer.fBilling.fDeliverByMail" i18n="true" styleClass="even" />
	<app:viewTextLine path="deliverByEmail" label="Customer.fBilling.fDeliverByEmail" i18n="true" styleClass="odd" />
	<app:viewTextLine path="deliverCopyEmail" label="Customer.fBilling.fDeliverCopyEmail" styleClass="even" />
	<app:viewLabelLine path="format" label="Customer.fBilling.format" i18n="true" styleClass="odd" />
	<app:viewTextLine path="deliverSigned" label="Customer.fBilling.deliverSigned" i18n="true" styleClass="even" />
	<app:viewTextLine path="isActive" label="Customer.fBilling.fIsActive" i18n="true" styleClass="odd" />
    <app:viewLabelLine path="status" label="Customer.fBilling.fStatus" i18n="true" styleClass="even" />
	<app:viewTextLine path="bankAccount" label="Customer.fBilling.fAccount" styleClass="odd" />
	<app:viewTextLine path="variableSymbol" label="Customer.fBilling.fVariableSymbol" styleClass="even" />
</s:nestedPath>
<s:nestedPath path="customer">
	<app:viewTextLine path="info" label="Customer.fInfo" styleClass="odd" />
</s:nestedPath>
</table>

</td></tr>
</table>

<%-- Services --%>

<c:set var="yes"><fmt:message key="app.label.yes"/></c:set>
<c:set var="no"><fmt:message key="app.label.no"/></c:set>

<c:if test="${!isNew}">
<h3><fmt:message key="editCustomer.servicesList" /></h3>
	<display:table name="${customer.services}" id="row" class="customerList">
		<display:column titleKey="app.fNo">
			${row_rowNum}</display:column>
		<display:column titleKey="Service.fName" property="shortInfo" />
		<display:column titleKey="Service.fId" property="id" />
		<display:column titleKey="Service.fPeriod.fFrom">
			<fmt:formatDate value="${row.period.from}" /></display:column>
		<display:column titleKey="Service.fPeriod.fTo">
			<fmt:formatDate value="${row.period.to}" /></display:column>
		<display:column titleKey="Service.fFrequency">
			<fmt:message key="${row.frequency.name}" /></display:column>
		<display:column titleKey="Service.fInfo">
			<small>${row.info}</small></display:column>
		<display:column titleKey="Service.fPrice" class="priceColumn">
			<fmt:formatNumber value="${row.price}" type="number" groupingUsed="true" maxFractionDigits="0" />
		</display:column>
		<display:column titleKey="app.action" url="/service/edit.html?action=showForm&_navPushUrl=1"
			paramId="serviceId" paramProperty="id">
				<img src="${ctx}/img/form/edit.png" /></display:column>
	</display:table>

<authz:authorize ifAllGranted="ROLE_ACCOUNTING">
<%-- Bills list --%>
<h3><fmt:message key="viewCustomer.header.billsList" /></h3>
<display:table name="${billsList}" id="row" class="billsList" >
	<display:column titleKey="Bill.fNumber" property="number" />
	<display:column titleKey="Bill.fPeriod" property="period.periodString" />
	<display:column titleKey="Bill.fIsConfirmed.short">
		${row.isConfirmed ? yes : no }</display:column>
	<display:column titleKey="Bill.fIsSent.short">
		${row.isSent ? yes : no }</display:column>
	<display:column titleKey="Bill.fIsDelivered.short">
		${row.isDelivered ? yes : no }</display:column>
	<display:column titleKey="Bill.totalPrice.short" class="priceColumn">
			<fmt:formatNumber value="${row.totalPrice}" type="number" groupingUsed="true" maxFractionDigits="0" />
	</display:column>
	<display:column titleKey="app.action">
		<a href="${ctx}/billing/view.html?action=detailBill&billId=${row.id}&_navPushUrl=1">
		<img src="${ctx}/img/form/view_detailed.png" /></a>
		<a href="${ctx}/billing/view.html?action=emailBill&billId=${row.id}&_navPushUrl=1"
			onclick="return confirm('<fmt:message key="listBills.confirm.resend"/>')">
		<img src="${ctx}/img/form/email.png" /></a>
		<a href="${ctx}/billing/view.html?action=printBillTxt&billId=${row.id}">
		<img src="${ctx}/img/form/fileprint.png" /></a></display:column>
</display:table>
</authz:authorize>

</c:if>


<%@ include file="/WEB-INF/jsp/inc/history.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Actions --%>
<div class="actionLinks">
<span class="actionLink"><a href="${ctx}/customer/view.html?action=goBack">
	<fmt:message key="app.action.goBack" /></a></span>
<br />
<span class="actionLink"><a href="${ctx}/customer/view.html?action=showList">
	<fmt:message key="viewCustomer.action.showList" /></a></span>
<span class="actionLink"><a href="${ctx}/customer/edit.html?action=showForm&customerId=${customer.id}&_navPushUrl=1">
	<fmt:message key="viewCustomer.action.edit" /></a></span>
<!--
<span class="actionLink"><a href="${ctx}/service/edit.html?action=showForm&customerId=${customer.id}&_navPushUrl=1">
	<fmt:message key="viewCustomer.action.addService" /></a></span>
-->
<span class="actionLink"><a href="${ctx}/service/edit.html?action=showForm&customerId=${customer.id}&formType=oneTime&_navPushUrl=1">
	<fmt:message key="viewCustomer.action.addOneTimeService" /></a></span>
<br />
<authz:authorize ifAllGranted="ROLE_ACCOUNTING">
	<c:if test="${customer.contact.address.country.name == 'enum.country.pl'}">
		<span class="actionLink"><a href="${ctx}/customer/c_${customer.exportName}.epp?action=exportToInsert&selectedCustomers_${customer.id}">
			<fmt:message key="viewCustomer.action.exportToInsert" /></a></span>
	</c:if>
</authz:authorize>
</div>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>