/**
 * Application framework package
 */
package com.rts.appframework;

import com.rts.commandprocessor.CommandProcessor;

/**
 * Internal clock class for the game. This will bind with the clock service and monitor the time.
 * @author Korie Seville
 *
 */
public class Clock extends Thread
{
	private boolean stopped; 
	
	/**
	 * Clock constructor
	 */
	public Clock()
	{
		stopped = false;
	}
	
	/**
	 * Threaded clock process - executes all commands in the command processor
	 */
	@Override
	public void run()
	{
		while(!stopped)
		{
			CommandProcessor cp = CommandProcessor.getInstance();
			cp.run();
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		return;
	}
	
	/**
	 * Stops the game clock by telling the currently running thread to suicide
	 * @throws InterruptedException if the thread is in interrupt state
	 */
	public void stopClock() throws InterruptedException
	{
		Thread.currentThread().join(10);
		stopped = true;
	}
}
