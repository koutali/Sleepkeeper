package com.sleepkeeper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Adapted from the Notepad example from Android tutorial
 * located at 
 * http://developer.android.com/resources/tutorials/notepad/index.html
 */
public class SleepRecordDbAdapter 
{
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_SLEEP_TIME = "sleep_time";
    public static final String KEY_WAKE_TIME = "wake_time";
    public static final String KEY_SLEEP_LENGTH = "sleep_length";
    
    private static final String TAG = "SleepRecordDbAdapter";
    private DatabaseHelper dbHelper = null;
    private SQLiteDatabase sqliteDb = null;

    private static final String DATABASE_NAME = "data";
    private static final String SLEEP_RECORD_TABLE = "records";
    private static final int DATABASE_VERSION = 1;
    
    private static final String SLEEP_RECORD_TABLE_CREATE = "create table " + SLEEP_RECORD_TABLE +
														    " (" + KEY_ROWID + " integer primary key autoincrement, " + 
														    KEY_DATE + " text not null, " +
														    KEY_SLEEP_TIME + " text not null, " +
														    KEY_WAKE_TIME + " text not null, " +
														    KEY_SLEEP_LENGTH + " text not null);";
    private final Context context;

    /* We'd like this class to be a singleton since it will be accessed 
     * by different activities */
    private static SleepRecordDbAdapter instance = null;
    
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) 
        {         
            db.execSQL(SLEEP_RECORD_TABLE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS" + DATABASE_NAME);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param context the Context within which to work
     */
    protected SleepRecordDbAdapter(Context context) 
    {
        this.context = context;
        open();
    }
    
    public static SleepRecordDbAdapter getInstance(Context context) 
    {
        if(instance == null)
           instance = new SleepRecordDbAdapter(context);
        
        return instance;
     }

    public void open()
    {
        dbHelper = new DatabaseHelper(this.context);
        sqliteDb = dbHelper.getWritableDatabase();
    }
    
    public void close() 
    {
        dbHelper.close();
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param recordDate the title of the note
     * @param sleepTime the body of the note
     * @return rowId or -1 if failed
     */
    public long createSleepRecord(String recordDate, String sleepTime, String wakeTime, String sleepLength) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DATE, recordDate);
        initialValues.put(KEY_SLEEP_TIME, sleepTime);
        initialValues.put(KEY_WAKE_TIME, wakeTime);
        initialValues.put(KEY_SLEEP_LENGTH, sleepLength);
        
        return sqliteDb.insert(SLEEP_RECORD_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSleepRecord(long rowId) 
    {
        return sqliteDb.delete(SLEEP_RECORD_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all records in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllSleepRecords() 
    {
        return sqliteDb.query(SLEEP_RECORD_TABLE, 
        				 new String[] {KEY_ROWID, KEY_DATE,	KEY_SLEEP_TIME, KEY_WAKE_TIME, KEY_SLEEP_LENGTH}, 
        				 null, 
        				 null, 
        				 null, 
        				 null,
        				 KEY_DATE);  /* Order by sleep date ascending */
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchSleepRecord(long rowId) throws SQLException 
    {
        Cursor mCursor = sqliteDb.query(true, 
        						   SLEEP_RECORD_TABLE, 
        						   new String[] {KEY_ROWID, KEY_DATE, KEY_SLEEP_TIME, KEY_WAKE_TIME, KEY_SLEEP_LENGTH}, 
        						   KEY_ROWID + "=" + rowId, 
        						   null,
                    			   null, 
                    			   null, 
                    			   null, 
                    			   null);
        if (mCursor != null) 
            mCursor.moveToFirst();
       
        return mCursor;
    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param recordDate value to set note title to
     * @param sleepTime value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateSleepRecord(long rowId, String recordDate, String sleepTime, String wakeTime, String sleepLength) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_DATE, recordDate);
        args.put(KEY_SLEEP_TIME, sleepTime);
        args.put(KEY_WAKE_TIME, wakeTime);
        args.put(KEY_SLEEP_LENGTH, sleepLength);
        
        return sqliteDb.update(SLEEP_RECORD_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public String getSleepDate(long rowId)
    {
    	Cursor c = fetchSleepRecord(rowId);
    	return c.getString(c.getColumnIndex(KEY_DATE)); 
    }
    
    public String getSleepTime(long rowId)
    {
    	Cursor c = fetchSleepRecord(rowId);
    	return c.getString(c.getColumnIndex(KEY_SLEEP_TIME)); 
    }
    
    public String getWakeTime(long rowId)
    {
    	Cursor c = fetchSleepRecord(rowId);
    	return c.getString(c.getColumnIndex(KEY_WAKE_TIME));
    }
    
    public String getSleepLength(long rowId)
    {
    	Cursor c = fetchSleepRecord(rowId);
    	return c.getString(c.getColumnIndex(KEY_SLEEP_LENGTH)); 
    }
}
