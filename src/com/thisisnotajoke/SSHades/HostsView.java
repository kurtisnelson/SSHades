package com.thisisnotajoke.SSHades;

import java.util.ArrayList;
import java.util.List;

import org.connectbot.bean.HostBean;
import org.connectbot.util.HostDatabase;

import android.content.Context;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;
import com.thisisnotajoke.SSHades.R;

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
		this.activate();
	}

	public HostBean getSelectedHost(){
		HostBean host = hosts.get(getSelectedItemPosition());
		//make sure the font is readable
		if(host.getFontSize() <= 16)
			host.setFontSize(16);
		return host;
	}

	@Override
	public int getCount() {
		return cards.getCount();
	}

	public void sync() {
		HostDatabase hostDb = new HostDatabase(context);
		hosts.clear();
		cards.clear();
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
