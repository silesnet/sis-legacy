<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="editWireless.title"/></h1>

<%-- Parent info --%>
<c:if test="${isSlave}">
	<h2><fmt:message key="detailWireless.header.parentInfo"/></h2>
	<%@ include file="/WEB-INF/jsp/wireless/parentInfo.jsp" %>
</c:if>

<form name="nodeWirelessEditForm" method="post">
	<table class="editForm">

	<tr><s:bind path="node.typeLabel">
		<td><fmt:message key="NodeWireless.fTypeLabel"/></td>
		<td>
			<select name="${status.expression}">
				<c:forEach var="typeLabel" items="${typeLabels}">
					<option value="${typeLabel.id}"
						<c:if test="${typeLabel.id == node.typeLabel.id}">
							selected
						</c:if>
					>
						${typeLabel.name}
					</option>
				</c:forEach>
			</select>
		</td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr class="even"><s:bind path="node.name">
		<td><fmt:message key="NodeWireless.fName"/></td>
		<td><input type="text" name="${status.expression}" value="${status.value}"/></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr><s:bind path="node.domainLabel">
		<td><fmt:message key="NodeWireless.fDomainLabel"/></td>
		<td>
			<select name="${status.expression}">
				<c:forEach var="domainLabel" items="${domainLabels}">
					<option value="${domainLabel.id}"
						<c:if test="${domainLabel.id == node.domainLabel.id}">
							selected
						</c:if>
					>
						${domainLabel.name}
					</option>
				</c:forEach>
			</select>
		</td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr class="even">
		<td><fmt:message key="NodeWireless.fSubNodesCount"/></td>
		<td>
			<c:if test="${isMaster}">
			<a href="${ctx}/net/wireless/view.html?view=viewSlaveList&parentId=${node.id}">
				${node.subNodesCount}</a>
			</c:if>				
			<c:if test="${isSlave}">
				${node.subNodesCount}
			</c:if>	
		</td>
		<td class="info">&nbsp;</td>
	</tr>
	
	<tr><s:bind path="node.MAC">
		<td><fmt:message key="NodeWireless.fMAC"/></td>
		<td><input type="text" name="${status.expression}" value="${status.value}"/></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr class="even"><s:bind path="node.macAuthorization">
		<td><fmt:message key="NodeWireless.fMacAuthorization"/></td>
		<td>
		<input type="hidden" name="_${status.expression}"/>
		<input type="checkbox" name="${status.expression}" value="true"
			<c:if test="${status.value}">checked</c:if> /></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr>
		<td class="editForm"><fmt:message key="NodeWireless.fVendorMac"/></td>
		<td>
			${node.vendorMAC}
		</td>
		<td class="info"></td>
	</tr>

	<tr class="even"><s:bind path="node.vendorOptional">
		<td><fmt:message key="NodeWireless.fVendorOptional"/></td>
		<td><input type="text" name="${status.expression}" value="${status.value}"/></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr><s:bind path="node.channel">
		<td><fmt:message key="NodeWireless.fChannel"/></td>
		<td><input type="text" name="${status.expression}" value="${status.value}"/></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr class="even"><s:bind path="node.route">
		<td><fmt:message key="NodeWireless.fRoute"/></td>
		<td><input type="text" name="${status.expression}" value="${status.value}"/></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr><s:bind path="node.IP">
		<td><fmt:message key="NodeWireless.fIP"/></td>
		<td><input type="text" name="${status.expression}" value="${status.value}"/></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr class="even" valign="top"><s:bind path="node.WEP">
		<td><fmt:message key="NodeWireless.fWEP"/></td>
		<td><textarea name="${status.expression}" cols="27" rows="4">${status.value}</textarea></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>
	
	<tr><s:bind path="node.SSID">
		<td><fmt:message key="NodeWireless.fSSID"/></td>
		<td><input type="text" name="${status.expression}" value="${status.value}"/></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>
	
	<tr class="even"><s:bind path="node.polarizationLabel">
		<td><fmt:message key="NodeWireless.fPolarizationLabel"/></td>
		<td>
			<select name="${status.expression}">
				<c:forEach var="polarizationLabel" items="${polarizationLabels}">
					<option value="${polarizationLabel.id}"
						<c:if test="${polarizationLabel.id == node.polarizationLabel.id}">
							selected
						</c:if>
					>
						${polarizationLabel.name}
					</option>
				</c:forEach>
			</select>
		</td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>

	<tr valign="top"><s:bind path="node.info">
		<td><fmt:message key="NodeWireless.fInfo"/></td>
		<td><textarea name="${status.expression}" rows="3">${status.value}</textarea></td>
		<td class="error">${status.errorMessage}</td>
	</s:bind></tr>


	<tr><td>&nbsp;</td></tr>

	</table>
	<c:choose>
		<c:when test="${empty node.id}">
			<input type="submit" name="buttonInsert" value="<fmt:message key="editWireless.button.insert"/>"/>
		</c:when>
		<c:when test="${not empty node.id}">
			<input type="submit" name="buttonSave" value="<fmt:message key="editWireless.button.save"/>"/>
			<input type="submit" name="buttonDelete" value="<fmt:message key="editWireless.button.delete"/>"
				onclick="return confirm('<fmt:message key="editWireless.confirm.delete"/>')"/>
		</c:when>
	</c:choose>
	<input type="submit" name="buttonCancel" value="<fmt:message key="editWireless.button.cancel"/>"/>

	<%-- Some JS via uitags --%>
	<%-- Disable some fields when SA label is selected --%>
	<ui:formGuide form="nodeWirelessEditForm">
		<ui:observe widget="typeLabel" forValue="12" />
		<ui:disable widget="domainLabel" />
		<ui:disable widget="macAuthorization" />
		<ui:disable widget="channel" />
		<ui:disable widget="route" />
		<ui:disable widget="WEP" />
		<ui:disable widget="SSID" />
		<ui:disable widget="polarizationLabel" />
	</ui:formGuide>

</form>

<%@ include file="/WEB-INF/jsp/inc/history.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>