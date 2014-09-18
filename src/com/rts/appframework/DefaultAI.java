/**
 * Application Framework package
 */
package com.rts.appframework;

import java.util.Random;

import android.util.Log;

import com.rts.MainMenu;
import com.rts.commandprocessor.ConstructBuildingCommand;
import com.rts.commandprocessor.ConstructUnitCommand;
import com.rts.commandprocessor.MoveCommand;

/**
 * AI implementation
 * @author Korie Seville
 *
 */
public class DefaultAI implements AI
{
	/**
	 * Selected difficulty. This controls the build rates of the AI. Mostly changes build rate and resources available to AI
	 * Difficulty Levels:
	 * 1 - Easy
	 * 2 - Medium
	 * 3 - Hard
	 */
	public int difficulty;
	
	/**
	 * Combat style chosen. This represents the build priorities and attack priority of the AI. Changes amount of units created prior to engaging the enemy
	 * Combat Styles:
	 * 1 - Defend
	 * 2 - Balanced
	 * 3 - Blitz
	 */
	public int combatStyle;
	
	/**
	 * Build rate of the AI. Controls the number of second delay per creation of create unit command.
	 */
	public int buildRate;
	
	/**
	 * Singleton AI instance
	 */
	public static DefaultAI ai;
	
	/**
	 * AI player created to assign units to
	 */
	public static Player aiPlayer;
	
	private int unitCreationTimer;
	private int triggerDeployUnitsRunCount;
	private int deployUnitsAtTime;
	private int triggerBuildBuildingsRunCount;
	private int buildBuildingsAtTime;
	
	/**
	 * Generic constructor for DefaultAI
	 */
	private DefaultAI()
	{
		aiPlayer = new Player("AI");
		triggerDeployUnitsRunCount = 0;
		triggerBuildBuildingsRunCount = 0;
		buildBuildingsAtTime = 30;
		deployUnitsAtTime = 30;
		difficulty = MainMenu.getDifficulty();
		unitCreationTimer = 0;
		if(difficulty == 1)
		{
			buildRate = 10;
		}
		else if(difficulty == 2)
		{
			buildRate = 7;
		}
		else
		{
			buildRate = 4;
		}
		
		combatStyle = MainMenu.getCombatStyle();
	}
	
	/**
	 * @return the AI instance
	 */
	public static DefaultAI getInstance()
	{
		if(ai == null)
		{
			ai = new DefaultAI();
		}
		return ai;
	}
	
	/**
	 * @return the active AI player
	 */
	public Player getAiPlayer()
	{
		return aiPlayer;
	}
	
	/**
	 * Trigger the AI to perform it's tasks every clock tick.
	 */
	public void aiTrigger()
	{
		buildUnits();
		
		if(triggerBuildBuildingsRunCount == buildBuildingsAtTime)
		{
			Log.v(null, "Build Buildings AI routine executed");
			buildBuildings();
			
			Random r = new Random();
			int minWaitTime;
			int maxWaitTime;
			if(difficulty == 1)
			{
				maxWaitTime = 120;
				minWaitTime = 45;
			}
			else if(difficulty == 2)
			{
				maxWaitTime = 45;
				minWaitTime = 30;
			}
			else
			{
				maxWaitTime = 30;
				minWaitTime = 10;
			}
			int temp = 0;
			do{
				temp = r.nextInt(maxWaitTime + 1);
			}while(temp  >= minWaitTime && temp <= maxWaitTime);
			triggerBuildBuildingsRunCount = 0;
		}
		else
		{
			triggerBuildBuildingsRunCount++;
		}
		
		if(aiPlayer.getUnitArraySize() >= 1)
		{
			selectAttackers();
		}
		
		if(triggerDeployUnitsRunCount == deployUnitsAtTime)
		{
			Log.v(null, "Deploy Units AI routine executed");
			deployUnits();
			Random r = new Random();
			int temp = 0;
			do
			{
				temp = r.nextInt(30);
			}while(temp < 10);
			deployUnitsAtTime = temp;
			triggerDeployUnitsRunCount = 0;
		}
		else
		{
			triggerDeployUnitsRunCount++;
		}
		
	}

	/**
	 * Select a target for each AI unit.
	 */
	private void selectAttackers()
	{
		Player activePlayer = Game.getActivePlayer();
		
		//Set a target for each unit (depending on if it is a building attacker or not)
		int aiArraySize = aiPlayer.getUnitArraySize();
		
		Random r = new Random();
		
		for(int i = 0; i < aiArraySize; i++)
		{
			Unit aiUnit = aiPlayer.units.get(i);
			if(!aiUnit.isDeployed())
			{
				if(activePlayer.getUnitArraySize() == 0)
				{
					int humanBuildingIndex = r.nextInt(activePlayer.buildings.size());
					Building humanBuilding;
					
					humanBuilding = activePlayer.buildings.get(humanBuildingIndex);
					aiUnit.setBuildingAttacker(true);
					aiUnit.setBuildingTarget(humanBuilding);
					humanBuilding.addAttackers(aiUnit);
				}
				else
				{
					int humanIndex = r.nextInt(activePlayer.getUnitArraySize());
					Unit humanUnit;
					
					humanUnit = activePlayer.units.get(humanIndex);
					aiUnit.setTarget(humanUnit);
					humanUnit.addAttackers(aiUnit);
				}
			}
		}
	}
	
	/**
	 * Deploy the specified number of units (depending on combatStyle) to their targets and attack.
	 */
	@SuppressWarnings("unused")
	public void deployUnits()
	{
		int numUnitsToDeploy = 0;
		
		if(combatStyle == 1)
		{
			numUnitsToDeploy = (int) aiPlayer.getUnitArraySize() / 4;
		}
		else if (combatStyle == 2)
		{
			numUnitsToDeploy = (int) aiPlayer.getUnitArraySize() / 2;
		}
		else
		{
			numUnitsToDeploy = aiPlayer.getUnitArraySize();
		}
		
		Random r = new Random();
		
		for(int i = 0; i < numUnitsToDeploy-1; i++)
		{
			int tries = 0;
			int index = 0;
			Unit unitToDeploy = null;
			do{
				Unit checkDeployed; 
				if(tries == 3)
					break;
				
				index = r.nextInt(aiPlayer.getUnitArraySize());
				
				if(index == 0)
					checkDeployed = aiPlayer.units.get(0);
				else
					checkDeployed = aiPlayer.units.get(index - 1);
				
				if(!checkDeployed.isDeployed() && checkDeployed.getTarget() != null || !checkDeployed.isDeployed() && checkDeployed.getTargetBuilding() != null)
					unitToDeploy = checkDeployed;

				tries++;
			}while(unitToDeploy == null);
			
			if(unitToDeploy == null)
			{
				for(int j = 0; j < aiPlayer.getUnitArraySize()-1; j++)
				{
					if(!aiPlayer.units.get(j).isDeployed() && aiPlayer.units.get(j).getTarget() != null || !aiPlayer.units.get(j).isDeployed() && aiPlayer.units.get(j).getTargetBuilding() != null)
					{
						unitToDeploy = aiPlayer.units.get(j);
					}					
				}
			}
			
			if(unitToDeploy == null)		//All units are deployed
			{
				return;
			}
			unitToDeploy.setDeployed(true);
			if(unitToDeploy.isBuildingAttacker())
			{
				Building target = unitToDeploy.getTargetBuilding();
				MoveCommand mc = new MoveCommand(aiPlayer, unitToDeploy, i, target.getX(), target.getY() + 1);
			}
			else
			{
				Unit target = unitToDeploy.getTarget();
				MoveCommand mc = new MoveCommand(aiPlayer, unitToDeploy, i, target.getX(), target.getY() + 1);
			}
			
		}
		Log.v(null, "Number AI Units to deploy: " + numUnitsToDeploy);
	}

	@SuppressWarnings("unused")
	private void buildBuildings()
	{
		Game game = Game.getInstance();
		
		for(int i = 0; i < 10; i++)
		{
			Object prospectiveBuildSpot = game.getObjectAtPosition(9,i);
			
			if(aiPlayer.buildings.size() == 10)
			{
				return; 
			}
			
			if(prospectiveBuildSpot == null && aiPlayer.getMainBase() != null)
			{
				Random r = new Random();
				int buildingType = 0;
				while(buildingType == 0 || buildingType == 1)
				{
					buildingType = r.nextInt(4);
				}
				Player aiPlayer = Game.getAiPlayer();
				
				ConstructBuildingCommand cbc = new ConstructBuildingCommand(aiPlayer.getMainBase(), buildingType, aiPlayer, i, 9);
				return;
			}
		}
	}

	/**
	 * Builds the number of units based on build rate
	 */
	@SuppressWarnings("unused")
	private void buildUnits()
	{
		if(unitCreationTimer == buildRate)
		{
			if(aiPlayer.getNumConstructionCenters() > 0)
			{
				Random r = new Random();
				int unitType = 0;
				while(unitType < 1)
				{
					unitType = r.nextInt(4);
				}
				ConstructUnitCommand uc = new ConstructUnitCommand(aiPlayer.getRandomConstructionCenter(), unitType, aiPlayer);
				unitCreationTimer = 0;
			}
		}
		else
		{
			unitCreationTimer++;
		}
	}
	
	/**
	 * Sets the game difficulty to the specified value
	 * @param value - new difficulty
	 */
	public void setDifficulty(int value)
	{
		difficulty = value;
	}
	
	/**
	 * Set the combat style of the game to the specified value
	 * @param value - new combatStyle
	 */
	public void setCombatStyle(int value)
	{
		combatStyle = value;
	}
	
	/**
	 * @return the current difficulty
	 */
	public int getDifficulty()
	{
		return difficulty;
	}
	
	/**
	 * @return the current combat style
	 */
	public int getCombatStyle()
	{
		return combatStyle;
	}
}