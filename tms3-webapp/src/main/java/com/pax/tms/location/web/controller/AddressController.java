package com.pax.tms.location.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pax.common.web.controller.BaseController;
import com.pax.common.web.support.editor.UTCDateEditor;
import com.pax.tms.location.model.Country;
import com.pax.tms.location.model.Province;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.location.service.CountryService;
import com.pax.tms.location.service.ProvinceService;

@Controller
@RequestMapping(value = "/address")
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private ProvinceService provinceService;

    @RequestMapping(value = "/service/getTimeZones")
    @ResponseBody
    public Object getTimeZones(Long countryId) {
        Map<String, Object> result = new HashMap<>();
        if (countryId == null) {
            result.put("timeZoneList", Collections.emptyList());
            result.put("provinceList", Collections.emptyList());
            return result;
        }
        List<Province> list = provinceService.getProvinceList(countryId);
        List<String[]> provinceList = new ArrayList<>(list.size() + 1);
        for (Province province : list) {
            String[] arr = new String[2];
            arr[0] = province.getId() + "";
            arr[1] = province.getName();
            provinceList.add(arr);
        }
        Country country = countryService.get(countryId);
        List<Map<String, String>> timeZoneList = addressService.getTimeZonesByCountry(country.getName());
        result.put("timeZoneList", timeZoneList);
        result.put("provinceList", provinceList);
        return result;
    }

    @RequestMapping(value = "/service/getNowTimeByTimeZone")
    @ResponseBody
    public Object getNowTimeByTimeZone(String timeZoneId) {
        String nowTime = UTCDateEditor.formatDate(new Date(), timeZoneId, true, "yyyy-MM-dd HH:mm:ss");
        return nowTime;
    }
}
