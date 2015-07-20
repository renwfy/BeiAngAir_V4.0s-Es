package com.beiang.airdog.ui.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beiang.airdog.net.business.ihomer.HomeGetDevPair.RspHomeGetDev;
import com.beiang.airdog.net.business.ihomer.getHomerPair.RspGetHomer;
import com.broadlink.beiangair.R;

/***
 * 
 * @author lsd
 * 
 */
public class DevAdapter extends BaseAdapter {
	List<RspHomeGetDev.Data> list;

	public void setData(List<RspHomeGetDev.Data> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return list == null ? 0 : position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Tag tag;
		if (convertView == null) {
			tag = new Tag();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_text, null);
			tag.bt = (TextView) convertView.findViewById(R.id.bt);
			convertView.setTag(tag);
		} else {
			tag = (Tag) convertView.getTag();
		}

		RspHomeGetDev.Data ety = (RspHomeGetDev.Data) getItem(position);
		tag.bt.setText(ety.name + "\n" + ety.device_id);
		return convertView;
	}

	class Tag {
		TextView bt;
	}
}
