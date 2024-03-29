package com.savagelook.knucklehead.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.savagelook.android.Lazy;
import com.savagelook.android.UrlJsonAsyncTask;
import com.savagelook.knucklehead.KHApplication;
import com.savagelook.knucklehead.KHToaster;
import com.savagelook.knucklehead.R;
import com.savagelook.knucklehead.model.FightDetails;

public class FighterRecordActivity extends Activity {
	private static final String TAG = "FighterRecordActivity";
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
		        final ArrayList<FightDetails> fightDetails = new ArrayList<FightDetails>();
		        for (int i = 0; i < fights.length(); i++ ) {
			        	fightDetails.add(new FightDetails(fights.getJSONObject(i)));
		        }
		        fightList.setAdapter(new FighterRecordAdapter(this, R.layout.listitem_fight, fightDetails));
		        
		        ListView list = fightList;
				list.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						FightDetails fightDetail = fightDetails.get(position);
						String url = constructFighterUrl(fightDetail.getLink());

						KHApplication kh = (KHApplication)getApplicationContext();
						FighterDetailsTask task = new FighterDetailsTask(FighterRecordActivity.this);
						task.setMessageLoading("Loading fighter details...");
						task.setConnectionParams(kh.getConnectTimeout(), kh.getReadTimeout(), kh.getRetries());
						task.execute(url);
					}
				});
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
    
    private class FighterDetailsTask extends UrlJsonAsyncTask {
		public FighterDetailsTask(Context context) {
			super(context);
		}

		@Override
	    	protected void onPostExecute(JSONObject json) {
	    		try {
	    			this.validateJson(json);
	    			Intent intent = new Intent(context, FighterTabActivity.class);
				intent.putExtra("json", json.getString("data").toString());
				startActivity(intent);
	    		} catch (IOException e) {
	    			KHToaster.toast(this.context, e.getMessage());
			} catch (JSONException e) {
				Log.e(TAG, Lazy.Ex.getStackTrace(e));
				KHToaster.toast(context, this.getMessageError());
			} finally {	
				super.onPostExecute(json);
			}
	    	}
    }
    
    private String constructFighterUrl(String fighterUrl) {
		return ((KHApplication)getApplicationContext()).getProxy() + "link=" + Lazy.Str.urlEncode(fighterUrl);
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
	    	
	    	/*
	    	@Override
	    	public boolean isEnabled(int position) {
		    	return false;
	    	}
	    	*/
    }
}
