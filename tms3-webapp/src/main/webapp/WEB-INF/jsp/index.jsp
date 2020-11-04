<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="includes/top.jsp" />
<style type="text/css">
	.delete-dashboard-item{
		cursor: pointer;
	    position: absolute;
	    right: 1px;
	    top: -3px;
	    width: 30px;
	    height: 30px;
	    color: #ff7068;
	    font-weight: 700;
	    font-size: 23px;
	    text-align: center;
	  /*   z-index:999; */
	}
	.dashboard-box .dashboard-name{
		font-size:14px;
		font-weight:500;
		margin-bottom:1em;
		
	}
	.dashboard-box .dashboard-smallnumber{
		position:relative;
		padding-left:35px;
		color:#8f97ab;	
	}
	.dashboard-smallnumber-title{
		min-width:30px;
		overflow:hidden;
		white-space:nowrap;
		text-overflow: ellipsis;
	}
	.dashboard-smallnumber:before{
		content:'';
		width:10px;
		height:10px;
		border-radius:50%;
		background-color:#ff7068;
		position:absolute;
		left:15px;
		top:6px;
	}
	.dashboard-smallnumber:after{
		content:'';
		width:6px;
		height:6px;
		border-radius:50%;
		background-color:#fff;
		position:absolute;
		left:17px;
		top:8px;
	}
	.dashboard-smallnumber.errorC:before{
		background-color:#ff7068;
	}
	.dashboard-smallnumber.successC:before{
		background-color:#59c402;
	}
	.dashboard-smallnumber.disabledC:before{
		background-color:#8f97ab;
	}
	.dashboard-num{
		margin-left:20px;
		color:#4a647e;
	}
	.dashboard-num2{
		position:absolute;
		text-align:center;
		font-size:18px;
		font-weight:bold;
		left: 50%;
    	top: 50%;
   		transform: translate(-50%,-50%);
   		-webkit-transform: translate(-50%,-50%);
   		-o-transform: translate(-50%,-50%);
   		-moz-transform: translate(-50%,-50%);
   		-ms-transform: translate(-50%,-50%);
		
	}
	.usageStatus{
 		text-align: center;
	}
	
	.usageStatus-name{
		position:relative;
		padding-left:1.5em;
		text-align:left;
		font-size:12px;
		color:#8f97ab;
		display:inline-block;
		padding-right:0.5em;
		height:40px;
		line-height: 40px;	
	}
	
	.usageStatus-name:before{
		content:'';
		width:6px;
		height:6px;
		position:absolute;
		left: 3px;
     	top: 17px;
		
	}
	
	@media (max-width: 360px) {
		.usageStatus-name{
			display: block;
			width: 100%;
			
		}
	}
	.usageStatus-name.error:before{
		background-color:#ff7068;;
	}
	.usageStatus-name.success:before{
		background-color:#59c402;
	}
	.usageStatus-name.unavailable:before{
		background-color:#8f97ab;
	}
	.dashboard-item{margin-top:0;}
	.realTime-dashboard-row,.usage-dashboard-row{position:relative;}
	.drag .realTime-dashboard-row,.dragg .realTime-dashboard-row > :not(.dragging),.drag .usage-dashboard-row,.dragg .usage-dashboard-row > :not(.dragging) {
	  -webkit-transition: all 0.4s ease-in-out;
	  -o-transition: all 0.4s ease-in-out;
	  -moz-transition: all 0.4s ease-in-out;
	  -ms-transition: all 0.4s ease-in-out;
	  transition: all 0.4s ease-in-out; }
	.realTime-dashboard-row .dragging,.usage-dashboard-row .dragging {
	  z-index: 800; }
	.realTime-dashboard-row .dashboard-item,.usage-dashboard-row .dashboard-item{padding:0;}
	.row{margin:0;}
  .button-active{    line-height: 1.42857143;
    padding: 6px 12px;border:1px solid transparent;}
</style>
<div class="g-middle-content">
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12 col-sm-12" id="real-time-panel">
				<div class="g-panel">
					<div class="g-panel-title clearfix">
						<span class="g-panel-text">Real - Time Status </span> <span class="g-panel-detail realTime-dashaboard-time dashaboard-time">
						</span>
						 
						<div class="g-panel-button button-active pull-right" data-toggle="modal" data-target="#add-dashboard-realtime-modal">
							<span class="glyphicon glyphicon-plus "></span>
							<!-- <span>Add</span> -->
						</div>
						<div class="dropdown g-pannel-dropdown real-time-dropdown">
						</div>
					</div>
					<div class="g-panel-body">
						<div class="row dashboard-row realTime-dashboard-row ">
						</div>
					</div>
				</div>
			</div>
			<!-- dashabord-usage-->
			<div class="col-md-12 col-sm-12" id="usage-panel" style="display:none" >
				<div class="g-panel">
					<div class="g-panel-title clearfix">
						<span class="g-panel-text">Usage Data</span>
						<span class="g-panel-detail usage-dashaboard-time dashaboard-time">
						</span>
						<div class="g-panel-button button-active pull-right" data-toggle="modal" data-target="#add-dashboard-usage-modal">
							<span class="glyphicon glyphicon-plus "></span>
							<!-- <span>Add</span> -->
						</div>
						<div class="dropdown g-pannel-dropdown usage-type">
						</div>
						<div class="dropdown g-pannel-dropdown usage-chart-type">
						</div>
						
					</div>
					<div class="g-panel-body">
						<div class="row usage-dashboard-row dashboard-row">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<jsp:directive.include file="includes/bottom.jsp" />

<!-- add real-time modal-->
<div class="modal fade" id="add-dashboard-realtime-modal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">ADD DASHBOARDS</h4>
			</div>
			<div class="modal-body text-center">
				<div class="form-edit-group">
				<div class="edit-name model-name">Real-time</div>
					<div class="edit-value model-value">
						<ul class="clearfix weekly-ul"></ul>
					</div>
				</div>
			</div>
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
<!-- add dashboard usage modal-->
<div class="modal fade" id="add-dashboard-usage-modal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">ADD DASHBOARDS</h4>
			</div>
			<div class="modal-body text-center">
				<div class="form-edit-group">
				<div class="edit-name model-name">Usage</div>
					<div class="edit-value model-value">
						<ul class="clearfix weekly-ul"></ul>
					</div>
				</div>
			</div>
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

<script type="text/javascript">
	seajs.use('real-time');
</script>
<input type='hidden' id="permission-view" value="<shiro:hasPermission name='tms:terminal:view'>1</shiro:hasPermission>">
</body>
</html>