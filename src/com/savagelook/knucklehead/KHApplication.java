package com.savagelook.knucklehead;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;

import com.savagelook.android.JsonHelper;

public class KHApplication extends Application {
	private String proxy;
	private int connectTimeout;
	private int readTimeout;
	private int retries;
	private String admobId;
	
	@Override
	public void onCreate() {
		try {
			JSONObject configJson = JsonHelper.getJsonObjectFromResource(this, R.raw.config);
			this.proxy = configJson.getString("proxy");
			this.connectTimeout = Integer.parseInt(configJson.getString("connectTimeout"));
			this.readTimeout = Integer.parseInt(configJson.getString("readTimeout"));
			this.retries = Integer.parseInt(configJson.getString("retries"));
			this.admobId = configJson.getString("admobId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onCreate();
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	
	public String getAdmobId() {
		return admobId;
	}

	public void setAdmobId(String admobId) {
		this.admobId = admobId;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}
}
