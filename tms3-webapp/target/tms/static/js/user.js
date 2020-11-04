/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 2017-1-10 	         TMS_HZ           	Create/Add/Modify/Delete
 * ============================================================================		
 */
define(function(require, exports, module) {
	require('validate-methods');
	require('datetimepicker');
	require('fileupload');
	var TMS = require('TMS');
	$(function(){
		TMS.init();
		

		//pannel切换
		$('.toggle-button').click(function() {
			var target = $(this).attr('data-target');
			$(target).toggle();
			$(this).find('.toggle-icon').toggleClass('icondown');
			if($(this).parents('.panel').hasClass('panel-primary')){
				$(this).parents('.panel').removeClass('panel-primary').addClass('panel-default');
			}else{
				$(this).parents('.panel').removeClass('panel-default').addClass('panel-primary');
			}
		});
		//文件上传
		$('.file').change(function(e) {
			var target = $(this).attr('data-target');
			$(target).val(e.target.files[0].name)
		});

		//表单验证
		$("#form1").validate({     
			debug:true,
			 submitHandler: function(form) //验证成功后执行函数
			   {    
			   		
			   }  
		});
		//时间选择
		$(".form_datetime").datetimepicker({
			autoclose:true,
			minView:2,
			container:'.body-content'
		});
		//table
		TMS.bootTable('#table',{
				url:'/static/js/data1.json',
				columns: [{
			        field: 'user',
			        title: 'user',
			        sortable: true
			    }, {
			        field: 'status',
			        title: 'status',
			        sortable: true
			    },
			    {
			        field: 'role',
			        title: 'role',
			        sortable: true
			    }, 
			    {
			        field: 'email',
			        title: 'email',
			        sortable: true
			    }, 
			    {
			        field: 'directory',
			        title: 'directory',
			        sortable: true
			    },{
			        field: 'ss',
			        title: 'operation',
			        formatter: operateFormatter,
			    }],
				queryParamsType:'',
			    sidePagination:'server',
			    queryParams:function(params){//接口参数处理
			    	return params;
			    },
			    detailView:true,
			    detailFormatter:function(index, row){//详情
			    	var html = [];
			        $.each(row, function (key, value) {
			            html.push('<p><b>' + key + ':</b> ' + value + '</p>');
			        });
			        return html.join('');
			    },
			    responseHandler:function(res){//接口数据处理
			    	this.data=res.items;
			    	res.total = res.totalCount;
			    	return res;
			    },

			    sortName:'name',
			    sortOrder:'asc',
		    	pagination:true,
		    	search:true,
		    	searchOnEnterKey:true
		    });
		function operateFormatter(value, row, index) {
			var trigger = 'hover';
			if(TMS.isPC()){
				trigger = 'focus';
			}
	        return [
	            '<a class="set" data-id='+row.id+' href="javascript:void(0)"  data-html="true" data-trigger="'+trigger+'"  data-toggle="popover" data-placement="left" >',
	            '<i class="glyphicon glyphicon-cog"></i>',
	            '</a>',

	        ].join('');
	    }
	    $('#table').on('load-success.bs.table',function(){
	    	$('.set').popover({
	    		content:'<ul class="list-inline" style="margin-bottom:0px"><li><i class="glyphicon glyphicon-wrench" title="Delpoy"></i></li><li class="modal-del"><i class="glyphicon glyphicon-trash" ></i></li><li><i class="glyphicon glyphicon-share-alt"></i></li><li><i class="glyphicon glyphicon-plus"></i></li></ul>',
	    		container: '.body-content'
	    	}).on('shown.bs.popover', function () {
		  		var self = this;
			    $('.modal-del').unbind().bind('click',function(){
			    	$('#myModal').modal();
			    });
			});
	    });

	    $('#fileupload').fileupload({
	        dataType: 'json',
	        autoUpload: false,
	        recalculateProgress:false,
	        dropZone: $('#none'),
	        formData:function(form){
	        	return {};
	        },
	 		add: function(e, data) {
			    var maxFileSize = 999*1024;//允许的文件最大值999kb
			    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			    $.each(data.files, function (index, file) {
			    	var uploadErrors = [];
			    	/*if(file.size >maxFileSize){
			    		TMS.alert('Filesize is too big','danger');
			    		uploadErrors.push('Filesize is too big');
			    	}*/
			    	$('#file').val(file.name);
			    	if(uploadErrors.length==0){
				    	var ff = new FileReader();//check the drag and drop is a folder(firefox)
			            ff.readAsArrayBuffer(file);
			            ff.onload = function(){
			            	
			            	data.submit();
			            	
			            };
			          	ff.onerror = function(){
			          		TMS.alert('Package file is a folder','danger-upload');
			         	};
			    	}

			    });

			},
	        done: function (e, data) {//上传完成
	        },
	        fail:function (e, data) {//上传失败
	        	TMS.alert('fail','danger-upload');
	        	$('#file').val('');
	        },
	        progress:function (e, data) {
			},
	        progressall: function (e, data) {//进度条
	            var progress = parseInt(data.loaded / data.total * 100, 10);
	            $('.progress .progress-bar').css(
	                'width',
	                progress + '%'
	            );


	        },
	        start:function (e) {//开始时
	        	$('.progress').removeClass('hide');
	        },
	        always:function (e, data) {//默认上传结束执行
	        },
	        stop:function(){
	        	setTimeout(function(){
	        		$('.progress').addClass('hide');
	        		$('.progress .progress-bar').css(
	                'width','0%'
		            );
	        	},500)
	        	
	        }
	    }).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled');
	});
})