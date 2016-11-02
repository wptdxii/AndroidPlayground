package com.wptdxii.playground.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.AlbumModel;
import cn.ubaby.ubaby.network.response.dto.manger.AlbumModelManger;
import cn.ubaby.ubaby.ui.view.BorderImageView;
import cn.ubaby.ubaby.ui.view.CustomTextView;
import cn.ubaby.ubaby.util.ImageHelper;
import cn.ubaby.ubaby.util.Utils;
import cn.ubaby.ubaby.util.cache.UserManager;

/**
 * 专辑列表
 * Created by wenlong.lu
 * on 2015/7/16.
 */
public class AlbumAdapter extends BaseAdapter {

    private Context context;
    private List<AlbumModel> albums;
    private String babyNick;
    private boolean isXmly;

    public AlbumAdapter(Context context, List<AlbumModel> albums, boolean isXmly) {
        this.context = context;
        this.albums = albums;
        this.isXmly = isXmly;
        babyNick = UserManager.getInstance().findPrimaryBaby().getNickname();
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int position) {
        return albums.get(position);
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
            view = LayoutInflater.from(context).inflate(R.layout.listitem_album_list_item, null);
            holder.albumIv = (BorderImageView) view.findViewById(R.id.albumIv);
            holder.albumTvTop = (CustomTextView) view.findViewById(R.id.album_tv_top);
            holder.albumAgeTv = (CustomTextView) view.findViewById(R.id.album_age_tv);
            holder.albumCountTv = (CustomTextView) view.findViewById(R.id.album_count_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final AlbumModel albumModel = albums.get(position);
        holder.albumTvTop.setText(albumModel.getTitle());
        holder.albumIv.displayImage(ImageHelper.generateIconUrl(albumModel.getImgUrl()));

        holder.albumCountTv.setText("共" + albumModel.getAudioCount() + "个");

        if (isXmly) {
            holder.albumAgeTv.setVisibility(View.INVISIBLE);
        } else {
            holder.albumAgeTv.setVisibility(View.VISIBLE);
        }
        if (AlbumModelManger.getIsRightAge(albumModel)) {
            holder.albumAgeTv.setText("适合" + babyNick);
            holder.albumAgeTv.setTextColor(context.getResources().getColor((R.color.color_ffe400)));
            holder.albumAgeTv.setBackgroundResource(R.drawable.border_rectangle_blue);
        } else {
            holder.albumAgeTv.setText("适合" + Utils.getAge(albumModel.getAgeStart()) + "-" + Utils.getAge(albumModel.getAgeEnd()) + "岁");
            holder.albumAgeTv.setTextColor(context.getResources().getColor((R.color.color_ededed)));
            holder.albumAgeTv.setBackgroundResource(R.drawable.border_rectangle_gray);
        }

        return view;
    }

    public void notifyDataSetChanged(List<AlbumModel> albums) {
        this.albums = albums;
        this.notifyDataSetChanged();
    }


    class ViewHolder {
        BorderImageView albumIv;
        CustomTextView albumTvTop;
        CustomTextView albumAgeTv;
        CustomTextView albumCountTv;
    }
}
