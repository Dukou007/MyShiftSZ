<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
	<div class="container-fluid">

		<div class="row">
			<!-- 内容区域这里开始 -->
			<div class="col-sm-12 col-md-12">
				<input id="tsn" value="${terminal.tsn}" hidden>
				<div class="g-panel">
					<div class="g-panel-title text-right clearfix"
						style="margin-bottom: 0px">
						<div class="g-panel-text pull-left mb10 text-left">Events
							List</div>
						<div class="text-left">
							<div class="dropdown mb10 block-inline vertical-top  pull-left"
								style="margin-right: 5px">
								<button class="btn dropdown-toggle"
									style="font-size: 13px; background-color: #fff; color: #4d5aa6;"
									type="button" data-toggle="dropdown">
									Last Week <span class="caret"></span>
								</button>
								<ul class="dropdown-menu" role="menu" id="timeTab">
									<li role="presentation" data-type="Last 1 Day"><a
										style="color: #4d5aa6;">Last 1 Day</a></li>
									<li role="presentation" class="" data-type="Last 3 Days">
										<a style="color: #4d5aa6;">Last 3 Days</a>
									</li>
									<li role="presentation" class="" data-type="Last Week"><a
										style="color: #4d5aa6;">Last Week</a></li>
									<li role="presentation" class="" data-type="Last Month"><a
										style="color: #4d5aa6;">Last Month</a></li>
									<li role="presentation" class="" data-type="All"><a
										style="color: #4d5aa6;">All</a></li>
								</ul>
							</div>
						</div>
						<div class="block-inline vertical-top">
							<div class="input-group mb10 ml12" id="search-btn-group"
								style="width: 240px">
								<input class="form-control g-searchInput" type="text"
									placeholder="Source,Message">
								<div class="input-group-addon g-searchGo btn">Go</div>
							</div>
						</div>

					</div>
					<div class="event-export hide" style="margin-bottom:15px;">
						<button id="exportExcel" class="btn btn-primary" type="button">Export</button>
					</div>
					<div class="g-panel-body">
						<table id="alert-table"></table>

					</div>
				</div>
			</div>
			<!-- 内容区域end-->
		</div>
	</div>

</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<script type="text/javascript">
	seajs.use('terminalEvents');
</script>
</body>
</html>
