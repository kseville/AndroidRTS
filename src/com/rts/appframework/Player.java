/**
 * 
 */
package com.rts.appframework;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rts.database.GameDatabase;

/**
 * All methods and instance variables for a Player
 * @author Korie Seville
 * 
 */
public class Player
{
	/**
	 * Amount of in-game resources
	 */
	private int resources;
	
	/**
	 * Units controlled by the player
	 */
	protected ArrayList<Unit> units;
	
	/**
	 * Buildings controlled by the player
	 */
	protected ArrayList<Building> buildings;
	
	/**
	 * Player name
	 */
	private String playerName;
	
	/**
	 * Database row id of the player
	 */
	private long rowID;
	
	/**
	 * Build delay for new units. Derived from the following formula: 10 - # construction centers
	 */
	private int buildDelay;
	
	/**
	 * Number of Construction centers 
	 */
	private int numConstructionCenters;
	
	/**
	 * Number of resource generators
	 */
	private int numResourceGenerators;

	/**
	 * Generic constructor for player
	 * 
	 * @param playerName
	 *            - name for the player
	 */
	public Player(String playerName)
	{
		this.playerName = playerName;
		units = new ArrayList<Unit>();
		buildings = new ArrayList<Building>();
		resources = 20;
		buildDelay = 10;
		numResourceGenerators = 0;
		numConstructionCenters = 0;
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(playerName);
		String[] selectionArgs = new String[1];
		selectionArgs[0] = playerName;
		Cursor cursor = gdb.query("Players", null, "NAME = ?", selectionArgs, null, null, null);
		boolean notEmpty = cursor.moveToFirst();
		if(notEmpty)
		{
			Log.v(null, "Entry found in Player database, using found player.");
			rowID = cursor.getPosition() + 1;
		}
		else
		{
			ContentValues playerCV = new ContentValues();
			playerCV.put("NAME", playerName);
			playerCV.put("RESOURCES", resources);
			rowID = gdb.insert("Players", null, playerCV);
		}
	}

	/**
	 * @return the player's name
	 */
	public String getPlayerName()
	{
		return playerName;
	}

	/**
	 * Add unit to player's units list
	 * @param unit - unit to associate
	 */
	public void associateUnitToPlayer(Unit unit)
	{
		units.add(unit);
	}

	/**
	 * Remove unit from player's units list
	 * @param unit - unit to be removed
	 */
	public void deassociateUnitFromPlayer(Unit unit)
	{
		units.remove(unit);
	}
	
	/**
	 * Add building to player's building list 
	 * @param building - building to associate
	 */
	public void associateBuildingToPlayer(Building building)
	{
		buildings.add(building);
	}

	/**
	 * Remove unit from player's units list
	 * @param building - building to remove
	 */
	public void deassociateBuildingFromPlayer(Building building)
	{
		buildings.remove(building);
	}
	
	/**
	 * @return the amount of resources the player has
	 */
	public int getResources()
	{
		return resources;
	}
	
	/**
	 * Sets the resources to the specified value
	 * @param value - new value for resources
	 */
	protected void setResources(int value)
	{
		resources = value;
	}
	
	/**
	 * Set the resources to the input value
	 */
	public void generateResources()
	{
		resources = resources + numResourceGenerators;
	}
	
	/**
	 * Spend resources
	 * @param value - number of resources to spend
	 * @return whether or not the specified amount of resources were able to be spent
	 */
	public boolean spendResources(int value)
	{
		if(resources - value >= 0)
		{
			resources = resources - value;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Returns the specified number of resources to the player
	 * @param value - number of resources to return
	 */
	public void returnResources(int value)
	{
		resources = resources + value;
	}
	
	/**
	 * @return the number of construction centers owned by the player
	 */
	public int getNumConstructionCenters()
	{
		return numConstructionCenters;
	}
	
	/**
	 * @return the number of resource generators owned by the player
	 */
	public int getNumResourceGenerators()
	{
		return numResourceGenerators;
	}
	
	/**
	 * Increases the count of construction centers
	 */
	public void increaseNumConstuctionCenters()
	{
		numConstructionCenters++;
	}
	
	/**
	 * Decreases the count of resource generators
	 */
	public void decreaseNumResourceGenerators()
	{
		numResourceGenerators--;
	}
	
	/**
	 * Decreases the count of construction centers
	 */
	public void decreaseNumConstuctionCenters()
	{
		numConstructionCenters--;
	}
	
	/**
	 * Increases the count of resource generators
	 */
	public void increaseNumResourceGenerators()
	{
		numResourceGenerators++;
	}
	
	/**
	 * @return the current build delay for the player
	 */
	public int getBuildDelay()
	{
		return buildDelay;
	}
	
	/**
	 * Updates the build delay
	 */
	public void updateBuildDelay()
	{
		if(buildDelay > 3)
		{
			buildDelay = buildDelay - numConstructionCenters;
		}	
	}
	
	/**
	 * @return the row id of the player
	 */
	public long getRowID()
	{
		return rowID;
	}
	
	/**
	 * @return the main base
	 */
	public Building getMainBase()
	{
		for(int i = 0; i < buildings.size() - 1; i++)
		{
			if(buildings.get(i).getType() == 1)
			{
				return buildings.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * @return a randomly selected construction center
	 */
	public Building getRandomConstructionCenter()
	{
		ArrayList<Building> constructionCenters = new ArrayList<Building>();
		
		for(int i = 0; i < buildings.size(); i++)
		{
			Building temp = buildings.get(i);
			if(temp.getType() == 2)
			{
				constructionCenters.add(temp);
			}
		}
		Random r = new Random();
		int arrayIndex = r.nextInt(constructionCenters.size());
		return constructionCenters.get(arrayIndex);
	}
	
	/**
	 * @return the size of the unit array
	 */
	public int getUnitArraySize()
	{
		return units.size();
	}
}
