package com.savagelook;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getListView().setDivider( null );
        this.getListView().setDividerHeight(0);
        this.getListView().setSelector(R.color.titlebackgroundcolor);
        fighterHandler = new Handler();
        fighterJson = null;
 
		try {
			JSONArray jsonFighters = new JSONArray(getIntent().getStringExtra("json"));
			final ArrayList<Fighter> fighters = new ArrayList<Fighter>();
			
			for (int i = 0; i < jsonFighters.length(); i++) {
			    	Fighter fighter = new Fighter(jsonFighters.getJSONObject(i));
			    fighters.add(fighter);
		    }
			this.setListAdapter(new FighterAdapter(this, R.layout.fighter_list_item2, fighters));
			
			ListView list = getListView();
			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Fighter fighter = fighters.get(position);
					urlString = constructFighterUrl(fighter.getLink());
					
					// loading animation
					new Thread(new Runnable() {
						public void run() {
							try {
								fighterJson = JsonHelper.getJsonObjectFromUrl(urlString, 5000, 5000);
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
						Toast.makeText(FighterListActivity.this, fighterJson.getString("info"), 3000).show();
					}
				} else {
					Toast.makeText(FighterListActivity.this, "There was an error processing your search. Try again.", 3000).show();
				}
			} catch (JSONException e) {
				Toast.makeText(FighterListActivity.this, "There was an exception processing your search. Try again.", 3000).show();
				Log.e("runnable", e.getMessage());
			} finally {
				//((Button)findViewById(R.id.search)).setEnabled(true);
				//((ProgressBar)findViewById(R.id.loadingAnimation)).setVisibility(View.INVISIBLE);	
			}
		}
    };
    
    private String qfix(String value) {
	    	return java.net.URLEncoder.encode(value.trim());
	}
	
	private String constructFighterUrl(String fighterUrl) {
	    	//return "http://192.168.98.101:8888/knucklehead/ff.php?link=" + qfix(fighterUrl);
		return "http://10.255.11.47:8888/knucklehead/ff.php?link=" + qfix(fighterUrl);
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
	            v = vi.inflate(R.layout.fighter_list_item2, null);	
			}
			
			final Fighter fighter = fighters.get(position);
			if (fighter != null) {
				TextView topText = (TextView)v.findViewById(R.id.fighter_name);
				if (topText != null) {
					topText.setText(fighter.getName());	
				}
				
//				TextView weightText = (TextView)v.findViewById(R.id.fighter_weight);
//				if (weightText != null) {
//					weightText.setText(fighter.getWeight().equals("") ? "" : "(" + fighter.getWeight() + ")");	
//				}
				
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