<%@ include file="/WEB-INF/jsp/inc/formHeader.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="wirelessForm.title"/></h1>

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

<c:if test="${isNew && isMaster == 'false'}">
	<c:set var="isSA" value="true" />
</c:if>

<form name="wirelessForm" method="POST" action="${ctx}/net/wireless/edit.html">
<s:nestedPath path="wireless">

<%-- IMPORTANT sent object id with post 2 proper retrieve backing object. --%>
<c:if test="${!isNew}">
	<input type="hidden" name="wirelessId" value="${wireless.id}" />
</c:if>
<%-- IMPORTANT preserve parentId in the case of new command --%>
<c:if test="${isNew}">
	<input type="hidden" name="parentId" value="${wireless.parentId}" />
</c:if>

<c:set var="yes"><fmt:message key="app.label.yes"/></c:set>
<c:set var="no"><fmt:message key="app.label.no"/></c:set>

<table style="margin-top: 5px;"><tr>
<td><table class="editForm">
	<app:formInputLine path="name" label="wireless.name" required="true" size="25" />
<c:if test="${isSA == 'false' }">
	<app:formLabelLine path="domain" label="wireless.domain" labels="${domains}" naOption="true" />
</c:if>
	<app:formEnumLine path="type" label="wireless.type" enums="${wirelessType}" required="true" />
<c:if test="${isSA == 'false' }">
	<app:formEnumLine path="frequency" label="wireless.frequency" enums="${wirelessFrequency}" naOption="true" required="true" i18n="false" />
</c:if>
	<app:formInputLine path="mac" label="wireless.mac" size="25" />
	<app:formInputLine path="ip" label="wireless.ip" size="25" />
<c:if test="${!isNew}">
	<app:formDisabledLine path="macVendor" label="wireless.macVendor" />
</c:if>
	<app:formInputLine path="customVendor" label="wireless.customVendor" size="25" />
<c:if test="${isSA == 'false' }">
	<app:formInputLine path="ssid" label="wireless.ssid" size="25" />
	<app:formCheckboxLine path="macAuthorization" label="wireless.macAuthorization" />
</c:if>
</table></td><td>&nbsp;</td>

<td><table class="editForm">
<c:if test="${isSA == 'false' }">
	<app:formTextareaLine path="wep" label="wireless.wep" cols="25" rows="3" />
	<app:formEnumLine path="polarization" label="wireless.polarization" enums="${wirelessPolarization}" required="true" />
	<app:formInputLine path="route" label="wireless.route" size="33" />
</c:if>
	<app:formTextareaLine path="info" label="wireless.info" cols="25" rows="3" />
	<app:formLine path="active" label="wireless.active" required="true">
		${wireless.active ? yes : no }
	</app:formLine>
</table></td>
</tr></table>

<%@ include file="/WEB-INF/jsp/inc/history.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>

<%-- Form submit buttons --%>
<app:formActionButtons confirmDeleteMsg="wirelessForm.confirmDelete" />
	
</s:nestedPath>
</form>

<div class="actionLinks">
<%-- update master parent select for not new slaves --%>
<c:if test="${isNew == 'false' && isMaster == 'false'}">
	<form name="changeParent" method="GET" action="${ctx}/net/wireless/edit.html">
		<span class="actionLink">
			<a href="javascript:document.changeParent.submit()" class="action"
				onclick="return confirm('<fmt:message key="editWireless.confirm.parentChange"/>')">
			<fmt:message key="editWireless.action.parentChange" /></a></span>
		<input type="hidden" name="action" value="updateParent" />
		<input type="hidden" name="wirelessId" value="${wireless.id}" />
		<select name="newParentId">
			<html:options items="${mastersList}" label="name" value="id" selected="${wireless.parentId}" />
		</select>
	</form>
</c:if>
</div>

<%-- Preset wireless.type options to SA for non master new wireless --%>
<c:if test="${isNew && isMaster == 'false'}">
	<script type="text/javascript">
		document.forms["wirelessForm"].elements["type"].selectedIndex=2;
	</script>
</c:if>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>