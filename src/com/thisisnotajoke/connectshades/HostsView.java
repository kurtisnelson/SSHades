package com.thisisnotajoke.connectshades;

import java.util.ArrayList;
import java.util.List;

import org.connectbot.bean.HostBean;
import org.connectbot.util.HostDatabase;

import android.content.Context;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class HostsView extends CardScrollView {
	private HostsCardScrollAdapter cards;
	private List<HostBean> hosts;
	private Context context;

	public HostsView(Context parent) {
		super(parent);
		context = parent;
		cards = new HostsCardScrollAdapter(parent);
		hosts = new ArrayList<HostBean>();
		this.setAdapter(cards);
		sync();
		this.activate();
	}

	public HostBean getSelectedHost(){
		return hosts.get(getSelectedItemPosition());
	}

	@Override
	public int getCount() {
		return cards.getCount();
	}

	private void sync() {
		HostDatabase hostDb = new HostDatabase(context);
		for (HostBean host : hostDb.getHosts(false)) {
			hosts.add(host);
			cards.build(host);
		}
		Card config = new Card(context);
		config.setText(R.string.menu_preferences);
		cards.insert(cards.getCount(), config.toView());
		hostDb.close();
		this.updateViews(false);
	}
}
