package com.pax.tms.res;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

public class FileToBase64 {

	public static void main(String[] args) throws IOException {
		String filePath = "PXMaster package/Uninstall_All_Forms.zip";
		byte[] data = FileUtils.readFileToByteArray(new File(filePath));
		String text = new String(Base64.encodeBase64(data));
		System.out.println(text);
	}

}
