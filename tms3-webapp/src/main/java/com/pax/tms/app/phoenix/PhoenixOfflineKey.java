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
package com.pax.tms.app.phoenix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pax.common.exception.BusinessException;
import com.pax.common.fs.FileManagerUtils;
import com.pax.common.util.FileDigest;
import com.pax.common.util.ZipFileUtil;

public class PhoenixOfflineKey {
    public static final Logger LOGGER = LoggerFactory.getLogger(PhoenixOfflineKey.class);
    public static final String ENCODING_DEFAULT = "UTF-8";
    private static final String MANIFEST_FILENAME = "manifest.xml";
    private ManifestFile manifestFile;
    private String offlineKeyMd5;
    private String offlineKeySha256;
    private Long offlineKeyFileSize;
    private String offlineKeyFileName;
    private String offlineKeyFilePath;
    private boolean offlineKeySigned;

    public PhoenixOfflineKey() {
        super();
    }
    public PhoenixOfflineKey(ManifestFile manifestFile, String packageMd5, String packageSha256, Long packageFileSize,
            String packageFileName) {
        super();
        this.manifestFile = manifestFile;
        this.offlineKeyMd5 = packageMd5;
        this.offlineKeySha256 = packageSha256;
        this.offlineKeyFileSize = packageFileSize;
        this.offlineKeyFileName = packageFileName;
    }
    public void parse(File file, String fileName) throws IOException {
        if (file == null) {
            throw new PackageException("msg.offlineKey.emptyFileContent");
        }
        // save file info and data
        ManifestFile manifestFile = new ManifestFile();
        manifestFile.setPackageType("offlinekey");
        manifestFile.setPackageDescription("PAX Offline Key");
        manifestFile.setPackageName(fileName.substring(0, fileName.lastIndexOf("_")));
        manifestFile.setPackageVersion(fileName.substring(fileName.lastIndexOf("_") + 1));
        this.manifestFile = manifestFile;
        File manifestXmlFile = createManifest(file, fileName, file.getParentFile().getAbsolutePath(), manifestFile);
        String offlineKeyZipPath = file.getParentFile().getAbsolutePath() + File.separator + fileName + ".zip";
        ZipFileUtil.zip(offlineKeyZipPath, file,manifestXmlFile);
        File offlineKeyZip = new File(offlineKeyZipPath);
        offlineKeyMd5 = FileDigest.md5Hex(offlineKeyZip);
        offlineKeySha256 = FileDigest.sha256Hex(offlineKeyZip);
        offlineKeyFileName = offlineKeyZip.getName();
        offlineKeyFileSize = offlineKeyZip.length();
        this.offlineKeyFilePath = FileManagerUtils.saveLocalFileToFdfs(offlineKeyZipPath);
    }
    
    private File createManifest(File keyFile, String keyFileName, String filePath, ManifestFile manifestFile) {
        File manifestXmlFile = new File(filePath + File.separator + MANIFEST_FILENAME);
        if (filePath == null) {
            throw new PackageException("msg.phonenixPackage.emptyFileContent");
        }
        try {
            Document document = DocumentHelper.createDocument();
            Element packageElement = document.addElement("package");
            packageElement.addAttribute("type", manifestFile.getPackageType());
            packageElement.addAttribute("name", manifestFile.getPackageName());
            packageElement.addAttribute("version", manifestFile.getPackageVersion());
            packageElement.addAttribute("description", manifestFile.getPackageDescription());
            packageElement.addElement("offlinekey");
            Element filesElement = packageElement.addElement("files");
            Element fileElement = filesElement.addElement("file");
            fileElement.addAttribute("path", keyFileName);
            fileElement.addAttribute("sha256",  FileDigest.sha256Hex(keyFile));
            // 5、设置生成xml的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            // 6、生成xml文件
            XMLWriter writer = new XMLWriter(new FileOutputStream(manifestXmlFile), format);
            // 设置是否转义，默认使用转义字符
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            LOGGER.error("create offline manifast file exception:{}", e.getMessage());
            throw new BusinessException("msg.sysErro", new String[] {""});
        }
        return manifestXmlFile;
    }
    
    public ManifestFile getManifestFile() {
        return manifestFile;
    }
    public void setManifestFile(ManifestFile manifestFile) {
        this.manifestFile = manifestFile;
    }
    public String getOfflineKeyMd5() {
        return offlineKeyMd5;
    }
    public void setOfflineKeyMd5(String offlineKeyMd5) {
        this.offlineKeyMd5 = offlineKeyMd5;
    }
    public String getOfflineKeySha256() {
        return offlineKeySha256;
    }
    public void setOfflineKeySha256(String offlineKeySha256) {
        this.offlineKeySha256 = offlineKeySha256;
    }
    public Long getOfflineKeyFileSize() {
        return offlineKeyFileSize;
    }
    public void setOfflineKeyFileSize(Long offlineKeyFileSize) {
        this.offlineKeyFileSize = offlineKeyFileSize;
    }
    public String getOfflineKeyFileName() {
        return offlineKeyFileName;
    }
    public void setOfflineKeyFileName(String offlineKeyFileName) {
        this.offlineKeyFileName = offlineKeyFileName;
    }
    public String getOfflineKeyFilePath() {
        return offlineKeyFilePath;
    }
    public void setOfflineKeyFilePath(String offlineKeyFilePath) {
        this.offlineKeyFilePath = offlineKeyFilePath;
    }
    public boolean isOfflineKeySigned() {
        return offlineKeySigned;
    }
    public void setOfflineKeySigned(boolean offlineKeySigned) {
        this.offlineKeySigned = offlineKeySigned;
    }
}
