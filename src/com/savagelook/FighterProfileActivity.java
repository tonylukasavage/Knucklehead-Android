package com.savagelook;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FighterProfileActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile);
        
        try {
			JSONObject json = new JSONObject(getIntent().getStringExtra("json"));
	        
			// set fighter image
			ImageView imageView = (ImageView)findViewById(R.id.fighter_image);
	        imageView.setImageDrawable(createDrawableFromURL(json.getString("imagelink")));
	        
	        // set profile data
	        LinearLayout profileView = (LinearLayout)findViewById(R.id.profile_view);
	        JSONArray profile = json.getJSONArray("profile");
	        for (int i = 0; i < profile.length(); i++ ) {
		        	LinearLayout layout = new LinearLayout(this);
		        	layout.setOrientation(LinearLayout.HORIZONTAL);
		        	layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		        	
		        	if (i % 2 == 0) {
		        		layout.setBackgroundColor(Color.rgb(50, 50, 50));
		        	} else {
		        		layout.setBackgroundColor(Color.rgb(30, 30, 30));	
		        	}
		        	
		        	String key = profile.getJSONObject(i).getString("k");
		        	String value = profile.getJSONObject(i).getString("v");
		        	
		        	if (key.toLowerCase().equals("name")) {
			        	((TextView)findViewById(R.id.fighter_name1)).setText(value);	
			        	continue;
		        	} else if (key.toLowerCase().equals("nick name")) {
			        	((TextView)findViewById(R.id.fighter_nickname1)).setText("\"" + value + "\"");
			        	continue;
		        	}
		        	
		        TextView label = new TextView(this);
		        label.setText(key + ":");
		        label.setWidth(130);
		        label.setPadding(6, 0, 0, 0);
		        layout.addView(label);
		        
		        TextView valueView = new TextView(this);
		        valueView.setText(value);
		        valueView.setPadding(0, 0, 6, 0);
		        layout.addView(valueView);
		        
		        profileView.addView(layout);
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
}
