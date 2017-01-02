package com.cloudhome.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.HomeWebShareActivity;
import com.cloudhome.activity.LoginActivity;
import com.cloudhome.activity.RecProductDetailActivity;
import com.cloudhome.listener.NetResultListener;
import com.cloudhome.network.Statistics;
import com.cloudhome.utils.IpConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Insurance_S_HScrollviewAdapter implements NetResultListener {

	private Context mContext;
	private LayoutInflater mInflater;
	public List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private String loginString, type = "", user_state;
	private SharedPreferences sp;
	// 统计接口
	private Statistics statistics = new Statistics(this);

	public Insurance_S_HScrollviewAdapter(Context context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);

		sp = context.getSharedPreferences("userInfo", 0);

	}

	public void setData(List<Map<String, String>> list) {
		this.list = list;

	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.jijie_hs_item, parent,
					false);
			viewHolder.mImg = (ImageView) convertView
					.findViewById(R.id.item_image);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// viewHolder.mImg.setImageResource((Integer)
		// list.get(position).get("thumb"));

		if (list.size() > 0)
			;

		{

			String imgstr = IpConfig.getIp3()
					+ list.get(position).get("img_url");

			if (imgstr != null && imgstr.length() > 6) {


				Glide.with(mContext)
						.load(imgstr)
						.centerCrop()
						.placeholder(R.drawable.white)  //占位图 图片正在加载
						.crossFade()
						.into(  viewHolder.mImg);

			}

			viewHolder.mImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					statistics.execute("mall_season");
					type = sp.getString("Login_TYPE", "");
					user_state = sp.getString("Login_CERT", "");
					loginString = sp.getString("Login_STATE", "none");

					if (list.get(position).get("is_url").equals("0")) {

						Intent intent = new Intent();

						intent.putExtra("product_id",
								list.get(position).get("product_id"));

						intent.setClass(mContext,
								RecProductDetailActivity.class);
						mContext.startActivity(intent);

					} else {

						if (loginString.equals("none")) {

							Intent intent = new Intent();
							intent.setClass(mContext, LoginActivity.class);
							mContext.startActivity(intent);
						} else {
							String strUTF8 = "";
							try {

								strUTF8 = URLEncoder.encode(list.get(position)
										.get("img_url"), "UTF-8");

							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}

							Intent intent = new Intent();

							intent.putExtra("title",
									list.get(position).get("name"));
							intent.putExtra("url", list.get(position)
									.get("url"));
							intent.putExtra("brief",
									list.get(position).get("feature_desc"));

							intent.putExtra("img", IpConfig.getIp3()+"/"+strUTF8);
							intent.putExtra("is_share", "1");
							intent.putExtra("share_title", list.get(position).get("name"));
							intent.setClass(mContext,
									HomeWebShareActivity.class);
							mContext.startActivity(intent);
						}

					}

				}
			});
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView mImg;
	}

	@Override
	public void ReceiveData(int action, int flag, Object dataObj) {
		// TODO Auto-generated method stub

	}

}
