<%--
	- options
	- 
	- Display a list of html option tags using the given items collection.
	- Accepts dynamic attributes.
	-
	- @param items a collection of items used to generate option tags (required)
	-     If labelProperty is not specified, the value of each item will be used
	-     as both the label and the value.
	- @param label the property on each item to use as the option label
	-     Required if valueProperty is specified.
	- @param value the property on each item to use as the option value
	- @param selected the value that should be selected initially. (this
	-     attribute is mutually exclusive with "selectedValues").
	- @param selectedValues the values that should be selected initially (this
	-     attribute is mutually exclusive with "selected").
	--%>

<%@ tag isELIgnored="false" %>
<%@ attribute name="label" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="selected" type="java.lang.Object" %>
<%@ attribute name="selectedValues" type="java.lang.Object" %>

<%@ include file="includes.tagf" %>

<html:attributes var="attrString" attributeMap="${attributes}">
	<c:choose>
		<c:when test="${!empty label}">
			<c:set var="lbl" value="${label}"/>
		</c:when>
		<c:otherwise>
			<c:set var="lbl"><jsp:doBody /></c:set>
		</c:otherwise>
	</c:choose>
	
	<c:set var="val" value="${value}"/>
	<c:set var="newAttrString">
        <c:out escapeXml="false" value="value=\""/><c:out value="${val}"/><c:out escapeXml="false"
    	       value="\" ${attrString}"/>
    </c:set>
	<c:choose>
		<c:when test="${!empty selected && selected == val}">
			<c:set var="newAttrString" value="selected=\"selected\" ${newAttrString}" />
		</c:when>
		<c:when test="${!empty selectedValues}">
			<c:forEach var="sel" items="${selectedValues}">
				<c:if test="${sel == val}">
					<c:set var="newAttrString" value="selected=\"selected\" ${newAttrString}" />
				</c:if>
			</c:forEach>
		</c:when>
	</c:choose>
	<option ${newAttrString}><c:out value="${lbl}" /></option>
</html:attributes>
