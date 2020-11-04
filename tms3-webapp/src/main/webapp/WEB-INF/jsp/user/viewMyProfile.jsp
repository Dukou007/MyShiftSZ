<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
	<div class="container-fluid">
		<div class="row">
            <!-- 内容区域这里开始 -->
            <!-- user profile -->
            <div class="col-md-5 col-sm-12">
                <!--user profile-->
                <div class="g-panel" id="view">
                	<div class="g-panel-title clearfix">
                		<div class="g-panel-text">
                           My Profile
                    	</div>
                	</div>

                     <div class="g-panel-title clearfix user-profile">
                        <div class="view-name">
                            <img src="<%=contextPath%>/static/images/user-pic.png" class="user-profile-img">
                        </div>
                        <div class="view-value">
                        <p class="user-profile-p1" style="margin-top:20px;">
                            <c:out value="${user.username}" escapeXml="true" />
                        </p>
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
                                    <div class="view-value">
                                    	  <c:out value="${user.fullname}" escapeXml="true" />
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Mobile Phone</div>
                                    <div class="view-value">
                                    	<c:out value="${user.phone}" escapeXml="true" />
                                   </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Email</div>
                                    <div class="view-value"><c:out value="${user.email}" escapeXml="true" /></div>
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
                                    <div class="view-value"><c:out value="${user.address}" escapeXml="true" /></div>
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
                             
                             <!-- 非系统用户,显示group path -->
                                <c:if test="${!user.sys}">
                                 <div class="view-item  bold font16px black">
                                    Group
                                </div>
                                <ul class="user-profile-group-items">
                                	<c:forEach items="${user.userGroupList}" var="userGroup">
                                		<c:choose>
											<c:when test="${userGroup.defaultGroup eq true}">
												<li>
													<span class="chooseFavorite">
													  <span class="glyphicon glyphicon-heart red" aria-hidden="true"></span>
													</span>
													<span class="lispan"><c:out value="${userGroup.group.namePath}" escapeXml="true" /></span>
												</li>
											</c:when>
											<c:otherwise>
												<li>
		                                    		<span class="chooseFavorite">
		                                    		<span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span>
		                                    		</span>
		                                    		<span class="lispan"><c:out value="${userGroup.group.namePath}" escapeXml="true" /></span>
	                                    		</li>
											</c:otherwise>
										</c:choose>
                                	</c:forEach>
                                	<li></li>
                                </ul>
                                </c:if>
                                <div class="pull-right view-button text-right">
                                    <button type="submit" class="btn btn-primary view-button-style" id="EditButton">Edit</button>
                                
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!--user edit-->
                <div class="g-panel" id="edit" hidden>
                	 <div class="g-panel-title clearfix">
                        <span class="g-panel-text">Edit User</span>
                    </div>
                    <form role="form" id="userProfileEdit" autocomplete="off">
                    <input type="hidden" name="groupId" value='<c:out value="${group.id}" escapeXml="true" />' />
                      <div class="g-panel-title clearfix user-profile form-edit-group">
                            <div class="view-name">
                                <img src="<%=contextPath%>/static/images/user-pic.png" class="user-profile-img" id="mmm">
                            </div>
                            <div class="view-value">
                            	  <p class="user-profile-p1" style="margin-top:20px;"><c:out value="${user.username}" escapeXml="true" /></p>
                                <input type="text" class="form-control hide" readonly placeholder=" " name="username"  value='<c:out value="${user.username}" escapeXml="true" />' />
                              
                            </div>
                        </div> 
                        <div class="g-panel-body">
                            <div class="row">
                                <div class="col-sm-12 col-md-12 form-horizontal">
                                <div class="view-item bold font16px black">
                                    Personal
                                </div>
                                	
                                  <div class="form-edit-group">
                                        <div class="edit-name">Full Name<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control required firstFocus specialInput" placeholder=" " name="fullname"  
                                              value='<c:out value="${user.fullname}" escapeXml="true" />' maxlength="128" />
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Mobile Phone<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control required phone" id="ldapPhone" placeholder=" " name="phone"  value='<c:out value="${user.phone}" escapeXml="true" />' maxlength="60">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Email<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <input type="email" class="form-control required" id="ldapEmail" placeholder=" " name="email"  value='<c:out value="${user.email}" escapeXml="true" />' maxlength="128" />
                                        </div>
                                    </div>
                                    <c:choose>
                                    <c:when test="${user.ldap}">
                                    <div class="form-edit-group">
                                    <div class="edit-name">Country</div>
                                        <div class="edit-value">
                                        	<input type="text" class="form-control" placeholder=" " name="countryId"  value='<c:out value="${user.country}" escapeXml="true" />' />
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">State/Province</div>
                                        <div class="edit-value">
                                         <input type="text" class="form-control" placeholder=" " name="provinceId"  value='<c:out value="${user.province}" escapeXml="true" />' />
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">City</div>
                                        <div class="edit-value">
                                           <input type="text" class="form-control citycheck" placeholder=" " name="cityName" id="cityId"  value='<c:out value="${user.city}" escapeXml="true" />' maxlength="35" />
                                        </div>
                                    </div>
                                    </c:when>
                                    <c:otherwise>
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
							                             <input type="hidden" id="province_id" data-province_id="${item.id}" />
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
                                            <input type="text" class="form-control citycheck" placeholder=" " name="cityName" id="cityId" value='<c:out value="${user.city}" escapeXml="true" />' maxlength="35" />
                                        </div>
                                    </div>
                                    </c:otherwise>
                                    </c:choose>
                                    
                                    <div class="form-edit-group">
                                        <div class="edit-name">ZIP/Postal Code</div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control zipcode" id="zipcode" placeholder="" name="zipCode" maxlength="7" value='<c:out value="${user.zipCode}" escapeXml="true" />' />
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Address</div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control specialInput" id="" placeholder=" " name="address" value='<c:out value="${user.address}" escapeXml="true" />'  maxlength="256">
                                        </div>
                                    </div>
                                     <c:if test="${!user.sys}">
                                     <div class="view-item  bold font16px black">
                                    Group
                               		 </div>
                               		 <ul class="user-profile-group-items">
	                                	<c:forEach items="${user.userGroupList}" var="userGroup">
	                                		<c:choose>
												<c:when test="${userGroup.defaultGroup eq true}">
													<li>
														<input type="radio" name="favoriteGroupId" id="${userGroup.group.id}" value='<c:out value="${userGroup.group.id}" escapeXml="true" />'
														 checked hidden>
														<label for="${userGroup.group.id}" class="chooseFavorite edit">
														   <span class="glyphicon glyphicon-heart red"  aria-hidden="true"></span>
														</label>
														<span class="lispan"><c:out value="${userGroup.group.namePath}" escapeXml="true" /></span>
													</li>
												</c:when>
												<c:otherwise>
													<li>
														<input type="radio" name="favoriteGroupId" id="${userGroup.group.id}" value='<c:out value="${userGroup.group.id}" escapeXml="true" />'
														 hidden>
			                                    		<label for="${userGroup.group.id}" class="chooseFavorite edit">
			                                    			<span class="glyphicon glyphicon-heart-empty" aria-hidden="true"></span>
			                                    		</label>
			                                    		<span class="lispan"><c:out value="${userGroup.group.namePath}" escapeXml="true" /></span>
		                                    		</li>
												</c:otherwise>
											</c:choose>
	                                	</c:forEach>
	                                	<li>
	                                		<span class="glyphicon glyphicon-heart red"></span>
	                                        <span class="font12px grey user-profile-group-bottom">Indicate favorite group</span>
	                                	</li>
                                	</ul>
                                    </c:if>
                                    <div class="pull-right view-button text-right">
                                        <button type="submit" class="btn btn-primary view-button-style" id="Confirm">Confirm</button>
                                        <button type="button" class="btn btn-default view-button-style" id="Back">Cancel</button> 
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- END -->
<script type="text/javascript">
	var IS_LDAP=${user.ldap};
	var GROUP_ID=${group.id};
	seajs.use('user-profile-edit');
</script>
</body>

</html>
