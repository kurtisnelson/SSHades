package com.thisisnotajoke.connectshades;

import org.connectbot.TerminalView;
import org.connectbot.service.PromptHelper;
import org.connectbot.service.TerminalBridge;
import org.connectbot.service.TerminalManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.glass.app.Card;

public class ConsoleActivity extends Activity {
	private String TAG = "ConsoleActivity";
	protected Uri requestedUri;
	private TerminalManagerConnection terminalManagerConnection;
	private TerminalBridge activeBridge;
	private TerminalView terminalView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestedUri = getIntent().getData();

		Log.d(TAG, "Requesting bridge " + requestedUri);

		setContentView(connectingView(requestedUri));
		terminalManagerConnection = new TerminalManagerConnection(this);
		bindService(new Intent(this, TerminalManager.class), terminalManagerConnection, Context.BIND_AUTO_CREATE);
	}

	public void connected(){
		setActiveBridge(terminalManagerConnection.requestBridge(requestedUri));
		Log.d(TAG, "Terminal bridge up: " + requestedUri);
	}

	public void disconnected(){
		setActiveBridge(null);
	}

	public View connectingView(Uri uri){
		String nick = uri.getFragment();
		Card c = new Card(this);
		c.setText("Loading...");
		c.setInfo(nick);
		return c.toView();
	}
    @Override
    public void onStart() {
            super.onStart();
            bindService(new Intent(this, TerminalManager.class), terminalManagerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);

            Log.d(TAG, "onNewIntent called");

            if(intent.getData() != null){
            	requestedUri = intent.getData();
    		}else{
                    Log.e(TAG, "Got null intent data in onNewIntent()");
                    return;
            }

            if (terminalManagerConnection.get() == null) {
                    Log.e(TAG, "We're not bound in onNewIntent()");
                    return;
            }
            setActiveBridge(terminalManagerConnection.requestBridge(requestedUri));
    }

    @Override
    public void onStop() {
            super.onStop();
            unbindService(terminalManagerConnection);
    }

    private void setActiveBridge(TerminalBridge b){
    	activeBridge = b;
    	terminalView.destroy();
    	terminalView = new TerminalView(this, activeBridge);
    	setContentView(terminalView);
    }

    protected PromptHelper getCurrentPromptHelper() {
        return activeBridge.promptHelper;
}
}
