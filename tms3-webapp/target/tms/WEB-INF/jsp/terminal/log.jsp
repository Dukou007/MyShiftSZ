<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
	<div class="container-fluid">
		<div class="row">
			<!-- 内容区域这里开始 -->
			<div class="col-sm-12 col-md-12">
				<div class="g-panel">
					<div class="g-panel-title text-right clearfix" style="margin-bottom: 0px;">
						<div class="g-panel-text mb10 text-left" style="float: none;">Terminal Logs</div>
						<div class="text-left">
							<div class="block-inline vertical-top pull-right">
								<div class="input-group mb10 ml12" id="search-btn-group"
									style="width: 240px">
									<input class="form-control g-searchInput" type="text"
										placeholder="Terminal SN,Message">
									<div class="input-group-addon g-searchGo btn">Go</div>
								</div>
							</div>
							<div class="event-export hide" style="margin-bottom:15px;">
								<button id="exportExcel" class="btn btn-primary" type="button">Export</button>
								<button class="btn btn-primary J-select-clean">Clean</button>
							</div>
							
						</div>
					</div>
					<div class="g-panel-body">
						<table id="log-table"></table>
					</div>
					<shiro:hasPermission name="tms:terminal log:level">
						<div class="log-level-set hide">
							<div class="edit-name" style="margin-right:14px;">
								<select name="logLevel" id="logLevel" class="form-control">
									<c:forEach items="${levelMap}" var="item">
										<option value="${item.value}" ${logLevel eq item.value ? "selected":""}>
											<c:out value="${item.value}" />
										</option>
									</c:forEach>
								</select>
							</div>
							<button id="setLogLevel" class="btn btn-primary" type="button">Set</button>
							<span style="margin-left:14px;">* Set server accepted logging level</span>
						</div>
					</shiro:hasPermission>
				</div>
			</div>
			<!-- 内容区域end-->
		</div>
	</div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />

<!-- /.modal -->
<!-- clean terminal log-->
<div class="modal fade" id="clean-terminal-log">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center">Do you clear all terminal logs saved in database?</div>
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

<script type="text/javascript">
	seajs.use('terminal-log');
	var GROUP_ID=${group.id};
</script>
</body>
</html>
