<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
	<div class="container-fluid">

		<div class="row">
			<!-- 内容区域这里开始 -->
			<div class="col-sm-12 col-md-12">
				<div class="g-panel">
					<div class="g-panel-title text-right clearfix" style="margin-bottom:0px">
						<div class="g-panel-text pull-left mb10 text-left">Users List</div>
						<div class="block-inline vertical-top">
							<div class="input-group mb10 ml12" id="search-btn-group" style="width:240px">
								<input class="form-control g-searchInput" type="text" placeholder="User,Email,Domain"> 
                                 <div class="input-group-addon g-searchGo btn">Go</div>
							</div>
						</div>
						<div class="dropdown block-inline ml12 mb10">
							<button class="btn btn-primary dropdown-toggle " type="button" data-toggle="dropdown">
								New User
								<span class="caret"></span>
							</button>
							<ul class="dropdown-menu dropdown-menu-right" role="menu">
								<li role="presentation">
									<a role="menuitem" tabindex="-1" href="<%=contextPath%>/user/toAddUser/${group.id}">Local User</a>
								</li>
								<li role="presentation">
									<a role="menuitem" tabindex="-1" href="<%=contextPath%>/user/toAddLdapUser/${group.id}">LDAP User</a>
								</li>
							</ul>
						</div>
						<div class="text-left">
							<div class="dropdown mb10 block-inline vertical-top">
								<button class="btn dropdown-toggle " style="font-size:13px;background-color:#fff;color:#4d5aa6;" type="button" data-toggle="dropdown">
								PPM
								<span class="caret"></span>
								</button>
								<ul class="dropdown-menu" role="menu" id="systemTab" >
									<li role="presentation" data-type="PPM">
										<a style="color:#4d5aa6;">PPM</a>
									</li>
									<li role="presentation" class="" data-type="Px Designer">
										<a style="color:#4d5aa6;">Px Designer</a>
									</li>
								</ul>
							</div>
							<ul class="nav nav-pills mb10 block-inline" role="tablist" id="roleTab">
							</ul>	
						</div>
						

				</div>
				<div class="g-panel-body">
					<table id="user-table"></table>

				</div>
			</div>
		</div>
		<!-- 内容区域end-->
	</div>
</div>

</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- delete user -->
<div class="modal fade" id="delete-user-modal">
<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span>
				<span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">WARNING</h4>
		</div>
		<div class="modal-body text-center">Are you sure to delete this user?</div>
		<!-- /.modal-content -->
		<div class="modal-footer">

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
<!-- /.modal -->
<!-- deactivate user -->
<div class="modal fade" id="deactivate-user-modal">
<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span>
				<span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">WARNING</h4>
		</div>
		<div class="modal-body text-center">Are you sure to deactivate this user?</div>
		<!-- /.modal-content -->
		<div class="modal-footer">

			<div class="modal-footer-btngroup">
				<button type="button" class="btn btn-default " data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary J-confirm-btn">Confirm</button>
			</div>

		</div>
	</div>
	<!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<!-- activate user -->
<div class="modal fade" id="activate-user-modal">
<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span>
				<span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">WARNING</h4>
		</div>
		<div class="modal-body text-center">Are you sure to activate this user?</div>
		<!-- /.modal-content -->
		<div class="modal-footer">

			<div class="modal-footer-btngroup">
				<button type="button" class="btn btn-default " data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary J-confirm-btn">Confirm</button>
			</div>

		</div>
	</div>
	<!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<!-- reset password -->
<div class="modal fade" id="reset-user-modal">
<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span>
				<span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">RESET PASSWORD</h4>
		</div>
		<div class="modal-body text-center">Choose the options to reset the password.</div>
		<!-- /.modal-content -->
		<div class="modal-footer">

			<div class="modal-footer-btngroup">
				<button type="button" class="btn btn-default " data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary J-confirm-btn2">Generate password</button>
				<button type="button" class="btn btn-primary J-confirm-btn">Send reset link</button>
			</div>

		</div>
	</div>
	<!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<script type="text/javascript">seajs.use('user-list');
var GROUP_ID=${group.id};
</script>
</body>
</html>