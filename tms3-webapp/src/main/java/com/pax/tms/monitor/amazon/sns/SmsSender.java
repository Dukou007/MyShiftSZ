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

import com.amazonaws.services.sns.model.Subscription;
import com.amazonaws.services.sns.model.Topic;

public interface SmsSender {

	String createTopic(String requestName, String displayName);

	void optInPhoneNumber(String phoneNumber);

	boolean checkIfPhoneNumberIsOptedOut(String phoneNumber);

	void listOptOut(List<String> list);

	void sendMsg(String phoneNumber, String message);

	String publishMsg(String topicArn, String message);

	List<Subscription> listSubscribes(String topicArn);

	void unsubscribeTopic(String subscribeArn);

	String subscribeTopic(String topicArn, String protocol, String endpoint);

	void deleteTopic(String topicArn);

	List<Topic> listTopic();

}
