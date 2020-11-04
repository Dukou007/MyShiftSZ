<jsp:directive.include file="includes/top.jsp" />
  <div class="hint_title error_title">
			<p></p>
	</div>
	<div class="hint_msg">
    <h2><spring:message code="screen.expiredpass.heading" /></h2>
    <p><spring:message code="screen.expiredpass.message" arguments="${passwordPolicyUrl}" /></p>
  	<a type="button" class="btn btn-primary" href="javascript:history.go(-1)">Go Back</a>
  </div>
<jsp:directive.include file="includes/bottom.jsp" />
