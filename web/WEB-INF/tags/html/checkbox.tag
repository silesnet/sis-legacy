<%--
	- checkbox 
	- multi-select checkbox when type="multibox"
	- radio button when type="radio"
	- 
	- Display a single checkbox with the given value. Bind the value to the
	- command or bean specified in the 'path' attribute.
	- Accepts dynamic attributes.
	-
	- @param type use this attribute to override the input type (radio, multibox).
	- @param path the name of the field to bind to (required)
	- @param value the value of this checkbox (required)
	--%>
<%@ include file="includes.tagf" %>
<%@ tag body-content="empty" %>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" %>
<%@ attribute name="type" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="elementPath" %>
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<spring:bind path="${path}">
	<c:set var="inputType" value="${type}"/>
	<c:if test="${inputType != \"radio\"}"><c:set var="inputType" value="checkbox"/></c:if>
	<html:attributes var="attrString" attributeMap="${attributes}" type="${inputType}" name="${status.expression}" value="${value}">
		<c:choose>
			<c:when test="${type == \"multibox\"}">
				<c:forEach var="val" items="${status.value}">
					<c:choose>
						<c:when test="${!empty elementPath}">
							<c:if test="${val[elementPath] == value}">
								<c:set var="isChecked" value="checked=\"checked\" "/>
							</c:if>
						</c:when>
						<c:otherwise>
							<c:if test="${val == value}">
								<c:set var="isChecked" value="checked=\"checked\" "/>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:if test="${status.value == value}">
					<c:set var="isChecked" value="checked=\"checked\" "/>
				</c:if>
			</c:otherwise>
		</c:choose>
		<c:set var="attrString" value="${isChecked}${attrString}" />
		<input ${attrString} /></html:attributes></spring:bind>