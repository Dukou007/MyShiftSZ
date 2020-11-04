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

public class Hex {

	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	private Hex() {
	}

	public static String string(byte... data) {
		return new String(encode(data));
	}

	public static char[] encode(byte... data) {

		int l = data.length;

		char[] out = new char[l << 1];

		// two characters form the hex value.
		int j = 0;
		for (int i = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return out;
	}

	public static char[] encode(byte[] data, int off, int len) {
		char[] out = new char[len << 1];

		// two characters form the hex value.
		int j = 0;
		for (int i = 0; i < len; i++) {
			out[j++] = DIGITS[(0xF0 & data[off + i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[off + i]];
		}
		return out;
	}

	public static byte[] decodeHex(final char[] data) {

		final int len = data.length;

		if ((len & 0x01) != 0) {
			throw new DecoderException("Odd number of characters.");
		}

		final byte[] out = new byte[len >> 1];

		// two characters form the hex value.
		int i = 0;
		int j = 0;
		while (j < len) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
			i++;
		}
		return out;
	}

	protected static int toDigit(final char ch, final int index) {
		final int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
		}
		return digit;
	}

}
