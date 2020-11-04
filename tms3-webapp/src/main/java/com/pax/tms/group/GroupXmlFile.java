/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.group;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.pax.common.exception.BusinessException;
import com.pax.common.security.XEEProtectUtils;
import com.pax.common.util.RegexMatchUtils;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.web.form.ImportFileForm;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.model.Province;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.location.service.CountryService;
import com.pax.tms.location.service.ProvinceService;
import com.pax.tms.location.web.form.AddProvinceForm;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.terminal.domain.TerminalInfo;
import com.pax.tms.terminal.model.Terminal;

@SuppressWarnings("all")
public class GroupXmlFile {

	private static final String GROUP_ELEMENT = "group";
	private static final String GROUP_ROOT = "groups";
	private static final String TERMINAL_ELEMENT = "terminal";
	private static final String TERMINAL_ELEMENTS = "terminals";
	private static ModelService modelService;
	private static CountryService countryService;
	private static ProvinceService provinceService;
	private static AddressService addressService;
	private static List<String> daylightSavings;
	private static List<String> syncToServerTimes;
	
	private static String USER_NAME = "default";//登录用户名
	private static Date REQUEST_TIME = new Date();//请求时间

	private GroupXmlFile() {
	}

	static {
		daylightSavings = Arrays.asList("Enable", "Disable");
		syncToServerTimes = Arrays.asList("Enable", "Disable");
	}

	public static void setModelService(ModelService modelService) {
		GroupXmlFile.modelService = modelService;
	}

	public static void setCountryService(CountryService countryService) {
		GroupXmlFile.countryService = countryService;
	}

	public static void setProvinceService(ProvinceService provinceService) {
		GroupXmlFile.provinceService = provinceService;
	}

	public static void setAddressService(AddressService addressService) {
		GroupXmlFile.addressService = addressService;
	}

	public static GroupInfo parse(InputStream inputStream, boolean isPermissionCreateGroup, ImportFileForm command) throws DocumentException {
		Element root = getRootElement(inputStream);
		if(null != command.getLoginUsername()&&!"".equals(command.getLoginUsername())){
			USER_NAME = command.getLoginUsername();
		}
		if(null != command.getRequestTime()){
			REQUEST_TIME = command.getRequestTime();
		}
		GroupInfo rootGroupInfo = new GroupInfo();// 虚拟一个parent root 根
		if (isPermissionCreateGroup) {
			parseGroupTerminal(root, rootGroupInfo);
		} else {
			parseTerminal(root, rootGroupInfo);
		}
		return rootGroupInfo;

	}

	private static void parseGroupTerminal(Element root, GroupInfo rootGroupInfo) {

		if (!isMatchRootName(root, GROUP_ROOT) && !isMatchRootName(root, TERMINAL_ELEMENTS)) {
			throw new BusinessException("msg.importFile.group.rootNode.notMatch");
		}
		if (isMatchRootName(root, GROUP_ROOT)) {
			List<Element> groupElements = root.elements(GROUP_ELEMENT);
			if (CollectionUtils.isEmpty(groupElements)) {
				throw new BusinessException("msg.importFile.noGroupElement");
			}
			for (Element groupElement : groupElements) {
				GroupInfo childGroup = createGroupInfo(groupElement);
				rootGroupInfo.addChildGroup(childGroup);
				parseGroupElement(groupElement, childGroup);
			}

		} else {
			parseTerminal(root, rootGroupInfo);

		}

	}

	private static boolean isMatchRootName(Element root, String matchNode) {
		String rootNodeName = root.getName();
		if (StringUtils.equalsIgnoreCase(rootNodeName, matchNode)) {
			return true;
		}
		return false;
	}

	private static void parseTerminal(Element root, GroupInfo rootGroupInfo) {

		if (!isMatchRootName(root, TERMINAL_ELEMENTS)) {
			throw new BusinessException("msg.importFile.terminal.rootNode.notMatch");
		}
		List<Element> groupElements = root.elements(GROUP_ELEMENT);
		if (CollectionUtils.isNotEmpty(groupElements)) {
			throw new BusinessException("msg.importFile.notAllowGroupElement");
		}
		List<Element> childTerminalElements = root.elements(TERMINAL_ELEMENT);
		parseChildTerminalElement(rootGroupInfo, childTerminalElements);

	}

	private static Element getRootElement(InputStream inputStream) throws DocumentException {

		SAXReader reader = XEEProtectUtils.createSAXReader();
		Document doc = reader.read(inputStream);
		Element root = doc.getRootElement();
		return root;
	}

	@SuppressWarnings("unchecked")
	private static void parseGroupElement(Element groupElement, GroupInfo rootGroupInfo) {

		List<Element> childTerminalsElements = groupElement.elements(TERMINAL_ELEMENTS);
		if (CollectionUtils.isNotEmpty(childTerminalsElements)) {
			for (Element childTerminaslElement : childTerminalsElements) {

				List<Element> childTerminalElements = childTerminaslElement.elements(TERMINAL_ELEMENT);
				parseChildTerminalElement(rootGroupInfo, childTerminalElements);

			}
		}

		List<Element> childElements = groupElement.elements(GROUP_ELEMENT);
		for (Element childElement : childElements) {
			GroupInfo childGrouInfo = createGroupInfo(childElement);
			rootGroupInfo.addChildGroup(childGrouInfo);
			parseGroupElement(childElement, childGrouInfo);
		}
	}

	private static void parseChildTerminalElement(GroupInfo rootGroupInfo, List<Element> childTerminalElements) {

		if (CollectionUtils.isEmpty(childTerminalElements)) {
			return;
		}
		for (Element terminalElement : childTerminalElements) {
			TerminalInfo terminalInfo = createTerminalInfo(terminalElement, rootGroupInfo);
			rootGroupInfo.addChildTerminal(terminalInfo);
		}

	}

	private static TerminalInfo createTerminalInfo(Element terminalElement, GroupInfo rootGroupInfo) {
		validateTerminalInput(terminalElement, rootGroupInfo);
		modelService.validateModel(terminalElement.attributeValue("terminalType"));
		TerminalInfo terminalInfo = new TerminalInfo();
		String countryName = terminalElement.attributeValue("country");
		Country country = countryService.getCountryByImportName(countryName);
		String provinceName = terminalElement.attributeValue("stateorprovice");
		Province province = provinceService.getProvinceByImportName(country.getId(), provinceName);
		Map<String, Object> timeZone = addressService.getParentTimeZone(country.getName(),
				terminalElement.attributeValue("timeZone"));
		terminalInfo.setTid(terminalElement.attributeValue("tid"));
		terminalInfo.setDestModel(terminalElement.attributeValue("terminalType"));
		terminalInfo.setCountry(country.getName());
		terminalInfo.setProvince(province.getName());
		terminalInfo.setCity(terminalElement.attributeValue("city"));
		terminalInfo.setZipcode(terminalElement.attributeValue("zipcode"));
		terminalInfo.setTimeZone((String) timeZone.get("timeZoneId"));
		terminalInfo.setAddress(terminalElement.attributeValue("address"));
		terminalInfo.setDaylight(terminalElement.attributeValue("daylight"));
		terminalInfo.setSyncToServerTime(terminalElement.attributeValue("syncToServerTime"));
		terminalInfo.setDescription(terminalElement.attributeValue("description"));
		return terminalInfo;
	}
	//修改如果终端country、province、zipcode等字段为空则复制它的直属第一级父类(父亲)的相应属性
	//详细字段: Country、State/Province、city、timeZone、daylight、zipcode、address、description
	public static void validateTerminalInput(Element terminalElement, GroupInfo rootGroupInfo) {
		String tid = terminalElement.attributeValue("tid");
		String countryName = terminalElement.attributeValue("country");
		String provinceName = terminalElement.attributeValue("stateorprovice");
		String city = terminalElement.attributeValue("city");
		String zipCode = terminalElement.attributeValue("zipcode");
		String timeZone = terminalElement.attributeValue("timeZone");
		String daylight = terminalElement.attributeValue("daylight");
		String address = terminalElement.attributeValue("address");
		String description = terminalElement.attributeValue("description");
		if (StringUtils.isEmpty(tid)) {
			throw new BusinessException("tsn.Required");
		}
		if (!RegexMatchUtils.isMatcher(tid, Terminal.TSN_REGEX)) {
			throw new BusinessException("tsn.invalidTsn", new String[] { tid });
		}
		if (StringUtils.isEmpty(countryName)) {
			terminalElement.addAttribute("country", rootGroupInfo.getCountry());
			countryName = terminalElement.attributeValue("country");
			//如果连根节点都没有country，则无法导入;province、city和zipcode一样
			if (null == countryName || "".equals(countryName)) {
				throw new BusinessException("msg.importFile.tsn.country.required", new String[] { tid });
			}
		}
		//理论上存在根组那么国家如果不存在是不会到这一步的
		Country country = countryService.getCountryByImportName(countryName);
		if (country == null) {
			throw new BusinessException("msg.importFile.tsn.country.notFound", new String[] { tid });
		}
		
		if (StringUtils.isEmpty(provinceName)) {
			terminalElement.addAttribute("stateorprovice", rootGroupInfo.getStateOrProvice());
			provinceName = terminalElement.attributeValue("stateorprovice");
			if(null == provinceName || "".equals(provinceName)){
				throw new BusinessException("msg.importFile.tsn.province.required", new String[] { tid });
			}
		}
		Province province = provinceService.getProvinceByImportName(country.getId(), provinceName);
		Long provinceCounts = provinceService.count();
		if (province == null) {
			Province s = new Province();
			s.setCountry(country);
			s.setName(provinceName);
			s.setCreator(USER_NAME);
			s.setCreateDate(REQUEST_TIME);
			s.setModifier(USER_NAME);
			s.setModifyDate(REQUEST_TIME);
			provinceService.save(s);
		}
		
		if (StringUtils.isEmpty(city)) {
			terminalElement.addAttribute("city", rootGroupInfo.getCity());
			city = terminalElement.attributeValue("city");
			if (null == city || "".equals(city)) {
				throw new BusinessException("msg.importFile.tsn.city.required", new String[] { tid });
			}
		}

		if (StringUtils.isEmpty(zipCode)) {
			terminalElement.addAttribute("zipcode", rootGroupInfo.getZipCode());
			zipCode = terminalElement.attributeValue("zipcode");
			if (null == zipCode || "".equals(zipCode)) {
				throw new BusinessException("msg.importFile.tsn.zipcodeRequired", new String[] { tid });
			}
		}
		if (zipCode.trim().length() > 7) {	
			throw new BusinessException("msg.importFile.tsn.zipcodeOverLength", new String[] { tid });
		}

		if (StringUtils.isEmpty(terminalElement.attributeValue("terminalType"))) {
			throw new BusinessException("msg.importFile.model.Required", new String[] { tid });
		}
		
		if (StringUtils.isEmpty(timeZone)) {
			terminalElement.addAttribute("timeZone", rootGroupInfo.getTimeZone());
			timeZone = terminalElement.attributeValue("timeZone");
		}
		List<String> timeZones = addressService.getTimeZoneIds(country.getName());
		if (!IsMatchCollstr(timeZone, timeZones)) {
			throw new BusinessException("msg.importFile.tsn.timeZone.match",
					new String[] { tid, StringUtils.join(timeZones, ", ") });
		}

		if (StringUtils.isEmpty(daylight)) {
			terminalElement.addAttribute("daylight", rootGroupInfo.getDaylight());
			daylight = terminalElement.attributeValue("daylight");
		}
		if (!IsMatchCollstr(daylight, daylightSavings)) {
			throw new BusinessException("msg.importFile.tsn.daylight.match",
					new String[] { tid, StringUtils.join(daylightSavings, ",") });
		}

		Map<String, Object> timeZoneObject = addressService.getParentTimeZone(countryName, timeZone);
		String isDaylightSaving = (String) timeZoneObject.get("isDaylightSaving");
		if ("Enable".equals(daylight) && "0".equals(isDaylightSaving)) {
			throw new BusinessException("msg.importFile.timeZone.dayLight.invalid", new String[] { timeZone });
		}

		String syncToServerTime = terminalElement.attributeValue("syncToServerTime");
		if (StringUtils.isEmpty(syncToServerTime)) {
			throw new BusinessException("msg.importFile.tsn.syncToServerTime.Required", new String[] { tid });

		}
		
		if (!IsMatchCollstr(syncToServerTime, syncToServerTimes)) {
			throw new BusinessException("msg.importFile.tsn.syncToServerTime.match",
					new String[] { tid, StringUtils.join(syncToServerTimes, ",") });

		}
		
		if (StringUtils.isEmpty(address)) {
			terminalElement.addAttribute("address", rootGroupInfo.getAddress());
		}
		
		if (StringUtils.isEmpty(description)) {
			terminalElement.addAttribute("description", rootGroupInfo.getDescription());
		}

	}

	private static boolean IsMatchCollstr(String str, List<String> strs) {
		for (String s : strs) {
			if (StringUtils.equalsIgnoreCase(str, s)) {
				return true;
			}
		}
		return false;
	}

	private static GroupInfo createGroupInfo(Element groupElement) {

		validateGroupInput(groupElement);
		GroupInfo groupInfo = new GroupInfo();
		String countryName = groupElement.attributeValue("country");
		Country country = countryService.getCountryByImportName(countryName);
		String provinceName = groupElement.attributeValue("stateorprovice");
		Province province = provinceService.getProvinceByImportName(country.getId(), provinceName);
		Map<String, Object> timeZone = addressService.getParentTimeZone(country.getName(),
				groupElement.attributeValue("timeZone"));
		groupInfo.setName(groupElement.attributeValue("name"));
		groupInfo.setCountry(country.getName());
		groupInfo.setStateOrProvice(province.getName());
		groupInfo.setCity(groupElement.attributeValue("city"));
		groupInfo.setZipCode(groupElement.attributeValue("zipcode"));
		groupInfo.setTimeZone((String) timeZone.get("timeZoneId"));
		groupInfo.setDaylight(groupElement.attributeValue("daylight"));
		groupInfo.setAddress(groupElement.attributeValue("address"));
		groupInfo.setDescription(groupElement.attributeValue("description"));
		return groupInfo;
	}

	private static void validateGroupInput(Element groupElement) {
		String groupName = groupElement.attributeValue("name");
		String countryName = groupElement.attributeValue("country");
		String provinceName = groupElement.attributeValue("stateorprovice");
		Group.validateGroupName(groupName);
		if (StringUtils.isEmpty(countryName)) {
			throw new BusinessException("msg.importFile.country.required", new String[] { groupName });
		}
		Country country = countryService.getCountryByImportName(countryName);
		if (country == null) {
			throw new BusinessException("msg.importFile.country.notFound", new String[] { groupName });
		}
		Group.validateZipCode(groupElement.attributeValue("zipcode"), groupName, country.getId());
		if (StringUtils.isEmpty(provinceName)) {
			throw new BusinessException("msg.importFile.province.required", new String[] { groupName });
		}
		Province province = provinceService.getProvinceByImportName(country.getId(), provinceName);
		if (province == null) {
			Province s = new Province();
			s.setCountry(country);
			s.setName(provinceName);
			s.setCreator(USER_NAME);
			s.setCreateDate(REQUEST_TIME);
			s.setModifier(USER_NAME);
			s.setModifyDate(REQUEST_TIME);
			provinceService.save(s);
		}
		if (StringUtils.isEmpty(groupElement.attributeValue("city"))) {
			throw new BusinessException("msg.importFile.city.required", new String[] { groupName });
		}
		String timeZone = groupElement.attributeValue("timeZone");
		if (StringUtils.isEmpty(timeZone)) {
			throw new BusinessException("msg.importFile.group.timeZone.Required", new String[] { groupName });
		}
		List<String> timeZones = addressService.getTimeZoneIds(country.getName());
		if (!IsMatchCollstr(timeZone, timeZones)) {
			throw new BusinessException("msg.importFile.group.timeZone.match",
					new String[] { groupName, StringUtils.join(timeZones, ", ") });
		}

		String daylight = groupElement.attributeValue("daylight");
		if (StringUtils.isEmpty(daylight)) {
			throw new BusinessException("msg.importFile.group.daylight.Required", new String[] { groupName });

		}
		if (!IsMatchCollstr(daylight, daylightSavings)) {
			throw new BusinessException("msg.importFile.group.daylight.match",
					new String[] { groupName, StringUtils.join(daylightSavings, ",") });

		}
		Map<String, Object> timeZoneObject = addressService.getParentTimeZone(countryName, timeZone);
		String isDaylightSaving = (String) timeZoneObject.get("isDaylightSaving");
		if ("Enable".equals(daylight) && "0".equals(isDaylightSaving)) {
			throw new BusinessException("msg.importFile.timeZone.dayLight.invalid", new String[] { timeZone });
		}

	}

}
