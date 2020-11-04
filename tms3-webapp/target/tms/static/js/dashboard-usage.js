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
	var TMS = require('TMS');
	require('chart');
	var dashboardType = "Count", // 默认百分比
	type = "Bar";
	var usage = {
		init : function() {
			var self = this;
			// TMS.init();
			self.initAlert();
			self.initDashboard();
			self.initEvent();
			self.bindEvent();

		},
		initDashboard : function() {
			var self = this;
			this.ajaxDashboard();
		},
		initAlert : function() {
			var self = this;
			this.ajaxAlert();
		},
		initEvent : function() {
			var self = this;
			this.eventTable();
		},
		ajaxAlert : function() {
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			var self = this;
		
			TMS.getData({
				url : _WEB_SITE_ + '/events/service/indexAlertEvents/'
						+ _ACTIVE_GROUP_,
				type : 'post'
			}, function(data) {
				if (data != 'fail') {
					if (!data.statusCode || data.statusCode
							&& data.statusCode == 200) {
						self.toDoAlert(data);
					} else {
						TMS.alert(data.message, 'danger');
					}
				}

			});
		},
		toDoAlert : function(data) {
			var items = data.result, temp = [];
			if (items.length == 0 || items == null) {
				$('.alert-list')
						.html(
								'<li><div class="alert text-center alert-info paxinfonext"><span class="paxspannext"><strong>Hint:</strong>There is no data!</span></div></li>');
				return false
			}
			for (var i = 0; i < items.length; i++) {
				var cls = '';
				if (items[i].alertSeverity == 2) {
					cls = 'alert-color';
				} else if (items[i].alertSeverity == 3) {
					cls = 'error-color';
				}
				temp.push('<li class="alert-item ' + cls + '">');
				temp.push('<p class="alert-name">' + items[i].groupName
						+ ':</p>');
				temp.push('<p class="alert-detail">' + items[i].alertMsg
						+ '</p>');
				temp.push('<p class="alert-date">'
						+ TMS.changeTime(items[i].alertTime) + '</p>');
				temp.push('</li>');
			}
			$('.alert-list').html(temp.join(''));
		},
		eventTable : function() {
			self = this;
			TMS.bootTable('#table', {
				url : _WEB_SITE_ + '/dashboard/service/indexStatistics/'
						+ _ACTIVE_GROUP_,
				columns : [ {
					field : 'terminalId',
					title : 'Terminal SN/TID',
					sortable : false,
					formatter : sourceOperate
				}, {
					field : 'usageName',
					title : 'Usage Name',
					sortable : false

				}, {
					field : 'period',
					title : 'Period',
					sortable : false,
					formatter : periodOperate
				}, {
					field : 'terminalModel',
					title : 'Terminal Type',
					sortable : false
				}, {
					field : 'error',
					title : 'Error/Normal Counts',
					sortable : false,
					formatter : countsOperate

				} ],

				responseHandler : function(res) { // 接口数据处理
					this.data = res.items;
					return res;
				}
			});

			function periodOperate(value, row, index) {
				return TMS.changeTime(row.startTime) + " - "
						+ TMS.changeTime(row.endTime);
			}

			function sourceOperate(value, row, index) {
				return row.terminalSN + " / " + row.terminalId;
			}

			function countsOperate(value, row, index) {
				var normalCount = row.totalCount - row.errorCount;
				return row.errorCount + " / " + normalCount;
			}
		},
		ajaxDashboard : function() {
			var self = this, url;
			if (type != "Bar") {
				url = _WEB_SITE_ + '/dashboard/usage/getData/' + _ACTIVE_GROUP_;
			} else {
				url = _WEB_SITE_ + '/dashboard/usage/ajaxDetail/'
						+ _ACTIVE_GROUP_;
			}
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			TMS.getData({
				url : url,
				type : 'post'
			}, function(data) {
				if (data != 'fail') {
					if (!data.statusCode || data.statusCode
							&& data.statusCode == 200) {
						self.dashboardData = data;
						self.toDo(data);
					} else {
						TMS.alert(data.message, 'danger');
					}
				}
				$('#statistics-panel').removeClass('hide');

			});
		},
		toDo : function(data) {
			if (!data) {
				return false;
			}
			var item = data.result, date = data.date, self = this, terminalNumber = data.terminalNumber, temp = [], chartObj = {}, errorColor = '#e75f47', successColor = '#43aea8', disabledColor = '#c1c5c8';
			if (type != "Bar") {
				for (var i = 0; i < item.length; i++) {
					var alertCls = '';
					if (item[i].alertLevel == 2) {
						alertCls = '#fcb322;';
					} else if (item[i].alertLevel == 3) {
						alertCls = '#e75f47;';
					}

					temp
							.push('<div class="col-sm-6 col-md-4 col-xs-12 dashboard-item" >');
					temp
							.push('<div class="dashboard-box clearfix" style="border-top: 1px solid '
									+ alertCls
									+ ';" id="'
									+ item[i].name
									+ '">');

					temp.push('<div class="dashboard-chart">');
					temp.push('<canvas width="100px" height="100px"></canvas>');
					temp.push('</div>');
					temp.push('<div class="dashboard-panel">');
					if (dashboardType == "Percentage") {
						var alertValue = item[i].redCount * 100
								/ (item[i].total);
						if (item[i].total == null || item[i].total == 0) {
							alertValue = '0<span>%</span>';
						} else {
							alertValue = alertValue.toFixed(0)
									+ '<span>%</span>';
						}
						var notStatistics = item[i].greyCount / (item[i].total)
								* 100;
						if (item[i].total == null || item[i].total == 0) {
							notStatistics = '0%';
						} else {
							notStatistics = notStatistics.toFixed(0) + '%';
						}
					} else {
						if (item[i].redCount == null
								|| item[i].greyCount == null) {
							alertValue = 0;
							notStatistics = 0;
						} else {
							alertValue = item[i].redCount;
							notStatistics = item[i].greyCount;
						}
					}
					temp.push('<p class="dashboard-bignumber" style="color:'
							+ alertCls + '">' + alertValue + '</p>');
					temp.push('<p class="dashboard-name" >' + item[i].name
							+ '</p>');
					temp.push('<p class="dashboard-smallnumber">'
							+ notStatistics + ' unavailable</p>');
					temp.push('</div>');
					temp.push('</div>');
					temp.push('</div>');
					chartObj[item[i].name] = {
						type : 'doughnut',
						data : {
							labels : [ item[i].greenTitle, item[i].greyTitle,
									item[i].redTitle ],
							datasets : [ {
								data : [ item[i].greenCount, item[i].greyCount,
										item[i].redCount ],
								backgroundColor : [ successColor,
										disabledColor, errorColor ],
								hoverBackgroundColor : [ successColor,
										disabledColor, errorColor ]
							} ]
						},
						options : {
							onClick : function(e, data) {
							},
							legend : {
								display : false
							},
							tooltips : {
								enabled : false,
								callbacks : {
									label : function(tooltipItem, data) {
										var datasetIndex = tooltipItem.datasetIndex, index = tooltipItem.index;
										return data.labels[index]
												+ ':'
												+ data.datasets[datasetIndex].data[index]
									}
								},
								custom : function(tooltip) {
									if (this._active[0]) {
										var datasetIndex = this._active[0]._datasetIndex, index = this._active[0]._index, v, data = this._chart.config.data.datasets[datasetIndex].data;
										var $partent = $(this._chart.canvas)
												.parents('.dashboard-chart');
										if (dashboardType == "Percentage") {
											var sum = 0;
											for (var i = 0; i < data.length; i++) {
												sum += data[i]
											}
											v = data[index] * 100 / sum;
											v = v.toFixed(0) + '%'
										} else {
											v = data[index]
										}
										var html = [
												'<span class="dashboard-tooltip '
														+ tooltip.yAlign
														+ '" style="left:'
														+ tooltip.x + 'px;top:'
														+ tooltip.y + 'px;">',
												this._chart.config.data.labels[index],
												":", v, "</span>" ]

										$('.dashboard-tooltip').remove();

										$partent.append(html.join(''));
									}

								}
							}
						}
					}
				}
				$('.dashboard-row').html(temp.join(''));
				$('.dashboard-box canvas').each(function(index, el) {
					var id = $(this).parents('.dashboard-box').attr('id');
					var ctx = el.getContext('2d');
					var myDoughnutChart = new Chart(ctx, chartObj[id]);
				});
			} else {
				for (var i = 0; i < item.length; i++) {
					var itemName = '', // 每项name
					dateArr = [], errorArr = [], successArr = [], disabledArr = [];
					var alertCls = '';

					for (var j = 0; j < item[i].length; j++) {
						itemData = item[i][j];
						itemName = itemData.name;
						if (j == 0) {
							var tempItem = [
									'<div class="col-sm-6 col-md-4 col-xs-12 dashboard-item">',
									'<div class="dashboard-box clearfix" style="border-top: 1px solid ;" id="'
											+ itemName + '">',
									'<div class="dashboard-bar-title" style="color:'
											+ alertCls + '">', itemName,
									'</div>', '<div class="dashboard-bar">',
									'<canvas></canvas>', '</div>', '</div>',
									'</div>', '</div>' ]
						}
						if (itemData.xvalue == null) {
							dateArr.push('');
						} else {
							dateArr.push(itemData.xvalue);
						}
						errorArr.push(itemData.redCount);
						successArr.push(itemData.greenCount);
						disabledArr.push(itemData.greyCount);
					}
					temp.push(tempItem.join(''));
					chartObj[itemName] = {
						type : 'bar',
						data : {
							labels : dateArr,
							datasets : [

							{
								label : "Abnormal",
								data : errorArr,
								backgroundColor : errorColor,
								borderColor : errorColor
							}, {
								label : "Normal",
								data : successArr,
								backgroundColor : successColor,
								borderColor : successColor
							}, {
								label : "Unavailable",
								data : disabledArr,
								backgroundColor : disabledColor,
								borderColor : disabledColor
							} ]

						},

						options : {

							legend : {
								display : false
							},
							scales : {
								gridLines : {
									display : false
								},
								xAxes : [ {

									stacked : true,
									categoryPercentage : 0.7
								} ],
								yAxes : [ {
									stacked : true,
									display : false
								} ]

							},
							tooltips : {
								enabled : true,
								callbacks : {
									label : function(tooltipItem, data) {
										var datasetIndex = tooltipItem.datasetIndex, index = tooltipItem.index, percent = '';
										return data.datasets[datasetIndex].label
												+ ':'
												+ data.datasets[datasetIndex].data[index]
									}
								}
							}

						}

					}
				}
				$('.dashboard-row').html(temp.join(''));
				$('.dashboard-box').each(
						function(index, el) {
							var id = $(el).attr('id');
							new Chart($(el).find('canvas')[0].getContext('2d'),
									chartObj[id]);
						});

			}

		},

		bindEvent : function() {
			var $body = $(".g-container"), self = this;
			$body.on('click', '.dashboard-reload', function() {
				self.ajaxDashboard();
			});
			$body.on('click', '.alert-reload', function() {
				self.ajaxAlert();
			});
			$body.on('click', '.J-data-type a', function() {
				var targetName = $(this).text();
				if (dashboardType == targetName) {
					return;
				} else {
					dashboardType = targetName;
					self.toDo(self.dashboardData);
				}
				$(this).parents(".dashboard-dropdown").siblings(
						'button[data-toggle="dropdown"]').html(
						dashboardType + ' <span class="caret"></span>');

			});
			$body
					.on(
							'click',
							'.J-chart-type a',
							function() {
								var targetName = $(this).text();
								if (type == targetName) {
									return;
								} else {
									type = targetName;
									self.ajaxDashboard();
									if (type == 'Bar') {
										$('.J-data-type')
												.html(
														'<li role="presentation"><a role="menuitem" tabindex="-1" >Count</a></li>');
										$('.J-data-type')
												.siblings(
														'button[data-toggle="dropdown"]')
												.html(
														'Count <span class="caret"></span>');
										dashboardType = "Count";
									} else {
										$('.J-data-type')
												.html(
														'<li role="presentation"><a role="menuitem" tabindex="-1" >Percentage</a></li><li role="presentation"><a role="menuitem" tabindex="-1" >Count</a></li>');
										$('.J-data-type')
												.siblings(
														'button[data-toggle="dropdown"]')
												.html(
														'Count <span class="caret"></span>');

									}
									$(this)
											.parents(".dashboard-dropdown")
											.siblings(
													'button[data-toggle="dropdown"]')
											.html(
													type
															+ ' <span class="caret"></span>');
								}

							});
			$body.on('mouseleave', '.dashboard-chart', function(event) {
				$('.dashboard-tooltip').remove();
			});
			$('.g-middle-content').scroll(function() {
				$('.dashboard-tooltip').remove();
			});

		}
	}
	$(function() {
		TMS.init();
		usage.init();

	})
});