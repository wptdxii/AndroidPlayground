package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.HomeWebShareActivity;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.IpConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HorizontalScrollViewAdapter implements NetResultListener
{

	private Context mContext;
	private LayoutInflater mInflater;
	public List<Map<String, String>> list =new ArrayList<Map<String, String>>();
	//统计接口
		public Statistics statistics=new Statistics(this);
	public HorizontalScrollViewAdapter(Context context)
	{
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	
	}


	public void setData(List<Map<String, String>> list) {
		this.list = list;

	}
	
	public int getCount()
	{
		return list.size();
	}

	public Object getItem(int position)
	{
		return list.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.discover_spokesman_item, parent, false);
			viewHolder.mImg = (ImageView) convertView
					.findViewById(R.id.item_image);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
	//	viewHolder.mImg.setImageResource((Integer) list.get(position).get("thumb"));
		
	
		Log.d("777777",list.size()+"");
	
		Log.d("777777",position+"");
	
		String imgstr = IpConfig.getIp3()+ list.get(position).get("logo");
		
	   
		
		if (imgstr.length() > 6) {



			Glide.with(mContext)
					.load(imgstr)
					.centerCrop()
					.placeholder(R.drawable.white)  //占位图 图片正在加载
					.crossFade()
					.into( 	viewHolder.mImg);




		}
		
		viewHolder.name.setText(list.get(position).get("name"));
		
		viewHolder.mImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				statistics.execute("found_cover_person");
//				DiscoverFragment.expert_jump = "false";
				
				Intent intent = new Intent();

				intent.putExtra("title", list.get(position).get("title"));
				intent.putExtra("img", "");
				intent.putExtra("brief", list.get(position).get("brief"));
				intent.putExtra("is_share", "0");
				intent.putExtra("url", list.get(position).get("url"));

				// 从Activity IntentTest跳转到Activity IntentTest01
				intent.setClass(mContext, HomeWebShareActivity.class);
				mContext.startActivity(intent);
				
			
				
				
			}
		});

	
		return convertView;
	}

	private class ViewHolder
	{
		ImageView mImg;
		TextView name;
	}

	@Override
	public void ReceiveData(int action, int flag, Object dataObj) {
		// TODO Auto-generated method stub
		
	}



}
