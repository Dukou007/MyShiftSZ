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

import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class CodecHelper {

	private CodecHelper() {
		// utility class
	}

	/**
	 * decode Asc to bcd
	 * 
	 * @param bytes
	 * @return
	 * @throws DecoderException
	 */
	public static byte[] ascByte2Bcd(byte[] bytes) throws DecoderException {
		Hex hex = new Hex();
		return hex.decode(bytes);
	}

	/**
	 * encode bcd to Asc
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] bcd2AscByte(byte[] bytes) {
		Hex hex = new Hex();
		return hex.encode(bytes);
	}

	/**
	 * Converts byte[8] to long representation.
	 * 
	 * @param input
	 *            byte[8] that will be converted.
	 * @param inputOffset
	 *            the offset in input where input starts.
	 * @throws IllegalBlockSizeException
	 *             if input length lesser than 8.
	 * @return the long representation.
	 */
	public static long toLong(byte[] input, int inputOffset) throws IllegalBlockSizeException {
		if (input.length - inputOffset < 8) {
			throw new IllegalBlockSizeException("Usable byte range is " + (input.length - inputOffset)
					+ " bytes large, but it should be 8 bytes or larger.");
		}

		long returnValue = 0L;

		for (int i = inputOffset; i - inputOffset < 8; i++) {
			returnValue |= (input[i] & 0x00000000000000ffL) << (64 - 8 - 8 * (i - inputOffset));
		}

		return returnValue;
	}

	/**
	 * Converte um array de (4) bytes em um int.
	 * 
	 * @param bytes
	 *            o array de bytes a ser convertido
	 * @return o int formado pelos bytes dados.
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int result = 0;
		result = result | (0xFF000000 & (bytes[0] << 24));
		result = result | (0x00FF0000 & (bytes[1] << 16));
		result = result | (0x0000FF00 & (bytes[2] << 8));
		result = result | (0x000000FF & (bytes[3]));
		return result;
	}

	/**
	 * Converte um array de (8) bytes em um long.
	 * 
	 * @param bytes
	 *            o array de bytes a ser convertido
	 * @return o long formado pelos bytes dados.
	 */
	public static long byteArrayToLong(byte[] bytes) {
		long result = 0;
		result = result | (0xFF00000000000000L & (((long) bytes[0]) << 56));
		result = result | (0x00FF000000000000L & (((long) bytes[1]) << 48));
		result = result | (0x0000FF0000000000L & (((long) bytes[2]) << 40));
		result = result | (0x000000FF00000000L & (((long) bytes[3]) << 32));
		result = result | (0x00000000FF000000L & (((long) bytes[4]) << 24));
		result = result | (0x0000000000FF0000L & (((long) bytes[5]) << 16));
		result = result | (0x000000000000FF00L & (((long) bytes[6]) << 8));
		result = result | (0x00000000000000FFL & ((long) bytes[7]));
		return result;
	}

	/**
	 * Converte um array de (2) bytes em um short.
	 * 
	 * @param bytes
	 *            o array de bytes a ser convertido
	 * @return o short formado pelos bytes dados.
	 */
	public static short byteArrayToShort(byte[] bytes) {
		int result = 0;
		result = result | (0x0000FF00 & (bytes[0] << 8));
		result = result | (0x000000FF & (bytes[1]));
		return (short) result;
	}

	/**
	 * Converte um inteiro em um array de (4) bytes.
	 * 
	 * @param valor
	 *            o inteiro a ser convertido
	 * @return o array dos bytes do inteiro dado.
	 */
	public static byte[] intToByteArray(int value) {
		int valor = value;
		byte[] result = new byte[4];
		for (int i = 0; i < result.length; i++) {
			result[3 - i] = (byte) (valor & 0xFF);
			valor = valor >> 8;
		}
		return result;
	}

	/**
	 * Converte um long em um array de (8) bytes.
	 * 
	 * @param valor
	 *            o long a ser convertido
	 * @return o array dos bytes do long dado.
	 */
	public static byte[] longToByteArray(long value) {
		long valor = value;
		byte[] result = new byte[8];
		for (int i = 0; i < result.length; i++) {
			result[7 - i] = (byte) (valor & 0xFF);
			valor = valor >> 8;
		}
		return result;
	}

	/**
	 * Converte um short em um array de (2) bytes.
	 * 
	 * @param valor
	 *            o short a ser convertido
	 * @return o array dos bytes do short dado.
	 */
	public static byte[] shortToByteArray(int value) {
		int valor = value;
		byte[] result = new byte[2];
		for (int i = 0; i < result.length; i++) {
			result[1 - i] = (byte) (valor & 0xFF);
			valor = valor >> 8;
		}
		return result;
	}

}
