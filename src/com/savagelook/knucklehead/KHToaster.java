package com.savagelook.knucklehead;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.savagelook.android.Toaster;

public class KHToaster {
	public static void toast(Context context, String message) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.toast, null);
		TextView text = (TextView)layout.findViewById(R.id.text);
		text.setText(message);
		
		Toaster.toast(context, layout);	
	}
	
	public static void toast(Context context, int messageResourceId) {
		toast(context, context.getString(messageResourceId)); 	
	}
}
