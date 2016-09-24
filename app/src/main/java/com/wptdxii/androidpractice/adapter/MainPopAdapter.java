package com.wptdxii.androidpractice.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.utils.IpConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainPopAdapter extends BaseAdapter {

	public ArrayList<HashMap<String, Object>> list;
	private Context context;
	private LayoutInflater inflater = null;

	public MainPopAdapter(Context context) {
		this.context = context;

		inflater = LayoutInflater.from(context);
	}

	public void setData(ArrayList<HashMap<String, Object>> list) {
		this.list = list;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.main_pop_item,null);
		ImageView iv_pop_icon = (ImageView) convertView.findViewById(R.id.iv_pop_icon);
		TextView tv_pop_txt = (TextView) convertView.findViewById(R.id.tv_pop_txt);
		View view = (View) convertView.findViewById(R.id.view);
		Map<String, Object> map = list.get(position);

		if(position==0){
			iv_pop_icon.setImageResource(R.drawable.all_category);
		} else{
			String url= IpConfig.getIp4()+map.get("img").toString();
			Glide.with(context)
					.load(url)
					.placeholder(R.drawable.white)
					.error(R.drawable.white)
					.crossFade()  //设置圆角图片
					.into(iv_pop_icon);
		}
		tv_pop_txt.setText((String)map.get("name"));
		if(position ==list.size()-1 )
		{
			view.setVisibility(View.GONE);
		}else {
			view.setVisibility(View.VISIBLE);

		}
		return convertView;
	}






}
