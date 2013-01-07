package com.sleepkeeper.app;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/* This class creates the 3-tab structure of the Sleepkeeper application.
 */
public class SleepkeeperDrawTabsActivity extends TabActivity  
{
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.sleepkeeper_tab_layout);

	    TabHost tabHost = getTabHost();
	    TabHost.TabSpec spec;
	    Intent intent;

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, StartStopSleepActivity.class);
	    spec = tabHost.newTabSpec("sleep").setIndicator(getString(R.string.sleep)).setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, SleepGraphActivity.class);
	    spec = tabHost.newTabSpec("graph").setIndicator(getString(R.string.graph)).setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, SettingsMenuActivity.class);
	    spec = tabHost.newTabSpec("settings").setIndicator(getString(R.string.settings)).setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	}
}
