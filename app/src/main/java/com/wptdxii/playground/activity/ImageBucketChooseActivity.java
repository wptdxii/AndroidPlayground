package com.wptdxii.playground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudhome.R;
import com.nui.multiphotopicker.adapter.ImageBucketAdapter;
import com.nui.multiphotopicker.model.ImageBucket;
import com.nui.multiphotopicker.util.CustomConstants;
import com.nui.multiphotopicker.util.ImageFetcher;
import com.nui.multiphotopicker.util.IntentConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择相册
 * 
 */

public class ImageBucketChooseActivity extends BaseActivity
{
	private ImageFetcher mHelper;
	private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
	private ListView mListView;
	private ImageBucketAdapter mAdapter;
	private int availableSize;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_image_bucket_choose);

		mHelper = ImageFetcher.getInstance(getApplicationContext());
		initData();
		initView();
	}

	private void initData()
	{
		mDataList = mHelper.getImagesBucketList(false);
		availableSize = getIntent().getIntExtra(
				IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
				CustomConstants.MAX_IMAGE_SIZE);
	}

	private void initView()
	{
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new ImageBucketAdapter(this, mDataList);
		mListView.setAdapter(mAdapter);
		TextView titleTv  = (TextView) findViewById(R.id.title);
		titleTv.setText("相册");
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id)
			{

				selectOne(position);

				Intent intent = new Intent(ImageBucketChooseActivity.this,
						ImageChooseActivity.class);
				intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
						(Serializable) mDataList.get(position).imageList);
				intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
						mDataList.get(position).bucketName);
				intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
						availableSize);

				startActivity(intent);
				finish();
			}
		});

		TextView cancelTv = (TextView) findViewById(R.id.action);
		cancelTv.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						PolicyPictureActivity.class);
			
				startActivity(intent);
				finish();
			}
		});
	}

	private void selectOne(int position)
	{
		int size = mDataList.size();
		for (int i = 0; i != size; i++)
		{
			mDataList.get(i).selected = i == position;
		}
		mAdapter.notifyDataSetChanged();
	}

}
