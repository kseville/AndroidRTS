/**
 * 
 */
package com.rts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;

/**
 * Fragment back end for Tab 1 of UnitList
 * 
 * Code taken from:
 * http://www.androidbegin.com/tutorial/implementing-fragment-tabs-in-android/
 * on Oct. 16, 2013 Copyright © 2013. AndroidBegin.com. All rights reserved.
 * Privacy Policy. Creative Commons License AndroidBegin.com content is licensed
 * under a Creative Commons Attribution-ShareAlike 3.0 Unported License. For any
 * reuse or distribution, you must make clear to others the license terms of
 * this work. The best way to do this is with a link to this web page. All
 * trademarks and registered trademarks appearing on AndroidBegin.com are the
 * property of their respective owners.
 */
public class Tab1 extends Fragment
{
	/**
	 * Create the view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.tab1, container, false);
		return rootView;
	}

}