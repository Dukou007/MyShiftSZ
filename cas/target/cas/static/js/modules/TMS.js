define(function(require, exports, module) {
		
	require('table');
	var TMS = {
		init: function() {
			var self = this;
			self.bindEvent();
			self.topMenu();
			self.leftmenu.init()
		},
		isPC: function() { //验证是否是pc端
			var userAgentInfo = navigator.userAgent;
			var Agents = ["Android", "iPhone",
				"SymbianOS", "Windows Phone",
				"iPad", "iPod"
			];
			var flag = true;
			for (var v = 0; v < Agents.length; v++) {
				if (userAgentInfo.indexOf(Agents[v]) > 0) {
					flag = false;
					break;
				}
			}
			return flag;
		},
		isPad:function(){//判断ipad
			var userAgentInfo = navigator.userAgent;
			if(userAgentInfo.indexOf('iPad')>0){
				return true;
			}
			return false;
		},
		bindEvent: function() {
			//隐藏显示group菜单
			var self = this;
			$('.button-menu-mobile').click(function() {
				$('.side-menu').toggleClass('mobile-toggle');
				$('.content-page').toggleClass('mobile-toggle');
				$('.logo-box').toggleClass('mobile-toggle');
				$('.tooltips-top').toggleClass('mobile-toggle');
			});
			$('.navbar-collapse').on('shown.bs.collapse', function() {
				$('.mask-collapse').show();
			})
			$('.navbar-collapse').on('hidden.bs.collapse', function() {
				$('.mask-collapse').hide();
			})
			$('.mask-collapse').click(function() {
				$('.navbar-collapse').collapse('hide');
			});
			if (!this.isPC()) {
				$(document).on('click', '.body-content input.form-control,.body-content input.form-input,.body-content textarea.form-input', function(event) { //fix bug 安卓机上滚动bug
					var scrollVal = $(this).offset().top + $('.body-content').scrollTop();
					$('.body-content').scrollTop(scrollVal - 100);
				});
			}

		},
		setArea: function(country, province, city) { //国家省市城市联动
			var $countryId = $(country),
				$provinceId = $(province),
				$cityId = $(city);
			$countryId.change(function() {
				if (!$countryId.val()) {
					$provinceId.empty();
					$provinceId.append($('<option></option>').val('')
						.text("--Please Select--"));
				}
				$.ajax({
					global: false,
					type: 'POST',
					dataType: "json",
					url: _WEB_SITE_+"/location/province/select?countryId=" + encodeURIComponent($countryId.val()),
					cache: false,
					data: {},
					success: function(json) {
						if (!json)
							return;
						$provinceId.empty();
						$provinceId.append($('<option></option>')
							.val('')
							.text(
								"--Please Select--"));
						$.each(json, function(i) {
							if (json[i] && json[i].length > 1) {
								$provinceId.append($(
										"<option></option>")
									.val(json[i][0]).text(json[i][1]));
							}
						});
						$provinceId.trigger("change");
					},
					error: function() {
						alert();
					}

				});
			});
			$provinceId.change(function() {
				if (!$provinceId.val()) {
					$cityId.empty();
					$cityId.append($('<option></option>').val('').text("--Please Select--"));
				}
				$.ajax({
					global: false,
					type: 'POST',
					dataType: "json",
					url: _WEB_SITE_+"/location/city/select?provinceId=" + encodeURIComponent($provinceId.val()),
					cache: false,
					data: {},
					success: function(json) {
						if (!json) return;
						$cityId.empty();
						$cityId.append($('<option></option>').val('').text(
							"--Please Select--"));
						$.each(json, function(i) {
							if (json[i] && json[i].length > 1) {
								$cityId.append($("<option></option>").val(
									json[i][0]).text(json[i][1]));
							}
						});
						$cityId.trigger("change");
					},

				});
			});
		},
		bootTable: function(el, option) { //table
			var self = this,
				$el = $(el),
				isPc = self.isPC(),
				isPad = self.isPad();
			//require.async('table', function() {
				var _option = {};
				_option.icons = { //自定义图标
					'detailOpen': 'glyphicon-chevron-down',
					'detailClose': 'glyphicon-chevron-up',
					'refresh': 'glyphicon-refresh icon-refresh'
				};
				_option.buttonsClass='primary';
				_option.queryParamsType='';//默认接口参数格式不按limit
				if(!isPc && !isPad){
					_option.cardView = true;
				}
				$.fn.bootstrapTable.locales['en-US'].formatLoadingMessage = function() { //加载中文案
					return 'loading';
				}
				$.fn.bootstrapTable.locales['en-US'].formatNoMatches = function() { //无数据文案
					return "<div class='alert alert-info paxinfonext'><a class='btn btn-xs btn-primary pull-right table-reload'  href='javascript:void(0)'>You can click refresh again</a><span  class='paxspannext'><strong>Hint:</strong>There is no data!</span></div>"
				}
				$.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['en-US'])
				$el.bootstrapTable($.extend({}, _option, option));
				var $search = $('.bootstrap-table .search'),$toolboor,$refresh;
				$('.body-content').on('click', '.table-search-btn', function(event) {
					var $searchInput = $(this).siblings('input');
					$toolboor = $(this).parents('.fixed-table-toolbar').find('.bs-bars');
					$refresh = $(this).parents('.fixed-table-toolbar').find('.columns');
					if ($searchInput.is(":hidden")) {
						$searchInput.show();
						if (!isPc) {
							$toolboor.hide();
							$refresh.hide();
						}
						

					} else {
						$el.bootstrapTable('resetSearch', $searchInput.val());
					}
					event.stopPropagation();
				});
				
				if (!isPc) {
					$('body').click(function(event) {
						$('.bootstrap-table .search input').hide();
						$toolboor&&$toolboor.show();
						$refresh&&$refresh.show();
					});
					$('.bootstrap-table .search input').click(function(event) {
						event.stopPropagation();
					});
				}

				$('html').off().on('click', '.table-reload', function(event) {
					$el.bootstrapTable('refresh');
				});
				$el.on('load-error.bs.table', function(e, data) {
					option.loadend && option.loadend()
					$el.find('.no-records-found td').html("<div class='alert alert-danger'><a class='btn btn-xs btn-danger pull-right table-reload' href='javascript:void(0)'>The request is wrong, please refresh the page again</a><span class='paxspan'><strong>Hint:</strong>there is an error in your request!</span></div>");

					console.log(JSON.stringify(data))
				});
				$el.on('load-success.bs.table', function(e, data) {
					option.loadend && option.loadend();
					console.log(e, data)
					if (data.statusCode !== undefined && data.statusCode != 200) {
						//error
						$(el).find('.no-records-found td').html("<div class='alert alert-info paxinfonext'><a class='btn btn-xs btn-primary pull-right table-reload'  href='javascript:void(0)'>You can click refresh again</a><span  class='paxspannext'><strong>Hint:</strong>" + data.message + "</span></div>");
					}
				});
				if (option.autocomplete) {
					require.async('jquery-ui', function() {
						$('.bootstrap-table .search input').autocomplete({
							source: option.autocompleteUrl,
							appendTo: ".body-content"
						});
					})
				}
			//});

		},
		leftmenu: {
			init: function() {
				var self = this;
				self.ajax('1', '20', 1);
				self.bindEvent();
			},
			ajax: function(pageIndex, pageSize, isfrist) {
				var self = this,
				groupId = $("#gid").val(),
					$menu = $('.menu-ul');
				$menu.attr('isajax', '1');
				$.ajax({
					url: _WEB_SITE_ + '/group/service/tree',
					type: 'GET',
					contentType: 'application/json',
					dataType: 'JSON',
					data: {
						groupId: groupId,
						pageIndex: pageIndex,
						pageSize: pageSize
					},
					success: function(json) {
						self.renderList(json, isfrist);
					},
					error: function(jqXHR) {
						console.log(JSON.stringify(jqXHR));
						$menu.attr('isajax', '0');
						$('.loading').hide();
					}
				});
			},
			searchAjax: function(name, pageIndex, pageSize) {
				var self = this,
					$searchList = $('.menu-search-list'),
					$loading = $('.menu-search-loading');
				$searchList.attr('isajax', '1');
				$searchList.attr('isend', '0');
				$loading.show();
				$.ajax({
					url: _WEB_SITE_ + '/group/service/search',
					type: 'GET',
					contentType: 'application/json',
					dataType: 'JSON',
					data: {
						fuzzyCondition: name,
						pageIndex: pageIndex,
						pageSize: pageSize
					},
					success: function(json) {
						self.renderSearchList(json);
					},
					error: function(jqXHR) {
						console.log(JSON.stringify(jqXHR));
						$searchList.attr('isajax', '0');
						$loading.hide();
					}
				});
			},
			renderList: function(json, isfrist) {
				var $menu = $('.menu-ul'),
					$loading = $('.loading');
				if (isfrist) {
					var groups = json.groups,
						groupTemp = [];
					for (var j = 0; j < groups.length; j++) {
						if (j != groups.length - 1) {
							groupTemp.push('<li>' + '<a href="' + _WEB_SITE_ + "/terminal/list/" + groups[j].id + '" data-id="' + groups[j].id + '">' + groups[j].name + '</a>' + '</li>');
						} else {
							groupTemp.push('<li class="active" >' + groups[j].name + '</li>');
						}
					}
					$('.breadcrumb').html(groupTemp.join(''));
				}

				var items = json.items;
				var temp = [];
				for (var i = 0; i < items.length; i++) {
					temp.push('<li>');
					temp.push('<a href="' + _WEB_SITE_ + "/terminal/list/" + items[i].id + '" data-id="' + items[i].id + '">');
					temp.push('<i class="iconfont menu-group-icon">&#xe604;</i>');
					temp.push('<div class="menu-li-name" >' + items[i].name + '</div>');
					temp.push('</a>');
					temp.push('</li>');
				}
				$menu.append(temp.join(''));
				if (json.totalCount - json.pageIndex * json.pageSize > 0) { //还有数据
					$loading.show();
					$menu.attr('data-index', json.pageIndex);
					$menu.attr('data-pagesize', json.pageSize);
				} else {
					$loading.hide();
					$menu.attr('isend', '1');
				}
				$menu.attr('isajax', '0');
			},
			renderSearchList: function(json) {
				var $searchList = $('.menu-search-list'),
					$loading = $('.menu-search-loading'),
					$nothing = $('.menu-search-nothing');
				var items = json.items;
				var temp = [];
				if (items.length == 0 && json.pageIndex == 1) {
					$nothing.show();
				} else {
					$nothing.hide();
					for (var i = 0; i < items.length; i++) {
						temp.push('<li class="menu-search-item">');
						temp.push('<a href="' + _WEB_SITE_ + "/terminal/list/" + items[i].id + '" data-id="' + items[i].id + '">');
						temp.push('<i class="iconfont">&#xe604;</i>' + items[i].name);
						temp.push('</a>');
						temp.push('</li>');
					}
					$searchList.append(temp.join(''));
				}


				if (json.totalCount - json.pageIndex * json.pageSize > 0) { //还有数据
					$loading.show();
					$searchList.attr('data-index', json.pageIndex);
					$searchList.attr('data-pagesize', json.pageSize);
				} else {
					$loading.hide();
					$searchList.attr('isend', '1');
				}
				$searchList.attr('isajax', '0');
			},
			bindEvent: function() {
				var self = this,
					$doc = $(document),
					flag = 1,
					timer;
				var $menu = $('.menu-ul'),
					$menuPush = $('.menu-push'),
					$menuBox = $('.menu-box'),
					$menuGroup = $('.menu-group-pannel'),
					$searchPannel = $('.menu-search-pannel'),
					$searchMenu = $('.menu-search-list'),
					$searchInput = $('.tooltips-left .search-box input'),
					$searchBtn = $('.tooltips-left .search-btn'),
					$searchBox = $('.tooltips-left .search-box'),
					$loading = $('.menu-search-loading'),
					$searchDel = $('.tooltips-left .delete-btn');
				$menuGroup.scroll(function() {
					if (($menuGroup.height() + $menuGroup.scrollTop()) > ($menu.height() - 40)) {

						if ($menu.attr('isend') != '1' && $menu.attr('isajax') != '1') {
							var pageIndex = $menu.attr('data-index'),
								pageSize = $menu.attr('data-pagesize');
							pageIndex = Number(pageIndex) + 1;
							self.ajax(pageIndex, pageSize, 0);
						}

					}
				});
				$searchPannel.scroll(function() {
					if (($searchPannel.height() + $searchPannel.scrollTop()) > ($searchMenu.height() - 40)) {
						if ($searchMenu.attr('isend') != '1' && $searchMenu.attr('isajax') != '1') {
							var pageIndex = $searchMenu.attr('data-index'),
							name = $searchInput.val();
							pageSize = $searchMenu.attr('data-pagesize');
							pageIndex = Number(pageIndex) + 1;
							self.searchAjax(name, pageIndex, pageSize);
						}

					}
				});
				$searchBtn.click(function() {
					if ($searchBox.hasClass('hide')) {
						$searchBox.removeClass('hide');
						$searchPannel.show();
						$searchInput.focus();
					} else {
						var name = $searchInput.val();
						if (name.length == 0) {
							return
						}
						$searchMenu.html('');
						self.searchAjax(name, '1', '20');
					}
				});
				$searchInput.keyup(function() {
					var name = $(this).val();
					$searchMenu.html('');
					$loading.hide();
					if (name.length == 0) {

						return
					}

					self.searchAjax(name, '1', '20');
				});
				$searchDel.click(function() {
					if ($searchInput.val().length == 0) {
						$searchBox.addClass('hide');
						$searchPannel.hide();
						$searchMenu.html('');
					} else {
						$searchInput.val('');
						$searchMenu.html('');

					}
					$loading.hide();
				});
				require.async('xscroll', function(xScroll) {
					new xScroll({
						el: '#breadcrumb'
					});
				});
			}
		},
		alert: function(message, type) {
			require.async('alert', function() {
				$.bootstrapGrowl(message, {
					ele:'.tooltips-right',
					type: type,
					offset: {
						from: 'top',
						amount: 10
					},
					width: 'auto',
					delay: 5000
				});
			});
		},
		resultHandle: function(json) {
			var self = this;
			if (!json.statusCode || json.statusCode && json.statusCode == 200) {
				//success
				self.alert(json.message, 'success')
			} else {
				//error
				self.alert(json.message, 'danger')
			}
		},
		getData: function(option, callback) { //ajax调用方法
			var self = this;
			self.loading.show();
			$.ajax({
				url: option.url,
				type: option.type,
				dataType: 'json',
				data: option.data ? option.data : {},
				success: function(data) {
					if(data.statusCode && data.statusCode!='200'){
						if(data.message){
							self.alert(data.message, 'danger');
							self.loading.hide();
						} else {
							self.alert('Network instability', 'danger');
							self.loading.hide();
						}
					} else {
						if(callback) {
							callback(data);
						} else {
							TMS.alert(data.message, 'success');
							if(option.followTo) {
	        					setTimeout(function(){
	        						window.location.href=option.followTo;
	        					},1000);
							}
						}
						self.loading.hide();
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					console.log(JSON.stringify(XMLHttpRequest))
					//callback && callback('fail');
					self.alert('Network instability', 'danger');
					self.loading.hide();
				}
			});


		},
		loading: {
			show: function() {
				var temp = [
						'<div class="g-loading">',
						'<div class="g-loading-box">',
						'<div class="spinner"><div class="dot1"></div><div class="dot2"></div></div>',
						'<span class="g-loading-text">loading</span>',
						'</div></div>'
					],
					$load = $('.g-loading');
				if ($load.length == 0) {
					$('body').append(temp.join(''));
				} else {
					$load.show();
				}
			},
			hide: function() {
				$('.g-loading').hide();
			}
		},
		topMenu:function(){
			var self = this;

			$('#menubar').on('click', '.J-dropdown', function(e) {
				var $menu = $(this).siblings('.dropdown-menu');
				var isShow = $menu.is(':hidden');
				if(isShow){
					$('.h5-dropdown-menu').html('<ul class="dropdown-menu">'+$menu.html()+'</ul>');
				}else{
					$('.h5-dropdown-menu').html('');
				}

			});
			$('.nav-user-item,.more-menu').click(function() {
				$('.h5-dropdown-menu').html('');
			});
			$(document).click(function(){
				$('.h5-dropdown-menu').html('');
			})
		}
	};
	return TMS;



})