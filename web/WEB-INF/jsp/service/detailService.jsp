<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="listServices.title"/></h1>

<%-- Services list --%>
<display:table name="${servicesList}" id="row" class="customerList">
	<display:column titleKey="app.fNo">
		${row_rowNum}</display:column>
	<display:column titleKey="Service.fType" property="type.name" />
	<display:column titleKey="Service.fName" property="name"
		url="/service/view.html?view=viewServiceDetail" paramId="serviceId" paramProperty="id"/>
	<display:column titleKey="Service.fPrice" property="price"/>
	<display:column title="" url="/service/edit.html?action=actionUpdateService&_navPushUrl=1"
		paramId="serviceId" paramProperty="id">
			<img src="${ctx}/img/form/update.png" /></display:column>
</display:table>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>