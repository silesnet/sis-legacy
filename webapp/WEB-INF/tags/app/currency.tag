<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>

<%@ attribute name="value" required="true" type="java.lang.Object" %>
<i18n:locale language="cs">
<c:set var="money"><i18n:formatNumber value="${value}" pattern="#,##0.00" /></c:set>
</i18n:locale>
${fn:replace(money, ",", ".")} 