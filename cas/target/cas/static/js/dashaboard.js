define(function(require, exports, module) {
	require('chart');
	var TMS = require('TMS');
	var dash = {
		init: function() {
			var self = this;
			TMS.init();
			this.ajax();
			setInterval(function(){
				self.ajax();
			},3000);
		},
		ajax: function() {
			var self = this;
			TMS.getData({
					url: $("#contextPath").val() + '/dashboard/ajaxIndex',
					type: 'post'

				},
				function(data) {
					if (data != 'fail') {
						if (!data.statusCode || data.statusCode && data.statusCode == 200) {
							self.toDo(data);
						} else {
							TMS.alert(data.message, 'danger');
						}
					}

				});
		},
		toDo: function(data) {
			var item = data.result,
				date = data.date,
				self = this,
				temp = [],
				chartObj = {},
				errorColor ='#e75f47',
				successColor ='#43aea8',
				disabledColor ='#c1c5c8';
			$('.dashaboard-time').html('<span style="color: #333;">Last Update Time:</span> '+date);
			for (var i = 0; i < item.length; i++) {
				var alertCls = '';
				if(item[i].alertLevel == 1){
					alertCls = 'color:#fcb322;';
				}else if(item[i].alertLevel == 2){
					alertCls = 'color:#e75f47;';
				}
				temp.push('<div class="col-md-4 col-sm-6  dashaboard-chart">');
				temp.push('<div class="panel panel-default">');
				temp.push('<div class="panel-heading clearfix">');
				temp.push('<span class="dashaboard-title " style="padding-right:0;' + alertCls + '">');
				temp.push(item[i].name);
				temp.push('</span>');
				temp.push('</div>');
				temp.push('<div class="panel-body dashaboard-chart-panelbody">');
				temp.push('<span class="chart-main-percent" style="' + alertCls + '" >' + item[i].alertValue + '%</span>');
				temp.push('<div class="chart-holder" >');
				temp.push('<canvas class="chart-pannel" id="' + item[i].name + '" width="200" height="200"></canvas>');
				
				temp.push('</div>');

				temp.push('</div>');
				temp.push('</div>');
				temp.push('</div>');
				chartObj[item[i].name] = {
				    type: 'doughnut',
				    data: {
				    	labels: [
				    		item[i].greenTitle,
				    		item[i].greyTitle,
					        item[i].redTitle
					    ],
					    datasets: [
					        {
					            data: [item[i].greenCount, item[i].greyCount,item[i].redCount],
					            backgroundColor: [
					                successColor,
					                disabledColor,
					                errorColor
					            ],
					            hoverBackgroundColor:[
					            	successColor,
					                disabledColor,
					                errorColor
					            ]
					        }]
				    },
				    options:{
						legend: {
				            display: false
				        },
				        tooltips: {
				            callbacks:{
				            	label:function(tooltipItem,data){
				            		var datasetIndex = tooltipItem.datasetIndex,index = tooltipItem.index;
				            		console.log(tooltipItem,data)
				            		return data.labels[index]+':'+data.datasets[datasetIndex].data[index]
				            	}
				            }
				        }
				    }
				}
			}
			temp.push('<div class="col-md-12 col-sm-12">');
			temp.push('<a class=\"see-more pull-right\" href=\"'+$('#contextPath').val()+'/dashboard/dashboard_usage\">');
			temp.push('SEE MORE');
			temp.push('</a>');
			temp.push('</div>');		
			$('#dashaboard-panel').html(temp.join(''));
			$('.dashaboard-chart .chart-pannel').each(function(index, el) {
				var id = $(this).attr('id');
				var ctx = document.getElementById(id).getContext('2d');
				var myDoughnutChart = new Chart(ctx,chartObj[id]);
			});
		},
		bindEvent:function(){

		}
	}

	$(function() {
		dash.init();
	})

});