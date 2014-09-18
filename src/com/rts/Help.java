package com.rts;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * Displays the help/faq activity
 * 
 * @author Korie Seville
 *
 */
public class Help extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
	}

	/**
	 * Controls the function of the action bar menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

}
