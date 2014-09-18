/**
 * Application Framework package
 */
package com.rts.appframework;

import java.util.ArrayList;
import com.rts.database.GameDatabase;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Defines all the information and provides all methods and functions of a unit.
 * @author Korie Seville
 *
 */
public class Building
{
	//Stats
	private int type;	//1 = Main Base 2 = Construction Center 3 = Resource Generator
	String ownerID;	//String representation of the owner
	Player owner;
	int hp;
	int xposition;
	int yposition;
	@SuppressWarnings("unused")
	private int cost;
	boolean createAtStart;
	private Unit attackingUnit;
	private ArrayList<Unit> attackers;
	
	/**
	 * Cost of a construction center
	 */
	public final static int COST_CONSTRUCTION_CENTER = 30;
	/**
	 * Cost of a resource generator
	 */
	public final static int COST_RESOURCE_GENERATOR = 20;
	/**
	 * 
	 */
	public final static int BUILD_TIME_CONSTRUCTION_CENTER = 30;
	/**
	 * 
	 */
	public final static int BUILD_TIME_RESOURCE_GENERATOR = 20;
	
	//Database uniqueID
	long rowID = 0;
	
	/**
	 * Unit constructor. Sets the type of unit to be created and passes the parameters to the createUnit method
	 * Types are as follows:
	 * 1. Main Base
	 * 2. Construction Center
	 * 3. Resource Generator
	 * @param type - type of unit to create
	 * @param id - player that owns this unit
	 * @param x - x position
	 * @param y - y position
	 * @param createAtStart - create the building at the start of the game
	 */
	public Building(int type, Player id, int x, int y, boolean createAtStart)
	{
		this.type = type;
		owner = id;
		xposition = x;
		yposition = y;
		this.createAtStart = createAtStart;
		attackers = new ArrayList<Unit>();
		
		createBuilding(true);
	}
	
	/**
	 * Unit constructor. Sets the type of unit to be created and passes the parameters to the createUnit method
	 * Types are as follows:
	 * 1. Main Base
	 * 2. Construction Center
	 * 3. Resource Generator
	 * @param type - type of unit to create
	 * @param id - player that owns this unit
	 * @param createAtStart - create the building at the start of the game
	 * @throws Exception if the human player attempts to use this constructor.
	 */
	public Building(int type, Player id, boolean createAtStart) throws Exception
	{
		this.type = type; 
		owner = id;
		if(owner.getPlayerName().compareTo("AI") == 0)
		{
			xposition = 0;
			yposition = 9;
			this.createAtStart = createAtStart;
			createBuilding(true);
		}
		else
		{
			throw new Exception("Non AI Player cannot use this constructor. You MUST specify the x and y coordinates via the user's click");
		}
	}
	
	/**
	 * Constructor for when the buildings are loaded from the database of a saved game.
	 * @param type - building type
	 * @param id - owner of the building
	 * @param hp - building health
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @param rowID - database row id
	 */
	public Building(int type, Player id, int hp, int x, int y, long rowID)
	{
		this.type = type;
		owner = id;
		xposition = x;
		yposition = y;
		this.hp = hp;
		this.rowID = rowID;
		attackers = new ArrayList<Unit>();
		owner.associateBuildingToPlayer(this);
		if(type == 2)
			owner.increaseNumConstuctionCenters();
		else if(type == 3)
			owner.increaseNumResourceGenerators();
		
		Game.forceAddBuildingToGameMap(this, xposition, yposition);
	}
	
	/**
	 * Creates the unit with the parameters specified by the Unit constructor. 
	 * @param addToDB - denotes if the building is already in the database
	 */
	public void createBuilding(boolean addToDB)
	{
		if(type == 1)
		{
			hp = 100;
			ownerID = owner.getPlayerName();
		}
		else if(type == 2)
		{
			hp = 40;
			cost = COST_CONSTRUCTION_CENTER;
			ownerID = owner.getPlayerName();
			owner.increaseNumConstuctionCenters();
		}
		else if(type == 3)
		{
			hp = 30;
			cost = COST_RESOURCE_GENERATOR;
			ownerID = owner.getPlayerName();
			owner.increaseNumResourceGenerators();
		}
		
		if(addToDB)
		{
			SQLiteDatabase gdb = GameDatabase.getGameDatabase(ownerID);
			ContentValues buildingCV = new ContentValues();
			buildingCV.put("TYPE", type);
			buildingCV.put("HP", hp);
			buildingCV.put("XPOSITION", xposition);
			buildingCV.put("YPOSITION", yposition);
			buildingCV.put("OWNER", ownerID);
			rowID = gdb.insert("Buildings", null, buildingCV);
		}
		
		owner.associateBuildingToPlayer(this);
		Game game = Game.getInstance();
		game.addBuildingToGameMap(this);
	}
	
	/**
	 * @return type of unit
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Sets the x coordinate
	 * @param value - new x coordinate
	 */
	public void setX(int value)
	{
		xposition = value;
	}
	
	/**
	 * Sets the y coordinate
	 * @param value - new y coordinate
	 */
	public void sety(int value)
	{
		yposition = value;
	}
	
	/**
	 * @return the x coordinate
	 */
	public int getX()
	{
		return xposition;
	}
	
	/**
	 * @return the y coordinate
	 */
	public int getY()
	{
		return yposition;
	}
	
	/**
	 * @return the owner ID
	 */
	public String getOwnerID()
	{
		return owner.getPlayerName();
	}
	
	/**
	 * @return the building owner
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * @return the current hp
	 */
	public int getHP()
	{
		return hp;
	}
	
	/**
	 * @return the buildings database row id
	 */
	public Long getRowID()
	{
		return rowID;
	}

	/**
	 * Deals damage to building. 
	 * @param value - amount to decrease HP
	 * @return whether the building was destroyed or not
	 */
	public boolean decreaseHP(int value)
	{
		hp = hp - value;
		if(hp <= 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * @return the attacking unit
	 */
	public Unit getAttackingUnit()
	{
		return attackingUnit;
	}
	
	/**
	 * Destroy the building when the unit's health reaches 0
	 * @return number of rows affected
	 */
	public int destroy()
	{
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(ownerID);
		owner.deassociateBuildingFromPlayer(this);
		Game.removeFromGameMap(xposition, yposition);
		Unit unitToAddExperience = getAttackingUnit();
		unitToAddExperience.addExperience();
		int unitsToClear = attackers.size();
		if(type == 2)
		{
			owner.decreaseNumConstuctionCenters();
		}
		else
		{
			owner.decreaseNumResourceGenerators();
		}
		for(int i = 0; i < unitsToClear - 1; i++)
		{
			Unit unit = attackers.get(i);
			unit.clearDeploymentState();
		}
		if(getOwner().getUnitArraySize() == 0 && getOwner().buildings.size() == 0)
		{
			Game.endGame(owner);
		}
		return gdb.delete("Buildings","ROWID = " + Long.toString(rowID), null);		
		
	}
	
	/**
	 * Adds the specified unit to the list of attackers.
	 * @param unit - unit to add to attackers
	 */
	public void addAttackers(Unit unit)
	{
		attackers.add(unit);
	}
	
	/**
	 * Removes the specified unit from the list of attackers.
	 * @param unit - unit to remove from attackers
	 */
	public void removeAttackers(Unit unit)
	{
		attackers.remove(unit);
	}
	
	/**
	 * Sets the attacking unit
	 * @param unit - new attacking unit
	 */
	public void setAttackingUnit(Unit unit)
	{
		attackingUnit = unit;
	}
	
	/**
	 * Gets the unit by row id
	 * @param rowID - database rowid of the unit
	 * @return - the unit at the specified row id
	 */
	public static Building getUnitByRowID(long rowID)
	{
		Player activePlayer = Game.getActivePlayer();
		Player aiPlayer = Game.getAiPlayer();
		
		for(int i = 0; i < activePlayer.buildings.size(); i++)
		{
			Building buildingToCompare = activePlayer.buildings.get(i);
			if(buildingToCompare.getRowID() == rowID)
			{
				return buildingToCompare;
			}
		}
		
		for(int i = 0; i < aiPlayer.buildings.size(); i++)
		{
			Building buildingToCompare = aiPlayer.buildings.get(i);
			if(buildingToCompare.getRowID() == rowID)
			{
				return buildingToCompare;
			}
		}
		
		return null;
	}
}