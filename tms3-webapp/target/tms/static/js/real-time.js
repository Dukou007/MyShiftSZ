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
	require('gridly');
	var viewTermPer = $("#permission-view").val();
	var dashboardRefreshTime = 180000;
	var realTimeDashboardType, type, usageDashboardType;
	// 定义一个变量用于获取session中存储的realtime-dashboard-type的类型
	var sessionRealTimeType = sessionStorage.getItem('GROUP_REAL_TIME_TYPE');
	var sessionUsageType = sessionStorage.getItem('GROUP_USAGE_TYPE');
	var sessionUsageChartType = sessionStorage.getItem('GROUP_USAGE_CHART_TYPE');
	// 记录realtime percentage和count的当前状态
	if (sessionRealTimeType == 'Percentage') {
		realTimeDashboardType = 'Percentage';
	} else {
		realTimeDashboardType = 'Count';
	}

	// 记录usage percentage和count的当前状态
	//如果session没有值或者值为空，默认为Count
	if (sessionUsageType == 'Percentage') {
		usageDashboardType = 'Percentage';
	} else {
		usageDashboardType = 'Count';
	}
	// 记录usage chart的当前状态
	if (sessionUsageChartType == 'Donut') {
		type = 'Donut';
	} else {
		type = 'Bar';
	}

	// dashboard 历史记录
	var historyData = {};
	var realtimeArray = [];
	var usageArray = [];
	var allrealtimeArray = [];
	var allusageArray = [];
	var comment = {
		init : function() {
			var self = this;
			self.bindEvent();
		},
		updateShow : function(data, obj) {// add confirm是否显示发给接口 数据按接口需求发送
			var data = data.replace(/,/, "");
			TMS.getData({
				url : _WEB_SITE_ + '/dashboard/realAndUsage/updateData/'
						+ _USER_ID_,// 正确的接口地址和data参数
				type : 'post',
				data : {
					"data" : data,
					"type" : obj
				}
			}, function(data) {
				if (data != 'fail') {
					if (!data.statusCode || data.statusCode
							&& data.statusCode == 200) {
					} else {
						TMS.alert(data.message, 'danger');
					}
				}
			});

		},
		gridlyIt : function(obj) {
			var windowW = $(window).width();
			var columns = 2;
			var basewidth = 0;
			if (windowW <= 769) {
				columns = 1;
				basewidth = Math.floor($(".container-fluid").width() - 30);
			} else if (windowW >= 1200) {
				columns = 3;
				basewidth = Math
						.floor(($(".container-fluid").width() - 70) / 3);
			} else {
				columns = 2;
				basewidth = Math
						.floor(($(".container-fluid").width() - 50) / 2);
			}
			$(obj + ' .dashboard-item').css({
				"width" : basewidth + "px"
			})
			setTimeout(
					function() {
						var isold = true;
						$(obj)
								.gridly(
										{
											base : basewidth, // px
											gutter : 20, // px
											columns : columns,
											callbacks : {
												reordering : function(data) {
													var array = [];
													for (var i = 0; i < data.length; i++) {
														array
																.push(data
																		.eq(i)
																		.find(
																				".dashboard-name")
																		.html())
													}
													isold = array;
												},
												reordered : function(data) {
													var array = [];
													var sendData = '';
													for (var i = 0; i < data.length; i++) {
														var key = data
																.eq(i)
																.find(
																		".dashboard-name")
																.html();
														array.push(key)
														sendData = sendData
																+ "," + key
																+ ":" + "1";
													}
													if (JSON.stringify(isold) == JSON
															.stringify(array)) {
														return false;
													}
													if (obj == '.realTime-dashboard-row') {
														for (var j = 0; j < allrealtimeArray.length; j++) {
															if (array
																	.indexOf(allrealtimeArray[j]) == -1) {
																sendData = sendData
																		+ ","
																		+ allrealtimeArray[j]
																		+ ":"
																		+ "0";
																array
																		.push(allrealtimeArray[j])
															}
														}
														allrealtimeArray = array;
														comment.updateShow(
																sendData,
																'realTime')
													} else {
														for (var j = 0; j < allusageArray.length; j++) {
															if (array
																	.indexOf(allusageArray[j]) == -1) {
																sendData = sendData
																		+ ","
																		+ allusageArray[j]
																		+ ":"
																		+ "0";
																array
																		.push(allusageArray[j])
															}
														}
														allusageArray = array;
														comment.updateShow(
																sendData,
																'usage')
													}
												}
											}
										});
					}, 10)
		},
		bindEvent : function() {
			var $body = $(".g-container"), self = this;
			$(window).resize(function() {
				self.gridlyIt('.realTime-dashboard-row');
				// when groupid is 1,hide the usage
				if (_ACTIVE_GROUP_ != 1) {
					self.gridlyIt('.usage-dashboard-row');
				}

			});
			// 解决ipad初始进入页面时 grid定位错误
			setTimeout(function() {
				self.gridlyIt('.realTime-dashboard-row');
				// when groupid is 1,hide the usage
				if (_ACTIVE_GROUP_ != 1) {
					self.gridlyIt('.usage-dashboard-row');
				}

			}, 50)
			// real-time delete
			$("body")
					.on(
							'click',
							'.delete-realtime',
							function($event) {
								$(".g-middle-content").addClass("dragg")
								setTimeout(function() {
									$(".g-middle-content").removeClass("dragg")
								}, 400)
								$(this).parents('.realTime-dashboard-item')
										.remove();
								var sendArray = '';
								var array = [], realtimeArray = [];

								var Dobj = $(".realTime-dashboard-row .realTime-dashboard-item");

								for (var j = 0; j < Dobj.length; j++) {
									sendArray = sendArray
											+ ','
											+ Dobj.eq(j)
													.find(".dashboard-name")
													.html() + ':' + '1';
									array.push(Dobj.eq(j).find(
											".dashboard-name").html());
									realtimeArray.push(Dobj.eq(j).find(
											".dashboard-name").html());
								}

								for (var i = 0; i < allrealtimeArray.length; i++) {
									if (array.indexOf(allrealtimeArray[i]) == -1) {
										sendArray = sendArray + ','
												+ allrealtimeArray[i] + ':'
												+ '0';
										array.push(allrealtimeArray[i]);
									}
								}
								allrealtimeArray = array;
								self.gridlyIt('.realTime-dashboard-row');
								// when groupid is 1,hide the usage
								if (_ACTIVE_GROUP_ != 1) {
									self.gridlyIt('.usage-dashboard-row');
								}
								
								comment.updateShow(sendArray, 'realTime')
							});
			
			// usage delete
			$("body")
					.on(
							'click',
							'.delete-usage',
							function() {
								$(".g-middle-content").addClass("dragg")
								setTimeout(function() {
									$(".g-middle-content").removeClass("dragg")
								}, 400)
								$(this).parents('.dashboard-item').remove();
								var sendArray = '';
								var array = [], usageArray = [];
								var Dobj = $(".usage-dashboard-row .usage-dashboard-item");

								for (var j = 0; j < Dobj.length; j++) {
									sendArray = sendArray
											+ ','
											+ Dobj.eq(j)
													.find(".dashboard-name")
													.html() + ':' + '1';
									array.push(Dobj.eq(j).find(
											".dashboard-name").html());
									usageArray.push(Dobj.eq(j).find(
											".dashboard-name").html());
								}
								for (var i = 0; i < allusageArray.length; i++) {
									if (array.indexOf(allusageArray[i]) == -1) {
										sendArray = sendArray + ','
												+ allusageArray[i] + ':' + '0';
										array.push(allusageArray[i]);
									}
								}
								allusageArray = array;
								self.gridlyIt('.realTime-dashboard-row');
									// when groupid is 1,hide the usage
									if (_ACTIVE_GROUP_ != 1) {
									self.gridlyIt('.usage-dashboard-row');
								}
								comment.updateShow(sendArray, 'usage');
							});

			$("#real-time-panel")
					.on(
							'click',
							'.g-panel-button',
							function() {
								var array = [];
								var Dobj = $(".realTime-dashboard-row .realTime-dashboard-item");
								for (var j = 0; j < Dobj.length; j++) {
									array.push(Dobj.eq(j).find(
											".dashboard-name").html())
								}
								var weekli = [];
								for (var i = 0; i < allrealtimeArray.length; i++) {
									if (array.indexOf(allrealtimeArray[i]) == -1) {
										weekli
												.push('<li class="weekly-li" data-value="'
														+ allrealtimeArray[i]
														+ '">'
														+ allrealtimeArray[i]
														+ '</li>')
									} else {
										weekli
												.push('<li class="weekly-li weekly-deactive" data-value="'
														+ allrealtimeArray[i]
														+ '">'
														+ allrealtimeArray[i]
														+ '</li>')
									}
								}
								$('#add-dashboard-realtime-modal .weekly-ul')
										.html(weekli.join(''));
							});
			$("#usage-panel")
					.on(
							'click',
							'.g-panel-button',
							function() {
								var array = [];
								var Dobj = $(".usage-dashboard-row .usage-dashboard-item");
								for (var j = 0; j < Dobj.length; j++) {
									array.push(Dobj.eq(j).find(
											".dashboard-name").html())
								}
								var weekli = [];
								for (var i = 0; i < allusageArray.length; i++) {
									if (array.indexOf(allusageArray[i]) == -1) {
										weekli
												.push('<li class="weekly-li" data-value="'
														+ allusageArray[i]
														+ '">'
														+ allusageArray[i]
														+ '</li>')
									} else {
										weekli
												.push('<li class="weekly-li weekly-deactive" data-value="'
														+ allusageArray[i]
														+ '">'
														+ allusageArray[i]
														+ '</li>')
									}
								}
								$('#add-dashboard-usage-modal .weekly-ul')
										.html(weekli.join(''));
							});
			$("#add-dashboard-realtime-modal")
					.on(
							'click',
							'.J-confirm-btn',
							function() {
								var sendArray = '';
								var array = [];
								realtimeArray = [];
								var selectObj = $("#add-dashboard-realtime-modal .weekly-ul li");
								for (var i = 0; i < selectObj.length; i++) {
									if (selectObj.eq(i).hasClass(
											"weekly-active")
											|| selectObj.eq(i).hasClass(
													"weekly-deactive")) {
										realtimeArray.push(selectObj.eq(i)
												.attr("data-value"))
										sendArray = sendArray
												+ ','
												+ selectObj.eq(i).attr(
														"data-value") + ':'
												+ "1";
										array.push(selectObj.eq(i).attr(
												"data-value"))
									}
								}
								for (var i = 0; i < selectObj.length; i++) {
									if (!selectObj.eq(i).hasClass(
											"weekly-active")
											&& !selectObj.eq(i).hasClass(
													"weekly-deactive")) {
										sendArray = sendArray
												+ ','
												+ selectObj.eq(i).attr(
														"data-value") + ':'
												+ "0";
										array.push(selectObj.eq(i).attr(
												"data-value"))
									}
								}
								allrealtimeArray = array;
								realTime.ajaxDashboard(function() {
									$("#add-dashboard-realtime-modal").modal(
											'hide');
								})
								comment.updateShow(sendArray, 'realTime')
							});
			$("#add-dashboard-usage-modal")
					.on(
							'click',
							'.J-confirm-btn',
							function() {
								var sendArray = '';
								var array = [];
								usageArray = [];
								var selectObj = $("#add-dashboard-usage-modal .weekly-ul li");
								for (var i = 0; i < selectObj.length; i++) {
									if (selectObj.eq(i).hasClass(
											"weekly-active")
											|| selectObj.eq(i).hasClass(
													"weekly-deactive")) {
										usageArray.push(selectObj.eq(i).attr(
												"data-value"))
										sendArray = sendArray
												+ ','
												+ selectObj.eq(i).attr(
														"data-value") + ':'
												+ "1";
										array.push(selectObj.eq(i).attr(
												"data-value"))
									}
								}
								for (var i = 0; i < selectObj.length; i++) {
									if (!selectObj.eq(i).hasClass(
											"weekly-active")
											&& !selectObj.eq(i).hasClass(
													"weekly-deactive")) {
										sendArray = sendArray
												+ ','
												+ selectObj.eq(i).attr(
														"data-value") + ':'
												+ "0";
										array.push(selectObj.eq(i).attr(
												"data-value"))
									}
								}
								allusageArray = array;
								usage.ajaxDashboard()
								$("#add-dashboard-usage-modal").modal('hide');
								comment.updateShow(sendArray, 'usage')
							});
			$(".weekly-ul").on(
					'click',
					'li',
					function() {
						if (!$(this).hasClass("weekly-deactive")) {
							$(this).hasClass("weekly-active") ? $(this)
									.removeClass("weekly-active") : $(this)
									.addClass("weekly-active");
						}
					});

		}
	}

	var realTime = {
		init : function() {
			var self = this;
			self.initDropdownContent();
			self.initDashboard();
			self.bindEvent();
		},
		initDropdownContent : function() {
			var self = this, str = '', temp = []
			if (realTimeDashboardType == 'Percentage') {
				str = 'Percentage';
			} else {
				str = 'Count';
			}
			temp
					.push('<button class="btn btn-primary realtime-type-dropdownBtn" type="button" data-toggle="dropdown">'
							+ str);
			temp.push('<span class="caret"></span>');
			temp.push('</button>');
			temp
					.push('<ul class="dropdown-menu dropdown-menu-right dashboard-dropdown realTime-dashboard-dropdown" role="menu">');
			temp
					.push('<li role="presentation"><a role="menuitem" tabindex="-1" >Percentage</a></li');
			temp
					.push('<li role="presentation"><a role="menuitem" tabindex="-1" >Count</a></li>');
			temp.push('</ul>');
			$('.real-time-dropdown').html(temp.join(''));

		},
		initDashboard : function() {
			var self = this;
			if (_ACTIVE_GROUP_) {
				if (_ACTIVE_GROUP_ == 1) {
					this.ajaxDashboard(function() {
					}, true);
				} else {
					this.ajaxDashboard(function() {
						$('#usage-panel').show();
						usage.init();
					}, true);
				}
			}

			setInterval(function() {
				self.changeDashboard();
			}, dashboardRefreshTime);
		},
		ajaxDashboard : function(callback, first) {
			var self = this;
			var first = first ? true : false;
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			TMS.getData({
				url : _WEB_SITE_ + '/dashboard/real/getData/' + _ACTIVE_GROUP_
						+ '/' + _USER_ID_,
				type : 'post'
			}, function(data) {
				if (data != 'fail') {
					if (!data.statusCode || data.statusCode
							&& data.statusCode == 200) {
						self.dashboardData = data;

						self.toDo(data, first);
						callback && callback();

					} else {
						TMS.alert(data.message, 'danger');
					}
				}
			});
		},
		toDo : function(data, first) {
			if (!data) {
				return false
			}

			var first = first ? true : false;
			var item = data.result, date = data.date, self = this, terminalNumber = data.terminalNumber, temp = [], chartObj = {}, nodataColor = '#444a4e', errorColor = '#ff7068', successColor = '#59c402', disabledColor = '#8f97ab';
			historyData = {};
			$('.realTime-dashaboard-time').html(
					'(Last Update Time:  ' + TMS.changeTime(date)
							+ '  | Terminal Count:' + terminalNumber + ' )');
			for (var i = 0; i < item.length; i++) {
				historyData[item[i].name] = {};
				historyData[item[i].name].alertLevel = item[i].alertLevel;
				historyData[item[i].name].alertValue = item[i].alertValue;
				historyData[item[i].name].redCount = item[i].redCount;
				historyData[item[i].name].greenCount = item[i].greenCount;
				historyData[item[i].name].greyCount = item[i].greyCount;
				var isshow = item[i].isshow;// 判断显示item.isshow =
				// 1||0;需要后台输出的字段判断
				if (first) {
					if (isshow) {
						realtimeArray.push(item[i].name)
					}
					allrealtimeArray.push(item[i].name)
				}
				if (first && isshow || !first
						&& realtimeArray.indexOf(item[i].name) != -1) {

					var alertCls = '';
					var dataItem = [], hoverBackgroundColor = [], backgroundColor = [];
					if (item[i].alertLevel == 2) {
						alertCls = '#fcb322;';
					} else if (item[i].alertLevel == 3) {
						alertCls = '#ff7068;';
					}
					if (item[i].total == 0) {
						dataItem = [ 1, 0, 0 ];
						backgroundColor = [ nodataColor, nodataColor,
								nodataColor ];
						hoverBackgroundColor = [ nodataColor, nodataColor,
								nodataColor ];

					} else {
						dataItem = [ item[i].redCount, item[i].greenCount,
								item[i].greyCount ];
						backgroundColor = [ errorColor, successColor,
								disabledColor ];
						hoverBackgroundColor = [ errorColor, successColor,
								disabledColor ];
					}
					temp.push('<div class="col-sm-6 col-md-4 col-xs-12 realTime-dashboard-item dashboard-item" >');
					temp.push('<div class="realTime-dashboard-box dashboard-box clearfix" style="position:relative" id="'
								+ item[i].name + '">');

					if (TMS.isPC()) {
						temp.push('<span class="delete-dashboard-item delete-realtime real-usage-del" >×</span>');
					} else {
						temp.push('<span class="delete-dashboard-item delete-realtime" >×</span>');
					}
					temp.push('<div class="realTime-dashboard-panel dashboard-panel">');
					if (realTimeDashboardType == "Percentage") {
						// percentage
						var alertValue = item[i].alertValue;
						var redValue = (item[i].redCount / item[i].total * 100)
								.toFixed(3);
						var greenValue = (item[i].greenCount / item[i].total * 100)
								.toFixed(3);
						var greyValue = (item[i].greyCount / item[i].total * 100)
								.toFixed(3);
						redValue = redValue.substring(0,
								redValue.indexOf('.') + 2);
						greenValue = greenValue.substring(0, greenValue
								.indexOf('.') + 2);
						greyValue = greyValue.substring(0, greyValue
								.indexOf('.') + 2);

						if ((item[i].total - item[i].greyCount) == 0) {
							redValue = '0.0%';
							greenValue = '0.0%';
							greyValue = '100.0%';
							alertValue = '0.0<span>%</span>';

						} else {
							redValue = redValue + '%';
							greenValue = greenValue + '%';
							greyValue = greyValue + '%';
							alertValue = alertValue + '<span>%</span>';
						}
						var notStatistics = (item[i].greyCount / (item[i].total) * 100).toFixed(1);
						if (item[i].total == 0) {
							greyValue = '0.0%';
							notStatistics = '0.0%';
						} else {

							notStatistics = notStatistics + '%';
						}
					} else {
						// count
						if (item[i].redCount == null
								|| item[i].greyCount == null) {
							redValue = 0;
							greenValue = 0;
							greyValue = 0;
							alertValue = 0;
							notStatistics = 0;
						} else {
							redValue = item[i].redCount;
							greenValue = item[i].greenCount;
							greyValue = item[i].greyCount;
							alertValue = item[i].redCount;
							notStatistics = item[i].greyCount;
						}
					}


					var hasPermission=$('#permission-delete').val();
					temp.push('<p class="dashboard-name" style="color:'+alertCls+'">' + item[i].name
							+ '</p>');
					// 取消activations和downloads的下钻功能
					if (item[i].name == 'Activations'
							|| item[i].name == 'Downloads' ) {
						temp
						.push('<p class="dashboard-smallnumber errorC"><a class="dashboard-smallnumber-title" href="'
								+ _WEB_SITE_
								+ "/dashboard/terminalDownload/"
								+ item[i].name
								+ '/Failed'
								+ "/"
								+ _ACTIVE_GROUP_
								+ '">'
								+ item[i].redTitle
								+ ':</a><span class="dashboard-num">'
								+ redValue + '</span></p>');
						temp
						.push('<p class="dashboard-smallnumber successC"><a class="dashboard-smallnumber-title" href="'
								+ _WEB_SITE_
								+ "/dashboard/terminalDownload/"
								+ item[i].name
								+ '/Success'
								+ "/"
								+ _ACTIVE_GROUP_
								+ '">'
								+ item[i].greenTitle
								+ ':</a><span class="dashboard-num">'
								+ greenValue + '</span></p>');
						temp
						.push('<p class="dashboard-smallnumber disabledC"><a class="dashboard-smallnumber-title" href="'
								+ _WEB_SITE_
								+ "/dashboard/terminalDownload/"
								+ item[i].name
								+ '/Pending'
								+ "/"
								+ _ACTIVE_GROUP_
								+ '">'
								+ item[i].greyTitle
								+ ':</a><span class="dashboard-num">'
								+ greyValue + '</span></p>');
				/*		if(hasPermission==1	){
							temp
							.push('<p class="dashboard-smallnumber errorC"><a class="dashboard-smallnumber-title" href="'
									+ _WEB_SITE_
									+ "/dashboard/terminalDownload/"
									+ item[i].name
									+ '/Failed'
									+ "/"
									+ _ACTIVE_GROUP_
									+ '">'
									+ item[i].redTitle
									+ ':</a><span class="dashboard-num">'
									+ redValue + '</span></p>');
							temp
							.push('<p class="dashboard-smallnumber successC"><a class="dashboard-smallnumber-title" href="'
									+ _WEB_SITE_
									+ "/dashboard/terminalDownload/"
									+ item[i].name
									+ '/Success'
									+ "/"
									+ _ACTIVE_GROUP_
									+ '">'
									+ item[i].greenTitle
									+ ':</a><span class="dashboard-num">'
									+ greenValue + '</span></p>');
							temp
							.push('<p class="dashboard-smallnumber disabledC"><a class="dashboard-smallnumber-title" href="'
									+ _WEB_SITE_
									+ "/dashboard/terminalDownload/"
									+ item[i].name
									+ '/Pending'
									+ "/"
									+ _ACTIVE_GROUP_
									+ '">'
									+ item[i].greyTitle
									+ ':</a><span class="dashboard-num">'
									+ greyValue + '</span></p>');
						}else{
							temp
							.push('<p class="dashboard-smallnumber errorC"><span class="dashboard-smallnumber-title" >'
									+ item[i].redTitle
									+ ':</span><span class="dashboard-num">'
									+ redValue + '</span></p>');
					temp
							.push('<p class="dashboard-smallnumber successC"><span class="dashboard-smallnumber-title" >'
									+ item[i].greenTitle
									+ ':</span><span class="dashboard-num">'
									+ greenValue + '</span></p>');
					temp
							.push('<p class="dashboard-smallnumber disabledC"><span class="dashboard-smallnumber-title">'
									+ item[i].greyTitle
									+ ':</span><span class="dashboard-num">'
									+ greyValue + '</span></p>');
						}*/
						
					} else{
						temp
								.push('<p class="dashboard-smallnumber errorC"><a class="dashboard-smallnumber-title" href="'
										+ _WEB_SITE_
										+ "/dashboard/oneStatusList/"
										+ item[i].name
										+ '/1,'
										+ item[i].redTitle
										+ "/"
										+ _ACTIVE_GROUP_
										+ '">'
										+ item[i].redTitle
										+ ':</a><span class="dashboard-num">'
										+ redValue + '</span></p>');
						temp
								.push('<p class="dashboard-smallnumber successC"><a class="dashboard-smallnumber-title" href="'
										+ _WEB_SITE_
										+ "/dashboard/oneStatusList/"
										+ item[i].name
										+ '/2,'
										+ item[i].greenTitle
										+ "/"
										+ _ACTIVE_GROUP_
										+ '">'
										+ item[i].greenTitle
										+ ':</a><span class="dashboard-num">'
										+ greenValue + '</span></p>');
						if (item[i].name != 'Offline') {
							temp
									.push('<p class="dashboard-smallnumber disabledC"><a class="dashboard-smallnumber-title" href="'
											+ _WEB_SITE_
											+ "/dashboard/oneStatusList/"
											+ item[i].name
											+ '/3,'
											+ item[i].greyTitle
											+ "/"
											+ _ACTIVE_GROUP_
											+ '">'
											+ item[i].greyTitle
											+ ':</a><span class="dashboard-num">'
											+ greyValue + '</span></p>');
						} else {
							temp
									.push('<p class="dashboard-smallnumber disabledC" style="opacity:0"><a class="dashboard-smallnumber-title" href="">'
											+ item.greyTitle
											+ '</a><span class="dashboard-num"></span></p>');
						}

					}

					temp.push('</div>');
					temp
							.push('<div class="realTime-dashboard-chart dashboard-chart">');
					temp.push('<span class="dashboard-num2" style="color:'
							+ alertCls + '">' + alertValue + '');
					temp.push('</span>');
					temp.push('<canvas width="100px" height="100px">');
					temp.push('</canvas>');
					temp.push('</div>');
					temp.push('</div>');
					temp.push('</div>');

					chartObj[item[i].name] = {
						type : 'doughnut',
						data : {
							labels : [ item[i].redTitle, item[i].greenTitle,
									item[i].greyTitle ],
							datasets : [ {
								data : dataItem,
								backgroundColor : backgroundColor,
								hoverBackgroundColor : hoverBackgroundColor,
							} ]
						},
						options : {
							onClick : function(e, data) {
							},
							cutoutPercentage : 80,
							legend : {
								display : false
							},

							tooltips : {
								enabled : false,

							}
						}
					}
				}
			}
			$('.realTime-dashboard-row').html(temp.join(''));

			$('.realTime-dashboard-chart canvas').each(function(index, el) {

				var id = $(this).parents('.realTime-dashboard-box').attr('id');
				var ctx = el.getContext('2d');
				var myDoughnutChart = new Chart(ctx, chartObj[id]);
			});
			comment.gridlyIt('.realTime-dashboard-row');
		},
		changeDashboard : function() {
			var self = this;
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			TMS
					.getData(
							{
								url : _WEB_SITE_ + '/dashboard/real/getData/'
										+ _ACTIVE_GROUP_ + '/' + _USER_ID_,
								type : 'post',
								loading : false
							},
							function(data) {
								if (data != 'fail') {
									if (!data.statusCode || data.statusCode
											&& data.statusCode == 200) {
										self.dashboardData = data;
										var terminalNumber = data.terminalNumber;
										var date = data.date;
										var data = data.result;

										$('.realTime-dashaboard-time')
												.html(
														'(Last Update Time:  '
																+ TMS
																		.changeTime(date)
																+ '  | Terminal Count:'
																+ terminalNumber
																+ ' )');

										for (var i = 0; i < data.length; i++) {
											if (!(historyData[data[i].name].alertLevel == data[i].alertLevel
													&& historyData[data[i].name].alertValue == data[i].alertValue
													&& historyData[data[i].name].redCount == data[i].redCount
													&& historyData[data[i].name].greenCount == data[i].greenCount && historyData[data[i].name].greyCount == data[i].greyCount)) {
												historyData[data[i].name].alertLevel = data[i].alertLevel;
												historyData[data[i].name].alertValue = data[i].alertValue;
												historyData[data[i].name].redCount = data[i].redCount;
												historyData[data[i].name].greenCount = data[i].greenCount;
												historyData[data[i].name].greyCount = data[i].greyCount;
												self.changeToDo(data[i], self
														.getobj(data[i].name));

											}
										}

									} else {
										TMS.alert(data.message, 'danger');
									}
								}
							});
		},
		getobj : function(obj) {
			var O = {};
			$(".realTime-dashboard-item").each(function(i, o) {
				if ($(o).find(".dashboard-name").html() == obj) {
					O = $(o);
				}
			})
			return O;
		},
		// repaint the changed chart
		changeToDo : function(data, repaintChart) {

			if (!data) {
				return false
			}
			var item = data, self = this, chartObj = {}, dataItem = [], backgroundColor = [], hoverBackgroundColor = [], nodataColor = '#444a4e', errorColor = '#ff7068', successColor = '#59c402', disabledColor = '#8f97ab';
			var alertCls = '', temp = [];
			if (item.alertLevel == 2) {
				alertCls = '#fcb322;';
			} else if (item.alertLevel == 3) {
				alertCls = '#ff7068;';
			}
			if (item.total == 0) {
				dataItem = [ 1, 0, 0 ];
				backgroundColor = [ nodataColor, nodataColor, nodataColor ];
				hoverBackgroundColor = [ nodataColor, nodataColor, nodataColor ];

			} else {
				dataItem = [ item.redCount, item.greenCount, item.greyCount ];
				backgroundColor = [ errorColor, successColor, disabledColor ];
				hoverBackgroundColor = [ errorColor, successColor,
						disabledColor ];

			}
			temp
					.push('<div class="realTime-dashboard-box dashboard-box clearfix" style="position:relative" id="'
							+ item.name + '">');
			if (TMS.isPC()) {
				temp
						.push('<span class="delete-dashboard-item delete-realtime real-usage-del" >×</span>');
			} else {
				temp.push('<span class="delete-dashboard-item delete-realtime" >×</span>');
			}
			temp.push('<div class="realTime-dashboard-panel dashboard-panel">');
			if (realTimeDashboardType == "Percentage") {
				// percentage
				var alertValue = item.alertValue;
				var redValue = (item.redCount / item.total * 100).toFixed(3);
				var greenValue = (item.greenCount / item.total * 100)
						.toFixed(3);
				var greyValue = (item.greyCount / item.total * 100).toFixed(3);
				redValue = redValue.substring(0, redValue.indexOf('.') + 2);
				greenValue = greenValue.substring(0,
						greenValue.indexOf('.') + 2);
				greyValue = greyValue.substring(0, greyValue.indexOf('.') + 2);

				if ((item.total - item.greyCount) == 0) {
					redValue = '0.0%';
					greenValue = '0.0%';
					greyValue = '100.0%';
					alertValue = '0.0<span>%</span>';

				} else {
					redValue = redValue + '%';
					greenValue = greenValue + '%';
					greyValue = greyValue + '%';
					alertValue = alertValue + '<span>%</span>';
				}
				var notStatistics = item.greyCount / (item.total) * 100;
				if (item.total == 0) {
					greyValue = '0.0%';
					notStatistics = '0.0%';
				} else {

					notStatistics = notStatistics.toFixed(1) + '%';
				}
			} else {
				// count
				if (item.redCount == null || item.greyCount == null) {
					redValue = 0;
					greenValue = 0;
					greyValue = 0;
					alertValue = 0;
					notStatistics = 0;
				} else {
					redValue = item.redCount;
					greenValue = item.greenCount;
					greyValue = item.greyCount;
					alertValue = item.redCount;
					notStatistics = item.greyCount;
				}
			}

			temp.push('<p class="dashboard-name" >' + item.name + '</p>');
			// 取消activations和downloads的下钻功能
			if (item.name == 'Activations' || item.name == 'Downloads') {
				temp
						.push('<p class="dashboard-smallnumber errorC"><a class="dashboard-smallnumber-title" href="'
								+ _WEB_SITE_
								+ "/dashboard/terminalDownload/"
								+ item.name
								+ '/Failed'
								+ "/"
								+ _ACTIVE_GROUP_
								+ '">'
								+ item.redTitle
								+ ':</a><span class="dashboard-num">'
								+ redValue + '</span></p>');
				temp
						.push('<p class="dashboard-smallnumber successC"><a class="dashboard-smallnumber-title" href="'
								+ _WEB_SITE_
								+ "/dashboard/terminalDownload/"
								+ item.name
								+ '/Success'
								+ "/"
								+ _ACTIVE_GROUP_
								+ '">'
								+ item.greenTitle
								+ ':</a><span class="dashboard-num">'
								+ greenValue + '</span></p>');
				temp
						.push('<p class="dashboard-smallnumber disabledC"><a class="dashboard-smallnumber-title" href="'
								+ _WEB_SITE_
								+ "/dashboard/terminalDownload/"
								+ item.name
								+ '/Pending'
								+ "/"
								+ _ACTIVE_GROUP_
								+ '">'
								+ item.greyTitle
								+ ':</a><span class="dashboard-num">'
								+ greyValue + '</span></p>');
			} else {
				temp
						.push('<p class="dashboard-smallnumber errorC"><a class="dashboard-smallnumber-title" href="'
								+ _WEB_SITE_
								+ "/dashboard/oneStatusList/"
								+ item.name
								+ '/1,'
								+ item.redTitle
								+ "/"
								+ _ACTIVE_GROUP_
								+ '">'
								+ item.redTitle
								+ ':</a><span class="dashboard-num">'
								+ redValue + '</span></p>');
				temp
						.push('<p class="dashboard-smallnumber successC"><a class="dashboard-smallnumber-title" href="'
								+ _WEB_SITE_
								+ "/dashboard/oneStatusList/"
								+ item.name
								+ '/2,'
								+ item.greenTitle
								+ "/"
								+ _ACTIVE_GROUP_
								+ '">'
								+ item.greenTitle
								+ ':</a><span class="dashboard-num">'
								+ greenValue + '</span></p>');
				if (item.name != 'Offline') {
					temp
							.push('<p class="dashboard-smallnumber disabledC"><a class="dashboard-smallnumber-title" href="'
									+ _WEB_SITE_
									+ "/dashboard/oneStatusList/"
									+ item.name
									+ '/3,'
									+ item.greyTitle
									+ "/"
									+ _ACTIVE_GROUP_
									+ '">'
									+ item.greyTitle
									+ ':</a><span class="dashboard-num">'
									+ greyValue + '</span></p>');
				} else {
					temp
							.push('<p class="dashboard-smallnumber disabledC" style="opacity:0"><a class="dashboard-smallnumber-title" href="">'
									+ item.greyTitle
									+ '</a><span class="dashboard-num"></span></p>');
				}
			}

			temp.push('</div>');
			temp.push('<div class="realTime-dashboard-chart dashboard-chart">');
			temp.push('<span class="dashboard-num2" style="color:' + alertCls
					+ '">' + alertValue + '');
			temp.push('</span>');
			temp.push('<canvas width="100px" height="100px">');
			temp.push('</canvas>');
			temp.push('</div>');
			temp.push('</div>');
			chartObj[item.name] = {
				type : 'doughnut',
				data : {
					labels : [ item.redTitle, item.greenTitle, item.greyTitle ],
					datasets : [ {
						data : dataItem,
						backgroundColor : backgroundColor,
						hoverBackgroundColor : hoverBackgroundColor,

					} ]
				},
				options : {
					onClick : function(e, data) {
					},
					cutoutPercentage : 80,
					legend : {
						display : false
					},

					tooltips : {
						enabled : false,
					}
				}
			}
			$(repaintChart).html(temp.join(''));
			var id = $(repaintChart).children('.realTime-dashboard-box').attr(
					'id');
			var ctx = ($(repaintChart).find('.realTime-dashboard-chart canvas'))[0]
					.getContext('2d');
			var myDoughnutChart = new Chart(ctx, chartObj[id]);
		},
		bindEvent : function() {
			var $body = $(".g-container"), self = this;
			// mouseenter and mouseleave
			if (TMS.isPC()) {
				$body.on('mouseenter', '.realTime-dashboard-item', function() {
					$(this).find('.delete-dashboard-item').show(300);
				})
				$body.on('mouseleave', '.realTime-dashboard-item', function() {
					$(this).find('.delete-dashboard-item').hide(300);
				})
			}
			$body.on('click', '.realTime-dashboard-reload', function() {
				self.ajaxDashboard();
			});
			$body
					.on(
							'click',
							'.realTime-dashboard-dropdown a',
							function() {
								var targetName = $(this).text();
								realtimeArray = [];
								// 取页面上显示的面板
								if (realTimeDashboardType == targetName) {
									return;
								} else {
									realTimeDashboardType = targetName;
									var showObj = $(".realTime-dashboard-row .realTime-dashboard-item");
									for (var j = 0; j < showObj.length; j++) {
										realtimeArray.push(showObj.eq(j).find(
												".dashboard-name").html());
									}
									self.ajaxDashboard(function() {
									}, false);

								}
								// 将改变后的realtime-dashboard-type存入session中
								sessionStorage.setItem('GROUP_REAL_TIME_TYPE',
										targetName);

								$(this)
										.parents(".realTime-dashboard-dropdown")
										.siblings(
												'button[data-toggle="dropdown"]')
										.html(
												realTimeDashboardType
														+ ' <span class="caret"></span>');

							});
			$body.on('mouseleave', '.realTime-dashboard-chart',
					function(event) {
						$('.dashboard-tooltip').remove();
					});
			$('.g-middle-content').scroll(function() {
				$('.dashboard-tooltip').remove();
			});

		}
	};

	var usage = {
		init : function() {
			var self = this;
			self.initDropdownContent();
			self.initDashboard();
			self.bindEvent();
		},
		initDropdownContent : function() {
			var self = this, str = '', temp = [];
			if (usageDashboardType == 'Percentage') {
				str = 'Percentage';
			} else {
				str = 'Count';
			}
			temp
					.push('<button class="btn btn-primary " type="button" data-toggle="dropdown">'
							+ str);
			temp.push('<span class="caret"></span>');
			temp.push('</button>');
			temp
					.push('<ul class="dropdown-menu dropdown-menu-right usage-dashboard-dropdown dashboard-dropdown J-data-type" role="menu">');
			temp
					.push('<li role="presentation"><a role="menuitem" tabindex="-1" >Count</a></li>');
			temp
					.push('<li role="presentation"><a role="menuitem" tabindex="-1" >Percentage</a></li>');
			temp.push('</ul>');
			$('.usage-type').html(temp.join(''));

			str = '';
			temp = [];
			if (type == 'Bar') {
				str = 'Bar';
			} else {
				str = 'Donut';
			}
			temp
					.push('<button class="btn btn-primary " type="button" data-toggle="dropdown">'
							+ str);
			temp.push('<span class="caret"></span>');
			temp.push('</button>');
			temp
					.push('<ul class="dropdown-menu dropdown-menu-right usage-dashboard-dropdown dashboard-dropdown J-chart-type" role="menu">');
			temp
					.push('<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Bar</a></li>');
			temp
					.push('<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Donut</a></li>');
			temp.push('</ul>');
			$('.usage-chart-type').html(temp.join(''));

		},
		initDashboard : function() {
			var self = this;
			this.ajaxDashboard(true);
		},
		ajaxDashboard : function(first) {
			var self = this, url;
			var first = first ? true : false;
			if (type != "Bar") {
				url = _WEB_SITE_ + '/dashboard/usage/getData/' + _ACTIVE_GROUP_
						+ '/' + _USER_ID_;
			} else {
				url = _WEB_SITE_ + '/dashboard/usage/ajaxDetail/'
						+ _ACTIVE_GROUP_ + '/' + _USER_ID_;
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
						self.toDo(data, first);
					} else {
						TMS.alert(data.message, 'danger');
					}
				}
				$('#statistics-panel').removeClass('hide');
				$('.usage-dashaboard-time').html(
						'(Calculate By ' + data.tzName + ' Time)');
			});
		},
		toDo : function(data, first) {
			if (!data) {
				return false;
			}
			var item = data.result, date = data.date, self = this, terminalNumber = data.terminalNumber, temp = [], chartObj = {}, nodataColor = '#444a4e', errorColor = '#ff7068', successColor = '#59c402', disabledColor = '#8f97ab';

			if (type != "Bar") {
				// Donut
				for (var i = 0; i < item.length; i++) {
					var isshow = item[i].isshow;// 判断显示item.isshow =
					// 1||0;需要后台输出的字段判断
					if (first) {
						if (isshow) {
							usageArray.push(item[i].name)
						}
						allusageArray.push(item[i].name)
					}
					if (first && isshow || !first
							&& usageArray.indexOf(item[i].name) != -1) {
						var alertCls = '';
						var dataItem = [], hoverBackgroundColor = [], backgroundColor = [];

						if (item[i].total == 0) {
							dataItem = [ 1, 0, 0 ];
							backgroundColor = [ nodataColor, nodataColor,
									nodataColor ];
							hoverBackgroundColor = [ nodataColor, nodataColor,
									nodataColor ];
						} else {
							dataItem = [ item[i].redCount, item[i].greenCount,
									item[i].greyCount ];
							backgroundColor = [ errorColor, successColor,
									disabledColor ];
							hoverBackgroundColor = [ errorColor, successColor,
									disabledColor ];

						}
						if (item[i].alertLevel == 2) {
							alertCls = '#fcb322;';
						} else if (item[i].alertLevel == 3) {
							alertCls = '#ff7068;';
						}

						temp
								.push('<div class="col-sm-6 col-md-4 col-xs-12 usage-dashboard-item dashboard-item" >');
						temp
								.push('<div class="usage-dashboard-box dashboard-box clearfix" style="position:relative" id="'
										+ item[i].name + '">');
						if (TMS.isPC()) {
							temp
									.push('<span class="delete-dashboard-item delete-usage real-usage-del" >×</span>');
						} else {
							temp
									.push('<span class="delete-dashboard-item delete-usage" >×</span>');
						}
						temp
								.push('<div class="usage-dashboard-panel dashboard-panel">');
						if (usageDashboardType == "Percentage") {
							// percentage
							var alertValue = item[i].alertValue;
							var redValue = (item[i].redCount / item[i].total * 100)
									.toFixed(3);
							var greenValue = (item[i].greenCount/ item[i].total * 100).toFixed(3);
							var greyValue = (item[i].greyCount / item[i].total * 100)
									.toFixed(3);
							redValue = redValue.substring(0, redValue
									.indexOf('.') + 2);
							greenValue = greenValue.substring(0, greenValue
									.indexOf('.') + 2);
							greyValue = greyValue.substring(0, greyValue
									.indexOf('.') + 2);

							if (item[i].total == null || item[i].total == 0) {
								alertValue = '0.0<span>%</span>';
								redValue = '0.0%';
								greenValue = '0.0%';
								greyValue = '100.0%';
							} else {
								redValue = redValue + '<span>%</span>';
								greenValue = greenValue + '<span>%</span>';
								greyValue = greyValue + '<span>%</span>';
								;
								alertValue = redValue;
							}
							var notStatistics = item[i].greyCount/ (item[i].total) * 100;
							if (item[i].total == null || item[i].total == 0) {
								greyValue = '0.0%';
								notStatistics = '0.0%';
							} else {
								notStatistics = notStatistics.toFixed(1) + '%';
							}
						} else {
							// count
							if (item[i].redCount == null
									|| item[i].greyCount == null) {
								alertValue = 0;
								notStatistics = 0;
								/*
								 * redValue = 0; greenValue =
								 * item[i].greenCount; greyValue = 0;
								 */
							} else {
								alertValue = item[i].redCount;
								notStatistics = item[i].greyCount;
								redValue = item[i].redCount;
								greenValue = item[i].greenCount;
								greyValue = item[i].greyCount;
							}
						}
						;
						temp.push('<p class="dashboard-name" style="color: "'+alertCls+'>' + item[i].name
								+ '</p>');
						/*
						 * temp.push('<p class="dashboard-bignumber"  style="color:'+ alertCls + '">' +
						 * alertValue + '</p>');
						 */
						temp
								.push('<p class="dashboard-smallnumber errorC"><span class="dashboard-smallnumber-title">Failed:</span><span class="dashboard-num">'
										+ redValue + '</span></p>');
						temp
								.push('<p class="dashboard-smallnumber successC"><span class="dashboard-smallnumber-title">Successful:</span><span class="dashboard-num">'
										+ greenValue + '</span></p>');
						temp.push('</div>');
						temp
								.push('<div class="usage-dashboard-chart dashboard-chart">');
						temp.push('<span class="dashboard-num2" style="color:'
								+ alertCls + '">' + alertValue + '');
						temp.push('</span>');
						temp
								.push('<canvas width="100px" height="100px"></canvas>');
						temp.push('</div>');
						temp.push('</div>');
						temp.push('</div>');
						chartObj[item[i].name] = {
							type : 'doughnut',
							data : {
								labels : [ item[i].greenTitle,
										item[i].greyTitle, item[i].redTitle ],
								datasets : [ {
									data : dataItem,
									backgroundColor : backgroundColor,
									hoverBackgroundColor : hoverBackgroundColor,
								} ]
							},
							options : {
								cutoutPercentage : 80,
								onClick : function(e, data) {

								},
								legend : {
									display : false
								},
								tooltips : {
									enabled : false,
								}
							}
						}
					}
				}
				$('.usage-dashboard-row').html(temp.join(''));
				$('.usage-dashboard-box canvas').each(
						function(index, el) {
							var id = $(this).parents('.usage-dashboard-box')
									.attr('id');
							var ctx = el.getContext('2d');
							var myDoughnutChart = new Chart(ctx, chartObj[id]);
						});
				comment.gridlyIt('.usage-dashboard-row');
			} else {
				// Bar
				for (var i = 0; i < item.length; i++) {
					var isshow = item[i][0].isshow;// 判断显示item[i][0].isshow =
					// 1||0;需要后台输出的字段判断
					if (first) {
						if (isshow) {
							usageArray.push(item[i][0].name)
						}
						allusageArray.push(item[i][0].name)
					}
					
					if (first && isshow || !first
							&& usageArray.indexOf(item[i][0].name) != -1) {
						var itemName = '', // 每项name
						dateArr = [], errorArr = [], successArr = [], disabledArr = [];
						var alertCls = '';
						var ticks = {};
						var flag = 1, tempSum = 0;
						for (var j = 0; j < item[i].length; j++) {
							itemData = item[i][j];
							itemName = itemData.name;

							var totalCount = itemData.total;
							tempSum = tempSum + totalCount;
							var redCount = itemData.redCount, greenCount = itemData.greenCount;
							if (usageDashboardType == "Percentage") {
								// percentage
								redCount = (itemData.redCount / totalCount * 100)
										.toFixed(1);
								greenCount = (itemData.greenCount / totalCount * 100)
										.toFixed(1);
								greyCount = (itemData.greyCount / totalCount * 100)
										.toFixed(1);

								ticks.beginAtZero = true;
								ticks.callback = function(value, index, values) {
									return value + '%'
								};

							} else {
								// count
								redCount = itemData.redCount;
								greenCount = itemData.greenCount;
								greyCount = itemData.greyCount;
								ticks.beginAtZero = true;
								ticks.callback = function(value, index, values) {
									if (value < 1) {
										value = value.toFixed(1);
									}
									return value
								};
							}
							;
							var delFlag = '';
							if (TMS.isPC()) {
								delFlag = '<span class="delete-dashboard-item delete-usage real-usage-del">×</span>';
							} else {
								delFlag = '<span class="delete-dashboard-item delete-usage">×</span>';
							}
							if (j == 0) {
								var tempItem = [
									'<div class="col-sm-6 col-md-4 col-xs-12 usage-dashboard-item dashboard-item">',
									'<div class="usage-dashboard-box dashboard-box  clearfix"  style="position:relative" id="'
											+ itemName + '">',
									delFlag,
									'<div class="usage-dashboard-bar-title dashboard-bar-title dashboard-name" style="color:'
											+ alertCls + '">',
									itemName,
									'</div>',
									'<div class="dashboard-bar">',
									'<canvas></canvas>',
									'</div>',
									'<div class="usageStatus">',
										'<span class="usageStatus-name error">Failed</span>',
										'<span class="usageStatus-name success">Successful</span>',
									'</div>',
									'</div>', '</div>', '</div>' ]
							}
							if (itemData.xvalue == null) {
								dateArr.push('');

							} else {

								dateArr.push(itemData.xvalue);
							}

							errorArr.push(redCount);
							successArr.push(greenCount);
							disabledArr.push(greyCount);
						}
						if (tempSum == 0) {
							flag = 0;
						}
						temp.push(tempItem.join(''));
						
						chartObj[itemName] = {
							type : 'bar',
							data : {
								labels : dateArr,
								datasets : [ {
									label : "Failed",
									data : errorArr,
									backgroundColor : errorColor,
									borderColor : '#fff'
								}, {
									label : "Successful",
									data : successArr,
									backgroundColor : successColor,
									borderColor : '#fff'
								}, {
									label : "Pending",
									data : disabledArr,
									backgroundColor : disabledColor,
									borderColor : '#fff'
								} ]

							},
							options : {
								borderColor : [ '#fff', '#fff', '#fff' ],
								borderWidth : [ 1, 1, 1 ],
								legend : {
									display : false
								},
								scales : {
									gridLines : {
										display : false
									},
									xAxes : [ {
										stacked : false,
										categoryPercentage : 0.4,
									} ],
									yAxes : [ {
										stacked : false,
										display : flag,
										ticks : {
											beginAtZero : ticks.beginAtZero,
											callback : ticks.callback
										}
									} ]

								},
								tooltips : {
									enabled : true,
									callbacks : {
										label : function(tooltipItem, data) {
											var datasetIndex = tooltipItem.datasetIndex, index = tooltipItem.index, percent = '';

											if (usageDashboardType == "Percentage") {
												percent = '%';
											}
											return data.datasets[datasetIndex].label
													+ ':'
													+ data.datasets[datasetIndex].data[index]
													+ percent
										}
									}
								}

							}

						}
					}
				}

				$('.usage-dashboard-row').html(temp.join(''));
				$('.usage-dashboard-box').each(
						function(index, el) {
							var id = $(el).attr('id');
							new Chart($(el).find('canvas')[0].getContext('2d'),
									chartObj[id]);
						});
				comment.gridlyIt('.usage-dashboard-row');
			}

		},
		bindEvent : function() {
			var $body = $(".g-container"), self = this;
			// mouseenter and mouseleave
			$body.on('mouseenter', '.usage-dashboard-item', function() {
				$(this).find('.delete-dashboard-item').show(300);
			})
			$body.on('mouseleave', '.usage-dashboard-item', function() {
				$(this).find('.delete-dashboard-item').hide(300);
			})
			$body.on('click', '.dashboard-reload', function() {
				self.ajaxDashboard();
			});
			$body.on('click', '.alert-reload', function() {
				self.ajaxAlert();
			});
			$body
					.on(
							'click',
							'.J-data-type a',
							function() {
								var targetName = $(this).text();
								usageArray = [];

								// 取页面上显示的面板
								if (usageDashboardType == targetName) {
									return;
								} else {
									sessionStorage.setItem('GROUP_USAGE_TYPE',
											targetName);
									usageDashboardType = targetName;
									var showObj = $(".usage-dashboard-row .usage-dashboard-item");
									for (var j = 0; j < showObj.length; j++) {
										usageArray.push(showObj.eq(j).find(
												".dashboard-name").html());
									}
									self.ajaxDashboard(false);
								}
								$(this)
										.parents(".usage-dashboard-dropdown")
										.siblings(
												'button[data-toggle="dropdown"]')
										.html(
												usageDashboardType
														+ ' <span class="caret"></span>');

							});
			$body
					.on(
							'click',
							'.J-chart-type a',
							function() {
								var targetName = $(this).text();
								usageArray = [];

								if (type == targetName) {
									return;
								} else {
									type = targetName;
									sessionStorage.setItem(
											'GROUP_USAGE_CHART_TYPE',
											targetName);

									var showObj = $(".usage-dashboard-row .usage-dashboard-item");
									for (var j = 0; j < showObj.length; j++) {
										usageArray.push(showObj.eq(j).find(
												".dashboard-name").html());
									}
									self.ajaxDashboard(false);
									$(this)
											.parents(
													".usage-dashboard-dropdown")
											.siblings(
													'button[data-toggle="dropdown"]')
											.html(
													type
															+ ' <span class="caret"></span>');
								}

							});
			$body.on('mouseleave', '.usage-dashboard-chart', function(event) {
				$('.usage-dashboard-tooltip').remove();
			});
			$('.g-middle-content').scroll(function() {
				$('.usage-dashboard-tooltip').remove();
			});

		}
	};

	$(function() {
		TMS.init();
		realTime.init();
		comment.init();

	})
});
