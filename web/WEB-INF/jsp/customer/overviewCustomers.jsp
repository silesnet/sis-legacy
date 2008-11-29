<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="overviewCustomers.title"/></h1>

<table><tr>
<td>

<h2><fmt:message key="enum.country.cz"/></h2>
<display:table name="${overviewCustomersCZ}" id="row" class="customerList">
	<display:column titleKey="app.field">
		<fmt:message key="${row.key}" /></display:column>
	<display:column titleKey="app.value" property="value" />
</display:table>

</td><td>&nbsp;&nbsp;</td><td>

<h2><fmt:message key="enum.country.pl"/></h2>
<display:table name="${overviewCustomersPL}" id="row" class="customerList">
	<display:column titleKey="app.field">
		<fmt:message key="${row.key}" /></display:column>
	<display:column titleKey="app.value" property="value" />
</display:table>

</td><td>&nbsp;&nbsp;</td><td>

<h2><fmt:message key="overviewCustomers.total"/></h2>
<display:table name="${overviewCustomers}" id="row" class="customerList">
	<display:column titleKey="app.field">
		<fmt:message key="${row.key}" /></display:column>
	<display:column titleKey="app.value">
		<span class="totalsColumn">${row.value}</display:column>
</display:table>

</td></tr></table>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Actions --%>
<div class="actionLinks">
<span class="actionLink"><a href="${ctx}/customer/view.html?action=goBack">
	<fmt:message key="app.action.goBack" /></a></span>
<br />
<form name="exchangeRateForm" method="post" action="${ctx}/customer/edit.html">
	<input type="hidden" name="action" value="updateExchangeRate" />
	<input type="text" name="exchangeRate" value="${exchangeRate.value}" size="10" />
</form>
<span class="actionLink"><a href="javascript:document.exchangeRateForm.submit();">
	<fmt:message key="overviewCustomers.action.updateExchange" /></a></span>

</div>


<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>