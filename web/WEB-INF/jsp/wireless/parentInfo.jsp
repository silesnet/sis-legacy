<div class="masterNodeInfo">
	<fmt:message key="NodeWireless.fTypeLabel" />(${parentNode.typeLabel.name}), 
	<fmt:message key="NodeWireless.fName" />(<a href="${ctx}/net/wireless/view.html?view=viewDetail&nodeId=${parentNode.id}">${parentNode.name}</a>),
	<fmt:message key="NodeWireless.fDomainLabel" />(${parentNode.domainLabel.name}),
	<fmt:message key="NodeWireless.fSubNodesCount" />(<a href="${ctx}/net/wireless/view.html?view=viewSlaveList&parentId=${parentNode.id}">${parentNode.subNodesCount}</a>),
	<fmt:message key="NodeWireless.fMAC" />(${parentNode.macFormatted}),
	<fmt:message key="NodeWireless.fMacAuthorization" />(${parentNode.macAuthorization}),
	<fmt:message key="NodeWireless.fVendor" />(${parentNode.vendor}),
	<fmt:message key="NodeWireless.fChannel" />(${parentNode.channel}), 
	<fmt:message key="NodeWireless.fRoute" />(${parentNode.route}), 
	<fmt:message key="NodeWireless.fIP" />(${parentNode.IP}),
	<fmt:message key="NodeWireless.fWEP" />(${parentNode.wepShort}),
	<fmt:message key="NodeWireless.fSSID" />(${parentNode.SSID}),
	<fmt:message key="NodeWireless.fPolarizationLabel" />(${parentNode.polarizationLabel.name}),
	<fmt:message key="NodeWireless.fInfo" />(${parentNode.info})&nbsp;
	<a href="${ctx}/net/wireless/edit.html?action=actionUpdate&nodeId=${parentNode.id}&_navPushUrl=1">
		<img src="${ctx}/img/form/update.png" /></a>
</div>
