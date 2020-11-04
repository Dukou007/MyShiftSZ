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
package com.pax.tms.app.broadpos;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.pax.common.fs.FileManagerUtils;
import com.pax.common.util.ZipFileUtil;
import com.pax.tms.app.PackageUtils;
import com.pax.tms.app.phoenix.PackageException;

public class BroadposPackage implements Closeable {

	public static final String PCT_ZIP_FILE = "PCT_FILE_PATH";
	public static final String PGS_ZIP_FILE = "PGS_ZIP_FILE";
	public static final String SIGN_VER = "SIGN_VER";
	public static final String PGO_ZIP_FILE = "PGO_ZIP_FILE";
	public static final String PGD_ZIP_FILE = "PGD_ZIP_FILE";
	public static final String PGT_ZIP_FILE = "PGT_ZIP_FILE";

	private String packageFilePath;
	private String packagefileName;
	private long packageFileSize;
	private String modelName;
	private Signer signer;

	private Map<String, String> fileMap;

	private ConfigFile configFile;

	private String tempDirPath;

	private String charset = "ISO8859-1";

	public BroadposPackage(String packageFilePath, String packagefileName, long packageFileSize, String modelName,
			Signer signer) {
		this.packageFilePath = packageFilePath;
		this.packagefileName = packagefileName;
		this.packageFileSize = packageFileSize;
		this.modelName = modelName;
		this.signer = signer;
	}

	public void parse() throws IOException {
		if (packageFilePath == null) {
			throw new PackageException("msg.broadposPackage.emptyFileContent");
		}

		tempDirPath = Files.createTempDirectory(FileManagerUtils.getLocalTempFilePrefix() + "broadpos-pack-")
				.toString();
		unzip();

		parseConfigFile();

		addRootCertToPackage(fileMap);
		addScriptCertToPackage(fileMap);

		validateModelName();

		processFirmwarePrograms();

		processModulePrograms();

		saveLocalPackageToFastDfs(packageFilePath);

	}

	private void saveLocalPackageToFastDfs(String packageFilePath) {
		this.packageFilePath = FileManagerUtils.saveLocalFileToFdfs(packageFilePath);

	}

	private Map<String, String> unzip() {
		fileMap = unzipPackageFile();

		String configZipPath = fileMap.get("config.zip");
		if (configZipPath == null) {
			throw new PackageException("msg.broadposPackage.noConfigPackage");
		}

		Map<String, String> configFiles = unzipConfigFile(configZipPath);
		fileMap.putAll(configFiles);

		String paramZipPath = fileMap.get("param.zip");
		if (paramZipPath == null) {
			throw new PackageException("msg.broadposPackage.noParamPackage");
		}

		Map<String, String> paramFiles = unzipParamFile(paramZipPath);
		fileMap.putAll(paramFiles);

		return fileMap;
	}

	private Map<String, String> unzipPackageFile() {
		try {
			return ZipFileUtil.unzip(packageFilePath, charset, tempDirPath);
		} catch (IOException e) {
			throw new PackageException("msg.broadposPackage.invalidPackageFormat", e);
		}
	}

	private Map<String, String> unzipConfigFile(String configZipFilePath) {
		try {
			return ZipFileUtil.unzip(configZipFilePath, charset, tempDirPath);
		} catch (IOException e) {
			throw new PackageException("msg.broadposPackage.invalidConfigPackageFormat", e);
		}
	}

	private Map<String, String> unzipParamFile(String paramZipFilePath) {
		try {
			return ZipFileUtil.unzip(paramZipFilePath, charset, tempDirPath);
		} catch (IOException e) {
			throw new PackageException("msg.broadposPackage.invalidParamPackageFormat", e);
		}
	}

	private void addRootCertToPackage(Map<String, String> zipFileEntries) {
		if (signer != null && signer.getRootCert() != null) {
			File rootCert = new File(signer.getRootCert());
			this.fileMap.put(rootCert.getName(), rootCert.getPath());
		}
	}

	private void addScriptCertToPackage(Map<String, String> zipFileEntries) {
		if (signer != null && signer.getScriptCert() != null) {
			File scriptCert = new File(signer.getScriptCert());
			this.fileMap.put(scriptCert.getName(), scriptCert.getPath());
		}
	}

	private void parseConfigFile() throws IOException {
		String confFilePath = fileMap.get("config.xml");
		if (confFilePath == null) {
			throw new PackageException("msg.broadposPackage.noConfigFile");
		}
		configFile = ConfigFile.parse(confFilePath);
	}

	private void validateModelName() {
		String mdlName = configFile.getApp().get("MDL_ABRNAM");
		if (!modelName.equals(mdlName)) {
			// The Model is not match!
			throw new PackageException("msg.broadposPackage.modelNameUnmatched");
		}
	}

	private void processFirmwarePrograms() throws IOException {
		for (Map<String, String> program : configFile.getFirmwarePrograms()) {
			packProgramFiles(program);
		}
	}

	private void processModulePrograms() throws IOException {
		for (Map<String, String> program : configFile.getModulePrograms()) {
			packProgramFiles(program);
			addPctFile(program);
		}
	}

	private void addPctFile(Map<String, String> program) {
		String pctFileName = program.get("PCT_FLNM");
		if (!StringUtils.isEmpty(pctFileName)) {
			String pctFilePath = fileMap.get(pctFileName);
			if (pctFilePath == null) {
				throw new PackageException("msg.broadposPackage.noPctFile", new String[] { pctFileName });
			}
			program.put(PCT_ZIP_FILE, pctFilePath);
		}
	}

	private void packProgramFiles(Map<String, String> program) throws IOException {
		String programFileName = program.get("PGO_FLNM");
		String signFileName = program.get("PGS_FLNM");
		String fileList = program.get("FILE");
		String packDirName = getPackDirName(programFileName);

		createSignFile(signFileName, programFileName, packDirName, program);
		createPackFile(fileList, programFileName, packDirName, program);
	}

	private String getPackDirName(String programFileName) {
		String packFilename = programFileName;
		int index = packFilename.indexOf(".");
		if (index > -1) {
			packFilename = packFilename.substring(0, index);
		}
		if (StringUtils.isEmpty(packFilename)) {
			throw new PackageException("msg.broadposPackage.invalidProgramFileName", new String[] { programFileName });
		}
		return packFilename;
	}

	private void createSignFile(String signFileName, String programFileName, String packDirName,
			Map<String, String> program) throws IOException {
		if (signFileName == null) {
			return;
		}

		String digestFileName = PackageUtils.getFilePrefix(signFileName) + ".d";
		String digestFilePath = fileMap.get(digestFileName);
		if (digestFilePath == null) {
			throw new PackageException("msg.broadposPackage.noDigestFile", new String[] { digestFileName });
		}
		String signFilePath = new File(PackageUtils.getParentFilePath(digestFilePath), signFileName).getPath();

		byte[] signature = signer.sign(digestFilePath);
		String sigVer = signer.getSignVersion();
		SignFile.generateSignFile(signature, sigVer, signFilePath);
		fileMap.put(signFileName, signFilePath);

		File pgsPackDir = new File(tempDirPath, "S" + packDirName);
		createPackDir(pgsPackDir);
		FileUtils.copyFile(new File(signFilePath), new File(pgsPackDir, signFileName));

		File pgsZipFile = new File(tempDirPath, "S" + programFileName);
		ZipFileUtil.zip(pgsZipFile.getPath(), charset, pgsPackDir.listFiles());
		program.put(PGS_ZIP_FILE, pgsZipFile.getPath());
		program.put(SIGN_VER, sigVer);
	}

	private void createPackFile(String fileList, String programFileName, String packDirName,
			Map<String, String> program) throws IOException {

		File pgoPackDir = new File(tempDirPath, packDirName);
		File pgdPackDir = new File(tempDirPath, "D" + packDirName);
		File pgtPackDir = new File(tempDirPath, "T" + packDirName);

		createPackDir(pgoPackDir);
		createPackDir(pgdPackDir);
		createPackDir(pgtPackDir);

		preparePackDir(fileList, pgoPackDir, pgdPackDir, pgtPackDir);
		zipPackDir(programFileName, pgoPackDir, pgdPackDir, pgtPackDir, program);
	}

	private static void createPackDir(File packDir) {
		if (packDir.isFile()) {
			throw new PackageException("msg.broadposPackage.invalidPackage.notDirectory",
					new String[] { packDir.getName() });
		}
		if (!packDir.exists()) {
			packDir.mkdir();
		}
	}

	private void preparePackDir(String fileList, File pgoPackDir, File pgdPackDir, File pgtPackDir) throws IOException {
		if (fileList == null) {
			return;
		}
		String[] files = fileList.split("[|]");
		for (int j = 0; j < files.length; j++) {
			String filename = StringUtils.trimToNull(files[j]);
			if (filename == null) {
				continue;
			}

			String filePath = fileMap.get(filename);
			if (filePath == null) {
				throw new PackageException("msg.broadposPackage.missingProgramFile", new String[] { filename });
			}

			if (filePath.endsWith(".d")) {
				FileUtils.copyFile(new File(filePath), new File(pgdPackDir, filename));
			} else if (!filePath.endsWith(".v")) {
				FileUtils.copyFile(new File(filePath), new File(pgoPackDir, filename));
			}

			FileUtils.copyFile(new File(filePath), new File(pgtPackDir, filename));
		}
	}

	private void zipPackDir(String programFileName, File pgoPackDir, File pgdPackDir, File pgtPackDir,
			Map<String, String> program) throws IOException {
		File pgoZipFile = null;
		if ("common.zip".equals(programFileName)) {
			pgoZipFile = new File(tempDirPath, programFileName);
			ZipFileUtil.zip(pgoZipFile.getPath(), charset, pgoPackDir.listFiles());
		} else {
			pgoZipFile = new File(tempDirPath, programFileName);
			ZipFileUtil.zip(pgoZipFile.getPath(), charset, pgoPackDir.getName(), new File[] { pgoPackDir });
		}

		File pgdZipFile = new File(tempDirPath, "D" + programFileName);
		ZipFileUtil.zip(pgdZipFile.getPath(), charset, pgdPackDir.listFiles());

		File pgtZipFile = new File(tempDirPath, "T" + programFileName);
		ZipFileUtil.zip(pgtZipFile.getPath(), charset, pgtPackDir.listFiles());

		program.put(PGO_ZIP_FILE, pgoZipFile.getPath());
		program.put(PGD_ZIP_FILE, pgdZipFile.getPath());
		program.put(PGT_ZIP_FILE, pgtZipFile.getPath());
	}

	public String getPackageFilePath() {
		return packageFilePath;
	}

	public void setPackageFilePath(String packageFilePath) {
		this.packageFilePath = packageFilePath;
	}

	public String getPackagefileName() {
		return packagefileName;
	}

	public void setPackagefileName(String packagefileName) {
		this.packagefileName = packagefileName;
	}

	public long getPackageFileSize() {
		return packageFileSize;
	}

	public void setPackageFileSize(long packageFileSize) {
		this.packageFileSize = packageFileSize;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Signer getSigner() {
		return signer;
	}

	public void setSigner(Signer signer) {
		this.signer = signer;
	}

	public Map<String, String> getFileMap() {
		return fileMap;
	}

	public ConfigFile getConfigFile() {
		return configFile;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public void close() throws IOException {
		delete();
	}

	public void delete() throws IOException {
		if (StringUtils.isNoneEmpty(tempDirPath)) {
			FileUtils.deleteDirectory(new File(tempDirPath));
			tempDirPath = null;
		}
	}

}
