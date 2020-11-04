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
			<input type="hidden"  name="pkgSchemaId" id='pkgSchema-id' value="${pkgSchemaId}" >
			<input type="hidden"  name="paramSet" id='param-Set'  value='<c:out value="${paramSet}" escapeXml="true" />'>
			<div class="row">
				<!-- 内容区域这里开始 -->
				<div class="col-sm-12 col-md-12">

					<div class="g-panel">

						<!-- <div class="g-panel-title clearfix">
							<span class="g-panel-text">View Deploy</span>
						</div> -->
						<div class="g-panel-title clearfix">
		                    	<div class="g-panel-text">
		                    		<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>View Deploy
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
											<div class="view-value package-name view-packagename"><c:out value="${pkg.name}" escapeXml="true" /></div>
										</div>
										<div class="view-item col-md-6">
											<div class="view-name">Package Version</div>
											<div class="view-value package-version view-packageversion" > <c:out value="${pkg.version}" escapeXml="true" /></div>
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
<script type="text/javascript">
	seajs.use('history-terminal-deploy');
</script>
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:terminal:deployments:delete'>1</shiro:hasPermission>
">
</body>
</html>