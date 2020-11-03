<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
	<div class="container-fluid">

		<div class="row">
			<!-- 内容区域这里开始 -->
			<div class="col-sm-12 col-md-12">
			<div class="g-panel" >
					<div class="g-panel-title clearfix" style="margin-bottom:0px;">
						<span class="g-panel-text">User Maintenance Report</span>
					</div>
				</div>
				
			</div>
			<div class="col-sm-12 col-md-12">
				
				<div class="g-panel" >
					
					<div class="g-panel-title text-right clearfix">
					
						<div class="block-inline vertical-top">
							<div class="input-group mb10 ml12" id="search-btn-group"
								style="width: 240px">
								<input class="form-control g-searchInput" type="text" placeholder="User,Role,Operation"> 
                                 <div class="input-group-addon g-searchGo btn">Go</div>
							</div>
						</div>

						<button type="button" id="advance-btn"
							class="btn btn-primary g-icon-btn mb10 vertical-top ml12">
							<span class="glyphicon glyphicon-filter"></span> Advanced
						</button>
						<div class="report-export pull-left hide">
						<form id="exportUser" action="<%=contextPath%>/report/userMaintenance/export/${group.id}"
							method="post">
							<input type="hidden" name="startTime"> 
							<input type="hidden" name="endTime">
							<input type="hidden" name="selectedStatus">
							<input type="hidden" name="groupId" value="${group.id}">
							<input type="hidden" name="fuzzyCondition">
							<button class="btn btn-primary" type="submit">Export</button>
						</form>
					</div>

					</div>
					<div class="advance-box hide">
						<div class="row">

							<div class="col-sm-3">
								<div class="report-datetime">
									<input type="text" class="form-control startTime g-timeselect firstFocus" id="start-time" readonly
										placeholder="Start Time"> <span
										class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
								</div>
							</div>
							<div class="col-sm-3">
							   <div class="report-datetime">
									<input type="text" class="form-control endTime g-timeselect" readonly id="end-time"
										placeholder="End Time"> <span
										class="glyphicon glyphicon-calendar datetime-icon"></span>
								</div>
							</div>
							<div class="col-sm-3">
								<div class="report-select">
									<select class="form-control" id="role-select">
										<option value="">--User Operations--</option>
										<option value="Add">Add User</option>
										<option value="Activate User">Activate User</option>
										<option value="Change">Change Password</option>
										<option value="Deactivate User">Deactivate User</option>
										<option value="Delete">Delete User</option>
										<option value="Edit">Edit User</option>
										<option value="Forgot">Forgot Password</option>
										<option value="Locked">Locked User</option>
										<option value="Login Successful">Login Successful</option>
										<option value="Login Failed">Login Failed</option>
										<option value="Logout Successful">Logout Successful</option>
										<option value="Reset">Reset Password</option>
									</select>
								</div>
							</div>
							<div class="col-sm-3 text-right">
									<button type="button" class="btn btn-primary report-btn"
										id="report-btn">Apply</button>
							</div>
						</div>
					</div>
					<div class="g-panel-body">
						<table id="table" ></table>
						
					</div>
				</div>

			</div>

			<!-- 内容区域end-->

		</div>
	</div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- add-tree -->
<div class="modal fade" id="add-tree">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header g-modal-title">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">SELECT GROUP</h4>
			</div>
			<div class="modal-body">
				<div class="g-search">

					<div class="input-group">
						<input class="form-control g-searchInput" type="text"
							placeholder="Search Here">
						<div class="input-group-addon g-searchGo btn"
							id="g-searchGo">Go</div>
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
<script type="text/javascript">
    var groupId=${group.id};
	seajs.use('report-UserMaintenance');
</script>
</body>
</html>