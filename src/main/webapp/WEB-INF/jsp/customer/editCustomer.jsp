<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="editCustomer.title"/></h1>

<form name="editCustomer" method="POST" action="${ctx}/customer/edit.html">
<s:nestedPath path="customer">

<%-- IMPORTANT! sent object id with post 2 proper retrieve backing object. --%>
<c:if test="${!isNew}">
	<input type="hidden" name="customerId" value="${customer.id}" />
</c:if>

<c:set var="yes"><fmt:message key="app.label.yes"/></c:set>
<c:set var="no"><fmt:message key="app.label.no"/></c:set>

<table>
<tr><td>

<%-- Contract --%>
<h3><fmt:message key="editCustomer.header.Contract" /></h3>
<table class="editForm">
	<app:formInputLine path="contractNo" label="Customer.fContractNo" required="true" size="12" />
	<app:formInputLine path="name" label="Customer.fName" required="true" size="25" />
	<app:formInputLine path="supplementaryName" label="Customer.fSupplementaryName" size="25" />
	<app:formInputLine path="publicId" label="${publicId_label}" required="true" size="12"/>
	<app:formInputLine path="DIC" label="${dic_label}" size="12" />
	<app:formInputLine path="symbol" label="Customer.fSymbol" size="12" />
	<app:formInputLine path="contact.address.street" label="Customer.fContact.fAddress.fStreet" required="true" size="25" />
	<app:formInputLine path="contact.address.city" label="Customer.fContact.fAddress.fCity" required="true" size="15" />
	<app:formInputLine path="contact.address.postalCode" label="Customer.fContact.fAddress.fPostalCode" required="true" size="7" />
	<app:formDisabledLine path="contact.address.country" label="Customer.fContact.fAddress.fCountry" i18n="true" enum="true" />
	<%--
		<app:formEnumLine path="contact.address.country" label="Customer.fContact.fAddress.fCountry" enums="${addressCountry}" disabled="true" />
	--%>
	<app:formInputLine path="contact.email" label="Customer.fContact.fEmail" size="30" />
	<app:formInputLine path="contact.phone" label="Customer.fContact.fPhone" size="30" />
	<app:formInputLine path="contact.name" label="Customer.fContact.fName" size="30" />
	<app:formInputLine path="connectionSpot" label="Customer.fConnectionSpot" size="30" />
</table>

</td><td>&nbsp;&nbsp;</td><td>


<%-- BillingInfo --%>
<h3><fmt:message key="Customer.fBilling" /></h3>
<table class="editForm">
	<app:formDateLine path="billing.lastlyBilled" label="Customer.fBilling.fLastlyBilled" required="true" size="10" />
	<app:formEnumLine path="billing.frequency" label="Customer.fBilling.fFrequency" enums="${billingFrequency}" required="true" />
	<app:formCheckboxLine path="billing.isBilledAfter" label="Customer.fBilling.fIsBilledAfter" required="true" />
	<app:formCheckboxLine path="billing.deliverByMail" label="Customer.fBilling.fDeliverByMail" required="true" />
	<app:formCheckboxLine path="billing.deliverByEmail" label="Customer.fBilling.fDeliverByEmail" required="true" />
	<app:formInputLine path="billing.deliverCopyEmail" label="Customer.fBilling.fDeliverCopyEmail" size="30"/>
	<app:formEnumLine path="billing.format" label="Customer.fBilling.format" enums="${invoiceFormats}" required="true" />
	<app:formCheckboxLine path="billing.deliverSigned" label="Customer.fBilling.deliverSigned" required="true" />
	<app:formLabelLine path="billing.shire" label="Customer.fBilling.shire" labels="${shires}" naOption="true" />
	<app:formLabelLine path="billing.responsible" label="Customer.fBilling.responsible" labels="${responsibles}" naOption="true"/>
	<app:formLine path="billing.isActive" label="Customer.fBilling.fIsActive" required="true">
		${customer.billing.isActive ? yes : no }
	</app:formLine>
	<app:formEnumLine path="billing.status" label="Customer.fBilling.fStatus" enums="${billingStatus}" required="true" />
    <app:formInputLine path="billing.accountNumber" label="Customer.fBilling.fAccount" size="17"/>
    <app:formInputLine path="billing.bankCode" label="Customer.fBilling.fBank" size="4"/>
	<app:formTextareaLine path="info" label="Customer.fInfo" cols="23" rows="5" />
</table>

</td></tr>
</table>

<%-- Services --%>

<c:if test="${!isNew}">
<h3><fmt:message key="editCustomer.servicesList" /></h3>
	<display:table name="${customer.services}" id="row" class="customerList">
		<display:column titleKey="app.fNo">
			${row_rowNum}</display:column>
		<display:column titleKey="Service.fName" property="shortInfo" />
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
</c:if>

<%@ include file="/WEB-INF/jsp/inc/history.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Form submit buttons --%>
<div class="formButtons">
	<input type="submit" name="cancel" value="<fmt:message key="app.button.cancel" />" />
	<c:choose>
		<c:when test="${isNew}">
			<br />
			<input type="submit" name="insert" value="<fmt:message key="app.button.insert" />" />
		</c:when>
		<c:otherwise>
			<c:if test="${!customer.billing.isActive}">
				<input type="submit" name="delete" value="<fmt:message key="app.button.delete" />"
			    	onclick="return confirm('<fmt:message key="editCustomer.confirmDelete" />')" />
			</c:if>
			<br />
			<input type="submit" name="update" value="<fmt:message key="app.button.update" />" />
		</c:otherwise>
	</c:choose>
</div>

</s:nestedPath>
</form>


<%-- Actions --%>
<c:if test="${!isNew}">
<div class="actionLinks">

<c:choose>
	<c:when test="${customer.billing.isActive}">
		<form name="customerNewStatus" method="POST" action="${ctx}/customer/edit.html?action=deactivate&customerId=${customer.id}&_navPushUrl=1">
		<span class="actionLink">
			<a href="javascript:document.customerNewStatus.submit()" class="action"
				onclick="return confirm('<fmt:message key="editCustomer.confirm.deactivate"/>')">
			<fmt:message key="editCustomer.action.deactivate" /></a></span>
			<select name="newStatusId">
				<html:optionsMsg items="${billingStatusComplementary}" label="name" value="id" />
			</select>
		</form>
	</c:when>
	<c:otherwise>
		<form name="customerNewStatus" method="POST" action="${ctx}/customer/edit.html?action=activate&customerId=${customer.id}&_navPushUrl=1">
			<select name="newStatusId">
				<html:optionsMsg items="${billingStatusComplementary}" label="name" value="id" />
			</select>
		<span class="actionLink">
			<a href="javascript:document.customerNewStatus.submit()" class="action"
				onclick="return confirm('<fmt:message key="editCustomer.confirm.activate"/>')">
			<fmt:message key="editCustomer.action.activate" /></a></span>
		</form>
	</c:otherwise>
</c:choose>

<span class="actionLink"><a href="${ctx}/service/edit.html?action=showForm&customerId=${customer.id}&_navPushUrl=1" class="action">
	<fmt:message key="listServices.action.addService" /></a></span>
<span class="actionLink"><a href="${ctx}/service/edit.html?action=showForm&customerId=${customer.id}&formType=oneTime&_navPushUrl=1">
	<fmt:message key="viewCustomer.action.addOneTimeService" /></a></span>
<br />
<script language="javascript">
	function enableSymbol(value)
	{
		document.editCustomer.symbol.disabled=value;
	}
	enableSymbol(true);
</script>
<span class="actionLink"><a href="javascript:enableSymbol(false)">
	<fmt:message key="viewCustomer.action.enableSymbolChange" /></a></span>
</div>
</c:if>

	


<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>