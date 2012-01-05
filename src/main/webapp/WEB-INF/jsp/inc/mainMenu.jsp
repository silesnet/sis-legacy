<a href="${ctx}/"><s:message code="app.menu.home"/></a><!--

--><authz:authorize ifAnyGranted="ROLE_ACCOUNTING,ROLE_CUST_VIEW">|<a href="${ctx}/customer/view.html?action=showList"><s:message code="app.menu.customers"/></a>|</authz:authorize><!--
--><authz:authorize ifAnyGranted="ROLE_ACCOUNTING"><a href="${ctx}/billing/view.html?action=mainBilling"><s:message code="app.menu.bills"/> CZ</a>|</authz:authorize><!--
--><authz:authorize ifAnyGranted="ROLE_ACCOUNTING"><a href="${ctx}/billing/view.html?action=mainBilling&country=pl"><s:message code="app.menu.bills"/> PL</a></authz:authorize>

