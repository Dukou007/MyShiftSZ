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
package com.pax.fastdfs.proto.mapper;

/**
 * 动态属性类型
 * 
 * <pre>
 * 可以为空的属性-不发送该报文
 * 剩余的所有byte-将该字段全部写入到最后的byte当中
 * </pre>
 * 
 * @author tobato
 *
 */
public enum DynamicFieldType {
    /** 非动态属性 */
    NULL,
    /** 剩余的所有Byte */
    ALLRESTBYTE,
    /** 可空的属性 */
    NULLABLE,
    /** 文件元数据Set */
	METADATE

}
