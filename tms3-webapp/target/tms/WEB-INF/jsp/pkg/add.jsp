<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
    <div class="container-fluid">
        <div class="row">
          
            <!-- 内容区域这里开始 -->
            <div class="col-sm-12 col-md-12">
                <div class="g-panel ">
                    <!--edit-->
                    <shiro:hasPermission name="tms:package:add">
                    <div class="g-panel-title clearfix">
                		<div class="g-panel-text">
                    	 	<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Add Package
                    	</div>
                	</div>
                    </shiro:hasPermission>
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-md-5 col-sm-12">
                                <form class="form-horizontal" id="addPkg" role="form">
                                <input id="gid" type="hidden" value="${group.id}">
                                <input id='packageType' type="hidden" value="Multilane">
                                    <div class="col-md-12 col-sm-12">
                                        <div class="row">
                                            <div class="group_chooseG">
                                                <div class="group_note">
                                                    <span class="font14px black normal pull-left">Group: &nbsp;&nbsp;</span>
                                                </div>
                                                <div class="group_chooseG_bottom" title="Select group"> <i class="iconfont left-icon">&#xe602;</i>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-12 col-sm-12">
                                        <div class="g-panel">
                                            <div class="g-panel-title clearfix">
                                                <div class="edit-name" style="text-align:left;margin-left: -15px;">Select File</div>
                                            </div>
                                            <div class="g-panel-body">
                                                <div class="row">
                                                    <div class="import-terminal-drag" id="drag">
                                                        <span class="glyphicon glyphicon-save"></span>
                                                        <p class="drag-p1">Drag and drop your file here</p>
                                                        <p class="drag-p2">You can select a local file to upload</p>
                                                        <input type="file" id="fileupload" multiple name="mFile"></div>
                                                    <div class="progress hide" style="margin-top:10px">
                                                        <div class="progress-bar" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
                                                            
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-12 col-md-12">
                                    	<p class="upload-filename" style="margin-top:10px;"></p>
                                    </div>
                                    <div class="col-sm-12 col-md-12">
										<div class="g-panel">
											<div class="g-panel-title clearfix">
                                                <div class="edit-name" style="text-align:left;margin-left: -15px;">Notes</div>
                                            </div>
                                            <div class="g-panel-body">
                                            <div class="row">
	                                            <textarea class="form-control" rows="4" id="notesArea" placeholder=" " name="notes" maxlength="200"></textarea>
	                                        </div>
	                                        </div>
	                                    </div>
                                    </div>
                                    <div class="pull-right view-button text-right">
                                        <button type="submit" class="btn btn-primary view-button-style" id="Confirm">Confirm</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 内容区域end--> </div>
    </div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- add-package-tree -->
<div class="modal fade" id="add-package-tree">
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
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary group-confirm-btn">Confirm</button>
        </div>

      </div>
    </div>
    <!-- /.modal-dialog --> </div>
</div>
<!-- END -->
<script type="text/javascript">
	seajs.use('add-package');
</script>
</body>
</html>