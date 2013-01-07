package com.sleepkeeper.util;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

import com.sleepkeeper.app.R;

/* Manages WiFi, 3G and Edge connectivity state of the phone
*/
public class NetworkStateManager extends android.app.Application
{
	private Context context = null;
	
	private WifiManager wifiManager = null;
	private boolean wasWifiTurnedOnPreviously = false;
	
	public NetworkStateManager(Context applicationContext)
	{
		context = applicationContext;
		wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wasWifiTurnedOnPreviously = false;
	}
	
	public void turnWifiOff()
	{
		if(wifiManager.isWifiEnabled())
		{
			wasWifiTurnedOnPreviously = true;
			boolean wifiDisabled = wifiManager.setWifiEnabled(false);
			if(!wifiDisabled)
				Toast.makeText(context, 
							   R.string.failed_to_disable_wifi, 
							   Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(context, 
							   R.string.wifi_disabled, 
							   Toast.LENGTH_SHORT).show();
		}
	}
	
	public void turnWifiOn()
	{
		if(wasWifiTurnedOnPreviously)
		{
			boolean wifiEnabled = wifiManager.setWifiEnabled(true);
			if(!wifiEnabled)
				Toast.makeText(context, 
							   R.string.failed_to_enable_wifi, 
							   Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(context, 
							   R.string.wifi_enabled, 
							   Toast.LENGTH_SHORT).show();	
		}
	}
	
	/* Code excerpt from 
	 * http://stackoverflow.com/questions/3249245/how-to-set-the-airplane-mode-on-to-true-or-on
	 */
	public void turnOnFlightMode()
	{
		boolean isFlightModeTurnedOn = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
		if(!isFlightModeTurnedOn)
		{
			Settings.System.putInt(context.getContentResolver(),
			      			   Settings.System.AIRPLANE_MODE_ON, 
			      			   1);
		
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", true);
			context.sendBroadcast(intent);
		}
	}
	
	public void turnOffFlightMode()
	{
		boolean isFlightModeTurnedOn = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
		if(isFlightModeTurnedOn)
		{
			Settings.System.putInt(context.getContentResolver(),
	      			   Settings.System.AIRPLANE_MODE_ON, 
	      			   0);

			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", false);
			context.sendBroadcast(intent);
		}
	}
}
