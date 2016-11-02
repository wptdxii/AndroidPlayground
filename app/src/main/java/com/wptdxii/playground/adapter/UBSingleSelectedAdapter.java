package com.wptdxii.playground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.SelectModel;
import cn.ubaby.ubaby.ui.view.CustomTextView;
import cn.ubaby.ubaby.util.ListUtil;
import cn.ubaby.ubaby.util.Trace;

/**
 * 单选适配器,只有一个元素被选中
 * 选中的元素通过{@link #getSelectedData()}
 * 获取选中的数据,通过{@link #setSelected(int, boolean)}设置选中的元素
 * 清空选中状态通过{@link #clearSelected()}
 * @param <Source> 源数据对象
 * @param <Data> 数据对象（来源自源数据对象)）{@link #getData(Object)}
 */
abstract public class UBSingleSelectedAdapter<Source,Data> extends BaseAdapter {

    protected List<SelectModel<Source>> mDataList;

    private final String TAG = getClass().getSimpleName();
    /**
     * 当前选中的位置
     */
    private Integer mSelectedPosition;

    public ListUtil.Converter<Source, SelectModel<Source>> mDataConverter =
            new ListUtil.Converter<Source, SelectModel<Source>>() {
                @Override
                public SelectModel<Source> transform(Source source) {
                    return new SelectModel<>(source);
                }
            };

    public UBSingleSelectedAdapter(List<Source> dataList) {
        mDataList = ListUtil.transform(dataList, mDataConverter);
    }

    @Override
    public SelectModel<Source> getItem(int position) {
        return mDataList.get(position);
    }

    /**
     * 更换数据
     *
     * @param dataList 数据列表
     */
    public void notifyDataSetChanged(List<Source> dataList) {
        mDataList = ListUtil.transform(dataList, mDataConverter);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    public void clearSelected() {
        ListUtil.operate(mDataList, new SelectModel<Source>().clearOperation);
        mSelectedPosition = null;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_knowledge_filter, null);
            holder.itemTv = (CustomTextView) view.findViewById(R.id.textView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (getItem(position).isSelected) {
            holder.itemTv.setTextColor(parent.getContext().getResources().getColor(R.color.white));
            holder.itemTv.setBackgroundResource(R.drawable.border_rectangle_red);
        } else {
            holder.itemTv.setTextColor(parent.getContext().getResources().getColor(R.color.color_c25741));
            holder.itemTv.setBackgroundResource(R.drawable.border_rectangle_yellow);
        }
        bindData(holder,mDataList.get(position).dataModel);
        return view;
    }

    /**
     * 获取选中的数据
     * @return
     */
    public Data getSelectedData() {
        if (mSelectedPosition == null) {
            return null;
        }
        Source source = mDataList.get(mSelectedPosition).dataModel;
        return getData(source);
    }

    /**
     * 设置选中的数据
     * @param position
     */
    public void setSelectedPosition(int position) {
        if (position >= mDataList.size() || position < 0) {
            Trace.e(TAG,"Select invalid position");
            return;
        }

        if (mSelectedPosition == null) {
            setSelected(position,true);
        } else if (position == mSelectedPosition) { //再次点击取消选中
            setSelected(position,false);
        } else {
            clearSelected();
            setSelected(position,true);
        }
        notifyDataSetChanged();
    }

    private void setSelected(int position,boolean isSelected) {
        if (isSelected) {
            mDataList.get(position).isSelected = true;
            mSelectedPosition = position;
        } else {
            mDataList.get(position).isSelected = false;
            mSelectedPosition = null;
        }
    }

    public boolean hasSelected() {
        return mSelectedPosition != null;
    }

    public UBSingleSelectedAdapter() {
        mDataList = new ArrayList<>();
    }

    /**
     * 绑定数据
     * @param viewHolder
     * @param source
     */
    abstract protected void bindData(ViewHolder viewHolder ,Source source);

    /**
     * 从源对象获取数据
     * @param source
     * @return
     */
    abstract protected Data getData(Source source);

    public static final class ViewHolder {
        public CustomTextView itemTv;
    }
}
