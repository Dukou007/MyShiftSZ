package com.pax.tms.user.security;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UTCTime {

	public static Calendar UTC_CLENDAR = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));

	/**
	 * @param utcTime
	 * @param timezone
	 *            Asia/Shanghai (UTC+08:00) US/Pacific (UTC-08:00)
	 * @param useDaylight
	 * @return
	 */
	public static Date utc2timezone(Date utcTime, String timezone, boolean useDaylight) {
		// String timeZoneID = timeZone.substring(1,
		// timeZone.indexOf(")")).replace("UTC", "GMT");
		TimeZone tz = TimeZone.getTimeZone(timezone);
		int timeOffset = tz.getRawOffset();
		// 开启夏令时并且在夏令时区间内
		boolean isDaylight = useDaylight && isDaylightForUSAorCA(new Date(utcTime.getTime() + timeOffset));

		int dstSavings = isDaylight ? tz.getDSTSavings() : 0;
		long offset = timeOffset + dstSavings;
		Date newDate = new Date(utcTime.getTime() + offset);
		return newDate;
	}

	/**
	 * @param tzDate
	 * @param timezone
	 *            Asia/Shanghai (UTC+08:00) US/Pacific (UTC-08:00)
	 * @param useDaylight
	 * @return
	 */
	public static Date timezone2utc(Date tzDate, String timezone, boolean useDaylight) {
		// 开启夏令时并且在夏令时区间内
		boolean isDaylight = useDaylight && isDaylightForUSAorCA(tzDate);
		// String timeZoneID = timeZone.substring(1,
		// timeZone.indexOf(")")).replace("UTC", "GMT");
		TimeZone tz = TimeZone.getTimeZone(timezone);
		int timeOffset = tz.getRawOffset();
		int dstSavings = isDaylight ? tz.getDSTSavings() : 0;
		long offset = timeOffset + dstSavings;
		Date newDate = new Date(tzDate.getTime() - offset);
		return newDate;
	}

	public static class TimeRange {

		private Date from;
		private Date to;

		public TimeRange() {
		}

		public TimeRange(Date from, Date to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public String toString() {
			return "from: " + from + "\n  to: " + to;
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
	}

	// mtd
	public static TimeRange monthToDay(Date utcTime, String timezone, boolean useDaylight) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(utcTime);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new TimeRange(cal.getTime(), utcTime);
	}

	// lm
	/**
	 * Starts on the first day of the month before the current day and continues
	 * for all the days of that month.
	 * 
	 * @return
	 */
	public static TimeRange lastMonth() {
		return lastNMonths(1);
	}

	public static TimeRange lastNMonths(int n) {
		TimeRange tr = new TimeRange();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		cal.add(Calendar.MONTH, -n);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setFrom(cal.getTime());

		cal.setTime(new Date());
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		tr.setTo(cal.getTime());

		return tr;
	}

	/**
	 * @return DateTimeRange from 00:00:00 to 23:59:59
	 */
	public static TimeRange getYesterday() {
		TimeRange tr = new TimeRange();

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setFrom(cal.getTime());

		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		tr.setTo(cal.getTime());

		return tr;
	}

	/**
	 * @return from Sunday to Saturday
	 */
	public static TimeRange getLastWeek() {
		TimeRange tr = new TimeRange();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

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
		cal.set(Calendar.MILLISECOND, 999);
		tr.setTo(cal.getTime());

		return tr;
	}

	public static TimeRange lastNSeconds(int n) {
		TimeRange tr = new TimeRange();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		tr.setTo(cal.getTime());

		cal.add(Calendar.SECOND, -n);
		tr.setFrom(cal.getTime());

		return tr;
	}

	public static String date2String(java.util.Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * @param date
	 *            经过时区转换的时间
	 * @return
	 */
	public static boolean isDaylightForUSAorCA(Date date) {
		Date[] daylight = getDaylightForUSAorCA();
		if (date.getTime() >= daylight[0].getTime() && date.getTime() <= daylight[1].getTime()) {
			return true;
		}
		return false;
	}

	/**
	 * 三月第二个周日 2点 ~ 十一月第一个周日3点
	 * 
	 * @return
	 */
	public static Date[] getDaylightForUSAorCA() {
		Date[] result = new Date[2];
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.MONTH, Calendar.MARCH);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) { // 第一天是否为周日
			cal.set(Calendar.WEEK_OF_MONTH, 2);
		} else {
			cal.set(Calendar.WEEK_OF_MONTH, 3);
		}
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		result[0] = cal.getTime();

		cal.set(Calendar.MONTH, Calendar.NOVEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			cal.set(Calendar.WEEK_OF_MONTH, 1);
		} else {
			cal.set(Calendar.WEEK_OF_MONTH, 2);
		}
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 3);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		result[1] = cal.getTime();

		return result;
	}

	public static TimeRange getLastNDayForUTC(int n) {
		Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
		TimeRange tr = new TimeRange();

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
		cal.set(Calendar.YEAR, utcCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, utcCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH) - n);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setFrom(cal.getTime());
		tr.setTo(utcCal.getTime());

		return tr;
	}

	public static TimeRange getLastDayForUTC() {
		TimeRange tr = new TimeRange();

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));

		cal.set(Calendar.YEAR, UTC_CLENDAR.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, UTC_CLENDAR.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, UTC_CLENDAR.get(Calendar.DAY_OF_MONTH) - 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		tr.setFrom(cal.getTime());

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);

		tr.setTo(cal.getTime());

		return tr;
	}

	public static TimeRange getLastWeekForUTC() {
		Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
		TimeRange tr = new TimeRange();

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));

		cal.set(Calendar.YEAR, utcCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, utcCal.get(Calendar.MONTH));
		cal.set(Calendar.WEEK_OF_YEAR, utcCal.get(Calendar.WEEK_OF_YEAR) - 1);

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

	public static TimeRange getLastMonthForUTC() {
		Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
		TimeRange tr = new TimeRange();

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));

		cal.set(Calendar.YEAR, utcCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, utcCal.get(Calendar.MONTH) - 1);

		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setFrom(cal.getTime());

		cal.set(Calendar.MONTH, utcCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		tr.setTo(cal.getTime());

		return tr;
	}

	public static int getDayForWeek(Date date, String tz, boolean useDaylight) {
		TimeZone timezone = TimeZone.getTimeZone(tz);
		Calendar cal = Calendar.getInstance(timezone);
		cal.setTime(date);
		if (timezone.inDaylightTime(date) && !useDaylight) {
			cal.add(Calendar.HOUR_OF_DAY, -1);
		}
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDayForMonth(Date date, String tz, boolean useDaylight) {
		TimeZone timezone = TimeZone.getTimeZone(tz);
		Calendar cal = Calendar.getInstance(timezone);
		cal.setTime(date);
		if (timezone.inDaylightTime(date) && !useDaylight) {
			cal.add(Calendar.HOUR_OF_DAY, -1);
		}
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static Date getLastNHours(int n) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
//		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.add(Calendar.HOUR, -n);

		return cal.getTime();
	}
	
}
