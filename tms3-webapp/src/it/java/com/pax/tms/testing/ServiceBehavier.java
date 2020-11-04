package com.pax.tms.testing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ServiceBehavier {

	@Mocked
	Dao dao = new Dao();

	private Service service = new Service();

	@Test
	public void test() {

		// 1. record 录制期望值
		new Expectations() {
			{
				/**
				 * 录制的方法
				 */
				dao.getStoreCount(anyString);// mock这个方法，无论传入任何String类型的值，都返回同样的值，达到黑盒的效果
				/**
				 * 预期结果，返回900
				 */
				result = 900;
				/**
				 * times必须调用两次。在Expectations中，必须调用，否则会报错，因此不需要作校验。
				 * 在NonStrictExpectations中不强制要求，但要进行verify验证.但似乎已经强制要求了
				 * 此外还有maxTimes，minTimes
				 */
				times = 1;
			}
		};

		service.setDao(dao);

		// 2. replay 调用
		Assert.assertEquals(Status.NORMAL, service.checkStatus("D"));

		// Assert.assertEquals(Status.NORMAL, service.checkStatus("D"));

		// 3.校验是否只调用了一次。如果上面注释的语句再调一次，且把录制的times改为2，那么在验证阶段将会报错。
		// new Verifications() {
		// {
		// dao.getStoreCount(anyString);
		// times = 1;
		// }
		// };

	}

}
