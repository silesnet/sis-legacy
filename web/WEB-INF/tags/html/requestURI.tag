<%@ tag isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<c:choose>
<c:when test="${!empty requestScope['javax.servlet.forward.request_uri']}"><c:out value="${requestScope['javax.servlet.forward.request_uri']}"/></c:when>
<c:otherwise><c:out value="${pageContext.request.requestURI}"/></c:otherwise>
</c:choose>
