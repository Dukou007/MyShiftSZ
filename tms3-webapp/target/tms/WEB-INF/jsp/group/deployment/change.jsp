<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../../includes/top.jsp" />
<style type="text/css">
    .g-middle-content .form-control {

    height: 29px;
    padding: 0px 6px;
    font-size: 12px;
}
    .parameters-file-btn .btn{
        height: 29px;
        font-size: 12px;
    }
.ulposition {
    display: block;
    position: absolute;
    background-color: #f2f5f7;
    z-index: 999;
    top: 47px;
    left: 30px;
    right: 30px;
    min-height: 60px;
    padding-top: 15px;
    padding-bottom: 15px;
    border-color: #ed7989;
    box-shadow: 0px 1px 0 #babec1;
   
}
.g-middle-content .edit-name{
    line-height: 27px;
    position: relative;
}
.form-horizontal .checkbox{
	padding-top: 0px;
}
.parameters-ul{
    margin-bottom: 10px;
}
</style>

<ul class="nav nav-pills parameters-ul ulposition hide" role="tablist" ></ul>
<div class="g-middle-content">
	<div class="container-fluid">
	<input type="hidden"  value="${group.id}" id='gid'>
		<form class="form-horizontal" id="change-group-parameters" role="form">
				<input type="hidden"  name="groupId" value="${deployGroup.id}" id='deployGroupId'  >
				<input type="hidden"  name="pkgId" id='pkg-id' >
				<input type="hidden"  name="pkgSchemaId" class='pkgSchema-id' >
				<div class="row">
					<!-- 内容区域这里开始 -->
					<div class="col-sm-12 col-md-12">

						<div class="g-panel">

							<!-- <div class="g-panel-title clearfix">
								<span class="g-panel-text">Change Parameters</span>
							</div> -->
							<div class="g-panel-title clearfix">
		                		<div class="g-panel-text">
		                    	 	<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Change Parameters
		                    	</div>
		                	</div>
						
							<div class="g-panel-body">
								<div id="step1">
									<div class="row">
										<div class="col-md-8">
											<div class="row">
												<div class="view-item col-md-6">
													<div class="view-name">Group Name</div>
													<div class="view-value">
														 <c:out value="${deployGroup.name}" escapeXml="true" />
														<input type="hidden"  value='<c:out value="${deployGroup.name}" escapeXml="true" />' name="groupName"></div>
												</div>
												<div class="form-edit-group col-md-6">
													<div class="edit-name">Terminal Type</div>
													<div class="edit-value">
														<select class="form-control required first-terminaltype" name='destModel' >
															<option value="">--Please Select--</option>
															<c:forEach items="${modelList}" var="item">
																<option value="${item.id }">
																	<c:out value="${item.name }" />
																</option>
															</c:forEach>
														</select>
													</div>
												</div>
												<div class="form-edit-group col-md-6">
													<div class="edit-name">
														Package Name
														<span class="icon-required">*</span>
													</div>
													<div class="edit-value">
														<select class="form-control required first-packagename" name='packagename'>
															<option value="">--Please Select--</option>
														</select>
													</div>
												</div>
												<div class="form-edit-group col-md-6">
													<div class="edit-name">
														Package Version
														<span class="icon-required">*</span>
													</div>
													<div class="edit-value">
														<select class="form-control required first-packageversion" name='packageversion'>
															<option value="">--Please Select--</option>
														</select>
													</div>
												</div>
												<div class="form-edit-group col-md-6">
													<div class="edit-name">
														Download Time
														<span class="icon-required">*</span>
													</div>
													<div class="edit-value">
														<input type="text" class="form-control required form_datetime g-timeselect" placeholder=" " readonly="readonly" name="dwnlStartTime" id='first-dwnlStartTime'></div>
												</div>
												<div class="form-edit-group col-md-6" >
													<div class="edit-name">
														Activation Time
														<span class="icon-required">*</span>
													</div>
													<div class="edit-value">
														<input type="text" class="form-control required form_datetime g-timeselect" placeholder=" " readonly="readonly" name="actvStartTime" id='first-actvStartTime'></div>
												</div>
											</div>
										</div>

										<div class="col-md-12 fenge-line"></div>
										<div class="col-md-12">
											<div class="parameters-box hide">

												<ul class="nav nav-pills parameters-ul ulrelative" role="tablist"></ul>
												<div class="tab-content"></div>

											</div>
											<div id="messageBox">
												<ul></ul>
											</div>
											<div class=" text-right">
												<button type="submit" class="btn btn-primary view-button-style" id="step1-next">Next</button>
											<!-- 	<a class="btn btn-default" href="javascript:history.go(-1)">Cancel</a> -->
											</div>
										</div>
									</div>
								</div>
								<div id="step2" class="hide">
									<div class="row">
										<div class="col-md-8">
											<div class="row">
												<div class="view-item col-md-6">
													<div class="view-name">Group Name</div>
													<div class="view-value">
														 <c:out value="${deployGroup.name}" escapeXml="true" />
													</div>
												</div>
												<div class="view-item col-md-6">
													<div class="view-name">Terminal Type</div>
													<div class="view-value view-terminaltype">
													</div>
												</div>
												<div class="view-item col-md-6">
													<div class="view-name">
														Package Name
													</div>
													<div class="view-value view-packagename">
													</div>
												</div>
												<div class="view-item col-md-6">
													<div class="view-name">
														Package Version
														
													</div>
													<div class="view-value view-packageversion">
														
													</div>
												</div>
												<div class="view-item col-md-6">
													<div class="view-name">
														Download Time
													</div>
													<div class="view-value view-dwnlstarttime">
													</div>
														
												</div>
												<div class="view-item col-md-6" >
													<div class="view-name">
														Activation Time
	
													</div>
													<div class="view-value view-actvstarttime">
													</div>
												</div>
											</div>
										</div>

										<div class="col-md-12 fenge-line"></div>
										<div class="col-md-12">
											<table id="change-parameters-table" class="table"></table>
											<div class=" text-right">
												<button type="button" class="btn btn-primary view-button-style" id="step2-confirm">Confirm</button>
												<a type="button" class="btn btn-default" id="step2-back">Cancel</a>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

					</div>
					<!-- 内容区域end-->
				</div>
		</form>

	</div>
</div>

<jsp:directive.include file="../../includes/bottom.jsp" />
<!-- footer -->
<script type="text/javascript">seajs.use('change-group-parameters');</script>
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:terminal:deployments:delete'>1</shiro:hasPermission>
">
</body>
</html>