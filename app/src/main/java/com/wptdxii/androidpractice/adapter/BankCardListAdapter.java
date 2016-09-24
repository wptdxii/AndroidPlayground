package com.wptdxii.androidpractice.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;

import java.util.HashMap;
import java.util.List;

public class BankCardListAdapter extends BaseAdapter {

	private List<HashMap<String, String>> list;

	private Context context;

	private LayoutInflater inflater = null;



	public BankCardListAdapter(Context context, List<HashMap<String, String>> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.list=list;
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
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_add_bank_card,null);
			holder.iv_bank_logo=(ImageView) convertView.findViewById(R.id.iv_bank_logo);
			holder.tv_bank_name=(TextView) convertView.findViewById(R.id.tv_bank_name);
			holder.tv_card_type=(TextView) convertView.findViewById(R.id.tv_card_type);
			holder.tv_bank_card_num=(TextView) convertView.findViewById(R.id.tv_bank_card_num);
			holder.rl_top_item=(RelativeLayout) convertView.findViewById(R.id.rl_top_item);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
			resetViewHolder(holder);
		}


		HashMap<String,String> map= list.get(position);
		


	     
		holder.tv_bank_name.setText(map.get("bankName"));
		holder.tv_card_type.setText(map.get("cardsType"));
		
		String card_num=map.get("bankCardNo");


		String right_num=card_num.substring(card_num.length() - 4, card_num.length());

		String left_star ="";
		for(int i=0;card_num.length() - 4-i>0;i++)
		{
             if(i%4==0){
				 left_star =" "+left_star;
			 }
			left_star ="*"+left_star;

		}

//		left_star=left_star.replaceAll("[0-9]", "*");
//		
//		Log.i("left_star---------------", left_star);
//		String right_num=card_num.substring(card_num.length()-4);
		
		holder.tv_bank_card_num.setText(left_star+ right_num);



		String url=map.get("bankLogoImg");



	//	Glide.with(context).load(url).error(R.drawable.bank_logo_moren).into(holder.iv_bank_logo);

		Glide.with(context)
				.load(url)
				.centerCrop()
				.placeholder(R.drawable.white)  //占位图 图片正在加载
				.error(R.drawable.bank_logo_moren) //下载出错图
				.crossFade()
				.into(holder.iv_bank_logo);

		Log.i("bankLogoImg---------", url);
		return convertView;
	}


	private void resetViewHolder(ViewHolder p_ViewHolder) {

		p_ViewHolder.iv_bank_logo.setImageResource(R.drawable.white_bg2);

	}

	class ViewHolder {
		private ImageView iv_bank_logo;
		private TextView tv_bank_name;
		private TextView tv_card_type;
		private TextView tv_bank_card_num;
		private RelativeLayout rl_top_item;
	}

}
