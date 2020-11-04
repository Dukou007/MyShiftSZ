<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<style>
.bootstrap-table .table>thead>tr>th {
	vertical-align: middle !important;
}

.report-select1 {
	margin-bottom: 8px;
}

.fixed-table-container thead th .th-inner {
	white-space: normal;
	text-align: center;
	line-height: 20px;
	padding: 1px;
}

.table-w {
	min-width: 65px;
	max-width: 100%;
	max-height: 40px;
	word-break: break-all;
	overflow: hidden;
}
</style>
<div class="g-middle-content">
	<div class="container-fluid">

		<div class="row">
			<input type ="hidden" id="itemStatus" value="${itemStatus}">
			<input type ="hidden" id="itemName" value="${itemName}">
			<!-- 内容区域这里开始 --> <%-- <div class="col-sm-12 col-md-12">
				<div class="g-panel">
					<div class="g-panel-title clearfix" style="margin-bottom: 0px;">
					<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>
						<c:choose>
							<c:when test="${itemStatus == 'Success'}">
								<span class="g-panel-text">Successful</span>
							</c:when>
							<c:otherwise>
								<span class="g-panel-text">${itemStatus}</span>
							</c:otherwise>
						</c:choose>
						<span class="g-panel-text">${itemName}</span>
					</div>
				</div>

			</div> --%>
			<div class="col-sm-12 col-md-12">
				<div class="g-panel">
					<div class="g-panel-title text-right clearfix">
						<div class="g-panel-text">
							<a class="g-back-button iconfont"
								href="javascript:history.go(-1)" title="Return to previous page"></a>
							<c:choose>
								<c:when test="${itemStatus == 'Success'}">
							Successful ${itemName}
						</c:when>
								<c:otherwise>
							${itemStatus} ${itemName}
						</c:otherwise>
							</c:choose>
						</div>
						<div class="block-inline vertical-top">
							<div class="input-group mb10 ml12" id="search-btn-group"
								style="width: 240px">
								<input class="form-control g-searchInput" type="text"
									placeholder="SN,Terminal Type">
								<div class="input-group-addon g-searchGo btn">Go</div>
							</div>
						</div>

						<button type="button" id="advance-btn"
							class="btn btn-primary g-icon-btn mb10 vertical-top ml12">
							<span class="glyphicon glyphicon-filter"></span> Advanced
						</button>
						<input type="hidden" id="tsn" value="${terminal.tsn}">
						<%-- 				<div class="report-export pull-left hide">
							<form id="exportTDownload"
								action="<%=contextPath%>/report/terminalDownload/export/${group.id}"
								method="post">
								<input type="hidden" name="startTime"> <input
									type="hidden" name="endTime"> <input type="hidden"
									name="timeType"> <input type="hidden" name="pkgType">
								<input type="hidden" name="terminalType"> <input
									type="hidden" name="downStatusType"> <input
									type="hidden" name="actiStatusType"> <input
									type="hidden" name="groupId" value="${group.id}"> <input
									type="hidden" name="tsn" value="${terminal.tsn}"> <input
									type="hidden" name="fuzzyCondition"> <input
									type="hidden" id="itemStatus"
									value='<c:out value="${itemStatus}" escapeXml="true" />' /> <input
									type="hidden" id="itemName"
									value='<c:out value="${itemName}" escapeXml="true" />' />
								<div>
									<button class="btn btn-primary" type="submit">Export</button>
								</div>
							</form>
						</div> --%>

					</div>
					<div class="advance-box hide">
						<div class="row">
							<div class="col-sm-10">
								<div class="row">
									<div class="col-sm-4">
										<div class="report-select">
											<select class="form-control" id="varioustime-select">
												<option value="Download Schedule">Download Schedule</option>
												<option value="Download Time">Download Time</option>
												<option value="Activation Schedule">Activation
													Schedule</option>
												<option value="Activation Time">Activation Time</option>
											</select>
										</div>

									</div>
									<div class="col-sm-4">
										<div class="report-datetime">
											<input type="text" class="form-control g-timeselect" readonly
												id="start-time" placeholder="Start Time"> <span
												class="glyphicon glyphicon-calendar datetime-icon start-datetime-icon"></span>
										</div>
									</div>
									<div class="col-sm-4">
										<div class="report-datetime">
											<input type="text" class="form-control g-timeselect" readonly
												id="end-time" placeholder="End Time"> <span
												class="glyphicon glyphicon-calendar datetime-icon"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-sm-4">
										<div class="report-select1">
											<select class="form-control" id="pkgType-select">
												<option value=" ">--Package Type--</option>
												<option value="form">Form</option>
												<option value="combo">Combo</option>
												<option value="application">Application</option>
												<option value="uninstall">Uninstall</option>
												<option value="whitelist">Whitelist</option>
												<option value="emv">Emv</option>
												<option value="configuration">Configuration</option>
												<option value="firmware">Firmware</option>
											</select>
										</div>
									</div>
										<div class="col-sm-4">
										<div class="report-select">
											<select class="form-control" id="terminalType-select">
												<option value="">--Terminal Type--</option>
												<option value="">All</option>
												<c:forEach items="${typeList}" var="item">
													<option value="${item.id}">
														<c:out value="${item.name}" />
													</option>
												</c:forEach>

											</select>
										</div>
									</div>
									<div class="col-sm-4">
										<div class="report-select1">
											<c:choose>
												<c:when test="${itemName == 'Downloads'}">
												<c:choose>
												<c:when test="${itemStatus == 'Success'}">
														<select class="form-control" id="activationStatus-select">
														<option value=" ">--Activation Status--</option>
														<option value=" ">All</option>
														<option value="Success">Successful</option>
														<option value="Failed">Failed</option>
														<option value="Pending">Pending</option>
														<option value="Canceled">Canceled</option>
														<option value="Not need activition">Not need
															activition</option>
													</select>
												</c:when>
												</c:choose>
												</c:when>
												<c:otherwise>
												<c:choose>
												<c:when test="${itemStatus == 'Pending'}">
														<select class="form-control" id="downloadStatus-select">
														<option value=" ">--Download Status--</option>
														<option value=" ">All</option>
														<option value="Success">Successful</option>
														<option value="Pending">Pending</option>
<!-- 														<option value="Failed">Failed</option>
														<option value="Canceled">Canceled</option>
														<option value="Expired">Expired</option> -->
													</select>
												</c:when>
												</c:choose>
												</c:otherwise>
											</c:choose>
										</div>
									</div>
								</div>
							</div>
							<div class="col-sm-2 text-right">

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
<!-- add-tree -->
<div class="modal fade" id="add-tree">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header g-modal-title">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">SELECT GROUP</h4>
			</div>
			<div class="modal-body">
				<div class="g-search">

					<div class="input-group">
						<input class="form-control g-searchInput" type="text"
							placeholder="Search Here">
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
<input type='hidden' id="permission-view" value="<shiro:hasPermission name='tms:terminal:view'>1</shiro:hasPermission>">
<script type="text/javascript">
	var groupId = ${group.id};
	seajs.use('dashboard_terminalDownload');
</script>
</body>
</html>