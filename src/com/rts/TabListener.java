package com.rts;
 
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar;
 
/**
 * Provides ability to switch tabs when clicked.
 * 
 * Code taken from: http://www.androidbegin.com/tutorial/implementing-fragment-tabs-in-android/ on Oct. 16, 2013
 * 
 * Copyright © 2013. AndroidBegin.com. All rights reserved. Privacy Policy. 
 * Creative Commons License
 * AndroidBegin.com content is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License. 
 * For any reuse or distribution, you must make clear to others the license terms of this work. The best way to do this is with a link to this web page. 
 * All trademarks and registered trademarks appearing on AndroidBegin.com are the property of their respective owners.
 */
public class TabListener implements ActionBar.TabListener {
 
    Fragment fragment;
 
    /**
     * Constructor for the Tab Listener
     * @param fragment - fragment that will be loaded
     */
    public TabListener(Fragment fragment) {
        this.fragment = fragment;
    }
 
   /**
    * Switch tab to the selected tab
    */
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.fragment_container, fragment);
    }
 
    /**
     * Remove the contents of the tab from the view
     */
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }
    
    /**
     * Reset the view when the tab is reselected
     */
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
 
    }
}