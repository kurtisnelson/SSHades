package com.thisisnotajoke.SSHades;

import org.connectbot.util.HostDatabase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.thisisnotajoke.SSHades.R;

public class MainActivity extends Activity {
	private HostsView hostsView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hostsView = new HostsView(this);
		hostsView.setOnItemClickListener(new TapListener());
		setContentView(hostsView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		hostsView.sync();
	}
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		int pos = hostsView.getSelectedItemPosition();
		if(pos == hostsView.getCount() - 1){
			menu.setGroupVisible(R.id.group_app, true);
			menu.setGroupVisible(R.id.group_host, false);
		}else{
			menu.setGroupVisible(R.id.group_app, false);
			menu.setGroupVisible(R.id.group_host, true);
		}
        return true;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection. Menu items typically start another
		// activity, start a service, or broadcast another intent.
		switch (item.getItemId()) {
		case R.id.connect_menu_item:
			return connectHost();
		case R.id.add_menu_item:
			return addHost();
		case R.id.delete_menu_item:
			return deleteHost();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class TapListener implements AdapterView.OnItemClickListener{
		public void onItemClick (AdapterView<?> parent, View view, int position, long id){
			openOptionsMenu();
		}
	}

	public boolean connectHost(){
		Uri uri = hostsView.getSelectedHost().getUri();
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
		return true;
	}

	public boolean deleteHost(){
		HostDatabase hostDb = new HostDatabase(this);
		hostDb.deleteHost(hostsView.getSelectedHost());
		hostDb.close();
		hostsView.sync();
		return true;
	}

	public boolean addHost(){
		Intent intent = new Intent(this, HostActivity.class);
		startActivity(intent);
		return true;
	}
}
