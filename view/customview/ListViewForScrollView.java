package com.cloudhome.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewForScrollView extends ListView {
	
	
	// -- footer view
	private XListViewFooter mFooterView;

	
	private boolean mIsFooterReady = false;
	
	
	
    public ListViewForScrollView(Context context) {
        super(context);
        initWithContext(context);
    }
    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }
    public ListViewForScrollView(Context context, AttributeSet attrs,
        int defStyle) {
    	
        super(context, attrs, defStyle);
        initWithContext(context);
    }
    
    private void initWithContext(Context context) {


		mFooterView = new XListViewFooter(context);

	}

    
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
    
	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (!mIsFooterReady) {
			mIsFooterReady = true;
		//	mFooterView.show();
		//	mFooterView.setState(XListViewFooter.STATE_NORMAL);
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

}
