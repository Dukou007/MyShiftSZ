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
package com.pax.fastdfs.proto.tracker.internal;

import org.apache.commons.lang3.Validate;

import com.pax.fastdfs.proto.CmdConstants;
import com.pax.fastdfs.proto.FdfsRequest;
import com.pax.fastdfs.proto.OtherConstants;
import com.pax.fastdfs.proto.ProtoHead;
import com.pax.fastdfs.proto.mapper.FdfsColumn;

/**
 * 按分组获取存储节点
 * 
 * @author tobato
 *
 */
public class TrackerGetStoreStorageWithGroupRequest extends FdfsRequest {

    private static final byte WITH_GROUP_CMD = CmdConstants.TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITH_GROUP_ONE;

    /**
     * 分组定义
     */
    @FdfsColumn(index = 0, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private final String groupName;

    /**
     * 获取存储节点
     * 
     * @param groupName
     */
    public TrackerGetStoreStorageWithGroupRequest(String groupName) {
        Validate.notBlank(groupName, "分组不能为空");
        this.groupName = groupName;
        this.head = new ProtoHead(WITH_GROUP_CMD);
    }

    public String getGroupName() {
        return groupName;
    }

}
