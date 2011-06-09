package com.savagelook.knucklehead.activity;

import com.savagelook.knucklehead.*;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class FighterTabActivity extends TabActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fightertab);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FighterProfileActivity.class);
		intent.putExtra("json", getIntent().getStringExtra("json"));
        
        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("profile").setIndicator("Profile",
                          res.getDrawable(R.drawable.ic_tab_profile))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, FighterRecordActivity.class);
        intent.putExtra("json", getIntent().getStringExtra("json"));
        spec = tabHost.newTabSpec("record").setIndicator("Fights",
                          res.getDrawable(R.drawable.ic_tab_record))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FighterGalleryActivity.class);
        intent.putExtra("json", getIntent().getStringExtra("json"));
        spec = tabHost.newTabSpec("pictures").setIndicator("Pictures",
                          res.getDrawable(R.drawable.ic_tab_pictures))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
	}
}
