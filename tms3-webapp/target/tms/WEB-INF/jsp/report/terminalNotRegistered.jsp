<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
	<div class="container-fluid">

		<div class="row">
			<!-- 内容区域这里开始 -->
			<div class="col-sm-12 col-md-12">
				<div class="g-panel" >
					<div class="g-panel-title clearfix" style="margin-bottom:0px;">
						<span class="g-panel-text">Terminal Not Registered</span>
					</div>
				</div>
			</div>
			<div class="col-sm-12 col-md-12">
				<div class="g-panel">
					<div class="g-panel-title text-right clearfix">
						<!--<span class="g-panel-text text-left ">Audit Log</span>-->
						<div class="block-inline vertical-top">
							<div class="input-group mb10 ml12" id="search-btn-group"
								style="width: 240px">
								<input class="form-control g-searchInput" type="text" placeholder="Terminal SN,Terminal Type"> 
                                 <div class="input-group-addon g-searchGo btn">Go</div>
							</div>
						</div>

						<button type="button" id="advance-btn"
							class="btn btn-primary g-icon-btn mb10 vertical-top ml12">
							<span class="glyphicon glyphicon-filter"></span> Advanced
						</button>
						<div class="report-export pull-left hide">
							<form id="exportUser" action="<%=contextPath%>/report/export/terminalNotRegisteredList/${group.id}"
								method="post">
								<input type="hidden" name="startTime"> 
								<input type="hidden" name="endTime"> 
								<input type="hidden" name="terminalType"> 
								<input type="hidden" name="fuzzyCondition">
								<div>
									<button class="btn btn-primary" type="submit">Export</button>
								</div>
							</form>
						</div>

					</div>
					<div class="advance-box hide">
						<div class="row">

							<div class="col-sm-3">
								<div class="report-datetime">
									<input type="text" class="form-control startTime g-timeselect" readonly
										id="start-time" placeholder="Start Time"> <span
										class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
								</div>


							</div>
							<div class="col-sm-3">
								<div class="report-datetime">
									<input type="text" class="form-control endTime g-timeselect" readonly id="end-time"
										placeholder="End Time"> <span
										class="glyphicon glyphicon-calendar datetime-icon"></span>
								</div>
							</div>
							<div class="col-sm-3">
								<div class="report-select">
									<select class="form-control" id="terminalType-select">
										<option value="">--Terminal Type--</option>
										<c:forEach items="${typeList}" var="item">
											<option value="${item.id }">
												<c:out value="${item.name }" />
											</option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class="col-sm-3 text-right">
									<button type="button" class="btn btn-primary report-btn"
										id="report-btn">Apply</button>
							</div>
							
							
						</div>
					</div>
					<div class="g-panel-body">
						<table id="table"></table>
						
					</div>
				</div>

			</div>

			<!-- 内容区域end-->

		</div>
	</div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- assign -->
<div class="modal fade" id="assign-terminal-modal">
  <div class="modal-dialog modal-lg">
      <div class="modal-content">
          <div class="modal-header g-modal-title">
            <button type="button" class="close" data-dismiss="modal">
              <span aria-hidden="true">×</span>
              <span class="sr-only">Close</span>
            </button>
            <h4 class="modal-title">ASSIGN TERMINAL</h4>
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
<!-- end assign -->

<script type="text/javascript">
	var groupId=${group.id};
	seajs.use('report-terminalNotRegistered');
</script>
<input type='hidden' id="permission-assign" value="<shiro:hasPermission name='tms:unrterminal:assign'>1</shiro:hasPermission>">
</body>
</html>