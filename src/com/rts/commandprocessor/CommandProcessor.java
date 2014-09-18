/**
 * Command package
 */
package com.rts.commandprocessor;

import java.util.LinkedList;
import java.util.Queue;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rts.GameMap;
import com.rts.appframework.Building;
import com.rts.appframework.DefaultAI;
import com.rts.appframework.Game;
import com.rts.appframework.Player;
import com.rts.appframework.Unit;
import com.rts.database.GameDatabase;

/**
 * Queue and processes all commands sent by various classes.
 * @author Korie Seville
 *
 */
public class CommandProcessor
{
	/**
	 * Queue of commands to be executed
	 */
	private Queue<Command> commandQueue;
	/**
	 * Singleton instance of command processor
	 */
	public static CommandProcessor cp;
	
	private static Player activePlayer;
	private static Player aiPlayer;
	private static DefaultAI ai;
	
	/**
	 * Generic constructor for the command processor.
	 */
	private CommandProcessor()
	{
		commandQueue = new LinkedList<Command>();
	}
	
	/**
	 * @return the singleton commandQueue
	 */
	public static CommandProcessor getInstance()
	{
		if(cp == null)
		{
			cp = new CommandProcessor();
			activePlayer = Game.getActivePlayer();
			aiPlayer = Game.getAiPlayer();
			ai = DefaultAI.getInstance();
		}
		return cp;
	}
	
	/**
	 * Add new command to commandQueue
	 * @param command - new command to add to queue
	 */
	public void addCommand(Command command)
	{
		commandQueue.add(command);
	}

	/**
	 * Execute the command processor in a separate thread.
	 */
	@SuppressWarnings("unused")
	public void run()
	{
		//Generate Resources
		activePlayer.generateResources();
		aiPlayer.generateResources();
		
		//Trigger the AI
		ai.aiTrigger();
		
		//Execute the Queue
		int size = commandQueue.size();
		Log.v(null, "Queue length: " + Integer.toString(commandQueue.size()));
		for(int i = 0; i < size; i++)
		{
			//Steps: get command, determine type of command, execute command function
			Command c = commandQueue.poll();
			
			if(c instanceof MoveCommand) 
			{
				if(((MoveCommand) c).getDelay() == 0)
				{
					Unit unit = ((MoveCommand) c).getUnit();
					int destinationx = ((MoveCommand) c).getDestinationx();
					int destinationy = ((MoveCommand) c).getDestinationy();
					unit.move(destinationx, destinationy);
					GameMap.gameMapActivity.runOnUiThread(new Runnable()
					{
					     public void run() 
					     {
					    	GameMap.update();
					     }
					});
				}
				else
				{
					((MoveCommand) c).setDelay(((MoveCommand)c).getDelay() - 1);
					commandQueue.add(c);
				}	
			}
			else if(c instanceof AttackCommand)
			{
				if(((AttackCommand) c).getDelay() == 0)
				{
					Unit source = ((AttackCommand) c).getSourceUnit();
					Unit target = ((AttackCommand) c).getTargetUnit();
					if(source != null && target != null)
					{
						source.attack(source, target);
					}
				}
				else
				{
					((AttackCommand) c).setDelay(((AttackCommand) c).getDelay() - 1);
					commandQueue.add(c);
				}
				
			}
			
			else if(c instanceof AttackBuildingCommand)
			{
				if(((AttackBuildingCommand) c).getDelay() == 0)
				{
					Unit source = ((AttackBuildingCommand) c).getSourceUnit();
					Building target = ((AttackBuildingCommand) c).getTargetUnit();
					if(source != null && target != null)
					{
						source.attackBuilding(source, target);
					}
				}
				else
				{
					((AttackBuildingCommand) c).setDelay(((AttackBuildingCommand) c).getDelay() - 1);
					commandQueue.add(c);
				}
				
			}
			
			else if(c instanceof ConstructBuildingCommand)
			{
				if(((ConstructBuildingCommand) c).getDelay() == 0)
				{
					if(((ConstructBuildingCommand) c).getX() == -1)
					{
						try
						{
							Building building = new Building(((ConstructBuildingCommand) c).getType(), ((ConstructBuildingCommand) c).getOwner(), false);
							GameMap.gameMapActivity.runOnUiThread(new Runnable()
							{
							     public void run() 
							     {
							    	GameMap.update();
							     }
							});
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						Building building = new Building(((ConstructBuildingCommand) c).getType(), ((ConstructBuildingCommand) c).getOwner(), ((ConstructBuildingCommand) c).getX(), ((ConstructBuildingCommand) c).getY(), false);
					}
				}
				else
				{
					((ConstructBuildingCommand) c).setDelay(((ConstructBuildingCommand)c).getDelay() - 1);
					commandQueue.add(c);
				}
			}
			
			else if(c instanceof ConstructUnitCommand)
			{
				if(((ConstructUnitCommand) c).getDelay() == 0)
				{
					try
					{
						Unit unit = new Unit(((ConstructUnitCommand)c).getUnitType(), ((ConstructUnitCommand) c).getOwner(), true);
						GameMap.gameMapActivity.runOnUiThread(new Runnable()
						{
						     public void run() 
						     {
						    	GameMap.update();
						     }
						});
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					((ConstructUnitCommand) c).setDelay(((ConstructUnitCommand)c).getDelay() - 1);
					commandQueue.add(c);
				}
			}
		}
		
		GameMap.gameMapActivity.runOnUiThread(new Runnable()
		{
		     public void run() 
		     {
		    	GameMap.update();
		     }
		});
	}
	
	/**
	 * Clears out commands that pertain to the selected unit
	 * @param unit - unit to remove commands for
	 */
	public void clearUnitCommands(Unit unit)
	{
		int size = commandQueue.size();
		for(int i = 0; i < size; i++)
		{
			Command c = commandQueue.poll();
			
			if(c instanceof MoveCommand) 
			{
				Unit unitToCheck = ((MoveCommand) c).getUnit();
				if(unitToCheck == unit)
				{
					commandQueue.remove(c);
				}
				else
				{
					commandQueue.add(c);
				}
			}
			else if(c instanceof AttackCommand)
			{
				Unit unitToCheck = ((AttackCommand) c).getSourceUnit();
				if(unitToCheck == unit)
				{
					commandQueue.remove(c);
				}
				else
				{
					commandQueue.add(c);
				}
			}
			else
			{
				commandQueue.add(c);
			}
		}
	}
	
	/**
	 * Clears out commands that pertain to the selected building
	 * @param building - building to remove commands for
	 */
	/**
	 * Clears out commands that pertain to the selected unit
	 * @param building - unit to remove commands for
	 */
	public void clearBuildingCommands(Building building)
	{
		int size = commandQueue.size();
		for(int i = 0; i < size; i++)
		{
			Command c = commandQueue.poll();
			
			if(c instanceof ConstructUnitCommand) 
			{
				commandQueue.remove(c);
			}
			else if(c instanceof ConstructBuildingCommand)
			{
				Building buildingToCheck = ((ConstructBuildingCommand) c).getSourceBuilding(); 
				if(buildingToCheck == building)
				{
					commandQueue.remove(c);
				}
				else
				{
					commandQueue.add(c);
				}
			}
			else
			{
				commandQueue.add(c);
			}
		}
	}
	
	/**
	 * Saves all commands currently in the command queue to the database
	 */
	public void saveCommandsToDatabase()
	{
		SQLiteDatabase gdb = GameDatabase.getGameDatabase(activePlayer.getPlayerName());
		for(int i = 0; i < commandQueue.size(); i++)
		{
			Command c = commandQueue.poll();
			
			if(c instanceof MoveCommand) 
			{
				ContentValues moveCV = new ContentValues();
				moveCV.put("COMMANDTYPE", 1);
				moveCV.put("PLAYERMOVE", ((MoveCommand) c).getPlayer().getPlayerName());
				moveCV.put("MOVINGUNIT", ((MoveCommand) c).getUnit().getRowID());
				moveCV.put("MOVEDELAY", ((MoveCommand) c).getDelay());
				moveCV.put("X", ((MoveCommand) c).getDestinationx());
				moveCV.put("Y", ((MoveCommand) c).getDestinationy());
				gdb.insert("Commands", null, moveCV);
			}
			else if(c instanceof AttackCommand)
			{
				ContentValues attackCV = new ContentValues();
				attackCV.put("COMMANDTYPE", 2);
				attackCV.put("SOURCEATTACKER", ((AttackCommand) c).getSourceUnit().getRowID());
				attackCV.put("TARGET", ((AttackCommand) c).getTargetUnit().getOwnerID());
				attackCV.put("ATTACKDELAY", ((AttackCommand) c).getDelay());
				gdb.insert("Commands", null, attackCV);
			}
			
			else if(c instanceof AttackBuildingCommand)
			{
				ContentValues attackBuildingCV = new ContentValues();
				attackBuildingCV.put("COMMANDTYPE", 3);
				attackBuildingCV.put("SOURCEATTACKER", ((AttackBuildingCommand) c).getSourceUnit().getRowID());
				attackBuildingCV.put("TARGET", ((AttackBuildingCommand) c).getTargetUnit().getOwnerID());
				attackBuildingCV.put("ATTACKDELAY", ((AttackBuildingCommand) c).getDelay());
				gdb.insert("Commands", null, attackBuildingCV);
			}
			
			else if(c instanceof ConstructBuildingCommand)
			{
				ContentValues constructBuildingCV = new ContentValues();
				constructBuildingCV.put("COMMANDTYPE", 4);
				constructBuildingCV.put("CONSTRUCTIONTYPE", ((ConstructBuildingCommand) c).getType());
				constructBuildingCV.put("SOURCEBUILDING", ((ConstructBuildingCommand) c).getSourceBuilding().getRowID());
				constructBuildingCV.put("OWNER", ((ConstructBuildingCommand) c).getOwner().getPlayerName());
				gdb.insert("Commands", null, constructBuildingCV);
			}
			
			else if(c instanceof ConstructUnitCommand)
			{
				ContentValues constructUnitCV = new ContentValues();
				constructUnitCV.put("COMMANDTYPE", 5);
				constructUnitCV.put("CONSTRUCTIONTYPE", ((ConstructUnitCommand) c).getUnitType());
				constructUnitCV.put("SOURCEBUILDING", ((ConstructUnitCommand) c).getSourceBuilding().getRowID());
				constructUnitCV.put("OWNER", ((ConstructUnitCommand) c).getOwner().getPlayerName());
				gdb.insert("Commands", null, constructUnitCV);
			}
		}
		Log.v(null, "Command queue written to database.");
	}
}