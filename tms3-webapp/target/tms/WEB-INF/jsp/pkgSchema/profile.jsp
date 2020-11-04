<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
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
}
.parameters-ul{
    margin-bottom: 10px;
}
</style>
<ul class="nav nav-pills parameters-ul ulposition hide" role="tablist" ></ul>
<div class="g-middle-content">
    <div class="container-fluid">
        <div class="row">
            <!-- 内容区域这里开始 -->
            <form class="form-horizontal" id="edit-template" role="form">
                <input type="hidden" id="gid" value="${group.id}">
                <div class="col-sm-12 col-md-12">
                    <div class="g-panel ">
                        <!--edit-->
                        <div class="g-panel-title clearfix">
                            <input type="hidden" name="pkgSchemaId" value="${pkgSchema.id}" id="pkgSchema-id"/>
                            <!-- <span class="g-panel-text">View Template</span> -->
                            <div class="g-panel-text">
	                    	 	<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>View Template
	                    	</div>
                            
                        </div>
                        <div class="g-panel-body">
                            <div class="row">

                                <div class="col-md-4 template-view ">
                                    <input type="hidden" name='pkgId' value="${pkgSchema.pkg.id}" id="first-pkgid">
                                    <div class="form-edit-group edit-item-panel hide">
                                        <div class="edit-name">
                                            Template Name
                                            <span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <input type="text" maxlength="128" class="form-control required" placeholder=" " name="name" id="first-templatename"  value='<c:out value="${pkgSchema.name}" escapeXml="true" />' /></div>
                                    </div>
                                    <div class="view-item view-item-panel ">
                                        <div class="view-name">
                                            Template Name
                                        </div>
                                        <div class="view-value first-templatename">
                                             <c:out value="${pkgSchema.name}" escapeXml="true" />
                                        </div>
                                    </div>
                                    <div class="view-item">
                                        <div class="view-name">Package Version</div>
                                        <div class="view-value"> <c:out value="${pkgSchema.pkg.version}" escapeXml="true" /></div>
                                    </div>

                                </div>
                                <div class="col-md-4 template-view">
                                    <div class="view-item">
                                        <div class="view-name">Package Name</div>
                                        <div class="view-value"> <c:out value="${pkgSchema.pkg.name}" escapeXml="true" /></div>
                                    </div>
                                </div>

                                <div class="col-md-12 fenge-line"></div>
                                <div class="col-md-12">
                                    <div class="parameters-box">
                                        <ul class="nav nav-pills parameters-ul ulrelative" role="tablist"></ul>
                                        <div class="tab-content"></div>
                                        <div id="messageBox">
                                            <ul></ul>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="text-right view-btn-group hide" >
                                        <shiro:hasPermission name="tms:template:delete">
                                            <button type="button" class="btn btn-danger view-button-style" data-toggle="modal" data-target="#remove-template-modal">Delete</button>
                                        </shiro:hasPermission>
                                        <shiro:hasPermission name="tms:template:edit">
                                            <button type="button" class="btn btn-primary view-button-style" id="go-edit">Edit</button>
                                        </shiro:hasPermission>
                                       

                                    </div>
                                    <div class="text-right edit-btn-group hide">
                                        <shiro:hasPermission name="tms:template:edit">
                                            <button type="submit" class="btn btn-primary view-button-style">Confirm</button>
                                        </shiro:hasPermission>
                                        <button type="button" class="btn btn-default" id="back-view" >Cancel</button>

                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </form>
            <!-- 内容区域end--> </div>
    </div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<script type="text/javascript">seajs.use('view-template');</script>
<!-- remove template -->
<div class="modal fade" id="remove-template-modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">Are you sure to delete this template?</div>
            <!-- /.modal-content -->
            <div class="modal-footer">
                <p class="g-tree-bottom"></p>
                <div class="modal-footer-btngroup">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary J-confirm-btn">Confirm</button>
                </div>

            </div>
        </div>
        <!-- /.modal-content --> </div>
    <!-- /.modal-dialog -->
</div>
</body>
</html>