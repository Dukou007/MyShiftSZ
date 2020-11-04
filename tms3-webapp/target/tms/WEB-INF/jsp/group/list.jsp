<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

<div class="g-middle-content">
    <input type="hidden" id="gid" value="${group.id}">
    <input type="hidden" id="pid" value="${group.parent.id}">
    <input type="hidden" id="groupIds" value="${groupIds}">
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12 col-sm-12">
                <div class="g-panel" id="my-grouptree">
                    <div class="g-panel-title clearfix">
                        <span class="g-panel-text">Manage Groups</span>
                        <div class="g-search">

                            <div class="input-group">
                                <input class="form-control g-searchInput" type="text" placeholder="Group Name">
                                <div class="input-group-addon g-searchGo btn" >Go</div>
                            </div>
                            <shiro:hasPermission name="tms:group:import"><a class="btn btn-primary g-icon-btn group-import hide" style="margin-left: 10px;vertical-align: top;" 
                               href="<%=contextPath%>/group/toImport/${group.id}"> <i class="iconfont">&#xe604;</i>
                                Import Groups
                            </a></shiro:hasPermission>
                           	 
                        </div>
                    </div>
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-sm-12">
                                <div class="mygroupbox">
                                    <div class="group-tree-body">
                                        <div class="group-tree-content"></div>
                                        <div class="group-tree-search hide"></div>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<!-- coyp group -->
<div class="modal fade" id="copy-group">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">COPY GROUP</h4>
            </div>
            <div class="modal-body">
                <div class="g-search">

                    <div class="input-group">
                        <input class="form-control g-searchInput" type="text" placeholder="Search Here">
                        <div class="input-group-addon g-searchGo btn" id="g-searchGo">Go</div>
                    </div>

                </div>
                <div class="group-tree-body">
                    <div class="group-tree-content"></div>
                    <div class="group-tree-search hide"></div>
                </div>
            </div>
            <!-- /.modal-content -->
            <div class="modal-footer">
                <p class="g-tree-bottom"></p>
                <div class="modal-footer-btngroup">
                    <button type="button" class="btn btn-default group-cancel-btn" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary group-confirm-btn">Confirm</button>
                </div>

            </div>
        </div>
        <!-- /.modal-content --> </div>
    <!-- /.modal-dialog -->
</div>

<!-- /.modal -->
<!-- coyp group -->
<div class="modal fade" id="move-group">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">MOVE GROUP</h4>
            </div>
            <div class="modal-body">
                <div class="g-search">

                    <div class="input-group">
                        <input class="form-control g-searchInput" type="text" placeholder="Search Here">
                        <div class="input-group-addon g-searchGo btn" id="g-searchGo">Go</div>
                    </div>

                </div>
                <div class="group-tree-body">
                    <div class="group-tree-content"></div>
                    <div class="group-tree-search hide"></div>
                </div>
            </div>
            <!-- /.modal-content -->
            <div class="modal-footer">
                <p class="g-tree-bottom"></p>
                <div class="modal-footer-btngroup">
                    <button type="button" class="btn btn-default group-cancel-btn" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary group-confirm-btn">Confirm</button>
                </div>

            </div>
        </div>
        <!-- /.modal-content --> </div>
    <!-- /.modal-dialog -->
</div>

<!-- /.modal -->
<!-- coyp group -->
<div class="modal fade" id="remove-group">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title">WARNING</h4>
            </div>
            <div class="modal-body text-center">
                If you delete the group, all the sub-groups will be deleted too. Are you sure to delete this group?
            </div>
            <!-- /.modal-content -->
            <div class="modal-footer">
                <div class="modal-footer-btngroup">
                    <button type="button" class="btn btn-default group-cancel-btn" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary group-confirm-btn">Confirm</button>
                </div>

            </div>
        </div>
        <!-- /.modal-content --> </div>
    <!-- /.modal-dialog -->
</div>

<!-- /.modal -->
<script type="text/javascript">seajs.use('my-group');

</script>
<input type='hidden' id="permission-add" value="<shiro:hasPermission name='tms:group:add'>1</shiro:hasPermission>">
<input type='hidden' id="permission-delete" value="<shiro:hasPermission name='tms:group:delete'>1</shiro:hasPermission>">
<input type='hidden' id="permission-copy" value="<shiro:hasPermission name='tms:group:copy'>1</shiro:hasPermission>">
<input type='hidden' id="permission-move" value="<shiro:hasPermission name='tms:group:move'>1</shiro:hasPermission>">
<input type='hidden' id="permission-import" value="<shiro:hasPermission name='tms:group:import'>1</shiro:hasPermission>">
<input type='hidden' id="permission-global-setting" value="<shiro:hasPermission name='tms:group:global Setting'>1</shiro:hasPermission>">
<input type='hidden' id="permission-addEnterprise" value="<shiro:hasPermission name='tms:group:addEnterprise'>1</shiro:hasPermission>">
</body>
</html>