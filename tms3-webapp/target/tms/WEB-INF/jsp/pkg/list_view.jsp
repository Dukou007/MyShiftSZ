<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

 <div class="g-middle-content">
  <input type="hidden" id="gid" value="${group.id}">
  <input type="hidden" id="tsn" value="${terminal.tsn}">
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
<!-- assign package -->
<div class="modal fade" id="assign-package-tree">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header g-modal-title">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">×</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">ASSIGN PACKAGE</h4>
      </div>
      <div class="modal-body">
        <div class="g-search">

          <div class="input-group">
            <input class="form-control g-searchInput" type="text" placeholder="Search Here">
            <div class="input-group-addon g-searchGo btn" id="g-searchGo">Go</div>
          </div>

        </div>
        <div class="group-tree-body">
          <div class="group-tree-content"></div>
          <div class="group-tree-search hide"></div>
        </div>
      </div>
      <!-- /.modal-content -->
      <div class="modal-footer">
        <p class="g-tree-bottom"></p>
        <div class="modal-footer-btngroup">
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary group-confirm-btn">Confirm</button>
        </div>
      </div>
    </div>
    <input type='hidden' id="permission-assign" value='<shiro:hasPermission name="tms:package:assign">1</shiro:hasPermission>'>
    <input type='hidden' id="permission-deploy" value='<shiro:hasPermission name="tms:group:deployments:add">1</shiro:hasPermission>'>
       
    <!-- /.modal-dialog --> </div>
</div>
<script type="text/javascript">
	seajs.use('package-list_view');
</script>
</body>
</html>