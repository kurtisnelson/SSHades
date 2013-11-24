package com.thisisnotajoke.connectshades;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	HostView mHostView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHostView = new HostView(this);
		setContentView(mHostView);
	}
}
