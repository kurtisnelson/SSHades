package com.thisisnotajoke.SSHades;

import org.connectbot.bean.HostBean;
import org.connectbot.transport.TransportFactory;
import org.connectbot.util.HostDatabase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.thisisnotajoke.SSHades.R;

public class HostActivity extends Activity {

	TextView quickconnect;
	Spinner transportSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_host);

		  quickconnect = (TextView) this.findViewById(R.id.front_quickconnect);
          quickconnect.setOnKeyListener(new OnKeyListener() {

                  public boolean onKey(View v, int keyCode, KeyEvent event) {

                          if(event.getAction() == KeyEvent.ACTION_UP) return false;
                          if(keyCode != KeyEvent.KEYCODE_ENTER) return false;

                          return startConsoleActivity();
                  }
          });

          transportSpinner = (Spinner)findViewById(R.id.transport_selection);
          ArrayAdapter<String> transportSelection = new ArrayAdapter<String>(this,
                          android.R.layout.simple_spinner_item, TransportFactory.getTransportNames());
          transportSelection.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          transportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                          String formatHint = TransportFactory.getFormatHint(
                                          (String) transportSpinner.getSelectedItem(),
                                          HostActivity.this);

                          quickconnect.setHint(formatHint);
                          quickconnect.setError(null);
                          quickconnect.requestFocus();
                  }
                  public void onNothingSelected(AdapterView<?> arg0) { }
          });
          transportSpinner.setAdapter(transportSelection);
	}

	private boolean startConsoleActivity() {
        Uri uri = TransportFactory.getUri((String) transportSpinner
                        .getSelectedItem(), quickconnect.getText().toString());

        if (uri == null) {
                quickconnect.setError(getString(R.string.list_format_error,
                                TransportFactory.getFormatHint(
                                                (String) transportSpinner.getSelectedItem(),
                                                HostActivity.this)));
                return false;
        }

        HostDatabase hostdb = new HostDatabase(this);
        HostBean host = TransportFactory.findHost(hostdb, uri);
        if (host == null) {
                host = TransportFactory.getTransport(uri.getScheme()).createHost(uri);
                host.setColor(HostDatabase.COLOR_GRAY);
                host.setPubkeyId(HostDatabase.PUBKEYID_ANY);
                hostdb.saveHost(host);
        }
        hostdb.close();
        uri = host.getUri();
        startActivity(new Intent(Intent.ACTION_VIEW, uri));

        return true;
}
}
