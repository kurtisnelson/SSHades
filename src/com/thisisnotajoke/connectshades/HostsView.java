package com.thisisnotajoke.connectshades;

import java.util.ArrayList;
import java.util.List;

import org.connectbot.bean.HostBean;
import org.connectbot.util.HostDatabase;

import android.content.Context;
import android.util.Log;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class HostsView extends CardScrollView {
	private String TAG = "HostsView";
	private HostsCardScrollAdapter cards;
	private List<HostBean> hosts;

	public HostsView(Context parent, HostDatabase hostDb) {
		super(parent);
		cards = new HostsCardScrollAdapter(parent);
		hosts = new ArrayList<HostBean>();

		Log.d(TAG, "Showing " + hostDb.getHosts(false).size() + " hosts");
		for (HostBean host : hostDb.getHosts(false)) {
			hosts.add(host);
			cards.build(host);
		}
		Card config = new Card(parent);
		config.setText(R.string.menu_preferences);
		cards.insert(cards.getCount(), config.toView());

		this.setAdapter(cards);
		this.activate();
	}

	public HostsView(Context context) {
		this(context, new HostDatabase(context));
	}

	public HostBean getSelectedHost(){
		return hosts.get(getSelectedItemPosition());
	}

	@Override
	public int getCount() {
		return cards.getCount();
	}
}
