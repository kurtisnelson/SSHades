package com.thisisnotajoke.SSHades;

import java.util.ArrayList;

import org.connectbot.bean.HostBean;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;

public class HostsCardScrollAdapter extends CardScrollAdapter {
	String TAG = "HostsCardScrollAdapter";
	private ArrayList<View> cards;
	private Context context;

	public HostsCardScrollAdapter(Context context){
		this.context = context;
		cards = new ArrayList<View>();
	}

	public View hostFactory(HostBean h){
		Card card = new Card(this.context);
		card.setText(h.getNickname());
		card.setInfo(h.getHostname());
		Log.d(TAG, "Inserting " + h.getNickname());
		View v = card.toView();
		v.setId((int)h.getId());
		return v;
	}

	public void build(HostBean h){
		cards.add(hostFactory(h));
	}

	public void add(Card c){
		cards.add(c.toView());
	}

	public void insert(int pos, View view){
		cards.add(pos, view);
	}

	public void clear(){
		cards.clear();
	}

	/* (non-Javadoc)
	 * @see com.google.android.glass.widget.CardScrollAdapter#findIdPosition(java.lang.Object)
	 */
	@Override
	public int findIdPosition(Object id) {
		for(int i = 0; i < cards.size(); i++){
			if(cards.get(i).getId() == (Integer) id){
				return i;
			}
		}
		return AdapterView.INVALID_POSITION;
	}

	/* (non-Javadoc)
	 * @see com.google.android.glass.widget.CardScrollAdapter#findItemPosition(java.lang.Object)
	 */
	@Override
	public int findItemPosition(Object item) {
		  for (int i = 0; i < cards.size(); i++) {
	            if (cards.get(i).equals(item)) {
	                return i;
	            }
	        }
	        return AdapterView.INVALID_POSITION;
	}

	/* (non-Javadoc)
	 * @see com.google.android.glass.widget.CardScrollAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return cards.size();
	}

	/* (non-Javadoc)
	 * @see com.google.android.glass.widget.CardScrollAdapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return cards.get(position);
	}

	/* (non-Javadoc)
	 * @see com.google.android.glass.widget.CardScrollAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            return setItemOnCard(this, cards.get(position));
	        } else {
	            return setItemOnCard(this, convertView);
	        }
	}
}
