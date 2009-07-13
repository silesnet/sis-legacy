<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="detailWireless.title"/></h1>

<%-- Actions --%>
<table><tr>

<%-- View master list action if isSlave  --%>
<c:if test="${isSlave}">
<td>
	<form action="${ctx}/net/wireless/view.html" method="get">
		<input type="hidden" name="view" value="viewMasterList" />
		<input type="submit" value="<fmt:message key="detailWireless.button.viewMasterList"/>" />
	</form>
</td>
</c:if>

<%-- View sibling list action  --%>
<td>
	<form action="${ctx}/net/wireless/view.html" method="get">
		<c:choose>
			<c:when test="${isSlave}">
				<input type="hidden" name="view" value="viewSlaveList" />
				<input type="hidden" name="parentId" value="${parentNode.id}" />
			</c:when>
			<c:otherwise>
				<input type="hidden" name="view" value="viewMasterList" />
			</c:otherwise>
		</c:choose>
		<input type="submit" value="<fmt:message key="detailWireless.button.viewSiblingList"/>" />
	</form>
</td>

<%-- Update node action --%>
<td>
	<form action="${ctx}/net/wireless/edit.html" method="get">
		<input type="hidden" name="action" value="actionUpdate" />
		<input type="hidden" name="nodeId" value="${node.id}" />
		<input type="hidden" name="_navPushUrl" value="1" />
		<input type="submit" value="<fmt:message key="detailWireless.button.updateNode"/>" />
	</form>
</td>

<%-- Add wireless sibling node action --%>
<td>
	<form action="${ctx}/net/wireless/edit.html" method="get">
		<input type="hidden" name="action" value="actionAdd" />
		<c:if test="${isSlave}">
			<input type="hidden" name="parentId" value="${parentNode.id}" />
		</c:if>
		<input type="hidden" name="_navPushUrl" value="1" />
		<input type="submit" value="<fmt:message key="detailWireless.button.addSiblingNode"/>" />
	</form>
</td>

<%-- Add wireless slave node action if isMaster--%>
<c:if test="${isMaster}">
<td>
	<form action="${ctx}/net/wireless/edit.html" method="get">
		<input type="hidden" name="action" value="actionAdd" />
		<input type="hidden" name="parentId" value="${node.id}" />
		<input type="hidden" name="_navPushUrl" value="1" />
		<input type="submit" value="<fmt:message key="detailWireless.button.addSlaveNode"/>" />
	</form>
</td>
</c:if>

</tr></table>

<%-- Parent info --%>
<c:if test="${isSlave}">
	<h2><fmt:message key="detailWireless.header.parentInfo"/></h2>
	<%@ include file="/WEB-INF/jsp/wireless/parentInfo.jsp" %>
</c:if>

<h2><fmt:message key="detailWireless.header.nodeInfo"/></h2>
<table class="wirelessDetail">
<thead>
	<tr>
		<th><fmt:message key="app.field" /></th>
		<th><fmt:message key="app.value" /></th>
	</tr>
</thead>
<c:choose>
	<c:when test="${isSA}">
		<tr class="odd"><td><fmt:message key="NodeWireless.fTypeLabel" /></td>
			<td>${node.typeLabel.name}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fName" /></td>
			<td>${node.name}</td></tr>
		<tr class="odd"><td><fmt:message key="NodeWireless.fMAC" /></td>
			<td>${node.macFormatted}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fVendor" /></td>
			<td>${node.vendor}</td></tr>
		<tr class="odd"><td><fmt:message key="NodeWireless.fIP" /></td>
			<td>${node.IP}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fInfo" /></td>
			<td>${node.info}</td></tr>
	</c:when>
	<c:otherwise>
		<tr class="odd"><td><fmt:message key="NodeWireless.fTypeLabel" /></td>
			<td>${node.typeLabel.name}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fName" /></td>
			<td>${node.name}</td></tr>
		<tr class="odd"><td><fmt:message key="NodeWireless.fDomainLabel" /></td>
			<td>${node.domainLabel.name}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fSubNodesCount" /></td>
			<td><a href="${ctx}/net/wireless/view.html?view=viewSlaveList&parentId=${node.id}">
				${node.subNodesCount}</a></td></tr>
		<tr class="odd"><td><fmt:message key="NodeWireless.fMAC" /></td>
			<td>${node.macFormatted}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fMacAuthorization" /></td>
			<td>${node.macAuthorization}</td></tr>
		<tr class="odd"><td><fmt:message key="NodeWireless.fVendor" /></td>
			<td>${node.vendor}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fChannel" /></td>
			<td>${node.channel}</td></tr>
		<tr class="odd"><td><fmt:message key="NodeWireless.fRoute" /></td>
			<td>${node.route}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fIP" /></td>
			<td>${node.IP}</td></tr>
		<tr class="odd"><td><fmt:message key="NodeWireless.fWEP" /></td>
			<td>${node.wepShort}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fSSID" /></td>
			<td>${node.SSID}</td></tr>
		<tr class="odd"><td><fmt:message key="NodeWireless.fPolarizationLabel" /></td>
			<td>${node.polarizationLabel.name}</td></tr>
		<tr class="even"><td><fmt:message key="NodeWireless.fInfo" /></td>
			<td>${node.info}</td></tr>
	</c:otherwise>
</c:choose>
</table>

<%@ include file="/WEB-INF/jsp/inc/history.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>