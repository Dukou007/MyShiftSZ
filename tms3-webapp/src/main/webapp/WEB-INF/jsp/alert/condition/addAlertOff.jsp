<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../../includes/top.jsp" />
<style type="text/css">
@media screen  and (max-width: 555px) {
.laydate_body .laydate_box {
	  width: auto;
}
.laydate_btn {
    position: relative !important;
 	margin-top: 20px !important;
	right: 0 !important;
	top: 0 !important;
}
</style>
<div class="g-middle-content">
	<div class="container-fluid">

		<div class="row">
			<!-- 内容区域这里开始 -->
			<div class="col-sm-12 col-md-12">
				<div id="view" class="g-panel" style="display: block;">
					<div class="g-panel-title clearfix">
						<div class="g-panel-text">
						<!-- 	<a class="g-back-button iconfont" href="javascript:history.go(-1)"></a> -->Alert Off
						</div>
						<div class="g-panel-button button-active pull-right hidden addAlertBtn" data-toggle="modal">
							<shiro:hasPermission name="tms:alert:alert off:add">
								<span class="glyphicon glyphicon-plus "></span>
								<span>Add</span>
							</shiro:hasPermission>
						</div>
					</div>
					<div class="g-panel-body">
						<div class="row">
							<div class="col-md-12">
								<table id="table" class="table"></table>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- 内容区域end-->
		</div>
	</div>

</div>
<jsp:directive.include file="../../includes/bottom.jsp" />
<!-- remove alert -->
<div class="modal fade" id="remove-modal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">WARNING</h4>
			</div>
			<div class="modal-body text-center">Are you sure to remove this alert?</div>
			<!-- /.modal-content -->
			<div class="modal-footer">
				<p class="g-tree-bottom"></p>
				<div class="modal-footer-btngroup">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-primary J-confirm-btn">Confirm</button>
				</div>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- 模态框（Modal） -->
<div class="modal fade" id="select-time">
	<div class="modal-dialog ">
		<form class="modal-content" id="form">
			<div class="modal-header g-modal-title">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">NEW ALERT OFF PLAN</h4>
			</div>
			<div class="modal-body">
				<ul class="g-title-model  clearfix alertoff-model" id="alert-off">
					<!--first-->
					<li class="active " data-type="yearly">Yearly</li>
					<li data-type="monthly">Monthly</li>
					<li data-type="weekly">Weekly</li>
					<li data-type="daily">Daily</li>
					<li data-type="onetime">One Time</li>
				</ul>
				<div class="alert-time">
					<div class="row">
						<div class="clearfix model-input" style="margin-left:15px;">
							<div class="edit-name model-name">Time Zone</div>
							<div class="edit-value model-value form-edit-group">
								  <c:out value="${timeZone}" escapeXml="true" />
							</div>
						</div>
					</div>
					<div class="g-panel clearfix " id="yearly" style="margin-top:-10px;">
						<!--yearly-->
						<input name="repeatType" value="4" hidden>
						<div class="row">
							<div class="col-md-6">
								<div class="form-horizontal">
									<div class="clearfix model-input">
										<div class="edit-name model-name">Select Date</div>
										<div class="edit-value model-value form-edit-group  date report-datetime">
											<input name="offDate" type="text" class="form-control form_datetime required g-timeselect " readonly placeholder="">
											<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
										</div>
									</div>
									<div class="form-edit-group overflow-hidden">
										<div class="edit-name model-name">Select Time</div>
										<div class="edit-value model-value">
											<span class="model-text1">from</span>
											<div class="form-edit-group model-input1">
												<input name="offStartTime" type="text" class="form-control  required   from-time g-timeselect" readonly placeholder=" ">
											</div>
											<div class="form-edit-group model-input2">
												<input name="offEndTime" type="text" class="form-control  required  to-time g-timeselect" readonly placeholder=" ">
											</div>
											<span class="model-text2">to</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class=" g-panel clearfix " id="monthly" style="display: none;margin-top:-10px">
						<!--monthly-->
						<input name="repeatType" value="3" hidden>
						<div class="row">
							<div class="col-md-6">
								<div class="form-horizontal">
									<div class=" clearfix model-input">
										<div class="edit-name model-name">Select Date</div>
										<div class="edit-value model-value form-edit-group report-datetime">
											<input name="offDate" type="text" class=" form-control form_monthtime required g-timeselect" readonly placeholder=""> 
											<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
										</div>
									</div>
									<div class="form-edit-group overflow-hidden">
										<div class="edit-name model-name">Select Time</div>
										<div class="edit-value model-value">
											<span class="model-text1">from</span>
											<div class="form-edit-group model-input1">
												<input name="offStartTime" type="text" class="form-control  required  from-time g-timeselect" readonly placeholder=" ">
											</div>
											<div class="form-edit-group model-input2">
												<input name="offEndTime" type="text" class="form-control  required  to-time g-timeselect" readonly placeholder=" ">
											</div>
											<span class="model-text2">to</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class=" g-panel clearfix " id="weekly" style="display: none;margin-top:-10px">
						<!--weekly-->
						<input name="repeatType" value="2" hidden>
						<div class="row">
							<div class="">
								<div class="col-md-12 clearfix ">
									<div class="form-edit-group weekly-select">
										<div class="edit-name model-name">Select Date</div>
										<div class="edit-value model-value">
											<ul class="clearfix weekly-ul">
												<li class="weekly-li " data-value="1">
													<div class="weekly-point" style="display: none;"></div> Sunday
												</li>
												<li class="weekly-li weekly-active" data-value="2">
													<div class="weekly-point"></div> Monday
												</li>
												<li class="weekly-li " data-value="3">
													<div class="weekly-point" style="display: none;"></div> Tuesday
												</li>
												<li class="weekly-li " data-value="4">
													<div class="weekly-point" style="display: none;"></div> Wednesday
												</li>
												<li class="weekly-li " data-value="5">
													<div class="weekly-point" style="display: none;"></div> Thursday
												</li>
												<li class="weekly-li " data-value="6">
													<div class="weekly-point" style="display: none;"></div> Friday
												</li>
												<li class="weekly-li " data-value="7">
													<div class="weekly-point" style="display: none;"></div> Saturday
												</li>

											</ul>
										</div>
									</div>

								</div>
								<div class="col-md-6 form-edit-group overflow-hidden">
									<div class="form-horizontal">
										<div class="edit-name model-name">Select Time</div>
										<div class="edit-value model-value">
											<span class="model-text1">from</span>
											<div class="form-edit-group model-input1">
												<input name="offStartTime" type="text" class=" form-control  required  from-time g-timeselect" readonly placeholder=" ">
											</div>
											<div class="form-edit-group model-input2">
												<input name="offEndTime" type="text" class="form-control  required  to-time g-timeselect" readonly placeholder=" ">
											</div>
											<span class="model-text2">to</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class=" g-panel clearfix " id="daily" style="display: none;margin-top:-10px">
						<!--daily-->
						<input name="repeatType" value="1" hidden>

						<div class="row">
							<div class="col-md-6">
								<div class="form-horizontal">
									<div class="form-edit-group overflow-hidden">
										<div class="edit-name model-name">Select Time</div>
										<div class="edit-value model-value">
											<span class="model-text1">from</span>
											<div class="form-edit-group model-input1">
												<input name="offStartTime" type="text" class="form-control  required  from-time g-timeselect" readonly placeholder=" ">
											</div>
											<div class="form-edit-group model-input2">
												<input name="offEndTime" type="text" class="form-control  required  to-time g-timeselect" readonly placeholder=" ">
											</div>
											<span class="model-text2">to</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class=" g-panel clearfix " id="onetime" style="display: none;margin-top:-10px">
						<!--one time-->
						<input name="repeatType" value="0" hidden>
						<div class="row">
							<div class="col-md-6">
								<div class="form-horizontal">
									<div class=" clearfix model-input">
										<div class="edit-name model-name">Select Date</div>
										<div class="edit-value model-value form-edit-group report-datetime">
											<input name="offDate" type="text" class="form-control form_onetime required g-timeselect" readonly placeholder=""> 
											<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
										</div>
									</div>
									<div class="form-horizontal">
										<div class="form-edit-group overflow-hidden">
											<div class="edit-name model-name">Select Time</div>
											<div class="edit-value model-value">
												<span class="model-text1">from</span>
												<div class="form-edit-group model-input1">
													<input name="offStartTime" type="text" class="form-control  required  from-time g-timeselect" readonly placeholder=" ">
												</div>
												<div class="form-edit-group model-input2">
													<input name="offEndTime" type="text" class="form-control  required  to-time g-timeselect" readonly placeholder=" ">
												</div>
												<span class="model-text2">to</span>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- /.modal-content -->
			<div class="modal-footer">
				<!--按钮-->
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="submit" class="btn btn-primary">Confirm</button>
			</div>
		</form>
		<!-- /.modal-dialog -->
	</div>
</div>
<!-- END -->
<script type="text/javascript">
	seajs.use('addAlertOff');
</script>
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:alert:alert off:delete'>1</shiro:hasPermission>">
</body>
</html>