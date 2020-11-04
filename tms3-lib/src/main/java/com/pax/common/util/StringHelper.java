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
package com.pax.common.util;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.zip.CRC32;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.pax.common.exception.AppException;

public class StringHelper {

	private StringHelper() {
	}

	public static String str2Hex(byte[] b) {
		return new String(Hex.encodeHex(b));
	}

	public static String hex2Str(String str) throws DecoderException {
		return new String(Hex.decodeHex(str.toCharArray()));
	}

	public static byte[] hex2ByteArray(String hex) {
		int length = hex.length();
		if ((length & 0x1) != 0) {
			throw new AppException("Odd number of characters.");
		}
		byte[] bytes = new byte[length >> 1];

		for (int i = 0; i < length / 2; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
		}
		return bytes;
	}

	public static String getCrcValue(String hex) {
		CRC32 crc = new CRC32();
		crc.update(hex2ByteArray(hex));
		String hexCrcValue = Integer.toHexString((int) crc.getValue());
		return StringUtils.leftPad(hexCrcValue, 8, '0');
	}

	public static byte[] str2bcd(String string) {
		String s = string;
		if (s.length() % 2 != 0) {
			s = "0" + s;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		char[] cs = s.toCharArray();
		for (int i = 0; i < cs.length; i += 2) {
			int high = cs[i] - 48;
			int low = cs[i + 1] - 48;
			baos.write(high << 4 | low);
		}
		return baos.toByteArray();
	}

	public static char[] hex2CharArray(String hex) {
		int length = hex.length();
		if (length % 2 != 0) {
			throw new AppException("length can not be odd number");
		}
		char[] array = new char[length / 2];
		for (int i = 0; i < length / 2; i++) {
			array[i] = (char) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
		}
		return array;
	}

	public static String charArray2Hex(char[] array) {
		String hex = "", buffer;
		for (char c : array) {
			buffer = Integer.toHexString(c);
			buffer = buffer.length() == 1 ? "0" + buffer : buffer;
			hex += buffer;
		}
		return hex;
	}

	public static String char2Hex(char c) {
		String buffer;
		buffer = Integer.toHexString(c);
		buffer = buffer.length() == 1 ? "0" + buffer : buffer;
		return buffer;
	}

	public static String char2Hex(byte b) {
		String buffer;
		buffer = Integer.toHexString(b);
		buffer = buffer.length() == 1 ? "0" + buffer : buffer;
		return buffer;
	}

	public static String filter(String input) {
		String tmp = input;
		tmp = tmp.replace("´", "'");
		tmp = tmp.replace("”", "\"");
		tmp = tmp.replace("“", "\"");
		tmp = tmp.replace("‘", "'");
		return tmp;
	}

	public static long hex2Dec(String hexString) {
		return Long.parseLong(hexString, 16);
	}

	/**
	 * 取随机字符串 <br>
	 * 
	 * @param arg0
	 *            构造指定长度的随机字符串
	 * @param arg1
	 *            指明是否包含字母，0-包含字母,数字和字母混合,默认是2 1-不包含数字,只有字母 2－不包含字母,只有数字
	 * @return
	 * @throws HiException
	 */
	public static String random(int len, int flag) {
		if (flag == 0) {
			return RandomStringUtils.randomAlphanumeric(len);
		} else if (flag == 1) {
			return RandomStringUtils.randomAlphabetic(len);
		} else {
			return RandomStringUtils.randomNumeric(len);
		}
	}

	/**
	 * <p>
	 * 获取当时的时间
	 * </p>
	 * 
	 * <pre>
	 *            获取当时的时间。 
	 *            替换字符串中的YYYY、YY、MM、DD、HH、MI、SS为对应的数值，其它字符不变。
	 *            GETDATETIME（YYMMDD）     040801
	 *            GETDATETIME（YYYY年MM月）  2004年08月
	 * </pre>
	 * 
	 * @param str
	 *            日期格式
	 * @return 时间格式字符串
	 */

	public static String getDatetime(String format) {
		String[] buf1 = { "YYYY", "YY", "MM", "DD", "HH", "MI", "SS" };
		String[] buf2 = { "yyyy", "yy", "MM", "dd", "HH", "mm", "ss" };
		String str = format.trim();
		for (int i = 0; i < buf1.length; i++) {
			str = StringUtils.replace(str, buf1[i], buf2[i]);
		}
		Calendar calendar = Calendar.getInstance();
		return DateFormatUtils.format(calendar.getTime(), str);
	}

	/**
	 * <p>
	 * 获取当时的时间
	 * </p>
	 * 
	 * <pre>
	 *            获取当时的时间。 
	 *            getDatetime（）  20040801123059
	 * </pre>
	 * 
	 * @return 时间格式字符串
	 */
	public static String getDatetime() {
		return getDatetime("yyyyMMDDHHmmss");
	}

	// String to boolean methods
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Converts a String to a boolean (optimised for performance).
	 * </p>
	 * 
	 * <p>
	 * <code>'true'</code>, <code>'on'</code> or <code>'yes'</code> (case
	 * insensitive) will return <code>true</code>. Otherwise, <code>false</code>
	 * is returned.
	 * </p>
	 * 
	 * <p>
	 * This method performs 4 times faster (JDK1.4) than
	 * <code>Boolean.valueOf(String)</code>. However, this method accepts 'on'
	 * and 'yes' as true values.
	 * 
	 * <pre>
	 *   BooleanUtils.toBoolean(null)    = false
	 *   BooleanUtils.toBoolean("true")  = true
	 *   BooleanUtils.toBoolean("TRUE")  = true
	 *   BooleanUtils.toBoolean("tRUe")  = true
	 *   BooleanUtils.toBoolean("on")    = true
	 *   BooleanUtils.toBoolean("yes")   = true
	 *   BooleanUtils.toBoolean("false") = false
	 *   BooleanUtils.toBoolean("x gti") = false
	 * </pre>
	 * 
	 * @param str
	 *            the String to check
	 * @return the boolean value of the string, <code>false</code> if no match
	 */
	public static boolean toBoolean(String str) {
		// Previously used equalsIgnoreCase, which was fast for interned 'true'.
		// Non interned 'true' matched 15 times slower.
		//
		// Optimisation provides same performance as before for interned 'true'.
		// Similar performance for null, 'false', and other strings not length
		// 2/3/4.
		// 'true'/'TRUE' match 4 times slower, 'tRUE'/'True' 7 times slower.
		if (str == "true") {
			return true;
		}
		if (str == null) {
			return false;
		}

		char ch0;
		char ch1;
		switch (str.length()) {
		case 1:
			ch0 = str.charAt(0);
			return ch0 == '1';
		case 2:
			ch0 = str.charAt(0);
			ch1 = str.charAt(1);
			return (ch0 == 'o' || ch0 == 'O') && (ch1 == 'n' || ch1 == 'N');
		case 3:
			return yesNoStringToBoolean(str);
		case 4:
			return trueFalseStringToBoolean(str);
		}
		return false;
	}

	private static boolean yesNoStringToBoolean(String str) {
		char ch = str.charAt(0);
		if (ch == 'y') {
			return (str.charAt(1) == 'e' || str.charAt(1) == 'E') && (str.charAt(2) == 's' || str.charAt(2) == 'S');
		}
		if (ch == 'Y') {
			return (str.charAt(1) == 'E' || str.charAt(1) == 'e') && (str.charAt(2) == 'S' || str.charAt(2) == 's');
		}
		return false;
	}

	private static boolean trueFalseStringToBoolean(String str) {
		char ch = str.charAt(0);
		if (ch == 't') {
			return (str.charAt(1) == 'r' || str.charAt(1) == 'R') && (str.charAt(2) == 'u' || str.charAt(2) == 'U')
					&& (str.charAt(3) == 'e' || str.charAt(3) == 'E');
		}
		if (ch == 'T') {
			return (str.charAt(1) == 'R' || str.charAt(1) == 'r') && (str.charAt(2) == 'U' || str.charAt(2) == 'u')
					&& (str.charAt(3) == 'E' || str.charAt(3) == 'e');
		}
		return false;
	}

	public static String int2Hex(int len) {
		int nHigh = len / 65536;
		int nLow = len % 65536;
		String hex1 = Integer.toHexString(nHigh / 256);
		hex1 = hex1.length() == 2 ? hex1 : "0" + hex1;
		String hex2 = Integer.toHexString(nHigh % 256);
		hex2 = hex2.length() == 2 ? hex2 : "0" + hex2;
		String hex3 = Integer.toHexString(nLow / 256);
		hex3 = hex3.length() == 2 ? hex3 : "0" + hex3;
		String hex4 = Integer.toHexString(nLow % 256);
		hex4 = hex4.length() == 2 ? hex4 : "0" + hex4;
		return hex1 + hex2 + hex3 + hex4;
	}

	public static String string(char c, int len) {
		StringBuilder buff = new StringBuilder(len);
		for (int j = 0; j < len; j++) {
			buff.append(c);
		}
		return buff.toString();
	}

	public static String getTheSameSubString(String str1, String str2) {
		if (str1 == null || str2 == null) {
			return null;
		}

		int maxLen = str1.length() <= str2.length() ? str1.length() : str2.length();
		int len = 0;
		while (len < maxLen && str1.charAt(len) == str2.charAt(len)) {
			len++;
		}

		if (len == 0) {
			return null;
		}
		return str1.substring(0, len);
	}

	public static String escapse(String keywords) {
		if (StringUtils.isEmpty(keywords)) {
			return null;
		}

		return keywords.replaceAll("!", "!!").replaceAll("%", "!%").replaceAll("_", "!_");
	}
    public static String getRandomPwdByLength(int length) {
        String base = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%&*";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
	
}
