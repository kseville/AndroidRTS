/**
 * Application Framework package
 */
package com.rts.appframework;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rts.GameMap;
import com.rts.MainMenu;
import com.rts.commandprocessor.AttackBuildingCommand;
import com.rts.commandprocessor.AttackCommand;
import com.rts.commandprocessor.CommandProcessor;
import com.rts.commandprocessor.ConstructBuildingCommand;
import com.rts.commandprocessor.ConstructUnitCommand;
import com.rts.commandprocessor.MoveCommand;
import com.rts.database.GameDatabase;
import com.rts.database.PlayerDatabase;

/**
 * Game state information and methods
 * @author Korie Seville
 * 
 */
public class Game
{
	/**
	 * 2-D array representing the positions of the units on the game map
	 */
	private static Object[][] gameMap;
	/**
	 * Active human player
	 */
	public static Player activePlayer;

	/**
	 * Active AI player
	 */
	public static Player aiPlayer;

	/**
	 * Singleton instance of the game state
	 */
	public static Game game;

	private static Clock clock;
	
	/**
	 * Flag that stores whether or not the game is over
	 */
	public static boolean gameOver;

	/**
	 * Constructor for the game
	 * 
	 * @return the game instance
	 */
	public static Game getInstance()
	{
		if (game == null)
		{
			activePlayer = MainMenu.getActivePlayer();
			game = new Game();
		}
		return game;
	}

	/**
	 * Initializes the new game state and game map
	 * @param difficulty - difficulty of the game
	 * @param combatStyle  - combat style of the game
	 */
	@SuppressWarnings("unused")
	public void initializeGame(int difficulty, int combatStyle)
	{
		DefaultAI ai = DefaultAI.getInstance();
		ai.setDifficulty(difficulty);
		ai.setCombatStyle(combatStyle);

		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		ContentValues difficultyCV = new ContentValues();
		difficultyCV.put("DIFFICULTY", difficulty);
		difficultyCV.put("COMBATSTYLE", combatStyle);
		pdb.update("PlayerProfiles", difficultyCV, "ACTIVE = 1", null);

		aiPlayer = ai.getAiPlayer();
		gameMap = new Object[10][10];
		gameOver = false;

		Building building = new Building(1, activePlayer, 5, 0, true);
		Building building2 = new Building(3, activePlayer, 8, 0, true);
		Building building3 = new Building(2, activePlayer, 2, 0, true);
		Building building4 = new Building(1, aiPlayer, 5, 9, true);
		Building building5 = new Building(3, aiPlayer, 8, 9, true);
		Building building6 = new Building(2, aiPlayer, 2, 9, true);

		Unit unit = new Unit(1, activePlayer, true);
		Unit unit2 = new Unit(1, aiPlayer, true);

		Log.v(null, "Difficulty: " + ai.getDifficulty());
		Log.v(null, "CombatStyle: " + ai.getCombatStyle());
	}

	/**
	 * Instantiates objects stored in the database and resumes the saved game
	 * @param difficulty - difficulty of the saved game
	 * @param combatStyle - combat style set in the saved game
	 */
	@SuppressWarnings("unused")
	public void resumeSavedGame(int difficulty, int combatStyle)
	{
		DefaultAI ai = DefaultAI.getInstance();
		ai.setDifficulty(difficulty);
		ai.setCombatStyle(combatStyle);
		aiPlayer = ai.getAiPlayer();
		gameMap = new Object[10][10];

		// Re-instantiate all units in DB
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(activePlayer.getPlayerName());
		Cursor cursor = gdb.query("Units", null, null, null, null, null, null);

		while (cursor.moveToNext())
		{
			int type = cursor.getInt(cursor.getColumnIndex("TYPE"));
			int hp = cursor.getInt(cursor.getColumnIndex("HP"));
			int power = cursor.getInt(cursor.getColumnIndex("POWER"));
			int experience = cursor.getInt(cursor.getColumnIndex("EXPERIENCE"));
			int level = cursor.getInt(cursor.getColumnIndex("LEVEL"));
			int xposition = cursor.getInt(cursor.getColumnIndex("XPOSITION"));
			int yposition = cursor.getInt(cursor.getColumnIndex("YPOSITION"));
			String ownerID = cursor.getString(cursor.getColumnIndex("OWNER"));
			long rowID = cursor.getPosition() + 1;

			Player owner;
			if (ownerID.compareTo(activePlayer.getPlayerName()) == 0)
			{
				owner = activePlayer;
			} else
			{
				owner = aiPlayer;
			}

			if (xposition == -1 && yposition == -1)
			{
				gdb.delete("Units", "ROWID = " + Long.toString(rowID), null);
			} else
			{
				Unit unit = new Unit(type, owner, true, rowID, hp, power, experience, level, xposition, yposition);
			}
		}

		// Re-instantiate all buildings in DB
		Cursor buildingcursor = gdb.query("Buildings", null, null, null, null, null, null);
		while (buildingcursor.moveToNext())
		{
			int buildingType = buildingcursor.getInt(buildingcursor.getColumnIndex("TYPE"));
			int buildingHP = buildingcursor.getInt(buildingcursor.getColumnIndex("HP"));
			int buildingX = buildingcursor.getInt(buildingcursor.getColumnIndex("XPOSITION"));
			int buildingY = buildingcursor.getInt(buildingcursor.getColumnIndex("YPOSITION"));
			String buildingOwnerID = buildingcursor.getString(buildingcursor.getColumnIndex("OWNER"));
			long buildingRowID = buildingcursor.getPosition() + 1;
			Player buildingOwner;
			if (buildingOwnerID.compareTo(activePlayer.getPlayerName()) == 0)
			{
				buildingOwner = activePlayer;
			} else
			{
				buildingOwner = aiPlayer;
			}
			Building building = new Building(buildingType, buildingOwner, buildingHP, buildingX, buildingY, buildingRowID);
		}

		// Set player resources to database values.
		Cursor playercursor = gdb.query("Players", null, null, null, null, null, null);
		while (playercursor.moveToNext())
		{
			String player = playercursor.getString(playercursor.getColumnIndex("NAME"));
			int resources = playercursor.getInt(playercursor.getColumnIndex("RESOURCES"));
			if (player.compareTo(activePlayer.getPlayerName()) == 0)
			{
				activePlayer.setResources(resources);
			} else
			{
				aiPlayer.setResources(resources);
			}
		}
		
		reinstantiateCommands();
	}

	/**
	 * Pulls stored commands from the database and adds them to the command queue
	 */
	@SuppressWarnings("unused")
	public void reinstantiateCommands()
	{
		// Reload all active commands in the command queue
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(activePlayer.getPlayerName());
		Cursor commandcursor = gdb.query("Commands", null, null, null, null, null, null);
		while (commandcursor.moveToNext())
		{
			int commandType = commandcursor.getInt(commandcursor.getColumnIndex("COMMANDTYPE"));
			if (commandType == 1) // Move command
			{
				String playerName = commandcursor.getString(commandcursor.getColumnIndex("PLAYERMOVE"));
				Player player;
				if (playerName.compareTo(activePlayer.getPlayerName()) == 0)
				{
					player = activePlayer;
				} else
				{
					player = aiPlayer;
				}

				long movingUnitRowID = commandcursor.getLong(commandcursor.getColumnIndex("MOVINGUNIT"));
				Unit movingUnit = Unit.getUnitByRowID(movingUnitRowID);
				int moveDelay = commandcursor.getInt(commandcursor.getColumnIndex("MOVEDELAY"));
				int x = commandcursor.getInt(commandcursor.getColumnIndex("X"));
				int y = commandcursor.getInt(commandcursor.getColumnIndex("Y"));

				MoveCommand m = new MoveCommand(player, movingUnit, moveDelay,
						x, y);
			} else if (commandType == 2) // Attack command
			{
				long sourceRowID = commandcursor.getLong(commandcursor.getColumnIndex("SOURCEATTACKER"));
				Unit source = Unit.getUnitByRowID(sourceRowID);
				long targetRowID = commandcursor.getLong(commandcursor.getColumnIndex("TARGET"));
				Unit target = Unit.getUnitByRowID(targetRowID);
				int delay = commandcursor.getInt(commandcursor.getColumnIndex("ATTACKDELAY"));
				AttackCommand a = new AttackCommand(source, target, delay);
			} else if (commandType == 3) // Attack building command
			{
				long sourceRowID = commandcursor.getLong(commandcursor.getColumnIndex("SOURCEATTACKER"));
				Unit source = Unit.getUnitByRowID(sourceRowID);
				long targetRowID = commandcursor.getLong(commandcursor.getColumnIndex("TARGET"));
				Building target = Building.getUnitByRowID(targetRowID);
				int delay = commandcursor.getInt(commandcursor.getColumnIndex("ATTACKDELAY"));
				AttackBuildingCommand a = new AttackBuildingCommand(source, target, delay);
			} else if (commandType == 4) // Construct building command
			{
				int buildingType = commandcursor.getInt(commandcursor.getColumnIndex("CONSTRUCTIONTYPE"));
				long sourceBuildingRowID = commandcursor.getLong(commandcursor.getColumnIndex("SOURCEBUILDING"));
				Building source = Building.getUnitByRowID(sourceBuildingRowID);
				String playerName = commandcursor.getString(commandcursor.getColumnIndex("OWNER"));
				Player player;
				if (playerName.compareTo(activePlayer.getPlayerName()) == 0)
				{
					player = activePlayer;
				} else
				{
					player = aiPlayer;
				}
				ConstructBuildingCommand b = new ConstructBuildingCommand(source, buildingType, player);
			} else if (commandType == 5) // Construct unit command
			{
				int unitType = commandcursor.getInt(commandcursor.getColumnIndex("CONSTRUCTIONTYPE"));
				long sourceBuildingRowID = commandcursor.getLong(commandcursor.getColumnIndex("SOURCEBUILDING"));
				Building source = Building.getUnitByRowID(sourceBuildingRowID);
				String playerName = commandcursor.getString(commandcursor.getColumnIndex("OWNER"));
				Player player;
				if (playerName.compareTo(activePlayer.getPlayerName()) == 0)
				{
					player = activePlayer;
				} else
				{
					player = aiPlayer;
				}
				ConstructUnitCommand u = new ConstructUnitCommand(source, unitType, player);
			}
		}
	}

	/**
	 * Starts the game clock
	 */
	public void startClock()
	{
		clock = new Clock();
		clock.start();
	}
	
	/**
	 * Stops the game clock
	 * @throws InterruptedException if thread is in interrupt state
	 */
	public void stopClock() throws InterruptedException
	{
		clock.stopClock();
	}

	/**
	 * @return the current gameMap
	 */
	public static Object[][] getGameMap()
	{
		return gameMap;
	}

	/**
	 * Adds object to gameMap grid. If an object is already in that position,
	 * the method decides on one of two actions: 1. If the unit is a new unit,
	 * it will look for an empty array position that is closest to 0,0 and add
	 * the unit to that position 2. If the unit is an existing unit, the
	 * application will show a "space already occupied" error
	 * 
	 * @param unit
	 *            - moving unit
	 * @param sourcex
	 *            - source x coordinate
	 * @param sourcey
	 *            - source y coordinate
	 * @param destinationx
	 *            - destination x coordinate
	 * @param destinationy
	 *            - destination y coordinate
	 */
	public void addToGameMap(Unit unit, int sourcex, int sourcey,
			int destinationx, int destinationy)
	{
		if (sourcex == -1 && sourcey == -1)
		{
			Object objectAtPosition = gameMap[destinationy][destinationx];
			if (objectAtPosition == null)
			{
				gameMap[destinationy][destinationx] = unit;
				unit.setX(destinationx);
				unit.sety(destinationy);
			} else
			{
				if (unit.getOwnerID().compareTo("AI") == 0)
				{
					boolean complete = false;
					for (int y = 8; y > 0; y--)
					{
						for (int x = 0; x < 10; x++)
						{
							objectAtPosition = gameMap[y][x];
							if (objectAtPosition == null)
							{
								gameMap[y][x] = unit;
								unit.setX(x);
								unit.sety(y);
								complete = true;
								break;
							}
						}
						if (complete)
						{
							break;
						}
					}
				} else
				{
					boolean complete = false;
					for (int y = 0; y < 10; y++)
					{
						for (int x = 0; x < 10; x++)
						{
							objectAtPosition = gameMap[y][x];
							if (objectAtPosition == null)
							{
								gameMap[y][x] = unit;
								unit.setX(x);
								unit.sety(y);
								complete = true;
								break;
							}
						}
						if (complete)
						{
							break;
						}
					}
				}
			}
		} else
		{
			Object objectAtPosition = gameMap[destinationy][destinationx];
			if (objectAtPosition == null)
			{
				gameMap[sourcey][sourcex] = null;
				gameMap[destinationy][destinationx] = unit;
				unit.setX(destinationx);
				unit.sety(destinationy);
			} else
			{
				boolean spaceFound = false;
				int closestDistanceToX = 100;
				int arrayValueAtClosestDistance = 0;

				for (int i = destinationy; i < 10; i++)
				{
					for (int j = 0; j < 10; j++)
					{
						objectAtPosition = gameMap[i][j];
						if (objectAtPosition == null)
						{
							int temp = destinationx - j;
							temp = Math.abs(temp);
							if (temp < closestDistanceToX)
							{
								closestDistanceToX = temp;
								arrayValueAtClosestDistance = j;
								spaceFound = true;
							}
						}
					}
					if (spaceFound)
					{
						destinationx = arrayValueAtClosestDistance;
						destinationy = i;
						gameMap[sourcey][sourcex] = null;
						gameMap[destinationy][destinationx] = unit;
						unit.setX(destinationx);
						unit.sety(destinationy);
						break;
					}
				}
			}
		}
	}

	/**
	 * @param yposition
	 *            - y position of gameMap
	 * @param xposition
	 *            - x position of gameMap
	 * @return the unit at the specified position
	 */
	public Object getObjectAtPosition(int yposition, int xposition)
	{
		return gameMap[yposition][xposition];
	}

	/**
	 * @return the active player
	 */
	public static Player getActivePlayer()
	{
		return activePlayer;
	}

	/**
	 * @return the ai player
	 */
	public static Player getAiPlayer()
	{
		return aiPlayer;
	}

	/**
	 * Add building to gameMap
	 * 
	 * @param building
	 *            - the building to add
	 */
	public void addBuildingToGameMap(Building building)
	{
		Object position = gameMap[building.getY()][building.getX()];
		if (position == null)
		{
			gameMap[building.getY()][building.getX()] = building;
		} else
		{
			building.getOwner().deassociateBuildingFromPlayer(building);
		}
	}

	/**
	 * Deletes an object from the gameMap
	 * @param x - x position
	 * @param y - y position
	 */
	public static void removeFromGameMap(int x, int y)
	{
		gameMap[y][x] = null;
	}

	/**
	 * Forces a unit to the game map (needed for when the unit is already in the database when a game is resumed)
	 * @param unit - unit to be added
	 * @param x - x coordinate
	 * @param y - y coordinate
	 */
	public static void forceAddUnitToGameMap(Unit unit, int x, int y)
	{
		gameMap[y][x] = unit;
	}

	/**
	 * Forces a building to the game map (needed for when the building is already in the database when a game is resumed)
	 * @param building - building to be added
	 * @param x - x coordinate
	 * @param y - y coordinate
	 */
	public static void forceAddBuildingToGameMap(Building building, int x, int y)
	{
		gameMap[y][x] = building;
	}

	/**
	 * Saves gamestate information to database
	 */
	@SuppressWarnings("unused")
	public static void saveGamestateToDatabase()
	{
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(activePlayer
				.getPlayerName());
		for (int i = 0; i < activePlayer.getUnitArraySize(); i++)
		{
			Unit unitToUpdate = activePlayer.units.get(i);
			ContentValues unitCV = new ContentValues();
			unitCV.put("TYPE", unitToUpdate.getType());
			unitCV.put("HP", unitToUpdate.getHP());
			unitCV.put("POWER", unitToUpdate.getPower());
			unitCV.put("EXPERIENCE", unitToUpdate.getExperience());
			unitCV.put("LEVEL", unitToUpdate.getLevel());
			unitCV.put("XPOSITION", unitToUpdate.getX());
			unitCV.put("YPOSITION", unitToUpdate.getY());
			unitCV.put("OWNER", unitToUpdate.getOwnerID());
			String rowid[] = new String[1];
			rowid[0] = Long.toString(unitToUpdate.getRowID());
			int rowsAffected = gdb.update("Units", unitCV, "ROWID = ?", rowid);
			Log.v(null, "Player rows affected: " + rowsAffected);
		}

		for (int i = 0; i < aiPlayer.getUnitArraySize(); i++)
		{
			Unit unitToUpdate = aiPlayer.units.get(i);
			ContentValues unitCV = new ContentValues();
			unitCV.put("TYPE", unitToUpdate.getType());
			unitCV.put("HP", unitToUpdate.getHP());
			unitCV.put("POWER", unitToUpdate.getPower());
			unitCV.put("EXPERIENCE", unitToUpdate.getExperience());
			unitCV.put("LEVEL", unitToUpdate.getLevel());
			unitCV.put("XPOSITION", unitToUpdate.getX());
			unitCV.put("YPOSITION", unitToUpdate.getY());
			unitCV.put("OWNER", unitToUpdate.getOwnerID());
			String rowid[] = new String[1];
			rowid[0] = Long.toString(unitToUpdate.getRowID());
			int rowsAffected = gdb.update("Units", unitCV, "ROWID = ?", rowid);
			Log.v(null, "AI unit rows affected: " + rowsAffected);
		}

		for (int i = 0; i < activePlayer.buildings.size(); i++)
		{
			Building buildingToUpdate = activePlayer.buildings.get(i);
			ContentValues buildingCV = new ContentValues();
			buildingCV.put("TYPE", buildingToUpdate.getType());
			buildingCV.put("HP", buildingToUpdate.getHP());
			buildingCV.put("XPOSITION", buildingToUpdate.getX());
			buildingCV.put("YPOSITION", buildingToUpdate.getY());
			buildingCV.put("OWNER", buildingToUpdate.getOwnerID());
			String rowid[] = new String[1];
			rowid[0] = Long.toString(buildingToUpdate.getRowID());
			int rowsAffected = gdb.update("Buildings", buildingCV, "ROWID = ?",
					rowid);
			Log.v(null, "Player building rows affected: " + rowsAffected);
		}

		for (int i = 0; i < aiPlayer.buildings.size(); i++)
		{
			Building buildingToUpdate = aiPlayer.buildings.get(i);
			ContentValues buildingCV = new ContentValues();
			buildingCV.put("TYPE", buildingToUpdate.getType());
			buildingCV.put("HP", buildingToUpdate.getHP());
			buildingCV.put("XPOSITION", buildingToUpdate.getX());
			buildingCV.put("YPOSITION", buildingToUpdate.getY());
			buildingCV.put("OWNER", buildingToUpdate.getOwnerID());
			String rowid[] = new String[1];
			rowid[0] = Long.toString(buildingToUpdate.getRowID());
			int rowsAffected = gdb.update("Buildings", buildingCV, "ROWID = ?", rowid);
			Log.v(null, "AI building rows affected: " + rowsAffected);
		}

		ContentValues playerResources = new ContentValues();
		playerResources.put("RESOURCES", activePlayer.getResources());
		String rowid[] = new String[1];
		rowid[0] = Long.toString(activePlayer.getRowID());
		int rowsAffected = gdb.update("Players", playerResources, "ROWID = ?", rowid);
		Log.v(null, "Player resources rows affected: " + rowsAffected);

		playerResources = new ContentValues();
		playerResources.put("RESOURCES", aiPlayer.getResources());
		rowid = new String[1];
		rowid[0] = Long.toString(aiPlayer.getRowID());
		int aiRowsAffected = gdb.update("Players", playerResources, "ROWID = ?", rowid);
		Log.v(null, "AI resources rows affected: " + rowsAffected);

		ContentValues profileSavedGameUpdate = new ContentValues();
		profileSavedGameUpdate.put("SAVEDGAME", 1);
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		int savedGameRowsAffected = pdb.update("PlayerProfiles", profileSavedGameUpdate, "ACTIVE = 1", null);
		Log.v(null, "Saved Game state rows affected: " + savedGameRowsAffected);

		CommandProcessor cp = CommandProcessor.getInstance();
		cp.saveCommandsToDatabase();

		Log.v(null, "Gamestate saved to database");
	}

	/**
	 * Clears the database of all old saved game information
	 */
	public void clearDatabase()
	{
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(activePlayer.getPlayerName());

		ContentValues resetPDB = new ContentValues();
		resetPDB.put("SAVEDGAME", 0);
		resetPDB.put("DIFFICULTY", 0);
		resetPDB.put("COMBATSTYLE", 0);
		pdb.update("PlayerProfiles", resetPDB, "ACTIVE = 1", null);

		ContentValues resetResources = new ContentValues();
		resetResources.put("RESOURCES", 0);
		gdb.update("Players", resetResources, null, null);

		gdb.delete("Units", null, null);
		gdb.delete("Buildings", null, null);
		gdb.delete("Commands", null, null);
	}
	
	/**
	 * Clears the commands out of the database
	 */
	public void clearCommandDatabase()
	{
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(activePlayer.getPlayerName());
		gdb.delete("Commands", null, null);
	}
	
	/**
	 * Ends the game
	 * @param playerEliminated - eliminated player  
	 */
	public static void endGame(Player playerEliminated)
	{
		String playerName = playerEliminated.getPlayerName();
		if(playerName.compareTo("AI") == 0)
		{
			GameMap.gameMapActivity.runOnUiThread(new Runnable()
			{
			     public void run() 
			     {
			    	 GameMap.gameMapActivity.displayToastMessageInGame("You are victorious!");
			     }
			});
		}
		else
		{
			GameMap.gameMapActivity.runOnUiThread(new Runnable()
			{
			     public void run() 
			     {
			    	 GameMap.gameMapActivity.displayToastMessageInGame("You have been defeated!");
			     }
			});
		}
		
		Game game = Game.getInstance();
		try
		{
			game.stopClock();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		gameOver = true;
		
		ContentValues profileSavedGameUpdate = new ContentValues();
		profileSavedGameUpdate.put("SAVEDGAME", 0);
		profileSavedGameUpdate.put("DIFFICULTY", 0);
		profileSavedGameUpdate.put("COMBATSTYLE", 0);
		SQLiteDatabase pdb = PlayerDatabase.getPlayerDatabase();
		int clearSavedGameRowsAffected = pdb.update("PlayerProfiles", profileSavedGameUpdate, "ACTIVE = 1", null);
		Log.v(null, "Clear saved game - rows affected: " + clearSavedGameRowsAffected);
	}
	
	/**
	 * @return whether or not the game is over
	 */
	public static boolean isGameOver()
	{
		return gameOver;
	}
	
	/**
	 * Checks whether the specified space is open or not
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @return whether or not the space is open
	 */
	public static boolean spaceIsClear(int x, int y)
	{
		if(gameMap[y][x] != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
