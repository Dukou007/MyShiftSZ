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
package com.pax.fastdfs.proto.storage.enums;

/**
 * 元数据设置方式
 * 
 * @author tobato
 *
 */
public enum StorageMetdataSetType {

    /** 覆盖 */
    STORAGE_SET_METADATA_FLAG_OVERWRITE((byte) 'O'),
    /** 没有的条目增加，有则条目覆盖 */
    STORAGE_SET_METADATA_FLAG_MERGE((byte) 'M');

    private byte type;

    private StorageMetdataSetType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

}
