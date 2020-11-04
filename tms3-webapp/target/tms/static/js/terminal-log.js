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
	var groupId = _ACTIVE_GROUP_;
	var hasData = false;
	var terminalLog = {
		init : function() {
			var self = this;
			self.bindEvent();
			self.$table = $('#log-table');
			this.fuzzyCondition = '';
			self.renderTable();
		},
		renderTable : function() {
			var self = this;
			window.initialTable=false;
		
			TMS.bootTable('#log-table', {
				url : _WEB_SITE_ + '/terminal/log/list/'+GROUP_ID,
				columns : [ {
					field : 'trmId',
					title : 'Terminal SN / Device Name',
					formatter: valueShowFormatter,
					sortable : false
				}, {
					field : 'severity',
					title : 'Severity',
					formatter: valueFormatter,
					sortable : false,
				}, {
					field : 'message',
					title : 'Message',
					sortable : false,
					cellStyle: function (value, row, index) {
                        return {
                            css: {
                                "min-width": "100px",
                                "white-space": "nowrap",
                                "text-overflow": "ellipsis",
                                "overflow": "hidden",
                                "max-width": "600px",
                            }
                        }
                    },
                    formatter: function (value, row, index) {
                    	if (value == '' || value == null) {
        					return '-';
        				} else {
        					var span = document.createElement("span");
                        	span.setAttribute("title",value);
                        	span.innerHTML = value;
                        	return span.outerHTML;
        				}
                    }
				}, {
					field : 'eventLocalTime',
					title : 'Event Local Time',
					formatter: valueFormatter,
					sortable : false,
				}, {
	                field: 'expanded',
	                formatter: expandFormatter,
	                title: '',
	                sortable: false,
	                cellStyle:{
	                    css: {width: '50px','text-align':'center'}
	                }
	            }],
	            detailFormatter:function(index, row){
	            	var installValueHtml = '';
	            	if (row.appInfo == undefined || row.appInfo == null || row.appInfo == '') {
	            		var tr='<tr class="no-records-found"><td colspan="4"><div class="alert alert-info paxinfonext"><strong>Hint:</strong>(NOT REPORTED)</div></td></tr>';
	            		installValueHtml += tr;
	            	} else {
	            		var terminalInstallsArr = JSON.parse(row.appInfo);
	            		for (var i=0;i<terminalInstallsArr.length;i++) {
		            		var tr='<tr><td>'+terminalInstallsArr[i].name+'</td><td>'+terminalInstallsArr[i].version+'</td></tr>';
		            		installValueHtml += tr;
		            	}
	            	}
	            	var str = "<div class='col-sm-12 col-md-6'>" +
	            				"<div class='g-panel' style='margin-top:0;'>" +
//	            				  "<div class='g-panel-title clearfix'>" +
//	            				    "<div class='g-panel-text'>" +
//	            					   "Terminal Log Details" +
//	            					 "</div>" +
//	            				  "</div>" +
	            				  "<div class='g-panel-body'>" +
	            				     "<div class='col-md-12 col-sm-12'>";
	            	if (row.trmId) {
	            		str += "<div class='view-item'><div class='view-name'>Terminal SN</div><div class='view-value'>" + row.trmId + "</div></div>";
	            	} else if (row.deviceName) {
	            		str += "<div class='view-item'><div class='view-name'>Device Name</div><div class='view-value'>" + row.deviceName + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Terminal SN</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.deviceType) {
	            		str += "<div class='view-item'><div class='view-name'>Terminal Type</div><div class='view-value'>" + row.deviceType + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Terminal Type</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.source) {
	            		str += "<div class='view-item'><div class='view-name'>Source</div><div class='view-value'>" + row.source + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Source</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.sourceVersion) {
	            		str += "<div class='view-item'><div class='view-name'>Source Version</div><div class='view-value'>" + row.sourceVersion + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Source Version</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.sourceType) {
	            		str += "<div class='view-item'><div class='view-name'>Source Type</div><div class='view-value'>" + row.sourceType + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Source Type</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.message) {
	            		str += "<div class='view-item'><div class='view-name'>Message</div><div class='view-value'>" + row.message + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Message</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.severity) {
	            		str += "<div class='view-item'><div class='view-name'>Severity</div><div class='view-value'>" + row.severity + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Severity</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.eventLocalTime) {
	            		str += "<div class='view-item'><div class='view-name'>Event Local Time</div><div class='view-value'>" + row.eventLocalTime + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Event Local Time</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.commType) {
	            		str += "<div class='view-item'><div class='view-name'>Connect Type</div><div class='view-value'>" + row.commType + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Connect Type</div><div class='view-value'>-</div></div>";
	            	}
	            	if (row.localIp) {
	            		str += "<div class='view-item'><div class='view-name'>Local IP</div><div class='view-value'>" + row.localIp + "</div></div>";
	            	} else {
	            		str += "<div class='view-item'><div class='view-name'>Local IP</div><div class='view-value'>-</div></div>";
	            	}
	            	str += "</div></div></div></div>";
	            	str += "<div class='col-md-6 col-sm-12' style='margin-top:0;'>" +
	            	         "<div class='g-panel-title clearfix'>" +
	            	           "<span class='g-panel-text'>Installed Applications</span>" +
	            	         "</div>" + 
	            	         "<div class='g-panel-body'>" +
	            	           "<div class='bootstrap-table'>" +
	            	             "<div class='fixed-table-container'>" +
	            	               "<div class='fixed-table-body'>" +
	            	                 "<table class='table table-condensed'>" +
	            	                   "<thead>" +
	            	                     "<tr>" +
	            	                       "<th>" +
	            	                         "<div class='th-inner'>Name</div>" +
	            	                       "</th>" +
	            	                       "<th>" +
	            	                         "<div class='th-inner'>Version</div>" +
	            	                       "</th>" +
	            	                     "</tr>" +
	            	                   "</thead>" +
	            	                   "<tbody class='terminal-log-install-tbody'>" +
	            	                     installValueHtml
	            	                   "</tbody>" +
	            	                 "</table>" +
	            	               "</div>" +
	            	             "</div>" +
	            	           "</div>" +
	            	         "</div>" +
	            	       "</div>";
	            	return str
                },
				loadend : function(res) {
					window.initialTable=false;
					$('.event-export').removeClass('hide');
					$('.event-export .btn').removeClass("disabled");
					$('.log-level-set').removeClass('hide');
					// 加载完执行code
                    if (res.totalCount && res.totalCount > 0) {
                    	hasData = true;
							$('.table-records').removeClass('hide').html(
									res.totalCount + ' records found');
					} else {
						hasData = false;
						$('.table-records').addClass('hide');
		                $('.event-export .btn').addClass("disabled");
					}
				},
				queryParams : function(params) { // 接口参数处理
					params.fuzzyCondition = self.fuzzyCondition;
					return params;
				},
				pagination : true,
			});
			function valueShowFormatter (value, row, index) {
				if (value == '' || value == null) {
					if (row.deviceName) {
						return row.deviceName
					}
					return '-';
				} else {
					return value;
				}
			}
			function valueFormatter(value, row, index) {
				if (value == '' || value == null) {
					return '-';
				} else {
					return value;
				}
			}
			function timeFormatter(value, row, index) {
				return TMS.changeTime(row.eventLocalTime, row.id);
			}
			function expandFormatter(value, row, index) {
                return [
                    '<a class="detail-icon" href="javascript:"><i class="glyphicon glyphicon-chevron-down"></i></a>'
                ].join('');
	        }
		},
		reSetTable : function(o) {
			var self = this;
			self.$table.bootstrapTable('refreshOptions', {
				url : _WEB_SITE_ + '/terminal/log/list/'+GROUP_ID,
				queryParams : function(params) { // 接口参数处理
					params.fuzzyCondition = self.fuzzyCondition;
					return params
				},
				pageNumber : 1
			// 重置页码为1
			});
		},

		bindEvent : function() {
			var $body = $(".g-container"), self = this, o = {};
			$('.g-searchGo').click(function() {
				var keyWord = $('.g-searchInput').val();
				self.fuzzyCondition = keyWord;
				self.reSetTable(o);

			});
			$('.g-searchInput').keyup(function(e) {
				if (e.keyCode == 13) {
					var keyWord = $(this).val();
					if (keyWord == self.fuzzyCondition) {
						return false
					}
					self.fuzzyCondition = keyWord;
					self.reSetTable(o);
				}
			});
			$('#exportExcel').click(function() {
				if (hasData) {
					TMS.getData({
	                    url: _WEB_SITE_ + '/terminal/log/isexport/' + groupId,
	                    type: 'get',
	                    data: {
	                    	fuzzyCondition: self.fuzzyCondition
	                    }
	                }, function(data) {
	                	if (data.statusCode != undefined && data.statusCode != '200') {
	    		            TMS.alert(data.message, 'danger');
	    		        } else {
	    		        	var downloadUrl = _WEB_SITE_ + '/terminal/log/export/' + groupId + '?fuzzyCondition=' + self.fuzzyCondition;
	    		        	window.location.href = downloadUrl;
	    		        }
	                });
				}
            });
			$body.on('click', '.detail-icon', function() {
                if ($(this).children().hasClass('glyphicon-chevron-up')) {
                    return false
                }
                var nowIndex=$(this).parents('tr').data('index');
                var $table=$('.bootstrap-table');  
                $table.find('tr.detail-view[data-parent='+nowIndex+']').remove();
            });
			
			//clean
			$body.on('click', '.J-select-clean', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				$('#clean-terminal-log').modal('show');
			});
			$('#clean-terminal-log .J-confirm-btn').click(function() {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				TMS.getData({
					url: _WEB_SITE_ + '/terminal/log/delete/' + GROUP_ID,
					type: 'post',
					dataType: 'json'
				}, function(data) {
					if (data.statusCode == 200 || data.statusCode == undefined) {
						TMS.alert(data.message, 'success');
						$('#clean-terminal-log').modal('hide');
						self.reSetTable(o);
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
			});
			$('#setLogLevel').click(function() {
				TMS.getData({
					url: _WEB_SITE_ + '/terminal/log/level',
					type: 'post',
					dataType: 'json',
					data: {
						terminalLogLevel: $('#logLevel').val()
                    }
				}, function(data) {
					if (data.statusCode == 200 || data.statusCode == undefined) {
						TMS.alert(data.message, 'success');
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
            });
		}
	};

	$(function() {
		TMS.init();
		terminalLog.init();
	})
});
