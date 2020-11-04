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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;

public class DateTimeUtils {

	public static final String PATTERN_STANDARD = "yyyy-MM-dd HH:mm:ss";

	public static final String PATTERN_DATE = "yyyy-MM-dd";

	private DateTimeUtils() {
	}

	public static DateTimeRange getDateTimeRange(String period) {
		DateTimeRange tr = null;
		if (StringUtils.isNotEmpty(period)) {
			if ("mtd".equalsIgnoreCase(period)) {
				tr = DateTimeUtils.monthToDay();
			} else if ("lm".equalsIgnoreCase(period)) {
				tr = DateTimeUtils.lastMonth();
			} else if ("l2m".equalsIgnoreCase(period)) {
				tr = DateTimeUtils.last2Months();
			} else if ("l3m".equalsIgnoreCase(period)) {
				tr = DateTimeUtils.last3Months();
			} else if ("l30d".equalsIgnoreCase(period)) {
				tr = DateTimeUtils.last30Days();
			} else if ("l60d".equalsIgnoreCase(period)) {
				tr = DateTimeUtils.last60Days();
			} else if ("l90d".equalsIgnoreCase(period)) {
				tr = DateTimeUtils.last90Days();
			}
		}
		return tr;
	}

	// mtd
	public static DateTimeRange monthToDay() {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);

		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new DateTimeRange(cal.getTime(), now);
	}

	// lm
	/**
	 * Starts on the first day of the month before the current day and continues
	 * for all the days of that month.
	 * 
	 * @return
	 */
	public static DateTimeRange lastMonth() {
		return lastNMonths(1);
	}

	// l2m
	public static DateTimeRange last2Months() {
		return lastNMonths(2);
	}

	// l3m
	public static DateTimeRange last3Months() {
		return lastNMonths(3);
	}

	// l30d
	/**
	 * Starts the current day and continues for the last 30 days.
	 * 
	 * @return
	 */
	public static DateTimeRange last30Days() {
		return lastNDays(30);
	}

	// l60d
	public static DateTimeRange last60Days() {
		return lastNDays(60);
	}

	// l90d
	public static DateTimeRange last90Days() {
		return lastNDays(90);
	}

	public static DateTimeRange lastNMonths(int n) {
		DateTimeRange tr = new DateTimeRange();
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.MONTH, -n);
        tr.setDate(cal.getTime());
        
        cal.setTime(now);
		cal.add(Calendar.MONTH, -n);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setFrom(cal.getTime());

		cal.setTime(now);
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setTo(cal.getTime());

		return tr;
	}
	
	public static DateTimeRange lastNMonths(int n, Date now) {
        DateTimeRange tr = new DateTimeRange();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, -n);
        tr.setDate(cal.getTime());
        
        cal.setTime(now);
        cal.add(Calendar.MONTH, -n);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        tr.setFrom(cal.getTime());

        cal.setTime(now);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        tr.setTo(cal.getTime());

        return tr;
    }

	// last n days means what exactly?
	public static DateTimeRange lastNDays(int n) {
		DateTimeRange tr = new DateTimeRange();
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, -n);
        tr.setDate(cal.getTime());
        
        cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, -n);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setFrom(cal.getTime());

		cal.setTime(now);
		 cal.add(Calendar.DAY_OF_MONTH, -n);
		 cal.set(Calendar.HOUR_OF_DAY, 23);
		 cal.set(Calendar.MINUTE, 59);
		 cal.set(Calendar.SECOND, 59);
		 cal.set(Calendar.MILLISECOND, 999);
		tr.setTo(cal.getTime());

		return tr;
	}
	
	public static DateTimeRange lastNDays(Date now,int n) {
        DateTimeRange tr = new DateTimeRange();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -n);
        tr.setDate(cal.getTime());
        
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -n);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        tr.setFrom(cal.getTime());

        cal.setTime(now);
         cal.add(Calendar.DAY_OF_MONTH, -n);
         cal.set(Calendar.HOUR_OF_DAY, 23);
         cal.set(Calendar.MINUTE, 59);
         cal.set(Calendar.SECOND, 59);
         cal.set(Calendar.MILLISECOND, 999);
        tr.setTo(cal.getTime());

        return tr;
    }
	
	
	/**
	 * @return
	 * DateTimeRange
	 * from 00:00:00 to 23:59:59
	 */
	public static DateTimeRange getYesterday() {
		DateTimeRange tr = new DateTimeRange();
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, -1);
        tr.setDate(cal.getTime());
        
        cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setFrom(cal.getTime());

		cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setTo(cal.getTime());

		return tr;
	}
	
	public static DateTimeRange getYesterday(Date now) {
        DateTimeRange tr = new DateTimeRange();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        tr.setDate(cal.getTime());
        
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        tr.setFrom(cal.getTime());

        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        tr.setTo(cal.getTime());

        return tr;
    }
	
	/**
	 * @return
	 * from Sunday to Saturday
	 */
	public static DateTimeRange getLastWeek() {
		DateTimeRange tr = new DateTimeRange();
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);

		cal.add(Calendar.WEEK_OF_YEAR, -1);
		
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setFrom(cal.getTime());

		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setTo(cal.getTime());

		return tr;
	}
	
	/**
     * @return
     * from Sunday to Saturday
     */
    public static DateTimeRange getLastWeek(Date now) {
        DateTimeRange tr = new DateTimeRange();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        cal.add(Calendar.WEEK_OF_YEAR, -1);
        
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        tr.setFrom(cal.getTime());

        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        tr.setTo(cal.getTime());

        return tr;
    }
	
	public static DateTimeRange lastNSeconds(int n) {
		DateTimeRange tr = new DateTimeRange();
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		tr.setTo(cal.getTime());

		cal.add(Calendar.SECOND, -n);
		tr.setFrom(cal.getTime());

		return tr;
	}

	public static class DateTimeRange {

		private Date from;
		private Date to;
		private Date date;

		public DateTimeRange() {
		}

		public DateTimeRange(Date from, Date to) {
			this.from = from;
			this.to = to;
		}

		public Date getFrom() {
			return from;
		}

		public void setFrom(Date from) {
			this.from = from;
		}

		public Date getTo() {
			return to;
		}

		public void setTo(Date to) {
			this.to = to;
		}

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
	}

	public static String timestamp2String(Timestamp timestamp, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date(timestamp.getTime()));
	}

	public static String date2String(java.util.Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	public static String date2String(java.util.Date date, String pattern, String timeZone){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		return sdf.format(date);
	}

	public static Timestamp currentTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	public static String currentTimestamp2String(String pattern) {
		return timestamp2String(currentTimestamp(), pattern);
	}

	public static Timestamp string2Timestamp(String strDateTime, String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = sdf.parse(strDateTime);
		return new Timestamp(date.getTime());
	}

	public static Date string2Date(String strDate, String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(strDate);
	}

	public static String stringToYear(String strDest) throws ParseException {
		Date date = string2Date(strDest, PATTERN_DATE);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return String.valueOf(c.get(Calendar.YEAR));
	}

	public static String stringToMonth(String strDest) throws ParseException {
		Date date = string2Date(strDest, PATTERN_DATE);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		month = month + 1;
		if (month < 10) {
			return "0" + month;
		}
		return String.valueOf(month);
	}

	public static String stringToDay(String strDest) throws ParseException {
		Date date = string2Date(strDest, PATTERN_DATE);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			return "0" + day;
		}
		return "" + day;
	}

	public static Date getFirstDayOfMonth(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = 1;
		c.set(year, month, day, 0, 0, 0);
		return c.getTime();
	}

	public static Date getLastDayOfMonth(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = 1;
		if (month > 11) {
			month = 0;
			year = year + 1;
		}
		c.set(year, month, day - 1, 0, 0, 0);
		return c.getTime();
	}

	public static String date2GregorianCalendarString(Date date) throws DatatypeConfigurationException {
		long tmp = date.getTime();
		GregorianCalendar ca = new GregorianCalendar();
		ca.setTimeInMillis(tmp);

		XMLGregorianCalendar tXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(ca);
		return tXMLGregorianCalendar.normalize().toString();
	}

	public static boolean compareDate(Date firstDate, Date secondDate) {
		Assert.notNull(firstDate);
		Assert.notNull(secondDate);
		String strFirstDate = date2String(firstDate, PATTERN_DATE);
		String strSecondDate = date2String(secondDate, PATTERN_DATE);
		if (strFirstDate.equals(strSecondDate)) {
			return true;
		}
		return false;
	}

	public static Date getStartTimeOfDate(Date currentDate) throws ParseException {
		Assert.notNull(currentDate);
		String strDateTime = date2String(currentDate, PATTERN_DATE) + " 00:00:00";
		return string2Date(strDateTime, PATTERN_STANDARD);
	}

	public static Date getEndTimeOfDate(Date currentDate) throws ParseException {
		Assert.notNull(currentDate);
		String strDateTime = date2String(currentDate, PATTERN_DATE) + " 59:59:59";
		return string2Date(strDateTime, PATTERN_STANDARD);
	}
	
	public static String getStartTimeOfString(Date currentDate, String timeZone) throws ParseException {
		Assert.notNull(currentDate);
		String strDateTime = date2String(currentDate, PATTERN_DATE, timeZone) + " 00:00:00";
		return strDateTime;
	}

	public static String getEndTimeOfString(Date currentDate, String timeZone) throws ParseException {
		Assert.notNull(currentDate);
		String strDateTime = date2String(currentDate, PATTERN_DATE, timeZone) + " 59:59:59";
		return strDateTime;
	}

	public static long calDate(Date sDate, Date eDate, char dhms) {
		Calendar s = Calendar.getInstance();
		Calendar e = Calendar.getInstance();
		s.setTime(sDate);
		e.setTime(eDate);
		long result = e.getTimeInMillis() - s.getTimeInMillis();
		switch (dhms) {
		case 's':
			result = result / 1000;
			break;
		case 'm':
			result = result / (1000 * 60);
			break;
		case 'h':
			result = result / (1000 * 60 * 60);
			break;
		case 'd':
			result = result / (1000 * 60 * 60 * 24);
			break;
		default:
			result = -1;
		}
		return result;
	}

	public static Date addDate(Date srcDate, String type, int num) {
		Calendar result = Calendar.getInstance();
		result.setTime(srcDate);
		if ("day".equals(type)) {
			result.add(Calendar.DAY_OF_MONTH, num);
		} else if ("min".equals(type)) {
			result.add(Calendar.MINUTE, num);
		} else if ("hour".equals(type)) {
			result.add(Calendar.HOUR_OF_DAY, num);
		}
		return result.getTime();
	}

	public static Date getDayStart(Date srcDate) {
		Calendar result = Calendar.getInstance();
		result.setTime(srcDate);
		result.set(Calendar.HOUR_OF_DAY, 0);
		result.set(Calendar.MINUTE, 0);
		result.set(Calendar.SECOND, 0);
		return result.getTime();
	}

	public static Date getDayEnd(Date srcDate) {
		Calendar result = Calendar.getInstance();
		result.setTime(srcDate);
		result.set(Calendar.HOUR_OF_DAY, 23);
		result.set(Calendar.MINUTE, 59);
		result.set(Calendar.SECOND, 59);
		return result.getTime();
	}

	public static java.util.Date parse(String dateString, String dateFormat) {
		return parse(dateString, dateFormat, java.util.Date.class);
	}

	@SuppressWarnings("unchecked")
	public static <T extends java.util.Date> T parse(String dateString, String dateFormat, Class<T> targetResultType) {
		if (StringUtils.isEmpty(dateString))
			return null;
		DateFormat df = new SimpleDateFormat(dateFormat);
		try {
			long time = df.parse(dateString).getTime();
			java.util.Date t = targetResultType.getConstructor(long.class).newInstance(time);
			return (T) t;
		} catch (ParseException e) {
			String errorInfo = "cannot use dateformat:" + dateFormat + " parse datestring:" + dateString;
			throw new IllegalArgumentException(errorInfo, e);
		} catch (Exception e) {
			throw new IllegalArgumentException("error targetResultType:" + targetResultType.getName(), e);
		}
	}

	public static String format(java.util.Date date, String dateFormat) {
		if (date == null) {
			return null;
		}

		return new SimpleDateFormat(dateFormat).format(date);
	}
	
	public static Date getTimeZoneDate(java.util.Date localDate ,String timeZone){
	    if(StringUtils.isBlank(timeZone)){
	        return localDate;
	    }
	    try {
	        Long targetTime = localDate.getTime() - TimeZone.getDefault().getRawOffset() +  TimeZone.getTimeZone(timeZone).getRawOffset();
	        return new Date(targetTime);   
        } catch (Exception e) {
            return localDate;
        }
	}
	
	public static boolean isDateStr(String dateStr){
	    if (StringUtils.isBlank(dateStr)) {
	        return false;
	    }
	    try {
	        String[] parsePatterns = { "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd",
	                "yyyy/MM/dd HH:mm:ss.SSS","yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM/dd","yyyyMMdd"};
	        Date date = DateUtils.parseDate(dateStr, parsePatterns);
	        if(null != date){
	            return true;
	        }else {
                return false;
            }
	    } catch (ParseException e) {
	        return false;
	    }
	}
}
