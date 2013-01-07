package com.sleepkeeper.app;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class SettingsMenuActivity extends PreferenceActivity 
{
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference setAlarmPreference = (Preference) findPreference("setAlarm");
        setAlarmPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() 
        {
			public boolean onPreferenceClick(Preference preference) 
			{
				startAndroidAlarmClock();
				return true;
			}
        });
        
        Preference loginToRK = (Preference) findPreference("loginToRK");
        loginToRK.setOnPreferenceClickListener(new OnPreferenceClickListener() 
        {
			public boolean onPreferenceClick(Preference preference) 
			{
				startRunkeeperLoginActivity();
				return true;
			}
        });
    }
    
    private void startRunkeeperLoginActivity()
    {
    	Intent intent = new Intent(this, LoginToRunkeeperActivity.class);
		startActivity(intent);
    }
    
    /* Code excerpt from
     * http://stackoverflow.com/questions/3590955/intent-to-launch-the-clock-application-on-android
     * 
     * Different alarm applications may exist on different Android systems.
     * There is currently no way of launching an implicit intent to
     * redirect an application to an alarm application either because no common intent is
     * defined for doing this job.
     * 
     * Because of the reasons stated above, the solution is to try redirecting the Sleepkeeper
     * application to an alarm application using the code below. If redirection fails,
     * we'll ask the user to manually set the alarm. 
     */
    private void startAndroidAlarmClock()
    {
    	Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);

    	// Verify clock implementation
    	String defaultClockImplementations[][] = 
    	{
    	        {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
    	        {"Standar Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
    	        {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
    	        {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
    	        {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"}
    	};

    	boolean foundClockImplementation = false;
    	Context context = getApplicationContext();

    	for(int i=0; i < defaultClockImplementations.length && !foundClockImplementation; i++) 
    	{
    	    String vendor = defaultClockImplementations[i][0];
    	    String packageName = defaultClockImplementations[i][1];
    	    String className = defaultClockImplementations[i][2];
    	    
    	    try 
    	    {
    	    	PackageManager packageManager = context.getPackageManager();
    	    	
    	        ComponentName componentName = new ComponentName(packageName, className);
    	        
    	        ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, 
    	        														   PackageManager.GET_META_DATA);
    	        
    	        alarmClockIntent.setComponent(componentName);
    	        foundClockImplementation = true;
    	    } 
    	    catch (NameNotFoundException e) 
    	    {
    	    	/* We'll issue a message if we can't find any matching application;
    	    	 * thus no action here.
    	    	 */
    	    }
    	}

    	if(foundClockImplementation)
    	{
    	    PendingIntent pendingIntent = PendingIntent.getActivity(context,
    	    														0, 
    	    														alarmClockIntent, 
    	    														0);
    	    try 
    	    {
				pendingIntent.send();
			} 
    	    catch (CanceledException e) 
    	    {
    	       	 Toast.makeText(context, 
        			 		R.string.alarm_app_could_not_be_started, 
        			 		Toast.LENGTH_SHORT).show();
			}
    	}
    	else
       	 Toast.makeText(context, 
       			 		R.string.no_alarm_app_found, 
       			 		Toast.LENGTH_SHORT).show();
    }
}