package com.savagelook.knucklehead.activity;

import java.io.IOException;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
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
        
        // set the edittexts up to handle the search button in the keyboards
        ((EditText)findViewById(R.id.firstname)).setOnEditorActionListener(new SearchOnEditorActionListener());
        ((EditText)findViewById(R.id.lastname)).setOnEditorActionListener(new SearchOnEditorActionListener());
        ((EditText)findViewById(R.id.nickname)).setOnEditorActionListener(new SearchOnEditorActionListener());
        
        // setup the search button's click handler
        Button b = (Button)findViewById(R.id.search);
        b.setOnClickListener(new FighterSearchOnClickListener()); 
        
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
    
    public String constructSearchUrl() {
	    	String url = ((KHApplication)getApplicationContext()).getProxy();
	    	String params = "";
	    	
	    String firstname = Lazy.Str.urlEncode(((EditText)findViewById(R.id.firstname)).getText().toString());
	    String lastname = Lazy.Str.urlEncode(((EditText)findViewById(R.id.lastname)).getText().toString());
	    String nickname = Lazy.Str.urlEncode(((EditText)findViewById(R.id.nickname)).getText().toString());
	    
	    @SuppressWarnings("unchecked")
	    String weightclass = Lazy.Str.urlEncode(((KeyValuePair<String,String>)((Spinner)findViewById(R.id.weightclasses)).getSelectedItem()).getValue());
	    
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
    
    private class FighterSearchOnClickListener implements OnClickListener {
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
    }
    
    private class SearchOnEditorActionListener implements OnEditorActionListener {
	    	@Override
	    	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	    		if (actionId == EditorInfo.IME_ACTION_DONE) {
	    			InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	    			return true;	
	    		} else if (actionId == EditorInfo.IME_ACTION_SEARCH) {
	    			new FighterSearchOnClickListener().onClick(v);
	    		}
	    		return false;
	    	}
    }
}


