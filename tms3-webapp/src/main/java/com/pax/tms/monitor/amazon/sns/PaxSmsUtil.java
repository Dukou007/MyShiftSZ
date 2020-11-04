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

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.util.ResourceUtils;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
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

public class PaxSmsUtil {

	private static AmazonSNSClient snsClient;

	private static boolean isEnableSms = false;

	private PaxSmsUtil() {
	}

	public static void setCredentials(String profilesConfigFilePath, String profileName, boolean enableService)
			throws FileNotFoundException {
		isEnableSms = enableService;
		if (isEnableSms) {
			snsClient = new AmazonSNSClient(new ProfileCredentialsProvider(
					ResourceUtils.getFile(profilesConfigFilePath).getAbsolutePath(), profileName));
		}
	}

	public static String createTopic(String requestName, String displayName) {
		CreateTopicRequest topicRequest = new CreateTopicRequest(requestName);
		CreateTopicResult topicResult = snsClient.createTopic(topicRequest);
		String topicArn = topicResult.getTopicArn();
		snsClient.setTopicAttributes(topicArn, "DisplayName", displayName);
		return topicArn;
	}

	public static List<Topic> listTopic() {
		ListTopicsResult rs = snsClient.listTopics();
		return rs != null ? rs.getTopics() : null;
	}

	public static void deleteTopic(String topicArn) {
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
		snsClient.deleteTopic(deleteTopicRequest);
	}

	public static String subscribeTopic(String topicArn, String protocol, String endpoint) {
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, protocol, endpoint);
		SubscribeResult subResult = snsClient.subscribe(subRequest);
		return subResult.getSubscriptionArn();
	}

	public static void unSubScribeTopic(String subscribeArn) {
		snsClient.unsubscribe(subscribeArn);
	}

	public static List<Subscription> listSubscribes(String topicArn) {
		ListSubscriptionsByTopicResult subscriptionsResult = snsClient.listSubscriptionsByTopic(topicArn);
		return subscriptionsResult.getSubscriptions();
	}

	public static String publishMsg(String topicArn, String message) {
		PublishRequest publishRequest = new PublishRequest(topicArn, message);
		PublishResult publishResult = snsClient.publish(publishRequest);
		return publishResult.getMessageId();
	}

	public static String sendMsg(String phoneNumber, String message) {
		if (isEnableSms) {
			PublishResult publishResult = snsClient
					.publish(new PublishRequest().withMessage(message).withPhoneNumber(phoneNumber));
			return publishResult.getMessageId();
		} else {
			return null;
		}
	}

	public static void listOptOut(List<String> list) {
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

	public static boolean checkIfPhoneNumberIsOptedOut(String phoneNumber) {
		CheckIfPhoneNumberIsOptedOutRequest request = new CheckIfPhoneNumberIsOptedOutRequest()
				.withPhoneNumber(phoneNumber);
		return snsClient.checkIfPhoneNumberIsOptedOut(request).isOptedOut();
	}

	public static void optInPhoneNumber(String phoneNumber) {
		OptInPhoneNumberRequest request = new OptInPhoneNumberRequest().withPhoneNumber(phoneNumber);
		snsClient.optInPhoneNumber(request);
	}

}
