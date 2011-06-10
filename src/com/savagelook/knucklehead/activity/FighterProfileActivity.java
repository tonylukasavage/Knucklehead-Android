package com.savagelook.knucklehead.activity;

import com.savagelook.android.KeyValuePair;
import com.savagelook.knucklehead.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class FighterProfileActivity extends Activity {
	private AdView adView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile);
        
	     // setup AdMob ad view
        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest();
        adRequest.setGender(AdRequest.Gender.MALE);
        adView.loadAd(adRequest);
        
        try {
			JSONObject json = new JSONObject(getIntent().getStringExtra("json"));
	        
			// set fighter image
			ImageView imageView = (ImageView)findViewById(R.id.fighter_image);
	        imageView.setImageDrawable(createDrawableFromURL(json.getString("imagelink")));
	        
	        // set profile data
	        TextView tvNickname = (TextView)findViewById(R.id.fighter_nickname1);
	        TextView tvRecord = (TextView)findViewById(R.id.fighter_record_text);
	        ListView listView = (ListView)findViewById(R.id.profile_listview);
	        JSONArray profileJsonArray = json.getJSONArray("profile");
	        ArrayList<KeyValuePair<String,String>> pairs = new ArrayList<KeyValuePair<String,String>>();        
	        
	        // set the name, nickname, and record
	        for (int i = 0; i < profileJsonArray.length(); i++ ) {
		        	JSONObject item = profileJsonArray.getJSONObject(i);
		        	String key = item.getString("k");
		        	String value = item.getString("v");
		        	
		        	if (key.toLowerCase().equals("name")) {
			        	((TextView)findViewById(R.id.fighter_name1)).setText(value);	    	
		        	} else if (key.toLowerCase().equals("nick name")) {
			        	tvNickname.setText("\"" + value + "\"");
		        	} else if (key.toLowerCase().equals("record")) {
		        		Pattern pattern = Pattern.compile("(\\d+)\\-(\\d+)\\-*(\\d*)");
		        		Matcher matcher = pattern.matcher(value);
		        		if (matcher.matches()) {
		        			tvRecord.setText(Html.fromHtml("<font color='#00ff00'>" + matcher.group(1) + "</font> - <font color='#ff0000'>" + matcher.group(2) + "</font> - " + matcher.group(3)));
		        		} else {
			        		tvRecord.setText(value);
		        		}
		        	} else {
			        	pairs.add(new KeyValuePair<String,String>(key, value));
		        	}
	        }
	        
	        // put the rest of the profile items in the list
	        listView.setAdapter(new FighterProfileAdapter(this, R.layout.tab_profile_row, pairs)); 
	        
	        // remove elements from layout if they are blank
	        if (tvNickname.getText().equals("")) {
		        	tvNickname.setVisibility(View.GONE);
	        }
	        if (tvRecord.getText().equals("")) {
		        	tvRecord.setVisibility(View.GONE);
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
    }
	
	@Override
	public void onDestroy() {
	    	adView.destroy();
	    	super.onDestroy();
    }
    
    private Drawable createDrawableFromURL(String urlString) {
	    	Drawable image = null;
	    	try {
			URL url = new URL(urlString);
			InputStream is = (InputStream)url.getContent();
			image = Drawable.createFromStream(is, "src");
	    	} catch (MalformedURLException e) {
			// handle URL exception
			image = null;
	    	} catch (IOException e) {
			// handle InputStream exception
			image = null;	
		}
		
	    	return image;
    }
    
    @Override
    public boolean onSearchRequested() {
	    	Intent intent = new Intent(this, SearchActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
	    	return false;
    }
    
    private class FighterProfileAdapter extends ArrayAdapter<KeyValuePair<String,String>> {
	    	private ArrayList<KeyValuePair<String,String>> pairs;
	    	
	    	public FighterProfileAdapter(Context context, int textViewResourceId, ArrayList<KeyValuePair<String,String>> pairs) {
		    	super(context, textViewResourceId, pairs);
		    	this.pairs = pairs;
	    	}
	    	
	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.tab_profile_row, null);	
			}
			
			TextView label = (TextView)v.findViewById(R.id.label_text);
			TextView valueView = (TextView)v.findViewById(R.id.value_text);
	        label.setText(pairs.get(position).getKey());
	        valueView.setText(pairs.get(position).getValue());
			
			return v;
	    	}
	    	
	    	@Override
	    	public boolean isEnabled(int position) {
		    	return false;
	    	}
    }
}
