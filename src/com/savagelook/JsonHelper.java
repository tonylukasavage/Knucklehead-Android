package com.savagelook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;
import org.json.*;

import android.content.Context;

public class JsonHelper {
	public static JSONArray getJsonArrayFromResource(Context context, int resourceId) throws JSONException, IOException {
		InputStream is = context.getResources().openRawResource(resourceId);
		return new JSONArray(getStringFromInputStream(is));
	}
	
	public static JSONObject getJsonObjectFromResource(Context context, int resourceId) throws JSONException, IOException {
		InputStream is = context.getResources().openRawResource(resourceId);
		return new JSONObject(getStringFromInputStream(is));
	}
	
	public static JSONArray getJsonArrayFromUrl(String url) throws MalformedURLException, JSONException, IOException 
	{
		return getJsonArrayFromUrl(url, 0, 0);
	}
	
	public static JSONArray getJsonArrayFromUrl(String url, int connectTimeout, int readTimeout) throws MalformedURLException, JSONException, IOException {
		return new JSONArray(getStringFromUrl(url, connectTimeout, readTimeout));
	}
	
	public static JSONObject getJsonObjectFromUrl(String url) throws MalformedURLException, JSONException, IOException 
	{
		return getJsonObjectFromUrl(url, 0, 0);
	}
	
	public static JSONObject getJsonObjectFromUrl(String url, int connectTimeout, int readTimeout) throws MalformedURLException, JSONException, IOException 
	{
		return new JSONObject(getStringFromUrl(url, connectTimeout, readTimeout));
	}
	
	static private String getStringFromInputStream(InputStream is) throws IOException {	
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayBuffer buffer = new ByteArrayBuffer(bis.available());
		int current = 0;
		while ((current = bis.read()) != -1) {
			buffer.append((byte)current);	
		}
		return new String(buffer.toByteArray());
	}
	
	private static String getStringFromUrl(String url, int connectTimeout, int readTimeout) throws MalformedURLException, JSONException, IOException {
		URL urlObject = new URL(url);
		HttpURLConnection urlConn = (HttpURLConnection)urlObject.openConnection();
		
		if (connectTimeout != 0) {
			urlConn.setConnectTimeout(connectTimeout);
		}
		if (readTimeout != 0) {
			urlConn.setReadTimeout(readTimeout);
		}
		
		String jsonString = getStringFromInputStream(urlConn.getInputStream());
		urlConn.disconnect();
		return jsonString;
	}
}
