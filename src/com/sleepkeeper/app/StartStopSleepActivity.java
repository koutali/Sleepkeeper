package com.sleepkeeper.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sleepkeeper.util.NetworkStateManager;
import com.sleepkeeper.util.SleepRecord;
import com.sleepkeeper.util.SleepRecordDbAdapter;
import com.sleepkeeper.util.SoundAndVibrationManager;

public class StartStopSleepActivity extends Activity 
{
	private SleepRecord recordToInsert = new SleepRecord();
	
	private static SleepRecordDbAdapter dbAdapter = null;
	private SharedPreferences preferences = null;
	private boolean isAutoFlightModeOn = false;
	private boolean isWifiAutoOffOn = false;
	private boolean sleepModeOn = true;
	
	private String soundPreference = null;
	
	private NetworkStateManager networkStateManager = null;
	private SoundAndVibrationManager soundAndVibrationManager = null;
	
	private TextView alarmText = null;
	private Button sleepButton = null;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_stop_sleep_activity_layout); 
        alarmText = (TextView) findViewById(R.id.alarm_text);
    	sleepButton = (Button) findViewById(R.id.sleep_button);
    	
        final StartStopSleepActivity restoredInstance = (StartStopSleepActivity)getLastNonConfigurationInstance();
        if(restoredInstance == null)
        {       	
            networkStateManager = new NetworkStateManager(this);
        	soundAndVibrationManager = new SoundAndVibrationManager(this);
        }
        else
        {
        	restoreActivityData(restoredInstance);
        }
        
        dbAdapter = SleepRecordDbAdapter.getInstance(this);
    }
    
    /* Everything that is put in this function is meant to be refreshed
     * when the activity is brought to foreground.
     */
    protected void onResume()
    {
    	super.onResume();
    	String nextAlarm = Settings.System.getString(getContentResolver(), 
				 									 Settings.System.NEXT_ALARM_FORMATTED);
    
    	if(alarmText != null)
    	{
			if(nextAlarm.isEmpty())
				alarmText.setText(getString(R.string.alarm_not_set));
			else
				alarmText.setText(getString(R.string.alarm_set) + ": " + nextAlarm);
    	}
    }
        
    public Object onRetainNonConfigurationInstance()
    {
        return this;
    }
    
    /* onClick function is declared in the layout XML file */
    public void onClickSleepButton(View v)
    {
    	if(sleepModeOn)
    	{
    		sleepButton.setText(R.string.waking_up_button);
    		recordToInsert.createSleepDate();
    		
    		readSharedPreferences();
    		
    		changeSystemConfiguration();
    		sleepModeOn = false;
    	}
    	else
    	{
    		sleepButton.setText(R.string.sleep_button);
    		recordToInsert.createWakeUpDate();
    		
    		dbAdapter.createSleepRecord(recordToInsert.getSleepDate(), 
    									recordToInsert.getSleepTime(), 
    									recordToInsert.getWakeUpTime(), 
    									recordToInsert.getSleepLength());
    		
    		restoreSystemConfiguration();
    		sleepModeOn = true;
    	}
    }
    
    private void readSharedPreferences()
    {
    	// Get the xml/preferences.xml preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try
        {
	        isAutoFlightModeOn = preferences.getBoolean("autoFlightModeOn", false);
	        isWifiAutoOffOn = preferences.getBoolean("wifiAutoOff", false);
	        soundPreference = preferences.getString("autoSilentMode", "");
        }
        catch(ClassCastException e)
        {
			Toast.makeText(getApplicationContext(), 
						   R.string.error_reading_shared_preferences, 
						   Toast.LENGTH_SHORT).show();
        }
    }
    
    private void changeSystemConfiguration()
    {
    	if(isWifiAutoOffOn)
    		networkStateManager.turnWifiOff();
    	
    	if(isAutoFlightModeOn)
    		networkStateManager.turnOnFlightMode();
    	
    	soundAndVibrationManager.changeRingingMode(soundPreference);
    }
    
    private void restoreSystemConfiguration()
    {
    	if(isWifiAutoOffOn)
    		networkStateManager.turnWifiOn();
    	
    	if(isAutoFlightModeOn)
    		networkStateManager.turnOffFlightMode();
    	
    	soundAndVibrationManager.restoreRingingMode();
    }
    
    private void restoreActivityData(StartStopSleepActivity restoredInstance)
    {
    	recordToInsert = restoredInstance.getRecordToInsert();
    	preferences = restoredInstance.getPreferences();
    	isAutoFlightModeOn = restoredInstance.IsAutoFlightModeOn();
    	isWifiAutoOffOn = restoredInstance.IsWifiAutoOffOn();
    	sleepModeOn = restoredInstance.IsSleepModeOn();
    	
    	soundPreference = restoredInstance.getSoundPreference();
    	
    	networkStateManager = restoredInstance.getNetworkStateManager();
    	soundAndVibrationManager = restoredInstance.getSoundAndVibrationManager();
    	
    	/* Button is clicked once. We need to restore the button text */
    	if(!sleepModeOn)
    		sleepButton.setText(R.string.waking_up_button);
    }
    
	public SleepRecord getRecordToInsert()
	{
		return recordToInsert;
	}
	
	public SharedPreferences getPreferences()
	{
		return preferences;
	}
	
	public boolean IsAutoFlightModeOn()
	{
		return isAutoFlightModeOn;
	}
	
	public boolean IsWifiAutoOffOn()
	{
		return isWifiAutoOffOn;
	}
	
	public boolean IsSleepModeOn()
	{
		return sleepModeOn;
	}
	
	public String getSoundPreference()
	{
		return soundPreference;
	}
	
	public NetworkStateManager getNetworkStateManager()
	{
		return networkStateManager;
	}
	
	public SoundAndVibrationManager getSoundAndVibrationManager()
	{
		return soundAndVibrationManager;
	}
}