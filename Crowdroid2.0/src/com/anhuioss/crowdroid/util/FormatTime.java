package com.anhuioss.crowdroid.util;

public class FormatTime {
	String replaceTime = "";

	String year = "";

	String month = "";

	String week = "";

	String day = "";

	String hms = "";

	String newTime = "";

	public String formatTime(String time) {
		replaceTime = String.valueOf(time.replace(" ", "").replace(":", "")
				.replace("+0800", ""));
		// FriJan181100422013

		year = replaceTime.substring(14, 18);
		month = formatMonth(replaceTime.substring(3, 6));
		week = formatWeek(replaceTime.substring(0, 3));
		day = replaceTime.substring(6, 8);
		hms = replaceTime.substring(8, 14);
		newTime = year + month + day + hms;
		return newTime;
	}

	public String formatMonth(String month) {
		String formatMonth = "";
		if (month.equals("Jan")) {
			formatMonth = "01";
		} else if (month.equals("Feb")) {
			formatMonth = "02";
		} else if (month.equals("Mar")) {
			formatMonth = "03";
		} else if (month.equals("Apr")) {
			formatMonth = "04";
		} else if (month.equals("may")) {
			formatMonth = "05";
		} else if (month.equals("Jun")) {
			formatMonth = "06";
		} else if (month.equals("Jul")) {
			formatMonth = "07";
		} else if (month.equals("Aug")) {
			formatMonth = "08";
		} else if (month.equals("Sept")) {
			formatMonth = "09";
		} else if (month.equals("Oct")) {
			formatMonth = "10";
		} else if (month.equals("Nov")) {
			formatMonth = "11";
		} else if (month.equals("Dec")) {
			formatMonth = "12";
		}

		return formatMonth;
	}

	public String formatWeek(String week) {
		String formatWeek = "";
		if (week.equals("Mon")) {
			formatWeek = "1";
		} else if (week.equals("Tue")) {
			formatWeek = "2";
		} else if (week.equals("Wed")) {
			formatWeek = "3";
		} else if (week.equals("Thu")) {
			formatWeek = "4";
		} else if (week.equals("Fri")) {
			formatWeek = "5";
		} else if (week.equals("Sat")) {
			formatWeek = "6";
		} else if (week.equals("Sun")) {
			formatWeek = "7";
		}
		return formatWeek;

	}

}
