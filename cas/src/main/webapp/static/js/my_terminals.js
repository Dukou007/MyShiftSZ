define(function(require, exports, module) {
	require('jquery-ui');
	require('validate-methods');
	var TMS = require('TMS');
	var myTerminals = {
		init: function() {
			TMS.init();
			this.$table = $('#table');
			this.table();
			this.bindEvent();
		},
		table: function() {
			var self = this;
			TMS.bootTable('#table', {
				url: _WEB_SITE_+'/terminal/service/ajaxList',
				columns: [{
					field: 'id',
					title: 'Terminal SN/ID'
				}, {
					field: 'status',
					title: 'Status',
					formatter: self.statusFormatter
				}, {
					field: 'type',
					title: 'Terminal Type'
				}, {
					field: 'deployment',
					title: 'Deployment'
				}, {
					formatter: self.operateFormatter,
					width: '30px'
				}],
				loadend: function() {

				},
				sidePagination: 'server',
				queryParams: function(params) { //接口参数处理
					return params
				},

				detailView: true,
				onExpandRow: function(index, row, $detail) {
					console.log(index, row, $detail)
					self.ajax(row.id,$detail)
				},
				responseHandler: function(res) { //接口数据处理
					this.data = res.items;
					res.total = res.totalCount;
					return res;
				},
				showRefresh: true,
				pagination: true,
				search: true,
				queryParamsType: '',
				searchOnEnterKey: true
			});
		},
		ajax: function(id,$detail) {
			TMS.getData({
					url: _WEB_SITE_+'/terminal/service/ajaxListDetail',
					type: 'get',
					data:{id:id}
				},
				function(data) {
					if (data.statusCode === undefined || data.statusCode == 200) {
						var data  = data.result;
						var html = [];
						var len = data.length;
						if(len >0){
							
							for(var i = 0;i<data.length;i++){
								html.push('<ul class="terminal-detail list-inline">');
								html.push('<li>');
								html.push('<i class="iconfont app-icon">&#xe612;</i> ');
								html.push('Name:<strong>'+data[i].name+'</strong>');
								html.push('</li>');
								html.push('<li>');
								html.push('Version:<strong>'+data[i].version+'</strong>');
								html.push('</li>');
								html.push('<li>');
								html.push('Parameter Timestamp:<strong>'+data[i].timestamp+'</strong>');
								html.push('</li>');
								html.push('</ul>');
							}
							
						}else{
							html.push('No data!')
						}
						$detail.html(html.join(''));
						
						
					} else {
						TMS.alert(data.message, 'danger');
						$detail.html(data.message);
					}
				});
		},
		ajaxMove:function(id,$target,modal){
			var group = $target.find('#move-group').val();
			TMS.getData({
					url: _WEB_SITE_+'/terminal/service/move',
					type: 'get',
					data: {
						id:id,
						group:group
					}
				},
				function(data) {
					if (data.statusCode === undefined || data.statusCode == 200) {
						TMS.alert(data.message, 'success');
						$(modal).modal('hide');
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
		},
		ajaxCopy:function(id,$target,modal){
			TMS.getData({
					url: _WEB_SITE_+'/static/js/terminals.json?id='+id,
					type: 'get',
					data: $target.serialize()
				},
				function(data) {
					if (data.statusCode === undefined || data.statusCode == 200) {
						TMS.alert(data.message, 'success');
						$(modal).modal('hide');
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
		},
		ajaxDel:function(id,modal){
			TMS.getData({
					url: _WEB_SITE_+'/static/js/terminals.json',
					type: 'get',
					data:{id:id}
				},
				function(data) {
					if (data.statusCode === undefined || data.statusCode == 200) {
						TMS.alert(data.message, 'success');
						$(modal).modal('hide');
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
		},
		ajaxDeactive:function(id,modal){
			TMS.getData({
					url: _WEB_SITE_+'/static/js/terminals.json',
					type: 'get',
					data:{id:id}
				},
				function(data) {
					if (data.statusCode === undefined || data.statusCode == 200) {
						TMS.alert(data.message, 'success');
						$(modal).modal('hide');
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
		},
		ajaxActive:function(id,modal){
			TMS.getData({
					url: _WEB_SITE_+'/static/js/terminals.json',
					type: 'get',
					data:{id:id}
				},
				function(data) {
					if (data.statusCode === undefined || data.statusCode == 200) {
						TMS.alert(data.message, 'success');
						$(modal).modal('hide');
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
		},
		ajaxGroupDel:function(id){
			TMS.getData({
					url: _WEB_SITE_+'/static/js/terminals.json',
					type: 'get',
					data:{id:id}
				},
				function(data) {
					if (data.statusCode === undefined || data.statusCode == 200) {
						TMS.alert(data.message, 'success');
						
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
		},
		statusFormatter: function(value, row, index) { //自定义status输出
			if (row.status) {
				return 'Active';
			} else {
				return 'Deactive';
			}
		},
		operateFormatter: function(value, row, index) { //自定义设置按钮输出
			var trigger = 'hover',
				content;
			if (TMS.isPC()) {
				trigger = 'focus';
			}
			if (!row.status) {
				content = '<ul class=\'nav popover-ul\'><li title=\'Move\' class=\'modal-move\'><a><i class=\'glyphicon glyphicon-share-alt\' ></i>&nbsp;&nbsp;Move</li><li title=\'Copy\' class=\'modal-copy\'><a><i class=\'glyphicon glyphicon-book\' ></i>&nbsp;&nbsp;Copy</li><li title=\'Delete\' class=\'modal-del\'><a><i class=\'glyphicon glyphicon-trash\' ></i>&nbsp;&nbsp;Delete</a></li><li title=\'Activate\' class=\'modal-active\'><a><i class=\'glyphicon glyphicon-heart\' ></i>&nbsp;&nbsp;Activate</li><li class=\'J-terminal-edit\' title=\'edit\'><a href=\'#\'><i class=\'glyphicon glyphicon-edit\' ></i>&nbsp;&nbsp;edit</a></li><li title=\'Deploy\'><a href=\'#\' class=\'J-terminal-deploy\'><i class=\'glyphicon glyphicon-wrench\' ></i>&nbsp;&nbsp;Deploy</a></li></ul>';
			} else {
				content = '<ul class=\'nav popover-ul\'><li title=\'Move\' class=\'modal-move\'><a><i class=\'glyphicon glyphicon-share-alt\' ></i>&nbsp;&nbsp;Move</li><li title=\'Copy\' class=\'modal-copy\'><a><i class=\'glyphicon glyphicon-book\' ></i>&nbsp;&nbsp;Copy</li><li title=\'Delete\' class=\'modal-del\'><a><i class=\'glyphicon glyphicon-trash\' ></i>&nbsp;&nbsp;Delete</a></li><li title=\'Deactivate\' class=\'modal-deactive\'><a><i class=\'glyphicon glyphicon-ban-circle\' ></i>&nbsp;&nbsp;Deactivate</li><li class=\'J-terminal-edit\' title=\'edit\'><a href=\'#\'><i class=\'glyphicon glyphicon-edit\' ></i>&nbsp;&nbsp;edit</a></li><li title=\'Deploy\'><a href=\'#\' class=\'J-terminal-deploy\'><i class=\'glyphicon glyphicon-wrench\' ></i>&nbsp;&nbsp;Deploy</a></li></ul>';
			}
			return [
				'<a class="set btn btn-primary btn-xs" data-id=' + row.id + ' href="javascript:void(0)"  data-html="true" data-content="' + content + '" data-trigger="' + trigger + '"  data-toggle="popover" data-placement="bottom" data-container=".body-content">',
				'operator',
				'</a>',

			].join('');
		},
		bindEvent: function() {
			var dataId,self = this;
			$('.body-content').on('click', '.set', function() {
				$(this).popover('toggle');
				dataId = $(this).attr('data-id');
			});
			$('.body-content').on('click', '.modal-move', function() {
				if ($(this).find('a').hasClass('disabled')) {
					return false;
				}
				$('#modal-terminal-move').modal();
			});
			$('.body-content').on('click', '.modal-copy', function() {
				if ($(this).find('a').hasClass('disabled')) {
					return false;
				}
				$('#modal-terminal-copy').modal();
			});
			$('.body-content').on('click', '.modal-del', function() {
				if ($(this).find('a').hasClass('disabled')) {
					return false;
				}
				$('#modal-terminal-del').modal();
			});
			$('.body-content').on('click', '.modal-del', function() {
				if ($(this).find('a').hasClass('disabled')) {
					return false;
				}
				$('#modal-terminal-del').modal();
			});
			$('.body-content').on('click', '.modal-deactive', function() {
				if ($(this).find('a').hasClass('disabled')) {
					return false;
				}
				$('#modal-terminal-deactive').modal();
			});
			$('.body-content').on('click', '.modal-active', function() {
				if ($(this).find('a').hasClass('disabled')) {
					return false;
				}
				$('#modal-terminal-active').modal();
			});
			$('.J-autocomplete').autocomplete({
				source:  _WEB_SITE_+'/group/service/getNameList',
				appendTo: "#modal-terminal-move"
			});
			/*$('.J-autocomplete').focus(function() {
				$(this).autocomplete( "search", "" );
				
			});*/
			$('.body-content').on('click', '.terminalSet-list li', function() {
				console.log(self.$table.bootstrapTable('getSelections'));
				var objArr = self.$table.bootstrapTable('getSelections'),idArr='';
				for(var i = 0;i<objArr.length;i++){
					idArr+= objArr[i].id + ',';

				}
				if(idArr){
					idArr = idArr.substring(0,idArr.length-1)
				}
				dataId = idArr;
				
			});
			$('.body-content').on('change', '.bootstrap-table input', function() {

				if ($(this).prop('checked')) {
					$('.terminalSet-list li a').removeClass('disabled');
				} else {
					if ($('.bootstrap-table input:checked').length == 0) {
						$('.terminalSet-list li a').addClass('disabled');
					}
				}
			});
			$('.body-content').on('click', '.J-select-terminals', function() {
				var isSelected = $(this).hasClass('terminals-selected');
				if (!isSelected) {
					$(this).addClass('terminals-selected').text('CANCEL');
					self.$table.bootstrapTable('refreshOptions', {
						columns: [{
							field: 'state',
							checkbox: true
						}, {
							field: 'id',
							title: 'Terminal SN/ID'
						}, {
							field: 'status',
							title: 'Status',
							formatter: self.statusFormatter
						}, {
							field: 'type',
							title: 'Terminal Type'
						}, {
							field: 'deployment',
							title: 'Deployment'
						}],
						detailView: false,
						showFooter: true
					});
					var temp = [
						'<ul class="list-inline terminalSet-list">',
						'<li title="Move" class="modal-move"><a class="disabled"><i class="glyphicon glyphicon-share-alt"></i>&nbsp;&nbsp;Move</a></li>',
						'<li title="Delete" class="modal-del"><a class="disabled"><i class="glyphicon glyphicon-trash"></i>&nbsp;&nbsp;Delete</a></li>',
						'<li title="Activate" class="modal-deactive"><a class="disabled"><i class="glyphicon glyphicon-heart"></i>&nbsp;&nbsp;Activate</a></li>',
						'<li title="Deactivate" class="modal-deactive"><a class="disabled"><i class="glyphicon glyphicon-ban-circle"></i>&nbsp;&nbsp;Deactivate</a></li>',
						'<li title="Deploy " class="J-terminal-deploy"><a class="disabled " href="#"><i class="glyphicon glyphicon-wrench"></i>&nbsp;&nbsp;Deploy</a></li>',
						'</ul>'
					];

					$('.fixed-table-footer').html(temp.join(''));
				} else {
					$(this).removeClass('terminals-selected').text('SELECT TERMINALS');
					self.$table.bootstrapTable('refreshOptions', {
						columns: [{
							field: 'id',
							title: 'Terminal SN/ID'
						}, {
							field: 'status',
							title: 'Status',
							formatter: self.statusFormatter
						}, {
							field: 'type',
							title: 'Terminal Type'
						}, {
							field: 'deployment',
							title: 'Deployment'
						}, {

							width: '30px',
							formatter: self.operateFormatter
						}],
						detailView: true,
						showFooter: false
					});
				}
			});
			/*$('#form-terminal-move').submit(function(event) {
				self.ajaxRemove(dataId,$(this),'#modal-terminal-move');
				return false
			});*/
			//表单验证
			$("#form-terminal-move").validate({
				submitHandler: function(form) //验证成功后执行函数
					{
						self.ajaxMove(dataId,$("#form-terminal-move"),'#modal-terminal-move');
					}
			});
			$("#form-terminal-copy").validate({
				submitHandler: function(form) //验证成功后执行函数
					{
						self.ajaxCopy(dataId,$("#form-terminal-copy"),'#modal-terminal-copy');
					}
			});
			$('.J-terminal-del').click(function() {
				self.ajaxDel(dataId,'#modal-terminal-del');
			});
			$('.J-terminal-deactive').click(function() {
				self.ajaxDeactive(dataId,'#modal-terminal-deactive');
			});
			$('.J-terminal-active').click(function() {
				self.ajaxActive(dataId,'#modal-terminal-active');
			});
			$('.body-content').on('click', '.J-terminal-edit', function(event) {
				window.location.href = _WEB_SITE_ + dataId;
			});
			$('.body-content').on('click', '.J-terminal-deploy', function(event) {
				window.location.href = _WEB_SITE_ + dataId;
			});
			$('.J-group-del').click(function() {//删除group
				self.ajaxGroupDel();
			})
			
			
		}
	}
	$(function() {
		myTerminals.init();
	})

});