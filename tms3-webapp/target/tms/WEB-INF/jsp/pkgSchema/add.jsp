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
    position: relative;
}
/*.g-middle-content .edit-name .icon-required{
    position: absolute;
    right: 5px;
    top:0px;
}*/
.parameters-ul{
    margin-bottom: 10px;
}
</style>
<input type="hidden" id="gid" value="${group.id}">
<form class="form-horizontal" id="add-template" role="form">
<ul class="nav nav-pills parameters-ul ulposition hide" role="tablist" ></ul>
<div class="g-middle-content">
    
    <div class="container-fluid">
        <div class="row">
            <!-- 内容区域这里开始 -->
            <div class="col-sm-12 col-md-12">
                    <div class="g-panel">
                         <div class="g-panel-title clearfix">
	                		<div class="g-panel-text">
	                    	 	<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Add Template
	                    	</div>
	                	</div>
                        <div class="g-panel-body">
                            <div class="row">
                                <div class="col-md-4">

                                    <input type="hidden" name="pkgId" id='first-pkgid'>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Template Name<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                            <input type="text" maxlength="128" class="form-control required" placeholder=" " name="name" id='first-templatename'></div>
                                    </div>
                                    
                                    <div class="form-edit-group">
                                        <div class="edit-name">Package Version<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                            <select class="form-control required" name='pkgVersion' id='first-packageversion'>
                                                <option value="">--Please Select--</option>
                                            </select>
                                        </div>
                                    </div>

                                </div>
                                <div class="col-md-4">
                                <div class="form-edit-group">
                                        <div class="edit-name">Package Name<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                            <select class="form-control required" name='pkgName' id='first-packagename'>
                                                <option value="">--Please Select--</option>
                                                <c:forEach items="${pkgNameList}" var="item">
                                                    <option value="${item}">
                                                        <c:out value="${item}" />
                                                    </option>
                                                </c:forEach>
                                            </select>
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
                                    <div class=" text-right">
                                        <button type="submit" class="btn btn-primary view-button-style">Confirm</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                
            </div>
            <!-- 内容区域end--> </div>
    </div>
</div>
</form>
<jsp:directive.include file="../includes/bottom.jsp" />
<script type="text/javascript">seajs.use('add-template');</script>
</body>
</html>