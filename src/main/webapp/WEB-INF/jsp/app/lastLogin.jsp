<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<h1><fmt:message key="lastLogin.title"/></h1>

<display:table name="${historyRecord}" id="row" class="auditList" pagesize="100" requestURI="${ctx}/app/view.html?view=viewLastLogin" export="true">
    <display:column titleKey="HistoryItem.fTimeStamp" sortable="true" headerClass="sortable" sortProperty="timeStamp.time"><fmt:formatDate value="${row.timeStamp}" pattern="dd.MM.yyyy HH:mm:ss"/></display:column>
    <display:column titleKey="HistoryItem.fUser" property="user.loginName" sortable="true" headerClass="sortable"/>
    <display:column titleKey="lastLogin.fIP" property="fieldName" sortable="true" headerClass="sortable"/>
<%-- 
    <display:column titleKey="lastLogin.fSessionId" property="oldValue"/>
--%>
    <display:column titleKey="lastLogin.fLogoutTime" property="logoutTime" sortable="true" headerClass="sortable" sortProperty="logoutOrderValue" />
</display:table>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>