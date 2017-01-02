package com.cloudhome.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wptdxii on 2016/11/10 0010.
 */

public class GuaranteeBean implements Serializable{
    private String mCategory;
    private List<ItemBean> mCategoryItems;

    public static class ItemBean implements Serializable{
        private String itemKey;
        private String itemValue;

        public ItemBean(String itemKey, String itemValue) {
            this.itemKey = itemKey;
            this.itemValue = itemValue;
        }

        public String getItemKey() {
            return itemKey;
        }

        public String getItemValue() {
            return itemValue;
        }
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public List<ItemBean> getCategoryItems() {
        return mCategoryItems;
    }

    public void setCategoryItems(List<ItemBean> mCategoryItems) {
        this.mCategoryItems = mCategoryItems;
    }

    public int getItemCount() {
        return mCategoryItems.size() + 1;
    }

    public Object getItem(int position) {
        if (position == 0) {
            return mCategory;
        } else {
            return mCategoryItems.get(position - 1);
        }
    }
}
