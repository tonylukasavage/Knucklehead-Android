package com.savagelook.knucklehead.activity;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.savagelook.android.JsonHelper;
import com.savagelook.android.KeyValuePair;
import com.savagelook.android.Lazy;
import com.savagelook.android.UrlJsonAsyncTask;
import com.savagelook.knucklehead.KHApplication;
import com.savagelook.knucklehead.KHToaster;
import com.savagelook.knucklehead.R;

public class SearchActivity extends Activity {
	private static final String TAG = "SearchAtivity";
	private AdView adView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.setTitle("Knuckle Head: MMA Fighter Database");
        
        // setup spinner and edit text fields 
        this.setupKeyValueSpinner(R.id.weightclasses, R.raw.weightclasses);  
        ((EditText)findViewById(R.id.firstname)).setOnEditorActionListener(new MyOnEditorActionListener());
        ((EditText)findViewById(R.id.lastname)).setOnEditorActionListener(new MyOnEditorActionListener());
        ((EditText)findViewById(R.id.nickname)).setOnEditorActionListener(new MyOnEditorActionListener());
        
        // setup the search button's click handler
        Button b = (Button)findViewById(R.id.search);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = constructSearchUrl();
				if (url.equals("")) {
					KHToaster.toast(getApplicationContext(), R.string.search_empty);
				} else {
					KHApplication kh = (KHApplication)getApplicationContext();
					FighterSearchTask task = new FighterSearchTask(SearchActivity.this);
					task.setMessageLoading("Searching for fighters...");
					task.setConnectionParams(kh.getConnectTimeout(), kh.getReadTimeout(), kh.getRetries());
					task.execute(url);
				}		
			}
		});
        
        // setup AdMob ad view
        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest();
        adRequest.setGender(AdRequest.Gender.MALE);
        adView.loadAd(adRequest);
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
    
    private class FighterSearchTask extends UrlJsonAsyncTask {
		public FighterSearchTask(Context context) {
			super(context);
		}

		@Override
	    	protected void onPostExecute(JSONObject json) {
	    		try {
	    			this.validateJson(json);
				Class<?> intentClass = json.getString("info").equals("list") ? FighterListActivity.class : FighterTabActivity.class;
				Intent intent = new Intent(context, intentClass);
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
    
    private String qfix(String value) {
	    	return java.net.URLEncoder.encode(value.trim());
    }
    
    private String constructSearchUrl() {
	    	String url = ((KHApplication)getApplicationContext()).getProxy();
	    	String params = "";
	    	
	    String firstname = qfix(((EditText)findViewById(R.id.firstname)).getText().toString());
	    String lastname = qfix(((EditText)findViewById(R.id.lastname)).getText().toString());
	    String nickname = qfix(((EditText)findViewById(R.id.nickname)).getText().toString());
	    
	    @SuppressWarnings("unchecked")
	    String weightclass = qfix(((KeyValuePair<String,String>)((Spinner)findViewById(R.id.weightclasses)).getSelectedItem()).getValue());
	    
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
        ArrayAdapter<KeyValuePair<String,String>> adapter = new ArrayAdapter<KeyValuePair<String,String>>(this, android.R.layout.simple_spinner_item);
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);	
        
        try {
	        	JSONArray entries = JsonHelper.getJsonArrayFromResource(this, jsonId);
		    for (int i = 0; i < entries.length(); i++) {
			    	JSONObject pair = entries.getJSONObject(i);
			    adapter.add(new KeyValuePair<String,String>(pair.getString("k"), pair.getString("v")));
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

