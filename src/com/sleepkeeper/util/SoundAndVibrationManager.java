package com.sleepkeeper.util;

import android.content.Context;
import android.media.AudioManager;
import android.widget.Toast;

import com.sleepkeeper.app.R;

/* This class is responsible for:
 * 
 * - Turning phone's vibration motor on/off
 * - Turning phone's sound on/off
*/
public class SoundAndVibrationManager 
{
	private Context context = null;
	private AudioManager audioManager = null;
	private int ringerMode = AudioManager.RINGER_MODE_NORMAL;
	private final String noSoundAndVibration = "No sound and vibration";
	private final String noSoundButVibration = "No sound. Vibration on.";
	
	public SoundAndVibrationManager(Context applicationContext)
	{
		context = applicationContext;
		audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		ringerMode = audioManager.getRingerMode();
	}
	
	public void changeRingingMode(String preferredRingerMode)
	{
		ringerMode = audioManager.getRingerMode();

		if(preferredRingerMode.equals(noSoundAndVibration))
		{
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			Toast.makeText(context, 
						   R.string.no_sound_and_vibration, 
						   Toast.LENGTH_SHORT).show();
		}
		else if(preferredRingerMode.equals(noSoundButVibration))
		{
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			Toast.makeText(context, 
						   R.string.no_sound_but_vibration, 
						   Toast.LENGTH_SHORT).show();
		}
	}
	
	public void restoreRingingMode()
	{
		audioManager.setRingerMode(ringerMode);
		
		if(ringerMode == AudioManager.RINGER_MODE_SILENT)
			Toast.makeText(context, R.string.no_sound_and_vibration, Toast.LENGTH_SHORT).show();
		else if(ringerMode == AudioManager.RINGER_MODE_VIBRATE)
			Toast.makeText(context, R.string.no_sound_but_vibration, Toast.LENGTH_SHORT).show();
		else if(ringerMode == AudioManager.RINGER_MODE_NORMAL)
			Toast.makeText(context, R.string.normal_ringer_mode, Toast.LENGTH_SHORT).show();
	}
}
