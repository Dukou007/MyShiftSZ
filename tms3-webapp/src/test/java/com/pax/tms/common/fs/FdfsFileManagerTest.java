package com.pax.tms.common.fs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.codec.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pax.common.fs.FileManager;
import com.pax.common.fs.FileManagerUtils;
import com.pax.fastdfs.proto.storage.DownloadCallback;

@ContextConfiguration(locations = { "classpath:ppm/spring-configuration/propertyFileConfigurer.xml",
		"classpath:ppm/spring-configuration/emailContext.xml", "classpath:ppm/spring-configuration/spring-config.xml",
		"classpath:ppm/spring-configuration/spring-shiro.xml", "classpath:ppm/spring-configuration/scheduler.xml",
		"classpath:ppm/spring-configuration/spring-redis.xml",
		"classpath:ppm/spring-configuration/spring-fastdfs.xml" })

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(transactionManager = "txManager")
@Rollback
// @Commit
public class FdfsFileManagerTest {

	@Autowired
	private FileManager fileManager;

	@Test
	public void testUploadFile() {
		byte[] buffer = new byte[] { '1', '2', '3', '4', '5', '6' };
		InputStream inputStream = new ByteArrayInputStream(buffer);
		String path = fileManager.uploadFile(inputStream, buffer.length, "dat");

		System.out.println(path);
		assertThat(path, notNullValue());
	}
	@Test
	public void noFileExtNameFile(){
		
		FileManagerUtils.saveLocalFileToFdfs("c:/aa");
		
	}
	
	@Test
	public void testDownloadFile() {
		byte[] buffer = new byte[] { '1', '2', '3', '4', '5', '6' };
		InputStream inputStream = new ByteArrayInputStream(buffer);
		String storePath = fileManager.uploadFile(inputStream, buffer.length, "dat");
		byte[] result = fileManager.downloadFile(storePath, new DownloadCallback<byte[]>() {
			@Override
			public byte[] recv(InputStream ins) throws IOException {
				return IOUtils.readFully(ins, (int) buffer.length);
			}
		});
		assertThat(buffer, equalTo(result));
	}

	@Test
	public void testDownloadFileRange() throws IOException {
		byte[] buffer = new byte[] { '1', '2', '3', '4', '5', '6' };
		InputStream inputStream = new ByteArrayInputStream(buffer);
		String storePath = fileManager.uploadFile(inputStream, buffer.length, "dat");

		System.out.println(storePath);

		int len = 3;
		byte[] result = fileManager.downloadFile(storePath, 2, len, new DownloadCallback<byte[]>() {
			@Override
			public byte[] recv(InputStream ins) throws IOException {
				return IOUtils.readFully(ins, len);
			}
		});
		System.out.println(new String(result));
		assertThat(buffer, equalTo(result));
	}

	@Test
	public void testDownloadBigFileRange() throws IOException {
		byte[] buffer = FileUtils.readFileToByteArray(
				new File("E:\\PAX\\TMS3.0\\workspace\\tms3\\tms3-webapp\\PXMaster package\\PXRetailer-0.91.0.zip"));

		InputStream inputStream = new ByteArrayInputStream(buffer);
		String storePath = fileManager.uploadFile(inputStream, buffer.length, "dat");
		
		System.out.println(storePath);

		int len = 10;
		byte[] result = fileManager.downloadFile(storePath, 10, len, new DownloadCallback<byte[]>() {
			@Override
			public byte[] recv(InputStream ins) throws IOException {
				return IOUtils.readFully(ins, len);
			}
		});
		System.out.println(Hex.encodeToString(result));
		assertThat(buffer, equalTo(result));
	}
	
	@Test
	public void testDeleteFile(){
		try{
		FileManagerUtils.getFileManager().deleteFile("group1/M00/00/01/wKhkmFkP5H2AYOroAJqs_coWhTg29ffd7.zip");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
