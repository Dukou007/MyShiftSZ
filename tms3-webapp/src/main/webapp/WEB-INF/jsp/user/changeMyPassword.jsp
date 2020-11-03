<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

<div class="g-middle-content">
	<div class="container-fluid">
		<div class="row">
			<!-- 内容区域这里开始 -->
			<!-- dashabord -->
			<div class="col-md-5 col-sm-12">
				<div class="g-panel">
				 	<div class="g-panel-title clearfix">
                		<div class="g-panel-text">
                    	 Change Password
                    	</div>
                	</div>
					<div class="g-panel-body">
						<div class="row">
							<div class="col-sm-12 col-md-12 form-horizontal">
								<form class="form-horizontal" id="changePwdForm" role="form" autocomplete="off">
									<div class="form-edit-group">
										<div class="edit-name">Old Password<span class="icon-required">*</span>
										</div>
										<div class="edit-value">
											<input type="password" class="form-control required firstFocus" id="oldPwd"
												placeholder=" " name="oldPassword">
										</div>
									</div>
									<div class="form-edit-group">
										<div class="edit-name">New Password<span class="icon-required">*</span>
										</div>
										<div class="edit-value">
											<input type="password"
												class="form-control required passwordCheck" id="newPwd"
												placeholder=" " name="newPassword" rangelength='[6,18]'>
										</div>
									</div>
									<div class="form-edit-group">
										<div class="edit-name">Confirm Password<span class="icon-required">*</span>
										</div>
										<div class="edit-value">
											<input type="password" class="form-control required" id="confirmNewPwd"
												placeholder="" name="ConfirmNewPassword" equalTo="#newPwd">
										</div>
									</div>
									<div class="pull-right view-button text-right">
										<button type="submit"
											class="btn btn-primary view-button-style" id="Confirm">Confirm</button>
										
									</div>
								</form>
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
<script>
	seajs.use('user-changePwd');
	var GROUP_ID=${group.id};
</script>
</body>

</html>
