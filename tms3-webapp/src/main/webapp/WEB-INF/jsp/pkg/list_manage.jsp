<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

 <div class="g-middle-content">
  <input type="hidden" id="gid" value="${group.id}">
  <input type="hidden" id="treeDepth" value="${group.treeDepth}">
  <div class="container-fluid">

    <div class="row">
      <!-- 内容区域这里开始 -->
  
      <div class="col-sm-12 col-md-12">
      	<div class="g-panel">
      		<div class="g-panel-body">
	        	<table id='table' class="table"></table>
        	</div>
      	</div>
      </div>
      <!-- 内容区域end--> 
    </div>
  </div>
</div>
<input type='hidden' id="perssion-add" value='<shiro:hasPermission name="tms:package:add">1</shiro:hasPermission>'>
<input type='hidden' id="perssion-deactive" value='<shiro:hasPermission name="tms:package:deactive">1</shiro:hasPermission>'>
<input type='hidden' id="perssion-active" value='<shiro:hasPermission name="tms:package:active">1</shiro:hasPermission>'>
<input type='hidden' id="perssion-assign" value='<shiro:hasPermission name="tms:package:assign">1</shiro:hasPermission>'>
<input type='hidden' id="perssion-remove" value='<shiro:hasPermission name="tms:package:remove">1</shiro:hasPermission>'>
<input type='hidden' id="perssion-delete" value='<shiro:hasPermission name="tms:package:delete">1</shiro:hasPermission>'> 

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
        
    <!-- /.modal-dialog --> 
 </div>
</div>
<!-- remove package -->
<div class="modal fade" id="remove-package-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to remove this package?</div>
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
<!-- /.modal -->
<!-- activate terminal -->
<div class="modal fade" id="activate-package-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to activate this package?</div>
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
<!-- /.modal -->
<!-- deactivate terminal -->
<div class="modal fade" id="deactivate-package-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to deactivate this package?</div>
      <!-- /.modal-content -->
      <div class="modal-footer">
        <p class="g-tree-bottom"></p>
        <div class="modal-footer-btngroup">
          <button type="button" class="btn btn-default " data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary J-confirm-btn">Confirm</button>
        </div>

      </div>
    </div>
    <!-- /.modal-content --> </div>
  <!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<!-- delete terminal -->
<div class="modal fade" id="delete-package-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to delete this package?</div>
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
<!-- /.modal -->
<script type="text/javascript">
	seajs.use('package-list_manage');
</script>
</body>
</html>