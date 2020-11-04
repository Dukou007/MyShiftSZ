package com.pax.tms.group.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.pax.common.util.FileDigest;
import com.pax.common.util.RegexMatchUtils;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.group.model.Group;

public class CloneTest {

	@Test
	public void testTerminalGroup() throws CloneNotSupportedException {

		Group group1 = new Group();
		group1.setIdPath("1/1_1");
		group1.setNamePath("2/2_2");
		Group group2 = new Group();
		group2.setIdPath("1/1_1");
		group2.setNamePath("2/2_2");
		System.out.println("group2:" + group2.getIdPath() + "--" + group2.getNamePath());

		group2.setIdPath("3/3_3");
		group2.setNamePath("4/4_4");
		System.out.println(group1 == group2);
		System.out.println("group1:" + group1.getIdPath() + "--" + group1.getNamePath());
		System.out.println("group2:" + group2.getIdPath() + "--" + group2.getNamePath());

	}

	@Test
	public void testReplace() {
		String string = "1000/3000/131";
		String string2 = string.replace("1000/3000", "1000/2000/3000");
		System.out.println(string2);

	}

	@Test
	public void testRef() {

		Group group = new Group();
		// group.setId(1L);
		Group target = group;

		group = null;
		System.out.println(target.getId());

	}

	@Test
	public void testRef1() {

		Group group = new Group();
		group.setId(1L);
		Group target = group;
		target.setId(2L);
		System.out.println(group.getId());

	}

	@Test
	public void testEqualObject() {

		Group group = new Group();
		group.setName("group");
		Group group1 = new Group();
		group.setId(1L);
		List<Group> groups = new ArrayList<>();
		groups.add(group);
		group1.setId(1L);
		group.setName("group1");
		System.out.println(groups.contains(group1));

	}

	@Test
	public void testRegexMatch() {
		String name = "ddsf";
		System.out.println(RegexMatchUtils.contains(name, Group.INVALID_GROUP_NAME_REGEX));

		name = "/";
		System.out.println(RegexMatchUtils.contains(name, Group.INVALID_GROUP_NAME_REGEX));

		name = "|";
		System.out.println(RegexMatchUtils.contains(name, Group.INVALID_GROUP_NAME_REGEX));

		name = ".";
		System.out.println(RegexMatchUtils.contains(name, Group.INVALID_GROUP_NAME_REGEX));

		name = "\\";
		System.out.println(RegexMatchUtils.contains(name, Group.INVALID_GROUP_NAME_REGEX));

	}

	@Test
	public void testEnum() {
		System.out.println(DownOrActvStatus.DOWNLOADING.name());

	}

	@Test
	public void testString() {
		String str = "123";
		String str1 = "123";
		int index = getPrefixIndex(str, str1);
		System.out.println(index);
		System.out.println(str.substring(0, index));
		System.out.println(str.substring(0, str.length() - 1));
	}

	private static int getPrefixIndex(String start, String end) {
		int p = 0;
		for (; p < start.length(); p++) {
			if (start.charAt(p) != end.charAt(p)) {
				break;
			}
		}
		return p;
	}

	@Test
	public void testPattern() {
		String tid = "AAAAAA1170w4G444";
		String pattern = "^[0-9 A-Z]{8}|^[0-9 A-Z]{10}|^[0-9 A-Z]{16}";
		System.out.println(RegexMatchUtils.isMatcher(tid, pattern));

	}

	@Test
	public void testSplit() {
		String str = "group/group1/11111111";
		int index = str.lastIndexOf("/");
		String groupPath = str.substring(0, index);
		String tid = str.substring(index + 1, str.length());
		System.out.println(groupPath);
		System.out.println(tid);

	}

	@Test
	public void testSet() {
		Set<Person> set = new HashSet<Person>();
		Person p = new Person("abc", 18);
		set.add(p);
		Person p1 = new Person("abc", 18);
		set.add(p1);
		System.out.println(set.size());
		System.out.println(DownOrActvStatus.PENDING.name());

	}

	@Test
	public void testList() {
		HashSet<String> ht = new HashSet<String>();
		ht.add("aa");
		ht.add("bb");
		ht.add("cc");
		List<String> al = new ArrayList<String>();
		al.add("11");
		al.add("aa");
		ht.addAll(al);
		for (String s : ht) {
			System.out.println(s);
		}

	}

	@Test
	public void testHashMap() {
		Map<Object, String> map = new HashMap<Object, String>();
		map.put("aa", "1");
		map.put("bb", "3");
		map.put("cc", "2");
		map.put("dd", "1");
		map.put("ee", "2");
		for (Map.Entry<Object, String> entry : map.entrySet()) {
			System.out.println("key:" + entry.getKey() + "value:" + entry.getValue());

		}
	}

	@Test
	public void testMD5() throws IOException {
		System.out.println(FileDigest.sha256Hex("d:/manifest.xml"));
		List<String> collected = new ArrayList<>();
		collected.add("alpha");
		collected.add("beta");
		collected = collected.stream().map(String::toUpperCase).collect(Collectors.toCollection(ArrayList::new));// 注意发生的变化
		System.out.println(collected);
		Map<String, String> env = System.getenv();
		for (String name : env.keySet()) {
			System.out.println(name + "--->" + env.get(name));
		}
		System.out.println("**************************************");
		Properties pros = System.getProperties();

		for (Object name : pros.keySet()) {
			System.out.println(name + "--->" + pros.getProperty((String) name));
		}

	}

}
