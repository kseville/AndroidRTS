/**
 * 
 */
package com.rts.appframework;

import java.util.LinkedList;
import java.util.Queue;

import com.rts.GameMap;
import com.rts.commandprocessor.AttackBuildingCommand;
import com.rts.commandprocessor.AttackCommand;
import com.rts.commandprocessor.CommandProcessor;
import com.rts.commandprocessor.MoveCommand;
import com.rts.database.GameDatabase;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Defines all the information and provides all methods and functions of a unit.
 * @author Korie Seville
 *
 */
public class Unit
{
	//Unit Stats
	private int type;	//1 = balanced 2 = low hp high power 3 = low power high hp
	private String ownerID;	//String representation of the owner
	private Player owner;
	private int hp;
	private int power;
	private int experience;
	private int level;
	private int xposition;
	private int yposition;
	private int cost;
	private Unit target;
	private Building buildingTarget;
	private Unit attackingUnit;
	private Queue<Unit> attackers;
	private boolean buildingAttacker;
	private boolean deployed;
	private boolean hasTarget;
	private int destinationx;
	private int destinationy;
	
	//Number of experience points to progress to each level
	private final int LEVEL2 = 3;
	private final int LEVEL3 = 6;
	private final int LEVEL4 = 9;
	private final int LEVEL5 = 12;
	
	/**
	 * Cost of a scout unit
	 */
	public final static int COST_SCOUT = 2;
	/**
	 * Cost of a high power unit
	 */
	public final static int COST_HIGH_POWER = 5;
	/**
	 * Cost of a high HP unit
	 */
	public final static int COST_HIGH_HP = 3;
	
	//Database uniqueID
	long rowID = 0;
	
	/**
	 * Unit constructor. Sets the type of unit to be created and passes the parameters to the createUnit method
	 * @param type - type of unit to create
	 * @param id - player that owns this unit
	 * @param noMoveDelay - whether or not the delay for the construction of the unit should be enforced
	 */
	public Unit(int type, Player id, boolean noMoveDelay)
	{
		if(id.units.size() >= 20)
		{
			if(id.getPlayerName().compareTo("AI") == 0)
			{
				Log.v(null, "Unable to build unit. Unit capacity is at maximum.");
			}
			else
			{
				GameMap.gameMapActivity.runOnUiThread(new Runnable()
				{
				     public void run() 
				     {
				    	GameMap.gameMapActivity.displayToastMessageInGame("Unable to build unit. Unit capacity is at maximum.");
				     }
				});
			}
			
			if(type == 1)
				cost = COST_SCOUT;
			else if(type == 2)
				cost = COST_HIGH_POWER;
			else
				cost = COST_HIGH_HP;
			
			owner.returnResources(cost);
			
			return;
		}
		else
		{
			this.type = type;
			owner = id;
			attackers = new LinkedList<Unit>();
			buildingAttacker = false;
			deployed = false;
			createUnit(noMoveDelay, true);
		}
	}
	

	/**
	 * Constructor used when a unit is read in from the database (when resuming a game)
	 * @param type - unit type
	 * @param id - owner
	 * @param noMoveDelay - indicates whether or not the movement of the unit should be delayed
	 * @param rowID - rowID of the unit
	 * @param hp - hp of the unit
	 * @param power = power of the unit
	 * @param experience - experience of the unit
	 * @param level - level of the unit
	 * @param xposition - x coordinate
	 * @param yposition - y coordinate
	 */
	public Unit(int type, Player id, boolean noMoveDelay, long rowID, int hp, int power, int experience, int level, int xposition, int yposition)
	{
		this.type = type;
		owner = id;
		attackers = new LinkedList<Unit>();
		buildingAttacker = false;
		deployed = false;
		this.rowID = rowID;
		this.hp = hp;
		this.power = power;
		this.level = level;
		this.experience = experience;
		this.xposition = xposition;
		this.yposition = yposition;
		ownerID = owner.getPlayerName();
		owner.associateUnitToPlayer(this);
		Game.forceAddUnitToGameMap(this, xposition, yposition);
	}
	
	/**
	 * Creates the unit with the parameters specified by the Unit constructor. 
	 * @param noMoveDelay - whether or not the movement delay should be enforced
	 * @param addToDB - denotes if the unit is already in the database
	 */
	public void createUnit(boolean noMoveDelay, boolean addToDB)
	{	
		if(type == 1)
		{
			hp = 20;
			power = 4;
			experience = 0;
			level = 1;
			xposition = -1;
			yposition = -1;
			cost = COST_SCOUT;
			ownerID = owner.getPlayerName();
		}
		else if(type == 2)
		{
			hp = 10;
			power = 8;
			experience = 0;
			level = 1;
			xposition = -1;
			yposition = -1;
			cost = COST_HIGH_POWER;
			ownerID = owner.getPlayerName();
		}
		else if(type == 3)
		{
			hp = 30;
			power = 2;
			experience = 0;
			level = 1;
			xposition = -1;
			yposition = -1;
			cost = COST_HIGH_HP;
			ownerID = owner.getPlayerName();
		}
		
		if(addToDB)
		{
			SQLiteDatabase gdb = GameDatabase.getGameDatabase(ownerID);
			ContentValues unitCV = new ContentValues();
			unitCV.put("TYPE", type);
			unitCV.put("HP", hp);
			unitCV.put("POWER", power);
			unitCV.put("EXPERIENCE", experience);
			unitCV.put("LEVEL", level);
			unitCV.put("XPOSITION", xposition);
			unitCV.put("YPOSITION", yposition);
			unitCV.put("OWNER", ownerID);
			rowID = gdb.insert("Units", null, unitCV);
		}
		
		owner.associateUnitToPlayer(this);
		
		if(owner.getPlayerName().compareTo("AI") == 0)
			move(0,8);
		else
			move(0,0);
	}
	
	/**
	 * Method that a unit will use to attack another unit
	 * @param attacker - unit attacking
	 * @param target - target of the attack
	 * @return if the attacking unit was destroyed or not
	 */
	@SuppressWarnings("unused")
	public boolean attack(Unit attacker, Unit target)
	{
		Log.v(null, "Attacking Unit");
		CommandProcessor cp = CommandProcessor.getInstance();
		cp.clearUnitCommands(attacker);
		cp.clearUnitCommands(target);
		
		double critical = Math.random();
		boolean targetDestroyed = false;
		boolean attackerDestroyed = false;
		attacker.setAttackingUnit(target);
		target.setAttackingUnit(attacker);
		
		if(critical <= .2)
		{
			targetDestroyed = target.decreaseHP(attacker.getPower() + 2);
		}
		else
		{
			targetDestroyed = target.decreaseHP(attacker.getPower());
		}
		
		if(!targetDestroyed)
		{
			critical = Math.random();
			
			if(critical <= .2)
			{
				attackerDestroyed = attacker.decreaseHP(target.getPower() + 2);
			}
			else
			{
				attackerDestroyed = attacker.decreaseHP(target.getPower());
			}
			
			if(attackerDestroyed)
			{
				attacker.destroy();
				attacker = attackers.peek();
				attackerDestroyed = true;
				AttackCommand ac = new AttackCommand(attacker, target, 1);
			}
			else
			{
				AttackCommand ac = new AttackCommand(attacker, target, 1);
			}
		}
		else
		{
			target.destroy();
		}
		
		if(attackerDestroyed = true)
			return true;
		else
			return false;
	}
	
	/**
	 * Method that a unit will use to attack a building
	 * @param attacker - unit attacking
	 * @param target - target of the attack
	 */
	@SuppressWarnings("unused")
	public void attackBuilding(Unit attacker, Building target)
	{
		Log.v(null, "Attacking Building");
		CommandProcessor cp = CommandProcessor.getInstance();
		cp.clearUnitCommands(attacker);
		target.setAttackingUnit(attacker);
		
		double critical = Math.random();
		boolean isDestroyed = false;
		
		if(critical <= .2)
		{
			isDestroyed = target.decreaseHP(attacker.getPower() + 2);
		}
		else
		{
			isDestroyed = target.decreaseHP(attacker.getPower());
		}
		
		if(isDestroyed)
		{
			target.destroy();
		}
		else
		{
			AttackBuildingCommand abc = new AttackBuildingCommand(attacker, target, 0);
		}
	}
	
	/**
	 * Move unit from place to place
	 * @param destinationx  - destination x coordinate
	 * @param destinationy  - destination y coordinate
	 */
	@SuppressWarnings("unused")
	public void move(int destinationx, int destinationy)
	{
		Game game = Game.getInstance();
		this.destinationx = destinationx;
		this.destinationy = destinationy;
		int xdistance = destinationx - xposition;
		int ydistance = destinationy - yposition;
		
		if(xposition == -1 && yposition == -1)
		{
			game.addToGameMap(this, xposition, yposition, destinationx, destinationy);
			return;
		}
		
		if(Math.abs(xdistance) > Math.abs(ydistance))
		{
			if(xdistance != 0)
			{
				if(xdistance < 0)
				{
					game.addToGameMap(this, xposition, yposition, xposition - 1, yposition);
				}
				else
				{
					game.addToGameMap(this, xposition, yposition, xposition + 1, yposition);
				}
			}
		}
		else
		{
			if(ydistance != 0)
			{
				if(ydistance < 0)
				{
					game.addToGameMap(this, xposition, yposition, xposition, yposition - 1);
				}
				else
				{
					game.addToGameMap(this, xposition, yposition, xposition, yposition + 1);
				}
			}
		}
		
		
		if(target != null)
		{
			if(Math.abs(yposition - target.getY()) <=1 && Math.abs(xposition - target.getX()) <= 1)
			{
				attack(this, target);
			}
			else
			{
				int y;
				if(target.getY() <= 1)
				{
					y = 1;
				}
				else
				{
					y = target.getY() - 1;
				}
				
				if(Game.spaceIsClear(y, target.getX()))
				{
					MoveCommand m = new MoveCommand(owner, this, 1, destinationx, destinationy);
				}
				else
				{
					findClearSpace();
					MoveCommand m = new MoveCommand(owner, this, 1, destinationx, destinationy);
				}
			}
		}
		else if(buildingTarget != null)
		{
			if(Math.abs(yposition - buildingTarget.getY()) <= 1 && Math.abs(xposition - buildingTarget.getX()) <= 1)
			{
				attackBuilding(this, buildingTarget);
			}
			else
			{
				int y;
				if(buildingTarget.getY() <= 1)
				{
					y = 1;
				}
				else
				{
					y = buildingTarget.getY() - 1;
				}
				
				if(Game.spaceIsClear(y, buildingTarget.getX()))
				{
					MoveCommand m = new MoveCommand(owner, this, 1, destinationx, destinationy);
				}
				else
				{
					findClearSpace();
					MoveCommand m = new MoveCommand(owner, this, 1, destinationx, destinationy);
				}
			}
		}
		else if(xdistance != 0 || ydistance != 0)
		{
			MoveCommand m = new MoveCommand(owner, this, 1, destinationx, destinationy);
		}
	}
	
	/**
	 * Issue move command to the command processor
	 * @param source - attacking unit
	 * @param target - target unit 
	 * @param delay - time before the command will be executed
	 */
	public void issueAttackCommand(Unit source, Unit target, int delay)
	{
		AttackCommand attack = new AttackCommand(source, target, delay);
		attack.sendToCommandProcessor();
	}
	
	/**
	 * Destroy the unit when the unit's health reaches 0
	 * @return number of rows affected
	 */
	public int destroy()
	{
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(ownerID);
		CommandProcessor cp = CommandProcessor.getInstance();
		cp.clearUnitCommands(this);
		owner.deassociateUnitFromPlayer(this);
		Game.removeFromGameMap(xposition, yposition);
		GameMap.gameMapActivity.runOnUiThread(new Runnable()
		{
		     public void run() 
		     {
		    	GameMap.update();
		     }
		});
		Unit unitToAddExperience = getAttackingUnit();
		unitToAddExperience.addExperience();
		for(int i = 0; i < attackers.size(); i++)
		{
			Unit unit = attackers.poll();
			unit.clearDeploymentState();
		}
		if(owner.getUnitArraySize() == 0 && owner.buildings.size() == 0)
		{
			Game.endGame(owner);
		}
		return gdb.delete("Units","ROWID = " + Long.toString(rowID), null);		
		
	}
	
	/**
	 * Add experience to unit and check to see if the unit is ready to level up
	 */
	public void addExperience()
	{
		experience++;
		
		if(this.level == 1)
		{
			if(experience >= LEVEL2)
			{
				level = 2;
				power++;
			}
		}
		else if(this.level == 2)
		{
			if(experience >= LEVEL3)
			{
				level = 3;
				power++;
			}
		}
		else if(this.level == 3)
		{
			if(experience >= LEVEL4)
			{
				level = 4;
				power++;
			}
		}
		else if(this.level == 4)
		{
			if(experience >= LEVEL5)
			{
				level = 5;
				power++;
			}
		}
	}
	
	/**
	 * @return type of unit
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Set the x coordinate of the unit
	 * @param value - new x coordinate
	 */
	public void setX(int value)
	{
		xposition = value;
	}
	
	/**
	 * Set the y coordinate of the unit
	 * @param value - new y coordinate
	 */
	public void sety(int value)
	{
		yposition = value;
	}
	
	/**
	 * @return the x coordinate of the unit
	 */
	public int getX()
	{
		return xposition;
	}
	
	/**
	 * @return the y coordinate of the unit
	 */
	public int getY()
	{
		return yposition;
	}
	
	/**
	 * @return the database row id of the unit
	 */
	public long getRowID()
	{
		return rowID;
	}
	
	/**
	 * @return the string representation of the unit's owner
	 */
	public String getOwnerID()
	{
		return ownerID;
	}
	
	/**
	 * @return the unit's owner
	 */
	public Player getOwner()
	{
		return owner;
	}
	
	/**
	 * @return the unit's current target
	 */
	public Unit getTarget()
	{
		return target;
	}
	
	/**
	 * @return target building
	 */
	public Building getTargetBuilding()
	{
		return buildingTarget;
	}
	
	/**
	 * Sets the target of the attacking unit to the specified unit
	 * @param unit - new target
	 */
	public void setTarget(Unit unit)
	{
		target = unit;
		unit.addAttackers(this);
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
	 * Gets a unit by its database row id
	 * @param rowID - row id of the unit
	 * @return the unit at the specified row id
	 */
	public static Unit getUnitByRowID(long rowID)
	{
		Player activePlayer = Game.getActivePlayer();
		Player aiPlayer = Game.getAiPlayer();
		
		for(int i = 0; i < activePlayer.getUnitArraySize(); i++)
		{
			Unit unitToCompare = activePlayer.units.get(i);
			if(unitToCompare.getRowID() == rowID)
			{
				return unitToCompare;
			}
		}
		
		for(int i = 0; i < aiPlayer.getUnitArraySize(); i++)
		{
			Unit unitToCompare = aiPlayer.units.get(i);
			if(unitToCompare.getRowID() == rowID)
			{
				return unitToCompare;
			}
		}
		
		return null;
	}
	
	/**
	 * @return whether or not the unit is a building attacker or not
	 */
	public boolean isBuildingAttacker()
	{
		return buildingAttacker;
	}
	
	/**
	 * Denotes the unit as a building attacker (it will ignore units and go for the enemy player's buildings
	 * @param value - whether the units is a building attacker or not
	 */
	public void setBuildingAttacker(boolean value)
	{
		buildingAttacker = value;
	}
	
	/**
	 * @return whether the unit is deployed or not
	 */
	public boolean isDeployed()
	{
		return deployed;
	}
	
	/**
	 * Set the deployed status of the unit
	 * @param value - whether the unit is deployed or not
	 */
	public void setDeployed(boolean value)
	{
		deployed = value;
	}
	
	/**
	 * Notes that the unit is currently idle (not attacking or moving to an enemy unit)
	 */
	public void clearDeploymentState()
	{
		deployed = false;
		target = null;
	}
	
	/**
	 * @return the unit's current HP
	 */
	public int getHP()
	{
		return hp;
	}
	
	/**
	 * Decrease the unit's HP and determine if it is still alive after the attack
	 * @param value = value to decrease the HP by
	 * @return whether the unit is destroyed or not
	 */
	public boolean decreaseHP(int value)
	{
		hp = hp - value;
		if(hp <= 0)
		{
			destroy();
			return true;
		}
		return false;
	}
	
	/**
	 * @return the unit's power
	 */
	public int getPower()
	{
		return power;
	}
	
	/**
	 * @return the unit's current level
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * @return the unit's current experience
	 */
	public int getExperience()
	{
		return experience;
	}
	
	/**
	 * @return whether or not the unit has an attacking target 
	 */
	public boolean hasTarget()
	{
		return hasTarget;
	}
	
	/**
	 * Set if the unit has a current target
	 * @param value - Does the unit have a current target?
	 */
	public void setHasTarget(boolean value)
	{
		hasTarget = value;
	}
	
	/**
	 * @return the attacking unit
	 */
	public Unit getAttackingUnit()
	{
		return attackingUnit;
	}
	
	/**
	 * Set the attacking unit
	 * @param unit - attacking unit
	 */
	public void setAttackingUnit(Unit unit)
	{
		attackingUnit = unit;
	}
	
	/**
	 * Defines the unit's building target
	 * @param target - building to be targeted
	 */
	public void setBuildingTarget(Building target)
	{
		buildingTarget = target;
	}
	
	/**
	 * Finds the nearest open space to the unit's desired position
	 */
	public void findClearSpace()
	{
		for(int i = destinationy; i < 10; i++)
		{
			for(int j = destinationx; j < 10; j++)
			{
				if(Game.spaceIsClear(j, i))
				{
					destinationx = j;
					destinationy = i;
					return;
				}
			}
			
			for(int j = destinationx; j >= 0; j--)
			{
				if(Game.spaceIsClear(j, i))
				{
					destinationx = j;
					destinationy = i;
					return;
				}
			}
		}
		
		for(int i = destinationy; i >= 0; i--)
		{
			for(int j = destinationx; j < 10; j++)
			{
				if(Game.spaceIsClear(j, i))
				{
					destinationx = j;
					destinationy = i;
					return;
				}
			}
			
			for(int j = 0; j < destinationx; j++)
			{
				if(Game.spaceIsClear(j, i))
				{
					destinationx = j;
					destinationy = i;
					return;
				}
			}
		}
		
	}
}