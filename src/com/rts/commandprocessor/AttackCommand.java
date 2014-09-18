/**
 * Command package
 */
package com.rts.commandprocessor;

import com.rts.appframework.Unit;

/**
 * Command to have a unit attack another unit
 * @author Korie Seville
 *
 */
public class AttackCommand implements Command
{
	
	/**
	 * Unit that is doing the attacking
	 */
	private Unit source;
	
	/**
	 * Target of the attack
	 */
	private Unit target;
	
	/**
	 * Delay before processing command
	 */
	public int delay;
	
	/**
	 * Attack command constructor
	 * @param source - unit initiating the attack
	 * @param target - unit being attacked
	 * @param delay - time before the command will be executed
	 */
	public AttackCommand(Unit source, Unit target, int delay)
	{
		this.source = source;
		this.target = target;
		this.delay = delay;
		sendToCommandProcessor();
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
	
	/**
	 * @return the source unit (unit initiating the attack)
	 */
	public Unit getSourceUnit()
	{
		return source;
	}
	
	/**
	 * @return the targeted unit
	 */
	public Unit getTargetUnit()
	{
		return target;
	}
	
	/**
	 * @return the delay
	 */
	public int getDelay()
	{
		return delay;
	}
	
	/**
	 * Set the delay
	 * @param value - value to set delay to
	 */
	public void setDelay(int value)
	{
		delay = value;
	}
}
