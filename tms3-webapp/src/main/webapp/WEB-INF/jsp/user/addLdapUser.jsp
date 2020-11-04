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
            <div class="col-sm-12 col-md-5">
                <!--new user-->
                <div class="g-panel" id="edit">
                
                   <div class="g-panel-title clearfix">
                		<div class="g-panel-text">
                    	 	<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Add LDAP User
                    	</div>
                	</div>
                    <!--edit-->
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-md-12 col-sm-12">
                                <form class="form-horizontal" id="ldap-before-authentic" role="form" autocomplete="off">
                                    <div class="view-item  bold font16px black">
                                        Personal
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">User Name<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value" style="position: relative;">
                                            <div class="ldap-getbtn">
                                                <button type="submit" class="btn btn-primary view-button-style" id="confirmEdit">Get</button>
                                            </div>
                                            <input type="text" class="form-control required firstFocus specialInput" placeholder="" name="ldapName" maxlength="36"/>
                                        </div>
                                    </div>
                                </form>
                      
                                      <form class="form-horizontal" id="ldap-after-authentic" role="form" hidden>
                                       <input type="hidden" name="groupId" value="${group.id}"/>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Full Name</div>
                                        <div class="edit-value">
                                        	<input type="hidden" class="ldap-input username" name="username" value="" >
                                         	<input type="hidden" name="ldap" value="true" />
                                         	<input type="text" class="ldap-input fullname" name="fullname" value="" readonly>
                                        </div>
                                    </div>
                                     <div class="form-edit-group">
                                        <div class="edit-name">Mobile Phone</div>
                                        <div class="edit-value">
                                            <input type="text" class="ldap-input tel" name="phone" value="" readonly>
                                        </div>
                                    </div>
                                     <div class="form-edit-group">
                                        <div class="edit-name">Email</div>
                                        <div class="edit-value">
                                            <input type="email" class="ldap-input mail" name="email" value="" readonly>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Country</div>
                                        <div class="edit-value">
                                            <input type="text" class="ldap-input country" name="countryName" value="" readonly>
                                        </div>
                                    </div>
                                     <div class="form-edit-group">
                                        <div class="edit-name">State/Province</div>
                                        <div class="edit-value">
                                            <input type="text" class="ldap-input province" name="provinceName" value="" readonly>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">City</div>
                                        <div class="edit-value">
                                            <input type="text" class="ldap-input city" name="cityName" value="" readonly>
                                        </div>
                                    </div>
                                     <div class="form-edit-group">
                                        <div class="edit-name">Zip/Post Code</div>
                                        <div class="edit-value">
                                            <input type="text" class="ldap-input zipCode" name="zipCode" value="" readonly>
                                        </div>
                                    </div>
                                     <div class="form-edit-group">
                                        <div class="edit-name">Address</div>
                                        <div class="edit-value">
                                            <input type="text" class="ldap-input address" name="address" value="" readonly>
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
                                    <div style="margin-top: 10px;">
                                        <div class="form-edit-group  bold font16px black">
                                            Role<span class="icon-required">*</span>
                                            
                                        </div>
                                        <div class="form-edit-group clearfix">
                                            <div class="edit-name viewUserProRole"> PPM</div>
                                            <div class="edit-value clearfix viewUserProRole">
                                                <c:forEach items="${tmsRole }" var="item" varStatus="status">
                                                	 <div class="col-md-6 col-sm-6 col-xs-12">
										         <div class="role-privilege-group">
										         <span class="font14px black normal"> <c:out value="${item.name }" escapeXml="true" /></span>
										         <div class="role-privilege-checkbox">
                                                        <div class="checkbox">
                                                            <input type="checkbox" class="styled" aria-label="Single checkbox One" name="roleIds" value="${item.id }">
                                                            <label></label>
                                                        </div>
                                                  </div>
                                                  </div>
                                                  </div>
										        </c:forEach>
                                            </div>
                                        </div>
                                        <div class="form-edit-group clearfix role-box">
                                            <div class="edit-name viewUserProRole">PX Designer</div>
                                            <div class="edit-value clearfix viewUserProRole">
                                              	<c:forEach items="${PXRole }" var="item" varStatus="status">
                                              	<div class="col-md-6 col-sm-6 col-xs-12">
											         <div class="role-privilege-group">
	                                                    <span class="font14px black normal"> <c:out value="${item.name }" escapeXml="true" /></span>
	                                                    <div class="role-privilege-checkbox">
	                                                        <div class="checkbox">
	                                                            <input type="checkbox" class="styled" aria-label="Single checkbox One" name="roleIds" value="${item.id }">
	                                                            <label></label>
	                                                        </div>
	                                                    </div>
	                                                </div>
	                                                </div>
										        </c:forEach>
                                             
                                            </div>
                                       </div>
                                    </div>
                                    <div class="pull-right view-button">
                                        <button type="submit" class="btn btn-primary view-button-style" id="confirmEdit">Confirm</button>
                                        <!-- <a type="button" class="btn btn-default view-button-style" id="Back" href="javascript:history.go(-1)">Cancel</a> -->
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
        <!-- /.modal-dialog -->
    </div>
</div>
<!-- footer -->
<script type="text/javascript">
var GROUP_ID=${group.id};
seajs.use('um-addLdapuser');
</script>
</body>

</html>
