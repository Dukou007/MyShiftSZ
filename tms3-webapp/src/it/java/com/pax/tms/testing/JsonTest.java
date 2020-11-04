package com.pax.tms.testing;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.Json;

public class JsonTest {

	public static void main(String[] args) {

		List<Person> list = new ArrayList<Person>();
		list.add(new Person("crazy", 108));
		list.add(new Person("cb", 8));
		
		System.out.println(Json.encode(list));
		
		String str = Json.encode(list);
		
		Json.decodeValue(str, List.class);

	}

	public static class Person {
		private String name;
		private int age;

		public Person(String name, int age) {
			super();
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

	}

}
