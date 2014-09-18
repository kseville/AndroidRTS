/**
 * Dialog Fragment package
 */
package com.rts.dailogFragments;

import com.rts.R.string;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Creates a dialog fragment if the profile name requested is taken
 * @author Korie Seville
 *
 */
public class NameExistsDF extends DialogFragment
{
	/**
	 * Actions that will be taken once the dialog fragment is created
	 * @param savedInstanceState - current instance 
	 * @return the dialog fragment
	 */
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		// Use the Builder class for convenient dialog construction
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setMessage(string.name_exists)
	          .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
	          {
	              public void onClick(DialogInterface dialog, int id) 
	              {
	            	  //Do nothing, just close the alert.
	              }
	          });
    
	   // Create the AlertDialog object and return it
	   return builder.create();
	}
}