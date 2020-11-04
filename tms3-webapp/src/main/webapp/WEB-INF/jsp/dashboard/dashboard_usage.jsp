<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

		<!-- 右侧内容 -->
 <div class="g-middle-content">
				<div class="container-fluid">
					<div class="row">
						<!-- 内容区域这里开始 -->
						<div class="col-sm-12 col-md-12">
							<ul class="g-title-tab clearfix">
								<li class=""><a href= "<%=contextPath%>/index/${group.id}">Real Time</a></li>
								<li class="active"><a href="<%=contextPath%>/dashboard/usage/${group.id}">Usage</a></li>
							</ul>
						</div>

						<!-- dashabord -->
						<div class="col-md-9 col-sm-12">
							<div class="g-panel">
								<div class="g-panel-title clearfix">
									<span class="g-panel-text">Status</span>
									<span class="g-panel-detail dashaboard-time">
	
									</span>
									<div class="dropdown g-pannel-dropdown">
										<button class="btn btn-primary " type="button" data-toggle="dropdown">
											Count
											<span class="caret"></span>
										</button>
										<ul class="dropdown-menu dropdown-menu-right dashboard-dropdown J-data-type" role="menu">
											
											<li role="presentation">
												<a role="menuitem" tabindex="-1" >Count</a>
											</li>

										</ul>
									</div>
									<div class="dropdown g-pannel-dropdown">
										<button class="btn btn-primary " type="button" data-toggle="dropdown">
											Bar
											<span class="caret"></span>
										</button>
										<ul class="dropdown-menu dropdown-menu-right dashboard-dropdown J-chart-type" role="menu">
											<li role="presentation">
												<a role="menuitem" tabindex="-1" href="#">Bar</a>
											</li>
											<li role="presentation">
												<a role="menuitem" tabindex="-1" href="#">Donut</a>
											</li>
										</ul>
									</div>
								</div>
								<div class="g-panel-body">
									<div class="row dashboard-row">
									</div>
								</div>
							</div>
						</div>

						<!-- alert -->
						<div class="col-md-3 col-sm-12 alert-box" >
							<div class="g-panel">
								<div class="g-panel-title clearfix">
									<span class="g-panel-text">Alert</span>
									<a href= "<%=contextPath%>/events/alertEvents/${group.id}" class="g-panel-more">See All</a>
								</div>
								<div class="g-panel-body">
									<ul class="alert-list">
									</ul>
								</div>
							</div>
						</div>

						<!-- Statistics -->
						<div class="col-md-9 col-sm-12 hide" id="statistics-panel">
							<div class="g-panel">
								<div class="g-panel-title clearfix">
									<span class="g-panel-text">Statistics</span>
									<a href= "<%=contextPath%>/dashboard/statistics/${group.id}" class="g-panel-more">See All</a>
								</div>
								<div class="g-panel-body">
									<table id='table' class="table"></table>


								</div>
							</div>
						</div>
						<!-- 内容区域end-->

					</div>
				</div>

				
			</div>

<jsp:directive.include file="../includes/bottom.jsp" />

<!-- FOOTER -->
<script type="text/javascript">
	seajs.use('dashboard-usage.js');
</script>
<!-- END -->
</body>
</html>