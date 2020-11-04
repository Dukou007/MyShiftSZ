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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pax.common.exception.BusinessException;
import com.pax.common.fs.FileManagerUtils;
import com.pax.common.util.FileDigest;
import com.pax.common.util.FileUtils;
import com.pax.common.util.ZipFileUtil;
import com.pax.tms.app.PackageUtils;
import com.pax.tms.location.service.AddressServiceImpl;

import io.vertx.core.json.Json;

public class PhoenixPackage {

	public static final Logger LOGGER = LoggerFactory.getLogger(PhoenixPackage.class);

	public static final String ENCODING_DEFAULT = "UTF-8";

	private static final String MANIFEST_FILENAME = "manifest.xml";

	private static final String MANIFEST_SIG_FILENAME = "manifest.xml.sig";

	private static final String NULL_VERSION = "";
	
	private static final String SIGNED_LAST_HEX = "5349474E45445F5645523A3030303031";
	
	private List<FileInfo> fileInfos;
	private ManifestFile manifestFile;

	private String packageMd5;
	private String packageSha256;
	private Long packageFileSize;
	private String packageFileName;
	private String packageFilePath;
	private boolean packageSigned;

	public PhoenixPackage() {
		super();
	}

	public PhoenixPackage(List<FileInfo> fileInfos, ManifestFile manifestFile, String packageMd5, String packageSha256,
			Long packageFileSize, String packageFileName) {
		super();
		this.fileInfos = fileInfos;
		this.manifestFile = manifestFile;
		this.packageMd5 = packageMd5;
		this.packageSha256 = packageSha256;
		this.packageFileSize = packageFileSize;
		this.packageFileName = packageFileName;
	}

	public static ManifestFile readManifest(String filePath, String fileName) {
		validatePackageFileName(fileName);
		if (filePath == null) {
			throw new PackageException("msg.phonenixPackage.emptyFileContent");
		}
		try {
			return readProgramInformation(filePath);
		} catch (IOException e) {
			throw new PackageException("msg.phonenixPackage.invalidPackageFormat", e);
		}
	}

	public void parse(File file, String fileName) throws IOException {
		validatePackageFileName(fileName);
		if (file == null) {
			throw new PackageException("msg.phonenixPackage.emptyFileContent");
		}

		// save file info and data
		fileInfos = new ArrayList<>();
		packageMd5 = FileDigest.md5Hex(file);
		packageSha256 = FileDigest.sha256Hex(file);
		packageFileName = fileName;
		packageFileSize = file.length();
		this.packageFilePath = file.getAbsolutePath();

		// get zip file content
		Map<String, String> zipFileEntries = null;
		Path tempFilePath = Files.createTempDirectory("phoneix-pack-");
		try {
			zipFileEntries = ZipFileUtil.unzip(file, tempFilePath.toFile().getPath());
			readManifestFile(zipFileEntries);
			this.packageSigned = checkSigned(file,zipFileEntries,tempFilePath.toFile().getPath());
			readManifestSigFile(zipFileEntries);
			readFiles(zipFileEntries);
			saveLocalPackageToFastDfs(packageFilePath);
		} catch (IOException e) {
			throw new PackageException("msg.phonenixPackage.invalidPackageFormat", e);
		} finally {
			// FileUtils.deleteDirectory(tempFilePath.toFile());
		}

	}
	
	public void checkFile(File file, String fileName) throws IOException {
        // save file info and data
        fileInfos = new ArrayList<>();
        packageMd5 = FileDigest.md5Hex(file);
        packageSha256 = FileDigest.sha256Hex(file);
        packageFileName = fileName;
        packageFileSize = file.length();
        this.packageFilePath = file.getAbsolutePath();

        // get zip file content
        Map<String, String> zipFileEntries = null;
        Path tempFilePath = Files.createTempDirectory("phoneix-pack-");
        try {
            zipFileEntries = ZipFileUtil.unzip(file, tempFilePath.toFile().getPath());
            readManifestFile(zipFileEntries);
            this.packageSigned = checkSigned(file,zipFileEntries,tempFilePath.toFile().getPath());
        } catch (IOException e) {
            throw new PackageException("msg.phonenixPackage.invalidPackageFormat", e);
        } finally {
            file.delete();
        }

    }

	private void saveLocalPackageToFastDfs(String packageFilePath) {
		this.packageFilePath = FileManagerUtils.saveLocalFileToFdfs(packageFilePath);

	}
	
	private static List<String> getPackageFormat() {
		String packageFormat = "";
		List<String> result = new LinkedList<String>();
		try {
			InputStream in = getResourceAsStream("package.json");
			packageFormat = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
		} catch (IOException e) {
			throw new BusinessException("msg.initTimeZone.fileNotFound", e);
		}
		if(null == packageFormat || "".equals(packageFormat.trim()))
		{
			return result;
		}
		@SuppressWarnings("unchecked")
		Map<String, List<String>> map = Json.decodeValue(packageFormat, Map.class);
		result = map.get("PackageFormat");
		return result;
	}
	
	private static InputStream getResourceAsStream(String resource) {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		if (stream == null) {
			stream = AddressServiceImpl.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			stream = AddressServiceImpl.class.getClassLoader().getResourceAsStream(stripped);
		}
		return stream;
	}

	private void readFiles(Map<String, String> zipFileEntries) throws IOException {
		List<FileElement> fileElements = manifestFile.getFileElements();
		if (zipFileEntries.size() != fileElements.size()) {
			throw new PackageException("msg.phonenixPackage.wrongFileNumber");
		}

		for (FileElement fileElement : fileElements) {
			String filePath = zipFileEntries.get(fileElement.getFileName());
			if (filePath == null) {
				throw new PackageException("msg.phonenixPackage.invalidFormat");
			}

			String md5 = FileDigest.md5Hex(filePath);
			String sha256 = FileDigest.sha256Hex(filePath);

			if (fileElement.getMd5() != null && !StringUtils.equalsIgnoreCase(md5, fileElement.getMd5())) {
				throw new PackageException("msg.phonenixPackage.manifest.file.invalidMd5");
			}

			if (fileElement.getSha256() != null && !StringUtils.equalsIgnoreCase(sha256, fileElement.getSha256())) {
				throw new PackageException("msg.phonenixPackage.manifest.file.invalidSha256");
			}

			FileInfo fileInfo = new FileInfo(fileElement.getFileName(), fileElement.getVersion(), filePath,
					new File(filePath).length(), md5, sha256, false);

			try {

				ManifestFile mf = readProgramInformation(filePath);
				if (mf != null) {
					fileInfo.setPgmType(mf.getPackageType());
					fileInfo.setPgmName(mf.getPackageName());
					fileInfo.setPgmVersion(mf.getPackageVersion());
					fileInfo.setPgmDesc(mf.getPackageDescription());
				}

			} catch (Exception e) {
				LOGGER.debug(e.getMessage(), e);
			}
			fileInfos.add(fileInfo);
		}
	}

	private static ManifestFile readProgramInformation(String filePath) throws IOException {
		ZipEntry zipEntry;
		ZipInputStream zipInput = null;
		Charset charset = Charset.forName(ENCODING_DEFAULT);
		try (BufferedInputStream bufferedInput = IOUtils.buffer(new FileInputStream(filePath))) {
			zipInput = new ZipInputStream(bufferedInput, charset);
			while ((zipEntry = zipInput.getNextEntry()) != null) {
				String zipEntryName = zipEntry.getName();
				if (!zipEntry.isDirectory() && MANIFEST_FILENAME.equals(zipEntryName)) {
					return ManifestFile.parse(zipInput);
				}
			}
		} finally {
			if (zipInput != null) {
				zipInput.close();
			}
		}
		return null;
	}
	/**
	 * @Description: 判断包是否签名
	 * @param file
	 * @param zipFileEntries
	 * @param pgmType
	 * @return
	 * @throws IOException
	 * @return: boolean
	 */
	private boolean checkSigned(File file,Map<String, String> zipFileEntries,String tempFilePath) throws IOException  {
       //1、如果整包文件的16进制值得尾部16位也等于 5349474E45445F5645523A3030303031，则认为是已签名；
       String packegeLastHex = FileUtils.getFileLastHex(file);
       if(StringUtils.equalsIgnoreCase(packegeLastHex, SIGNED_LAST_HEX)){
//           LOGGER.info("fileName="+file.getName() +";check ture at 1");
           return true;
       }
       String pgmType = manifestFile.getPackageType() ;
       boolean hasSig = hasSig(zipFileEntries);
       boolean hasUnsig = hasUnsig(zipFileEntries);
       //2、只要有.unsig文件，即表示未签名
       if(hasUnsig){
//           LOGGER.info("fileName="+file.getName() +";check false at 2");
           return false;
       }
       //3、判断包中是否存在manifest.xml.sig。如存在，且这个文件的尾部16字符的hex值是等于 5349474E45445F5645523A3030303031，则认为已签名。
       String manifestSigfilePath = zipFileEntries.get(MANIFEST_SIG_FILENAME);
       if (manifestSigfilePath != null) {
          String manifestSigLastHex =  FileUtils.getFileLastHex(new File(manifestSigfilePath));
          if(StringUtils.equalsIgnoreCase(manifestSigLastHex, SIGNED_LAST_HEX)){
//              LOGGER.info("fileName="+file.getName() +";check ture at 3");
              return true;
          }
       }
       //4、类型为avpuk和firmware，则判断除manifest文件以外的其他文件的尾部16字符的hex都等于 5349474E45445F5645523A3030303031，则已签名；
       if(StringUtils.equalsIgnoreCase(pgmType, "avpuk") || StringUtils.equalsIgnoreCase(pgmType, "firmware")){
            for (Entry<String, String> fileInfo : zipFileEntries.entrySet()) {
                String fileName = fileInfo.getKey();
                String filePath = fileInfo.getValue();
                if (!StringUtils.equalsIgnoreCase(fileName, MANIFEST_FILENAME) && StringUtils.isNoneBlank(filePath)) {
                    String fileSigLastHex = FileUtils.getFileLastHex(new File(filePath));
                    if (!StringUtils.equalsIgnoreCase(fileSigLastHex, SIGNED_LAST_HEX)) {
//                        LOGGER.info("fileName="+file.getName()+";check file="+fileInfo.getKey() +";check false at 4");
                        return false;
                    }
                }
            }
            return true;
       }
       // 5、如果包中（包括子目录）不存在unsig文件但存在sig文件，
        if (hasSig) {
            //类型为application，如果bin文件和所有.sig文件的尾部16字符的hex等于 5349474E45445F5645523A3030303031 则已签名。
            if (StringUtils.equalsIgnoreCase(pgmType, "application")) {
                String binFilePath = getBinFilePath();
                for (Entry<String, String> fileInfo : zipFileEntries.entrySet()) {
                    String fileName = fileInfo.getKey();
                    String filePath = fileInfo.getValue();
                    if(StringUtils.isBlank(filePath)){
                        continue;
                    }
                    String relativePath = filePath.replace(tempFilePath, "./").replace("\\", "/").replace(".//", "./").replace("./", "");
                    if (StringUtils.equals(binFilePath, relativePath) || StringUtils.endsWith(fileName, ".sig")) {
                        String fileSigLastHex = FileUtils.getFileLastHex(new File(filePath));
                        if (!StringUtils.equalsIgnoreCase(fileSigLastHex, SIGNED_LAST_HEX)) {
//                            LOGGER.info("fileName="+file.getName()+";check file="+fileInfo.getKey() +";check false at 5.1");
                            return false;
                        }
                    }
                }
                return true;
            } else {
                // 如果其他类型，则所有的.sig的尾部16字符的hex等于 5349474E45445F5645523A3030303031 则已签名。
                for (Entry<String, String> fileInfo : zipFileEntries.entrySet()) {
                    String fileName = fileInfo.getKey();
                    String filePath = fileInfo.getValue();
                    if (StringUtils.endsWith(fileName, ".sig") && StringUtils.isNoneBlank(filePath)) {
                        String fileSigLastHex = FileUtils.getFileLastHex(new File(filePath));
                        if (!StringUtils.equalsIgnoreCase(fileSigLastHex, SIGNED_LAST_HEX)) {
//                            LOGGER.info("fileName="+file.getName()+";check file="+fileInfo.getKey() +";check false at 5.2");
                            return false;
                        }
                    }
                }
                return true;
            }
        }
       //6.如果类型为application，不存在.sig和.unsig文件,且bin文件尾部16字符的hex等于 5349474E45445F5645523A3030303031 则已签名
       if(StringUtils.equalsIgnoreCase(pgmType, "application") && !hasSig){
           String binFilePath = getBinFilePath();
           for (Entry<String, String> fileInfo : zipFileEntries.entrySet()) {
               String filePath = fileInfo.getValue();
               String relativePath = filePath.replace(tempFilePath, "./").replace("\\", "/").replace(".//", "./").replace("./", "");
               if (StringUtils.equals(binFilePath, relativePath)  && StringUtils.isNoneBlank(filePath)) {
                   String fileSigLastHex = FileUtils.getFileLastHex(new File(filePath));
                   if (!StringUtils.equalsIgnoreCase(fileSigLastHex, SIGNED_LAST_HEX)) {
//                       LOGGER.info("fileName="+file.getName()+";check file="+fileInfo.getKey() +";check false at 6");
                       return false;
                   }
               }
           }
           return true;
       }
       //7.如果为combo类型，如果combo中所有package均为签名的package，那么该combo为signed；如果combo中存在未签名的package，那么该combo为Unsigned
       if(StringUtils.equalsIgnoreCase(pgmType, "combo")){
           for (Entry<String, String> fileInfo : zipFileEntries.entrySet()) {
               String filePath = fileInfo.getValue();
               String fileName = fileInfo.getKey();
               if (StringUtils.endsWith(filePath, ".zip") || StringUtils.endsWith(filePath, ".aip") || StringUtils.endsWith(filePath, ".pkg")) {
                   File subFile = new File(filePath);
                   PhoenixPackage phoenixPackage = new PhoenixPackage();
                   try {
                       //读取文件包信息，保存文件到FastDfs
                       phoenixPackage.parse(subFile, fileName);
                   } catch (IOException e) {
//                       LOGGER.info("msg.pkg.sysErro");
                       throw new BusinessException("msg.pkg.sysErro");
                   }
                   if(!phoenixPackage.isPackageSigned()){
//                       LOGGER.info("fileName="+file.getName()+";check file="+fileInfo.getKey() +";check false at 7");
                       return false;
                   }
               }
           }
           return true;
       }
       return false;
    }
	
	/**
	 * @Description: 判断包中是否有.sig文件
	 * @param zipFileEntries
	 * @return
	 * @return: boolean
	 */
    private boolean hasSig(Map<String, String> zipFileEntries) {
        for (Entry<String, String> fileInfo : zipFileEntries.entrySet()) {
            String fileName = fileInfo.getKey();
            if (StringUtils.endsWith(fileName, ".sig")) {
                return true;
            }
        }
        return false;
    }
    /**
     * @Description: 获取bin文件目录
     * @return
     * @return: String
     */
    private String getBinFilePath(){
        List<ChildElement> childElements = manifestFile.getChildElements();
        if(null != childElements){
            for (ChildElement childElement : childElements) {
                if(childElement instanceof ApplicationElement){
                    ApplicationElement applicationElement = (ApplicationElement) childElement;
                    return applicationElement.getBin().replace("./", "");
                }
            }
        }
        return null;
    }
    /**
     * @Description: 判断包中是否有.unsig文件
     * @param zipFileEntries
     * @return
     * @return: boolean
     */
    private boolean hasUnsig(Map<String, String> zipFileEntries) {
        for (Entry<String, String> fileInfo : zipFileEntries.entrySet()) {
            String fileName = fileInfo.getKey();
            if (StringUtils.endsWith(fileName, ".unsig")) {
                return true;
            }
        }
        return false;
    }
	
	private void readManifestSigFile(Map<String, String> zipFileEntries) throws IOException {
		String manifestSigfilePath = zipFileEntries.get(MANIFEST_SIG_FILENAME);
		if (manifestSigfilePath != null) {
			zipFileEntries.remove(MANIFEST_SIG_FILENAME);
			fileInfos.add(new FileInfo(MANIFEST_SIG_FILENAME, NULL_VERSION, manifestSigfilePath,
					new File(manifestSigfilePath).length(), FileDigest.md5Hex(manifestSigfilePath),
					FileDigest.sha256Hex(manifestSigfilePath), false));
		}
	}

	private void readManifestFile(Map<String, String> zipFileEntries) throws IOException {
		String manifestFilePath = zipFileEntries.get(MANIFEST_FILENAME);
		if (manifestFilePath == null) {
			throw new PackageException("msg.phonenixPackage.noManifestFile");
		}

		manifestFile = ManifestFile.parse(manifestFilePath);
		zipFileEntries.remove(MANIFEST_FILENAME);
		fileInfos.add(
				new FileInfo(MANIFEST_FILENAME, NULL_VERSION, manifestFilePath, new File(manifestFilePath).length(),
						FileDigest.md5Hex(manifestFilePath), FileDigest.sha256Hex(manifestFilePath), true));
	}

	private static void validatePackageFileName(String fileNameInput) {
		List<String> packageFormat = getPackageFormat();
		if (null == packageFormat || packageFormat.isEmpty()) {
			packageFormat.add("zip");
			packageFormat.add("aip");
			packageFormat.add("pkg");
		}
		String invalidtErrorMsg = getinvalidtErrorMsg(packageFormat);
		if (StringUtils.isEmpty(fileNameInput)) {
			throw new PackageException("msg.phonenixPackage.emptyFileName");
		}

		String fileExtension = PackageUtils.getFileExtension(fileNameInput);
		if (fileExtension != null) {
			fileExtension = fileExtension.toLowerCase();
		}
		if (!packageFormat.contains(fileExtension)) {
			throw new PackageException("msg.phonenixPackage.invalidPackageFormat",
					new String[] { invalidtErrorMsg });
		}
	}
	
	private static String getinvalidtErrorMsg(List<String> packageFormat) {
		String result = "";
		if (null == packageFormat || packageFormat.isEmpty() || packageFormat.size() == 0) {
			return "";
		}
		if (packageFormat.size() == 1)
		{
			result = " " + packageFormat.get(0) + " ";
		}
		else {
			result = " " + packageFormat.get(0);
			StringBuffer sb = new StringBuffer(result);
			for (int i = 0; i < packageFormat.size(); i++) {
				if (i == 0) {
					continue;
				}
				if (i == packageFormat.size()-1) {
					sb.append(" or ");
					sb.append(packageFormat.get(i));
					sb.append(" ");
					break;
				}
				sb.append(" or ");
				sb.append(packageFormat.get(i));
			}
			result = sb.toString();
		}
		return result;
	}

	public List<FileInfo> getFileInfos() {
		return fileInfos;
	}

	public void setFileInfos(List<FileInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}

	public ManifestFile getManifestFile() {
		return manifestFile;
	}

	public void setManifestFile(ManifestFile manifestFile) {
		this.manifestFile = manifestFile;
	}

	public String getPackageMd5() {
		return packageMd5;
	}

	public void setPackageMd5(String packageMd5) {
		this.packageMd5 = packageMd5;
	}

	public String getPackageSha256() {
		return packageSha256;
	}

	public void setPackageSha256(String packageSha256) {
		this.packageSha256 = packageSha256;
	}

	public Long getPackageFileSize() {
		return packageFileSize;
	}

	public void setPackageFileSize(Long packageFileSize) {
		this.packageFileSize = packageFileSize;
	}

	public String getPackageFileName() {
		return packageFileName;
	}

	public void setPackageFileName(String packageFileName) {
		this.packageFileName = packageFileName;
	}

	public String getPackageFilePath() {
		return packageFilePath;
	}

	public void setPackageFilePath(String packageFilePath) {
		this.packageFilePath = packageFilePath;
	}

    public boolean isPackageSigned() {
        return packageSigned;
    }

    public void setPackageSigned(boolean packageSigned) {
        this.packageSigned = packageSigned;
    }

}
