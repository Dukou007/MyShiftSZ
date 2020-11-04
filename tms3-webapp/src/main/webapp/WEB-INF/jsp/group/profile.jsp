<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<style>
#Delete,#EditButton{display:none;}
</style>
 <div class="g-middle-content">
        <div class="container-fluid">
		<input type="hidden" id="groupIds" value="${groupIds}">
          <div class="row">
            <!-- 内容区域这里开始 -->
            
            <div class="col-sm-12 col-md-12">
            <div class="row">
               <div class="col-md-5 col-sm-12">
            	<div id="view" class="g-panel" style="display: block;">
						
								<div class="g-panel-title clearfix">
			                    	<div class="g-panel-text">
			                    		<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Group Details
			                    	</div>
			                   	</div>
								<div class="g-panel-body">
									<!-- <div class="row">
										<div class="" > -->
											<div class="view-item">
											 	<div class="view-name ">Group Name</div>
		                   						<div class="view-value "> <c:out value="${viewGroup.name}" escapeXml="true" /></div>  
											</div>
											<div class="view-item">
											 	<div class="view-name ">Terminal Type</div>
		                   						<div class="view-value "> <c:out value="${termianlTypes}" escapeXml="true" /></div>  
											</div>
											<div class="view-item">
											 	<div class="view-name ">Terminal Number</div>
		                   						<div class="view-value "> <c:out value="${terminalNumber}" escapeXml="true" /></div>  
											</div>
											<div class="view-item">
											 	<div class="view-name ">Country</div>
		                   						<div class="view-value "  id="d-country-name"> <c:out value="${viewGroup.country}" escapeXml="true" /></div>  
											</div>
											<div class="view-item">
											 	<div class="view-name ">State/Province</div>
		                   						<div class="view-value " id="viewProvinceName"> <c:out value="${viewGroup.province}" escapeXml="true" /></div>  
											</div>
											<div class="view-item">
											 	<div class="view-name ">City</div>
		                   						<div class="view-value "> <c:out value="${viewGroup.city}" escapeXml="true" /></div>  
											</div>
											<div class="view-item">
											 	<div class="view-name ">ZIP/Postal Code</div>
		                   						<div class="view-value " id="d-zipCode"> <c:out value="${viewGroup.zipCode}" escapeXml="true" /></div>  
											</div>
											 <div class="view-item">
			                                    <div class="view-name">Time Zone</div>
			                                    <div class="view-value">
			                                    	<c:set var="timeZone" value="${viewGroup.timeZone}" />
			                                    	<c:forEach items="${timeZoneList}" var="item">
			                                    		<c:set var="time" value="${item.timeZoneId}" />
						                            	<c:if test="${timeZone == time}"><c:out value="${item.timeZoneName} " escapeXml="true" /></c:if>
			                                   		</c:forEach>
			                                    </div>
			                                </div>
				                                <div class="view-item">
				                                    <div class="view-name">Daylight Saving</div>
				                                    <div class="view-value">
				                                    	<c:set var="daylightSaving" value="${viewGroup.daylightSaving}" />
				                                        <c:if test="${daylightSaving == true}">Enable</c:if>
				                                        <c:if test="${daylightSaving == false}">Disable</c:if>
				                                    </div>
				                                </div>
											<div class="view-item">
											 	<div class="view-name ">Address</div>
		                   						<div class="view-value "> <c:out value="${viewGroup.address}" escapeXml="true" /></div>  
											</div>
											<div class="view-item">
											 	<div class="view-name ">Description</div>
		                   						<div class="view-value "> <c:out value="${viewGroup.description}" escapeXml="true" /></div>  
											</div>
											<div class="pull-right view-button">
											  <c:if test="${viewGroup.id != 1 }">
												<shiro:hasPermission name="tms:group:sync">
											    	<button type="button" style="width:174px;" class="btn btn-primary view-button-style" id="synchronize" data-toggle="modal" data-target="#modal_group_sync">Sync Profile to Terminals</button>
											  	</shiro:hasPermission>
											  	<shiro:hasPermission name="tms:group:edit">
											  		<button type="button" class="btn btn-primary view-button-style" id="EditButton">Edit</button>
											  	</shiro:hasPermission>
											  	<shiro:hasPermission name="tms:group:delete">
													<button type="button" class="btn btn-danger view-button-style" id="Delete" data-toggle="modal" data-target="#modal_group_delete">Delete</button>
											  	</shiro:hasPermission>
											  </c:if>
										  </div>
												
									 <!-- </div> 	
									</div> -->
								</div>
							</div>
							
							<div class="g-panel " id="edit" style="display: none;"><!--edit-->
								<div class="g-panel-title clearfix">
									<span class="g-panel-text">Edit Group</span>	
								</div>
								<div class="g-panel-body">
									<!-- <div class="row"> -->
										<!-- <div class="col-md-4" > -->
											<form class="form-horizontal" id="edit-form" role="form" autocomplete="off">
											<input type="hidden" id="gid" value="${group.id}">
											<input type="hidden" id="vid" name="id" value="${viewGroup.id}">
                          					<input type="hidden" id="parentId" name="parentId" value="${viewGroup.parent.id}">
												<div class="form-edit-group">
											    <div class="edit-name">Group Name<span class="icon-required">*</span></div>
											    <div class="edit-value">
											      <input type="text" maxlength="60" class="firstFocus form-control required specialInput" name='name' id="groupName"  value='<c:out value="${viewGroup.name}" escapeXml="true" />' placeholder=" ">
											    </div>
											  </div>
											  <div class="form-edit-group">
											    <div class="edit-name">Country<span class="icon-required">*</span></div>
											    <div class="edit-value">
											    	<select class="form-control required"  name="countryId" id="countryId" >
						                            <option value="">--Please Select--</option>
						                            <c:forEach items="${countryList}" var="item">
						                                <option value="${item.id}" ${viewGroup.country eq item.name ? "selected":"" }>
						                                   <c:out value="${item.name }" />
						                                </option>
						                            </c:forEach>
					                           		</select>
											    </div>
											  </div>
											  <div class="form-edit-group">
											    <div class="edit-name">State/Province<span class="icon-required">*</span></div>
											    <div class="edit-value">
											      <c:forEach items="${provinceList}" var="item">
	                                        	     <c:if test="${item.name == viewGroup.province }">
							                             <input type="hidden" id="province_id" data-province_id="${item.id}"></input>
							                         </c:if>
						                		  </c:forEach>
						                           <select class="form-control" id="provinceId" name="provinceId"
		                                                onchange="document.getElementById('provinceName').value=this.options[this.selectedIndex].text;">
		                                             <c:if test="${!empty viewGroup.country }">
		                                                <c:forEach items="${provinceList}" var="item">
		                                                  <option value="${item.id}" ${viewGroup.province eq item.name ? "selected":"" }>
		                                                    <c:out value="${item.name }" />
		                                                  </option>
		                                                </c:forEach>
		                                             </c:if>
		                                           </select>
		                                           <div class="form-control-input">
			                                           	<input type="text" class="form-control required" placeholder="Add or Select a value" name="provinceName" id="provinceName"
			                                               value='<c:out value="${viewGroup.province}" escapeXml="true" />' maxlength="64"
			                                               onchange="document.getElementById('provinceId').value='';">
		                                           </div>
											    </div>
											  </div>
											  <div class="form-edit-group">
											    <div class="edit-name">City<span class="icon-required">*</span></div>
											    <div class="edit-value">
											    	<input type="text" class="form-control citycheck required" placeholder=" " name="cityName" id="cityId" value='<c:out value="${viewGroup.city}" escapeXml="true" />' maxlength="35">
											    </div>
											  </div>
											  <div class="form-edit-group">
											    <div class="edit-name">ZIP/Postal Code<span class="icon-required">*</span></div>
											    <div class="edit-value">  
                                            		<input type="text" class="form-control zipcode required" id="zipcode" placeholder="" name="zipCode" maxlength="7" value="${viewGroup.zipCode}" value='<c:out value="${user.username}" escapeXml="true" />'>
											    </div>
											  </div>
											    <div class="form-edit-group">
                                        <div class="edit-name">Time Zone<span class="icon-required">*</span></div>
                                         <div class="edit-value">
                                            <select class="form-control required" name='timeZone' id="timeZoneId">
                                            	<option value="">--Please Select--</option>
						                        <c:forEach items="${timeZoneList}" var="item">
						                        	<c:set var="time" value="${item.timeZoneId}" />
						                        	<c:set var="timeZone" value="${viewGroup.timeZone}" />
						                            <option data-isDaylightSaving="${item.isDaylightSaving}" value="${item.timeZoneId}" ${timeZone eq time ? "selected":"" }>
						                            	<c:out value="${item.timeZoneName}" />
						                            </option>
						                        </c:forEach>
			                                </select>
                                        </div> 
                                    </div>
                                    <div class="form-edit-group">
                                    	<c:set var="parentTimeZoneDaylight" value="${parentTimeZone.isDaylightSaving}" />
                                        <div class="edit-name">Daylight Saving<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                        	   <!-- enable daylightsaving-->
                                              	<c:if test="${parentTimeZoneDaylight=='1'}" >
                                              		 <select class="form-control required" name='daylightSaving' style="cursor:pointer">
                                                  		<c:set var="daylightSaving" value="${viewGroup.daylightSaving}" />
                                                 			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
                            						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                  	</select>
                                              	</c:if>
                                              	<c:if test="${empty parentTimeZoneDaylight}" >
                                              		 <select class="form-control required" name='daylightSaving' style="cursor:pointer">
                                                  		<c:set var="daylightSaving" value="${viewGroup.daylightSaving}" />
                                                 			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
                            						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                  	</select>
                                              	</c:if>
                                              	 <!-- disable daylightsaving-->
                                              	<c:if test="${parentTimeZoneDaylight=='0'}" >
                                              		 <select class="form-control required" name='daylightSaving' disabled style="cursor:not-allowed;">
                                                  		<c:set var="daylightSaving" value="${viewGroup.daylightSaving}" />
                                                 			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
                            						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                  	</select>
                                              	</c:if>
                                        </div>
                                    </div>
											  <div class="form-edit-group">
											    <div class="edit-name">Address</div>
											    <div class="edit-value">
											      <input type="text" class="form-control specialInput" name='address' value="${viewGroup.address}" value='<c:out value="${user.username}" escapeXml="true" />' placeholder="" maxlength="256">
											    </div>
											  </div>
											  <div class="form-edit-group">
											    <div class="edit-name">Description</div>
											    <div class="edit-value">
											      <textarea class="form-control form-line" rows="3" name="description" placeholder=" "> <c:out value="${viewGroup.description}" escapeXml="true" /></textarea>
											    </div>
											  </div>
											  <div class="pull-right view-button">
										  	<button type="submit" class="btn btn-primary view-button-style" id="Confirm">Confirm</button>
										  	<button type="button" class="btn btn-default view-button-style" id="Back">Cancel</button>
										    </div>
											</form>
											
										<!-- </div> -->
									<!-- </div> -->
									
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
<!--modal sync terminal-->
<div class="modal fade" id="modal_group_sync" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-left">
                If you synchronize the group profile to terminals, the group's fields which are 'Country', 'State/Province', 'City', 'ZIP/Postal Code', 'Time Zone' and 'Daylight Saving' will be synchronized to these fields of all the terminals in this group. 
                Are you sure to synchronize? 
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="confirm_group_sync">Confirm</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>

<!--modal remove terminal-->
<div class="modal fade" id="modal_group_delete" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                If you delete the group, all the sub-groups will be deleted too. Are you sure to delete this group?
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

<!-- /.modal -->
<!--modal cancel task-->
<div class="modal fade" id="modal_group_cancel" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                <p>Are you sure to cancel this task?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="confirm_terminal_cancel">Confirm</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<script type="text/javascript">
	seajs.use('group-tree-profile');
</script>
</body>
</html>