package com.rts.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Player database helper class
 * @author Korie Seville
 * 
 */
public class PlayerDatabase extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "playerDatabase.db";
	private static final String PLAYERDB_TABLE_NAME = "PlayerProfiles";
	private static final String PLAYERDB_TABLE_CREATE = "CREATE TABLE " + PLAYERDB_TABLE_NAME + " (NAME TEXT NOT NULL, " +
			" ACTIVE INT NOT NULL," +
			" DATABASEPATH TEXT NOT NULL," + 
			" SAVEDGAME INT NOT NULL," +
			" DIFFICULTY INT," +
			" COMBATSTYLE INT);";
	private static SQLiteDatabase pdb = null;

	/**
	 * Default constructor for the database. Passes the database creation to the super class.
	 * @param context - default context
	 */
	public PlayerDatabase(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Process to create the database
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(PLAYERDB_TABLE_CREATE);
		Log.v(null, "Player Database created");
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
	 * @return player database
	 * 
	 */
	@SuppressLint("SdCardPath")
	public static SQLiteDatabase getPlayerDatabase()
	{
		if(pdb == null)
		{
			pdb = SQLiteDatabase.openDatabase("/data/data/com.rts/databases/playerDatabase.db", null, 0);
		}
		return pdb;
	}
	
	/**
	 * Close the player database connection
	 */
	public static void closeDatabaseConnection()
	{
		pdb.close();
	}
}