<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../../includes/top.jsp" />

<!-- 右侧内容 -->
 <div class="g-middle-content">
                <div class="container-fluid">
                    <div class="row">
                        <!-- 内容区域这里开始 -->
                        <div class="col-sm-12">
                            <div class="g-panel">
                              <form class="form" action="" id="editCondForm">
                                <div class="g-panel-title clearfix">
                                   	<div class="g-panel-text">
			                    	 	Manage Conditions
			                    	</div>
			                    	<div class="text-right">
                                            <div class="ac-block1 hidden">
                                                <button  class="btn btn-primary btn1-edit" type="button"><i class="iconfont ">&#xe60c;</i>&nbsp;Edit</button>
                                            </div>
                                            <div class="ac-block2" style="display: none;">
                                             
                                            <button type="reset" class="btn btn-default btn2-back" href="">Cancel</button>
                                            <button  class="btn btn-primary btn3-confirm" type="submit">Confirm</button>
                                            </div>
                                        </div>
                                </div>
                                <div class="g-panel-body">
                                   <!--  <form class="form" action="" id="editCondForm"> -->
                                        <table id='table' class="table alertCondition">
                                        </table>
                                    <!--      <div class="text-right">
                                            <div class="ac-block1 hidden">
                                                <button  class="btn btn-primary btn1-edit" type="button"><i class="iconfont ">&#xe60c;</i>&nbsp;Edit</button>
                                            </div>
                                            <div class="ac-block2" style="display: none;">
                                             
                                            <button type="reset" class="btn btn-default btn2-back" href="">Cancel</button>
                                            <button  class="btn btn-primary btn3-confirm" type="submit">Confirm</button>
                                            </div>
                                        </div>  -->
                                  <!--   </form> -->
                                </div>
                                </form> 
                            </div>
                        </div>
                        <!-- 内容区域end-->
                    </div>
                </div>
            </div>
            <jsp:directive.include file="../../includes/bottom.jsp" />
    <script type="text/javascript">
    seajs.use(['alertCondition','TMS']);
    </script>
    <input type='hidden' id="permission-edit" value="<shiro:hasPermission name='tms:alert:condition:edit'>1</shiro:hasPermission>">
   
</body>

</html>
