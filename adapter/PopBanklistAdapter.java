package com.cloudhome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloudhome.R;
import com.cloudhome.activity.WithdrawActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PopBanklistAdapter extends BaseAdapter {

	public ArrayList<HashMap<String, String>> list;
	private LayoutInflater inflater = null;
	private WithdrawActivity context;

	public PopBanklistAdapter(WithdrawActivity context) {
		this.context = context;

		inflater = LayoutInflater.from(context);
	}

	public void setData(ArrayList<HashMap<String, String>> list) {
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.withdraw_mybank_item,
					parent, false);
			holder = new ViewHolder();
			holder.bank_icon= (ImageView) convertView.findViewById(R.id.bank_icon);
			holder.tv_text= (TextView) convertView.findViewById(R.id.tv_text);
			holder.select_bank_img= (ImageView) convertView.findViewById(R.id.select_bank_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}

		Map<String, String> Map = list.get(position);

		String bankcardno = Map.get("bankCardNo");
		String bankname= Map.get("bankName") + "(尾号" + bankcardno.substring(
				bankcardno.length() - 4, bankcardno.length()) + ")";

		holder.tv_text.setText(bankname);






		Glide.with(context)
				.load( Map.get("bankLogoImg"))
				.centerCrop()
				.placeholder(R.drawable.white_bg)
				.error(R.drawable.bank_logo_moren)
				.crossFade()
				.into( holder.bank_icon);
if(context.bankpos==position)
{
	holder.select_bank_img.setImageResource(R.drawable.withdraw_select_bankcard);
}else{
	holder.select_bank_img.setImageResource(R.drawable.withdraw_unselect_bankcard);
}

		return convertView;

	}



	 static class ViewHolder {

		public ImageView bank_icon;

		public TextView tv_text;
		public ImageView select_bank_img;

	}

}
