<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="listCustomers.title"/></h1>

<c:choose>
<c:when test="${!empty emptyFilter}">
	<h3><fmt:message key="listCustomers.label.filterInfo" /></h3>
</c:when>
<c:otherwise>
<%-- Customers list --%>
<display:table name="${customersList}" id="row" class="customerList" pagesize="100" requestURI="${ctx}/customer/view.html?action=showList" export="true" excludedParams="*">
	<display:column titleKey="app.fNo">
		${row_rowNum}</display:column>

<%--
	<display:column media="html" titleKey="Customer.fContractNo" property="contractNo" sortable="true" headerClass="sortable" maxLength="11" />
	<display:column media="csv xml excel pdf" titleKey="Customer.fContractNo" property="contractNo" />
--%>

	<display:column media="html" titleKey="Customer.fName" property="name" sortable="true" headerClass="sortable" maxLength="23"
		url="/customer/view.html?action=showDetail&_navPushUrl=1" paramId="customerId" paramProperty="id" />
	<display:column media="csv xml excel pdf" titleKey="Customer.fName" property="name" />

	<display:column titleKey="Customer.fServices" property="servicesInfo" sortable="true" />
	<display:column titleKey="Customer.fOverallPrice" sortable="true" headerClass="sortable" class="priceColumn" sortProperty="overallPrice">
		<fmt:formatNumber value="${row.overallPrice}" type="number" groupingUsed="true" maxFractionDigits="0" />
	</display:column>

	<display:column media="html" titleKey="Customer.fContact.fPhone" property="contact.phone" maxLength="12" />
	<display:column media="csv xml excel pdf" titleKey="Customer.fContact.fPhone" property="contact.phone" />

	<display:column media="html" titleKey="app.action">
		<a href="${ctx}/customer/edit.html?action=showForm&customerId=${row.id}&_navPushUrl=1">
			<img src="${ctx}/img/form/edit.png" /></a>
		<a href="mailto:${row.contact.email}">
			<img src="${ctx}/img/form/email.png" /></a>
	</display:column>
</display:table></c:otherwise></c:choose>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Customers filtering --%>
<script type="text/javascript">
	function clearForm(formName) {
		document.forms[formName].elements["_filter.customer.name"].value="";
<!--
		document.forms[formName].elements["_filter.customer.contractNo"].value="";
-->
		document.forms[formName].elements["_filter.customer.contact.email"].value="";
		document.forms[formName].elements["_filter.customer.contact.address.street"].value="";
		document.forms[formName].elements["_filter.customer.contact.address.city"].value="";
		document.forms[formName].elements["_filter.customer.contact.address.country"].selectedIndex=0;
		document.forms[formName].elements["_filter.customer.billing.frequency"].selectedIndex=0;
		document.forms[formName].elements["_filter.customer.billing.isBilledAfter"].selectedIndex=0;
		document.forms[formName].elements["_filter.customer.billing.status"].selectedIndex=0;
		document.forms[formName].elements["_filter.customer.billing.isActive"].selectedIndex=1;
		document.forms[formName].elements["_filter.service.period.to"].value="";
		document.forms[formName].elements["_filter.customer.name"].focus();
	}
</script>
<table  cellspacing="0" cellpadding="0">
<tr><td>
<form name="customerFilterForm" method="post" action="${ctx}/customer/view.html?action=showList">
<table class="filterForm" >
	<tr><td><h1><fmt:message key="listCustomers.header.filter" /></h1></td></tr>

	<tr class="filterCaption"><td><fmt:message key="Customer.fName" /></td></tr>
	<tr><td><input type="text" name="_filter.customer.name" value="${sisFilterMap['customer.name']}"/></td></tr>
<!--
	<tr class="filterCaption"><td><fmt:message key="Customer.fContractNo" /></td></tr>
	<tr><td><input type="text" name="_filter.customer.contractNo" value="${sisFilterMap['customer.contractNo']}"/></td></tr>
-->
	<tr class="filterCaption"><td><fmt:message key="Customer.fContact.fEmail" /></td></tr>
	<tr><td><input type="text" name="_filter.customer.contact.email" value="${sisFilterMap['customer.contact.email']}"/></td></tr>

	<tr class="filterCaption"><td><fmt:message key="Customer.fContact.fAddress.fStreet" /></td></tr>
	<tr><td><input type="text" name="_filter.customer.contact.address.street" value="${sisFilterMap['customer.contact.address.street']}"/></td></tr>

	<tr class="filterCaption"><td><fmt:message key="Customer.fContact.fAddress.fCity" /></td></tr>
	<tr><td><input type="text" name="_filter.customer.contact.address.city" value="${sisFilterMap['customer.contact.address.city']}"/></td></tr>

	<tr class="filterCaption"><td><fmt:message key="Customer.fContact.fAddress.fCountry" /></td></tr>
	<tr><td><select name="_filter.customer.contact.address.country" />
		<option value="0"><fmt:message key="app.label.notAvailable" /></option>
		<html:optionsMsg items="${addressCountry}" label="name" value="id" selected="${sisFilterMap['customer.contact.address.country']}" />
		</select></td></tr>

	<tr class="filterCaption"><td><fmt:message key="Customer.fBilling.fFrequency" /></td></tr>
	<tr><td><select name="_filter.customer.billing.frequency" />
		<option value="0"><fmt:message key="app.label.notAvailable" /></option>
		<html:optionsMsg items="${billingFrequency}" label="name" value="id" selected="${sisFilterMap['customer.billing.frequency']}" />
		</select></td></tr>

	<tr class="filterCaption"><td><fmt:message key="Customer.fBilling.fIsBilledAfter" /></td></tr>
	<tr><td>
		<select name="_filter.customer.billing.isBilledAfter"/>
			<option value=""><fmt:message key="app.label.notAvailable" /></option>
			<option value="true" <c:if test="${sisFilterMap['customer.billing.isBilledAfter'] == 'true'}">selected</c:if> >
				<fmt:message key="true" /></option>
			<option value="false" <c:if test="${sisFilterMap['customer.billing.isBilledAfter'] == 'false'}">selected</c:if> >
				<fmt:message key="false" /></option>
				
	<tr class="filterCaption"><td><fmt:message key="listCustomers.label.serviceTo" /></td></tr>
	<tr><td><input type="text" name="_filter.service.period.to" value="${sisFilterMap['service.period.to']}" size="8"/>
		<app:datePicker name="_filter.service.period.to" form="'customerFilterForm'" /></td></tr>

	<tr class="filterCaption"><td><fmt:message key="Customer.fBilling.fStatus" /></td></tr>
	<tr><td><select name="_filter.customer.billing.status" />
		<option value="0"><fmt:message key="app.label.notAvailable" /></option>
		<html:optionsMsg items="${billingStatus}" label="name" value="id" selected="${sisFilterMap['customer.billing.status']}" />
		</select></td></tr>
	
	<tr class="filterCaption"><td><fmt:message key="Customer.fBilling.fIsActive" /></td></tr>
	<tr><td>
		<select name="_filter.customer.billing.isActive"/>
			<option value=""><fmt:message key="app.label.notAvailable" /></option>
			<option value="true" <c:if test="${sisFilterMap['customer.billing.isActive'] == 'true'}">selected</c:if> >
				<fmt:message key="true" /></option>
			<option value="false" <c:if test="${sisFilterMap['customer.billing.isActive'] == 'false'}">selected</c:if> >
				<fmt:message key="false" /></option>

	<tr><td class="filterSubmit">
		<input type="button" value="<fmt:message key="app.button.reset" />"
			onclick="clearForm('customerFilterForm');" />
		<input type="submit" value="<fmt:message key="app.button.filter" />"/></td></tr>
</table>
<script type="text/javascript">
	document.forms["customerFilterForm"].elements["_filter.customer.name"].select();
	document.forms["customerFilterForm"].elements["_filter.customer.name"].focus();
</script>
</form>

<%-- Actions --%>
<div class="actions">
<authz:authorize ifAllGranted="ROLE_ACCOUNTING">
<span class="actionLink"><a href="${ctx}/customer/view.html?action=showOverview&_navPushUrl=1">
	<fmt:message key="listCustomers.action.showOverview" /></a></span>
</authz:authorize>

<%-- Disabled
<span class="actionLink"><a href="${ctx}/customer/edit.html?action=showForm&_navPushUrl=1">
	<fmt:message key="listCustomers.action.addCustomer" /></a></span>
</div>
--%>

</td></tr></table>


<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>