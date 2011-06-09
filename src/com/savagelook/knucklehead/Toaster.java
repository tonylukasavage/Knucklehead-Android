package com.savagelook.knucklehead;

import com.savagelook.*;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Toaster {
	public static void toast(Context context, String message, int duration) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.toast, null);
		
		TextView text = (TextView)layout.findViewById(R.id.text);
		text.setText(message);
		
		Toast toast = ToastSingleton.getInstance(context);
		toast.cancel();
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();	
	}
	
	public static void toast(Context context, String message) {
		toast(context, message, Toast.LENGTH_SHORT);	
	}
	
	public static void toast(Context context, int messageResourceId, int duration) {
		toast(context, context.getString(messageResourceId), duration);	
	}
	
	public static void toast(Context context, int messageResourceId) {
		toast(context, context.getString(messageResourceId));	
	}
}