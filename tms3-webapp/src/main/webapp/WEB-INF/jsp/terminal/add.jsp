<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

<div class="g-middle-content">
         <div class="container-fluid">
             <div class="row">
                 <!-- 内容区域这里开始 -->
                 <div class="col-sm-12 col-md-12">
                     <div class="g-panel ">
                         <!--edit-->
                          <div class="g-panel-title clearfix">
                	<div class="g-panel-text">
                		Add Terminal(s)
                	</div>
                </div>
                         <div class="g-panel-body">
                             <div class="row">
                                 <div class="col-md-5">
                                     <form class="form-horizontal" id="addTerminal" role="form" autocomplete="off">
                                     <input id="gid" type="hidden" name="groupId" value="${group.id}">
                                         <div class="form-edit-group overflow-hidden sntid">
                                             <div class="edit-name">Terminal SN<span class="icon-required">*</span></div>
                                             <div class="edit-value  ">
                                                 <div class="form-edit-group global-input1">
                                                     <input type="text" maxlength="36" class="form-control  numbersletters lengthtype required firstFocus"  placeholder=" "  name="start">
                                                 </div>
                                                 <span class="global-text">to</span>
                                                 <div class="form-edit-group  global-input2">
                                                     <input type="text" maxlength="36" class="form-control  numbersletters lengthtype "  placeholder=" " name="end">
                                                 </div>
                                             </div>
                                         </div>
                                         <div class="form-edit-group">
                                             <div class="edit-name">Terminal Type<span class="icon-required">*</span></div>
                                             <div class="edit-value">
                                                 <select class="form-control required" name='destModel'>
                                                        <option value="">
														 --Please Select--
													    </option>
														<c:forEach items="${typeList}" var="item">
															<option value="${item.id }">
																<c:out value="${item.name }" />
															</option>
														</c:forEach>
                                                 </select>
                                             </div>
                                         </div>
                                         <div class="form-edit-group">
                                             <div class="edit-name">Country<span class="icon-required">*</span></div>
                                             <div class="edit-value">
                                                <select name="countryId" id="countryId" class="form-control required">
								                   <option value="" >--Please Select--</option>
							                       <c:forEach items="${countryList}" var="item">
								                       <option value="${item.id}" ${group.country eq item.name ? "selected":"" }>
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
		                                             <c:if test="${!empty group.country }">
		                                                <c:forEach items="${provinceList}" var="item">
		                                                  <option value="${item.id}" ${group.province eq item.name ? "selected":"" }>
		                                                    <c:out value="${item.name }" />
		                                                  </option>
		                                                </c:forEach>
		                                             </c:if>
	                                           </select>
	                                           <div class="form-control-input">
			                                       <input type="text" class="form-control required" placeholder="Add or Select a value" name="provinceName" id="provinceName"
	                                               value='<c:out value="${group.province}" escapeXml="true" />' maxlength="64"
	                                               onchange="document.getElementById('provinceId').value='';">
		                                       </div>
                                             </div>
                                         </div>
                                         <div class="form-edit-group">
                                             <div class="edit-name">City<span class="icon-required">*</span></div>
                                             <div class="edit-value">
                                                 <input type="text" class="form-control citycheck required" placeholder=" " name="cityName" id="cityId"  value='<c:out value="${group.city}" escapeXml="true" />' maxlength="35">
                                             </div>
                                         </div>
                                         <div class="form-edit-group">
                                             <div class="edit-name">ZIP/Postal Code<span class="icon-required">*</span></div>
                                             <div class="edit-value">
                                                 <input type="text" class="form-control required zipcode" name="zipCode" id="zipcode" placeholder=" "   value='<c:out value="${group.zipCode}" escapeXml="true" />' maxlength="7">
                                             </div>
                                         </div>
                                         <div class="form-edit-group">
                                             <div class="edit-name">Time Zone<span class="icon-required">*</span></div>
                                             <div class="edit-value">
                                                  <select class="form-control required" name='timeZone' id="timeZoneId">
			                                            	<option value="">--Please Select--</option>
									                        <c:forEach items="${timeZoneList}" var="item">
									                        	<c:set var="time" value="${item.timeZoneId}" />
									                        	<c:set var="timeZone" value="${group.timeZone}" />
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
                                                        		<c:set var="daylightSaving" value="${group.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                                    	<c:if test="${empty parentTimeZoneDaylight}" >
                                                    		 <select class="form-control required" name='daylightSaving' style="cursor:pointer">
                                                        		<c:set var="daylightSaving" value="${group.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                                    	 <!-- disable daylightsaving-->
                                                    	<c:if test="${parentTimeZoneDaylight=='0'}" >
                                                    		 <select class="form-control required" name='daylightSaving' disabled style="cursor:not-allowed;">
                                                        		<c:set var="daylightSaving" value="${group.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                             </div>
                                         </div>
                                          <div class="form-edit-group">
	                                        <div class="edit-name">Sync To Server Time<span class="icon-required">*</span></div>
	                                        <div class="edit-value">
	                                            <select class="form-control required" name='syncToServerTime'>
				                                	<option value="0">Disable</option>
				                               		<option value="1">Enable</option>
				                               	</select>
	                                        </div>
                                    	</div>
                                         <div class="form-edit-group">
                                             <div class="edit-name">Address</div>
                                             <div class="edit-value">
                                                 <input type="text" class="form-control specialInput" name="address" placeholder=" " value='<c:out value="${group.address}" escapeXml="true" />' maxlength="256">
                                             </div>
                                         </div>
                                         <div class="form-edit-group">
                                             <div class="edit-name">Description</div>
                                             <div class="edit-value">
                                                 <textarea class="form-control" rows="3" id="" placeholder=" " name="description"> <c:out value="${group.description}" escapeXml="true" /></textarea>
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
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- deactivate terminal -->
<div class="modal fade" id="add-terminal-cover-modal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title">WARNING</h4>
      </div>
      <div class="modal-body text-center"></div>
      <!-- /.modal-content -->
      <div class="modal-footer">
        <p class="g-tree-bottom"></p>
        <div class="modal-footer-btngroup">
          <button type="button" class="btn btn-default " data-dismiss="modal">Cancel</button>
          <button type="button" class="btn btn-primary J-confirm-btn">Confirm</button>
        </div>

      </div>
    </div>
    <!-- /.modal-content --> </div>
  <!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<script type="text/javascript">
	seajs.use('add-terminal');
</script>
</body>
</html>