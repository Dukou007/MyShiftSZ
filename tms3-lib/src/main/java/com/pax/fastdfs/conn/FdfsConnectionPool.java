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
package com.pax.fastdfs.conn;

import java.net.InetSocketAddress;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定义Fdfs连接池对象
 * 
 * <pre>
 * 定义了对象池要实现的功能,对一个地址进行池化Map Pool
 * </pre>
 * 
 *
 */
@Component
public class FdfsConnectionPool extends GenericKeyedObjectPool<InetSocketAddress, Connection> {

	/**
	 * 默认构造函数
	 * 
	 * @param factory
	 * @param config
	 */
	@Autowired
	public FdfsConnectionPool(KeyedPooledObjectFactory<InetSocketAddress, Connection> factory,
			GenericKeyedObjectPoolConfig config) {
		super(factory, config);
	}

	/**
	 * 默认构造函数
	 * 
	 * @param factory
	 */
	public FdfsConnectionPool(KeyedPooledObjectFactory<InetSocketAddress, Connection> factory) {
		super(factory);
	}

}
