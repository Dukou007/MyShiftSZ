<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

<div class="g-middle-content">
        <div class="container-fluid">

          <div class="row">
            <!-- 内容区域这里开始 -->
         
            <div class="col-sm-12 col-md-12">
            	<div id="global-edit" class="g-panel">
            		<div class="g-panel-title clearfix">
                		<div class="g-panel-text">
                    	 	<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Global Setting
                    	</div>
                	</div>
								 <div class="g-panel-title clearfix">
									<span class="g-panel-text" style="font-size:16px">Terminal Usage Threshold Setting</span>	
								</div> 
								<div class="g-panel-body">
									<div class="row">
										<div class="col-md-6" >
											<form class="form-horizontal" id="globalSettingForm" role="form" autocomplete="off">
											<input type="hidden" name="groupId" id="vid" value="${viewGroup.id}">
											<input type="hidden" id="gid" value="${group.id}">
											<div class="renderForm">
												
											</div>
					                   <c:forEach items="${usageList}" var="item" varStatus="status">
					                        <div class="overflow-hidden singleRecord" style="margin-bottom:15px;">
					                        	<input type="hidden"  name="itemName" value='<c:out value="${item.itemName}" escapeXml="true" />'>
					                            <div class="edit-name global-name"> <c:out value="${item.itemName}" escapeXml="true" /></div>
					                            <div class="edit-value global-value">
					                                <div class="form-edit-group global-input1" style="margin-bottom: 0px;position: relative;">
					                                    <c:if test="${!status.last}">
					                                     <input disabled type="text" class="form-control required regex_Port_1 thdValue" name="${item.id}" value="${item.thdValue}" placeholder=" " style="padding-right: 15px;" >
					                                    <span class="global-percent" >%</span>
					                                    </c:if> 
					                                    <c:if test="${status.last}">
					                                     <input disabled type="text" class="form-control  required regex_Number thdValue" name="${item.id}" value="${item.thdValue}" placeholder=" " style="padding-right: 15px;" >
					                                    </c:if> 
					                                </div>
					                                <span class="global-text">/</span>
					                                <div class="form-edit-group global-input2" style="margin-bottom: 0px">
					                                	 <select disabled class="form-control " name="reportCycle">
					                                	 	<option value="per day" <c:if test="${item.reportCycle == 'per day'}"> selected</c:if>>day</option>
										                    <option value="per week" <c:if test="${item.reportCycle == 'per week'}"> selected</c:if>>week</option>
										                    <option value="per month" <c:if test="${item.reportCycle == 'per month'}"> selected</c:if>>month</option>
								                             </select>
					                                </div>
					                            </div>
					                        </div>
					                        </c:forEach> 
											
											  <div class="pull-right" id="viewBtnGroup">
											  	<button type="button" class="btn btn-primary view-button-style" id="globalEdit">Edit</button>
												<!-- <a type="button" class="btn btn-default view-button-style" href="javascript:history.go(-1)">Cancel</a> -->
											  </div> 
											  <div class="pull-right hide" id="editBtnGroup">
											  	<button type="submit" class="btn btn-primary view-button-style" id="globalEdit">Confirm</button>
												<button type="button" class="btn btn-default view-button-style" id="Back">Cancel</button>
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

<script type="text/javascript">
	seajs.use('global-setting');
</script>
</body>
</html>