package com.savagelook;

import java.net.SocketTimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SearchActivity extends Activity {
	private Handler searchHandler;
	private JSONObject searchJson;
	private String searchUrl;
	private ProgressDialog progressDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        searchHandler = new Handler();
        searchJson = null;
        
        this.setupKeyValueSpinner(R.id.weightclasses, R.raw.weightclasses);
        //this.setupKeyValueSpinner(R.id.organizations, R.raw.organizations);  
        
        ((EditText)findViewById(R.id.firstname)).setOnEditorActionListener(new MyOnEditorActionListener());
        ((EditText)findViewById(R.id.lastname)).setOnEditorActionListener(new MyOnEditorActionListener());
        ((EditText)findViewById(R.id.nickname)).setOnEditorActionListener(new MyOnEditorActionListener());
        //((EditText)findViewById(R.id.association)).setOnEditorActionListener(new MyOnEditorActionListener());
        
        Button b = (Button)findViewById(R.id.search);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchUrl = constructSearchUrl();
				if (searchUrl.equals("")) {
					Toast toast = Toast.makeText(SearchActivity.this, "Give me search criteria first!", 3000);
					toast.show();
					return;
				}

				progressDialog = ProgressDialog.show(SearchActivity.this, "", "Searching for fighters...", true);
				
				new Thread(new Runnable() {
					public void run() {
						try {
							searchJson = JsonHelper.getJsonObjectFromUrl(searchUrl, 5000, 5000);
						} catch (SocketTimeoutException e) {
							searchJson = null;
					    } catch (Exception e) {
							searchJson = null;
						} 
					    
					    searchHandler.post(handleSearchJson);
					}
				}).start();
			}
		});
    }
    
    @Override
    public boolean onSearchRequested() {
	    	Intent intent = new Intent(this, SearchActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
	    	return false;
    }
    
    private String qfix(String value) {
	    	return java.net.URLEncoder.encode(value.trim());
    }
    
    private String constructSearchUrl() {
	    	String url = ((Knucklehead)getApplicationContext()).getProxy();
	    	String params = "";
	    	
	    String firstname = qfix(((EditText)findViewById(R.id.firstname)).getText().toString());
	    String lastname = qfix(((EditText)findViewById(R.id.lastname)).getText().toString());
	    String nickname = qfix(((EditText)findViewById(R.id.nickname)).getText().toString());
	    String weightclass = qfix(((KeyValuePair)((Spinner)findViewById(R.id.weightclasses)).getSelectedItem()).getValue());
	    
	    if (!firstname.equals("")) {
		    	params += "firstname=" + firstname + "&";
	    }
	    if (!lastname.equals("")) {
		    	params += "lastname=" + lastname + "&";
	    }
	    if (!nickname.equals("")) {
		    	params += "nickname=" + nickname + "&";
	    }
	    if (!weightclass.equals("")) {
		    params += "weight=" + weightclass;
	    }
	    	
	    if (params.equals("")) {
		    	return "";
	    } else {
			return url + params;
	    }
    }
    
    private Runnable handleSearchJson = new Runnable() {
		@Override
		public void run() {
			try {
				if (searchJson != null) {
					if (searchJson.getBoolean("success")) {
						if (searchJson.getString("info").equals("list")) {
							Intent i = new Intent(SearchActivity.this, FighterListActivity.class);
							i.putExtra("json", searchJson.getString("data").toString());
							startActivity(i);	
						} else {
							Intent i = new Intent(SearchActivity.this, FighterTabActivity.class);
							i.putExtra("json", searchJson.getString("data").toString());
							startActivity(i);
						}
					} else {
						Toast.makeText(SearchActivity.this, searchJson.getString("info"), 3000).show();
					}
				} else {
					Toast.makeText(SearchActivity.this, "There was an error processing your search. Try again.", 3000).show();
				}
			} catch (JSONException e) {
				Toast.makeText(SearchActivity.this, "There was an exception processing your search. Try again.", 3000).show();
				Log.e("runnable", e.getMessage());
			} finally {	
				progressDialog.dismiss();
			}
		}
    };
    
    private void setupKeyValueSpinner(int spinnerId, int jsonId) {
	    	Spinner s = (Spinner)findViewById(spinnerId);
        ArrayAdapter<KeyValuePair> adapter = new ArrayAdapter<KeyValuePair>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);	
        
        try {
	        	JSONArray entries = JsonHelper.getJsonArrayFromResource(this, jsonId);
		    for (int i = 0; i < entries.length(); i++) {
			    	JSONObject pair = entries.getJSONObject(i);
			    adapter.add(new KeyValuePair(pair.getString("k"), pair.getString("v")));
		    }
        } catch (Exception e) {
	        	// do something
        }
    }
}

class MyOnEditorActionListener implements OnEditorActionListener {
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			return true;	
		}
		return false;
	}
}
