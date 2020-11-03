<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../../includes/top.jsp" />
	<div class="g-middle-content">
		<div class="container-fluid">
				<div class="row">
					<!-- 内容区域这里开始 -->
					<div class="col-sm-12 col-md-5">
						<div class="g-panel">
							 <div class="g-panel-title clearfix">
								<span class="g-panel-text">Deploy Package</span>
							</div> 
							<div class="g-panel-body">
								<div class="row">
									<div class="col-md-12">
										<div class="row">
										<form class="form-horizontal" id="add-template" role="form" autocomplete="off">
											<input type="hidden"  name="groupId" value="${group.id}" id='gid'  >
											<input type="hidden"  name="pkgId" value="${pkg.id}" id='pkg-id' >
											<div class="form-edit-group view-item col-md-12">
												<div class="view-name">Terminal SN</div>
												<div class="view-value">
													 <c:out value="${terminal.tsn}" escapeXml="true" />
													<input type="hidden"  value="${terminal.tsn}" name="tsn" id='first-tsn'></div>
											</div>
											<div class="form-edit-group col-md-12">
												<div class="edit-name">Package Name<span class="icon-required">*</span></div>
												<div class="edit-value">
													<select class="form-control required firstFocus" name='packagename' id='first-packagename'>
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
												<div class="edit-name">Package Version<span class="icon-required">*</span></div>
												<div class="edit-value">
													<select class="form-control required" name='packageversion' id='first-packageversion'>
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
                                                    <c:set var="timeZone" value="${terminal.timeZone}" />
                                                    <div class="edit-value">
                                                        <select class="form-control required" name="timeZone" id="timeZoneId">
                                                        	<option value="">--Please Select--</option>
									                        <c:forEach items="${timeZoneList}" var="item">
									                        	<c:set var="time" value="${item.timeZoneId}" />
									                        	<c:set var="timeZone" value="${terminal.timeZone}" />
									                            <option data-isDaylightSaving="${item.isDaylightSaving}" value="${item.timeZoneId}" ${timeZone eq time ? "selected":"" }>
									                            	<c:out value="${item.timeZoneName}" />
									                            </option>
									                        </c:forEach>
                                                        </select>
                                                    </div>
                                            </div>  
                                           <%--  <div class="form-edit-group col-md-12">
                                                <c:set var="parentTimeZoneDaylight" value="${parentTimeZone.isDaylightSaving}" />
                                                    <div class="edit-name">Daylight Saving<span class="icon-required">*</span></div>
                                                    <div class="edit-value">
                                                    <!-- enable daylightsaving-->
                                                    	<c:if test="${parentTimeZoneDaylight=='1'}" >
                                                    		 <select class="form-control required" name='daylightSaving' style="cursor:pointer">
                                                        		<c:set var="daylightSaving" value="${terminal.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                                    	<c:if test="${empty parentTimeZoneDaylight}" >
                                                    		 <select class="form-control required" name='daylightSaving' style="cursor:pointer">
                                                        		<c:set var="daylightSaving" value="${terminal.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                                    	 <!-- disable daylightsaving-->
                                                    	<c:if test="${parentTimeZoneDaylight=='0'}" >
                                                    		 <select class="form-control required" name='daylightSaving' disabled style="cursor:not-allowed;">
                                                        		<c:set var="daylightSaving" value="${terminal.daylightSaving}" />
                                                       			<option value="0" <c:if test="${daylightSaving == false}" > selected</c:if>>Disable</option>
			                               						<option value="1" <c:if test="${daylightSaving == true}"> selected</c:if>>Enable</option>
                                                        	</select>
                                                    	</c:if>
                                                    	
                                                    </div>
                                                </div> --%>
											<!-- <div class="form-edit-group hide col-md-12">
												<div class="edit-name">Template Name<span class="icon-required hide">*</span></div>
												<div class="edit-value">
													<select class="form-control" name='pkgSchemaId' id='first-templatename'>
														<option value="">--Please Select--</option>
													</select>
												</div>
											</div> -->
											<div class="form-edit-group col-md-12">
													<div class="edit-name">Download Time <span class="icon-required">*</span></div>
													<div class="edit-value report-datetime deploy-datetime">
														<input type="text" class="form-control required form_datetime g-timeselect" placeholder=" Download Time " readonly="readonly" name="dwnlStartTime" id='first-templatename'>
														<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
													</div>
											</div>
											<div class="form-edit-group col-md-12" >
												<div class="edit-name ">Activation Time<span class="icon-required">*</span></div>
												<div class="edit-value report-datetime deploy-datetime">
													<input type="text" class="form-control required form_datetime g-timeselect" placeholder="Activation Time " readonly="readonly" name="actvStartTime" id='first-templatename'>
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
												<div class="edit-name ">Expiration Time<span class="icon-required"></span></div>
												<div class="edit-value report-datetime deploy-datetime">
													<input type="text" class="form-control form_datetime g-timeselect" placeholder="Expiration Time " readonly="readonly" name="dwnlEndTime" id='first-templatename'>
													<span class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
												</div>
											</div>
										

									<div class="col-md-12 fenge-line"></div>
									<div class="col-md-12">
										<div class=" text-right">
											<button type="submit" class="btn btn-primary view-button-style">Confirm</button>
											<!-- <a class="btn btn-default" href="javascript:history.go(-1)">Cancel</a> -->
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
   </div>
</div>
<jsp:directive.include file="../../includes/bottom.jsp" />
<!-- footer -->
<script type="text/javascript">seajs.use('add-terminal-deploy');</script>
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:terminal:deployments:delete'>1</shiro:hasPermission>
">
</body>
</html>