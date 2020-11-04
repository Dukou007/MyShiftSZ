<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

 <div class="g-middle-content">
  <input type="hidden" id="gid" value="${group.id}">
  <input type="hidden" id="itemStatus" value='<c:out value="${itemStatus}" escapeXml="true" />'>
  <input type="hidden" id="itemName" value='<c:out value="${itemName}" escapeXml="true" />'>
  <input type="hidden" id="treeDepth" value='<c:out value="${group.treeDepth}" escapeXml="true" />'>
  <div class="container-fluid">

    <div class="row">
      <!-- 内容区域这里开始 -->
      <div class="col-sm-12 col-md-12">
        <table id='table' class="table"></table>
      </div>
      <!-- 内容区域end--> </div>
  </div>

</div>
 <jsp:directive.include file="../includes/bottom.jsp" />

<script type="text/javascript">
  seajs.use('terminal-oneStatus-list');
</script>
<input type='hidden' id="per-terminal-view" value="<shiro:hasPermission name='tms:terminal:view'>1</shiro:hasPermission>">
</body>
</html>