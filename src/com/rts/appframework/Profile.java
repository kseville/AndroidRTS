package com.rts.appframework;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rts.database.GameDatabase;
import com.rts.database.PlayerDatabase;

/**
 * Database and Game Profile
 * 
 * @author Korie Seville
 * 
 */
public class Profile
{
	static String playerName;
	SQLiteDatabase gdb;
	static SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();

	/**
	 * Constructor for new player. Creates a player the specified name and the
	 * database for that player, then adds that player to the player database,
	 * which holds their name and the location of their database
	 * 
	 * @param playerNameIn
	 *            - player's desired name
	 * @param existingProfileSelected 
	 * 			  - whether or not the selected profile exists already or not
	 */
	public Profile(String playerNameIn, boolean existingProfileSelected)
	{
		if(existingProfileSelected)
		{
			playerName = playerNameIn;
			setActive();
		}
		else
		{
			playerName = playerNameIn;
			gdb = GameDatabase.getGameDatabase(playerName);
			ContentValues profileCV = new ContentValues();
			profileCV.put("NAME", playerName);
			profileCV.put("ACTIVE", 0);
			profileCV.put("DATABASEPATH", gdb.getPath());
			profileCV.put("SAVEDGAME", 0);
			profileCV.put("DIFFICULTY", 0);
			profileCV.put("COMBATSTYLE", 0);
			pdb.insert("PlayerProfiles", null, profileCV);
			setActive();
		}
	}

	/**
	 * Check to see if the player name already exists in the database.
	 * 
	 * @param playerName
	 *            - name of the player to create
	 * @return true if the player already exists or false if this player does
	 *         not exist.
	 */
	public static boolean playerNameExists(String playerName)
	{
		String query = "Select * FROM PlayerProfiles WHERE NAME = '" + playerName + "'";
		Cursor cursor = pdb.rawQuery(query, null);
		cursor.moveToFirst();

		String foundName;
		try
		{
			foundName = cursor.getString(cursor.getColumnIndex("NAME"));

		} catch (Exception e)
		{
			Log.v(null, "character does not exist, creating new...");
			return false;
		}

		Log.v(null, "Found name in database: " + foundName);
		Log.v(null, "character exists, throw error...");
		return true;
	}

	/**
	 * Sets the active player profile in the database. The Main activity will
	 * read the database to look for the active player.
	 */
	public void setActive()
	{
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		ContentValues cv = new ContentValues();
		cv.put("ACTIVE", 1);
		String[] selectionArgs = new String[1];
		selectionArgs[0] = playerName;
		int rows = pdb.update("PlayerProfiles", cv, "NAME = ?", selectionArgs);
		Log.v(null, "Set Active - Rows affected: " + rows);
	}
	
	/**
	 * Query database for active player and reload the player into the main menu
	 * @return active player
	 */
	public static Player loadActivePlayer()
	{
		String[] selectionArgs = new String[1];
		selectionArgs[0] = "1";
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		Cursor cursor = pdb.query("PlayerProfiles", null, "ACTIVE = ?", selectionArgs, null, null, null);
		cursor.moveToFirst();
		playerName = cursor.getString(cursor.getColumnIndex("NAME"));
		Player player = new Player(playerName);
		return player;
	}
}
