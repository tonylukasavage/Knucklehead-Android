package com.savagelook;

import java.net.SocketTimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public abstract class JsonAsyncTask extends AsyncTask<String, Void, JSONObject> {
	protected JSONObject queryUrlForJson(String url, int connectTimeout, int readTimeout, int retries, String tooBusy, String oops) {
		JSONObject json = new JSONObject();
		String tag = "queryUrlForJson()";
		
		try {
		    	try {
		    		json.put("success", false);
		    		json = JsonHelper.getJsonObjectFromUrl(url, connectTimeout, readTimeout);
		    	} catch (SocketTimeoutException e) {
		    		if (retries-- > 0) {
			    		json = queryUrlForJson(url, connectTimeout, readTimeout, retries, tooBusy, oops);	
		    		} else {
			    		json.put("info", tooBusy);
		    		}
		    	} catch (Exception e) {
		    		if (retries-- > 0) {
		    			json = queryUrlForJson(url, connectTimeout, readTimeout, retries, tooBusy, oops);
		    		} else {
					Log.e(tag, Lazy.Exception.getStackTrace(e));
			    		json.put("info", oops);	
		    		}
		    	} 
		} catch (JSONException e) {
			return null;
		}
	    	
	    	return json;
	}
}
