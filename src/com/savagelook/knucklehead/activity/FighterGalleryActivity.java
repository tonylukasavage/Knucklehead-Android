package com.savagelook.knucklehead.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FighterGalleryActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Coming soon...");
        setContentView(textview);
    }
}
