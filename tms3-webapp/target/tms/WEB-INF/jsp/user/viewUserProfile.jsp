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
            <!-- user profile -->
            <div class="col-md-5 col-sm-12" id="view">
                <div class="g-panel">
                	<div class="g-panel-title clearfix">
                		<div class="g-panel-text">
                    	 	<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>User Profile
                    	</div>
                	</div>
                 <div class="g-panel-title clearfix user-profile">
                        <div class="view-name">
                            <img src="<%=contextPath%>/static/images/user-pic.png" class="user-profile-img">
                        </div>
                         <div class="view-value">
                             <p class="user-profile-p1" style="margin-top:20px;"><c:out value="${user.username}" escapeXml="true" /></p>
                        </div> 
                    </div> 
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-sm-12 col-md-12 form-horizontal">
                                <div class="view-item bold font16px black">
                                    Personal
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Full Name</div>
                                    <div class="view-value"><c:out value="${user.fullname}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Mobile Phone</div>
                                    <div class="view-value"><c:out value="${user.phone}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Email</div>
                                    <div class="view-value"><c:out value="${user.email}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Status</div>
                                    <div class="view-value"><c:out value="${user.enabled?'actived':'deactived'}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Country</div>
                                    <div class="view-value" id="d-country-name"><c:out value="${user.country}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">State/Province</div>
                                    <div class="view-value" id="viewProvinceName"><c:out value="${user.province}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">City</div>
                                    <div class="view-value"><c:out value="${user.city}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">ZIP/Postal Code</div>
                                    <div class="view-value" id="d-zipCode"><c:out value="${user.zipCode}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Address</div>
                                    <div class="view-value">
                                        <c:out value="${user.address}" escapeXml="true" />
                                    </div>
                                </div>
                                <div class="view-item app-key hide">
                                    <div class="view-name">App Key</div>
                                    <div class="view-value"></div>
                                </div>
                               
                                <div class="view-item  bold font16px black">
                                    Groups
                                </div>
                                <div class="view-item">
                                	 <div class="form-edit-group" id="group-path-box" style="font-size: 14px;color: #4a647e;">
                                	   <c:forEach items="${user.userGroupList}" var="userGroup" varStatus="userGrouplists">
		                                   	<c:if test="${!userGrouplists.last}"><p title="${userGroup.group.namePath}"><c:out value="${userGroup.group.name}" escapeXml="true" />;</p></c:if>
		                                   	<c:if test="${userGrouplists.last}"><p title="${userGroup.group.namePath}"><c:out value="${userGroup.group.name}" escapeXml="true" /></p></c:if>
                                   	  </c:forEach> 
                                  
                                    </div>
                                </div>
                                 <div class="view-item  bold font16px black">
                                    Role
                                </div>
                                 <div class="view-item">
                                 <c:if test="${not empty selectedTMSRoleList}">
                                    <div class="view-name"><p class="font12px grey">PPM</p></div>
                                    <div class="view-value">
                                    	 
                                             <c:forEach items="${selectedTMSRoleList}" var="tmsRoles" varStatus="tmsStatus">
		                                    	  <c:if test="${!tmsStatus.last}">
				                                     <span class="font14px black"><c:out value="${tmsRoles}" escapeXml="true" />/</span>
				                                  </c:if> 
			                                      <c:if test="${tmsStatus.last}">
			                                   		 <span class="font14px black"><c:out value="${tmsRoles}" escapeXml="true" /></span>
			                                      </c:if> 
		                                 	</c:forEach>
                                    </div>
                                    </c:if>
                                </div>
                                 <div class="view-item">
                                 <c:if test="${not empty selectedPXRoleList}">
                                    <div class="view-name"> <p class="font12px grey">PXDesigner</p></div>
                                    <div class="view-value">
                                    	 
                                              <c:forEach items="${selectedPXRoleList}" var="pxRoles" varStatus="pxStatus">
		                                      
		                                       <c:if test="${!pxStatus.last}">
				                                      <span class="font14px black"><c:out value="${pxRoles}" escapeXml="true" />/</span>
				                                  </c:if> 
			                                      <c:if test="${pxStatus.last}">
			                                   		 <span class="font14px black"><c:out value="${pxRoles}" escapeXml="true" /></span>
			                                      </c:if> 
		                                    </c:forEach>
                                    </div>
                                     </c:if>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--user edit-->
            <div class="col-md-12 col-sm-12" id="edit" hidden>
                <div class="g-panel">
                	 <div class="g-panel-title clearfix">
                	 	<span class="g-panel-text">Edit User</span>
                	 </div>
                    <form role="form" id="userProfileEdit" autocomplete="off">
                     <input type="hidden" name="groupId" value="${group.id}"/>
                        <div class="g-panel-body">
                            <div class="row">
                                <div class="col-sm-12 col-md-10 form-horizontal">
                                    <div class="col-sm-12 col-md-6" id="ldap-part3">
                                        <div class="g-panel-title clearfix user-profile form-edit-group">
                                            <div class="view-name">
                                                <img src="<%=contextPath%>/static/images/user-pic.png" class="user-profile-img" id="mmm">
                                            </div>
                                            <div class="view-value">
                                             <p class="user-profile-p1" style="margin-top:20px;"><c:out value="${user.username}" escapeXml="true" /></p>
                                                <input type="text" class="form-control hide" readonly placeholder=" " name="username" value='<c:out value="${user.username}" escapeXml="true" />' />
                                          		 <input type="hidden" name="id"  value='<c:out value="${user.id}" escapeXml="true" />' />
                                            </div>
                                        </div> 
                                        <div class="ldap-part1">
                                        	 <div class="view-item bold font16px black">
                                            Personal
                                        </div>
                                        <div class="form-edit-group">
                                            <div class="edit-name uid">Full Name<span class="icon-required">*</span></div>
                                            <div class="edit-value">
                                                <input type="text" class="form-control  required firstFocus specialInput" placeholder=" " name="fullname" value='<c:out value="${user.fullname}" escapeXml="true" />' maxlength="128">
                                            </div>
                                        </div>
                                        <div class="form-edit-group">
                                            <div class="edit-name">Mobile Phone<span class="icon-required">*</span></div>
                                            <div class="edit-value">
                                                <input type="text" class="form-control phone required" placeholder=" " name="phone"  value='<c:out value="${user.phone}" escapeXml="true" />' maxlength="60">
                                            </div>
                                        </div>
                                        <div class="form-edit-group">
                                            <div class="edit-name">Email<span class="icon-required">*</span></div>
                                            <div class="edit-value">
                                                <input type="email" class="form-control required" placeholder=" " name="email"  value='<c:out value="${user.email}" escapeXml="true" />' maxlength="128">
                                            </div>
                                        </div>
                                        <div class="form-edit-group">
                                            <div class="edit-name">Country</div>
                                            <div class="edit-value">
                                                 <select class="form-control" name='countryId' id='countryId'>
                                               <option value="">
										        --Please Select--
										       </option>
                                               <c:forEach items="${countryList}" var="item">
													<option value="${item.id }" ${user.country eq item.name ? "selected":"" }>
														<c:out value="${item.name }" />
													</option>
												</c:forEach>
                                            </select>
                                            </div>
                                        </div>
                                        <div class="form-edit-group">
                                            <div class="edit-name">State/Province</div>
                                            <div class="edit-value">
                                             <c:forEach items="${provinceList}" var="item">
	                                        	     <c:if test="${item.name == user.province }">
							                             <input type="hidden" id="province_id" data-province_id="${item.id}"></input>
							                         </c:if>
						                	 </c:forEach>
											<select class="form-control" id="provinceId" name="provinceId"
                                                onchange="document.getElementById('provinceName').value=this.options[this.selectedIndex].text;">
                                             <c:if test="${!empty user.country }">
                                                <c:forEach items="${provinceList}" var="item">
                                                  <option value="${item.id}" ${user.province eq item.name ? "selected":"" }>
                                                    <c:out value="${item.name }" />
                                                  </option>
                                                </c:forEach>
                                             </c:if>
                                           </select>
                                           <div class="form-control-input">
                                           		<input type="text" class="form-control" placeholder="Add or Select a value" name="provinceName" id="provinceName"
	                                               value='<c:out value="${user.province}" escapeXml="true" />' maxlength="64"
	                                               onchange="document.getElementById('provinceId').value='';">
                                           </div>
                                            </div>
                                        </div>
                                        <div class="form-edit-group">
                                            <div class="edit-name">City</div>
                                            <div class="edit-value">
                                       			 <input type="text" class="form-control citycheck" placeholder=" " name="cityName" id="cityId" value='<c:out value="${user.city}" escapeXml="true" />' maxlength="35">
                                            </div>
                                        </div>
                                        <div class="form-edit-group">
                                            <div class="edit-name">ZIP/Postal Code</div>
                                            <div class="edit-value">
                                            	<input type="text" class="form-control zipcode" id="" placeholder="" name="zipCode"  maxlength="7"  value='<c:out value="${user.zipCode}" escapeXml="true" />'>
                                            </div>
                                        </div>
                                        <div class="form-edit-group">
                                            <div class="edit-name">Address</div>
                                            <div class="edit-value">
                                                <input type="text" class="form-control specialInput" id="" placeholder=" " name="address" value='<c:out value="${user.address}" escapeXml="true" />' maxlength="256">
                                            </div>
                                        </div>
                                      </div>
                                    </div>
                                       
                                    <div class="col-sm-12 col-md-6 ldap-part2">
                                    	<c:if test="${user.sys}">
	                                    	<div class="form-edit-group bold font16px black clearfix">
	                                            Group
	                                        </div>
	                                        <div class="form-edit-group clearfix">
	                                            <div class="edit-value clearfix viewUserProRole">
	                                            	<span>Entities</span>
	                                            	<input type="hidden" name="groupIds" value=1 hidden/>
	                                        	</div>
                                        	</div>
                                    	</c:if>
                                    	<c:if test="${!user.sys}">
                                    	 <div class="form-edit-group bold font16px black clearfix">
                                            Group<span class="icon-required">*</span>
                                        </div>
                                        <div class="form-edit-group clearfix">
                                            <div class="col-md-12 col-sm-12 view-item">
	                                            <div class="row">
	                                                <div class="group_chooseG">
	                                               
	                                                    <div class="group_note">
	                                                     <c:forEach items="${user.userGroupList}" var="userGroup">
	                                                      <span class="group_note_items" data-id="${userGroup.group.id}" data-idpath="${userGroup.group.idPath}" data-name="${userGroup.group.name}" title="${userGroup.group.namePath}"><c:out value="${userGroup.group.name}" escapeXml="true" />
	                                                      <span class="group_note_delete">x</span> <input type="hidden" name="groupIds" value="${userGroup.group.id}" id="shixm"/></span>
	                                                     
	                                                    </c:forEach>
	                                                    </div>
	                                                    <span class="group_chooseG_bottom" title="Select group">
	                                                        <i class="iconfont left-icon">&#xe602;</i>
	                                                    </span>
	                                                </div>
	                                            </div>
                                        	</div>
                                        </div>
                                        </c:if>
                                        <c:if test="${user.sys}">
	                                        <div class="form-edit-group bold font16px black">
	                                            Role
	                                        </div>
                                        	<div class="form-edit-group clearfix">
                                        		<div class="edit-value clearfix viewUserProRole">
                                        			<span>Site Administrator</span>
                                        			<input class="siteAdmin" type="hidden" name="roleIds" value=1 hidden/>
                                        		</div>
                                        	</div>
                                        </c:if>
                                        <c:if test="${!user.sys}">
                                        <div class="form-edit-group bold font16px black">
                                            Role<span class="icon-required">*</span>
                                        </div>
                                        <div class="form-edit-group clearfix">
                                            <div class="edit-name viewUserProRole"> PPM</div>
                                            <div class="edit-value clearfix viewUserProRole">
                                              	 <c:forEach items="${tmsRoleList}" var="role">
                                              	 <div class="col-md-6 col-sm-6 col-xs-12">
	                                                <div class="role-privilege-group">
	                                                	<div class="role-privilege-checkbox">
	                                                       <div class="checkbox">
	                                                            <input type="checkbox"  class="styled" id="eRolePPM_${role.id}"  value='<c:out value="${role.id}" escapeXml="true" />'
	                                                            aria-label="Single checkbox One" name="roleIds"  ${fn:contains(selectedTMSRoleList,role.name) ? 'checked':''}>
	                                                            <label></label>
	                                                        </div> 
	                                                    </div>
	                                                    <label for="eRolePPM_${role.id}" style="cursor:pointer;margin-bottom:0;">
	                                                    	<span class="font14px black normal"><c:out value="${role.name}" escapeXml="true" /></span>
	                                                    </label>
	                                                  </div>
	                                             </div>
                                                 </c:forEach>
                                            </div>
                                        </div>
                                        <div class="form-edit-group clearfix role-box">
                                            <div class="edit-name viewUserProRole">PX Designer</div>
                                            <div class="edit-value clearfix viewUserProRole">
                                              
                                              	 		<c:forEach items="${pxRoleList}" var="role">
                                              	 		 <div class="col-md-6 col-sm-6 col-xs-12">
		                                                <div class="role-privilege-group">
		                                                 <div class="role-privilege-checkbox">
		                                                         <div class="checkbox">
		                                                            <input type="checkbox"  class="styled" id="eRolePX_${role.id }"  value='<c:out value="${role.id}" escapeXml="true" />'
		                                                            aria-label="Single checkbox One" name="roleIds"  ${fn:contains(selectedPXRoleList,role.name) ? 'checked':''}>
		                                                            <label></label>
		                                                        </div> 
		                                                    </div>
		                                                    <label for="eRolePX_${role.id }" style="cursor:pointer;margin-bottom:0;">
		                                                    	<span class="font14px black normal"><c:out value="${role.name}" escapeXml="true" /></span>
		                                                    </label>
		                                                    </div>
		                                                </div>
                                             			</c:forEach> 
                                             
                                            </div>
                                        </div>
                                       </c:if>
                                    </div>
                                    <div class="pull-right view-button text-right">
                                        <button type="submit" class="btn btn-primary view-button-style" id="Confirm">Confirm</button>
                                        <a href="<%=contextPath%>/user/view/${user.id}/${group.id}" class="btn btn-default">Cancel</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="pull-left view-button text-right view" style="text-align:left;padding-left: 140px;">
                <shiro:hasPermission name="tms:app client:get">
                    <button class="btn btn-primary" id="GetAppKey">Get App Key</button>									
                </shiro:hasPermission>
                <shiro:hasPermission name="tms:app client:refresh">		
                    <button class="btn btn-primary hide"  data-toggle="modal" data-target="#modal_refresh_key" id="RefreshAppKeyBtn">Refresh App Key</button>									
                </shiro:hasPermission>
                <shiro:hasPermission name="tms:app client:remove">
                    <button class="btn btn-primary hide"  data-toggle="modal" data-target="#modal_remove_key" id="RemoveAppKeyBtn" >Remove App Key</button>	
                </shiro:hasPermission>
				<c:if test="${!user.sys}">
					<button type="button" class="btn btn-delete" id="" data-toggle="modal" data-target="#modal_user_delete">Delete</button>
				</c:if>
				<button type="button" class="btn btn-primary" id="EditButton">Edit</button>
			</div>
        </div>
    </div>
</div>
<!-- END -->
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- add-package-tree -->
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
<!-- END -->
<!-- modal -->
<!--modal delete user-->
<div class="modal fade" id="modal_user_delete" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                <p>Are you sure to delete this user?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="confirm_user_delete">Confirm</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!--modal refresh user-->
<div class="modal fade" id="modal_refresh_key" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                <p>Are you sure to refresh App Key of this user?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="RefreshAppKey">Confirm</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<!--modal refresh user-->
<div class="modal fade" id="modal_remove_key" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                <p>Are you sure to remove App Key of this user?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="RemoveAppKey">Confirm</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<script type="text/javascript">
	var USER_ID = ${user.id};
	var IS_LDAP=${user.ldap};
	var GROUP_ID=${group.id};
	var AppKey="${user.appKey}";
	seajs.use('um-edit');
</script>
</body>

</html>
