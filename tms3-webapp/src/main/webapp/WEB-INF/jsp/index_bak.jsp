<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="includes/top.jsp" />

<!-- 右侧内容 -->
<div class="tooltips-right content-page">
	<div class="body-content">
		<h1>
			Group <small>My Group</small>
		</h1>
		<div id="toolbar">
			<a id="remove" class="btn btn-primary"> <i
				class="glyphicon glyphicon-plus"></i> New
			</a>
			<div class='form-inline table-search-box '>
				<a id="searchbtn" class="btn btn-primary "> <i
					class="iconfont ft14">&#xe60a;</i>
				</a>
			</div>

		</div>
		<table id='table' class="table table-hover table-style"
			data-toolbar='#toolbar'>
		</table>
		<ul class="nav nav-pills nav-justified" role="tablist">
			<li role="presentation" class="active"><a href="#home"
				aria-controls="home" role="tab" data-toggle="tab">First Data
					Omaha</a></li>
			<li role="presentation"><a href="#profile"
				aria-controls="profile" role="tab" data-toggle="tab">Industry</a></li>
			<li role="presentation"><a href="#messages"
				aria-controls="messages" role="tab" data-toggle="tab">EDC</a></li>
			<li role="presentation"><a href="#settings"
				aria-controls="settings" role="tab" data-toggle="tab">Receipt</a></li>
		</ul>
		<div class="tab-content mt10">
			<div role="tabpanel" class="tab-pane active" id="home">
				<form class="" id='form1'>
					<div class="panel panel-primary">
						<div class="panel-heading toggle-button" data-target="#p1">
							<h3 class="panel-title">Host Features</h3>
							<span href="javascript:;" class="toggle-icon"> <i
								class="glyphicon glyphicon-chevron-up"></i>
							</span>
						</div>
						<div id='p1' class="toggle-button-target panel-body">
							<div class="container-fluid">
								<div class="row form-horizontal">
									<div class="col-sm-6">
										<div class="form-group">
											<label for="11" class="col-sm-4 control-label">input<span
												class='required-icon'>*</span></label>
											<div class="col-sm-8">
												<input type="text"
													class="form-control numberslettersonly required" name='11'
													id="11" placeholder="">
											</div>
										</div>
									</div>
									<div class="col-sm-6">
										<div class="form-group">
											<label for="22" class="col-sm-4 control-label ">input
												readonly</label>
											<div class="col-sm-8">
												<input type="text" class="form-control " id="22" name='22'
													placeholder="" readonly>
											</div>
										</div>
									</div>
									<div class="col-sm-6">
										<div class="form-group">
											<label for="33" class="col-sm-4 control-label">Port</label>
											<div class="col-sm-8">
												<input type="text" class="form-control portonly required"
													name='33' id="33" placeholder="">
											</div>
										</div>
									</div>
									<div class="col-sm-6">
										<div class="form-group">
											<label for="44" class="col-sm-4 control-label">keyIndex</label>
											<div class="col-sm-8">
												<input type="text" class="form-control keyindexonly"
													name='44' id="44" placeholder="">
											</div>
										</div>
									</div>
									<div class="col-sm-6">
										<div class="form-group">
											<label for="55" class="col-sm-4 control-label">persent</label>
											<div class="col-sm-8">
												<input type="text" class="form-control percentonly"
													name='55' id="55" placeholder="">
											</div>
										</div>
									</div>
									<div class="panel-body-line col-sm-12">
										<span>我是分割线</span>
									</div>
									<div class="col-sm-6">
										<div class="form-group">
											<label for="66" class="col-sm-4 control-label">ip</label>
											<div class="col-sm-8">
												<input type="text" class="form-control iponly" name='66'
													id="66" placeholder="">
											</div>
										</div>
									</div>
									<div class="col-sm-6">
										<div class="form-group">
											<label for="77" class="col-sm-4 control-label">float</label>
											<div class="col-sm-8">
												<input type="text" class="form-control amount" name='77'
													id="77" placeholder="">
											</div>
										</div>
									</div>
									<div class="col-sm-6">
										<div class="form-group">
											<label for="88" class="col-sm-4 control-label">select</label>
											<div class="col-sm-8">
												<select class="form-control" name='88' id='88'>
													<option>1</option>
													<option>2</option>
													<option>3</option>
												</select>
											</div>
										</div>
									</div>
									<div class="col-sm-6">
										<div class="form-group">
											<label for="file" class="col-sm-4 control-label">time</label>
											<div class="col-sm-8" style="position: relative">
												<input type="text" name='file'
													class="form-control form_datetime" id=""
													data-date-format="yyyy-mm-dd" readonly>
											</div>
										</div>
									</div>
									<!-- <div class="col-sm-12">
					        				<div class="form-group">
											    <label for="file" class="col-sm-2 control-label">file</label>
											    <div class="col-sm-4">
											      	<input type="text" class="form-control" name='file' id="file"  readonly>
											    </div>
											    <div class="col-sm-6" >
											    	<button class="btn btn-primary file-box">选择文件<input type='file' name='file' data-target='#file' class="file" /></button>
											    </div>
											 </div>
					        			</div> -->
									<div class="col-sm-6">
										<div class="form-group">
											<label for="file" class="col-sm-4 control-label">file</label>
											<div class="col-sm-8 ">
												<input type="text" class="form-control required" name='file'
													id="file" readonly>
												<div class="progress hide"
													style="margin-top: 10px; min-width: 2em;">
													<div class="progress-bar progress-bar-success"></div>
												</div>
											</div>

										</div>

									</div>
									<div class="col-sm-6">
										<div class="col-sm-2">
											<a class="btn btn-primary file-box">选择文件<input
												id="fileupload" type="file" name="files[]"
												data-url="/deploy/addPackage" multiple class='file' value=""></a>

										</div>
									</div>
									<div class="panel-body-line col-sm-12"></div>
									<div class="col-sm-12">
										<div class="form-group">
											<label for="121" class="col-sm-2 control-label">textarea</label>
											<div class=" col-sm-10">
												<textarea class="form-control" id='121' name='121' rows="3"></textarea>
											</div>

										</div>

									</div>

								</div>
							</div>

						</div>
					</div>
					<div class="container-fluid">
						<button type='button' id='sub' data-loading-text="提交中..."
							class="btn btn-primary">提交</button>
						<button type='reset' class="btn btn-default">取消</button>
					</div>
				</form>

			</div>
			<div role="tabpanel" class="tab-pane" id="profile">...</div>
			<div role="tabpanel" class="tab-pane" id="messages">...</div>
			<div role="tabpanel" class="tab-pane" id="settings">...</div>
		</div>
	</div>
</div>
<!-- 右侧内容end -->
<div class="modal" id="myModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span><span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">标签</h4>
			</div>
			<br>
			<div class="modal-body">
				<div class="tagsinput-primary">
					<input name="tag[name]" class="form-control" value=""> <input
						name="tag[testcase_id]" value="" type="hidden">
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<input type='submit'>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>

<jsp:directive.include file="includes/bottom.jsp" />

<script type="text/javascript">
	seajs.use('index');
</script>
<!-- END -->

</body>
</html>