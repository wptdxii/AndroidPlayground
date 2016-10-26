package com.wptdxii.uikit.widget.recyclerview.expandable;

import java.util.ArrayList;
import java.util.List;

/**
 * a abstract helper to implement expandable item
 * if don't want to extend this class, implement interface IExpandablen directly
 * Created by wptdxii on 2016/10/14 0014.
 */

public abstract class AbsExpandableItem<T> implements IExpandable<T> {
    protected boolean mExpanded;
    protected List<T> mSubData;

    @Override
    public boolean isExpanded() {
        return this.mExpanded;
    }

    @Override
    public void setExpanded(boolean expandable) {
        this.mExpanded = expandable;
    }

    @Override
    public List<T> getSubData() {
        return mSubData;
    }

    public void setSubData(List<T> subData) {
        this.mSubData = subData;
    }

    public T getSubItemData(int position) {
        if (mSubData != null && position >= 0 && position < mSubData.size()) {
            return mSubData.get(position);
        } else {
            return null;
        }
    }

    public int getSubItemPosition(T subItem) {
        return mSubData != null ? mSubData.indexOf(subItem) : -1;
    }

    public void addSubItemData(T itemData) {
        if (mSubData == null) {
            mSubData = new ArrayList<T>();
        }

        mSubData.add(itemData);
    }

    public void addSubItemData(int position, T itemData) {
        if (mSubData == null) {
            mSubData = new ArrayList<T>();
        }

        if (position >= 0 && position < mSubData.size()) {
            mSubData.add(position, itemData);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public boolean removeSubItemData(T itemData) {
        return mSubData != null && mSubData.remove(itemData);
    }

    public boolean removeSubItemData(int position) {
        if (mSubData != null && position >= 0 && position < mSubData.size()) {
            mSubData.remove(position);

            return true;
        }

        return false;
    }

    public boolean contains(T itemData) {
        return mSubData != null && mSubData.contains(itemData);
    }
}
