<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
<input type="hidden" id="gId" name="groupId" value="${group.id}">
<input type="hidden" id="tsn" value="${terminal.tsn}">
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-12 col-md-12">
                <div class="g-panel clearfix">
                    <div class="g-panel-text ">
                       <!--  <a class="g-back-button iconfont" href="javascript:history.go(-1)"></a> -->Import Keys
                    </div>
                </div>
            </div>
            
            <!-- 内容区域这里开始 -->
            <div class="col-md-4 col-sm-12">
            	<form class="form-horizontal" id="importKey" role="form">
	                <div class="g-panel">
	                     <div class="g-panel-title clearfix">
	                    	<div class="g-panel-text">
	                    		Select Files
	                    	</div>
				         </div>
	                    <div class="g-panel-body">
	                        <div class="row">
	                            <div class="import-terminal-drag" id="drag">
	                                <span class="glyphicon glyphicon-save"></span>
	                                <p class="drag-p1">Drag and drop your files here</p>
	                                <p class="drag-p2">You can select local files to upload</p>
	                                <input type="file" id="fileupload" multiple name="mFile">
	                            </div>
	                            <div class="progress hide" style="margin-top:10px">
	                                <div class="progress-bar" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
	                                </div>
	                            </div>
                                <p class="upload-filename" style="margin-top:10px;"></p>
	                        </div>
	                    </div>
	                    
	                </div>
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
					<div class="view-button text-right">
		                <button type="submit" class="btn btn-primary view-button-style" id="Confirm">Confirm</button>
		            </div>
	            </form>
            </div>
            <!-- alert -->
            <div class="col-md-8 col-sm-12">
                <div class="g-panel">
                    <div class="g-panel-title clearfix">
                        <span class="g-panel-text">File List</span>
                    </div>
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-sm-12 col-md-12 importGroup-table">
                                <table id="table" class="table terminalList"></table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 内容区域end-->
        </div>
    </div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- END -->
<!-- /.modal -->
<!--modal delete group-->
<div class="modal fade" id="modal_key_delete" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                <p>Are you sure you want to delete?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="confirm_delete_key">Confirm</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<script type="text/javascript">
seajs.use('import-key');
</script>
</body>

</html>
