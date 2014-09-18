/**
 * Database package
 */
package com.rts.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Game Database helper class
 * @author Korie Seville
 *
 */
@SuppressLint("SdCardPath")
public class GameDatabase extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "GameDatabase.db";
	private static final String UNITS_TABLE_NAME = "Units";
	private static final String BUILDINGS_TABLE_NAME = "Buildings";
	private static final String PLAYERS_TABLE_NAME = "Players";
	private static final String COMMANDS_TABLE_NAME = "Commands";
	private static final String UNITS_TABLE_CREATE = "CREATE TABLE " + UNITS_TABLE_NAME + " (TYPE INT NOT NULL, " +
			" HP INT NOT NULL," +
			" POWER INT NOT NULL," + 
			" EXPERIENCE INT NOT NULL," + 
			" LEVEL INT NOT NULL," +
			" XPOSITION INT," +
			" YPOSITION INT," + 
			" OWNER STRING NOT NULL);";
	private static final String BUILDINGS_TABLE_CREATE = "CREATE TABLE " + BUILDINGS_TABLE_NAME + " (TYPE INT NOT NULL, " +
			" HP INT NOT NULL," +
			" XPOSITION INT," +
			" YPOSITION INT," + 
			" OWNER STRING NOT NULL);";
	private static final String PLAYERS_TABLE_CREATE = "CREATE TABLE " + PLAYERS_TABLE_NAME + " (NAME STRING NOT NULL, " + 
			" RESOURCES INT NOT NULL);";
	private static final String COMMANDS_TABLE_CREATE = "CREATE TABLE " + COMMANDS_TABLE_NAME + " (COMMANDTYPE INT NOT NULL, " + 
			" CONSTRUCTIONTYPE INT," + 
			" SOURCEBUILDING LONG," +
			" OWNER STRING," +
			" SOURCEATTACKER LONG," +
			" TARGET LONG," +
			" ATTACKDELAY INT," +
			" PLAYERMOVE STRING," + 
			" MOVINGUNIT LONG," +
			" MOVEDELAY INT," +
			" X INT," + 
			" Y INT);";
	private static SQLiteDatabase gdb = null;

	/**
	 * Default constructor for the database. Passes the database creation to the super class.
	 * @param context - default context
	 * @param playerName  - name of the player
	 */
	public GameDatabase(Context context, String playerName)
	{
		super(context, playerName + DATABASE_NAME, null, DATABASE_VERSION);
		Log.v(null, "PlayerDatabase constructor active");
	}

	/**
	 * Process to create the database
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(PLAYERS_TABLE_CREATE);
		db.execSQL(UNITS_TABLE_CREATE);
		db.execSQL(BUILDINGS_TABLE_CREATE);
		db.execSQL(COMMANDS_TABLE_CREATE);
		Log.v(null, "Game Database created");
	}

	/**
	 * Process to upgrade the database
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}
	
	/**
	 * Returns the player database
	 * @param playerName  - name of the player
	 * @return player database
	 * 
	 */
	public static SQLiteDatabase getGameDatabase(String playerName)
	{
		if(gdb == null)
		{
			gdb = SQLiteDatabase.openDatabase("/data/data/com.rts/databases/" + playerName + "GameDatabase.db", null, 0);
		}
		return gdb;
	}
	
	/**
	 * Close the player database connection
	 */
	public static void closeDatabaseConnection()
	{
		gdb.close();
	}
}
