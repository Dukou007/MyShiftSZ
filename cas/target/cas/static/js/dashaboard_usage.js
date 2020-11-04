define(function(require, exports, module) {
	require('chart');
	var TMS = require('TMS');
	var bl = 1.76331361, // 缩放
	_w, _h;
	var dashUsage = {
		init : function() {
			TMS.init();
			this.ajax();
			this.bindEvent();
			this.isResize = false;
		},
		ajax : function() {
			var self = this;
			TMS.getData({
				url : $("#contextPath").val() + '/dashboard/ajaxIndexMore',
				type : 'post'
			}, function(data) {
				if (!data.statusCode || data.statusCode
						&& data.statusCode == 200) {
					self.toDo(data);
				} else {
					TMS.alert(data.message, 'danger');
				}
			});
		},
		columnAjax : function(id, callback) {
			var self = this;
			TMS.getData({
				url : $("#contextPath").val() + '/dashboard/ajaxDetail'+name,
				type : 'post',
				data : {
					name : id
				}
			}, function(data) {
				if (!data.statusCode || data.statusCode
						&& data.statusCode == 200) {
					callback && callback();
					self.columnToDo(id, data);

					$('.dashaboard-chart[data-id="' + id + '"]').attr(
							'data-ajax', '1');
				} else {
					TMS.alert(data.message, 'danger');
				}

			});
		},
		columnToDo : function(id, data) {
			var item = data.result, self = this, dateArr = [], errorArr = [], successArr = [], disabledArr = [];
			for (var i = 0; i < item.length; i++) {
				dateArr.push(item[i].dateStr);
				errorArr.push(item[i].redCount);
				successArr.push(item[i].greenCount);
				disabledArr.push(item[i].greyCount);
			}
			var chartObj = {
				type : 'bar',
				data : {
					labels : dateArr,
					datasets : [ {
						label : "success",
						data : successArr,
						backgroundColor : self.successColor,
						borderColor : self.successColor
					}, {
						label : "error",
						data : errorArr,
						backgroundColor : self.errorColor,
						borderColor : self.successColor
					}, {
						label : "disabled",
						data : disabledArr,
						backgroundColor : self.disabledColor,
						borderColor : self.disabledColor
					} ]

				},
				options : {
					legend : {
						display : false
					},
					gridLines : {
						offsetGridLines : true
					},
					tooltips : {
						callbacks : {
							label : function(tooltipItem, data) {
								var datasetIndex = tooltipItem.datasetIndex, index = tooltipItem.index;
								console.log(tooltipItem, data)
								return data.datasets[datasetIndex].label
										+ ':'
										+ data.datasets[datasetIndex].data[index];
							}
						}
					}

				}
			}
			var canvasH = $(
					'.dashaboard-chart[data-id="' + id + '"] .panel-body')
					.height(), canvasW = $(
					'.dashaboard-chart[data-id="' + id + '"] .panel-body')
					.width();
			$('.dashaboard-chart[data-id="' + id + '"] .chart-column-holder')
					.html(
							'<canvas class="chart-pannel" width="' + canvasW
									+ '" height="' + canvasH + '" ></canvas>');
			var canvas = $('.dashaboard-chart[data-id="' + id
					+ '"] .chart-column-holder .chart-pannel');
			ctx = canvas[0].getContext('2d');
			new Chart(ctx, chartObj);

		},
		toDo : function(data) {
			var item = data.result,date=data.date, self = this, temp = [], chartObj = {};
			self.errorColor = '#e75f47';
			self.successColor = '#43aea8';
			self.disabledColor = '#c1c5c8';
			$('.dashaboard-time').html('<span style="color: #333;">Last Update Time:</span> '+date);
			for (var i = 0; i < item.length; i++) {
				var alertCls = '';
				if (item[i].alertLevel == 1) {
					alertCls = 'color:#fcb322;';
				} else if (item[i].alertLevel == 2) {
					alertCls = 'color:#e75f47;';
				}
				temp
						.push('<div class="col-md-4 col-sm-6  dashaboard-chart" data-id="'
								+ item[i].name + '">');
				temp.push('<div class="panel panel-default">');
				temp.push('<div class="panel-heading clearfix">');
				temp.push('<span class="dashaboard-title " style="' + alertCls
						+ '"> ');
				temp.push(item[i].name);
				temp.push('</span>');
				temp.push('<div class="dashaboard-pull-right">');
				temp
						.push('<div class="dashaboard-chart-btn dropdown-toggle " data-toggle="dropdown"><i class="iconfont">&#xe610;</i><span class="caret"></span></div>');
				temp.push('<ul class="dropdown-menu dropdown-menu-right">');
				temp
						.push('<li class="dashaboard-donut"><a href="javascript:;"><i class="iconfont" style="display: inline-block;width: 25px;text-align: left;">&#xe610;</i> <span>Donut</span></a></li>');
				temp
						.push('<li class="dashaboard-bar"><a href="javascript:;"><i class="iconfont" style="display: inline-block;width: 25px;text-align: left;">&#xe611;</i> <span>Bar</span></a></li>');
				temp.push('</ul>');
				temp.push('</div>');
				temp.push('</div>');
				temp.push('<div class="panel-body usage-chart-panelbody">');
				temp.push('<span class="chart-main-percent" style="' + alertCls
						+ '">' + item[i].alertValue + '%</span>');
				temp.push('<div class="chart-holder" >');
				temp
						.push('<canvas class="chart-pannel" width="200" height="200"></canvas>');

				temp.push('</div>');
				temp.push('<div class="chart-column-holder hide">');
				temp.push('<canvas class="chart-pannel" ></canvas>');
				temp.push('</div>');
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
							backgroundColor : [ self.successColor,
									self.disabledColor, self.errorColor ],
							hoverBackgroundColor : [ self.successColor,
									self.disabledColor, self.errorColor ]
						} ]
					},
					options : {
						legend : {
							display : false
						},
						tooltips : {
							callbacks : {
								label : function(tooltipItem, data) {
									var datasetIndex = tooltipItem.datasetIndex, index = tooltipItem.index;
									console.log(tooltipItem, data)
									return data.labels[index]
											+ ':'
											+ data.datasets[datasetIndex].data[index]
								}
							}
						}
					}
				}
			}
			$('#dashaboard-panel').html(temp.join(''));
			self.resize();

			$('.dashaboard-chart .chart-holder .chart-pannel').each(
					function(index, el) {
						var id = $(this).parents('.dashaboard-chart').attr(
								'data-id');
						var ctx = $(el)[0].getContext('2d');
						new Chart(ctx, chartObj[id]);
					});
		},
		resize : function() {
			var self = this;
			_w = $('.panel-body:eq(0)').width();
			/*
			 * if($(window).width()<=376){ bl = 1.108; }else{ bl = 1.763; }
			 */
			_h = _w / bl;
			/*
			 * if(_h>290){ _h = 290 }else if(_h<190){ _h = 190; }
			 */
			console.log(_h)

			$('.panel-body').height(_h);

		},
		bindEvent : function() {
			var self = this;

			$('.body-content')
					.on(
							'click',
							'.dashaboard-bar',
							function() {
								var id = $(this).parents('.dashaboard-chart')
										.attr('data-id'), _self = this;
								if ($(this).parents('.dashaboard-chart').attr(
										'data-ajax') != '1') {
									self
											.columnAjax(
													id,
													function() {
														$(_self)
																.parents(
																		'.panel')
																.find(
																		'.dashaboard-chart-btn .iconfont')
																.html(
																		'&#xe611;');
														$(_self)
																.parents(
																		'.panel')
																.find(
																		'.chart-holder')
																.addClass(
																		'hide');
														$(_self)
																.parents(
																		'.panel')
																.find(
																		'.chart-column-holder')
																.removeClass(
																		'hide');
														$(_self)
																.parents(
																		'.panel')
																.find(
																		'.chart-main-percent')
																.hide();
													});
								} else {
									$(_self).parents('.panel').find(
											'.dashaboard-chart-btn .iconfont')
											.html('&#xe611;');
									$(_self).parents('.panel').find(
											'.chart-holder').addClass('hide');
									$(_self).parents('.panel').find(
											'.chart-column-holder')
											.removeClass('hide');
									$(_self).parents('.panel').find(
											'.chart-main-percent').hide();
								}

							});
			$('.body-content').on(
					'click',
					'.dashaboard-donut',
					function() {
						var id = $(this).parents('.dashaboard-chart').attr(
								'data-id');
						$(this).parents('.panel').find(
								'.dashaboard-chart-btn .iconfont').html(
								'&#xe610;');
						$(this).parents('.panel').find('.chart-holder')
								.removeClass('hide');
						$(this).parents('.panel').find('.chart-column-holder')
								.addClass('hide');
						$(this).parents('.panel').find('.chart-main-percent')
								.show();

					});
			var timer = '';
			$('.button-menu-mobile').click(function() {
				clearTimeout(timer);
				timer = setTimeout(function() {
					self.resize();
					/*
					 * if($('.body-content').width()<500){
					 * $('.dashaboard-chart').css({'width':'100%'}); }
					 */
				}, 300)

			});
			$(window).resize(function(event) {
				self.resize();
			});
		}
	}
	$(function() {
		dashUsage.init();

	});
});