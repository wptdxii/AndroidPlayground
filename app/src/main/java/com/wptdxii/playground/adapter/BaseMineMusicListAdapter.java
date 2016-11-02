package com.wptdxii.playground.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.AudioModel;
import cn.ubaby.ubaby.network.response.dto.manger.FavoriteManger;
import cn.ubaby.ubaby.ui.view.BorderImageView;
import cn.ubaby.ubaby.ui.view.CustomTextView;
import cn.ubaby.ubaby.ui.view.ImageTextView;
import cn.ubaby.ubaby.util.Constants;
import cn.ubaby.ubaby.util.ImageHelper;
import cn.ubaby.ubaby.util.Utils;

/**
 * Created by zc on 2015/11/6.
 */
public class BaseMineMusicListAdapter extends BaseAdapter {
    private Context context;
    private List<AudioModel> songs = new ArrayList<>();
    private int type;
    private String imageUrl;

    public BaseMineMusicListAdapter(Context context, List<AudioModel> songs, int type) {
        this.context = context;
        this.songs = songs;
        this.type = type;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void notifyDataSetChanged(List<AudioModel> songs) {
        this.songs = songs;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.base_song_list_item, null);
            holder.imageView = (BorderImageView) view.findViewById(R.id.songIv);
            holder.songNameTextView = (CustomTextView) view.findViewById(R.id.songNameTv);
            holder.songTypeTextView = (CustomTextView) view.findViewById(R.id.song_age_tv);
            holder.sourceTv = (CustomTextView) view.findViewById(R.id.sourcOresizeTv);
            holder.optionLeftBtn = (ImageTextView) view.findViewById(R.id.favItv);
            holder.optionCenterBtn = (ImageTextView) view.findViewById(R.id.downloadItv);
            holder.optionRightBtn = (ImageTextView) view.findViewById(R.id.shareItv);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final AudioModel song = songs.get(position);


        if (song.isXimalayaSong) {
            holder.optionCenterBtn.setText("分享");
            holder.optionCenterBtn.setTextColor(context.getResources().getColor(R.color.color_df9f07));
            holder.optionCenterBtn.setDrawableTop(R.drawable.ic_list_share_disable);
        } else {
            holder.optionCenterBtn.setText("分享");
            holder.optionCenterBtn.setDrawableTop(R.drawable.selector_btn_listitem_share);
        }

        if (type == Constants.MINE_TYPE_COLLECTION) {
            holder.optionLeftBtn.setText("离线");
            holder.optionLeftBtn.setDrawableTop(R.drawable.selector_bottom_right_download_grey);

            holder.optionRightBtn.setText("删除");
            holder.optionRightBtn.setDrawableTop(R.drawable.selector_btn_listitem_delete);

            if (song.isDown()) {
                if (Utils.isDownloaded(song)) {
                    holder.optionLeftBtn.setText("已离线");
                } else {
                    holder.optionLeftBtn.setText("离线");
                }
                holder.optionLeftBtn.setDrawableTop(R.drawable.selector_bottom_right_download_grey);
            } else {
                holder.optionLeftBtn.setText("离线");
                holder.optionLeftBtn.setTextColor(context.getResources().getColor(R.color.color_df9f07));
                holder.optionLeftBtn.setDrawableTop(R.drawable.ic_list_download_normal_disable);
            }

        } else if (type == Constants.MINE_TYPE_DOWNLODAD) {

            holder.optionLeftBtn.setText("收藏");
            holder.optionLeftBtn.setDrawableTop(R.drawable.selector_bottom_left_collection_grey);

            holder.optionRightBtn.setText("删除");
            holder.optionRightBtn.setDrawableTop(R.drawable.selector_btn_listitem_delete);

            if (song.isFavorite()) {
                if (song.isXimalayaSong) {
                    holder.optionLeftBtn.setText("收藏");
                    holder.optionLeftBtn.setTextColor(context.getResources().getColor(R.color.color_df9f07));
                    holder.optionLeftBtn.setDrawableTop(R.drawable.ic_list_like_disable);
                } else if (FavoriteManger.getInstance().isContains(song.getId())) {
                    holder.optionLeftBtn.setText("已收藏");
                    holder.optionLeftBtn.setDrawableTop(R.drawable.ic_list_like_already);
                } else {
                    holder.optionLeftBtn.setText("收藏");
                    holder.optionLeftBtn.setDrawableTop(R.drawable.selector_bottom_left_collection_grey);
                }
            } else {
                holder.optionLeftBtn.setText("收藏");
                holder.optionLeftBtn.setTextColor(context.getResources().getColor(R.color.color_df9f07));
                holder.optionLeftBtn.setDrawableTop(R.drawable.ic_list_like_disable);
            }

        } else if (type == Constants.MINE_TYPE_HISTORY) {

            holder.optionLeftBtn.setText("收藏");
            holder.optionLeftBtn.setDrawableTop(R.drawable.selector_bottom_left_collection_grey);

            if (song.isDown()) {
                if (Utils.isDownloaded(song)) {
                    holder.optionRightBtn.setText("已离线");
                } else {
                    holder.optionRightBtn.setText("离线");
                }
                holder.optionRightBtn.setDrawableTop(R.drawable.selector_bottom_right_download_grey);
            } else {
                holder.optionRightBtn.setText("离线");
                holder.optionRightBtn.setTextColor(context.getResources().getColor(R.color.color_df9f07));
                holder.optionRightBtn.setDrawableTop(R.drawable.ic_list_download_normal_disable);
            }

            if (song.isFavorite()) {
                if (song.isXimalayaSong) {
                    holder.optionLeftBtn.setText("收藏");
                    holder.optionLeftBtn.setTextColor(context.getResources().getColor(R.color.color_df9f07));
                    holder.optionLeftBtn.setDrawableTop(R.drawable.ic_list_like_disable);
                } else if (FavoriteManger.getInstance().isContains(song.getId())) {
                    holder.optionLeftBtn.setText("已收藏");
                    holder.optionLeftBtn.setDrawableTop(R.drawable.ic_list_like_already);
                } else {
                    holder.optionLeftBtn.setText("收藏");
                    holder.optionLeftBtn.setDrawableTop(R.drawable.selector_bottom_left_collection_grey);
                }
            } else {
                holder.optionLeftBtn.setText("收藏");
                holder.optionLeftBtn.setTextColor(context.getResources().getColor(R.color.color_df9f07));
                holder.optionLeftBtn.setDrawableTop(R.drawable.ic_list_like_disable);
            }
        }
        holder.imageView.displayImage(ImageHelper.generateIconUrl(song.getImgUrl()));
        holder.songNameTextView.setText(song.getTitle());

        if (TextUtils.isEmpty(song.getCtgTitle())) {
            holder.songTypeTextView.setVisibility(View.GONE);
        } else {
            String ctgTitle = song.getCtgTitle();
            holder.songTypeTextView.setVisibility(View.VISIBLE);
            holder.songTypeTextView.setText(ctgTitle);
        }

        if (!Utils.isListNull(song.getAttribute())) {
            AudioModel.Attribute attribute = song.getAttribute().get(0);
            if (!TextUtils.isEmpty(attribute.getName()) && !TextUtils.isEmpty(attribute.getValue())) {
                holder.sourceTv.setVisibility(View.VISIBLE);
                holder.sourceTv.setText(attribute.getName() + "：" + attribute.getValue());
            } else {
                holder.sourceTv.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.sourceTv.setVisibility(View.INVISIBLE);
        }

        return view;
    }


    class ViewHolder {
        BorderImageView imageView;
        CustomTextView songNameTextView;
        CustomTextView songTypeTextView;
        CustomTextView sourceTv;

        ImageTextView optionLeftBtn;
        ImageTextView optionCenterBtn;
        ImageTextView optionRightBtn;
    }
}
