package com.wptdxii.playground.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devin.utils.DateUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.bean.enums.UserStatus;
import cn.ubaby.ubaby.network.response.dto.CommentModel;
import cn.ubaby.ubaby.ui.view.CustomTextView;
import cn.ubaby.ubaby.util.ImageHelper;
import cn.ubaby.ubaby.util.cache.UserManager;

/**
 * 专辑列表
 * Created by zc
 * on 2015/7/25.
 */
public class CommentAdapter extends BaseAdapter implements View.OnClickListener{

    private Context context;
    private List<CommentModel> comments;
    private Callback callback;
    private long userId;
    private String path;
    private String userNick;

    public CommentAdapter(Context context, List<CommentModel> comments, Callback callback) {
        this.context = context;
        this.comments = comments;
        this.callback = callback;
        if (UserManager.getInstance().getUserInfo().getStatus() == UserStatus.Login) {
            userId = UserManager.getInstance().getUserInfo().getId();
            path = UserManager.getInstance().getUserInfo().getPortrait();
            userNick = UserManager.getInstance().getUserInfo().getNickname();
        }
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.comment_listview_item_layout, null);
            holder.userIv = (RoundedImageView) view.findViewById(R.id.userIv);
            holder.userNameTv = (CustomTextView) view.findViewById(R.id.userNameTv);
            holder.likeNumTv = (CustomTextView) view.findViewById(R.id.likeNumTv);
            holder.likeIv = (ImageView) view.findViewById(R.id.likeIv);
            holder.timeTv = (CustomTextView) view.findViewById(R.id.timeTv);
            holder.contentTv = (TextView) view.findViewById(R.id.contentTv);
            holder.deleteTv = (CustomTextView) view.findViewById(R.id.deleteTv);
            holder.likeIv.setOnClickListener(this);
            holder.deleteTv.setOnClickListener(this);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final CommentModel commentModel = comments.get(position);

        holder.userNameTv.setText(commentModel.getFromUname());
        holder.timeTv.setText("发表时间: " + DateUtils.friendlyTimeUbaby(commentModel.getCreateTime()));
//        try {
//            String s = URLDecoder.decode(commentModel.getContent(), "UTF-8");
//            holder.contentTv.setText(s + "");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        holder.contentTv.setText(commentModel.getContent());
        holder.likeNumTv.setText(commentModel.getPostCount() + "");

        if (commentModel.getFromUid() == userId) {
            ImageHelper.displayImage(holder.userIv, path, R.drawable.photo_default);
            holder.deleteTv.setVisibility(View.VISIBLE);
            holder.userNameTv.setText(userNick);
        } else {
            holder.deleteTv.setVisibility(View.GONE);
            ImageHelper.displayImage(holder.userIv, commentModel.getFromUphoto(), R.drawable.photo_default);
        }

        if(commentModel.isHasPost()){
            holder.likeIv.setBackgroundResource(R.drawable.play_like_selectored);
        } else {
            holder.likeIv.setBackgroundResource(R.drawable.play_like_normal);
        }

        holder.likeIv.setTag(R.id.tag_comment, commentModel);
        holder.deleteTv.setTag(R.id.tag_comment, commentModel);

        return view;
    }

    public void notifyDataSetChanged(List<CommentModel> comments) {
        this.comments = comments;
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.likeIv:
                CommentModel commentModel = (CommentModel) v.getTag(R.id.tag_comment);
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.comment_like_anim));
                callback.likeClick(v, commentModel);
                break;
            case R.id.deleteTv:
                commentModel = (CommentModel) v.getTag(R.id.tag_comment);
                callback.deledteClick(v, commentModel);
                break;
        }
    }

    public interface Callback {
        void likeClick(View v, CommentModel commentModel);
        void deledteClick(View v, CommentModel commentModel);
    }

    class ViewHolder {
        RoundedImageView userIv;
        CustomTextView userNameTv;
        CustomTextView likeNumTv;
        ImageView likeIv;
        CustomTextView timeTv;
        TextView contentTv;
        CustomTextView deleteTv;
    }
}
