package com.savagelook;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	private Handler fighterHandler;
	private String urlString;
	private JSONObject fighterJson;
	private ProgressDialog progressDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getListView().setDivider( null );
        //this.getListView().setDividerHeight(0);
        //this.getListView().setSelector(R.color.titlebackgroundcolor);
        fighterHandler = new Handler();
        fighterJson = null;
        progressDialog = null;
 
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
					urlString = constructFighterUrl(fighter.getLink());
					
					if (progressDialog == null) {
						progressDialog = ProgressDialog.show(FighterListActivity.this, "", "Loading fighter profile...", true);
					} else {
						return;	
					}
					
					// loading animation
					new Thread(new Runnable() {
						public void run() {
							try {
								Knucklehead kd = (Knucklehead)getApplicationContext();
								fighterJson = JsonHelper.getJsonObjectFromUrl(urlString, kd.getConnectTimeout(), kd.getReadTimeout());
							} catch (SocketTimeoutException e) {
								fighterJson = null;
						    } catch (Exception e) {
								fighterJson = null;
							} 
						    
						    fighterHandler.post(handleFighterJson);
						}
					}).start();
				}
			});
		} catch (Exception e) {
			Log.e("FighterListActivity.onCreate()", e.getMessage());
		}
    }
    
    private Runnable handleFighterJson = new Runnable() {
		@Override
		public void run() {
			try {
				if (fighterJson != null) {
					if (fighterJson.getBoolean("success")) {
						if (fighterJson.getString("info").equals("list")) {
							Intent i = new Intent(FighterListActivity.this, FighterListActivity.class);
							i.putExtra("json", fighterJson.getString("data").toString());
							startActivity(i);	
						} else {
							Intent i = new Intent(FighterListActivity.this, FighterTabActivity.class);
							i.putExtra("json", fighterJson.getString("data").toString());
							startActivity(i);
						}
					} else {
						Toast.makeText(FighterListActivity.this, fighterJson.getString("info"), Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(FighterListActivity.this, R.string.too_busy, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				Toast.makeText(FighterListActivity.this, R.string.request_exception, Toast.LENGTH_SHORT).show();
				Log.e("runnable", e.getMessage());
			} finally {
				progressDialog.dismiss();	
				progressDialog = null;
			}
		}
    };
    
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