package com.savagelook;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FighterRecordActivity extends Activity {
	private AdView adView;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_fights);
        
	     // setup AdMob ad view
        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest();
        adRequest.setGender(AdRequest.Gender.MALE);
        adView.loadAd(adRequest);
        
        try {
	        	// get json from Intent
	        	String jsonString = getIntent().getStringExtra("json");
	        JSONObject json = new JSONObject(jsonString);       	
        	
		    // set fight list
	        ListView fightList = (ListView)findViewById(R.id.fight_list);
	        JSONArray fights = json.getJSONArray("fights");
	        
	        if (fights.length() > 0) {
		        ArrayList<FightDetails> fightDetails = new ArrayList<FightDetails>();
		        for (int i = 0; i < fights.length(); i++ ) {
			        	fightDetails.add(new FightDetails(fights.getJSONObject(i)));
		        }
		        fightList.setAdapter(new FighterRecordAdapter(this, R.layout.listitem_fight, fightDetails));
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
    
    @Override
	public void onDestroy() {
	    	adView.destroy();
	    	super.onDestroy();
    }
    
    @Override
    public boolean onSearchRequested() {
	    	Intent intent = new Intent(this, SearchActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
	    	return false;
    }
    
    private class FighterRecordAdapter extends ArrayAdapter<FightDetails> {
	    	private ArrayList<FightDetails> fightDetails;
	    	
	    	public FighterRecordAdapter(Context context, int textViewResourceId, ArrayList<FightDetails> fightDetails) {
		    	super(context, textViewResourceId, fightDetails);
		    	this.fightDetails = fightDetails;
	    	}
	    	
	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			FightDetails fd = fightDetails.get(position);
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.listitem_fight, null);	
			}
			
			TextView eventName = (TextView)v.findViewById(R.id.fight_event_name);
			TextView eventDate = (TextView)v.findViewById(R.id.fight_date);
			TextView result = (TextView)v.findViewById(R.id.fight_result);
			TextView opponent = (TextView)v.findViewById(R.id.fight_opponent);
			TextView method = (TextView)v.findViewById(R.id.fight_method);
			TextView round = (TextView)v.findViewById(R.id.fight_round);
			TextView time = (TextView)v.findViewById(R.id.fight_time);
			
			
	        eventName.setText(fd.getEvent());
	        eventDate.setText(fd.getDate());
	        opponent.setText(fd.getOpponent());
	        method.setText("via " + fd.getMethod());
	        round.setText("Rd. " + fd.getRound());
	        time.setText(fd.getTime());
	        
	        String resString = fightDetails.get(position).getResult().toUpperCase();
	        if (resString.equals("WIN")) {
		        	result.setTextColor(Color.GREEN);
	        } else if (resString.equals("LOSS")) {
		        	result.setTextColor(Color.RED);
	        } else {
		        	result.setTextColor(Color.WHITE);
	        }
	        result.setText(resString);
			
			return v;
	    	}
	    	
	    	@Override
	    	public boolean isEnabled(int position) {
		    	return false;
	    	}
    }
}
