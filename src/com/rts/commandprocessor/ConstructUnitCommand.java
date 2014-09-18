/**
 * Command package
 */
package com.rts.commandprocessor;

import android.util.Log;

import com.rts.GameMap;
import com.rts.appframework.Building;
import com.rts.appframework.Game;
import com.rts.appframework.Player;
import com.rts.appframework.Unit;

/**
 * Command that prompts the system to create a new unit at the specifie time.
 * 
 * @author Korie Seville
 *
 */
public class ConstructUnitCommand implements Command
{
	private int unitType;
	private int delay;
	private Player owner;
	private Building sourceBuilding;
	
	/**
	 * Constructor to create the building creation command 
	 * @param sourceBuilding - building that is creating this unit
	 * @param unitType - type of unit to be built
	 * @param owner - owner of the unit
	 */
	public ConstructUnitCommand(Building sourceBuilding, int unitType, Player owner)
	{
		this.sourceBuilding = sourceBuilding;
		this.unitType = unitType;
		this.owner = owner;
		delay = owner.getBuildDelay();
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
	 * @return the owner of the unit
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * @return the unit
	 */
	public int getUnitType()
	{
		return unitType;
	}
	
	/**
	 * @return the building responsible for the unit's construction
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
		if(unitType == 1)
			cost = Unit.COST_SCOUT;
		else if(unitType == 2)
			cost = Unit.COST_HIGH_POWER;
		else if(unitType == 3)
			cost = Unit.COST_HIGH_HP;
		
		if(owner.spendResources(cost) && owner.getUnitArraySize() <= 20)
		{
			CommandProcessor cp = CommandProcessor.getInstance();
			cp.addCommand(this);
		}
		else
		{
			if(owner == Game.getActivePlayer())
			{
				if(owner.getUnitArraySize() > 20)
				{
					GameMap.gameMapActivity.runOnUiThread(new Runnable()
					{
					     public void run() 
					     {
					    	GameMap.gameMapActivity.displayToastMessageInGame("Unable to complete request. Unit capacity is at maximum.");
					     }
					});
				}
				else
				{
					GameMap.gameMapActivity.runOnUiThread(new Runnable()
					{
					     public void run() 
					     {
					    	GameMap.gameMapActivity.displayToastMessageInGame("Insufficient resources to complete request.");
					     }
					});
				}
			}
			else
			{
				Log.v(null, "AI Player: insufficient resources to build requested unit.");
			}
			
			owner.returnResources(cost);
		}
	}
}
