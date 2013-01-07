package com.sleepkeeper.util;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class SleepRecord 
{
	private Calendar sleepDate = Calendar.getInstance();
	private Calendar wakeUpDate = Calendar.getInstance();
	private final long HOUR_TO_MS = 60L * 60L * 1000L;
	
	public SleepRecord()
	{}
	
	public SleepRecord(String sleepDateString, String sleepTimeString, String wakeUpDateString, String wakeUpTimeString)
	{
		sleepDate.setTime(DateTimeHelper.convertStringToDate(sleepDateString, sleepTimeString));		
		wakeUpDate.setTime(DateTimeHelper.convertStringToDate(wakeUpDateString, wakeUpTimeString));
	}
	
	public void createSleepDate()
	{
		sleepDate = Calendar.getInstance();
	}
	
	public void createWakeUpDate()
	{
		wakeUpDate = Calendar.getInstance();
	}
	
	public boolean setWakeUpDate(int year, int month, int day)
	{
		Calendar tempWakeUpDate = (Calendar)wakeUpDate.clone();
		tempWakeUpDate.set(Calendar.YEAR, year);
		tempWakeUpDate.set(Calendar.MONTH, month);
		tempWakeUpDate.set(Calendar.DAY_OF_MONTH, day);
		
		if(tempWakeUpDate.before(sleepDate))
			return false;
		else
		{
			wakeUpDate.set(Calendar.YEAR, year);
			wakeUpDate.set(Calendar.MONTH, month);
			wakeUpDate.set(Calendar.DAY_OF_MONTH, day);
			return true;
		}
	}
	
	public boolean setWakeUpTime(int hour, int minute)
	{
		Calendar tempWakeUpDate = (Calendar)wakeUpDate.clone();
		tempWakeUpDate.set(Calendar.HOUR, hour);
		tempWakeUpDate.set(Calendar.MINUTE, minute);
		
		if(tempWakeUpDate.before(sleepDate))
			return false;
		else
		{
			wakeUpDate.set(Calendar.HOUR, hour);
			wakeUpDate.set(Calendar.MINUTE, minute);
			
			return true;
		}
	}
	
	public boolean setSleepDate(int year, int month, int day)
	{
		Calendar tempSleepDate = (Calendar) sleepDate.clone();
		tempSleepDate.set(Calendar.YEAR, year);
		tempSleepDate.set(Calendar.MONTH, month);
		tempSleepDate.set(Calendar.DAY_OF_MONTH, day);
		
		if(wakeUpDate.before(tempSleepDate))
			return false;
		else
		{
			sleepDate.set(Calendar.YEAR, year);
			sleepDate.set(Calendar.MONTH, month);
			sleepDate.set(Calendar.DAY_OF_MONTH, day);
			
			return true;
		}
	}
	
	public boolean setSleepTime(int hour, int minute)
	{
		Calendar tempSleepDate = (Calendar) sleepDate.clone();
		tempSleepDate.set(Calendar.HOUR, hour);
		tempSleepDate.set(Calendar.MINUTE, minute);
		
		if(wakeUpDate.before(tempSleepDate))
			return false;
		else
		{
			sleepDate.set(Calendar.HOUR, hour);
			sleepDate.set(Calendar.MINUTE, minute);
			
			return true;
		}
	}
	
	public String getSleepDate()
	{
		return DateTimeHelper.extractDateFromCalendarTime(sleepDate);
	}
	
	public String getSleepTime()
	{
		return DateTimeHelper.extractTimeFromCalendarTime(sleepDate);
	}
	
	public String getWakeUpDate()
	{
		return DateTimeHelper.extractDateFromCalendarTime(wakeUpDate);
	}
	
	public String getWakeUpTime()
	{
		return DateTimeHelper.extractTimeFromCalendarTime(wakeUpDate);
	}
	
	public int getSleepRecordHour()
	{
		return sleepDate.get(Calendar.HOUR);
	}
	
	public int getSleepRecordMinute()
	{
		return sleepDate.get(Calendar.MINUTE);
	}
	
	public int getSleepRecordYear()
	{
		return sleepDate.get(Calendar.YEAR);
	}
	
	public int getSleepRecordMonth()
	{
		return sleepDate.get(Calendar.MONTH);
	}
	
	public int getWakeUpRecordDay()
	{
		return wakeUpDate.get(Calendar.DAY_OF_MONTH);
	}
	
	public int getWakeUpRecordHour()
	{
		return wakeUpDate.get(Calendar.HOUR);
	}
	
	public int getWakeUpRecordMinute()
	{
		return wakeUpDate.get(Calendar.MINUTE);
	}
	
	public int getWakeUpRecordYear()
	{
		return wakeUpDate.get(Calendar.YEAR);
	}
	
	public int getWakeUpRecordMonth()
	{
		return wakeUpDate.get(Calendar.MONTH);
	}
	
	public int getSleepRecordDay()
	{
		return sleepDate.get(Calendar.DAY_OF_MONTH);
	}
	
	public String getSleepLength()
	{
		Date tempSleepDate = sleepDate.getTime();
		Date tempWakeUpDate = wakeUpDate.getTime();
		long dateDifferenceInMilliseconds = tempWakeUpDate.getTime() - tempSleepDate.getTime();
		
		float dateDifferenceInHours = (float) dateDifferenceInMilliseconds / HOUR_TO_MS;
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
				
		return df.format(dateDifferenceInHours) ;
	}
}
