<jsp:directive.include file="includes/top.jsp" />
  <div class="hint_title warning_title">
			<p></p>
	</div>
	<div class="hint_msg">
    <p><spring:message code="screen.confirmation.message" arguments="${fn:escapeXml(param.service)}${fn:indexOf(param.service, '?') eq -1 ? '?' : '&'}ticket=${serviceTicketId}" /></p>
  	<a type="button" class="btn btn-primary" href="javascript:history.go(-1)">Go Back</a>
  </div>
<jsp:directive.include file="includes/bottom.jsp" />