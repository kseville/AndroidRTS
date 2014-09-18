/**
 * Command package
 */
package com.rts.commandprocessor;

import android.util.Log;

import com.rts.GameMap;
import com.rts.appframework.Building;
import com.rts.appframework.Game;
import com.rts.appframework.Player;

/**
 * Command to initiate the construction of a building
 * 
 * @author Korie Seville
 *
 */
public class ConstructBuildingCommand implements Command
{
	private int buildingType;
	private Player owner;
	private int delay;
	private Building sourceBuilding;
	private int x = -1; 
	private int y = -1;
	
	/**
	 * Constructor to create the building creation command 
	 * @param sourceBuilding - building that is constructing the building (will always be the player's main base)
	 * @param buildingType - type of building
	 * @param owner - building owner
	 */
	public ConstructBuildingCommand(Building sourceBuilding, int buildingType, Player owner)
	{
		this.buildingType = buildingType;
		this.owner = owner;
		this.sourceBuilding = sourceBuilding;
		if(buildingType == 2)
		{
			delay = Building.BUILD_TIME_CONSTRUCTION_CENTER;
		}
		else if(buildingType == 3)
		{
			delay = Building.BUILD_TIME_RESOURCE_GENERATOR;
		}
		sendToCommandProcessor();
	}
	
	/**
	 * @param sourceBuilding - building that is constructing the building (will always be the player's main base)
	 * @param buildingType - type of building
	 * @param owner - building owner
	 * @param x - x coordinate
	 * @param y - y coordinate
	 */
	public ConstructBuildingCommand(Building sourceBuilding, int buildingType, Player owner, int x, int y)
	{
		this.buildingType = buildingType;
		this.sourceBuilding = sourceBuilding;
		this.owner = owner;
		if(buildingType == 2)
		{
			delay = Building.BUILD_TIME_CONSTRUCTION_CENTER;
		}
		else if(buildingType == 3)
		{
			delay = Building.BUILD_TIME_RESOURCE_GENERATOR;
		}
		this.x = x;
		this.y = y;
		sendToCommandProcessor();
	}
	
	/**
	 * @return the delay
	 */
	public int getDelay()
	{
		return delay;
	}
	
	/**
	 * Sets the delay time
	 * @param value - delay time
	 */
	public void setDelay(int value)
	{
		delay = value;
	}
	
	/**
	 * @return the type of building to be created
	 */
	public int getType()
	{
		return buildingType;
	}
	
	/**
	 * @return the owner of the building
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * @return x coordinate
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * @return y coordinate
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 * @return the building constructing the new building
	 */
	public Building getSourceBuilding()
	{
		return sourceBuilding;
	}

	/**
	 * Send command to command processor
	 */
	@Override
	public void sendToCommandProcessor()
	{
		int cost = 0;
		if(buildingType == 2)
			cost = Building.COST_CONSTRUCTION_CENTER;
		else if(buildingType == 3)
			cost = Building.COST_RESOURCE_GENERATOR;
		
		if(owner.spendResources(cost))
		{
			CommandProcessor cp = CommandProcessor.getInstance();
			cp.addCommand(this);
		}
		else
		{
			if(owner == Game.getActivePlayer())
			{
				GameMap.gameMapActivity.runOnUiThread(new Runnable()
				{
				     public void run() 
				     {
				    	GameMap.gameMapActivity.displayToastMessageInGame("Insufficient resources to complete request.");
				     }
				});
			}
			else
			{
				Log.v(null, "AI Player: insufficient resources to build requested building.");
			}
			
			owner.returnResources(cost);
		}
	}
}
