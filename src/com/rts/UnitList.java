package com.rts;
 
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
 
/**
 * Class for the main activity that will house the unit list tabs and contente
 * 
 * Code taken from: http://www.androidbegin.com/tutorial/implementing-fragment-tabs-in-android/ on Oct. 16, 2013
 * 
 * Copyright © 2013. AndroidBegin.com. All rights reserved. Privacy Policy. 
 * Creative Commons Licence
 * AndroidBegin.com content is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License. 
 * For any reuse or distribution, you must make clear to others the license terms of this work. The best way to do this is with a link to this web page. 
 * All trademarks and registered trademarks appearing on AndroidBegin.com are the property of their respective owners.
 */
public class UnitList extends Activity {
    // Declare Tab Variable
    ActionBar.Tab Tab1, Tab2;
    Fragment fragmentTab1 = new Tab1();
    Fragment fragmentTab2 = new Tab2();
 
    /**
     * Create the view
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_list);
 
        ActionBar actionBar = getActionBar();
 
        // Hide Actionbar Icon
        actionBar.setDisplayShowHomeEnabled(false);
 
        // Hide Actionbar Title
        actionBar.setDisplayShowTitleEnabled(false);
 
        // Create Actionbar Tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        // Set Tab Icon and Titles
        Tab1 = actionBar.newTab().setText("Units");
        Tab2 = actionBar.newTab().setText("Buildings");
 
        // Set Tab Listeners
        Tab1.setTabListener(new TabListener(fragmentTab1));
        Tab2.setTabListener(new TabListener(fragmentTab2));
 
        // Add tabs to actionbar
        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
    }
}