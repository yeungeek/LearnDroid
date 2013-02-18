package com.anhuioss.crowdroid.util;

import java.util.*;

public class DayStyle {
	public final static int iColorText = 0xff78E1F0;
	public final static int iColorBkg = 0xffffffff;
	public final static int iColorBkgHoliday = 0xffe4f0ff;
	public final static int iColorBkgHolidaySUNDAY = 0xffE1F0F0;

	public final static int iColorTextToday = 0xffffffff;
	public final static int iColorBkgToday = 0xffdf5a77;

	public final static int iColorTextSelected = 0xff001122;
	public final static int iColorBkgSelectedLight = 0xffbbddff;
	public final static int iColorBkgSelectedDark = 0xffD2F0F0;

	public final static int iColorTextFocused = 0xff221100;
	public final static int iColorBkgFocusLight = 0xffffddbb;
	public final static int iColorBkgFocusDark = 0xffaa5500;

	public final static int iColorTextBkgSchedule = 0xffffffff;
	public final static int iColorRectBkgSchedule = 0xff6987B4;

	public static int getColorText(boolean bToday) {
		if (bToday)
			return iColorTextToday;
		return iColorText;
	}

	public static int getColorBkg(boolean bHoliday, boolean bHolidaySUNDAY,
			boolean bToday) {
		if (bToday)
			return iColorBkgToday;
		if (bHoliday)
			return iColorBkgHoliday;
		if (bHolidaySUNDAY)
			return iColorBkgHolidaySUNDAY;
		return iColorBkg;
	}

	public static int getWeekDay(int index, int iFirstDayOfWeek) {
		int iWeekDay = -1;

		if (iFirstDayOfWeek == Calendar.SUNDAY) {
			iWeekDay = index + Calendar.SUNDAY;
			if (iWeekDay > Calendar.FRIDAY)
				iWeekDay = Calendar.SATURDAY;
		}

		if (iFirstDayOfWeek == Calendar.SATURDAY) {
			iWeekDay = index + Calendar.SATURDAY;
		}

		return iWeekDay;
	}

}
