package com.thisisnotajoke.connectshades;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ConsoleActivity extends Activity {
	private String TAG = "ConsoleActivity";
	protected Uri requestedUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestedUri = getIntent().getData();
		Log.d(TAG, "Requested " + requestedUri);
	}
}
