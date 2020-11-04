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
package com.pax.common.security;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.ConfigurableHashService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;

public class HashedCredentials {
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaDPx1NwZIPov0B28oNB9paXw6j4/+Co/dKz4r2+o323zb3QaMvwfhlNu6IJzhKesyqixxbeQYCZMcikd17SHdTThHYgDifxElFk3rd9i6iBhd+TQMuYgCvTc+orp+xH7gLVYka4oQK2NW9ytQVrOqd1QULKxf0mle1ADlr8uKNQIDAQAB";
    private static String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJoM/HU3Bkg+i/QHbyg0H2lpfDqPj/4Kj90rPivb6jfbfNvdBoy/B+GU27ognOEp6zKqLHFt5BgJkxyKR3XtId1NOEdiAOJ/ESUWTet32LqIGF35NAy5iAK9Nz6iun7EfuAtViRrihArY1b3K1BWs6p3VBQsrF/SaV7UAOWvy4o1AgMBAAECgYBIrFmo3lVrXX5el+c7eyNacjX11mYifU8TEmRZAn0C7tt/SUzetvv70mK58sqvejwHgbpHpITXRiUNcLp3im/PoFCEKBURdGWOS1CxwheOnfaTArz5ZRNmYSqo9s5dRgvnUcyxr9aPEKD6ZoJr9G5xcST4obb+ybP+lEu6+eUYfQJBAMbNkK0IYEZhdO/LLV10/U6ExgzcEyhRCrzjfULBuWiuL8YJdSAi7gHZPhy93KdXfwoezuf927w2bOSG7OzQTaMCQQDGX0h3mqO2DIdUd1LTfduAXNhCD44KKnH9QT96n4SDQsEqSErNHFbButN1+STVY0qr3HeIKy/yCoCG5x6Y3hZHAkBCHS0HDmkOHu0HrjcpBHYVPbyrnCjW2JTMmo8Wu1xQvtTudEi6ZkNm4/tvDYkrMcLy96nxpxADeMof+esjGmcnAkAYCIs7El0rPTFYJmy+es0RLC53MnM/AA13ZWRPcuwXCwtkGAYX+4r3Ra9A58Jyp+jwEXHZ6YGRjXws2+t1EdMZAkB/SW1DsR9PyZmJOSU+sgvmWal9Tu920KpoSQW547isRJIMn9XZ4/4HPyLwSi/TN/gvHypMVY80+UzA4nUxraP7";
	// We'll use a Random Number Generator to generate salts. This
	// is much more secure than using a username as a salt or not
	// having a salt at all. Shiro makes this easy.
	private static RandomNumberGenerator rng = new SecureRandomNumberGenerator();

	private HashedCredentials() {
		// utility class
	}

	public static String sha256Hash(String plainTextPassword) {
		Object salt = rng.nextBytes();
		// Now hash the plain-text password with the random salt and multiple
		// iterations and then Base64-encode the value (requires less space than
		// Hex):
		return new Sha256Hash(plainTextPassword, salt, 1024).toBase64();
	}

	public static String encodedPassword(String inputtedPassword, String algorithmName, String staticSalt,
			Object dynaSalt, Integer numberOfIterations) {
		final ConfigurableHashService hashService = new DefaultHashService();

		if (StringUtils.isNotBlank(staticSalt)) {
			hashService.setPrivateSalt(ByteSource.Util.bytes(staticSalt));
		}

		HashRequest.Builder builder = new HashRequest.Builder();
		hashService.setHashAlgorithmName(algorithmName);
		if (dynaSalt != null) {
			if (dynaSalt instanceof String) {
				String str = (String) dynaSalt;
				if (str.length() != 0) {
					builder.setSalt(dynaSalt);
				}
			} else {
				builder.setSalt(dynaSalt);
			}
		}
		if (numberOfIterations != null && numberOfIterations > 0) {
			builder.setIterations(numberOfIterations);
		}
		//页面传过来的密码是经过RSA加密的密码，需要先解密
		String decryptPassword = decrypt(inputtedPassword, privateKey);
		builder.setSource(decryptPassword);
		HashRequest request = builder.build();
		return hashService.computeHash(request).toHex();
	}

	public static PasswordInfo digestEncodedPassword(String password) {
		String algorithmName = "SHA-512";
		String staticSalt = "pax";
		String dynaSalt = rng.nextBytes().toHex();
		int numberOfIterations = RandomUtils.nextInt(0, 1025);
		String encodedPassword = encodedPassword(password, algorithmName, staticSalt, dynaSalt, numberOfIterations);
		return new PasswordInfo(encodedPassword, algorithmName, dynaSalt, numberOfIterations);
	}

	public static String getStatcSalt() {
		return "pax";
	}
	/**
	 * @Description: 用私钥解密密码
	 * @param password
	 * @return
	 * @return: String
	 */
	public static String decryptPassword(String password){
	    return decrypt(password, privateKey);
	}
	/**
	 * @Description: 用公钥加密密码
	 * @param password
	 * @return
	 * @return: String
	 */
	public static String encryptPassword(String password){
        return encrypt(password, publicKey);
    }
	/** 
     * RSA公钥加密 
     *  
     * @param str 
     *            加密字符串
     * @param publicKey 
     *            公钥 
     * @return 密文 
     * @throws Exception 
     *             加密过程中的异常信息 
     */  
    public static String encrypt( String str, String publicKey ){
        String outStr = null;
        RSAPublicKey pubKey;
        try {
            //base64编码的公钥
            byte[] decoded = Base64.decodeBase64(publicKey.getBytes("UTF-8"));
            pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] en_data = cipher.doFinal(str.getBytes("UTF-8"));
            outStr = Base64.encodeBase64String(en_data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outStr;
    }
    /** 
     * RSA私钥解密
     *  
     * @param str 
     *            加密字符串
     * @param privateKey 
     *            私钥 
     * @return 铭文
     * @throws Exception 
     *             解密过程中的异常信息 
     */  
    public static String decrypt(String str, String privateKey){
        String outStr = "";
        try {
            //64位解码加密后的字符串
            byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
            //base64编码的私钥
            byte[] decoded = Base64.decodeBase64(privateKey); 
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));  
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            return outStr;
        }
    }
    
	public static class PasswordInfo {
		private String encodedPassword;
		private String algorithmName;
		private String dynaSalt;
		private int numberOfIterations;

		public PasswordInfo() {
		}

		public PasswordInfo(String encodedPassword, String algorithmName, String dynaSalt, int numberOfIterations) {
			super();
			this.encodedPassword = encodedPassword;
			this.algorithmName = algorithmName;
			this.dynaSalt = dynaSalt;
			this.numberOfIterations = numberOfIterations;
		}

		@Override
		public String toString() {
			return "PasswordInfo: encodedPassword=" + encodedPassword + ";algorithmName=" + algorithmName + ";dynaSalt="
					+ dynaSalt + ";numberOfIterations=" + numberOfIterations;
		}

		public String getEncodedPassword() {
			return encodedPassword;
		}

		public void setEncodedPassword(String encodedPassword) {
			this.encodedPassword = encodedPassword;
		}

		public String getAlgorithmName() {
			return algorithmName;
		}

		public void setAlgorithmName(String algorithmName) {
			this.algorithmName = algorithmName;
		}

		public String getDynaSalt() {
			return dynaSalt;
		}

		public void setDynaSalt(String dynaSalt) {
			this.dynaSalt = dynaSalt;
		}

		public int getNumberOfIterations() {
			return numberOfIterations;
		}

		public void setNumberOfIterations(int numberOfIterations) {
			this.numberOfIterations = numberOfIterations;
		}

	}
}
