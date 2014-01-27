package cn.hjmao.specialfriend.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Date {
	private static String dateFmtStr = "yyyy-MM-dd HH:mm:ss";
	public static SimpleDateFormat dateFmt = new SimpleDateFormat(dateFmtStr);
	
	private static String dayHourMinFmtStr = "MM-dd HH:mm";
	public static SimpleDateFormat dayHourMinFmt = new SimpleDateFormat(dayHourMinFmtStr);
	
	public static long now() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}

	public static String mills2str(long mills) {
		String str = Date.dateFmt.format(new java.util.Date(mills));
		return str;
	}

	public static String mills2dayHourMin(long mills) {
		String str = Date.dayHourMinFmt.format(new java.util.Date(mills));
		return str;
	}
}
