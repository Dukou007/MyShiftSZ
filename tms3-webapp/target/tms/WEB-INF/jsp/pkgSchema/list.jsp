<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

 <div class="g-middle-content">
 <input type="hidden" id="gid" value="${group.id}">
  <div class="container-fluid">

    <div class="row">
      <!-- 内容区域这里开始 -->
      <shiro:hasPermission name="tms:package:view">
      <div class="col-sm-12 col-md-12">
            <ul class="g-title-tab clearfix">
              <li ><a href="<%=contextPath%>/pkg/list/${group.id}">Package List</a></li>
              <li class="active"><a>Template List</a></li>
            </ul>
          </div>
       </shiro:hasPermission>
      <div class="col-sm-12 col-md-12">
          <table id="template-table"></table>
      </div>
      <!-- 内容区域end--> </div>
  </div>

</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- remove template -->
<div class="modal fade in" id="remove-template-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to delete this template?</div>
      <!-- /.modal-content -->
      <div class="modal-footer">
        <p class="g-tree-bottom"></p>
        <div class="modal-footer-btngroup">
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary J-confirm-btn">Confirm</button>
        </div>

      </div>
    </div>
    <!-- /.modal-content --> </div>
  <!-- /.modal-dialog -->
</div>
<input type="hidden" id="perssion-add" value='<shiro:hasPermission name="tms:template:add">1</shiro:hasPermission>'/>
<input type="hidden" id="perssion-delete" value='<shiro:hasPermission name="tms:template:delete">1</shiro:hasPermission>'/>
<input type="hidden" id="perssion-edit" value='<shiro:hasPermission name="tms:template:edit">1</shiro:hasPermission>'/>
<script type="text/javascript">
	seajs.use('template-list');
</script>
</body>
</html>