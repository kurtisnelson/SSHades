/**
 *
 */
package com.thisisnotajoke.connectshades;

import java.util.List;

import org.connectbot.service.TerminalBridge;
import org.connectbot.service.TerminalManager;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

/**
 * @author kurt
 *
 */
public class TerminalManagerConnection implements ServiceConnection {
	protected TerminalManager bound;
	private ConsoleActivity parent;
	private String TAG = "TerminalManagerConnection";

	public TerminalManagerConnection(ConsoleActivity parent){
		this.parent = parent;
	}
	public TerminalManager get(){
		return bound;
	}

	public void onServiceConnected(ComponentName className, IBinder service) {
		bound = ((TerminalManager.TerminalBinder) service).getService();

		// let manager know about our event handling services

		Log.d(TAG, String.format("Connected to TerminalManager and found bridges.size=%d",
				bound.bridges.size()));

		bound.setResizeAllowed(true);
		parent.connected();
	}

	public TerminalBridge requestBridge(Uri requested){
		String requestedNickname = requested.getFragment();
		TerminalBridge requestedBridge = bound.getConnectedBridge(requestedNickname);

		// If we didn't find the requested connection, try opening it
		if (requestedNickname != null && requestedBridge == null) {
			try {
				Log.d(TAG,
						String.format(
								"We couldnt find an existing bridge with URI=%s (nickname=%s), so creating one now",
								requested.toString(), requestedNickname));
				requestedBridge = bound.openConnection(requested);
			} catch (Exception e) {
				Log.e(TAG, "Problem while trying to create new requested bridge from URI", e);
			}
		}
		return requestedBridge;
	}

	public List<TerminalBridge> getBridges(){
		return bound.bridges;
	}

	public void onServiceDisconnected(ComponentName className) {
		// tell each bridge to forget about our prompt handler
		synchronized (bound.bridges) {
			for (TerminalBridge bridge : bound.bridges)
				bridge.promptHelper.setHandler(null);
		}

		parent.disconnected();
		bound = null;
	}
}
