package com.pax.tms.monitor.dev;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class MainTest {

	public static void main(String[] args) throws IOException {
		InputStream in = MainTest.class.getClassLoader().getResourceAsStream("alertCondition.json");
		String condJson = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
//		System.out.println(condJson);
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("eventId", new BigDecimal(100));
		list.add(map1);
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("eventId", new BigDecimal(200));
		list.add(map2);
		System.out.println(sortList(list, "asc"));
		
	}
	
	private static List<Map<String, Object>> sortList(List<Map<String, Object>> list, final String type) {
		list.sort((map1, map2) -> {
			BigDecimal n1 = (BigDecimal) map1.get("eventId");
			BigDecimal n2 = (BigDecimal) map2.get("eventId");

			if ("asc".equals(type)) {
				return n1.compareTo(n2);
			} else {
				return n2.compareTo(n1);
			}
		});
		return list;
	}

	@Test
	public void testTimeZone() {
		ZonedDateTime tz = ZonedDateTime.now();
		System.out.println(tz);
	}
}
