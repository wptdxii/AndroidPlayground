package com.wptdxii.uikit.widget.recyclerview.section;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.wptdxii.uikit.widget.recyclerview.BaseViewAdapter;

import java.util.List;

/**
 * Created by wptdxii on 2016/10/26 0026.
 */

public abstract class BaseSectionAdapter<T extends SectionEntity> extends BaseViewAdapter<T> {
    protected static final int Section_VIEW = 0x00000555;
    private int mSectionHeaderResId;

    public BaseSectionAdapter(Context context,@LayoutRes int layoutResId, @LayoutRes int sectionHeaderResId, List<T> data) {
        super(context, layoutResId,data);
        this.mSectionHeaderResId = sectionHeaderResId;
    }

    @Override
    protected int getItemType(int position) {
        return mData.get(position).isHeader ? Section_VIEW : 0;
    }
}