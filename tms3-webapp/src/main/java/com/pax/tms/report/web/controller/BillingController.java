package com.pax.tms.report.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.report.service.BillingService;
import com.pax.tms.report.web.form.QueryBillingForm;
import com.pax.tms.terminal.model.TerminalStatus;

@Controller
@RequestMapping("/report")
public class BillingController extends BaseController {
	
	@Autowired
	private BillingService billingService;
	
	@Autowired
	private GroupService groupService;
	
	private static final String REPORT_LIST_URL = "/report/billingReport/";
	private static final String REPORT_TITLE = "REPORTS";
	
	@RequestMapping("/billingReport/{groupId}")
	@RequiresPermissions(value = "tms:report:billing")
	public ModelAndView getList(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mv = new ModelAndView("report/billingReport");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		groupService.checkPermissionOfGroup(command, groupId);
		mv.addObject("group", groupService.get(groupId));
		mv.addObject("activeUrl", REPORT_LIST_URL);
		mv.addObject("title", REPORT_TITLE);
		return mv;
	}
	/**
	 * 获取计费终端总列表
	 * @param groupId
	 * @param command
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/service/billingList/{groupId}")
	@RequiresPermissions(value = "tms:report:billing")
	public Page<Map<String, Object>> getBillingList(@PathVariable Long groupId, QueryBillingForm command) {
		command.setGroupId(groupId);
		Page<Map<String, Object>> result = billingService.getBillingList(command);
		return result;
	}
	
	/**
	 * 下载终端计费月份详细数据PDF
	 * @param groupId
	 * @param command
	 * @param response
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	@RequiresPermissions(value = "tms:report:billing")
	@RequestMapping(value = "/billingList/export/{groupId}", method = { RequestMethod.GET, RequestMethod.POST })
	public void export(@PathVariable Long groupId, QueryBillingForm command, HttpServletResponse response) throws IOException {
		String month = monthFormatEa(command.getMonth());
		Group group = groupService.get(groupId);
		String groupName = group.getName();
		String pdfName = groupName+"-Billing-"+monthFormat(month)+".pdf";
		List<TerminalStatus> tslist = billingService.getBillingTerminalStatusList(month, groupId);
		// 设置响应头，控制浏览器下载该文件
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(pdfName, "UTF-8"));
        //常用的有paragraph段落、phrase语句块、chunk最小单位块
        try {
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI,BaseFont.NOT_EMBEDDED);
            Font font = new Font(baseFont, 12, Font.NORMAL);

            // 创建输出流
            OutputStream out = response.getOutputStream();

            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, out);

            doc.open();

            //段落文本
            Paragraph paragraphBlue = new Paragraph("", font);
            paragraphBlue.setLeading(12f);// 行间距
            paragraphBlue.setAlignment(Element.ALIGN_CENTER); 
            Font font1 = new Font(baseFont, 18, Font.BOLD);
            Chunk chunk1 = new Chunk("Billing Report");
            chunk1.setFont(font1);
            paragraphBlue.add(chunk1);  
            doc.add(paragraphBlue);
            
            Font font2 = new Font(baseFont, 16, Font.NORMAL);
            Paragraph paragraphBlue1 = new Paragraph("", font2);
            paragraphBlue1.setSpacingBefore(20);
            doc.add(paragraphBlue1);
            
            Paragraph paragraphBlue2 = new Paragraph("Month: "+month+"               "+"Total terminals: "+tslist.size()+"         Time Zone: UTC", font2);
            paragraphBlue2.setAlignment(Element.ALIGN_CENTER);
            paragraphBlue2.setSpacingBefore(20);
            doc.add(paragraphBlue2);
            
            //创建表格
            PdfPTable table = new PdfPTable(4);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中  
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE); 
            table.getDefaultCell().setFixedHeight(30);
            table.getDefaultCell().setBorderWidth(0.5f);// 边框宽度

            // 添加表头元素
            table.addCell(new Paragraph("Terminal SN", font));
            table.addCell(new Paragraph("Group Name", font));
            table.addCell(new Paragraph("Terminal Type", font));
            table.addCell(new Paragraph("Last Accessed Time", font));

            // 添加表格的内容
            for(TerminalStatus ts:tslist) {
            	table.addCell(new Paragraph(ts.getTsn(), font));
            	table.addCell(new Paragraph(groupName, font));
            	table.addCell(new Paragraph(ts.getModel().getName(), font));
            	table.addCell(new Paragraph(strToDate(ts.getLastConnTime()), font));
            }
            table.setSpacingBefore(10f);// 设置表格上面空白宽度
            doc.add(table);

            doc.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
	}
	
	 private String strToDate(Date date) {
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM/dd/yyyy");
	    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	    String str = sdf.format(date);
	    return str;
	 }
	 
	 private String monthFormat(String month) {
		 String[] ms = month.split("/");
		 if(ms[0].length() == 1) {
			 return "0"+ms[0]+ms[1];
		 }
		 return ms[0]+ms[1];
	 }
	 
	 private String monthFormatEa(String month) {
		 String[] ms = month.split("/");
		 if(ms[0].length() == 1) {
			 return "0"+ms[0]+"/"+ms[1];
		 }
		 return ms[0]+"/"+ms[1];
	 }
}
