package com.savagelook.knucklehead.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
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
import com.savagelook.knucklehead.model.Fighter;

public class FighterListActivity extends ListActivity {
	private static final String TAG = "FighterListActivity";
	private AdView adView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        
	    // setup AdMob ad view
        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest();
        adRequest.setGender(AdRequest.Gender.MALE);
        adView.loadAd(adRequest);
        
		try {
			JSONArray jsonFighters = new JSONArray(getIntent().getStringExtra("json"));
			final ArrayList<Fighter> fighters = new ArrayList<Fighter>();
			
			for (int i = 0; i < jsonFighters.length(); i++) {
			    	Fighter fighter = new Fighter(jsonFighters.getJSONObject(i));
			    fighters.add(fighter);
		    }
			this.setListAdapter(new FighterAdapter(this, R.layout.listitem_fighter, fighters));
			
			ListView list = getListView();
			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Fighter fighter = fighters.get(position);
					String url = constructFighterUrl(fighter.getLink());

					KHApplication kh = (KHApplication)getApplicationContext();
					FighterDetailsTask task = new FighterDetailsTask(FighterListActivity.this);
					task.setMessageLoading("Loading fighter details...");
					task.setConnectionParams(kh.getConnectTimeout(), kh.getReadTimeout(), kh.getRetries());
					task.execute(url);
				}
			});
		} catch (Exception e) {
			// do something
		}
    }
    
    @Override
	public void onDestroy() {
	    	adView.destroy();
	    	super.onDestroy();
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
    
    @Override
    public boolean onSearchRequested() {
	    	Intent intent = new Intent(this, SearchActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
	    	return false;
    }
    
    private class FighterAdapter extends ArrayAdapter<Fighter> {
	    	private ArrayList<Fighter> fighters;
	    	
	    	public FighterAdapter(Context context, int textViewResourceId, ArrayList<Fighter> fighters) {
		    	super(context, textViewResourceId, fighters);
		    	this.fighters = fighters;
	    	}
	    	
	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.listitem_fighter, null);	
			}
			
			final Fighter fighter = fighters.get(position);
			if (fighter != null) {
				TextView topText = (TextView)v.findViewById(R.id.fighter_name);
				if (topText != null) {
					topText.setText(fighter.getName());	
				}
				
				TextView detailsText = (TextView)v.findViewById(R.id.fighter_nickname);
				if (detailsText != null) {
					String fullText = "";
					
					if (!fighter.getNickname().equals("")) {
						fullText += "\"" + fighter.getNickname() + "\"";
					}
					if (!fighter.getWeight().equals("")) {
						if (!fullText.equals("")) {
							fullText += ", ";	
						}
						fullText += fighter.getWeight();
					}
					if (!fighter.getHeight().equals("")) {
						if (!fullText.equals("")) {
							fullText += ", ";	
						}
						fullText += fighter.getHeight();
					}
					
					if (!fullText.equals("")) {
						detailsText.setText(fullText);
						detailsText.setVisibility(View.VISIBLE);
					} else {
						detailsText.setVisibility(View.GONE);
					}
				}
			}
			
			return v;
	    	}
    }
}