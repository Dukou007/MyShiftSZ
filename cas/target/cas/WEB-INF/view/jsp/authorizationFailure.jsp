<jsp:directive.include file="/WEB-INF/view/jsp/default/ui/includes/top.jsp" />

<%@ page isErrorPage="true" %>
<%@ page import="org.jasig.cas.web.support.WebUtils"%>

	<div class="hint_title error_title">
			<p></p>
	</div>
	<div class="hint_msg">
    <h2>${pageContext.errorData.statusCode} - <spring:message code="screen.blocked.header" /></h2>

    <%
        Object casAcessDeniedKey = request.getAttribute(WebUtils.CAS_ACCESS_DENIED_REASON);
        request.setAttribute("casAcessDeniedKey", casAcessDeniedKey);

    %>

    <c:choose>
        <c:when test="${not empty casAcessDeniedKey}">
            <p><spring:message code="${casAcessDeniedKey}" /></p>
        </c:when>
    </c:choose>
    <p><%=request.getAttribute("javax.servlet.error.message")%></p>
    <p><spring:message code="AbstractAccessDecisionManager.accessDenied"/></p>
    <a type="button" class="btn btn-primary" href="javascript:history.go(-1)">Go Back</a>
</div>
<jsp:directive.include file="/WEB-INF/view/jsp/default/ui/includes/bottom.jsp" />
