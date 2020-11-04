/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 20170112	             Jaden           	    Modify
 * ============================================================================		
 */
package com.pax.tms.terminal.service.impl;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.pax.common.exception.BusinessException;
import com.pax.common.util.RegexMatchUtils;
import com.pax.tms.terminal.model.Terminal;

public class TerminalSerialNumber {

	private TerminalSerialNumber() {
	}

	public static void validateTsn(String tsn) {
		if (!RegexMatchUtils.isMatcher(tsn, Terminal.TSN_REGEX)) {
			throw new BusinessException("tsn.invalidTsn", new String[] { tsn });
		}
	}

	public static Set<String> parseTsns(String[] tsns, int maxBatchSize) {
		int maxSize = maxBatchSize;
		if (maxSize <= 0) {
			maxSize = Integer.MAX_VALUE;
		}

		Set<String> result = new TreeSet<String>();
		if (tsns != null) {
			for (String tsn : tsns) {
				parseTsn(tsn, maxSize, result);
			}
		}
		return result;
	}

	private static void parseTsn(String tsn, int maxBatchSize, Set<String> result) {
		String trimmedTsn = StringUtils.trimToNull(tsn);
		if (trimmedTsn == null) {
			return;
		}
		trimmedTsn = trimmedTsn.toUpperCase();
		if (trimmedTsn.contains("-")) {
			parseRange(tsn, maxBatchSize, result);
		} else {
			validateTsn(trimmedTsn);
			if (result.size() + 1 > maxBatchSize) {
				throw new BusinessException("tsn.error.exceedBatchSize", new String[] { String.valueOf(maxBatchSize) });
			}
			result.add(trimmedTsn);
		}
	}

	private static void parseRange(String rangeStr, int maxBatchSize, Set<String> result) {
		String start = null;
		String end = null;

		String[] tsns = rangeStr.split("-");
		if (tsns.length != 2) {
			throw new BusinessException("tsn.tsnRange.illegal", new String[] { rangeStr });
		}
		start = StringUtils.trimToNull(tsns[0]);
		end = StringUtils.trimToNull(tsns[1]);

		validateRangeStr(rangeStr, start, end);

		int p = getPrefixIndex(start, end);
		String prefix = start.substring(0, p);
		start = start.substring(p, start.length());
		end = end.substring(p, end.length());
		if (StringUtils.isEmpty(start) && StringUtils.isEmpty(end)) {
			result.add(prefix);
			return;
		}

		validateRangeNumber(rangeStr, start, end, maxBatchSize - result.size());

		addTerminalSerialNumber(result, prefix, start, end);
	}

	private static void addTerminalSerialNumber(Collection<String> result, String prefix, String startNumber,
			String endNumber) {
		long lStart = Long.parseLong(startNumber);
		long lEnd = Long.parseLong(endNumber);
		int len = startNumber.length();
		int count = (int) (lEnd - lStart + 1);
		for (int i = 0; i < count; i++) {
			result.add(prefix + StringUtils.leftPad(String.valueOf(lStart + i), len, "0"));
		}
	}

	private static void validateRangeNumber(String rangeStr, String startNumber, String endNumber, int limitSize) {
		if (!StringUtils.isNumeric(startNumber) || !StringUtils.isNumeric(endNumber)) {
			throw new BusinessException("tsn.tsnRange.notConsecutive", new String[] { rangeStr });
		}

		long lStart = Long.parseLong(startNumber);
		long lEnd = Long.parseLong(endNumber);

		if (lStart > lEnd) {
			throw new BusinessException("tsn.tsnRange.startGtEnd", new String[] { rangeStr });
		}

		if (lEnd - lStart + 1 > limitSize) {
			throw new BusinessException("tsn.error.exceedBatchSize", new String[] { String.valueOf(limitSize) });
		}
	}

	private static void validateRangeStr(String rangeStr, String startStr, String endStr) {
		if (startStr == null || endStr == null) {
			throw new BusinessException("tsn.tsnRange.illegal", new String[] { rangeStr });
		}

		if (startStr.length() != endStr.length()) {
			throw new BusinessException("tsn.tsnRange.notSameLength", new String[] { rangeStr });
		}

		validateTsn(startStr);
		validateTsn(endStr);
	}

	private static int getPrefixIndex(String start, String end) {
		int p = 0;
		for (; p < start.length(); p++) {
			if (start.charAt(p) != end.charAt(p)) {
				break;
			}
		}
		return p;
	}
}
