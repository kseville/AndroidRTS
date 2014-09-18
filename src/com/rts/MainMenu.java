package com.rts;

import java.util.Random;

import com.rts.appframework.Game;
import com.rts.appframework.Player;
import com.rts.appframework.Profile;
import com.rts.dailogFragments.SavedGameDialogFragment;
import com.rts.database.PlayerDatabase;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Main menu input handlers and input listeners
 * @author Korie Seville
 *
 */
public class MainMenu extends Activity
{
	private static Player activePlayer; 
	protected static int difficulty = 0;
	protected static int combatStyle = 0;
	protected static boolean difficultyModified = false;
	/**
	 * mainMenu activity object
	 */
	public static MainMenu mainMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		
		activePlayer = Profile.loadActivePlayer();
		mainMenu = this;
	}

	/**
	 * Create the option menu (action bar menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	/**
	 * Controls the functions of the action bar menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.item1:
			Intent i = new Intent(getApplicationContext(), UnitList.class);
			startActivity(i);
			break;
		case R.id.item2:
			Intent i2 = new Intent(getApplicationContext(), Settings.class);
			startActivity(i2);
			break;
		case R.id.item3:
			Intent i3 = new Intent(getApplicationContext(), Help.class);
			startActivity(i3);
			break;
		case R.id.item4:
			Intent i4 = new Intent(getApplicationContext(), About.class);
			startActivity(i4);
			break;
		}
		return true;
	}
	
	/**
	 * @return the active player
	 */
	public static Player getActivePlayer()
	{
		return activePlayer;
	}
	
	/**
	 * @return the set difficulty
	 */
	public static int getDifficulty()
	{
		return difficulty;
	}
	
	/**
	 * @return the set combat style
	 */
	public static int getCombatStyle()
	{
		return combatStyle;
	}
	
	/**
	 * Start the game
	 * @param view - the Current intent's view
	 */
	public void startGame(View view)
	{
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		Cursor cursor = pdb.query("PlayerProfiles", null, "SAVEDGAME = 1", null, null, null, null);
		if(cursor.moveToFirst())
		{
			SavedGameDialogFragment df = new SavedGameDialogFragment();
			df.show(getFragmentManager(), null);
		}
		else
		{
			startNewGame();
		}
	}
	
	/**
	 * Acts on the response from the new or resume game dialog fragment
	 * @param resumeSavedGame - response from the new or resume game (true = resume game, false = new game)
	 */
	public void responseFromDF(boolean resumeSavedGame)
	{
		if(resumeSavedGame)
		{
			resumeSavedGame();
		}
		else
		{
			startNewGame();
		}
	}
	
	/**
	 * Starts a new game
	 */
	public void startNewGame()
	{
		Intent i = new Intent(getApplicationContext(), GameMap.class);
		startActivity(i);
		Game game = Game.getInstance();
		game.clearDatabase();
		
		if(difficultyModified)
		{
			game.initializeGame(difficulty, combatStyle);
		}
		else
		{
			int temp = 0;
			Random r = new Random();
			while(temp < 1)
			{
				temp = r.nextInt(4);
			}
			difficulty = temp;
			
			int temp1 = 0;
			Random r1 = new Random();
			while(temp1 < 1)
			{
				temp1 = r1.nextInt(4);
			}
			combatStyle = temp1;
			
			game.initializeGame(difficulty, combatStyle);
		}
	}
	
	/**
	 * Resumes a saved game
	 */
	public void resumeSavedGame()
	{
		Intent i = new Intent(getApplicationContext(), GameMap.class);
		startActivity(i);
		
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		Cursor cursor = pdb.query("PlayerProfiles", null, "SAVEDGAME = 1", null, null, null, null);
		cursor.moveToFirst();
		int difficulty = cursor.getInt(cursor.getColumnIndex("DIFFICULTY"));
		int combatStyle = cursor.getInt(cursor.getColumnIndex("COMBATSTYLE"));
		
		Game game = Game.getInstance();
		game.resumeSavedGame(difficulty, combatStyle);
	}
	
	/**
	 * Change the difficulty and attack strategy of the game
	 * @param view - current context's view
	 */
	public void changeDifficulty(View view)
	{
		Intent i = new Intent(getApplicationContext(), DifficultySettings.class);
		startActivity(i);
	}
	
	/**
	 * Change the map
	 * @param view - current context's view
	 */
	public void changeMap(View view)
	{
		Intent i = new Intent(getApplicationContext(), Maps.class);
		startActivity(i);
	}
	
	/**
	 * Switch the current profile (go back to login screen)
	 * @param view - Current intent's view
	 */
	public void switchProfile(View view)
	{
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		ContentValues cv = new ContentValues();
		cv.put("ACTIVE", 0);
		int rows = pdb.update("PlayerProfiles", cv, null, null);
		Log.v(null, "Switch Profiles rows affected: " + rows);
		Intent i = new Intent(getApplicationContext(), LoginScreen.class);
		startActivity(i);
		finish();
	}
	
	/**
	 * Exit the application cleanly (save data, close database connections, etc)
	 * @param view - Current intent's view
	 */
	public void exit(View view)
	{
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		ContentValues cv = new ContentValues();
		cv.put("ACTIVE", 0);
		int rows = pdb.update("PlayerProfiles", cv, null, null);
		Log.v(null, "Exit rows affected: " + rows);
		finish();
	}
}
