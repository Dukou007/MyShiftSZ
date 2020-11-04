<jsp:directive.include file="includes/top.jsp" />
  <c:url var="url" value="/login">
    <c:param name="service" value="${param.service}" />
    <c:param name="renew" value="true" />
  </c:url>
  
  <div class="hint_title error_title">
			<p></p>
	</div>
	<div class="hint_msg">
    <h2><spring:message code="screen.service.sso.error.header" /></h2>
    <p><spring:message code="screen.service.sso.error.message"  arguments="${fn:escapeXml(url)}" /></p>
  	<a type="button" class="btn btn-primary" href="javascript:history.go(-1)">Go Back</a>
  </div>
<jsp:directive.include file="includes/bottom.jsp" />
