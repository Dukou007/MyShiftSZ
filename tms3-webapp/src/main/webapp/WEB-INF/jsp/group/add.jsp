<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

  <div class="g-middle-content">
  <input type="hidden" id="gid" value="${group.id}">
                <div class="container-fluid">
                    <div class="row">
                        <!-- 内容区域这里开始 -->
                        <div class="col-sm-12 col-md-12">
                            <div class="g-panel ">
                                <!--edit-->
                                <div class="g-panel-title clearfix">
			                    	<div class="g-panel-text">
			                    		<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Add Group
			                    	</div>
		                    	</div>
                                <div class="g-panel-body">
                                    <div class="row">
                                        <div class="col-md-5">
                                            <form class="form-horizontal" id="addGroup" role="form" autocomplete="off">
                                            <input type="hidden" id="parentId" name="parentId" value="${parentGroup.id}">
                                                <div class="form-edit-group">
                                                    <div class="edit-name">Group Name<span class="icon-required">*</span></div>
                                                    <div class="edit-value">
                                                        <input type="text" maxlength="60" class="form-control required specialInput firstFocus" id="11" placeholder=" " name="name">
                                                    </div>
                                                </div>
                                                <div class="form-edit-group">
                                                    <div class="edit-name">Country<span class="icon-required">*</span></div>
                                                    <div class="edit-value">
                                                         <select name="countryId" id="countryId" class="form-control required">
								                              <option value="" >--Please Select--</option>
							                                  <c:forEach items="${countryList}" var="item">
								                                <option value="${item.id}" ${parentGroup.country eq item.name ? "selected":"" }>
								                                   <c:out value="${item.name }" />
								                                </option>
								                             </c:forEach>
							                            </select>
                                                    </div>
                                                </div>
                                                <div class="form-edit-group">
                                                    <div class="edit-name">State/Province<span class="icon-required">*</span></div>
                                                    <div class="edit-value">
	                                                    <select class="form-control" id="provinceId" name="provinceId"
			                                                onchange="document.getElementById('provinceName').value=this.options[this.selectedIndex].text;">
				                                             <c:if test="${!empty parentGroup.country }">
				                                                <c:forEach items="${provinceList}" var="item">
				                                                  <option value="${item.id}" ${parentGroup.province eq item.name ? "selected":"" }>
				                                                    <c:out value="${item.name }" />
				                                                  </option>
				                                                </c:forEach>
				                                             </c:if>
				                                         </select>
				                                         <div class="form-control-input">
				                                         	<input type="text" class="form-control required" placeholder="Add or Select a value" name="provinceName" id="provinceName"
			                                                value='<c:out value="${parentGroup.province}" escapeXml="true" />' maxlength="64"
			                                                onchange="document.getElementById('provinceId').value='';">
			                                             </div>
											       </div>
                                                </div>
                                                <div class="form-edit-group">
                                                    <div class="edit-name">City<span class="icon-required">*</span></div>
                                                    <div class="edit-value">
                                                       <input type="text" class="form-control citycheck required" placeholder=" " name="cityName" id="cityId" value='<c:out value="${parentGroup.city}" escapeXml="true" />' maxlength="35">
                                                    </div>
                                                </div>
                                                <div class="form-edit-group">
                                                    <div class="edit-name">ZIP/Postal Code<span class="icon-required">*</span></div>
                                                    <div class="edit-value">
				                                         <input type="text" class="form-control zipcode required" id="zipcode" placeholder="" name="zipCode" maxlength="7" value='<c:out value="${parentGroup.zipCode}" escapeXml="true" />'>
                                                    </div>
                                                </div>
                                                  <div class="form-edit-group">
                                                    <div class="edit-name">Time Zone<span class="icon-required">*</span></div>
                                                    <div class="edit-value">
                                                         <select class="form-control required" name='timeZone' id="timeZoneId">
			                                            	<option value="">--Please Select--</option>
									                        <c:forEach items="${timeZoneList}" var="item">
									                        	<c:set var="time" value="${item.timeZoneId}" />
									                        	<c:set var="timeZone" value="${parentGroup.timeZone}" />
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
                                                        		<c:set var="daylightSaving" value="${parentGroup.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                                    	<c:if test="${empty parentTimeZoneDaylight}" >
                                                    		 <select class="form-control required" name='daylightSaving' style="cursor:pointer">
                                                        		<c:set var="daylightSaving" value="${parentGroup.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                                    	 <!-- disable daylightsaving-->
                                                    	<c:if test="${parentTimeZoneDaylight=='0'}" >
                                                    		 <select class="form-control required" name='daylightSaving' disabled style="cursor:not-allowed;">
                                                        		<c:set var="daylightSaving" value="${parentGroup.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                                    	
                                                    </div>
                                                </div>
                                                <div class="form-edit-group">
                                                    <div class="edit-name">Address</div>
                                                    <div class="edit-value">
                                                        <input type="text" class="form-control specialInput" id="" placeholder=" " name="address" value='<c:out value="${parentGroup.address}" escapeXml="true" />' maxlength="256">
                                                    </div>
                                                </div>
                                                <div class="form-edit-group">
                                                    <div class="edit-name">Description</div>
                                                    <div class="edit-value">
                                                        <textarea class="form-control" rows="3" id="" placeholder=" " name="description"> <c:out value="${parentGroup.description}" escapeXml="true"/></textarea>
                                                    </div>
                                                </div>
                                                <div class="pull-right view-button text-right">
                                                    <button type="submit" class="btn btn-primary view-button-style" id="Confirm">Confirm</button>
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
            <!-- footer -->

<jsp:directive.include file="../includes/bottom.jsp" />
<script type="text/javascript">
	seajs.use('add-group');
	
</script> 
</body>
</html>