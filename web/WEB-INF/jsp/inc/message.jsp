<c:if test="${not empty successMsg}">
	<div class="successMsg">
		${successMsg}
	</div>
</c:if>
<c:if test="${not empty failureMsg}">
	<div class="failureMsg">
		${failureMsg}
	</div>
</c:if>