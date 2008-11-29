<div id="wirelessShortInfo">
<s:nestedPath path="wirelessParent">
<h3><fmt:message key="wirelessShortInfo.label.master" /></h3>
<table width="100%">
<tr><td><h2>
<a href="${ctx}/net/wireless/view.html?action=showDetail&wirelessId=${wirelessParent.id}&_navPushUrl=1">
${wirelessParent.name}</a>&nbsp;
<a href="${ctx}/net/wireless/edit.html?action=showForm&wirelessId=${wirelessParent.id}&_navPushUrl=1">
	<img src="${ctx}/img/form/edit.png" /></a></h2></td><td></td><td></td></tr>
<tr>	
	<td><table width="100%">
		<app:viewLabelLine path="domain" label="wireless.domain" />
		<app:viewTextLine path="subNodesCount" label="wireless.subNodesCount"
			link="${ctx}/net/wireless/view.html?action=showList&parentId=${wirelessParent.id}" />
		<app:viewLabelLine path="type" label="wireless.type" i18n="true" />
		<app:viewLabelLine path="frequency" label="wireless.frequency" />
		<app:viewTextLine path="vendor" label="wireless.vendor" />
	</table></td>
	<td><table width="100%">
		<app:viewTextLine path="macFormatted" label="wireless.mac" />
		<app:viewTextLine path="ip" label="wireless.ip" />
		<app:viewTextLine path="macAuthorization" label="wireless.macAuthorization" i18n="true" />
		<app:viewTextLine path="ssid" label="wireless.ssid" />
		<app:viewTextLine path="shortWep" label="wireless.wep" />
	</table></td>
	<td><table width="100%">
		<app:viewLabelLine path="polarization" label="wireless.polarization" i18n="true" />
		<app:viewTextLine path="route" label="wireless.route" />
		<app:viewTextLine path="info" label="wireless.info" />
		<app:viewTextLine path="active" label="wireless.active" i18n="true" />
	</table></td>
</tr>	
</table>
</s:nestedPath>
</div>
