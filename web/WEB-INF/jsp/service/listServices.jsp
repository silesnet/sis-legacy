<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="listServices.title"/></h1>

<%-- Actions --%>
<div class="actions">
<a href="${ctx}/service/edit.html?action=showForm&_navPushUrl=1" class="action">
  <fmt:message key="listServices.action.addService" /></a>
</div>

<%-- Services list --%>
<display:table name="${servicesList}" id="row" class="customerList">
		<display:column titleKey="app.fNo">
			${row_rowNum}</display:column>
		<display:column titleKey="Service.fPeriod.fFrom">
			<fmt:formatDate value="${row.period.from}" />
		</display:column>
		<display:column titleKey="Service.fName" property="name" />
		<display:column titleKey="Service.fFrequency">
			<fmt:message key="${row.frequency.name}" />
		</display:column>
		<display:column titleKey="Service.fConnectivity.fDownload" property="connectivity.download" />
		<display:column titleKey="Service.fConnectivity.fUpload" property="connectivity.upload" />
		<display:column titleKey="Service.fPrice">
			<fmt:formatNumber value="${row.price}" type="currency" currencyCode="CZK" maxFractionDigits="0" />
		</display:column>
	<display:column titleKey="app.action" url="/service/edit.html?action=showForm&_navPushUrl=1"
		paramId="serviceId" paramProperty="id">
			<img src="${ctx}/img/form/update.png" /></display:column>
</display:table>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>