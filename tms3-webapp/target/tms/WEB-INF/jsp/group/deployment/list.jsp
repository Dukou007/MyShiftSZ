<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../../includes/top.jsp" />

<div class="g-middle-content">
	<div class="container-fluid">
		<div class="row">

			<div class="col-md-12 col-sm-12">
				<div class="g-panel">
					<div class="g-panel-title clearfix">
						<div class="g-panel-text">
							Deployments
						</div>
					</div>

					<div class="g-panel-body" id="deploy-list">
					</div>
				</div>
			</div>
		</div>
		<!-- 内容区域end-->
	</div>
</div>
<jsp:directive.include file="../../includes/bottom.jsp" />
<input type="hidden" id="deployGroupId" name="groupId" value="${group.id}" >
<!--modal delete task-->
<div class="modal fade" id="modal_group_delete" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
					<span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">WARNING</h4>
			</div>
			<div class="modal-body text-center">
				<p>Are you sure to cancel this task?</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary" id="confirm_group_delete">Confirm</button>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<script type="text/javascript">seajs.use('group_deployment_v2');</script>
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:group:deployments:delete'>1</shiro:hasPermission>">
</body>
</html>