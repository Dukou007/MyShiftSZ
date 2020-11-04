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
package com.pax.tms.monitor.amazon.sns;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CheckIfPhoneNumberIsOptedOutRequest;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.ListPhoneNumbersOptedOutRequest;
import com.amazonaws.services.sns.model.ListPhoneNumbersOptedOutResult;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.OptInPhoneNumberRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.Subscription;
import com.amazonaws.services.sns.model.Topic;

@Component("amazonSmsSender")
public class AmazonSmsSender implements SmsSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSmsSender.class);

	@Autowired(required = false)
	@Qualifier("amazonSNSClient")
	private AmazonSNSClient snsClient;

	@Autowired(required = false)
	@Qualifier("amazonSendSmsTaskExecutor")
	private ThreadPoolTaskExecutor executorPool;

	@Override
	public String createTopic(String requestName, String displayName) {
		CreateTopicRequest topicRequest = new CreateTopicRequest(requestName);
		CreateTopicResult topicResult = snsClient.createTopic(topicRequest);
		String topicArn = topicResult.getTopicArn();
		snsClient.setTopicAttributes(topicArn, "DisplayName", displayName);
		return topicArn;
	}

	@Override
	public List<Topic> listTopic() {
		ListTopicsResult rs = snsClient.listTopics();
		return rs != null ? rs.getTopics() : null;
	}

	@Override
	public void deleteTopic(String topicArn) {
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
		snsClient.deleteTopic(deleteTopicRequest);
	}

	@Override
	public String subscribeTopic(String topicArn, String protocol, String endpoint) {
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, protocol, endpoint);
		SubscribeResult subResult = snsClient.subscribe(subRequest);
		return subResult.getSubscriptionArn();
	}

	@Override
	public void unsubscribeTopic(String subscribeArn) {
		snsClient.unsubscribe(subscribeArn);
	}

	@Override
	public List<Subscription> listSubscribes(String topicArn) {
		ListSubscriptionsByTopicResult subscriptionsResult = snsClient.listSubscriptionsByTopic(topicArn);
		return subscriptionsResult.getSubscriptions();
	}

	@Override
	public String publishMsg(String topicArn, String message) {
		PublishRequest publishRequest = new PublishRequest(topicArn, message);
		PublishResult publishResult = snsClient.publish(publishRequest);
		return publishResult.getMessageId();
	}

	@Override
	public void sendMsg(String phoneNumber, String message) {
		executorPool.execute(new SendSmsTask(message, phoneNumber));
	}

	@Override
	public void listOptOut(List<String> list) {
		String nextToken = null;
		do {
			ListPhoneNumbersOptedOutResult result = snsClient
					.listPhoneNumbersOptedOut(new ListPhoneNumbersOptedOutRequest().withNextToken(nextToken));
			nextToken = result.getNextToken();
			for (String phoneNum : result.getPhoneNumbers()) {
				list.add(phoneNum);
			}
		} while (nextToken != null);
	}

	@Override
	public boolean checkIfPhoneNumberIsOptedOut(String phoneNumber) {
		CheckIfPhoneNumberIsOptedOutRequest request = new CheckIfPhoneNumberIsOptedOutRequest()
				.withPhoneNumber(phoneNumber);
		return snsClient.checkIfPhoneNumberIsOptedOut(request).isOptedOut();
	}

	@Override
	public void optInPhoneNumber(String phoneNumber) {
		OptInPhoneNumberRequest request = new OptInPhoneNumberRequest().withPhoneNumber(phoneNumber);
		snsClient.optInPhoneNumber(request);
	}

	public class SendSmsTask implements Runnable {

		private String message;

		private String phoneNumber;

		public SendSmsTask(String message, String phoneNumber) {
			this.message = message;
			this.phoneNumber = phoneNumber;
		}

		@Override
		public void run() {
			if (StringUtils.isEmpty(phoneNumber)) {
				LOGGER.warn("Send SMS message failed: the phone number is empty");
				return;
			}
			try {
				doAction();
			} catch (Exception e) {
				LOGGER.error("Send SMS message failed", e);
			}
		}

		private void doAction() {
			PublishResult publishResult = snsClient
					.publish(new PublishRequest().withMessage(message).withPhoneNumber(phoneNumber));
			LOGGER.debug("Send SMS message completed, MessageId: {}", publishResult.getMessageId());
		}
	}

	public AmazonSNSClient getSnsClient() {
		return snsClient;
	}

	public void setSnsClient(AmazonSNSClient snsClient) {
		this.snsClient = snsClient;
	}

	public ThreadPoolTaskExecutor getExecutorPool() {
		return executorPool;
	}

	public void setExecutorPool(ThreadPoolTaskExecutor executorPool) {
		this.executorPool = executorPool;
	}

}
