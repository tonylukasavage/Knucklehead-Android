package com.savagelook.knucklehead.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.savagelook.android.Lazy;
import com.savagelook.knucklehead.*;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class FighterTabActivity extends TabActivity {
	private static final String TAG = "FighterTabActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fightertab);
        
        String jsonString = getIntent().getStringExtra("json");
        
        // get fighter name for title bar
        // TODO - push this logic out to the screen scraping code
        this.setTitle("Fighter Profile");
        try {
	        JSONObject json = new JSONObject(jsonString);
	        JSONArray profileJsonArray = json.getJSONArray("profile");  
	        for (int i = 0; i < profileJsonArray.length(); i++ ) {
		        	JSONObject item = profileJsonArray.getJSONObject(i);
		        	String key = item.getString("k");
		        	String value = item.getString("v");
		        	
		        	if (key.toLowerCase().equals("name")) {
		        		this.setTitle("Fighter: " + value);
		        	}
	        }
        } catch (JSONException e) {
		    Log.e(TAG, Lazy.Ex.getStackTrace(e));
        }
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FighterProfileActivity.class);
		intent.putExtra("json", jsonString);
        spec = tabHost.newTabSpec("profile").setIndicator("Profile",
                          res.getDrawable(R.drawable.ic_tab_profile))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, FighterRecordActivity.class);
        intent.putExtra("json", jsonString);
        spec = tabHost.newTabSpec("record").setIndicator("Fights",
                          res.getDrawable(R.drawable.ic_tab_record))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FighterGalleryActivity.class);
        intent.putExtra("json", jsonString);
        spec = tabHost.newTabSpec("pictures").setIndicator("Gallery",
                          res.getDrawable(R.drawable.ic_tab_pictures))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
	}
}
