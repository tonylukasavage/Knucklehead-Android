package com.savagelook;

import java.net.SocketTimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        this.setupKeyValueSpinner(R.id.weightclasses, R.raw.weightclasses);  
        ((EditText)findViewById(R.id.firstname)).setOnEditorActionListener(new MyOnEditorActionListener());
        ((EditText)findViewById(R.id.lastname)).setOnEditorActionListener(new MyOnEditorActionListener());
        ((EditText)findViewById(R.id.nickname)).setOnEditorActionListener(new MyOnEditorActionListener());
        
        Button b = (Button)findViewById(R.id.search);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = constructSearchUrl();
				if (url.equals("")) {
					Toaster.toast(getApplicationContext(), R.string.search_empty);
				} else {
					new FighterSearchTask().execute(url);
				}		
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
    
    private class FighterSearchTask extends AsyncTask<String, Void, JSONObject> {
	    	private ProgressDialog mProgressDialog = null;
	
	    	@Override
	    	protected JSONObject doInBackground(String... searchUrls) {
	    		String url = searchUrls[0];
	    		JSONObject json = null;
	    		Knucklehead kd = (Knucklehead)getApplicationContext();
	    		int retries = kd.getRetries();
	    		
	    		try {
				
				json = JsonHelper.getJsonObjectFromUrl(url, kd.getConnectTimeout(), kd.getReadTimeout());
			} catch (SocketTimeoutException e) {
				retries--;
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
	    			SearchActivity.this, 
	    			"", 
	    			"Searching for fighters...", 
	    			true,
	    			true,
	    			new DialogInterface.OnCancelListener() {	
					@Override
					public void onCancel(DialogInterface arg0) {
						FighterSearchTask.this.cancel(true);
					}
				});
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(JSONObject json) {
	    		Context context = SearchActivity.this;  		
	    		try {
				if (json != null) {
					if (json.getBoolean("success")) {
						Class<?> intentClass = json.getString("info").equals("list") ? FighterListActivity.class : FighterTabActivity.class;
						Intent intent = new Intent(context, intentClass);
						intent.putExtra("json", json.getString("data").toString());
						startActivity(intent);
					} else {
						Toaster.toast(context, json.getString("info"));
					}
				} else {
					Toaster.toast(context, R.string.too_busy);
				}
			} catch (JSONException e) {
				Toaster.toast(context, R.string.request_exception);
			} finally {	
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
	    	}
	
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

