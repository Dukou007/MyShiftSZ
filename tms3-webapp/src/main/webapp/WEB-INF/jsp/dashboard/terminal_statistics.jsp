<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />
<div class="g-middle-content">
	<div class="container-fluid">
		<div class="row">
			<!-- 内容区域这里开始 -->
			<input id="tsn" value="${terminal.tsn}" hidden>
			<div class="col-sm-12 col-md-12">
				<div class="g-panel">
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
<script type="text/javascript">
	seajs.use('terminal_statistics');
</script>
</body>

</html>
