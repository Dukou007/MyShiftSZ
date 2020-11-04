/**
 * ============================================================================       
 * = COPYRIGHT		          
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or nondisclosure 	
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or 
 *   disclosed except in accordance with the terms in that agreement.       
 *       Copyright (C) 2019-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.    
 * Description:       
 * Revision History:      
 * Date                         Author                    Action
 * 2019年12月20日 下午3:07:33           liming                   CSVWritter
 * ============================================================================
 */
package com.pax.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CSVWritter {
    
    private static final Logger logger = LoggerFactory.getLogger(CSVWritter.class);
    
    /**
     * CSV文件列分隔符
     */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /**
     * CSV文件行分隔符
     */
    private static final String CSV_ROW_SEPARATOR = "\r\n";

    /**
     * @param dataList 集合数据
     * @param titles   表头部数据
     * @param keys     表内容的键值
     * @param os       输出流
     */
    public static void doExport(List<List<Object>> dataList, OutputStream os) throws Exception {
        StringBuffer buf = new StringBuffer();
        // 组装数据
        if (null != dataList && !dataList.isEmpty()) { 
            for(List<Object> row : dataList){
                int i = 1;
                int size = row.size();
                for (Object data : row) {
                    String dataStr;
                    if(null != data){
                        dataStr = data.toString().replaceAll("\"", "\"\"");
                    }else{
                        dataStr = "";
                    }
                    if(DateTimeUtils.isDateStr(dataStr)){
                        buf.append("\"\t").append(dataStr).append("\t\"");
                    }else{
                        buf.append("\"").append(dataStr).append("\""); 
                    }
                    if (i < size) {
                        buf.append(CSV_COLUMN_SEPARATOR);
                    }
                }
                buf.append(CSV_ROW_SEPARATOR);
            }
        }

        // 写出响应
        os.write(buf.toString().getBytes("GBK"));
        os.flush();
    }

    /**
     * 设置Header
     *
     * @param fileName
     * @param response
     * @throws UnsupportedEncodingException
     */
    public static void responseSetProperties(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        // 读取字符编码
        String utf = "UTF-8";
        // 设置响应
        response.setContentType("application/ms-txt.numberformat:@");
        response.setCharacterEncoding(utf);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    }
    /**
     * CSV文件生成方法
     * @param head 文件头
     * @param dataList 数据列表
     * @param outPutPath 文件输出路径
     * @param filename 文件名
     * @return
     */
    public static File createCSVFile(String filePath, List<List<Object>> dataList) {
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(filePath);
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();
            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
 
            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            logger.error("createCSVFile Exception",e);
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                logger.error("close csvWtriter Exception",e);
            }
        }
        return csvFile;
    }
 
    /**
     * 写一行数据方法
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        int i = 1;
        int size = row.size();
        // 写入文件头部
        for (Object data : row) {
            StringBuffer sb = new StringBuffer();
            String dataStr;
            if(null != data){
                dataStr = data.toString().replaceAll("\"", "\"\"");
            }else{
                dataStr = "";
            }
            if(DateTimeUtils.isDateStr(dataStr)){
                sb.append("\"\t").append(dataStr).append("\t\"");
            }else{
                sb.append("\"").append(dataStr).append("\""); 
            }
            if (i < size) {
                sb.append(CSV_COLUMN_SEPARATOR);
            }
            String rowStr = sb.toString();
            csvWriter.write(rowStr);
            i++;
        }
        csvWriter.newLine();
    }

}
