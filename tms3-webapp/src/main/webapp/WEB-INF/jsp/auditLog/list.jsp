<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
	<div class="container-fluid">

		<div class="row">
			<!-- 内容区域这里开始 -->
			<div class="col-sm-12 col-md-12">
				<div class="g-panel">
					<div class="g-panel-title text-right clearfix" style="margin-bottom:0px">
						<span class="g-panel-text text-left ">Audit Logs</span>
						<div class="block-inline vertical-top">
							<div class="input-group mb10 ml12" id="search-btn-group" style="width:240px">
								<input class="form-control g-searchInput" type="text" placeholder="User,Role,Operations"> 
                                 <div class="input-group-addon g-searchGo btn">Go</div>
							</div>
						</div>

						<button type="button" id="advance-btn" class="btn btn-primary g-icon-btn mb10 vertical-top ml12">
							<span class="glyphicon glyphicon-filter"></span>
							Advanced
						</button>

					</div>
					<div class="advance-box hide">
						<div class="row">

							<div class="col-sm-3">
								<div class="report-datetime">
									<input type="text" class="form-control g-timeselect" id="start-time" placeholder="Start time " readonly>
									<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
								</div>
							</div>
							<div class="col-sm-3">
								<div class="report-datetime">
									<input type="text" class="form-control g-timeselect" id="end-time" placeholder="End time" readonly>
									<span class="glyphicon glyphicon-calendar datetime-icon"></span>
								</div>
							</div>
							<div class="col-sm-3">
							<div class="report-select">
									<select class="form-control" id="role-select">
										<option value="">All</option>
										<option value="Site Administrator">Site Administrator</option>
										<option value="Administrator" >Administrator</option>
										<option value="Supervisor" >Supervisor</option>
										<option value="Operator" >Operator</option>
										<option value="Installer" >Installer</option>
									</select>
								</div>
							</div>
							<div class="col-sm-3 text-right">
								
									<button type="button" class="btn btn-primary report-btn" id="report-btn">Apply</button>

							</div>
						</div>
					</div>
					<div class="g-panel-body">
						
						<table id="table"></table>

					</div>
				</div>
			</div>
			<!-- 内容区域end-->
		</div>
	</div>

</div>
<jsp:directive.include file="../includes/bottom.jsp"/>
<!-- add-tree -->
<div class="modal fade" id="add-tree">
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
<script type="text/javascript">
seajs.use('audit-log');
var GROUP_ID=${group.id};
</script>
</body>
</html>