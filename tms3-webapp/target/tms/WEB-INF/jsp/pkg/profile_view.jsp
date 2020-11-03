<%@ page pageEncoding="UTF-8"%>
<jsp:directive.include file="../includes/top.jsp" />

<div class="g-middle-content">
    <input type="hidden" id="gid" value="${group.id}">
    <div class="container-fluid">
        <div class="row g-panel" >
            <div class="col-sm-12 col-md-12">
                <div class="g-panel-title clearfix J-view">
                    <div class="g-panel-text">
                        <a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>
                        Package Info
                    </div>
                </div>
            </div>
            <div class="col-sm-12 col-md-4">
                <div class="J-view"  style="display: block;">
                    <div class="g-panel-body">
                        <div class="row">
                            <div class="col-md-12 col-sm-12">
                                <div class="view-item">
                                    <div class="view-name">Package Name</div>
                                    <div class="view-value"> <c:out value="${pkg.name}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Package Version</div>
                                    <div class="view-value"> <c:out value="${pkg.version}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Package Type</div>
                                    <div class="view-value"> <c:out value="${pkg.pgmType}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Status</div>
                                    <div class="view-value">
                                        <c:if test="${pkg.status == true}">Active</c:if>
                                        <c:if test="${pkg.status == false}">Deactive</c:if>
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Size</div>
                                    <div class="view-value">
                                        <fmt:formatNumber type="number" value="${pkg.fileSize/1024}" pattern="0.00" maxFractionDigits="2"/>
                                        KB
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Group Path</div>
                                     <div class="view-value group-path-box" >
                                     	<div>
                                         	<c:forEach items="${groupNames}" var="item" varStatus="status">
	                                       		<c:if test="${not empty item}">
	                                       			<c:if test="${status.count <=6}">
	                                       				<c:if test="${!status.last}">
	                                       					<p class="group-path-margin0"> <c:out value="${item}" escapeXml="true" />;</p> 
	                                       				</c:if>
	                                       				<c:if test="${status.last}">
	                                       					<p class="group-path-margin0"> <c:out value="${item}" escapeXml="true" /></p> 
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
	                                       					<p class="group-path-margin0"> <c:out value="${item}" escapeXml="true" />;</p> 
	                                       				</c:if>
	                                       				<c:if test="${status.last}">
	                                       					<p class="group-path-margin0"> <c:out value="${item}" escapeXml="true" /></p> 
	                                       				</c:if>
	                                         		</c:if>
	                                         	</c:if>
                                         	</c:forEach>
                                         </div>
                                    </div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Remarks</div>
                                    <div class="view-value"> <c:out value="${pkg.desc}" escapeXml="true" /></div>
                                </div>
                                <div class="view-item">
                                    <div class="view-name">Notes</div>
                                    <div class="view-value"> <c:out value="${pkg.notes}" escapeXml="true" /></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <c:if test="${pkg.pgmType == 'combo'}">
                <div class="col-md-8 col-sm-12">
                        <div class="g-panel-title clearfix">
                            <span class="g-panel-text">CONTENT</span>
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
                                                    <th>
                                                        <div class="th-inner ">Description</div>
                                                    </th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:if test="${empty pkgProgramList}">
                                                    <tr class="no-records-found">
                                                        <td colspan="4">
                                                            <div class="alert alert-info paxinfonext"> <strong>Hint:</strong>
                                                                There is no data!
                                                            </span>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:if>
                                            <c:if test="${not empty pkgProgramList}">
                                                <c:forEach items="${pkgProgramList}" var="item">
                                                    <tr>
                                                        <td> <c:out value="${item.name}" escapeXml="true" /></td>
                                                        <td> <c:out value="${item.version}" escapeXml="true" /></td>
                                                        <td> <c:out value="${item.type}" escapeXml="true" /></td>
                                                        <td>
                                                            <div class="table-w"> <c:out value="${item.desc}" escapeXml="true" /></div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                </div>
            </div>
            <!-- 内容区域end--> </c:if>
    </div>
</div>
</div>
<jsp:directive.include file="../includes/bottom.jsp" />
<script type="text/javascript">seajs.use('package-info');</script>
</body>
</html>