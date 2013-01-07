package com.sleepkeeper.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DateTimeHelper 
{
	private static String TAG = "DateTimeHelper";
	private static String invalidDateFormat = "Invalid date format";
	
	public static String extractDateFromCalendarTime(Calendar time)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		return dateFormatter.format(time.getTime());
	}
	
	public static String extractTimeFromCalendarTime(Calendar time)
	{
		SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
			return timeFormatter.format(time.getTime());
	}
	
	public static Calendar convertStringToCalendar(String stringDate)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy"); 
		Date date = null;
		
		try
		{
			date = (Date)formatter.parse(stringDate); 
		}
		catch(java.text.ParseException e)
		{
			Log.w(TAG, invalidDateFormat);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date); 
		return calendar;
	}
	
	public static Date convertStringToDate(String date, String time)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		String dateString = date + " " + time;
		Date newDate = null;
		
		try
		{
			newDate = (Date)formatter.parse(dateString);  
		}
		catch (java.text.ParseException e) 
		{
			Log.w(TAG, invalidDateFormat);
		}
		
		return newDate;
	}
}
