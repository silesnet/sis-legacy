<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<c:choose>
	<c:when test="${empty wirelessParent}">
		<c:set var="isMaster" value="true" />
		<h1><fmt:message key="wirelessList.masterTitle"/></h1>
	</c:when>
	<c:otherwise>
		<c:set var="isMaster" value="false" />
		<h1><fmt:message key="wirelessList.slaveTitle"/></h1>
		<%@ include file="/WEB-INF/jsp/wireless/wirelessShortInfo.jsp" %>
	</c:otherwise>
</c:choose>

<%-- Wireless list --%>
<display:table name="${wirelesss}" id="row" class="wirelessList" pagesize="1000" requestURI="${ctx}/net/wireless/view.html?action=showList" export="true" excludedParams="*" >
	<display:column titleKey="app.fNo">
		${row_rowNum}</display:column>
	<display:column titleKey="wireless.type" sortable="true" headerClass="sortable" >
		<fmt:message key="${row.type.name}" /></display:column>
	<display:column titleKey="wireless.name" property="name" sortable="true" headerClass="sortable"
		url="/net/wireless/view.html?action=showDetail&_navPushUrl=1" paramId="wirelessId" paramProperty="id" />
<c:if test="${isMaster == 'true'}">
	<display:column titleKey="wireless.frequency" sortable="true" headerClass="sortable">
	 ${row.frequency.name}</display:column>
	<display:column titleKey="wireless.domain" property="domain.name" sortable="true" headerClass="sortable" />
</c:if>
	<display:column titleKey="wireless.vendor" property="vendor" sortable="true" headerClass="sortable" />
<c:if test="${isMaster == 'true'}">
	<display:column titleKey="wireless.route" property="route" sortable="true" headerClass="sortable" />
</c:if>
<c:if test="${isMaster == 'false'}">
	<display:column titleKey="wireless.mac" property="macFormatted" sortable="true" headerClass="sortable" />
	<display:column titleKey="wireless.ip" property="ip" sortable="true" headerClass="sortable" />
</c:if>
<c:if test="${isMaster == 'true'}">
	<display:column titleKey="wireless.subNodesCount" property="subNodesCount" sortable="true" headerClass="sortable" sortProperty="subNodesCount"
		url="/net/wireless/view.html?action=showList" paramId="parentId" paramProperty="id" />
</c:if>
	<display:column media="html" titleKey="app.action">
		<a href="${ctx}/net/wireless/edit.html?action=showForm&${commandId}=${row.id}&_navPushUrl=1">
			<img src="${ctx}/img/form/edit.png" /></a>
	</display:column>
</display:table>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<table  cellspacing="0" cellpadding="0">
<tr><td>

<c:if test="${isMaster == 'true'}">

<%-- Wireless filtering --%>
<script type="text/javascript">
	function clearForm(formName) {
		document.forms[formName].elements["_filter.wireless.domain"].selectedIndex=0;
		document.forms[formName].elements["_filter.wireless.name"].value="";
		document.forms[formName].elements["_filter.wireless.name"].focus();
	}
</script>
<form name="wirelessFilterForm" method="post" action="${ctx}/net/wireless/view.html?action=showList">
<table class="filterForm" >
	<tr><td><h1><fmt:message key="${view}.header.filter" /></h1></td></tr>

	<tr class="filterCaption"><td><fmt:message key="wireless.domain" /></td></tr>
	<tr><td><select name="_filter.wireless.domain" />
		<option value="0"><fmt:message key="app.label.notAvailable" /></option>
		<html:options items="${domains}" label="name" value="id" selected="${sisFilterMap['wireless.domain']}" />
		</select></td></tr>

	<tr class="filterCaption"><td><fmt:message key="wireless.name" /></td></tr>
	<tr><td><input type="text" name="_filter.wireless.name" value="${sisFilterMap['wireless.name']}"/></td></tr>

	<tr><td class="filterSubmit">
		<input type="button" value="<fmt:message key="app.button.reset" />"
			onclick="clearForm('wirelessFilterForm');" />
		<input type="submit" value="<fmt:message key="app.button.filter" />"/></td></tr>
</table>
<script type="text/javascript">
	document.forms["wirelessFilterForm"].elements["_filter.wireless.name"].select();
	document.forms["wirelessFilterForm"].elements["_filter.wireless.name"].focus();
</script>
</form>
</c:if>

<%-- Actions --%>
<div class="actionLinks">
<c:if test="${isMaster == 'false'}">
<span class="actionLink"><a href="${ctx}/net/wireless/view.html?action=showList">
	<fmt:message key="${view}.action.viewMasterList" /></a></span>
	<br />
<span class="actionLink"><a href="${ctx}/net/wireless/edit.html?action=showForm&parentId=${wirelessParent.id}&_navPushUrl=1">
	<fmt:message key="${view}.action.addLabel" /></a></span>
</c:if>
<c:if test="${isMaster == 'true'}">
<span class="actionLink"><a href="${ctx}/net/wireless/edit.html?action=showForm&_navPushUrl=1">
	<fmt:message key="${view}.action.addLabel" /></a></span>
</c:if>
</div>
</td></tr></table>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>