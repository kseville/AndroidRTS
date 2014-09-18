/**
 * Dialog Fragment package
 */
package com.rts.dailogFragments;

import com.rts.MainMenu;
import com.rts.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author Korie Seville
 *
 */
public class SavedGameDialogFragment extends DialogFragment
{
	/**
	 * Actions taken when the dialog fragment is created
	 * @param savedInstanceState - instance stated
	 * @return the dialog fragment
	 */
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setMessage("You currently have a saved game. Would you like to resume your saved game or start a new one? \n\n WARNING: Starting a new game will overwrite your current saved game!")
	          .setPositiveButton(R.string.dialogfragment_startnewgame, new DialogInterface.OnClickListener() 
	          {
	              public void onClick(DialogInterface dialog, int id) 
	              {
	            	  MainMenu.mainMenu.responseFromDF(false);
	              }
	          })
	    
	    	.setNegativeButton(R.string.dialogfragment_resumesavedgame, new DialogInterface.OnClickListener() 
	    	{
	    		public void onClick(DialogInterface dialog, int id) 
	    		{
	    			 MainMenu.mainMenu.responseFromDF(true);
	    		}
	    	});
    
	   // Create the AlertDialog object and return it
	   return builder.create();
	}
}
