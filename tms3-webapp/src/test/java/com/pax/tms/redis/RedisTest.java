package com.pax.tms.redis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.message.MessageSender;

public class RedisTest extends ServiceJunitCase {

	@Autowired
	private MessageSender messageSender;

	@Test
	public void testPublishMessage() {
		messageSender.publish("tms.user.status.changed", 3L);

		List<Long> list = new ArrayList<>();
		list.add(4L);
		messageSender.batchPublish("tms.user.status.changed", list);
		try {
			Thread.sleep(1000 * 3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
