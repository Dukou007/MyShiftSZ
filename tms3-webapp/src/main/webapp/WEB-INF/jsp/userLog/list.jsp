<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="../includes/top.jsp"%>
				<!-- 右侧内容 -->
				 <div class="tooltips-right content-page">
        <div class="body-content">
            <!--标题内容-->
            <div class="body-title">
                <span class="title-first">User Management</span>
                <span class="title-right">></span>
                <span class="title-second">User Log</span>
                 
            </div>
            <table id='table' class="table table-hover table-style" data-toolbar='#toolbar'>
            </table>
        </div>
    </div>
    <!-- 右侧内容end -->
    <!-- FOOTER -->
    <script type="text/javascript">
    seajs.use('TMS', function(TMS) {
        $(function() {
            TMS.init();
            //table
            /*  countrycascade.kylin(),*/

            TMS.bootTable('#table', {
                url:  $("#contextPath").val() + '/userLog/service/list',
                columns: [{
                    field: 'username',
                    title: 'User Name',
                    sortable: false
                }, {
                    field: 'role',
                    title: 'Role',
                    sortable: false
                }, {
                    field: 'eventAction',
                    title: 'Action',
                    sortable: false
                }, {
                    field: 'clientIp',
                    title: 'IP Address',
                    sortable: false
                }, {
                    field: 'eventTime',
                    title: 'Log Time',
                    sortable: false
                
                }],
                queryParamsType: '',
                sidePagination: 'server',
                queryParams: function(params) { //接口参数处理

                    console.log(params);
                    return params;
                },
                detailView: true,

                detailFormatter: function(index, row) { //详情
                    console.log(index, row)
                    var html = [];
                    $.each(row, function(key, value) {
                        html.push('<p><b>' + key + ':</b> ' + value + '</p>');
                    });
                    return html.join('');
                },
                responseHandler: function(res) { //接口数据处理
                    this.data = res.items;
                    res.total = res.totalCount;
                    return res;
                },
                sortName:'userName',
                sortOrder:'asc',
                pagination:true,
                search:true,
                searchOnEnterKey:true,
                showRefresh:true
              

            });
        });
    });
    </script>
    <!-- END -->
				
				

</body>
</html>