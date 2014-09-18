package com.rts;

import java.util.Random;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Displays the difficulty settings activity and allows for the difficulty and combat style to be changed.
 * @author Korie Seville
 *
 */
public class DifficultySettings extends Activity
{
	
	private String difficulty;
	private String combatStyle;
	private boolean difficultySelected = false;
	private boolean combatStyleSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_difficulty_settings);
		radioButtonListenerSetup();
	}

	/**
	 * Controls the function of the action bar menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.difficulty_settings, menu);
		return true;
	}
	
	/**
	 * Sets up the listeners on the radio button groups
	 */
	public void radioButtonListenerSetup()
	{
		RadioGroup radioGroup1;
		RadioGroup radioGroup2;
		
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
		
		radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) 
	        {
	            RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
	            difficulty = checkedRadioButton.getText().toString();
	            difficultySelected = true;
	            Log.v(null, "Difficulty Selected: " + difficulty);
	        }
	    });
		
		radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) 
	        {
	            RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
	            combatStyle = checkedRadioButton.getText().toString();
	            combatStyleSelected = true;
	            Log.v(null, "Combat Style Selected: " + combatStyle);
	        }
	    });
	}
	
	/**
	 * Functions to be performed when the ok button is pressed
	 * @param view - Current view
	 */
	public void okSelected(View view)
	{
		if(difficultySelected)
		{
			if(difficulty.compareTo("Easy") == 0)
			{
				MainMenu.difficulty = 1;
			}
			else if(difficulty.compareTo("Medium") == 0)
			{
				MainMenu.difficulty = 2;
			}
			else if(difficulty.compareTo("Hard") == 0)
			{
				MainMenu.difficulty = 3;
			}
			else
			{
				int temp = 0;
				Random r = new Random();
				while(temp < 1)
				{
					temp = r.nextInt(4);
				}
				MainMenu.difficulty = temp;
			}
			MainMenu.difficultyModified = true;
		}
		else
		{
			int temp = 0;
			Random r = new Random();
			while(temp < 1)
			{
				temp = r.nextInt(4);
			}
			MainMenu.difficulty = temp;
			MainMenu.difficultyModified = true;
		}
		
		if(combatStyleSelected)
		{
			if(combatStyle.compareTo("Defensive") == 0)
			{
				MainMenu.combatStyle = 1;
			}
			else if(combatStyle.compareTo("Balanced") == 0)
			{
				MainMenu.combatStyle = 2;
			}
			else if(combatStyle.compareTo("Aggressive") == 0)
			{
				MainMenu.combatStyle = 3;
			}
			else
			{
				int temp = 0;
				Random r = new Random();
				while(temp < 1)
				{
					temp = r.nextInt(4);
				}
				MainMenu.combatStyle = temp;
			}
			MainMenu.difficultyModified = true;
		}
		else
		{
			int temp = 0;
			Random r = new Random();
			while(temp < 1)
			{
				temp = r.nextInt(4);
			}
			MainMenu.combatStyle = temp;
			MainMenu.difficultyModified = true;
		}
		finish();
	}
}
