<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="labelList.title"/></h1>

<%-- Label list --%>
<display:table name="${labels}" id="row" class="customerList" pagesize="100" requestURI="${ctx}/label/view.html?action=showList" export="true" excludedParams="*" >
	<display:column titleKey="app.fNo">
		${row_rowNum}</display:column>
	<display:column titleKey="label.name" property="name" sortable="true" headerClass="sortable" />
	<display:column media="html" titleKey="app.action">
		<a href="${ctx}/label/edit.html?action=showForm&labelId=${row.id}&_navPushUrl=1">
			<img src="${ctx}/img/form/edit.png" /></a>
	</display:column>
</display:table>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Actions --%>
<div class="actions">
<span class="actionLink"><a href="${ctx}/label/edit.html?action=showForm&_navPushUrl=1">
	<fmt:message key="labelList.action.addLabel" /></a></span>
</div>
</td></tr></table>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>