package com.savagelook;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FighterListActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
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
					new FighterDetailsTask().execute(url);
				}
			});
		} catch (Exception e) {
			// do something
		}
    }
    
    private class FighterDetailsTask extends AsyncTask<String, Void, JSONObject> {
	    	private ProgressDialog mProgressDialog = null;
	
	    	@Override
	    	protected JSONObject doInBackground(String... searchUrls) {
	    		String url = searchUrls[0];
	    		JSONObject json = null;
	    		
	    		try {
				Knucklehead kd = (Knucklehead)getApplicationContext();
				json = JsonHelper.getJsonObjectFromUrl(url, kd.getConnectTimeout(), kd.getReadTimeout());
			} catch (SocketTimeoutException e) {
				// TODO use shorter timeouts with under-the-hood retries
				json = null;
		    } catch (Exception e) {
			    	json = null;
			}
		    
		    return json;
	    	}
	    	
	    	@Override 
	    	protected void onPreExecute() {
	    		mProgressDialog = ProgressDialog.show(
	    			FighterListActivity.this, 
	    			"", 
	    			"Loading fighter details...", 
	    			true,
	    			true,
	    			new DialogInterface.OnCancelListener() {	
					@Override
					public void onCancel(DialogInterface arg0) {
						FighterDetailsTask.this.cancel(true);
					}
				});
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(JSONObject json) {
	    		Context context = FighterListActivity.this; 
	    		try {
				if (json != null) {
					if (json.getBoolean("success")) {
						Intent intent = new Intent(context, FighterTabActivity.class);
						intent.putExtra("json", json.getString("data").toString());
						startActivity(intent);
					} else {
						Toast.makeText(context, json.getString("info"), Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context, R.string.too_busy, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				Toast.makeText(context, R.string.request_exception, Toast.LENGTH_SHORT).show();
			} finally {	
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
	    	}
	}
    
    private String qfix(String value) {
	    	return java.net.URLEncoder.encode(value.trim());
	}
	
	private String constructFighterUrl(String fighterUrl) {
		return ((Knucklehead)getApplicationContext()).getProxy() + "link=" + qfix(fighterUrl);
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