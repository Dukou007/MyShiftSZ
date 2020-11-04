package com.pax.tms.testing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ServiceBehavier2 {

	Dao dao = new Dao();

	private Service service = new Service();

	@Test
	public void test() {

		// 1. mock对象
		MockUp<Dao> mockUp = new MockUp<Dao>() {

			@Mock
			public int getStoreCount(String group) {
				return 2000;
			}
		};

		// 2. 获取实例
		dao = mockUp.getMockInstance();
		service = new Service();
		service.setDao(dao);

		// 3.调用
		Assert.assertEquals(Status.SELLINGWELL, service.checkStatus("FFF"));

		// 4.
		// 还原对象，避免测试方法之间互相影响。其实对一个实例来说没什么影响，对静态方法影响较大。旧版本的tearDown()方法是Mockit类的静态方法
		// mockUp.tearDown();
	}

}
