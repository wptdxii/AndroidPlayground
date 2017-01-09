package com.nui.multiphotopicker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cloudhome.R;
import com.cloudhome.activity.ImageZoomActivity;
import com.cloudhome.activity.PolicyPictureActivity;
import com.nui.multiphotopicker.model.ImageItem;
import com.nui.multiphotopicker.util.ImageDisplayer;
import com.nui.multiphotopicker.util.IntentConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImagePublishAdapter extends BaseAdapter
{
	private List<ImageItem> mDataList = new ArrayList<ImageItem>();
	private Context mContext;

	public ImagePublishAdapter(Context context, List<ImageItem> dataList)
	{
		this.mContext = context;
		this.mDataList = dataList;
	}

	public int getCount()
	{
		// 多返回一个用于展示添加图标
//		if (mDataList == null)
//		{
//			return 1;
//		}
//		else if (mDataList.size() == CustomConstants.MAX_IMAGE_SIZE)
//		{
//			return CustomConstants.MAX_IMAGE_SIZE;
//		}
//		else
//		{
//			return mDataList.size() + 1;
//		}
		return mDataList.size();
	}

	public Object getItem(int position)
	{
//		if (mDataList != null
//				&& mDataList.size() == CustomConstants.MAX_IMAGE_SIZE)
//		{
//			return mDataList.get(position);
//		}
//
//		else if (mDataList == null || position - 1 < 0
//				|| position > mDataList.size())
//		{
//			return null;
//		}
//		else
//		{
//			return mDataList.get(position - 1);
//		}
		return mDataList.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		//所有Item展示不满一页，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问题
		convertView = View.inflate(mContext, R.layout.item_publish, null);
		ImageView imageIv = (ImageView) convertView
				.findViewById(R.id.item_grid_image);
		ImageView imageDelete = (ImageView) convertView
				.findViewById(R.id.iv_delete_item);

//		if (isShowAddItem(position))
//		{
//			imageIv.setImageResource(R.drawable.btn_add_pic);
//			
//			imageIv.setBackgroundResource(R.color.bg_gray);
//		}
//		else
//		{
			final ImageItem item = mDataList.get(position);
			ImageDisplayer.getInstance(mContext).displayBmp(imageIv,
					item.thumbnailPath, item.sourcePath);
//		}
			imageDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mDataList.remove(position);
					notifyDataSetChanged();
				}
			});
			
			imageIv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					PolicyPictureActivity.ll_service_content.setVisibility(View.INVISIBLE);
					PolicyPictureActivity.isShown=false;
					
					Intent intent = new Intent(mContext,
							ImageZoomActivity.class);
					intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
							(Serializable) mDataList);
					intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
					mContext.startActivity(intent);
				}
			});

		return convertView;
	}

	private boolean isShowAddItem(int position)
	{
		int size = mDataList == null ? 0 : mDataList.size();
		return position == size;
	}

}
