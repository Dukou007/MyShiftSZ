<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ResourceBundle"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page import="com.pax.tms.web.controller.PPMVersion"%>
<%
	String version = PPMVersion.getPPMVersion();
	String messageURL = request.getScheme()+"://"+request.getServerName(); 
	ResourceBundle resource = ResourceBundle.getBundle("tms");
%>
<!-- footer -->
<div class="g-footer-content">
	Mailbox： <a href="#">ppmsupport@pax.us</a> &nbsp;&nbsp;Copyright © 2020 PAX All Rights Reserved.
</div>
<!-- footer end -->
</div>
<!-- modal change group -->
<div class="modal fade" id="group-tree">
	<div class="modal-dialog ">
		<div class="modal-content">
			<div class="modal-header g-modal-title">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">CHANGE GROUP</h4>
			</div>
			<div class="modal-body">
				<div class="g-search">

					<div class="input-group">
						<input class="form-control g-searchInput" type="text" placeholder="Search Here">
						<div class="input-group-addon g-searchGo btn " id="g-searchGo">Go</div>
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
		<!-- /.modal-dialog -->
	</div>
</div>

<!-- modal PXMaster version -->
<div class="modal fade" id="about-pxmaster">
	<div class="modal-dialog ">
		<div class="modal-content">
			<div class="modal-header g-modal-title">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">ABOUT PAX PORTFOLIO MANAGER</h4>
			</div>
			<div class="modal-body2 text-left">
				<div>PAX Portfolio Manager version is <%=version%></div>
				<div class="server-title">Message Server:</div>
				<div class="server-context">URL: <%=messageURL%></div>
				<div class="server-context">Port: <%=resource.getString("pxretailer.tcp.port")%></div>
			</div>
			<!-- /.modal-content -->
			<div class="modal-footer">
				<div>
					<button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
				</div>

			</div>
		</div>
		<!-- /.modal-dialog -->
	</div>
</div>

<!-- modal sysinfo -->
<div class="modal fade" id="sysinfo">
	<div class="modal-dialog ">
		<div class="modal-content">
			<div class="modal-header g-modal-title">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span> <span class="sr-only">Close</span>
				</button>
				<h4 class="modal-title">ABOUT SYSTEM INFORMATION</h4>
			</div>
			<div class="modal-body">
				
			</div>
			<!-- /.modal-content -->
			<div class="modal-footer">
				<div>
					<button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
				</div>

			</div>
		</div>
		<!-- /.modal-dialog -->
	</div>
</div>

</div>

