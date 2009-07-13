<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<c:if test="${isMaster}">
	<h1><fmt:message key="listWireless.title.master"/></h1>
</c:if>

<c:if test="${isSlave}">
	<h1><fmt:message key="listWireless.title.slave"/></h1>
</c:if>

<%-- Actions --%>
<table><tr>

<%-- View Master list action for isSlave view --%>
<c:if test="${isSlave}">
<td>
	<form action="${ctx}/net/wireless/view.html" method="get">
		<input type="hidden" name="action" value="viewMasterList" />
		<input type="submit" value="<fmt:message key="listWireless.button.viewMasterList"/>" />
	</form>
</td>
</c:if>

<%-- Add wireless node action --%>
<td>
	<form action="${ctx}/net/wireless/edit.html" method="get">
		<input type="hidden" name="action" value="actionAdd" />
		<c:if test="${isSlave}">
			<input type="hidden" name="parentId" value="${parentNode.id}" />
		</c:if>
		<input type="hidden" name="_navPushUrl" value="1" />
		<input type="submit" value="<fmt:message key="listWireless.button.addNode"/>" />
	</form>
</td>

<%-- Filter Master list action for isMaster view --%>
<c:if test="${isMaster}">
<td>
	<form action="${ctx}/net/wireless/view.html" method="get">
		<input type="hidden" name="action" value="viewMasterList" />
		<select name="_filterDomainLabelId">
			<c:forEach var="domainLabel" items="${domainLabels}">
				<option value="${domainLabel.id}"
					<c:if test="${domainLabel.id == sisFilterMap['DomainLabelId']}">
						selected
					</c:if>
				>
					${domainLabel.name}
				</option>
			</c:forEach>
		</select>
		<input type="submit" value="<fmt:message key="listWireless.button.filterDomain"/>" />
	</form>
</td>
</c:if>

</tr></table>

<%-- Parent info --%>
<c:if test="${isSlave}">
	<h2><fmt:message key="listWireless.header.parentInfo"/></h2>
	<%@ include file="/WEB-INF/jsp/wireless/parentInfo.jsp" %>
</c:if>

<%-- Wireless node list --%>
<display:table name="${nodes}" id="row" class="wirelessList">
	<display:column titleKey="app.fNo">
		${row_rowNum}</display:column>
	<display:column titleKey="NodeWireless.fTypeLabel" property="typeLabel.name" />
	<display:column titleKey="NodeWireless.fName" property="name"
		url="/net/wireless/view.html?action=viewDetail" paramId="nodeId" paramProperty="id"/>
	<c:if test="${isMaster}">
		<display:column titleKey="NodeWireless.fDomainLabel" property="domainLabel.name" />
	</c:if>
	<c:if test="${isSlave}">
		<display:column titleKey="NodeWireless.fMAC" property="macFormatted" />
	</c:if>
	<display:column titleKey="NodeWireless.fVendor" property="vendor" />
	<c:if test="${isMaster}">
		<display:column titleKey="NodeWireless.fChannel" property="channel" />
		<display:column titleKey="NodeWireless.fRoute" property="route"/>
		<display:column titleKey="NodeWireless.fSubNodesCount" property="subNodesCount"
			url="/net/wireless/view.html?action=viewSlaveList" paramId="parentId" paramProperty="id" />
	</c:if>
	<display:column title="" url="/net/wireless/edit.html?action=actionUpdate&_navPushUrl=1"
		paramId="nodeId" paramProperty="id">
			<img src="${ctx}/img/form/update.png" /></display:column>
</display:table>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>