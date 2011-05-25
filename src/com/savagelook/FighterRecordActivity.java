package com.savagelook;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FighterRecordActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fight_record);
        
        try {
		    // set fight list
	        ListView fightList = (ListView)findViewById(R.id.fight_list);
	        String jsonString = getIntent().getStringExtra("json");
	        JSONObject json = new JSONObject(jsonString);
	        JSONArray fights = json.getJSONArray("fights");
	        
	        if (fights.length() > 0) {
		        ArrayAdapter<String> dataAdapter;
		        dataAdapter = new ArrayAdapter<String>(this, R.layout.list_item);
		        for (int i = 0; i < fights.length(); i++ ) {
			        	FightDetails details = new FightDetails(fights.getJSONObject(i));
			        	dataAdapter.add(details.getResult() + " -- " + details.getOpponent());
		        }
		        fightList.setAdapter(dataAdapter);
	        } else {
		        	TextView tv = new TextView(this);
		        	tv.setText("No Fight Record");
		        	tv.setTextSize(36f);
		        	tv.setTypeface(null, Typeface.BOLD); 
		        	tv.setGravity(android.view.Gravity.CENTER);
		        	setContentView(tv);
	        }
        } catch (Exception e) {
	        // do something	
        }
    }
}
