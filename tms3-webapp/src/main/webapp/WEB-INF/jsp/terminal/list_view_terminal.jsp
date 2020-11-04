<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

 <div class="g-middle-content">
  <input type="hidden" id="gid" value="${group.id}">
  <input type="hidden" id="treeDepth" value="${group.treeDepth}">
  <input type="hidden" id="tsn" value="${terminal.tsn}">
  <div class="container-fluid">

    <div class="row">
      <!-- 内容区域这里开始 -->
      <div class="col-sm-12 col-md-12">
			<div class="g-panel" >
				<div class="g-panel-title clearfix" style="margin-bottom:0px;">
					<span class="g-panel-text">Terminals List</span>
				</div>
			</div>
	  </div>
      <div class="col-sm-12 col-md-12">
        <table id='table' class="table"></table>
      </div>
      <!-- 内容区域end--> </div>
  </div>

</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- deactivate terminal -->
<div class="modal fade" id="deactivate-terminal-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to deactivate this terminal?</div>
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
<!-- activate terminal -->
<div class="modal fade" id="activate-terminal-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to activate this terminal?</div>
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
<!-- clone terminal -->
<div class="modal fade" id="clone-terminal-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">CLONE TERMINAL</h4>
      </div>
      <form class="form-normal">
        <div class="modal-body">
          <div class="row">
            <div class="col-sm-12">

              <input type="hidden" id="group-id"   name="targetGroupId">
              <input type="hidden" id="source-tsn"   name="tsn">
              <input type="hidden" name="groupId" value="${group.id}">
              <div class="form-edit-group">
                <div class="edit-name">Group Name</div>
                <div class="edit-value">
                  <div class="J-show-cloneTree icon-from-group">
                      <input type="text" class="form-control required g-timeselect" placeholder="" name="groupname" readonly> <i class="iconfont">&#xe607;</i>
                  </div>
                </div>
              </div>
              <div class="form-edit-group">
                <div class="edit-name">Terminal SN</div>
                <div class="edit-value">
                  <input type="text" class="form-control numbersletters required lengthtype" placeholder="" name="tsnRanges"></div>
              </div>
            </div>
          </div>
        </div>
        <!-- /.modal-content -->
        <div class="modal-footer">
          <p class="g-tree-bottom"></p>
          <div class="modal-footer-btngroup">
            <button type="button" class="btn btn-default"  data-dismiss="modal">Cancel</button>
            <button type="submit" class="btn btn-primary">Confirm</button>
          </div>

        </div>
      </form>
    </div>
    <!-- /.modal-content --> </div>
  <!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<!-- clone-tree -->
<div class="modal fade" id="clone-terminal-tree">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header g-modal-title">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">×</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">SELECT GROUP</h4>
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
          <button type="button" class="btn btn-default group-cancel-btn" data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary group-confirm-btn">Confirm</button>
        </div>

      </div>
    </div>
    <!-- /.modal-dialog --> </div>
</div>
<!-- /.modal -->
<!-- clone terminal re-confirm -->
<div class="modal fade" id="reConfirm-clone-terminal-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center"></div>
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
<!-- copy -->
<div class="modal fade" id="copy-terminal-modal">
  <div class="modal-dialog modal-lg">
      <div class="modal-content">
          <div class="modal-header g-modal-title">
            <button type="button" class="close" data-dismiss="modal">
              <span aria-hidden="true">×</span>
              <span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">COPY TERMINAL</h4>
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
<!-- end assign -->
<!-- move-tree -->
<div class="modal fade" id="move-terminal-tree">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header g-modal-title">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">×</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">MOVE TERMINAL</h4>
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
          <button type="button" class="btn btn-default group-cancel-btn" data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary group-confirm-btn">Confirm</button>
        </div>

      </div>
    </div>
    <!-- /.modal-dialog --> </div>
</div>
<!-- /.modal -->
<!-- remove terminal -->
<div class="modal fade" id="remove-terminal-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to remove this terminal?</div>
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
<!-- delete terminal modal-->
<div class="modal fade" id="delete-terminal-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Are you sure to delete this terminal?</div>
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
  seajs.use('terminal-list');
</script>
<input type='hidden' id="permission-copy" value="<shiro:hasPermission name='tms:terminal:copy'>1</shiro:hasPermission>">
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:terminal:delete'>1</shiro:hasPermission>">
<input type='hidden' id="permission-remove" value="<shiro:hasPermission name='tms:terminal:remove'>1</shiro:hasPermission>">
</body>
</html>