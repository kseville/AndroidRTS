package com.rts;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

/**
 * Maps activity - displays other maps available and allows map selection
 * 
 * @author Korie Seville
 *
 */
public class Maps extends Activity
{
	/**
	 * Actions to be performed when the View is created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
	}

	/**
	 * Actions to be performed when the action bar item menu is created
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.maps, menu);
		return true;
	}
	
	/**
	 * Actions to be performed when the user hits ok
	 * @param view - Current view
	 */
	public void okButton(View view)
	{
		finish();
	}

}
