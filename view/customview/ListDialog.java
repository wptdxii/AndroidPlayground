package com.cloudhome.view.customview;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudhome.R;

public abstract class ListDialog extends AlertDialog implements OnItemClickListener{

	private Context mContext;
	private Builder builder;
	private ListView dialogListView;
	private TextView dialogTitle;
	private String[] itemArray;
	private int type;
	private AlertDialog dialog;
	private int position=-1;
	private String title="";
	
	public ListDialog(Context context) {
		super(context);
		mContext=context;
		init();
	}

	public ListDialog(Context mContext, String[] itemArray,
					  String title) {
		super(mContext);
		this.mContext = mContext;
		this.itemArray = itemArray;
		this.title = title;
		init();
	}

	private void init() {
		LayoutInflater factory = LayoutInflater.from(mContext);
		View myView = factory.inflate(R.layout.view_dialog_list,null);
		dialogListView=(ListView) myView.findViewById(R.id.lv_dialog);
		dialogTitle=(TextView) myView.findViewById(R.id.tv_title_dialog);
		builder= new Builder(mContext);
		builder.setView(myView);

		dialogTitle.setText(title);

		MySimpleAdapter adapter=new MySimpleAdapter(mContext);
		dialogListView.setAdapter(adapter);
		dialogListView.setOnItemClickListener(this);
		dialog=builder.create();
		dialog.show();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		item(arg2);
	}
	
	public abstract void item(int m);
	
	
	class MySimpleAdapter extends BaseAdapter{
		
		private Context context;
		private LayoutInflater layoutInflater;
		
		

		public MySimpleAdapter(Context context) {
			super();
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemArray.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return itemArray[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.item_make_insurance_dialog, null);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_item_make_insurance_dialog=(TextView) convertView.findViewById(R.id.tv_item_make_insurance_dialog);
			holder.tv_item_make_insurance_dialog.setText(itemArray[arg0]);
			return convertView;
		
		}
		
		class ViewHolder {
			TextView tv_item_make_insurance_dialog;
		}
		
	}
	

}
