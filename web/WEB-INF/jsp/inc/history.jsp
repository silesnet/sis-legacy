
<c:if test="${not empty historyRecord}">
	<h3><fmt:message key="app.history.header"/>&nbsp;
		<span style="font-size: 80%"
		onclick="toggleHistory()" 
		onmouseover="this.style.backgroundColor='#ffffaa'"
		onmouseout="this.style.backgroundColor='#fefefe'">
			[<fmt:message key="app.label.showHide"/>]</span></h3>

	<div id="historyTable">
	<display:table name="${historyRecord}" id="row" class="auditList">
	    <display:column titleKey="HistoryItem.fTimeStamp">
	    	<fmt:formatDate value="${row.timeStamp}" pattern="dd.MM.yyyy" /><br/>
	    	<small><fmt:formatDate value="${row.timeStamp}" pattern="HH:mm:ss" /></small>
		</display:column>
	    <display:column titleKey="HistoryItem.fUser" property="user.loginName"/>
	    <display:column titleKey="HistoryItem.fFieldName">
			<fmt:message key="${row.fieldName}" />
	    </display:column>
	    <display:column titleKey="HistoryItem.fOldValue" property="oldValue"/>
	    <display:column titleKey="HistoryItem.fNewValue" property="newValue"/>
	</display:table>
	</div>
	<c:out value='<script type="text/javascript">' escapeXml='false' />
		<c:import url="/js/toggleHistory.js" />
	<c:out value='</script>' escapeXml='false' />

</c:if>
