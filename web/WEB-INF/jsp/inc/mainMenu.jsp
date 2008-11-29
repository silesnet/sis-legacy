<a href="https://tech.silesnet.cz:2828/"><s:message code="app.menu.home"/></a><!--

--><authz:authorize ifAllGranted="ROLE_USER">|<!--
--><authz:authorize ifAnyGranted="ROLE_ACCOUNTING,ROLE_CUST_VIEW"><a href="${ctx}/customer/view.html?action=showList"><s:message code="app.menu.customers"/></a>|</authz:authorize><!--
--><authz:authorize ifAnyGranted="ROLE_ACCOUNTING"><a href="${ctx}/billing/view.html?action=mainBilling"><s:message code="app.menu.bills"/> CZ</a>|</authz:authorize><!--
--><authz:authorize ifAnyGranted="ROLE_ACCOUNTING"><a href="${ctx}/billing/view.html?action=mainBilling&country=pl"><s:message code="app.menu.bills"/> PL</a>|</authz:authorize><!--
--><a href="${ctx}/net/wireless/view.html?action=showList"><s:message code="app.menu.wireless"/></a>|<!--
--><a href="${ctx}/app/view.html?view=redirectMantis"><s:message code="app.menu.bugtracker"/></a><!--
--></authz:authorize><!--

--><authz:authorize ifAllGranted="ROLE_DEALER"><!--
--><a href="${ctx}/net/wireless/view.html?view=viewMasterList"><s:message code="app.menu.wireless"/></a>|<!--
--><a href="${ctx}/app/view.html?view=redirectMantis"><s:message code="app.menu.bugtracker"/></a><!--
--></authz:authorize><!--

--><authz:authorize ifAllGranted="ROLE_DEALER2">|<!--
--><a href="${ctx}/net/wireless/view.html?view=viewMasterList"><s:message code="app.menu.wireless"/></a>|<!--
--></authz:authorize>
