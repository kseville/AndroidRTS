package com.rts;

import java.io.File;

import com.rts.appframework.Profile;
import com.rts.dailogFragments.NameExistsDF;
import com.rts.database.GameDatabase;
import com.rts.database.PlayerDatabase;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Starting activity. Sets the user's profile and logs into the main menu.
 * @author Korie Seville
 *
 */
public class LoginScreen extends Activity
{
	private boolean radioButtonChecked = false; 
	private String existingProfile = null;
	
	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		File database = getApplicationContext().getDatabasePath("playerDatabase.db");
		if(database.exists())
		{
			SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
			Log.v(null, "Profile database Exists");
			ContentValues clearActive = new ContentValues();
			clearActive.put("ACTIVE", 0);
			pdb.update("PlayerProfiles", clearActive, null, null);
		}
		else
		{
			PlayerDatabase temp = new PlayerDatabase(getApplicationContext());
			SQLiteDatabase pdb = temp.getWritableDatabase();
			Log.v(null, "Profile database does not exist");
		}
		displayRadioButtons();
	}
	
	/**
	 * Dynamically add radio buttons to accommodate saved profiles
	 */
	public void displayRadioButtons() 
	{
		RadioGroup radioGroup = null;
		
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		String columns[] = new String[1];
		columns[0] = "NAME";
		Cursor c = pdb.query("PlayerProfiles", columns, null, null, null, null, null);
		int count = c.getCount();
		if(count == 0)
		{
			return;
		}
		String profiles[] = new String[count];
		int whileCounter = 0;
		while(c.moveToNext())
		{
			profiles[whileCounter] = c.getString(c.getColumnIndex("NAME"));
			whileCounter++;
		}
			
		for(int i=0;i < count;i++) 
		{
			radioGroup = (RadioGroup)findViewById(R.id.RadioGroup01);
			RadioButton rdbtn = new RadioButton(this);
			rdbtn.setId(i);
			rdbtn.setText(profiles[i]);
			radioGroup.addView(rdbtn);
		}

	   	radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) 
	        {
	            RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
	            existingProfile = checkedRadioButton.getText().toString();
	            radioButtonChecked = true;
	            Log.v(null, "Radio button checked");
	            Log.v(null, "Profile selected: " + existingProfile);
	            Log.v(null, "Status of radioButtonChecked flag: " + radioButtonChecked);
	        }
	    });
	}
	
	
	/**
	 * Defines the processes that take place when the options menu is created
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_screen, menu);
		return true;
	}
	
	/**
	 * When user hits OK, the system uses the name provided, checks for existing database, and either creates a new database or uses the existing database. Then the system closes the login activity and starts the MainMenu activity.
	 * @param view - Default View
	 */
	@SuppressWarnings("unused")
	public void okButton(View view)
	{
		EditText text = (EditText)findViewById(R.id.editText1);
		String playerName = text.getText().toString();
		Log.v(null, "Player name: " + playerName);
		Log.v(null, "Length of playername: " + playerName.length());
		if(playerName.isEmpty() && radioButtonChecked)
		{
			Profile p = new Profile(existingProfile, true);
			Intent i = new Intent(getApplicationContext(), MainMenu.class);
			startActivity(i);
			finish();
			return;
		}
		if(Profile.playerNameExists(playerName))
		{
			NameExistsDF df = new NameExistsDF();
			df.show(getFragmentManager(), null);
			return;
		}
		
		File gamedatabase = getApplicationContext().getDatabasePath(playerName + "playerDatabase.db");
		if(gamedatabase.exists())
		{
			SQLiteDatabase gdb = GameDatabase.getGameDatabase(playerName);
			Log.v(null, "Database Exists");
		}
		else
		{
			GameDatabase temp = new GameDatabase(getApplicationContext(), playerName);
			SQLiteDatabase gdb = temp.getWritableDatabase();
			Log.v(null, "Database does not exist");
		}
		
		Profile p = new Profile(playerName, false);
		Intent i = new Intent(getApplicationContext(), MainMenu.class);
		startActivity(i);
		finish();
		return;
	}
}
