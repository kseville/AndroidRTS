/**
 * Command package
 */
package com.rts.commandprocessor;

/**
 * Interface for a command - all required methods for a command
 * @author Korie Seville
 *
 */
public interface Command
{	
	/**
	 * Send command to command processor
	 */
	public void sendToCommandProcessor();
}
