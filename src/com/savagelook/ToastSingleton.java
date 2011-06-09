package com.savagelook;

import android.content.Context;
import android.widget.Toast;

public class ToastSingleton {
	private static Toast instance;
	
	private ToastSingleton() {}
	
	public synchronized static Toast getInstance(Context context) {
		if (instance == null) {
			instance = new Toast(context);	
		}
		return instance;
	}
}