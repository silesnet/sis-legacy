<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="wirelessDetail.title"/></h1>

<c:choose>
	<c:when test="${empty wirelessParent}">
		<c:set var="isMaster" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="isMaster" value="false" />
		<%@ include file="/WEB-INF/jsp/wireless/wirelessShortInfo.jsp" %>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${wireless.type.name == 'enum.wireless.sa'}">
		<c:set var="isSA" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="isSA" value="false" />
	</c:otherwise>
</c:choose>

<s:nestedPath path="wireless">
<h2 class="wirelessDetail">${wireless.name}</h2>
<c:choose>
	<c:when test="${isSA}">
		<table class="wirelessDetail">
			<app:viewTextLine path="name" label="wireless.name" emphasis="true" styleClass="even" />
			<app:viewLabelLine path="type" label="wireless.type" i18n="true" styleClass="odd" />
			<app:viewTextLine path="vendor" label="wireless.vendor" styleClass="even" />
			<app:viewTextLine path="macFormatted" label="wireless.mac" styleClass="odd" />
			<app:viewTextLine path="ip" label="wireless.ip" styleClass="even" />
			<app:viewTextLine path="info" label="wireless.info" styleClass="odd" />
		</table>
	</c:when>
	<c:otherwise>
		<table><tr>
		<td><table class="wirelessDetail">
			<app:viewTextLine path="name" label="wireless.name" emphasis="true" styleClass="even" />
			<app:viewLabelLine path="domain" label="wireless.domain" styleClass="odd" />
			<app:viewTextLine path="subNodesCount" label="wireless.subNodesCount" styleClass="even"
				link="${ctx}/net/wireless/view.html?action=showList&parentId=${wireless.id}" />
			<app:viewLabelLine path="type" label="wireless.type" i18n="true" styleClass="odd" />
			<app:viewLabelLine path="frequency" label="wireless.frequency" styleClass="even" />
			<app:viewTextLine path="vendor" label="wireless.vendor" styleClass="odd" />
			<app:viewTextLine path="macFormatted" label="wireless.mac" styleClass="even" />
			<app:viewTextLine path="ip" label="wireless.ip" styleClass="odd" />
		</table></td><td>&nbsp;</td>
		<td><table class="wirelessDetail">
			<app:viewTextLine path="macAuthorization" label="wireless.macAuthorization" i18n="true" styleClass="even" />
			<app:viewTextLine path="ssid" label="wireless.ssid" styleClass="odd" />
			<app:viewTextLine path="shortWep" label="wireless.wep" styleClass="even" />
			<app:viewLabelLine path="polarization" label="wireless.polarization" i18n="true" styleClass="odd" />
			<app:viewTextLine path="route" label="wireless.route" styleClass="even" />
			<app:viewTextLine path="info" label="wireless.info" styleClass="odd" />
			<app:viewTextLine path="active" label="wireless.active" i18n="true" styleClass="even" />
		</table></td>
	</tr></table>
	</c:otherwise>
</c:choose>

</s:nestedPath>

<%@ include file="/WEB-INF/jsp/inc/history.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<div class="actionLinks">
<span class="actionLink"><a href="${ctx}/net/wireless/view.html?action=goBack">
	<fmt:message key="app.action.goBack" /></a></span>
<span class="actionLink"><a href="${ctx}/net/wireless/view.html?action=showList">
	<fmt:message key="wirelessList.action.viewMasterList" /></a></span>
<br />
<span class="actionLink"><a href="${ctx}/net/wireless/edit.html?action=showForm&wirelessId=${wireless.id}&_navPushUrl=1">
	<fmt:message key="${view}.action.edit" /></a></span>
</div>


<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>