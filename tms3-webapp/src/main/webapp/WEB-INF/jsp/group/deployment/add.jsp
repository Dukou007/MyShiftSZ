<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../../includes/top.jsp" />

<div class="g-middle-content">
	<div class="container-fluid">
			<div class="row">
				<!-- 内容区域这里开始 -->
				<div class="col-sm-12 col-md-5">
					<div class="g-panel">
						<div class="g-panel-title clearfix">
	                		<div class="g-panel-text">
	                    	 	Deploy Package
	                    	</div>
	                	</div>
						
						<div class="g-panel-body">
							<div class="row">
									<form class="form-horizontal" id="add-group-deploy" role="form" autocomplete="off">
										<input type="hidden"  value="${group.id}" id='gid'  >
										<input type="hidden"  name="groupId" value="${group.id}" id='deployGroupId'  >
										<input type="hidden"  name="pkgId" value="${pkg.id}" id='pkg-id' >
										<div class="form-edit-group view-item col-md-12">
											<div class="view-name">Group</div>
											<div class="view-value">
												<c:if test="${empty group.namePath}">  
													<c:out value="${group.name}" escapeXml="true" />
												</c:if>
												<c:if test="${not empty group.namePath}">
													<c:out value="${group.namePath}" escapeXml="true" />
												</c:if>
												<input type="hidden"  value="${group.name}" name="groupName"></div>
										</div>
										<div class="form-edit-group col-md-12">
											<div class="edit-name">Terminal Type</div>
											<div class="edit-value">
												<select class="form-control first-terminaltype firstFocus" name='destModel' >
													<option value="">--Please Select--</option>
													<c:forEach items="${modelList}" var="item">
														<option value="${item.id }">
															<c:out value="${item.name }" />
														</option>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="form-edit-group col-md-12">
											<div class="edit-name">
												Package Name
												<span class="icon-required">*</span>
											</div>
											<div class="edit-value">
												<select class="form-control required first-packagename" name='packagename'>
													<option value="">--Please Select--</option>
													<c:forEach items="${pkgNameList}" var="item">
															<option value="${item}" ${pkg.name eq item ? "selected":"" }>
																<c:out value="${item}" />
															</option>
														</c:forEach>
												</select>
											</div>
										</div>
										<div class="form-edit-group col-md-12">
											<div class="edit-name">
												Package Version
												<span class="icon-required">*</span>
											</div>
											<div class="edit-value">
												<select class="form-control required first-packageversion" name='packageversion'>
													<option value="">--Please Select--</option>
													<c:if test="${not empty pkg}">
													<option selected>
														<c:out value="${pkg.version}" />
													</option>
													</c:if>
												</select>
											</div>
										</div>
										<div class="form-edit-group col-md-12">
                                                    <div class="edit-name">Time Zone<span class="icon-required">*</span></div>
                                                    <c:set var="timeZone" value="${group.timeZone}" />
                                                    <div class="edit-value"> 
                                                        <select class="form-control required" name="timeZone"  id="timeZoneId">
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
                                        <%-- <div class="form-edit-group col-md-12">
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
                                                </div> --%>
										<!-- <div class="form-edit-group col-md-12 hide">
											<div class="edit-name">
												Template Name
												<span class="icon-required hide">*</span>
											</div>
											<div class="edit-value">
												<select class="form-control first-templatename" name='pkgSchemaId'>
													<option value="">--Please Select--</option>
												</select>
											</div>
										</div> -->
										<div class="form-edit-group col-md-12">
											<div class="edit-name">
												Download Time
												<span class="icon-required">*</span>
											</div>
											<div class="edit-value report-datetime deploy-datetime">
												<input type="text" class="form-control required form_datetime g-timeselect" placeholder=" Download Time" readonly="readonly" name="dwnlStartTime" id='first-templatename'>
												<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
											</div>
										</div>
										<div class="form-edit-group col-md-12" >
											<div class="edit-name">
												Activation Time
												<span class="icon-required">*</span>
											</div>
											<div class="edit-value report-datetime deploy-datetime">
												<input type="text" class="form-control required form_datetime g-timeselect" placeholder=" Activation Time" readonly="readonly" name="actvStartTime" id='first-templatename'>
												<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
												
											</div>
										</div>
										<div class="form-edit-group col-md-12" >
											<div class="edit-value" style="float:right;">
												<label for="installNow" style="cursor:pointer;margin-bottom:0;">Activate immediately after downloading </label>	
												<input id="installNow" name="installNow" type="checkbox">
												<span class="icon-required"></span>
											</div>
										</div>
										<div class="form-edit-group col-md-12" >
											<div class="edit-name">
												Expiration Time
												<span class="icon-required"></span>
											</div>
											<div class="edit-value report-datetime deploy-datetime">
												<input type="text" class="form-control form_datetime g-timeselect" placeholder=" Expiration Time" readonly="readonly" name="dwnlEndTime" id='first-templatename'>
												<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
												
											</div>
										</div>
										<div class="form-edit-group col-md-12" >
											<div class="edit-name">
												<input id="deletedWhenDone" name="deletedWhenDone" type="checkbox" value="1" checked>
												<span class="icon-required"></span>
											</div>
											<div class="edit-value">
												<label for="deletedWhenDone" style="cursor:pointer;margin-bottom:0;">Task deleted once all terminals have finished. </label>
											</div>
										</div>
							
										<div class="col-md-12 fenge-line"></div>
										
										<div class="col-md-12">
											<div class=" text-right">
												<button type="submit" class="btn btn-primary view-button-style">Submit</button>
											</div>
										</div>
									</form>
								</div>
							</div>
						
					</div>

				</div>
				<!-- 内容区域end-->
			</div>
	
	</div>
</div>


<jsp:directive.include file="../../includes/bottom.jsp" />

<div class="modal fade" id="deploy-confirm-modal">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header g-modal-title">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span>
					<span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">Deployment Confirmation </h4>
			</div>
			<div class="modal-body">
				<div class="confirm-modal-body">
					<div class="view-item ">
						<div class="view-name" >Group</div>
						<div class="view-value" style=" " id="confirm-groupName">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name" >Terminal Type</div>
						<div class="view-value" id="confirm-destModel" style=" ">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name" >Package Name</div>
						<div class="view-value" id="confirm-packagename" style=" ">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name" >Package Version</div>
						<div class="view-value" id="confirm-packageversion" style=" ">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name" >Time Zone</div>
						<div class="view-value" id="confirm-timeZone" style=" ">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name" >Download Time</div>
						<div class="view-value" id="confirm-dwnlStartTime" style=" ">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name" >Activation Time </div>
						<div class="view-value" id="confirm-actvStartTime" style=" ">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name" >Expiration Time</div>
						<div class="view-value" id="confirm-dwnlEndTime" style=" ">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name" >Current Terminal Number</div>
						<div class="view-value" id="confirm-currentCount" style=" ">
							
						</div>
					</div>
					<div class="view-item ">
						<div class="view-name"  >Delete Task after Completing</div>
						<div class="view-value" id="confirm-currentDelete" style=" ">
							
						</div>
					</div>
					<div class="view-item  view-item-fullrow view-item-spacial">
						<div class="view-name" >Current Terminal Detail</div>
						<div class="view-value" id="confirm-currentDetail" style=" ">
							
						</div>
					</div>
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
<script type="text/javascript">seajs.use('add-group-deploy');</script>
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:terminal:deployments:delete'>1</shiro:hasPermission>
">
</body>
</html>