package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.MoreTopicActivity;

import java.util.List;
import java.util.Map;

public class MoreTopicAdapter extends BaseAdapter {

	public List<Map<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;
	private MoreTopicActivity activity;

	public MoreTopicAdapter(Context context) {
		this.context = context;
		activity=(MoreTopicActivity)context;

		inflater = LayoutInflater.from(context);
	}

	public void setData(List<Map<String, String>> list) {
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MoreTopicHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.more_hot_topic_item,
					parent, false);

			viewHolder = new MoreTopicHolder();

			viewHolder.small_image = (ImageView) convertView
					.findViewById(R.id.small_image);
			viewHolder.hot_img = (ImageView) convertView
					.findViewById(R.id.hot_img);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.add_time = (TextView) convertView
					.findViewById(R.id.add_time);
			viewHolder.rl_video_length= (RelativeLayout) convertView.findViewById(R.id.rl_video_length);
			viewHolder.tv_video_length= (TextView) convertView.findViewById(R.id.tv_video_length);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MoreTopicHolder) convertView.getTag();

			resetViewHolder(viewHolder);

		}

		String imgstr = list.get(position).get("small_image");


		if (imgstr != null && imgstr.length() > 6) {



			Glide.with(context)
					.load(imgstr)
					.placeholder(R.drawable.white_bg)
					.into(viewHolder.small_image);


		}

		String title = list.get(position).get("title");
		viewHolder.title.setText(title);

		String add_time = list.get(position).get("pub_date");
		String video_time= list.get(position).get("video_time");
		if(activity.isVideoLengthShow){
			viewHolder.rl_video_length.setVisibility(View.VISIBLE);
			viewHolder.tv_video_length.setText(video_time);
		}else{
			viewHolder.rl_video_length.setVisibility(View.GONE);
		}



		String hot_img = list.get(position).get("tag");



		if (hot_img != null && hot_img.length() > 6) {
			viewHolder.hot_img.setVisibility(View.VISIBLE);



			Glide.with(context)
					.load(hot_img)
					.centerCrop()
					.placeholder(R.drawable.white_bg)
					.crossFade()
					.into(viewHolder.hot_img);



		}else{
			
		viewHolder.hot_img.setVisibility(View.GONE);
		}
		viewHolder.add_time.setText(add_time);
		return convertView;

	}

	protected void resetViewHolder(MoreTopicHolder p_ViewHolder) {

	
		p_ViewHolder.add_time.setText(null);
		p_ViewHolder.small_image.setImageResource(R.drawable.white_bg2);
		p_ViewHolder.hot_img.setImageResource(R.drawable.white_bg2);
		p_ViewHolder.title.setText(null);
		p_ViewHolder.tv_video_length.setText(null);
		p_ViewHolder.rl_video_length.setVisibility(View.GONE);
	}

	public static class MoreTopicHolder {

		public ImageView small_image;
		public ImageView hot_img;
		public TextView title;
		public TextView add_time;
		public RelativeLayout rl_video_length;
		public TextView tv_video_length;
	}

}
