package com.pax.tms.webservice.pxmaster;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.pax.common.web.HttpUtils;

public class IpAddressInInterceptor extends AbstractPhaseInterceptor<Message> implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressInInterceptor.class);

	private String ipAddresses;

	private Set<String> allowedList = new HashSet<>();

	public IpAddressInInterceptor() {
		super(Phase.RECEIVE);
	}

	public void handleMessage(Message message) throws Fault {
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

		String ipAddress = HttpUtils.getRemoteAddr(request);
		String localAddress = request.getLocalAddr();
		if (!StringUtils.equals(ipAddress, localAddress) && !allowedList.contains(ipAddress)) {
			LOGGER.debug("Reject IP address {} to access PXDesigner API");
			throw new Fault(new IllegalAccessException("IP address " + ipAddress + " is not allowed"));
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (ipAddresses != null) {
			String[] ipArray = ipAddresses.split(",");
			for (String ip : ipArray) {
				String trimmedIp = StringUtils.trimToNull(ip);
				if (ip != null) {
					allowedList.add(trimmedIp);
				}
			}
		}

		allowedList.add("127.0.0.1");
		try {
			allowedList.add(InetAddress.getLocalHost().getHostAddress());
			allowedList.add(InetAddress.getLoopbackAddress().getHostAddress());
		} catch (Exception e) {
			// ignore
		}

		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null) {
						allowedList.add(ip.getHostAddress());
					}
				}
			}
		} catch (Exception e) {
			// ignore
		}

		LOGGER.info("allowed IP addresses {}", allowedList);
	}

	@Autowired
	public void setIpAddresses(@Value("${pxdesigner.server.ip:''}") String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}
}
