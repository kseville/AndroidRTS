package com.rts;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

/**
 * Displays the settings activity
 * @author Korie Seville
 *
 */
public class Settings extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	/**
	 * Controls the function of the action bar menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	/**
	 * Functions to be performed when the ok button is pressed
	 * @param view - Current view
	 */
	public void okButton(View view)
	{
		CheckBox music = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox sound = (CheckBox) findViewById(R.id.checkBox2);
		
		Log.v(null, "Music on: " + music.isChecked());
		Log.v(null, "Sound on: " + sound.isChecked());
		
		finish();
	}
}
