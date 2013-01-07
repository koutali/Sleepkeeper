package com.sleepkeeper.app;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.sleepkeeper.util.SleepRecord;
import com.sleepkeeper.util.SleepRecordDbAdapter;

public class SleepGraphActivity extends ListActivity 
{
	private ListView listView = null;	
    private static SleepRecordDbAdapter dbAdapter = null;
    
    private static final int EDIT_SLEEP_DATA = 0;
    private static final int ADD_SLEEP_DATA_MANUALLY = EDIT_SLEEP_DATA + 1;
    
    /* This ID is used in options menu */
    private static final int MANUALLY_INSERT_SLEEP_RECORD_ID = Menu.FIRST;
    
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep_graph_layout);
        
        listView = (ListView) findViewById(android.R.id.list);

        LayoutInflater inflater = getLayoutInflater();
        View headerView = (View) inflater.inflate(R.layout.list_static_header, null);
        listView.addHeaderView(headerView, null, false);
        
        registerForContextMenu(listView);
        
        // create/open database 
        dbAdapter = SleepRecordDbAdapter.getInstance(this);
    }
    
    /* This callback function is needed so that we refresh the table 
     * each time we return to the activity.
     */
    protected void onResume()
    {
    	super.onResume(); 
    	fillData();
    }
    
    /* This function creates Edit/Delete/Share floating context menu */
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_delete_share_data_menu, menu);
    }
    
	/* This function displays the "Add sleep record" option when the menu button is
	 * pressed on the UI.
	 */
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MANUALLY_INSERT_SLEEP_RECORD_ID, Menu.NONE, R.string.manually_add_sleep_record);
        return true;
    }
    
    /* Manual sleep record creation happens here */
    public boolean onMenuItemSelected(int featureId, MenuItem item) 
    {
        switch(item.getItemId()) 
        {
	        case MANUALLY_INSERT_SLEEP_RECORD_ID:
	        	
            	Intent intent = new Intent(this, EditSleepDataActivity.class);
            	SleepRecord now = new SleepRecord();
            	
            	/* We don't send the rowId deliberately since this will be created
            	 * upon insertion into SQLite DB.
            	 */
            	intent.putExtra(SleepRecordDbAdapter.KEY_DATE, now.getSleepDate());
            	intent.putExtra(SleepRecordDbAdapter.KEY_SLEEP_TIME, now.getSleepTime());
            	intent.putExtra(SleepRecordDbAdapter.KEY_WAKE_TIME, now.getWakeUpTime());
            	intent.putExtra(SleepRecordDbAdapter.KEY_SLEEP_LENGTH, now.getSleepLength());
            	
            	startActivityForResult(intent, ADD_SLEEP_DATA_MANUALLY);
	            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    /* This function is responsible for handling Edit, Delete and Share actions
     * on the floating context menu.
     */
	public boolean onContextItemSelected(MenuItem item) 
    {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) 
        {
            case R.id.edit_sleep_data:
            	
            	Intent intent = new Intent(this, EditSleepDataActivity.class); 
            	intent.putExtra(SleepRecordDbAdapter.KEY_ROWID, info.id);
            	intent.putExtra(SleepRecordDbAdapter.KEY_DATE, dbAdapter.getSleepDate(info.id));
            	intent.putExtra(SleepRecordDbAdapter.KEY_SLEEP_TIME, dbAdapter.getSleepTime(info.id));
            	intent.putExtra(SleepRecordDbAdapter.KEY_WAKE_TIME, dbAdapter.getWakeTime(info.id));
            	intent.putExtra(SleepRecordDbAdapter.KEY_SLEEP_LENGTH, dbAdapter.getSleepLength(info.id));
            	
            	startActivityForResult(intent, EDIT_SLEEP_DATA);
            	
                return true;
                
            case R.id.delete_sleep_data:

				Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.sleep_record_delete_confirmation);
				builder.setCancelable(true);
				builder.setPositiveButton(R.string.delete_confirmed, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) 
					{
			            dbAdapter.deleteSleepRecord(info.id);
			            fillData();
						Toast.makeText(getApplicationContext(), R.string.sleep_record_deleted, Toast.LENGTH_SHORT).show();
					}
				});
				
				builder.setNegativeButton(R.string.delete_cancelled, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						Toast.makeText(getApplicationContext(), R.string.sleep_record_kept, Toast.LENGTH_SHORT).show();
					}
				});
				
				AlertDialog dialog = builder.create();
				dialog.show();
				
                return true;
                
            case R.id.share_sleep_data:
            	/* TODO: Share a sleep record on Runkeeper website. */
            	 Toast.makeText(getApplicationContext(), R.string.share_sleep_data, Toast.LENGTH_SHORT).show();
                return true;
                
            default:
                return super.onContextItemSelected(item);
        }
    }
    
	private void fillData() 
	{
    	Cursor sleepDataCursor = dbAdapter.fetchAllSleepRecords();
        startManagingCursor(sleepDataCursor);

        String[] from = new String[] {SleepRecordDbAdapter.KEY_DATE, 
        							  SleepRecordDbAdapter.KEY_SLEEP_TIME,
        							  SleepRecordDbAdapter.KEY_WAKE_TIME,
        							  SleepRecordDbAdapter.KEY_SLEEP_LENGTH};
        
        int[] to = new int[] { R.id.date, R.id.sleep_time, R.id.wake_time, R.id.sleep_duration};
        
		SimpleCursorAdapter sleepRecords = new SimpleCursorAdapter(this, 
															R.layout.list_item, 
															sleepDataCursor, 
															from, 
															to);
        setListAdapter(sleepRecords);		
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	if(intent != null)
    	{    		
        	Bundle extras = intent.getExtras();
        	if(extras != null)
        	{
        		switch(requestCode) 
            	{
            		case EDIT_SLEEP_DATA:
        	    	    Long rowId = extras.getLong(SleepRecordDbAdapter.KEY_ROWID);
        	    	    
        	    	    if (rowId != null) 
        	    	    {
        	    	        String newSleepDate = extras.getString(SleepRecordDbAdapter.KEY_DATE);
        	    	        String newSleepTime = extras.getString(SleepRecordDbAdapter.KEY_SLEEP_TIME);
        	    	        String newWakeTime = extras.getString(SleepRecordDbAdapter.KEY_WAKE_TIME);
        	    	        String newSleepLength = extras.getString(SleepRecordDbAdapter.KEY_SLEEP_LENGTH);
        	    	        
        	    	        dbAdapter.updateSleepRecord(rowId, newSleepDate, newSleepTime, newWakeTime, newSleepLength);
        	    	    }
        	    	    
        	    	    fillData();
        	    	    break;
        	    	 
            		case ADD_SLEEP_DATA_MANUALLY:
        	    	        String newSleepDate = extras.getString(SleepRecordDbAdapter.KEY_DATE);
        	    	        String newSleepTime = extras.getString(SleepRecordDbAdapter.KEY_SLEEP_TIME);
        	    	        String newWakeTime = extras.getString(SleepRecordDbAdapter.KEY_WAKE_TIME);
        	    	        String newSleepLength = extras.getString(SleepRecordDbAdapter.KEY_SLEEP_LENGTH);
        	    	        
        	    	        dbAdapter.createSleepRecord(newSleepDate, newSleepTime, newWakeTime, newSleepLength);
        	    	        Toast.makeText(getApplicationContext(), R.string.sleep_data_created_manually, Toast.LENGTH_SHORT).show();
        	    	        fillData();
        	    	        break;
        	    	    
        	    	default:
        	    		break;
            	}
        	}
    	}
    }
}
