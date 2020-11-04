<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<style type="text/css">
.viewUserProRole.edit-name{
	width:80px;
	
}
.viewUserProRole.edit-value{
	margin-left:80px;
	
}
@media screen and (max-width: 767px){
	.viewUserProRole.edit-name{
		width:100%;
	}
	.viewUserProRole.edit-value{
		width:100%;
		margin-left:0;
	}
}
</style>
<div class="g-middle-content">
    <div class="container-fluid">
        <div class="row">
            <!-- 内容区域这里开始 -->
            <div class="col-sm-12 col-md-5">
                <!--new user-->
                <div class="g-panel" id="edit">
                 	<div class="g-panel-title clearfix">
                		<div class="g-panel-text">
                    	 	<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Add Local User
                    	</div>
                	</div>
                    <!--edit-->
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-md-12 col-sm-12">
                                <form class="form-horizontal" id="um_addUser" role="form" autocomplete="off">
                                <input type="hidden" name="groupId" value="${group.id}"/>
                                    <div class="view-item  bold font16px black">
                                        Personal
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">User Name<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control required firstFocus specialInput"  name="username" maxlength="36">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Full Name<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control required specialInput"  name="fullname" maxlength="128">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Mobile Phone<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control phone required"  name="phone" maxlength="60">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Email<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <input type="email" class="form-control required"  name="email" maxlength="128">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Country</div>
                                        <div class="edit-value">
                                            <select name="countryId" id="countryId" class="form-control">
												<option value="">
													--Please Select--
												</option>
												<c:forEach items="${countryList}" var="item">
													<option value="${item.id}">
														<c:out value="${item.name }" />
													</option>
												</c:forEach>
											</select>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">State/Province</div>
                                        <div class="edit-value">
                                          <select class="form-control" id="provinceId" name="provinceId"
                                                onchange="document.getElementById('provinceName').value=this.options[this.selectedIndex].text;">
                                           </select>
                                           <div class="form-control-input">
		                                       <input type="text" class="form-control" placeholder="Add or Select a value" name="provinceName" id="provinceName"
                                               maxlength="64"
                                               onchange="document.getElementById('provinceId').value='';">
	                                       </div>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">City</div>
                                        <div class="edit-value">
                                          	 <input type="text" class="form-control citycheck" placeholder=" " name="cityName" id="cityId" value="${user.city}" maxlength="35">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">ZIP/Postal Code</div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control zipcode text-uppercase"  name="zipCode" maxlength="7">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Address</div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control specialInput"   name="address">
                                        </div>
                                    </div>
                                    <div class="view-item  bold font16px black">
                                        Group<span class="icon-required">*</span>
                                        
                                    </div>
                                     <div class="form-edit-group">
                                        <div class="view-item">
                                            <div class="group_chooseG">
                                                <div class="group_note">
                                                </div>
                                                <span class="group_chooseG_bottom" title="Select group">
                                                    <i class="iconfont left-icon">&#xe602;</i>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                     <div class="form-edit-group  bold font16px black">
                                         Role<span class="icon-required ">*</span>
                                     </div>
                                     <div class="form-edit-group clearfix">
                                            <div class="edit-name viewUserProRole">PPM</div>
                                            <div class="edit-value clearfix viewUserProRole">
                                                <c:forEach items="${tmsRole }" var="item" varStatus="status">
                                                	 <div class="col-md-6 col-sm-6 col-xs-12">
										         <div class="role-privilege-group">
										         <label for="rolePPM_${item.id }" style="cursor:pointer;margin-bottom:0;">
										         	<span class="font14px black normal"> <c:out value="${item.name }" escapeXml="true" /></span>
										         </label>
										         <div class="role-privilege-checkbox">
                                                        <div class="checkbox">
                                                            <input id="rolePPM_${item.id }" type="checkbox" class="styled" aria-label="Single checkbox One" name="roleIds" value="${item.id }">
                                                            <label></label>
                                                        </div>
                                                  </div>
                                                  </div>
                                                  </div>
										        </c:forEach>
                                            </div>
                                        </div>
                                        <div class="form-edit-group role-box clearfix">
                                            <div class="edit-name viewUserProRole">PX Designer</div>
                                            <div class="edit-value clearfix viewUserProRole">
                                              	<c:forEach items="${PXRole }" var="item" varStatus="status">
                                              	<div class="col-md-6 col-sm-6 col-xs-12">
											         <div class="role-privilege-group">
											         	<label for="rolePX_${item.id }" style="cursor:pointer;margin-bottom:0;">
	                                                    	<span class="font14px black normal"> <c:out value="${item.name }" escapeXml="true" /></span>
	                                                    </label>
	                                                    <div class="role-privilege-checkbox">
	                                                        <div class="checkbox">
	                                                            <input id="rolePX_${item.id }" type="checkbox" class="styled" aria-label="Single checkbox One" name="roleIds" value="${item.id }">
	                                                            <label></label>
	                                                        </div>
	                                                    </div>
	                                                </div>
	                                                </div>
										        </c:forEach>
                                             
                                            </div>
                                       </div>
                                   
                                    <div class="pull-right view-button">
                                        <button type="submit" class="btn btn-primary view-button-style" id="confirmEdit">Confirm</button>
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
<!-- footer -->
<!-- new user-tree -->
<div class="modal fade" id="add-tree">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header g-modal-title">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">×</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">SELECT GROUP</h4>
      </div>
      <div class="modal-body">
        <div class="g-search">

          <div class="input-group">
            <input class="form-control g-searchInput" type="text" placeholder="Search Here">
            <div class="input-group-addon g-searchGo btn" id="g-searchGo">Go</div>
          </div>

        </div>
        <div class="group-tree-body">
          <div class="group-tree-content"></div>
          <div class="group-tree-search hide"></div>
        </div>
      </div>
      <!-- /.modal-content -->
      <div class="modal-footer">
        <p class="g-tree-bottom"></p>
        <div class="modal-footer-btngroup">
          <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary group-confirm-btn">Confirm</button>
        </div>

      </div>
    </div>
    <!-- /.modal-dialog --> </div>
</div>
<!-- footer -->

<script type="text/javascript">
var GROUP_ID=${group.id};

seajs.use('addUser');
</script>
</body>

</html>
