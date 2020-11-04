<%@ page pageEncoding="UTF-8" %>
<jsp:directive.include file="../includes/top.jsp"/>

<div class="g-middle-content">
    <div class="container-fluid">
        <div class="row">
            <!-- 内容区域这里开始 -->

            <div class="col-sm-12 col-md-5">
                <div id="view" class="g-panel" style="display: block;">
                    <div class="g-panel-title clearfix">
                        <div class="g-panel-text">
                            <a class="g-back-button iconfont" href="javascript:history.go(-1)"
                               title="Return to previous page"></a>Terminal Details
                        </div>
                    </div>
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-md-12 col-sm-12">
                                <div class="view-item">
                                    <div class="view-name ">Terminal SN</div>
                                    <div class="view-value "><c:out value="${terminal.tsn}" escapeXml="true"/></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name ">Terminal Type</div>
                                    <div class="view-value "><c:out value="${terminal.model.name}"
                                                                    escapeXml="true"/></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name ">Status</div>
                                    <div class="view-value ">
                                        <c:if test="${terminal.status == true}">
                                            Active
                                        </c:if>
                                        <c:if test="${terminal.status == false}">
                                            Deactive
                                        </c:if>
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Country</div>
                                    <div class="view-value" id="d-country-name"><c:out value="${terminal.country}"
                                                                                       escapeXml="true"/></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">State/Province</div>
                                    <div class="view-value" id="viewProvinceName" data-proid=""><c:out
                                            value="${terminal.province}" escapeXml="true"/></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">City</div>
                                    <div class="view-value" id=""><c:out value="${terminal.city}"
                                                                         escapeXml="true"/></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">ZIP/Postal Code</div>
                                    <div class="view-value" id="d-zipCode"><c:out value="${terminal.zipCode}"
                                                                                  escapeXml="true"/></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Time Zone</div>
                                    <div class="view-value">
                                        <c:set var="timeZone" value="${terminal.timeZone}"/>
                                        <c:forEach items="${timeZoneList}" var="item">
                                            <c:set var="time" value="${item.timeZoneId}"/>
                                            <c:if test="${timeZone == time}"> <c:out value="${item.timeZoneName}"
                                                                                     escapeXml="true"/></c:if>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Daylight Saving</div>
                                    <div class="view-value">
                                        <c:set var="daylightSaving" value="${terminal.daylightSaving}"/>
                                        <c:if test="${daylightSaving == true}">Enable</c:if>
                                        <c:if test="${daylightSaving == false}">Disable</c:if>
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Sync To Server Time</div>
                                    <div class="view-value">
                                        <c:set var="syncToServerTime" value="${terminal.syncToServerTime}"/>
                                        <c:if test="${syncToServerTime == true}">Enable</c:if>
                                        <c:if test="${syncToServerTime == false}">Disable</c:if>
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Address</div>
                                    <div class="view-value"><c:out value="${terminal.address}" escapeXml="true"/></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Group Path</div>
                                    <div class="view-value group-path-box">
                                        <div>
                                            <c:forEach items="${groupNames}" var="item" varStatus="status">
                                                <c:if test="${not empty item}">
                                                    <c:if test="${status.count <=6}">
                                                        <c:if test="${!status.last}">
                                                            <p class="group-path-margin0"><c:out value="${item}"
                                                                                                 escapeXml="true"/>;</p>
                                                        </c:if>
                                                        <c:if test="${status.last}">
                                                            <p class="group-path-margin0"><c:out value="${item}"
                                                                                                 escapeXml="true"/></p>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                        <div id="leftover" style="display:none">
                                            <c:forEach items="${groupNames}" var="item" varStatus="status">
                                                <c:if test="${status.count > 6}">
                                                    <c:if test="${not empty item}">
                                                        <c:if test="${!status.last}">
                                                            <p class="group-path-margin0"><c:out value="${item}"
                                                                                                 escapeXml="true"/>;</p>
                                                        </c:if>
                                                        <c:if test="${status.last}">
                                                            <p class="group-path-margin0"><c:out value="${item}"
                                                                                                 escapeXml="true"/></p>
                                                        </c:if>
                                                    </c:if>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Description</div>
                                    <div class="view-value"><c:out value="${terminal.description}"
                                                                   escapeXml="true"/></div>
                                </div>
                                <div class="pull-right text-right" id="terminal-view-btn-list">
                                    <input type='hidden' id="permission-delete"
                                           value="<shiro:hasPermission name='tms:terminal:delete'>1</shiro:hasPermission>">
                                    <input type='hidden' id="permission-remove"
                                           value="<shiro:hasPermission name='tms:terminal:remove'>1</shiro:hasPermission>">
                                    <input type='hidden' id="permission-edit"
                                           value="<shiro:hasPermission name='tms:terminal:edit'>1</shiro:hasPermission>">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="g-panel " id="edit" style="display: none;">
                    <!--edit-->
                    <div class="g-panel-title clearfix">
                        <span class="g-panel-text">Edit Terminal</span>
                    </div>
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-md-12 col-sm-12">
                                <form class="form-horizontal" id="edit-form" role="form" autocomplete="off">
                                    <input type="hidden" id="gid" name="groupId" value="${group.id}">
                                    <input type="hidden" id="treeDepth" value="${group.treeDepth}">
                                    <div class="form-edit-group">
                                        <div class="edit-name">Terminal SN</div>
                                        <div class="edit-value">
                                            <input type="hidden" id="tsn" name="tsn" value="${terminal.tsn}">
                                            <c:out value="${terminal.tsn}" escapeXml="true"/>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Country<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                            <select class="form-control required firstFocus" name="countryId"
                                                    id="countryId">
                                                <option value="">--Please Select--</option>
                                                <c:forEach items="${countryList}" var="item">
                                                    <option value="${item.id}" ${terminal.country eq item.name ? "selected":"" }>
						                               <span id="province_id" data-province_id="${item.id}"><c:out
                                                               value="${item.name }"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="form-edit-group">
                                        <div class="edit-name">State/Province<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                       	   <c:forEach items="${provinceList}" var="item">
                                               <c:if test="${item.name == terminal.province }">
                                                   <input type="hidden" id="province_id"
                                                          data-province_id="${item.id}"></input>
                                               </c:if>
                                           </c:forEach>
                                           <select class="form-control" id="provinceId" name="provinceId"
                                                onchange="document.getElementById('provinceName').value=this.options[this.selectedIndex].text;">
                                             <c:if test="${!empty terminal.country }">
                                                <c:forEach items="${provinceList}" var="item">
                                                  <option value="${item.id}" ${terminal.province eq item.name ? "selected":"" }>
                                                    <c:out value="${item.name }" />
                                                  </option>
                                                </c:forEach>
                                             </c:if>
                                           </select>
                                           <div class="form-control-input">
		                                       <input type="text" class="form-control required" placeholder="Add or Select a value" name="provinceName" id="provinceName"
                                               value='<c:out value="${terminal.province}" escapeXml="true" />' maxlength="64"
                                               onchange="document.getElementById('provinceId').value='';">
	                                       </div>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">City<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control citycheck required" placeholder=" "
                                                   name="cityName" id="cityId"
                                                   value='<c:out value="${terminal.city}" escapeXml="true" />'
                                                   maxlength="35">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">ZIP/Postal Code<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                             <input type="text" class="form-control zipcode required"
                                                    placeholder="" name="zipCode" id="zipcode" maxlength="7"
                                                    value="${terminal.zipCode}">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Time Zone<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                            <select class="form-control required" name='timeZone' id="timeZoneId">
                                                <option value="">--Please Select--</option>
                                                <c:forEach items="${timeZoneList}" var="item">
                                                    <c:set var="time" value="${item.timeZoneId}"/>
                                                    <c:set var="timeZone" value="${terminal.timeZone}"/>
                                                    <option data-isDaylightSaving="${item.isDaylightSaving}"
                                                            value="${item.timeZoneId}" ${timeZone eq time ? "selected":"" }>
                                                        <c:out value="${item.timeZoneName}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <c:set var="parentTimeZoneDaylight" value="${parentTimeZone.isDaylightSaving}"/>
                                        <div class="edit-name">Daylight Saving<span class="icon-required">*</span></div>
                                        <div class="edit-value">
                                            <!-- enable daylightsaving-->
                                            <c:if test="${parentTimeZoneDaylight=='1'}">
                                                <select class="form-control required" name='daylightSaving'
                                                        style="cursor:pointer">
                                                    <c:set var="daylightSaving" value="${terminal.daylightSaving}"/>
                                                    <option value="0" <c:if
                                                            test="${daylightSaving == false}"> selected</c:if>>Disable
                                                    </option>
                                                    <option value="1" <c:if
                                                            test="${daylightSaving == true}"> selected</c:if>>Enable
                                                    </option>
                                                </select>
                                            </c:if>
                                            <c:if test="${empty parentTimeZoneDaylight}">
                                                <select class="form-control required" name='daylightSaving'
                                                        style="cursor:pointer">
                                                    <c:set var="daylightSaving" value="${terminal.daylightSaving}"/>
                                                    <option value="0" <c:if
                                                            test="${daylightSaving == false}"> selected</c:if>>Disable
                                                    </option>
                                                    <option value="1" <c:if
                                                            test="${daylightSaving == true}"> selected</c:if>>Enable
                                                    </option>
                                                </select>
                                            </c:if>
                                            <!-- disable daylightsaving-->
                                            <c:if test="${parentTimeZoneDaylight=='0'}">
                                                <select class="form-control required" name='daylightSaving' disabled
                                                        style="cursor:not-allowed;">
                                                    <c:set var="daylightSaving" value="${terminal.daylightSaving}"/>
                                                    <option value="0" <c:if
                                                            test="${daylightSaving == false}"> selected</c:if>>Disable
                                                    </option>
                                                    <option value="1" <c:if
                                                            test="${daylightSaving == true}"> selected</c:if>>Enable
                                                    </option>
                                                </select>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Sync To Server Time<span class="icon-required">*</span>
                                        </div>
                                        <div class="edit-value">
                                            <select class="form-control required" name='syncToServerTime'>
                                                <option value="0" <c:if
                                                        test="${syncToServerTime == false}"> selected</c:if>>Disable
                                                </option>
                                                <option value="1" <c:if
                                                        test="${syncToServerTime == true}"> selected</c:if>>Enable
                                                </option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Address</div>
                                        <div class="edit-value">
                                            <input type="text" class="form-control specialInput" maxlength="256"
                                                   name='address'
                                                   value='<c:out value="${terminal.address}" escapeXml="true" />'
                                                   placeholder="">
                                        </div>
                                    </div>
                                    <div class="form-edit-group">
                                        <div class="edit-name">Description</div>
                                        <div class="edit-value">
                                            <textarea class="form-control form-line" rows="3" name="description"
                                                      placeholder=" "> <c:out value="${terminal.description}"
                                                                              escapeXml="true"/></textarea>
                                        </div>
                                    </div>
                                    <div class="pull-right view-button">
                                        <button type="submit" class="btn btn-primary view-button-style" id="Confirm">
                                            Confirm
                                        </button>
                                        <button type="button" class="btn btn-default view-button-style" id="Back">
                                            Cancel
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-7 col-sm-12 terminal-profile hide">
                <div class="g-panel-title clearfix">
                    <c:if test="${not empty terminal.reportTime}">
                        <span class="g-panel-right terminal-table-subtitle">
                            Report Time : 
                            <fmt:formatDate value="${terminal.reportTime}" pattern="HH:mm MM/dd/yyyy "></fmt:formatDate>
                        </span>
                    </c:if>
                    
                    <span class="g-panel-text">Installed Packages</span>
                </div>
                <div class="g-panel-body">
                    <div class="bootstrap-table">
                        <div class="fixed-table-container">
                            <div class="fixed-table-body">
                                <table id="table_packagemodule" class="table table-condensed">
                                    <thead>
                                    <tr>
                                        <th>
                                            <div class="th-inner ">Name</div>
                                        </th>
                                        <th>
                                            <div class="th-inner ">Version</div>
                                        </th>
                                        <th>
                                            <div class="th-inner ">Type</div>
                                        </th>
                                    </tr>
                                    </thead>
                                    <tbody class="terminal-detail-indetall-tbody">
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
        <!-- 内容区域end-->
    </div>
</div>

<jsp:directive.include file="../includes/bottom.jsp"/>
<!-- footer -->
<!-- modal -->
<!--modal remove terminal-->
<div class="modal fade" id="modal_terminal_remove" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                <p>Are you sure to remove this terminal?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="confirm_terminal_remove">Confirm</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<!-- modal -->
<!--modal delete terminal-->
<div class="modal fade" id="modal_terminal_delete" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                <p>Are you sure to delete this terminal?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary J-confirm-btn">Confirm</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<script type="text/javascript">
    window.terminalInstalls='${terminal.installApps}'
    seajs.use('terminal_profile');
</script>
</body>
</html>