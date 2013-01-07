package com.sleepkeeper.app;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sleepkeeper.util.DateTimeHelper;
import com.sleepkeeper.util.SleepRecord;
import com.sleepkeeper.util.SleepRecordDbAdapter;

/* This activity talks to SleepGraphActivity.
 * 
 * SleepGraphActivity redirects actions to EditSleepDataActivity in two cases:
 * 
 * 1- When the user actually to edit an already created record
 * 2- When the user wants to add a new record manually
 * 
 * In the first case we retrieve data from SQLite DB (done in SleepGraphAcitivity),
 * send it to EditSleepDataActivity and update all buttons and texts on the 
 * EditSleepDataActivity layout accordingly. 
 * 
 * In the latter case we create a temporary SleepRecord object with the current
 * time stamps for sleep date and wake up date. Again this is done in SleepGraphActivity 
 * class. After that EditSleepDataActivity is triggered. This way, button and text fields are
 * initialized with current date values.
 * 
 * Once the user pressed Save on EditSleepDataActivity layout, we send the changed date and time
 * values to SleepGraphActivity. This class is responsible for storing them in the SQLite DB.
 */
public class EditSleepDataActivity extends Activity 
{
    private Button sleepTimeButton = null;
    private Button wakeUpTimeButton = null;
   
    private Button sleepDateButton = null;
    private Button wakeUpDateButton = null;
    
    private Button saveButton = null;
    private Button cancelButton = null;
    
    private TextView sleepDurationText = null;
    
    static final int SLEEP_TIME_DIALOG_ID = 0;
    static final int WAKE_UP_TIME_DIALOG_ID = 1;
    static final int SLEEP_DATE_DIALOG_ID = 2;
    static final int WAKE_UP_DATE_DIALOG_ID = 3; 
    
    private final String recordDateKey = "sleepRecordDateKey";
    private final String sleepTimeKey = "sleepTimeKey";
    private final String wakeUpTimeKey = "wakeUpTimeKey";
    private final String sleepLengthKey = "sleepLengthKey";
    private final String rowIdKey = "rowIdKey";
    
    private Long rowId;
    private SleepRecord sleepRecord;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_sleep_data_layout);
        
        sleepTimeButton = (Button) findViewById(R.id.sleep_time_button);
        sleepTimeButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
                showDialog(SLEEP_TIME_DIALOG_ID);
            }
        });
        
        wakeUpTimeButton = (Button) findViewById(R.id.wake_time_button);
        wakeUpTimeButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
                showDialog(WAKE_UP_TIME_DIALOG_ID);
            }
        });

        sleepDateButton = (Button) findViewById(R.id.sleep_date_button);
        sleepDateButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
                showDialog(SLEEP_DATE_DIALOG_ID);
            }
        });
        
        wakeUpDateButton = (Button) findViewById(R.id.wake_date_button);
        wakeUpDateButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
                showDialog(WAKE_UP_DATE_DIALOG_ID);
            }
        });
        
        saveButton = (Button) findViewById(R.id.save_button_id);
        saveButton.setOnClickListener(new View.OnClickListener() 
        {	
			public void onClick(View v) 
			{
				updateButtonTexts(sleepRecord.getSleepDate(),
								  sleepRecord.getSleepTime(),
								  sleepRecord.getWakeUpTime(),
								  sleepRecord.getSleepLength(),
								  sleepRecord.getWakeUpDate());
				
				sendActivityResult();
			}
		});
        
        cancelButton = (Button) findViewById(R.id.cancel_button_id);
        cancelButton.setOnClickListener(new View.OnClickListener() 
        {			
			public void onClick(View v) 
			{
				sendActivityResult();
			}
		});
        
        sleepDurationText = (TextView)findViewById(R.id.sleep_duration);
        
        createOrRestoreActivityData(savedInstanceState);
     }
    
    /* onSaveInstanceState and onSaveInstanceState needed so that we don't 
     * lose data when changing device orientation
     */
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		if(sleepRecord != null)
		{
			savedInstanceState.putString(recordDateKey, sleepRecord.getSleepDate());
			savedInstanceState.putString(sleepTimeKey, sleepRecord.getSleepTime());
			savedInstanceState.putString(wakeUpTimeKey, sleepRecord.getWakeUpTime());
			savedInstanceState.putString(sleepLengthKey, sleepRecord.getSleepLength());
			savedInstanceState.putLong(rowIdKey, rowId);
		}
		
		super.onSaveInstanceState(savedInstanceState);
	}
	
    private TimePickerDialog.OnTimeSetListener sleepTimeListener =  new TimePickerDialog.OnTimeSetListener() 
    {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		{
		    if(sleepRecord.setSleepTime(hourOfDay, minute))
		    	updateButtonText(sleepTimeButton, sleepRecord.getSleepTime());
		    else
				Toast.makeText(getApplicationContext(), 
							   R.string.error_setting_date, 
							   Toast.LENGTH_SHORT).show();
		}
    };
    
    private TimePickerDialog.OnTimeSetListener wakeUpTimeListener =  new TimePickerDialog.OnTimeSetListener() 
    {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		{
			if(sleepRecord.setWakeUpTime(hourOfDay, minute))
				updateButtonText(wakeUpTimeButton, sleepRecord.getWakeUpTime());
			else
				Toast.makeText(getApplicationContext(), 
							   R.string.error_setting_date, 
							   Toast.LENGTH_SHORT).show();
		}
    };
    
    private DatePickerDialog.OnDateSetListener sleepDateSetListener = new DatePickerDialog.OnDateSetListener() 
    {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		{
		    if(sleepRecord.setSleepDate(year, monthOfYear, dayOfMonth))
		    	updateButtonText(sleepDateButton, sleepRecord.getSleepDate());
		    else
				Toast.makeText(getApplicationContext(), 
							   R.string.error_setting_date, 
							   Toast.LENGTH_SHORT).show();
		}
    };
    
    private DatePickerDialog.OnDateSetListener wakeUpDateSetListener = new DatePickerDialog.OnDateSetListener() 
    {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		{
		    if(sleepRecord.setWakeUpDate(year, monthOfYear, dayOfMonth))
		    	updateButtonText(wakeUpDateButton, sleepRecord.getWakeUpDate());
		    else
				Toast.makeText(getApplicationContext(), 
							   R.string.error_setting_date, 
							   Toast.LENGTH_SHORT).show();
		}
    };
    
    protected Dialog onCreateDialog(int id) 
    {
        switch (id) 
        {
        	case SLEEP_TIME_DIALOG_ID:
        		return new TimePickerDialog(this, 
        									sleepTimeListener, 
        									sleepRecord.getSleepRecordHour(), 
        									sleepRecord.getSleepRecordMinute(), 
        									false);
        	case WAKE_UP_TIME_DIALOG_ID:
        		return new TimePickerDialog(this, 
        									wakeUpTimeListener, 
        									sleepRecord.getWakeUpRecordHour(), 
        									sleepRecord.getWakeUpRecordMinute(),  
        									false);
        	case SLEEP_DATE_DIALOG_ID:
        		return new DatePickerDialog(this,
                        					sleepDateSetListener,
                        					sleepRecord.getSleepRecordYear(), 
                        					sleepRecord.getSleepRecordMonth(), 
                        					sleepRecord.getSleepRecordDay());
        		
        	case WAKE_UP_DATE_DIALOG_ID:
        		return new DatePickerDialog(this,
                        					wakeUpDateSetListener,
                        					sleepRecord.getWakeUpRecordYear(), 
                        					sleepRecord.getWakeUpRecordMonth(), 
                        					sleepRecord.getWakeUpRecordDay());
        }
        return null;
    }
    
    private void updateButtonText(Button buttonToUpdate, String newText)
    {
    	buttonToUpdate.setText(newText);
    }
    
    private void updateButtonTexts(String sleepDate, 
    								  String sleepTime, 
    								  String wakeUpTime, 
    								  String sleepLength, 
    								  String wakeUpDateString)
    {
        if(sleepDate != null)
    	    updateButtonText(sleepDateButton, sleepDate);
        
        if(sleepTime != null)
    	    updateButtonText(sleepTimeButton, sleepTime);
        
        if (wakeUpTime != null)
            updateButtonText(wakeUpTimeButton, wakeUpTime);

        if (sleepLength != null)
        {
        	String toDisplay = sleepLength + " " + getString(R.string.hours);
        	sleepDurationText.setText(toDisplay);
        }
        
        updateButtonText(wakeUpDateButton, wakeUpDateString);
    }
    
    private void createOrRestoreActivityData(Bundle savedInstanceState)
    {
    	String sleepDate = null;
        String sleepTime = null;
        String wakeUpTime = null;
        String sleepLength = null;
        String wakeUpDateString = null;
        
        if(savedInstanceState != null)
        {
        	/* This block is meant to be executed when the activity is recreated */
        	
            sleepDate = savedInstanceState.getString(recordDateKey);
            sleepTime = savedInstanceState.getString(sleepTimeKey);
            wakeUpTime = savedInstanceState.getString(wakeUpTimeKey);
            sleepLength = savedInstanceState.getString(sleepLengthKey);
            rowId = savedInstanceState.getLong(rowIdKey);
        }
        else
        {
        	Bundle extras = getIntent().getExtras();
            if (extras != null) 
            {
            	/* This block is meant to be executed when the activity is triggered 
            	 * from outside. (Expected work flow.)
            	  */
            	
            	rowId = extras.getLong(SleepRecordDbAdapter.KEY_ROWID);

                sleepDate = extras.getString(SleepRecordDbAdapter.KEY_DATE);
                sleepTime = extras.getString(SleepRecordDbAdapter.KEY_SLEEP_TIME);
                wakeUpTime = extras.getString(SleepRecordDbAdapter.KEY_WAKE_TIME);
                sleepLength = extras.getString(SleepRecordDbAdapter.KEY_SLEEP_LENGTH);
            }
        }
        
        Calendar wakeUpDate = getWakeUpDate(sleepDate, sleepLength);
        wakeUpDateString = DateTimeHelper.extractDateFromCalendarTime(wakeUpDate);
        sleepRecord = new SleepRecord(sleepDate, sleepTime, wakeUpDateString, wakeUpTime);
        updateButtonTexts(sleepDate, sleepTime, wakeUpTime, sleepLength, wakeUpDateString);
    }
    
    private void sendActivityResult()
    {
    	Bundle bundle = new Bundle();

	    bundle.putString(SleepRecordDbAdapter.KEY_DATE, sleepRecord.getSleepDate());
	    bundle.putString(SleepRecordDbAdapter.KEY_SLEEP_TIME, sleepRecord.getSleepTime());
	    bundle.putString(SleepRecordDbAdapter.KEY_WAKE_TIME, sleepRecord.getWakeUpTime());
	    bundle.putString(SleepRecordDbAdapter.KEY_SLEEP_LENGTH, sleepRecord.getSleepLength());
	    
	    if (rowId != null) 
	        bundle.putLong(SleepRecordDbAdapter.KEY_ROWID, rowId);
	    
	    Intent intent = new Intent();
	    intent.putExtras(bundle);
	    setResult(RESULT_OK, intent);
	    finish();
    }
    
    /* Wake up date is not stored in the DB, so we calculate it manually. */
    private Calendar getWakeUpDate(String sleepTimeString, String timeDifferenceString)
    {
    	Calendar sleepTime = DateTimeHelper.convertStringToCalendar(sleepTimeString);
    	Calendar wakeUpDate = (Calendar)sleepTime.clone();
    	
    	DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.getDefault());
    	DecimalFormat form = new DecimalFormat("");
    	form.setDecimalFormatSymbols(sym);
    	
    	float timeDifferenceInHours = (float)0.0;
		try 
		{
			timeDifferenceInHours = form.parse(timeDifferenceString).floatValue();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		int hour = (int) timeDifferenceInHours;
		int minute = (int)((timeDifferenceInHours - hour) * 60);
		
		wakeUpDate.add(Calendar.HOUR, hour);
		wakeUpDate.add(Calendar.MINUTE, minute);
		
    	return wakeUpDate;
    }
}