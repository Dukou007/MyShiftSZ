package com.pax.tms.report.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.report.dao.BillingDao;
import com.pax.tms.report.dao.TerminalBillingDao;
import com.pax.tms.report.dao.TerminalBillingDetailDao;
import com.pax.tms.report.domain.BillingTerminalStatus;
import com.pax.tms.report.mail.BillingReportMailSender;
import com.pax.tms.report.model.TerminalBilling;
import com.pax.tms.report.model.TerminalBillingDetail;
import com.pax.tms.report.web.form.QueryBillingForm;
import com.pax.tms.res.model.Model;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.terminal.model.TerminalStatus;
import com.pax.tms.user.dao.UserRoleDao;
import com.pax.tms.user.model.User;

@Service("billingServiceImpl")
public class BillingServiceImpl extends BaseService<TerminalStatus, Long> implements BillingService {
	
	private String statement = "Download";//规定好了状态
	
	@Autowired
	private BillingDao billingDao;
	
	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	private TerminalBillingDao terminalBillingDao;
	
	@Autowired
	private TerminalBillingDetailDao terminalBillingDetailDao;
	
	@Autowired(required = false)
	private BillingReportMailSender billingReportMailSender;
	
	@Autowired
	private UserRoleDao userRoleDao;
	
	@Autowired
	private ModelService modelService;
	
	@Override
	public IBaseDao<TerminalStatus, Long> getBaseDao() {
		return billingDao;
	}
	
	@Override
	public Page<Map<String, Object>> getBillingList(QueryBillingForm command) {
		Long groupId = command.getGroupId();
		Page<Map<String, Object>> page = new Page<>(command.getPageIndex(), command.getPageSize());
		List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
		List<TerminalBilling> tbs = terminalBillingDao.getTerminalBillingListByGroupId(groupId);
		for (TerminalBilling tb:tbs) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Month", tb.getMonth());
			map.put("Connected Devices", tb.getConnectedDevices());
			map.put("Statement", tb.getStatement());
			items.add(map);
		}
		page.setTotalCount(tbs.size());
		List<Map<String, Object>> temp = new ArrayList<Map<String,Object>>();
		if (1 == command.getPageIndex()) {//如果是第一页,总共就两页
			if (command.getPageSize() < items.size()) {
				for (int i=0; i< command.getPageSize(); i++) {
					temp.add(items.get(i));
				}
			} else {
					temp.addAll(items);
			}
		} else {
			for (int i=command.getPageSize(); i< tbs.size(); i++) {
				temp.add(items.get(i));
			}
		}
		page.setItems(temp);
		return page;
	}
	/**
	 * for循环事务没有提交
	 */
	@Override
	public void getCountBillingListTask() {
		//初始化tmsttrmstatus_temp临时表数据
		billingDao.truncateTmsttrmStatusTemp();
		billingDao.initTmsttrmStatusTemp();
		List<Group> groups = groupDao.getEnterpriseAndRootGroups();
		//统计总表详情，应该与统计月份互不影响
		for(Group g:groups) {
			Long cd = billingDao.getBillingMonthCount(getUTCFirstDayOfCurrentMonth(), getUTCLastDayOfCurrentMonth(), g.getId());
			TerminalBilling tb = new TerminalBilling();
			tb.setGroupId(g.getId());
			tb.setStatement(statement);
			tb.setMonth(getUTCCurrentMonth());
			tb.setConnectedDevices(cd);
			tb.setCreateTime(new Date());
			terminalBillingDao.save(tb);
		}
		//统计月份详情
		List<Model> modelList = modelService.getList();
		Map<String, Model> modelMap = modelList.stream().collect(Collectors.toMap(Model::getId,Function.identity()));
		for (Group g:groups) {
			List<BillingTerminalStatus> bts = billingDao.getBillingTerminalStatusTsnList(getUTCFirstDayOfCurrentMonth(), getUTCLastDayOfCurrentMonth(), g.getId());
			List<TerminalStatus> temp = new LinkedList<TerminalStatus>();
			for (BillingTerminalStatus bt:bts) {
				TerminalStatus ts = new TerminalStatus();
				Model model = modelMap.get(bt.getMODEL_ID());
				ts.setTsn(bt.getTRM_SN());
				ts.setModel(model);
				ts.setLastConnTime(bt.getLAST_CONN_TIME());
				ts.setOnlineSince(bt.getONLINE_SINCE());
				ts.setOfflineSince(bt.getOFFLINE_SINCE());
				temp.add(ts);
			}
			List<TerminalBillingDetail> tbds = terminaStatusToTerminalBillingDetails(temp, g, getUTCCurrentMonth());
			terminalBillingDetailDao.batchInsert(tbds);
			if (1 < groups.size()) {
				terminalBillingDetailDao.flush();
			}
		}
		//回收临时表数据
		billingDao.truncateTmsttrmStatusTemp();
	}

	@Override
	public List<TerminalStatus> getBillingTerminalStatusList(String month, Long groupId) {
		List<TerminalStatus> result = new LinkedList<TerminalStatus>();
		List<TerminalBillingDetail> tbds = terminalBillingDetailDao.getDetailByGroupId(groupId, month);
		for (TerminalBillingDetail tbd:tbds) {
			TerminalStatus ts = new TerminalStatus();
			Model model = new Model();
			model.setId(tbd.getType());
			model.setName(tbd.getType());
			ts.setTsn(tbd.getTsn());
			ts.setModel(model);
			ts.setLastConnTime(tbd.getLastAccessTime());
			result.add(ts);
		}
		return result;
	}
	
	@Override
	public void sendEmailTaskTailedToSizeAdmin() {
		Long siteAdminId = 1l;
		List<User> siteAdmins = userRoleDao.getUsersHasRole(siteAdminId);
		for (User u:siteAdmins) {
			if (null == u.getEmail() || "".equals(u.getEmail())) {
				continue;
			}
			billingReportMailSender.sendMail(u.getEmail(), u.getFullname());
		}
	}
    
    /**
     * 获取当UTC时间的前月第一天
     * @param month
     * @return
     */
    public static String getUTCFirstDayOfCurrentMonth() {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar c = Calendar.getInstance();  
    	 // 2、取得时间偏移量：
        int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = c.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        c.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        String first = format.format(c.getTime())+ " 00:00:00";
        return first;
    }
    
    /**
     * 获取当前月最后一天
     * @param month
     * @return
     */
    public static String getUTCLastDayOfCurrentMonth() {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar ca = Calendar.getInstance();  
    	 // 2、取得时间偏移量：
        int zoneOffset = ca.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = ca.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        ca.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH)); 
        String last = format.format(ca.getTime())+ " 23:59:59";
        return last;
    }
    
    public static String getUTCCurrentMonth() {
    	SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
    	Calendar ca = Calendar.getInstance();   
    	 // 2、取得时间偏移量：
        int zoneOffset = ca.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = ca.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        ca.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH)); 
        String month = format.format(ca.getTime());
        return month;
    }
    
    public static Date getUTCDate() {
    	Calendar ca = Calendar.getInstance();
    	 // 2、取得时间偏移量：
        int zoneOffset = ca.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = ca.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        ca.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
    	return ca.getTime();
    }
    
    /**
     * 把terminaStatus集合转换成TerminalBillingDetail集合
     * @param terminalStatusList
     * @param group
     * @param month
     * @return
     */
    private List<TerminalBillingDetail> terminaStatusToTerminalBillingDetails(List<TerminalStatus> terminalStatusList, Group group, String month) {
    	List<TerminalBillingDetail> tbds = new ArrayList<>();
    	for (TerminalStatus ts:terminalStatusList) {
    		TerminalBillingDetail tbd = new TerminalBillingDetail();
    		tbd.setGroupId(group.getId());
    		tbd.setGroupName(group.getName());
//    		tbd.setLastAccessTime(ts.getLastConnTime());
    		Date onlineSince = ts.getOnlineSince();
    		Date offlineSince = ts.getOfflineSince();
    		if(offlineSince == null || onlineSince.after(offlineSince)){
    		    tbd.setLastAccessTime(onlineSince);
    		}else {
    		    tbd.setLastAccessTime(offlineSince);
    		}
    		tbd.setTsn(ts.getTsn());
    		tbd.setType(ts.getModel().getName());
    		tbd.setMonth(month);
    		tbd.setCreateTime(new Date());
    		tbds.add(tbd);
    	}
    	return tbds;
    }
    
}
