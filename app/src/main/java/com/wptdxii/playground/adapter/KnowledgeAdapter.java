package com.wptdxii.playground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.ChildArticleModel;
import cn.ubaby.ubaby.ui.view.CustomTextView;
import cn.ubaby.ubaby.util.ImageHelper;

/**
 * 育儿
 * Created by wenlong.lu
 * on 2015/7/3.
 */
public class KnowledgeAdapter extends BaseAdapter implements View.OnClickListener {

    private List<ChildArticleModel> list;
    private String imageUrl = null;
    private boolean isShowTipBtn;
    private Callback callback;
    private ChildArticleModel mSelectedChildArticleModel;

    public KnowledgeAdapter(List<ChildArticleModel> list, boolean isShowTipBtn, Callback callback) {
        this.list = list;
        this.callback = callback;
        this.isShowTipBtn = isShowTipBtn;
    }

    /**
     * 刷新回传的评论数和点赞数
     */
    public void refreshSelectedCommentAndLikeCount(int likecount,int commentCount,boolean hasPost) {
        if (mSelectedChildArticleModel != null) {
            mSelectedChildArticleModel.setCommentCount(commentCount);
            mSelectedChildArticleModel.setPostCount(likecount);
            mSelectedChildArticleModel.setHasPost(hasPost);
            notifyDataSetChanged();
        }
    }

    public void setSelectedChildArticleModel(ChildArticleModel articleModel) {
        this.mSelectedChildArticleModel = articleModel;
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_knowledge, null);
            holder.changeTv = (CustomTextView) view.findViewById(R.id.changeTv);
            holder.titleTv = (CustomTextView) view.findViewById(R.id.titleTv);
            holder.contentTv = (CustomTextView) view.findViewById(R.id.contentTv);
            holder.ageTv = (CustomTextView) view.findViewById(R.id.ageTv);
            holder.imageView = (ImageView) view.findViewById(R.id.imageView);
            holder.actionLayout = (FrameLayout) view.findViewById(R.id.actionLayout);
            holder.likeNumTv = (CustomTextView) view.findViewById(R.id.likeNumTv);
            holder.commentNumTv = (CustomTextView) view.findViewById(R.id.commentNumTv);
            holder.changeTv.setOnClickListener(this);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ChildArticleModel article = list.get(position);
        holder.titleTv.setText(article.getTitle());
        holder.contentTv.setText(article.getDesc());

        holder.titleTv.getPaint().setFakeBoldText(true);
        if(article.getPostCount()>0){
            holder.likeNumTv.setVisibility(View.VISIBLE);
        }else{
            holder.likeNumTv.setVisibility(View.INVISIBLE);
        }

        if(article.getCommentCount()>0){
            holder.commentNumTv.setVisibility(View.VISIBLE);
        }else{
            holder.commentNumTv.setVisibility(View.INVISIBLE);
        }
        holder.likeNumTv.setText(Integer.toString(article.getPostCount()));
        holder.commentNumTv.setText(Integer.toString(article.getCommentCount()));
        holder.ageTv.setText(String.format("适合%s岁", article.getAgeTag()));

        if (isShowTipBtn) {
            if (0 == position) {
                holder.actionLayout.setVisibility(View.VISIBLE);
            } else {
                holder.actionLayout.setVisibility(View.GONE);
            }
        }

        if (null == imageUrl || !imageUrl.equalsIgnoreCase(article.getImgUrl())) {
            ImageHelper.displayImage(holder.imageView, article.getImgUrl(), R.drawable.pic_default_find_banner);
            imageUrl = article.getImgUrl();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeTv:
                callback.chanegeArticle();
                break;
        }
    }

    public void notifyDataSetChanged(List<ChildArticleModel> articles) {
        this.list=articles;
        this.notifyDataSetChanged();
    }

    public interface Callback {
        void chanegeArticle();
    }

    class ViewHolder {
        CustomTextView changeTv;
        CustomTextView titleTv;
        CustomTextView contentTv;
        CustomTextView ageTv;
        ImageView imageView;
        FrameLayout actionLayout;
        CustomTextView likeNumTv;
        CustomTextView commentNumTv;

    }
}

