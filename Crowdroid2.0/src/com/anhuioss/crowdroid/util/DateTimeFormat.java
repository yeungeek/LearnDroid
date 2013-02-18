package com.anhuioss.crowdroid.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeFormat {

	public enum FormatType {
		DATE, DATE_TIME
	}

	/**
	 * 格式化数字
	 * 
	 * @param number
	 *            需要格式化的数字
	 * @return 当数字是一位时，返回0X的格式；当数字是两位时，直接返回！
	 */
	public static String format(int number) {

		if (number >= 0 && number < 10) {
			return "0" + number;
		}
		return String.valueOf(number);

	}

	/**
	 * 格式化数字
	 * 
	 * @param number
	 *            需要格式化的数字的字符串
	 * @return 当数字是一位时，返回0X的格式；当数字是两位时，直接返回！
	 */
	public static String format(String number) {

		if (number.length() == 1) {
			return "0" + number;
		}
		return String.valueOf(number);

	}

	public static String getCurrentDateTime(FormatType type) {

		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat sdf;
		switch (type) {
		case DATE: {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(date);
		}
		case DATE_TIME: {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(date);
		}
		default: {
			return null;
		}
		}

	}

	public static int getLastDayOfMonth(int year, int month) {

		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12: {
			return 31;
		}
		case 4:
		case 6:
		case 9:
		case 11: {
			return 30;
		}
		case 2: {
			return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0) ? 29
					: 28;
		}
		}
		return 0;

	}

	/**
	 * 获得两个日期之间的相差的天数，日期的格式为：yyyy-MM-dd
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 日期间差的天数
	 */
	public static int getDifferenceOfDate(String startDate, String endDate) {

		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date0 = dateFormat.parse(startDate);
			Date date1 = dateFormat.parse(endDate);
			return (int) ((date0.getTime() - date1.getTime()) / 24 / 60 / 60 / 1000);
		} catch (ParseException e) {
			e.printStackTrace();
			return Integer.MAX_VALUE;
		}

	}

}
