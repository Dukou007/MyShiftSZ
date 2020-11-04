package com.pax.tms.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public class MongoApp {

	public static void main(String[] args) throws Exception {

		ServerAddress serverAddress = new ServerAddress("192.168.6.174", 27017);

		MongoClientOptions options = MongoClientOptions.builder().legacyDefaults().build();

		MongoTemplate template = new MongoTemplate(new MongoClient(serverAddress, options), "tms3");
		template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		MongoOperations mongoOps = template;

		Map<String, Object> map = new HashMap<>();
		map.put("name", "Joe");
		map.put("age", 34);

		mongoOps.insert(map, "map1");

		System.out.println(mongoOps.findOne(new Query(where("name").is("Joe")), Map.class, "map1"));

		// long t = System.currentTimeMillis();
		// for (int i = 0; i < 100000; i++) {
		// mongoOps.insert(new Person("Joe", 34));
		// }
		// System.out.println((System.currentTimeMillis() - t) / (1000 * 1000));

		System.out.println(mongoOps.findOne(new Query(where("name").is("Joe")), Person.class).toString());

		// mongoOps.dropCollection("person");
	}

}
