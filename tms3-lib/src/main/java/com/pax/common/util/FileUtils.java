/**
 * ============================================================================       
 * = COPYRIGHT		          
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or nondisclosure 	
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or 
 *   disclosed except in accordance with the terms in that agreement.       
 *       Copyright (C) 2020-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.    
 * Description:       
 * Revision History:      
 * Date                         Author                    Action
 * 2020年2月26日 下午4:57:22           liming                   FileUtils
 * ============================================================================
 */
package com.pax.common.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtils {
    
    public static String getFileLastHex(File file) throws IOException{
        String lastHex = "";
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        byte[] lastByte = new byte[16];
        int begin = (int) (raf.length() - 16);
        raf.skipBytes(begin);
        raf.read(lastByte,0,16);
        lastHex = Hex.string(lastByte);
        raf.close();//关闭文件
        return lastHex;
    }
    
}
