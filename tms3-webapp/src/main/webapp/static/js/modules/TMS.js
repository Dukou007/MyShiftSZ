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
    require('table');
    require('alert');
    var TMS = {
        init: function() {
            var self = this;
            window.initialTable = false; //是否加载table中
            this.pageActive(); //导航页面选中
            this.searchGroupTerminal({
                activeUrl: _ACTIVE_URL_,
                groupId: _ACTIVE_GROUP_,
                tsn: _ACTIVE_TSN_,
                namePath: _NAME_PATH_
            });
            this.$body = $('.g-body');
            this.topLeftTree = new self.leftTree('#top-left-tree').init();
            this.repairIpadFocus() //解决ipad下光标错位问题
            this.bindEvent();
            this._isPc = self.isPC();
            this._isPad = self.isPad();
            this._isMobile = !this._isPc && !this._isPad;
            //fixed firefox刷新表单数据不清空
            $('input,select,textarea').attr('autocomplete', 'off');

            this.initExportUser()

        },
        isPC: function() { // 验证是否是pc端
            var userAgentInfo = navigator.userAgent;
            var Agents = ["Android", "iPhone", "SymbianOS", "Windows Phone",
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
        isPad: function() { // 判断ipad
            var userAgentInfo = navigator.userAgent;
            if (userAgentInfo.indexOf('iPad') > 0) {
                return true;
            }
            return false;
        },       
     
        //get checkbox id or tsns
        setCheckedItems: function(checkedItems, id) {
            var formatItems = '';
        	if (checkedItems.length) {
                if (checkedItems.length == 1) {
                    formatItems = checkedItems[0][id];
                } else {
                    for (var i = 0; i < checkedItems.length; i++) {
                        if (i < checkedItems.length - 1) {
                            formatItems += checkedItems[i][id] + ',';
                        } else {
                            formatItems += checkedItems[i][id]
                        }
                    }
                }
            } 
           
            return formatItems;
        },
		changeModalText: function(modal, action, type, checkedItems) {
				var textObj = {
					"key": ['Are you sure to '+action+' these '+type+'s?', 'Are you sure to '+action+' this '+type+'?']
				}
				if(checkedItems.length > 1) {
					$(modal+' .modal-body').text(textObj["key"][0]);
				} else {
					$(modal+' .modal-body').text(textObj["key"][1]);
				}
		},
        //see more
        groupPathSeeMore: function(el) {
            var leftover = $(el).find('#leftover').children();

            if (leftover.length > 0) {
                //add see more btn
                var btn = '<a class="more-btn"><i class="iconfont notend">&#xe66e;</i><span style="font-weight:bold">More</span></a>';
                $(el).append(btn);
                $('body').on('click', el + ' a', function() {
                    var btnText = $(this).find('span').text();
                    if (btnText == 'More') {
                        //调整箭头朝上，内容淡入
                        $(el).find('#leftover').fadeIn();
                        $(this).find('i').addClass('n');
                        $(this).find('span').text('Less');
                    } else {
                        $(el).find('#leftover').fadeOut();
                        $(this).find('i').removeClass('n');
                        $(this).find('span').text('More');
                    }
                });

            }
        },
        backReset: function(form) { //重置编辑状态
            form[0].reset();
            form.find('.form-edit-group').removeClass('has-error').removeClass('has-success');
            form.find('.form-edit-group  .help-block').remove();
        },
        removeAlertdanger: function(alertArr) {
            alertArr.remove();
        },
        convertArrayDescribe: function(arr) {
            var str = '';
            if (!arr) return;
            if (arr.length > 0 && arr.length <= 3) {
                for (var i = 0; i < arr.length; i++) {
                    str += arr[i] + ',';
                }
            } else {
                for (var i = 0; i < 3; i++) {
                    str += arr[i] + ',';
                }
            }
            return str.substr(0, str.length - 1);
        },
        changeTime: function(d, id, isUTCstr) {
            var self = this;

            if (d != null) {
                //给定时间UTC的值

                //计算夏令时下时间戳对应的变化
                //判断东西半球
            	var tolocaltime = null;
            	if (typeof(d) == 'string' && !isUTCstr) {
            		//兼容IE浏览器时间格式
            		tolocaltime = new Date(d.replace(/-/g, "/"));
            	} else {
            		tolocaltime = new Date(d);
            	}
                var month = tolocaltime.getMonth() + 1,
                    day = tolocaltime.getDate(),
                    hours = tolocaltime.getHours(),
                    min = tolocaltime.getMinutes(),
                    sec = tolocaltime.getSeconds(),
                    year = tolocaltime.getFullYear();

                if (id) {
                    return '<span  data-rowTime=' + d + ' data-rowId=' + id + '>' + this.stringNumber(hours) + ':' + this.stringNumber(min) + ' ' + this.stringNumber(month) + '/' + this.stringNumber(day) + '/' + year + '</span>';
                } else {
                    return '<span  data-rowTime=' + d + '>' + this.stringNumber(hours) + ':' + this.stringNumber(min) + ' ' + this.stringNumber(month) + '/' + this.stringNumber(day) + '/' + year + '</span>';
                }


            }
        },
        transferTimestamp: function(time) {
            var self = this;

            function isDayLightTime(time) {
                var now = new Date(time.replace(/-/g, "/"));
                var start = new Date();
                //得到一年的开始时间
                start.setMonth(0);
                start.setDate(1);
                start.setHours(0);
                start.setMinutes(0);
                start.setSeconds(0);

                var middle = new Date(start.getTime());
                middle.setMonth(6);
                // 如果年始和年中时差相同，则认为此时区没有夏令时
                if ((middle.getTimezoneOffset() - start.getTimezoneOffset()) == 0) {
                    return false;
                }

                var margin = 0;
                //判断当前用户在东半球还是西半球
                //处于东半球
                if (isEastEarthTime()) {
                    margin = start.getTimezoneOffset();
                }
                //处于西半球
                else {
                    margin = middle.getTimezoneOffset();
                }

                if (now.getTimezoneOffset() == margin) {
                    return true; //当前时间处在夏令时
                } else {
                    return false; //当前时间未处在夏令时
                }

            }
            //判断东西半球
            function isEastEarthTime() {
                var now = new Date();
                var timeZone = now.getTimezoneOffset();
                if (timeZone < 0) {
                    return true; } //true, 东半球
                else {
                    return false; } //false, 西半球
            }
            time = time.replace('-', '/').replace('-', '/');
            //当前时间转时间戳
            var currentTimestamp = Date.parse(time);
            //当前时区与UTC的偏移值
            var timezoneOffset = new Date().getTimezoneOffset() * 60 * 1000;
            //当前时间处在夏令时
            if (isDayLightTime(time)) {
                utctime = currentTimestamp + timezoneOffset - 60 * 60 * 1000;
            } else {
                utctime = currentTimestamp + timezoneOffset
            }

            var transferUTC = new Date(utctime);
            var month = transferUTC.getMonth() + 1,
                day = transferUTC.getDate(),
                hours = transferUTC.getHours(),
                min = transferUTC.getMinutes(),
                sec = transferUTC.getSeconds(),
                year = transferUTC.getFullYear();
            var formatTime = this.stringNumber(year) + '-' + this.stringNumber(month) + '-' + this.stringNumber(day) + ' ' + this.stringNumber(hours) + ':' + this.stringNumber(min) + ':' + this.stringNumber(sec);
            return formatTime;
        },
        stringNumber: function(n) {
            return n > 9 ? n : '0' + n;
        },

        repairIpadFocus: function() {
            $('input').on('focus', function() {
                var nowTop = $('.g-middle-content').scrollTop();
                setTimeout(function() {
                    $('.g-middle-content').scrollTop(nowTop);
                }, 10)
            })
        },
        
        //xss defence begin
        onSend:function (e,xhr,o) {
            console.log(o.data)
            o.data=this.dataEncode(o.data);
            console.log(o.data)
        },
        htmlEncode:function  (str){
            var s = "";
            if (str.length == 0) return "";
            //s = str.replace(/ /g, "&nbsp;");
            //s = str.replace(/&/g, "&amp;");
            s = str.replace(/</g, "%26lt%3B");
            s=s.replace(/%3C/g,"%26lt%3B");
            s=s.replace(/%3c/g,"%26lt%3B");
            s = s.replace(/>/g, "%26gt%3B");
            s = s.replace(/%3E/g, "%26gt%3B");
            s = s.replace(/%3e/g, "%26gt%3B");
//            s = s.replace(/\'/g, "&#39;");
//            s = s.replace(/\"/g, "&quot;");
            //s = s.replace(/\n/g, "<br>");
            return s;
        },
        htmlEncodeOut:function  (str){
            var s = "";
            if (str.length == 0) return "";
            //s = str.replace(/ /g, "&nbsp;");
            //s = str.replace(/&/g, "&amp;");
            s = str.replace(/</g, "&lt;");
            s=s.replace(/%3C/g,"&lt;");
            s=s.replace(/%3c/g,"&lt;");
            s = s.replace(/>/g, "&gt;");
            s = s.replace(/%3E/g, "&gt;");
            s = s.replace(/%3e/g, "&gt;");
            s = s.replace(/%26lt%3B/g, "&lt;");
            s = s.replace(/%26lt%3b/g, "&lt;");
            s = s.replace(/%26gt%3B/g, "&gt;");
            s = s.replace(/%26gt%3b/g, "&gt;");
            s = s.replace(/\'/g, "&#39;");
            //s = s.replace(/\"/g, "&quot;");
            //s = s.replace(/\n/g, "<br>");
            return s;
        },
        dataEncode:function (data){
            var rel=data;
            var source="";
            if(typeof(rel) == "object"){
                source=this.htmlEncode(JSON.stringify(rel));
                source=JSON.parse(source);
                rel=source;
            }else if(typeof(rel) == "string"){
                source=this.htmlEncode(rel);
                rel=source;
            }
            return rel;
        },
        dataEncodeOut:function (data){
            var rel=data;
            var source="";
            if(typeof(rel) == "object"){
                source=this.htmlEncodeOut(JSON.stringify(rel));
                source=JSON.parse(source);
                rel=source;
            }else if(typeof(rel) == "string"){
                source=this.htmlEncodeOut(rel);
                rel=source;
            }
            return rel;
        },
        //xss defence end
        
        initExportUser: function() {
            $('#exportUser button,#exportTDownload button').click(function(e) {
                if ($(this).hasClass('disabled')) {
                    return false;
                }
                if ($('#exportUser').length > 0) {
                    $('#exportUser')[0].submit()
                } else {
                    $('#exportTDownload')[0].submit()
                }

                return false;
            });
        },
        pageActive: function() {
            var title = $('.g-top-title').val(),
                changeObj;
            changeObj = { //因为title是大写，需要转化
                'DASHBOARD': 'Dashboards',
                'REPORTS': 'Reports',
                'TERMINAL': 'Terminals',
                'PACKAGE': 'Packages',
                'OFFLINEKEY': 'Keys',
                'ALERT': 'Alerts',
                'EVENT': 'Events'
            }
            $('.g-menu-nav >li').removeClass('active');
            $('.g-menu-nav >li[title="' + changeObj[title] + '"]').addClass('active');
        },
        bindEvent: function() {
            // 隐藏显示菜单
            var self = this;
            //add focus style
            if (this.isPC()) {
                $('.firstFocus').focus();
            }
            //under PAX
            if(_ACTIVE_GROUP_ == 1) {
            	//hide alert off and manage condition link
            	$('.g-menu-nav').find('li[title="Alerts"]').find('li[title="Alert Off"]').addClass('hide');
            	$('.g-menu-nav').find('li[title="Alerts"]').find('li[title="Manage Conditions"]').addClass('hide');
            } else {
            	$('.g-menu-nav').find('li[title="Alerts"]').find('li[title="Alert Off"]').removeClass('hide');
            	$('.g-menu-nav').find('li[title="Alerts"]').find('li[title="Manage Conditions"]').removeClass('hide');
            }
            $('.datetime-icon').click(function() {
            	$(this).prev().click();	
            })
            //add pointer style to all select file
            /*$('select:not[disabled]').css('cursor','pointer');*/
            //$('input:not(input[name="groupname"])').addClass('specialInput');
            
           
            $('input[type=checkbox]').css('cursor', 'pointer');
            $('.show-group-btn').click(function() {
                $('.g-left-content,.g-body-content').addClass(
                    'toggle-amin').addClass('amin');
                $(this).hide();
                $('.hide-group-btn').show();
            });
            $('.hide-group-btn').click(function() {
                $('.g-left-content,.g-body-content').removeClass(
                    'toggle-amin');
                $(this).hide();
                $('.show-group-btn').show();
            });
            $('.g-top-groupbtn').click(function() {
                $('#group-tree').modal('show');
                self.tree.init();
            });

            if (!this.isPC()) {
                $('.g-container')
                    .on(
                        'click',
                        '.g-middle-content input.form-control,.g-middle-content input.form-input,.g-middle-content textarea.form-input',
                        function(event) { // fix bug 安卓机上滚动bug
                            var scrollVal = $(this).offset().top + $('.g-middle-content')
                                .scrollTop();
                            $('.g-middle-content').scrollTop(
                                scrollVal - 100);
                        });
            }
            $('.menu-navbar-toggle').click(function() {
                $('.bs-js-setting-collapse').collapse('hide');
                $('.about-pxmaster-collapse').collapse('hide');
                $(this).toggleClass('active');
                $('.setting-navbar-toggle').removeClass('active');
                $('.about-pxmaster-navbar-toggle').removeClass('active');
            });
            $('.setting-navbar-toggle').click(function() {
                $('.bs-js-navbar-collapse').collapse('hide');
                $('.about-pxmaster-collapse').collapse('hide');
                $(this).toggleClass('active');
                $('.menu-navbar-toggle').removeClass('active');
                $('.about-pxmaster-navbar-toggle').removeClass('active');

            });
            $('.about-pxmaster-navbar-toggle').click(function() {
                $('.bs-js-setting-collapse').collapse('hide');
                $('.bs-js-navbar-collapse').collapse('hide');
                $(this).toggleClass('active');
                $('.setting-navbar-toggle').removeClass('active');
                $('.menu-navbar-toggle').removeClass('active');
            })
            $('.g-menu-nav li').on('mouseout', function() {
                $(this).removeClass("open")
            })
            $('.g-top-right div').on('mouseout', function() {
                    $(this).removeClass("open")
            })
            $('.g-menu-nav>li>a,.g-user-box>a').on('click', function() {
                if(!TMS.isPC()){
                	$('.g-menu-nav ul,.g-user-box ul').hide()
                    $(this).siblings('ul').toggle()
                }
            })
            $('.g-menu-nav>li>a,.g-user-box>a').on('mouseenter', function() {
                if(TMS.isPC()){
                	$('.g-menu-nav ul,.g-user-box ul').hide()
                    $(this).siblings('ul').toggle()
                }
            })
                //timezone change,enable disable the daylight saving
            $('#timeZoneId').change(function() {
            	//checke current URL,in deploy page, there is no daylightSaving item
            	if(_URL_.indexOf('groupDeploy/toDeploy')<0 && _URL_.indexOf('terminalDeploy/toDeploy')<0){
            		var isDaylightSaving = $(this).find('option:selected').attr('data-isDaylightSaving');
                    if (isDaylightSaving == '1' || isDaylightSaving == undefined) {
                        $('select[name="daylightSaving"]').removeAttr('disabled');
                        $('select[name="daylightSaving"] option')[1].selected = true;
                        $('select[name="daylightSaving"]').css('cursor', 'pointer');

                    } else {
                        $('select[name="daylightSaving"]').attr('disabled', 'true');
                        $('select[name="daylightSaving"] option')[0].selected = true;
                        $('select[name="daylightSaving"]').css('cursor', 'not-allowed');
                    }
            	}
                
            });
            
            $('.sysinfoBtn a').click(function() {
            	//移除之前的消息提示
        		TMS.removeAlertdanger($('.bootstrap-growl .alert'));
    			var self = this;
    		      $.ajax({
    	                global: false,
    	                type: 'GET',
    	                dataType: "json",
    	              
    	            	url: _WEB_SITE_ + '/ppmConfiguration/list',
    	                cache: false,
    	                data: {},
    	                success: function(data) {
    	                	data=TMS.dataEncodeOut(data);
    	                	if (!data.statusCode || data.statusCode && data.statusCode == 200){
    	                		var arr = [];   
    	                		for(var key in data) {
    	                			arr.push('<h5 class="sysinfoh5">'+key+'</h5>');
    	                			var obj = data[key];
    	                			arr.push('<ul class="sysul">');
    	                			for(var key in obj){
    	                				arr.push('<li>');
    	                				arr.push('<label>'+key+'</label>');
    	                				arr.push('<span> &nbsp;&nbsp;'+obj[key]+'</span>');
    	                				arr.push('</li>');
    	                			}
    	                			arr.push('</ul>');
    	                		}
    	                		$('#sysinfo .modal-body').html(arr.join(''));
    	                		$('#sysinfo').modal('show');
    	                	}
    	                   
    	                },
    	                error: function() {
    	                	TMS.alert('Network Instability','danger');
    	                }

    	            });
            });



        },

        setProvince: function(countryDefault, provinceDefault, provinceId) {
            var $provinceId = $('#provinceId');
            var token = {};
            token[_TOKEN_HEADER_NAME_] = _TOKEN_;
            $.ajax({
                global: false,
                type: 'POST',
                dataType: "json",
                url: _WEB_SITE_ + "/location/province/select?countryId=" + encodeURIComponent(countryDefault),
                cache: false,
                data: {},
                headers: token,
                success: function(json) {
                	json=TMS.dataEncodeOut(json);
                    if (!json)
                        return;
                    if (!provinceDefault) {
                        //省无值
                        $provinceId.empty();
                        /* $provinceId.append($('<option></option>').val('').text(
                            "--Please Select--")); */
                        $.each(json, function(i) {
                            if (json[i] && json[i].length > 1) {
                                $provinceId.append($("<option></option>").val(
                                    json[i][0]).html(json[i][1]));
                            }
                        });
                        $provinceId.trigger("change");


                    } else {
                        //省有值

                        $provinceId.empty();
                        $provinceId.append($('<option></option>').val(provinceId).text(
                            provinceDefault));
                        $.each(json, function(i) {
                            if (json[i][0] != provinceId) {
                                if (json[i] && json[i].length > 1) {
                                    $provinceId.append($("<option></option>").val(
                                        json[i][0]).html(json[i][1]));
                                }
                            }

                        });
                        $provinceId.trigger("change");
                    }

                },
                error: function() {

                }

            });


        },
        setUserProvince: function(countryDefault, provinceDefault, provinceId) {
            var $provinceId = $('#provinceId');
            var token = {};
            token[_TOKEN_HEADER_NAME_] = _TOKEN_;
            $.ajax({
                global: false,
                type: 'POST',
                dataType: "json",
                url: _WEB_SITE_ + "/location/province/select?countryId=" + encodeURIComponent(countryDefault),
                cache: false,
                data: {},
                headers: token,
                success: function(json) {
                	json=TMS.dataEncodeOut(json);
                    if (!json)
                        return;
                    if (!provinceDefault) {
                        //省无值
                        $provinceId.empty();
                        $provinceId.append($('<option></option>').val('').text(
                            ""));
                        $.each(json, function(i) {
                            if (json[i] && json[i].length > 1) {
                                $provinceId.append($("<option></option>").val(
                                    json[i][0]).html(json[i][1]));
                            }
                        });
                        $provinceId.trigger("change");


                    } else {
                        //省有值

                        $provinceId.empty();
                        $provinceId.append($('<option></option>').val(provinceId).text(
                            provinceDefault));
                        $.each(json, function(i) {
                            if (json[i][0] != provinceId) {
                                if (json[i] && json[i].length > 1) {
                                    $provinceId.append($("<option></option>").val(
                                        json[i][0]).html(json[i][1]));
                                }
                            }

                        });
                        $provinceId.trigger("change");
                    }

                },
                error: function() {

                }

            });


        },
        setArea: function(country, province, timeZone) { // 国家省市城市联动
            var $countryId = $(country),
                $provinceId = $(province),
                $timeZoneId = $(timeZone);
            var token = {};
            token[_TOKEN_HEADER_NAME_] = _TOKEN_;
            $countryId.change(function() {
                if (!$countryId.val()) {
                    $provinceId.empty();
                    $timeZoneId.empty();
                    /* $provinceId.append($('<option></option>').val('').text(
                        "--Please Select--")); */
                    $timeZoneId.append($('<option></option>').val('').text(
                        "--Please Select--"));
                }
                $.ajax({
                    global: false,
                    type: 'POST',
                    dataType: "json",
                    url: _WEB_SITE_ + "/location/province/select?countryId=" + encodeURIComponent($countryId.val()),
                    cache: false,
                    data: {},
                    headers: token,
                    success: function(json) {
                    	json=TMS.dataEncodeOut(json);
                        if (!json)
                            return;
                        $provinceId.empty();
                        /* $provinceId.append($('<option></option>').val('').text(
                            "--Please Select--")); */
                        $.each(json, function(i) {
                            if (json[i] && json[i].length > 1) {
                                $provinceId.append($("<option></option>").val(
                                    json[i][0]).html(json[i][1]));
                                $timeZoneId.append($("<option></option>").val(
                                    json[i][0]).text(json[i][1]));
                            }
                        });
                        $provinceId.trigger("change");
                        $timeZoneId.trigger("change");
                    },
                    error: function() {

                    }

                });
            });
        },
        setUserArea: function(country, province, timeZone) { // 国家省市城市联动
            var $countryId = $(country),
                $provinceId = $(province),
                $timeZoneId = $(timeZone);
            var token = {};
            token[_TOKEN_HEADER_NAME_] = _TOKEN_;
            $countryId.change(function() {
                if (!$countryId.val()) {
                    $provinceId.empty();
                    $timeZoneId.empty();
                    $provinceId.append($('<option></option>').val('').text(
                        ""));
                    $timeZoneId.append($('<option></option>').val('').text(
                        "--Please Select--"));
                }
                $.ajax({
                    global: false,
                    type: 'POST',
                    dataType: "json",
                    url: _WEB_SITE_ + "/location/province/select?countryId=" + encodeURIComponent($countryId.val()),
                    cache: false,
                    data: {},
                    headers: token,
                    success: function(json) {
                    	json=TMS.dataEncodeOut(json);
                        if (!json)
                            return;
                        $provinceId.empty();
                        $provinceId.append($('<option></option>').val('').text(
                            ""));
                        $.each(json, function(i) {
                            if (json[i] && json[i].length > 1) {
                                $provinceId.append($("<option></option>").val(
                                    json[i][0]).html(json[i][1]));
                                $timeZoneId.append($("<option></option>").val(
                                    json[i][0]).text(json[i][1]));
                            }
                        });
                        $provinceId.trigger("change");
                        $timeZoneId.trigger("change");
                    },
                    error: function() {

                    }

                });
            });
        },
        setAreatimeZone: function(country, province, timeZone) { // 国家省timezone联动
            var $country = $(country),
                $province = $(province),
                $timeZone = $(timeZone);
            var token = {};
            token[_TOKEN_HEADER_NAME_] = _TOKEN_;
            $country.change(function() {
                $('#cityId').val('');
                $('#zipcode').val('');
                if (!$country.val()) {
                    $province.empty();
                    $timeZone.empty();
                    /* $province.append($('<option></option>').val('').text(
                        "--Please Select--")); */
                    $timeZone.append($('<option></option>').val('').text(
                        "--Please Select--"));
                }
                $.ajax({
                    global: false,
                    type: 'POST',
                    dataType: "json",
                    url: _WEB_SITE_ + "/address/service/getTimeZones?countryId=" + encodeURIComponent($country.val()),
                    cache: false,
                    data: {},
                    headers: token,
                    success: function(json) {
                    	json=TMS.dataEncodeOut(json);
                        if (!json)
                            return;
                        $province.empty();
                        /* $province.append($('<option></option>').val('').text(
                            "--Please Select--")); */
                        $timeZone.empty();
                        $timeZone.append($('<option></option>').val('').text(
                            "--Please Select--"));
                        var provinceList = json.provinceList;
                        var timezoneList = json.timeZoneList;
                        for (var i = 0; i < provinceList.length; i++) {
                            $province.append($("<option></option>").val(provinceList[i][0]).html(provinceList[i][1]));
                        }
                        for (var i = 0; i < timezoneList.length; i++) {
                            $timeZone.append($("<option data-isDaylightSaving=" + timezoneList[i].isDaylightSaving + "></option>").val(timezoneList[i].timeZoneId).text(timezoneList[i].timeZoneName));
                        }
                        $province.trigger("change");
                        $timeZone.trigger("change");
                    },
                    error: function() {

                    }

                });
            });
        },
        bootTable: function(el, option) { // table
            var self = this,
                $el = $(el),
                isPc = self.isPC(),
                isPad = self
                .isPad();
            var _option = {
                icons: { // 自定义图标
                    'detailOpen': 'glyphicon-chevron-down',
                    'detailClose': 'glyphicon-chevron-up',
                    'refresh': 'glyphicon-refresh icon-refresh'
                },
                buttonsClass: 'primary',
                classes: 'table fade',
                queryParamsType: '',
                sidePagination: 'server',
                cache: false,
                maintainSelected: true,
                queryParams: function(params) { // 接口参数处理
                    return params
                },
                responseHandler: function(res) { // 接口数据处理
                	res=TMS.dataEncodeOut(res);
                    window.initialTable = false;
                    $.each(res.items, function(i, row) {
                        var nowTsn = row.tsn || row.id;
                        row.checkStatus = selectionIds[nowTsn] != undefined; //判断当前行的数据id是否存在与选中的数组，存在则将多选框状态变为true  

                    });
                    this.data = res.items;
                    res.total = res.totalCount;
                    //判断是否可以操作按钮
                    var arr = [],
                        activeNum = 0;

                    for (var key in selectionIds) {
                        if (selectionIds[key].status) {
                            activeNum++
                        }
                        arr.push(selectionIds[key])

                    }
                    if (arr.length > 1) {
                        setTimeout(function() {
                            $('.select-box .btn').removeClass('disabled');
                            $('.J-select-copy').addClass('disabled')
                            activeNum == arr.length ? $('.J-select-activate').addClass('disabled') : activeNum == 0 ? $('.J-select-deactivate').addClass('disabled') : null;
                        }, 20)
                    } else if (arr.length == 1) {
                        setTimeout(function() {
                            $('.select-box .btn').removeClass('disabled');
                            arr[0].status ? $('.J-select-activate').addClass('disabled') : $('.J-select-deactivate').addClass('disabled');
                        }, 20)
                    }

                    return res;
                },
                pagination: true,
                searchOnEnterKey: true
            };
            //定义选中数组
            window.selectionIds = [];
            //选中事件操作数组  
            var union = function(array, objs) {
                $.each(objs, function(i, obj) {
                    id = obj.tsn || obj.id;
                    if (array[id] == undefined) {
                        array[id] = obj;
                    }
                });
                return array;
            };
            //取消选中事件操作数组  
            var difference = function(array, objs) {
                $.each(objs, function(i, obj) {
                    id = obj.tsn || obj.id;

                    if (array[id] != undefined) {
                        delete array[id]
                    }
                });
                return array;
            };
            var _ = { "union": union, "difference": difference };
            $.fn.bootstrapTable.locales['en-US'].formatLoadingMessage = function() { // 加载中文案
                return 'loading';
            }
            $.fn.bootstrapTable.locales['en-US'].formatSearch = function() { // 加载中文案
                return 'Search here';
            }

            $.fn.bootstrapTable.locales['en-US'].formatNoMatches = function() { // 无数据文案
                return "<div class='alert alert-info paxinfonext'><span  class='paxspannext'><strong>Hint:</strong>There is no data!</span></div>"
            }


            $.extend($.fn.bootstrapTable.defaults,
                $.fn.bootstrapTable.locales['en-US'])
            $el.bootstrapTable($.extend({}, _option, option));

            $('html').off().on('click', '.table-reload', function(event) {
                $el.bootstrapTable('refresh');
            });
            $el.on('load-error.bs.table', function(e, data) {

                $el.find('.no-records-found td').html("<div class='alert alert-danger'><span class='paxspan'><strong>Hint:</strong>there is an error in your request!</span></div>");
            });
            $el.on('check.bs.table check-all.bs.table uncheck.bs.table uncheck-all.bs.table', function(e, rows) {
                if (e.type == 'check-all') {
                    rows = $('#table').bootstrapTable('getAllSelections')
                    $('.fixed-table-body .detail-view input').prop('checked',true);                    
                }else if(e.type =='uncheck-all'){
                    $('.fixed-table-body .detail-view input').prop('checked',false);
                }
                var ids = $.map(!$.isArray(rows) ? [rows] : rows, function(row) {
                    return row;
                });
                func = $.inArray(e.type, ['check', 'check-all']) > -1 ? 'union' : 'difference';
                selectionIds = _[func](selectionIds, ids);
                $(".bs-checkbox input").trigger('change')
            });


            $el.on('load-success.bs.table',
                function(e, data) {
                    if (data.statusCode !== undefined && data.statusCode != 200) {
                        // error
                        $(el).find('.no-records-found td').html("<div class='alert alert-info paxinfonext'><span  class='paxspannext'><strong>Hint:</strong>" + data.message + "</span></div>");
                    } else if (data.items.length == 0) {
                        $(el).find('.no-records-found td').html("<div class='alert alert-info paxinfonext'><span  class='paxspannext'><strong>Hint:</strong>There is no data!</span></div>");
                    }
                });
            if (option.autocomplete) {
                require.async('jquery-ui', function() {
                    $('.bootstrap-table .search input').autocomplete({
                        source: option.autocompleteUrl,
                        appendTo: ".g-middle-content"
                    });
                })
            }
            window.initialTable = true;

        },
        alert: function(message, type) {
            $.bootstrapGrowl(message, {
                type: type,
                offset: {
                    from: 'top',
                    amount: 75
                },
                width: 'auto',
                delay: 2000
            });
        },
        resultHandle: function(json) {
            var self = this;
            if (!json.statusCode || json.statusCode && json.statusCode == 200) {
                // success
                self.alert(json.message, 'success')
            } else {
                // error
                self.alert(json.message, 'danger')
            }
        },
        getData: function(option, callback) { // ajax调用方法
            var self = this;
            var contentType = option.contentType;
            var token = {};
            token[_TOKEN_HEADER_NAME_] = _TOKEN_;
            if (option.loading == undefined || option.loading == true) {
                self.loading.show();
            }
            if ((location.pathname.indexOf('terminal/toImport') != -1 && location.pathname.indexOf('group/toImport') != -1)) {
                TMS.removeAlertdanger($('.bootstrap-growl .alert'));
            }
            if (contentType !== undefined) {

                $
                    .ajax({
                        url: option.url,
                        type: option.type,
                        dataType: 'json',
                        contentType: contentType,
                        data: option.data ? option.data : {},
                        headers: token,
                        success: function(data) {
                        	data=TMS.dataEncodeOut(data);
                            if (!window.initialTable) { //table没有在加载中时 才去除loading图标
                                self.loading.hide();
                            }

                            if (callback) {
                                callback(data);
                                if (option.followTo && (data.statusCode == 200 || data.statusCode == undefined)) {
                                    setTimeout(
                                        function() {
                                            window.location.href = option.followTo;
                                        }, 2000);
                                }
                            } else {
                                if (data.statusCode && data.statusCode != '200') {
                                    if (data.message) {
                                        self.alert(data.message, 'danger');
                                    } else {
                                    	
                                        self.alert('Network instability',
                                            'danger');

                                    }
                                    return false;
                                } else {
                                    TMS.alert(data.message, 'success');
                                    if (option.followTo) {
                                        setTimeout(
                                            function() {
                                                window.location.href = option.followTo;
                                            }, 2000);
                                    }
                                }


                            }

                        },
                        error: function(XMLHttpRequest, textStatus,
                            errorThrown) {
                        	if(XMLHttpRequest && (XMLHttpRequest.status == '401'  ||XMLHttpRequest.readyState=='0') ) {
                        		window.location.reload();
                        	} else {
                        		self.alert('Network instability', 'danger');
                        	}
                            
                            self.loading.hide();
                        }
                    });
            } else {
                $
                    .ajax({
                        url: option.url,
                        type: option.type,
                        dataType: option.dataType?option.dataType:'json',
                        data: option.data ? option.data : {},
                        headers: token,
                        success: function(data) {
                        	data=TMS.dataEncodeOut(data);
                            if (!window.initialTable) {
                                self.loading.hide();
                            }
                            if (callback) {
                                callback(data);
                                if (option.followTo && (data.statusCode == 200 || data.statusCode == undefined)) {
                                    setTimeout(
                                        function() {
                                            window.location.href = option.followTo;
                                        }, 2000);
                                }
                            } else {
                                if (data.statusCode && data.statusCode != '200') {
                                    if (data.message) {
                                        self.alert(data.message, 'danger');
                                    } else {
                                        self.alert('Network instability',
                                            'danger');

                                    }
                                    return false;
                                } else {
                                    TMS.alert(data.message, 'success');
                                    if (option.followTo) {
                                        setTimeout(
                                            function() {
                                                window.location.href = option.followTo;
                                            }, 2000);
                                    }
                                }
                            }
                        },
                        error: function(XMLHttpRequest, textStatus,
                            errorThrown) {
                        	if(XMLHttpRequest && (XMLHttpRequest.status == '401'||XMLHttpRequest.readyState=='0') ) {
                        		window.location.reload()
                        	} else {
                        		self.alert('Network instability', 'danger');
                        	}
                            
                            self.loading.hide();
                        }
                    });
            }

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
        actionMenuEvent: function(callback, showcallback) {
            this.$body.on('click', ".g-action-menu a", function() {
                callback && callback(this)
            });
            this.$body.on('click', '.g-action-btn', function(event) {

                $('.g-action .g-action-btn').removeClass('active');
                $('.g-action .g-action-menu').hide();
                $('.g-action .g-action-menu').css({
                    'width': "0px"
                });
                $(this).toggleClass('active');
                var $menu = $(this).siblings('.g-action-menu'),
                    w = 0;
                $menu.toggle();
                $menu.find('li').each(function(index, el) {
                    w += $(el).outerWidth(true);
                });
                w = w + 2;
                w = Math.ceil(w);
                $menu.css({
                    'width': w + 'px'
                });
                event.stopPropagation();
                showcallback && showcallback(this);
            });
            this.$body.click(function() {
                $('.g-action .g-action-btn').removeClass('active');
                $('.g-action .g-action-menu').hide();
                $('.g-action .g-action-menu').css({
                    'width': "0px"
                });
            })
        }

    };
    /**
     * 左侧group树方法
     * @param  {string} el html钩子
     */
    TMS.leftTree = function(el) {
        this.el = el;
    }
    TMS.leftTree.prototype = {
        init: function() {
            this.defaultTree(_ACTIVE_GROUP_);
            this.bindEvent();
            return this;
        },
        refresh: function() {
            this.defaultTree(_ACTIVE_GROUP_);
        },
        bindEvent: function() {
            var self = this;
            TMS.$body.on('click', self.el + ' .notend', function() { //group下拉事件
                var $target = $(this),
                    id = $target.closest('.group-items').attr('data-id');
                if ($target.attr('data-isAjax') == '0') {
                    var id = $(this).parent().parent().attr('data-id');
                    self.getChildTree(id, $target);
                } else if ($target.hasClass('h')) {
                    $target.parent().siblings('.g-group-menu').fadeIn();
                    $target.removeClass('h').addClass('s');
                } else {
                    $target.parent().siblings('.g-group-menu').fadeOut();
                    $target.removeClass('s').addClass('h');
                }

            });
        },
        defaultTree: function(id) {
            var self = this,
                data;
            if (id) {
                data = {
                    groupId: id,
                    pageIndex: 1,
                    pageSize: 20000
                };
            } else {
                data = {
                    pageIndex: 1,
                    pageSize: 20000
                };
            }
            TMS.getData({
                url: _WEB_SITE_ + '/group/service/tree/groupContext',
                type: 'get',
                data: data,
                loading: false,
            }, function(data) {
//            	data=TMS.dataEncodeOut(data);
                if (data.statusCode !== undefined && data.statusCode != '200') {
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    TMS.alert(data.message, 'danger');

                } else {
                    self.activeGroupId = data.activeGroup;
                    if (data.items.length == 0) {
                        self.renderNull();
                    } else {
                        self.renderDefaultGroup(data.items, $(self.el), 0);
                    }

                }

            });


        },
        getChildTree: function(id, $el) {
            var self = this;
            TMS.getData({
                url: _WEB_SITE_ + '/group/service/tree/subgroup',
                type: 'get',
                dataType: 'json',
                data: {
                    groupId: id
                },
            }, function(data) {
//            	data=TMS.dataEncodeOut(data);
                if (data.statusCode != undefined && data.statusCode != '200') {
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    TMS.alert(data.message, 'danger')
                } else {
                    var $thisTree = $el.parent().siblings('.g-group-menu');
                    self.renderDefaultGroup(data.items, $thisTree, Number($el.closest('.group-items').attr('data-level')) + 1);
                    $el.attr('data-isAjax', '1');
                    $el.removeClass('h').addClass('s');
                }

            })
        },
        renderNull: function(el) {
            $(this.el).html("<li>There is no group!</li>")
        },
        renderDefaultGroup: function(data, $el, level) {
            var temp = [],
                self = this,
                level = level, //层级
                detail = '';
            if (!data || data.length == 0) {
                return false
            }
            var _CURRENT_URL; //url 重定向
            if (_URL_ == '/terminalDeploy/list/') {
                _CURRENT_URL = _WEB_SITE_ + '/groupDeploy/list/';
            } else if (_URL_ == '/terminalDeploy/toDeploy/') {
                _CURRENT_URL = _WEB_SITE_ + '/groupDeploy/toDeploy/';
            } else if (_URL_ == '/myProfile/view/' || _URL_ == '/myProfile/toChangePassword/') {
                _CURRENT_URL = _WEB_SITE_ + '/index/';
            } else {
                _CURRENT_URL = _ACTIVE_URL_;
            }
            this._CURRENT_URL = _CURRENT_URL;
            temp.push(self.pushChild(data, level));
            $el.html(temp.join('')).fadeIn();
        },
        pushChild: function(data, level) {
            var temp = [],
                self = this,
                level = level;

            for (var i = 0; i < data.length; i++) {
                var hasChild = false;
                var showChild = false;
                var _data = data[i];
                temp.push('<li class="group-items" data-id="' + _data.id + '" data-level="' + level + '">');
                var paddingL = level * 15 + 30
                var style = 'padding-left:' + paddingL + 'px';
                if (self.activeGroupId == _data.id) {
                    temp.push('<div class="group-item active" style="' + style + '">');
                } else {
                    temp.push('<div class="group-item" style="' + style + '">');
                }
                if (_data.subgroup && _data.subgroup.items.length != 0) {
                    showChild = true;
                }
                if (_data.subCount != 0) {
                    hasChild = true;
                }

                if (hasChild) {
                    if (showChild) {
                        temp.push('<i class="iconfont notend s">&#xe66e;</i>');
                    } else {
                        temp.push('<i class="iconfont notend h" data-isAjax="0">&#xe66e;</i>');
                    }
                } else {
                    temp.push('<i class="iconfont end">&#xe66f;</i>');

                }
                temp.push('<a href="' + this._CURRENT_URL + _data.id + '">' + _data.name + '</a>');
                temp.push('</div>');
                if (showChild) {
                    temp.push('<ul class="g-group-menu">');
                    temp.push(self.pushChild(_data.subgroup.items, level + 1));
                    temp.push('</ul>');
                } else {
                    temp.push('<ul class="g-group-menu" style="display:none"></ul>');
                }
                temp.push('</li>');
            }
            return temp.join('');

        }
    }

    /**
     * group树
     * @param  {string} el html钩子
     * @param  {object} option 配置参数
     */
    TMS.groupTree = function(el, option) {
        this.el = el;
        this.defaultNamePath = ''; // 当前路径
        this.option = $.extend([], TMS.groupTree.defaults, option);
        this.bindEvent();
        if (this.option.groupPanel.display) {
            this.bindGroupPanelEvent();
        }
    }
    TMS.groupTree.defaults = {
        nowGroup: true, //当前页面group 是否高亮显示
        checkBox: true, //是否显示选择按钮
        multiselect: false, //是否多选
        childselect: true, //父节点选中时子节点全部选中
        loadAll: false, //是否张开全部树
        filterChildGroup: false, //是否过滤子集
        allowSelectOtherEGroup: true, //是否允许选择不同enterprise group
        activeGroup: { //group 当前选择高亮
            display: false,
            items: []
        },
        existedIds: [],
        errorMessage: 'Cannot select more than one enterprise group.',
        treeGroupUrl: _WEB_SITE_ + "/group/service/tree/groupContext",
        isModal: false, //是否是modal框
        operator: false,
        groupPanel: { //group 选择框
            display: false,
            titleShow: true,
            el: '.group_chooseG'
        },
        renderGroupPanel: function(obj) {
            return obj;
        },
        cancel: undefined, //关闭回调
        confirm: undefined //确认回调
    }
    TMS.groupTree.prototype = {
        init: function(activeGroup) {
            if (activeGroup) {
                this.defaultTree(activeGroup);
            } else {
                this.defaultTree(_ACTIVE_GROUP_);
            }
            this.renderGroupPath();

            return this;
        },
        getActiveGroup: function() {
            return this.option.activeGroup.items;
        },
        initGroupPanel: function() {
            var self = this;
            self.renderPanelGroupSelect();
        },
        bindGroupPanelEvent: function() {
            var self = this;
            TMS.$body.on('click', '.group_chooseG_bottom', function() {
                if ($(this).parents('.group_chooseG').find('.group_note .group_note_items')) {
                    var existedIds = [];
                    $(this).parents('.group_chooseG').find('.group_note .group_note_items').each(function(index) {
                        existedIds.push($(this).attr('data-id'));
                        self.option.existedIds = existedIds
                    });

                }
                self.init();

            });
            TMS.$body.on('click', '.group_note_delete', function(event) {
                var id = $(this).parents('.group_note_items').attr('data-id');
                $(this).parents('.group_note_items').remove();
                self.deletePanelGroup(id);
            });
        },
        deletePanelGroup: function(id) {
            var self = this,
                items = self.option.activeGroup.items;
            for (var i = 0; i < items.length; i++) {
                if (id == items[i].id) {
                    items.splice(i, 1);
                    break;
                }
            }
        },
        renderPanelGroupSelect: function() {
            var self = this,
                items = self.option.activeGroup.items,
                temp = [];
            if (self.option.groupPanel.titleShow) {
                temp.push('<span class="font14px black normal pull-left">Group:&nbsp;&nbsp;</span>');
            }
            for (var i = 0; i < items.length; i++) {
                temp.push('<span class="group_note_items" data-id="' + items[i].id + '" title="' + items[i].path + '">' + items[i].name + '<span class="group_note_delete" title="delete">x</span><input type="checkbox" name="groupIds" hidden checked value="' + items[i].id + '"/></span>');
            }
            $('.group_note').html(temp.join(''));
        },
        defaultTree: function(id) {
            var self = this,
                data,
                isLoadAll = 0;
            if (self.option.loadAll) {
                isLoadAll = 1
            }
            if (id) {
                data = {
                    groupId: id,
                    pageIndex: 1,
                    pageSize: 20000,
                    loadAll: isLoadAll
                };
            } else {
                data = {
                    pageIndex: 1,
                    pageSize: 20000,
                    loadAll: isLoadAll
                };
            }

            TMS.getData({
                url: self.option.treeGroupUrl,
                type: 'get',
                data: data,
            }, function(data) {
//            	data=TMS.dataEncodeOut(data);
                if (data.statusCode !== undefined && data.statusCode != '200') {
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    TMS.alert(data.message, 'danger');
                    self.renderError('.group-tree-content');
                } else {
                    self.activeGroupId = data.activeGroup;

                    if (data.items.length == 0) {
                        self.renderNull('.group-tree-content');
                    } else {
                        self.renderDefaultGroup(data.items, '.group-tree-content');
                    }

                    $('.group-tree-content').removeClass('hide');
                    $('.group-tree-search').addClass('hide');

                }
                if (self.option.isModal) {
                    $(self.el).modal('show');
                }

            });


        },
        renderNull: function(el) {
            $(this.el + ' .group-tree-body>' + el).html("<div class='alert alert-info paxinfonext'><span  class='paxspannext'><strong>Hint:</strong>There is no group!</span></div>")
        },
        renderError: function(el) {
            $(this.el + ' .group-tree-body>' + el).html('');
        },
        searchTree: function(keyword) {
            var self = this;
            TMS.getData({
                url: _WEB_SITE_ + '/group/service/tree/search',
                type: 'get',
                dataType: 'json',
                data: {
                    keyword: keyword,
                    groupId: _ACTIVE_GROUP_
                },
            }, function(data) {
//            	data=TMS.dataEncodeOut(data);
                if (data.statusCode != undefined && data.statusCode != '200') {
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    TMS.alert(data.message, 'danger')
                } else {
                    if (data.items.length == 0) {
                        self.renderNull('.group-tree-search');
                    } else {
                        self.renderDefaultGroup(data.items, '.group-tree-search');
                    }
                    $(self.el + ' .group-tree-search').removeClass('hide');
                    $(self.el + ' .group-tree-content').addClass('hide');
                }

            });
        },
        activeGroup: function(id) {

            var self = this,
                temp;
            var existedIds = self.option.existedIds;

            function contains(id, existedIds) {
                var i = existedIds.length;
                while (i--) {
                    if (existedIds[i] == id) {
                        return true;
                    }
                }
                return false;
            }
            if ((self.option.nowGroup && this.activeGroupId == id) || contains(id, existedIds)) {
                temp = '<span class="A active"></span>';
            } else {
                temp = '<span class="A"></span>';
            }
            return temp;
        },
        renderGroupPath: function() {
            var self = this;
            if (!_NAME_PATH_ || !self.option.nowGroup) {
                return false;
            }
            var group = _NAME_PATH_.split('/'),
                arr = [];
            var flag = 0
            for (var i = 0; i < group.length; i++) {
                if (i == group.length - 1 && i != 0) {
                    arr.push('<span class="font14px" title = "' + group[i] + '"> > ' + group[i] + '</span>');

                } else if (i == group.length - 1 && i == 0) {
                    arr.push('<span class="font14px" title = "' + group[i] + '">' + group[i] + '</span>');
                } else if (i > 2 && i < group.length - 1) {

                    if (!flag) {
                        flag = 1;
                        arr.push('<span class="font14px"> > ...</span>');
                    }

                } else if (i == 0 && i != group.length - 1) {
                    arr.push('<span class="font14px grey" title = "' + group[i] + '" >' + group[i] + '</span>');
                } else {
                    arr.push('<span class="font14px grey" title = "' + group[i] + '" > > ' + group[i] + '</span>');
                }

            }
            $('.g-tree-bottom').html(arr.join(''));
        },
        renderDefaultGroup: function(data, renderEL) {
            var temp = [],
                self = this,
                detail = '';
            if (!data || data.length == 0) {
                return false
            }
            for (var i = 0; i < data.length; i++) {

                temp.push('<div class="g-tree tree-item" data-idpath="' + data[i].idPath + '" data-id="' + data[i].id + '" data-name="' + data[i].name + '" data-path="' + data[i].namePath + '">');
                temp.push('<div class="groupbox">');
                if (self.option.checkBox) {
                    temp.push('<span class="g-tree-circle">');
                    temp.push(self.activeGroup(data[i].id));
                    temp.push('</span>');
                }
                if (self.option.detail) {
                    detail = 'href="' + self.option.detail(data[i]) + '"';
                }
                if (data[i].name == 'PAX') {
                    temp.push('<a class="g-tree-parentName font16px bold black" href="javascript:;">');
                } else {
                    temp.push('<a class="g-tree-parentName font16px bold black" ' + detail + '>');
                }
                temp.push(data[i].name);


                var subCount = data[i].subCount;
                temp.push('<span class="font14px grey normal">(' + subCount + ')</span>');
                temp.push('</a>');
                if (subCount != 0) {

                    if (data[i].subgroup && data[i].subgroup.items && data[i].subgroup.items.length > 0) {
                        temp
                            .push('<span class="g-tree-toggling"><i class="glyphicon glyphicon-chevron-up"></i></span>')
                    } else {
                        temp
                            .push('<span class="g-tree-toggling" data-isAjax="0"><i class="glyphicon glyphicon-chevron-down"></i></span>')
                    }

                }
                if (self.option.operator && self.option.operatorHtml) {
                    temp.push(self.option.operatorHtml(data[i]));
                }
                temp.push('</div>');
                if (data[i].subgroup && data[i].subgroup.items.length != 0) {
                    temp.push('<ul class="g-tree-firstchild">');
                    for (var j = 0; j < data[i].subgroup.items.length; j++) {
                        var _data = data[i].subgroup.items[j];
                        temp.push('<li class="g-tree-item tree-item" data-idpath="' + _data.idPath + '" data-id="' + _data.id + '" data-name="' + _data.name + '" data-path="' + _data.namePath + '">');
                        temp.push('<div class="groupT-panel">');
                        if (self.option.checkBox) {
                            temp.push('<span class="g-tree-circle">');
                            temp.push(self.activeGroup(_data.id));
                            temp.push('</span>');
                        }
                        if (self.option.detail) {
                            detail = 'href="' + self.option.detail(_data) + '"';
                        }
                        temp.push('<a class="g-tree-parentName font14px" ' + detail + '>');
                        temp.push(_data.name);
                        var _subCount = _data.subCount
                        temp.push('<span class="font14px grey normal">(' + _subCount + ')</span>');
                        temp.push('</a>');
                        if (_subCount != 0) {
                            if (_data.subgroup && _data.subgroup.items.length > 0) {
                                temp
                                    .push('<span class="g-tree-toggling"><i class="glyphicon glyphicon-chevron-up"></i></span>')
                            } else {
                                temp
                                    .push('<span class="g-tree-toggling" data-isAjax="0"><i class="glyphicon glyphicon-chevron-down"></i></span>')
                            }

                        }
                        if (self.option.operator && self.option.operatorHtml) {
                            temp.push(self.option.operatorHtml(_data));
                        }
                        temp.push('</div>')
                        if (_data.subgroup && _data.subgroup.items.length != 0) {
                            temp.push(self.pushChild(_data.subgroup.items));
                        }
                        temp.push('</li>');
                    }
                    temp.push('</ul>');
                }
                temp.push('</div>');

            }
            $(this.el + ' .group-tree-body>' + renderEL).html(temp.join(''));
        },
        pushChild: function(data) {
            var self = this,
                temp = [],
                detail = '';
            temp.push('<ul class="groupT">');
            for (var j = 0; j < data.length; j++) {
                var _data = data[j];
                temp.push('<li class="tree-item" data-idpath="' + _data.idPath + '"  data-id="' + _data.id + '" data-name="' + _data.name + '" data-path="' + _data.namePath + '">');
                temp.push('<div class="groupT-panel">');
                if (self.option.checkBox) {
                    temp.push('<span class="g-tree-circle">');
                    temp.push(self.activeGroup(_data.id));
                    temp.push('</span>');
                }
                if (self.option.detail) {
                    detail = 'href="' + self.option.detail(_data) + '"';
                }
                temp.push('<a class="g-tree-parentName font14px" ' + detail + '>');
                temp.push(_data.name);
                var subCount = _data.subCount
                temp.push('<span class="font14px grey normal">(' + subCount + ')</span>');
                temp.push('</a>');
                if (subCount != 0) {
                    if (_data.subgroup && _data.subgroup.items.length > 0) {
                        temp
                            .push('<span class="g-tree-toggling"><i class="glyphicon glyphicon-chevron-up"></i></span>')
                    } else {
                        temp
                            .push('<span class="g-tree-toggling" data-isAjax="0"><i class="glyphicon glyphicon-chevron-down"></i></span>')
                    }
                }
                if (self.option.operator && self.option.operatorHtml) {
                    temp.push(self.option.operatorHtml(_data));
                }
                temp.push('</div>')
                if (_data.subgroup && _data.subgroup.items.length != 0) {
                    temp.push(self.pushChild(_data.subgroup.items));
                }
                temp.push('</li>');
            }
            temp.push('</ul>');
            return temp.join('');
        },
        renderGroup: function(id, data, $tree, isFitst) {
            if (!data || data.length == 0) {
                return false
            }
            var itemsCls = '',
                detail = '',
                ulCls = 'groupT',
                temp = [],
                self = this;
            if (isFitst) {
                itemsCls = 'g-tree-item';
                ulCls = 'g-tree-firstchild';
            }
            temp.push('<ul class="' + ulCls + '">');
            for (var j = 0; j < data.length; j++) {
                var _data = data[j];
                temp.push('<li class="' + itemsCls + ' tree-item" data-idpath="' + _data.idPath + '"  data-id="' + _data.id + '" data-name="' + _data.name + '" data-path="' + _data.namePath + '">');
                temp.push('<div class="groupT-panel">');
                if (self.option.checkBox) {
                    temp.push('<span class="g-tree-circle">');
                    temp.push(self.activeGroup(_data.id));
                    temp.push('</span>');
                }
                if (self.option.detail) {
                    detail = 'href="' + self.option.detail(_data) + '"';
                }
                temp.push('<a class="g-tree-parentName font14px" ' + detail + '>');
                temp.push(_data.name);
                var _subCount = _data.subCount
                temp.push('<span class="font14px grey normal">(' + _subCount + ')</span>');
                temp.push('</a>');
                if (_subCount != 0) {
                    if (_data.subgroup && _data.subgroup.items.length > 0) {
                        temp
                            .push('<span class="g-tree-toggling"><i class="glyphicon glyphicon-chevron-up"></i></span>')
                    } else {
                        temp
                            .push('<span class="g-tree-toggling" data-isAjax="0"><i class="glyphicon glyphicon-chevron-down"></i></span>')
                    }

                }
                if (self.option.operator && self.option.operatorHtml) {
                    temp.push(self.option.operatorHtml(_data));
                }
                temp.push('</div>')
                if (_data.subgroup && _data.subgroup.items.length != 0) {
                    temp.push(self.pushChild(_data.subgroup.items));
                }
                temp.push('</li>');
            }
            temp.push('</ul>');
            $tree.append(temp.join(''));

        },
        getChildTree: function(id, el, callback) {
            var self = this;
            TMS.getData({
                url: _WEB_SITE_ + '/group/service/tree/subgroup',
                type: 'get',
                dataType: 'json',
                data: {
                    groupId: id
                },
            }, function(data) {
                if (data.statusCode != undefined && data.statusCode != '200') {
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    TMS.alert(data.message, 'danger')
                } else {
                    var $thisTree = $(el).parent().parent();
                    if ($thisTree.hasClass('g-tree')) {

                        self.renderGroup(id, data.items, $thisTree, 1);
                    } else {

                        self.renderGroup(id, data.items, $thisTree, 0);
                    }
                    callback && callback();
                    $(el).attr('data-isAjax', '1');
                }

            })
        },
        reset: function() {
            var self = this;
            $(this.el + ' .group-tree-content').removeClass('hide');
            $(this.el + ' .group-tree-search').addClass('hide');
            $(this.el + ' .g-searchInput').val('');
        },
        renderPath: function(path) {
            var self = this;
            if (path == 'null' || !path) {
                $(self.el + ' .g-tree-bottom').html('');
                return false;
            }
            var path = path.split('/'),
                self = this;
            var temp = [],
                len = path.length;

            var flag = 0
            for (var i = 0; i < len; i++) {
                if (i == len - 1 && i != 0) {
                    temp.push('<span class="font14px" title = "' + path[i] + '"> > ' + path[i] + '</span>');

                } else if (i == len - 1 && i == 0) {
                    temp.push('<span class="font14px" title = "' + path[i] + '">' + path[i] + '</span>');
                } else if (i > 2 && i < len - 1) {

                    if (!flag) {
                        flag = 1;
                        temp.push('<span class="font14px"> > ...</span>');
                    }

                } else if (i == 0 && i != len - 1) {
                    temp.push('<span class="font14px grey" title = "' + path[i] + '" >' + path[i] + '</span>');
                } else {
                    temp.push('<span class="font14px grey" title = "' + path[i] + '" > > ' + path[i] + '</span>');
                }

            }
            temp = temp.join('');
            $(self.el + ' .g-tree-bottom').html(temp);

        },
        checkIsSelect: function(id, items) {
            var self = this,
                isSelected = false;
            for (var i = 0; i < items.length; i++) {
                if (items[i].id == id) {
                    isSelected = true;
                    break;
                }
            }
            return isSelected;
        },
        hebingGroup: function(items) { //合并组
            var _items = this.option.activeGroup.items,
                self = this;
            if (!self.option.multiselect) {
                return items;
            }
            for (var i = 0; i < _items.length; i++) {
                if (!self.checkIsSelect(_items[i].id, items)) {
                    items.push(_items[i]);
                }
            }
            return items;

        },
        filterGroup: function(items) { //过滤同父子集
            var self = this;
            if (!this.option.filterChildGroup) {
                return items;
            }
            for (var i = 0; i < items.length; i++) {
                for (var j = 0; j < items.length; j++) {
                    if (items[j].idPath.indexOf(items[i].idPath) != -1 && items[j] != items[i]) {
                        items.splice(j, 1);
                        return self.filterGroup(items);
                    }
                }
            }
            return items;
        },
        checkIsFromCommonGroup: function(items) { //判断是否选择了不同的顶级组
            var self = this,
                flag = true;
            if (items.length > 1) {
                var fgroup = items[0].idPath;
                var farr = fgroup.split('/');
                var fgroupPath = '';
                if (farr[0] && farr[1]) {
                    fgroupPath = farr[0] + '/' + farr[1];
                }
                for (var i = 1; i < items.length; i++) {
                    var _group = items[i].idPath;
                    var _arr = _group.split('/');
                    var _groupPath = '';
                    if (_arr[0] && _arr[1]) {
                        _groupPath = _arr[0] + '/' + _arr[1];
                    }
                    if (fgroupPath && _groupPath && fgroupPath != _groupPath) {
                        flag = false;
                        break;
                    }
                }
            }
            return flag;
        },
        bindEvent: function() {
            var self = this;
            TMS.$body.on('click', self.el + ' .g-tree-toggling', function() { //group下拉事件
                var _self = this;
                var $glyphicon = $(this).find('.glyphicon');
                if ($glyphicon.hasClass('glyphicon-chevron-up')) {
                    $(this).parent().siblings('.groupT').hide();
                    $(this).parent().siblings('.g-tree-firstchild').hide();
                    $glyphicon.removeClass('glyphicon-chevron-up').addClass(
                        'glyphicon-chevron-down');
                } else {
                    if ($(this).attr('data-isAjax') == '0') {
                        var id = $(this).parent().parent().attr('data-id');
                        self.getChildTree(id, this, function() {
                            if (self.option.multiselect && self.option.childselect && $(_self).siblings('.g-tree-circle').find('.A').hasClass('active')) {
                                $(_self).parent().siblings('.groupT,.g-tree-firstchild').find('.g-tree-circle .A').addClass('active')
                            }
                        });
                    } else {
                        $(this).parent().siblings('.groupT').show();
                        $(this).parent().siblings('.g-tree-firstchild').show();
                    }
                    $glyphicon.removeClass('glyphicon-chevron-down').addClass(
                        'glyphicon-chevron-up');

                }

            });
            TMS.$body.on('click', self.el + ' .g-tree-circle', function() { //group选择事件
                var $target = $(this).find('.A');
                if (!self.option.multiselect) { //copy move
                    $(self.el + ' .g-tree-circle .A').removeClass('active');
                    var namePath = $(this).parent().parent().attr('data-path');
                    self.renderPath(namePath);
                    if (self.option.selectUp) {
                        for (var i = 0; i < $(this).parents(".tree-item").length; i++) {
                            $(this).parents(".tree-item:eq(" + i + ")").find(">div .A").addClass('active');
                        }
                    } else {
                        $target.addClass('active');
                    }
                } else { //assign
                    if (self.option.selectUp) {

                        $(this).parent().parent().find("ul .A").removeClass("active");
                        for (var i = 0; i < $(this).parents(".tree-item").length; i++) {
                            if (i == 0) {
                                if ($(this).parents(".tree-item:eq(0)").find(">div .A").hasClass('active')) {
                                    $(this).parents(".tree-item:eq(0)").find(">div .A").removeClass('active')
                                } else {
                                    $(this).parents(".tree-item:eq(0)").find(">div .A").addClass('active')
                                }
                            } else {
                                $(this).parents(".tree-item:eq(" + i + ")").find(">div .A").addClass('active');
                            }


                        }
                        var namePath = $(this).parent().parent().attr('data-path');
                        self.renderPath(namePath);
                    } else {
                        if ($target.hasClass('active')) {
                            $target.removeClass('active');
                            self.renderPath('');
                        } else {

                            if (($(this).parent().siblings('.groupT') || $(this).parent().siblings('.g-tree-firstchild')) && self.option.childselect) {
                                $(this).parent().parent().find('.g-tree-circle .A').addClass('active');
                            } else {
                                $target.addClass('active');
                            }
                            var namePath = $(this).parent().parent().attr('data-path');
                            self.renderPath(namePath);
                        }
                    }
                }


            });
            TMS.$body.on('click', self.el + ' .g-tree-parentName', function() {
                var target = $(this).siblings('.g-tree-circle');
                if (target) {
                    target.trigger('click');
                }
            });
            $(self.el).on('hidden.bs.modal', function() { //group modal取消事件
                self.reset();
                if (self.option.cancel) {
                    self.option.cancel();
                    return;
                }
                $(self.el).modal('hide');
                if (!self.option.nowGroup) {
                    self.renderPath('');
                    return false
                }
                self.renderPath(self.defaultNamePath);
                $(self.el + ' .g-tree-circle .A').removeClass('active');
                $('.tree-item[data-id="' + self.activeGroupId + '"]' + '>.groupbox>.g-tree-circle>.A').addClass('active');
            });
            TMS.$body.on('click', self.el + ' .group-confirm-btn', function() { //group确认事件
                TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                var activeGroupItems = [];
                var $current = '';
                if ($(self.el).find('.group-tree-content').hasClass('hide')) {
                    $current = $(self.el).find('.group-tree-search');
                } else {
                    $current = $(self.el).find('.group-tree-content');
                }
                if (self.option.selectUp) { //往上选择
                    if (self.option.isassign) { //assign
                        $current.find('.g-tree-circle').each(function(index, el) {
                            if ($(el).find('.A').hasClass('active') && $(el).parent().parent().find("ul .A.active").length == 0) {
                                var id = $(el).closest('.tree-item').attr('data-id');
                                var idPath = $(el).closest('.tree-item').attr('data-idpath');
                                var path = $(el).closest('.tree-item').attr('data-path');
                                var name = $(el).closest('.tree-item').attr('data-name');
                                if (!self.checkIsSelect(id, activeGroupItems)) {
                                    activeGroupItems.push({
                                        id: id,
                                        path: path,
                                        name: name,
                                        idPath: idPath
                                    });
                                }

                            }
                        });
                    } else { //copy move
                        var circleLength = $current.find('.g-tree-circle .A.active').length - 1;
                        var el = $current.find('.g-tree-circle .A.active:eq(' + circleLength + ')');
                        var id = $(el).closest('.tree-item').attr('data-id');
                        var idPath = $(el).closest('.tree-item').attr('data-idpath');
                        var path = $(el).closest('.tree-item').attr('data-path');
                        var name = $(el).closest('.tree-item').attr('data-name');
                        if (!self.checkIsSelect(id, activeGroupItems)) {
                            activeGroupItems.push({
                                id: id,
                                path: path,
                                name: name,
                                idPath: idPath
                            });
                        }
                    }
                } else {
                    $current.find('.g-tree-circle').each(function(index, el) {
                        if ($(el).find('.A').hasClass('active')) {

                            var id = $(el).closest('.tree-item').attr('data-id');
                            var idPath = $(el).closest('.tree-item').attr('data-idpath');
                            var path = $(el).closest('.tree-item').attr('data-path');
                            var name = $(el).closest('.tree-item').attr('data-name');
                            if (!self.checkIsSelect(id, activeGroupItems)) {
                                activeGroupItems.push({
                                    id: id,
                                    path: path,
                                    name: name,
                                    idPath: idPath
                                });
                            }

                        }
                    });
                }

                if (!self.option.nowGroup) {
                    self.renderPath('');

                }
                activeGroupItems = self.hebingGroup(activeGroupItems);
                activeGroupItems = self.filterGroup(activeGroupItems);
                if (!self.option.allowSelectOtherEGroup && !self.checkIsFromCommonGroup(activeGroupItems)) {
                    TMS.alert(self.option.errorMessage, 'danger');
                    return;
                }

                if (self.option.confirm) {
                    self.option.confirm(activeGroupItems);

                } else if (self.option.groupPanel.display) {
                    self.option.activeGroup.items = activeGroupItems;
                    self.initGroupPanel();
                } else {
                    window.location.href = _ACTIVE_URL_ + self.option.activeGroup.items[0].id; // 跳转对应group
                }
                if (self.option.isModal) {
                    $(self.el).modal('hide');
                }


            });
            TMS.$body.on('click', self.el + ' .g-searchGo', function() {
                var value = $(self.el).find('.g-searchInput').val();
                if ($.trim(value).length == 0) {
                    self.reset();
                } else {
                    self.searchTree(value);
                }


            });

        }
    }
    TMS.searchGroupTerminal = function(option) {
        return {
            init: function() {
                this.renderGroupPath();
                this.bindEvent();
            },
            renderGroupPath: function() {
                if (!option.namePath && !option.tsn) {
                    return false;
                }
                var arr = [],
                    group;
                if (option.namePath) {
                    group = option.namePath.split('/');
                    for (var i = 0; i < group.length; i++) {
                        if (i == 0) {
                            arr.push('<li>' + group[i] + '</li>');
                        } else {
                            arr.push('<li><span>/</span>' + group[i] + '</li>');
                        }
                    }
                    if (option.tsn) {
                        arr.push('<li><span>/</span>' + option.tsn + '</li>');
                    }
                } else {
                    arr.push('<li>' + option.tsn + '</li>');
                }


                if (arr.length > 5) {
                    var _arr = [];
                    _arr.push(arr[0]);
                    _arr.push(arr[1]);
                    _arr.push('<li><span>/</span>...</li>');
                    _arr.push(arr[arr.length - 2]);
                    _arr.push(arr[arr.length - 1]);
                    $('.g-path').html(_arr.join(''));
                } else {
                    $('.g-path').html(arr.join(''));
                }


            },
            ajaxSearchList: function(val) {
                var self = this;
                if (val.length == 0) {
                    return false
                }
                TMS.getData({
                    url: _WEB_SITE_ + '/group/service/searchGroupOrTerminal',
                    type: 'get',
                    data: {
                        keyword: val
                    }
                }, function(data) {
                    if (data.length > 0) {
                        self.renderSearchList(data);
                    } else {
                        $('.g-search-list').removeClass('hide').html('<li>There is no data!</li>');
                    }
                })
            },
            renderSearchList: function(data) {
                var temp = [],
                    cls = "item-terminal";
                for (var i = 0; i < data.length; i++) {
                    var arr = [],
                        href;
                    if (!data[i].tsn) {
                        cls = "item-group";
                        if (_URL_ == '/terminalDeploy/list/') {
                            href = _WEB_SITE_ + '/groupDeploy/list/' + data[i].groupId;
                        } else if (_URL_ == '/terminalDeploy/toDeploy/') {
                            href = _WEB_SITE_ + '/groupDeploy/toDeploy/' + data[i].groupId;
                        } else if (_URL_ == '/events/terminalEvents/') {
                            href = _WEB_SITE_ + '/events/allEvents/' + data[i].groupId;
                        } else if (_URL_ == '/myProfile/view/' || _URL_ == '/myProfile/toChangePassword/') {
                            href = _WEB_SITE_ + '/index/' + data[i].groupId;
                        } else {
                            href = option.activeUrl + data[i].groupId;
                        }
                    } else {
                        if (_URL_ == '/group/list/' || _URL_ == '/myProfile/view/' || _URL_ == '/myProfile/toChangePassword/') {
                            href = _WEB_SITE_ + '/terminal/list/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/user/list/' || _URL_ == '/auditLog/list/' || _URL_ == '/report/terminalNotRegistered/') {
                            href = _WEB_SITE_ + '/terminal/list/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/groupDeploy/list/') {
                            href = _WEB_SITE_ + '/terminalDeploy/list/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/groupDeploy/toDeploy/') {
                            href = _WEB_SITE_ + '/terminalDeploy/toDeploy/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/report/userMaintenance/') {
                            href = _WEB_SITE_ + '/report/terminalDownload/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/index/') {
                            href = _WEB_SITE_ + '/terminal/monitor/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/events/allEvents/') {
                            href = _WEB_SITE_ + '/events/terminalEvents/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/events/alertEvents/' || _URL_ == '/alert/alertOff/' || _URL_ == '/alert/alertCondition/') {
                            href = _WEB_SITE_ + '/terminal/list/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/dashboard/statistics/') {
                            href = _WEB_SITE_ + '/terminal/list/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/pkg/manageList/') {
                            href = _WEB_SITE_ + '/pkg/list/' + data[i].groupId + '/' + data[i].tsn;
                        } else if (_URL_ == '/terminal/toAdd/') {
                            href = _WEB_SITE_ + '/terminal/list/' + data[i].groupId + '/' + data[i].tsn;
                        } else {
                            href = option.activeUrl + data[i].groupId + '/' + data[i].tsn;
                        }
                    }

                    temp.push('<li class="search-item">');
                    temp.push('<a class="search-link ' + cls + '" href="' + href + '">');
                    if (data[i].groupPath) {
                        arr = data[i].groupPath.split('/');
                        if (arr.length > 5) {
                            temp.push('<span class="path">' + arr[0] + '</span>');
                            temp.push('<span class="path">' + arr[1] + '</span>');
                            temp.push('<span class="path">...</span>');
                            if (data[i].tsn) {
                                temp.push('<span class="path">' + arr[arr.length - 1] + '</span>');
                                temp.push('<span class="path">' + data[i].tsn + '</span>');
                            } else {
                                temp.push('<span class="path">' + arr[arr.length - 2] + '</span>');
                                temp.push('<span class="path">' + arr[arr.length - 1] + '</span>');
                            }

                        } else {
                            for (var j = 0; j < arr.length; j++) {
                                temp.push('<span class="path">' + arr[j] + '</span>');
                            }
                            if (data[i].tsn) {
                                temp.push('<span class="path">' + data[i].tsn + '</span>');
                            }
                        }


                    } else if (data[i].tsn) {
                        temp.push('<span class="path">' + data[i].tsn + '</span>');
                    }
                    temp.push('</a>');
                    temp.push('</li>');
                }
                $('.g-search-list').removeClass('hide').html(temp.join(''));
            },
            bindEvent: function() {
                var self = this;
                $('.g-search-btn').click(function(event) {
                    $('.g-search-btn,.g-path').addClass('hide');
                    $('.g-search-show').removeClass('hide');
                    $('.g-search-input').focus();
                    $('.navbar-toggle').addClass('hide');
                    event.stopPropagation();
                });
                $('.g-top-search').click(function(event) {
                    event.stopPropagation();
                });
                $('.g-search-input').keyup(function(event) {
                    var val = $(this).val();
                    if (event.keyCode == 13) {
                        self.ajaxSearchList($.trim(val));
                    }
                });
                $('.g-search-showbtn').click(function(event) {
                    var val = $('.g-search-input').val();
                    self.ajaxSearchList($.trim(val));

                });
                $('.g-container').click(function(event) {
                    $('.g-search-btn,.g-path').removeClass('hide');
                    $('.g-search-show').addClass('hide');
                    $('.navbar-toggle').removeClass('hide');
                    $('.g-search-input').val('');
                    $('.g-search-list').addClass('hide').html('');
                });
                $("#logout-btn").click(function() {
                	var token = {};
                    token[_TOKEN_HEADER_NAME_] = _TOKEN_;
                	$.ajax({
	            	     type: "get",
	            	     url: _WEB_SITE_ + '/myProfile/service/logout',
	            	     dataType:'json',
	            	     headers: token,
	            	     success: function(data) {
	            	    	 if (data.statusCode !== undefined && data.statusCode != '200') {
	                             TMS.removeAlertdanger($('.bootstrap-growl .alert'));
	                             TMS.alert(data.message, 'danger');
	                         }
	            	     },
                	     complete: function(xhr, textStatus) {
                	    	var url = xhr.getResponseHeader("redirectUrl");
                	    	var enable = xhr.getResponseHeader("enableRedirect");
            	    	    if((enable == "true") && (url != "")){
            	    	        var win = window;
            	    	        while(win != win.top){
            	    	            win = win.top;
            	    	        }
            	    	        win.location.href = url;
            	    	    }
                	     },
                	     error: function(){}
                	});
                });
            }
        }.init();
    }
    return TMS;

})
