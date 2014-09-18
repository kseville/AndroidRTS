/**
 * Command package
 */
package com.rts.commandprocessor;

import com.rts.appframework.Player;
import com.rts.appframework.Unit;

/**
 * Command to move a unit to a specified location
 * @author Korie Seville
 *
 */
public class MoveCommand implements Command
{
	/**
	 * Unit that will be moving
	 */
	private Unit source;
	
	/**
	 * X coordinate of the unit's current position
	 */
	private int sourcex;
	
	/**
	 * Y coordinate of the unit's current position
	 */
	private int sourcey;
	
	/**
	 * X coordinate of the destination of the unit
	 */
	private int destinationx;
	
	/**
	 * Y coordinate of the destination of the unit
	 */
	private int destinationy;
	
	/**
	 * Player requesting the command
	 */
	private Player player;
	
	/**
	 * Delay before processing command
	 */
	private int delay;
	
	/**
	 * Constructor for move commands
	 * @param player - player initiating the command
	 * @param movingUnit -  the unit to move
	 * @param delay - delay before executing command
	 * @param destinationx - destination x coordinate
	 * @param destinationy - destination y coordinate
	 */
	public MoveCommand(Player player, Unit movingUnit, int delay, int destinationx, int destinationy)
	{
		this.player = player;
		this.delay = delay;
		source = movingUnit;
		this.sourcex = movingUnit.getX();
		this.sourcey = movingUnit.getY();
		this.destinationx = destinationx;
		this.destinationy = destinationy;
		sendToCommandProcessor();
	}
	
	/**
	 * @return the unit to move
	 */
	public Unit getUnit()
	{
		return source;
	}
	
	/**
	 * @return the sourcex
	 */
	public int getSourcex()
	{
		return sourcex;
	}


	/**
	 * @return the sourcey
	 */
	public int getSourcey()
	{
		return sourcey;
	}

	/**
	 * @return the destinationx
	 */
	public int getDestinationx()
	{
		return destinationx;
	}

	/**
	 * @return the destinationy
	 */
	public int getDestinationy()
	{
		return destinationy;
	}
	
	/**
	 * @return the player requesting the command
	 */
	public Player getPlayer()
	{
		return player;
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
	 * Send command to commandQueue
	 */
	@Override
	public void sendToCommandProcessor()
	{
		CommandProcessor cp = CommandProcessor.getInstance();
		cp.addCommand(this);
	}
}
