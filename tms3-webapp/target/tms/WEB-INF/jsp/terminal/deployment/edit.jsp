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
.parameters-ul{
    margin-bottom: 10px;
}

</style>
<ul class="nav nav-pills parameters-ul ulposition hide" role="tablist" ></ul>
<div class="g-middle-content">
	<div class="container-fluid">
		<form class="form-horizontal" id="edit-deploy" role="form">
			<input type="hidden"  name="groupId" value="${group.id}" id='gid'  >
			<input type="hidden"  name="pkgSchemaId" id='pkgSchema-id' value="${deploy.pkgSchema.id}" >
			<input type="hidden"  name="deployId" value="${deploy.id}">
			<input type="hidden"  name="paramSet" id='param-Set' value='<c:out value="${deploy.paramSet}" escapeXml="true" />'>
			<div class="row">
				<!-- 内容区域这里开始 -->
				<div class="col-sm-12 col-md-12">

					<div class="g-panel">
						<div class="g-panel-title clearfix">
	                    	<div class="g-panel-text">
	                    		 <a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a><span class="g-panel-text-viewDeploy">View Deploy</span>
	                    	</div>
	                    </div>
								
						<div class="g-panel-body">
							<div class="row">
								<div class="col-md-8">
									<div class="row">
										<div class="view-item col-md-6">
											<div class="view-name">SN/TID</div>
											<div class="view-value">
												 <c:out value="${terminal.tsn}" escapeXml="true" />
												<input type="hidden"  value="${terminal.tsn}" name="tsn" id='first-tsn'></div>
										</div>
										<div class="view-item col-md-6">
											<div class="view-name">Package Name</div>
											<div class="view-value package-name view-packagename"> <c:out value="${deploy.pkg.name}" escapeXml="true" /></div>
										</div>
										<div class="view-item col-md-6">
											<div class="view-name">Package Version</div>
											<div class="view-value package-version view-packageversion" > <c:out value="${deploy.pkg.version}" escapeXml="true" /></div>
										</div>
										<div class="form-edit-group edit-item-panel hide col-md-6">
											<div class="edit-name">
												Download Time
												<span class="icon-required">*</span>
											</div>
											<div class="edit-value">
												<input id="dwnlstarttime" type="text" class="form-control required form_datetime g-timeselect" placeholder=" " readonly="readonly" name="dwnlStartTime" value="<fmt:formatDate value="${deploy.dwnlStartTime}" pattern="yyyy-MM-dd HH:mm:ss" />">
												</div>
										</div>
										<div class="view-item view-item-panel col-md-6">
											<div class="view-name">
												Download Time
											</div>
											<div class="view-value J-dwnlstarttime">
												<fmt:formatDate value="${deploy.dwnlStartTime}" pattern="yyyy-MM-dd HH:mm:ss" />
												</div>
										</div>
										<div class="form-edit-group edit-item-panel hide col-md-6" >
											<div class="edit-name">
												Activation Time
												<span class="icon-required">*</span>
											</div>
											<div class="edit-value">
												<input type="text" id="actvstarttime" class="form-control required form_datetime g-timeselect" placeholder=" " readonly="readonly" name="actvStartTime" value="<fmt:formatDate value="${deploy.actvStartTime}" pattern="yyyy-MM-dd HH:mm:ss" />">
												
												</div>
										</div>
										<div class="view-item view-item-panel col-md-6" >
											<div class="view-name">
												Activation Time
											</div>
											<div class="view-value J-actvstarttime">
												<fmt:formatDate value="${deploy.actvStartTime}" pattern="yyyy-MM-dd HH:mm:ss" />
												</div>
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
								</div>
								<div class="col-md-12">
									<div class=" text-right view-btn-group hide">
										<button type="button" class="btn btn-primary view-button-style" id="go-edit">Edit</button>
										
									</div>
									<div class=" text-right edit-btn-group hide">
										<button type="submit" class="btn btn-primary view-button-style">Confirm</button>
										<button type="button" class="btn btn-default" id="back-view" >Cancel</button>
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
<script type="text/javascript">seajs.use('view-terminal-deploy');</script>
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:terminal:deployments:delete'>1</shiro:hasPermission>
">
</body>
</html>