package com.thisisnotajoke.connectshades;

import org.connectbot.TerminalView;
import org.connectbot.service.PromptHelper;
import org.connectbot.service.TerminalBridge;
import org.connectbot.service.TerminalManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.glass.touchpad.GestureDetector;

public class ConsoleActivity extends Activity {
	/**
	 * @author kurt
	 *
	 */

	private String TAG = "ConsoleActivity";
	protected Uri requestedUri;
	private TerminalManagerConnection terminalManagerConnection;
	private TerminalBridge activeBridge;
	private TerminalView terminalView;
	private GestureDetector gestureDetector;

	// Prompt Stuff
	private RelativeLayout stringPromptGroup;
	protected EditText stringPrompt;
	private TextView stringPromptInstructions;

	private RelativeLayout booleanPromptGroup;
	private TextView booleanPrompt;
	private Button booleanYes, booleanNo;

	protected Handler promptHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.act_console);
		requestedUri = getIntent().getData();

		Log.d(TAG, "Requesting bridge " + requestedUri);

		gestureDetector = new ConsoleGestureDetector(this);
		promptHandler = new PromptHandler();

		terminalManagerConnection = new TerminalManagerConnection(this);

		stringPromptGroup = (RelativeLayout) findViewById(R.id.console_password_group);
		stringPromptInstructions = (TextView) findViewById(R.id.console_password_instructions);
		stringPrompt = (EditText) findViewById(R.id.console_password);
		stringPrompt.setOnKeyListener(new PromptKeyListener());

		booleanPromptGroup = (RelativeLayout) findViewById(R.id.console_boolean_group);
		booleanPrompt = (TextView) findViewById(R.id.console_prompt);

		booleanYes = (Button) findViewById(R.id.console_prompt_yes);
		booleanYes.setOnClickListener(new PromptClickListener(true));

		booleanNo = (Button) findViewById(R.id.console_prompt_no);
		booleanNo.setOnClickListener(new PromptClickListener(false));
	}

	public void connected() {
		setActiveBridge(terminalManagerConnection.requestBridge(requestedUri));
		Log.d(TAG, "Terminal bridge up: " + requestedUri);
	}

	public void disconnected() {
		setActiveBridge(null);
	}

	public void disconnect() {
		this.finish();
	}

	public TerminalView getTerminalView(){
		return terminalView;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Binding service");
		bindService(new Intent(this, TerminalManager.class), terminalManagerConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Log.d(TAG, "onNewIntent called");

		if (intent.getData() != null) {
			requestedUri = intent.getData();
		} else {
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

	private void setActiveBridge(TerminalBridge b) {
		LinearLayout holder = (LinearLayout) findViewById(R.id.console);
		if (activeBridge != b && terminalView != null) {
			terminalView.destroy();
			activeBridge.promptHelper.setHandler(null);
		}
		holder.removeAllViews();
		activeBridge = b;
		if (activeBridge == null)
			return;
		activeBridge.setCharset("utf-8");
		terminalView = new TerminalView(this, activeBridge);
		activeBridge.promptHelper.setHandler(promptHandler);

		holder.addView(terminalView);
		updatePromptVisible();
	}

	public TerminalBridge getActiveBridge() {
		return activeBridge;
	}

	protected PromptHelper getCurrentPromptHelper() {
		if (activeBridge == null)
			return null;
		return activeBridge.promptHelper;
	}

	protected void updatePromptVisible() {
		// check if our currently-visible terminalbridge is requesting any
		// prompt services

		// Hide all the prompts in case a prompt request was canceled
		hideAllPrompts();

		PromptHelper prompt = getCurrentPromptHelper();
		if (String.class.equals(prompt.promptRequested)) {
			stringPromptGroup.setVisibility(View.VISIBLE);

			String instructions = prompt.promptInstructions;
			if (instructions != null && instructions.length() > 0) {
				stringPromptInstructions.setVisibility(View.VISIBLE);
				stringPromptInstructions.setText(instructions);
			} else
				stringPromptInstructions.setVisibility(View.GONE);
			stringPrompt.setText("");
			stringPrompt.setHint(prompt.promptHint);
			stringPrompt.requestFocus();

		} else if (Boolean.class.equals(prompt.promptRequested)) {
			booleanPromptGroup.setVisibility(View.VISIBLE);
			booleanPrompt.setText(prompt.promptHint);
			booleanYes.requestFocus();

		} else {
			hideAllPrompts();
			terminalView.requestFocus();
		}
	}

	protected void hideAllPrompts() {
		stringPromptGroup.setVisibility(View.GONE);
		booleanPromptGroup.setVisibility(View.GONE);
	}

	@SuppressLint("HandlerLeak")
	private final class PromptHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			updatePromptVisible();
		}
	}

	private class PromptKeyListener implements OnKeyListener {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_UP)
				return false;
			if (keyCode != KeyEvent.KEYCODE_ENTER)
				return false;

			// pass collected password down to current terminal
			String value = stringPrompt.getText().toString();

			PromptHelper helper = getCurrentPromptHelper();
			if (helper == null)
				return false;
			helper.setResponse(value);

			// finally clear password for next user
			stringPrompt.setText("");
			updatePromptVisible();
			return true;
		}
	}

	private class PromptClickListener implements OnClickListener {
		Boolean answer;

		public PromptClickListener(Boolean answer) {
			this.answer = answer;
		}

		public void onClick(View v) {
			PromptHelper helper = getCurrentPromptHelper();
			if (helper == null)
				return;
			helper.setResponse(answer);
			updatePromptVisible();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.connection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection. Menu items typically start another
		// activity, start a service, or broadcast another intent.
		switch (item.getItemId()) {
		case R.id.disconnect_menu_item:
			disconnect();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (gestureDetector != null) {
            return gestureDetector.onMotionEvent(event);
        }
        return false;
    }
}
