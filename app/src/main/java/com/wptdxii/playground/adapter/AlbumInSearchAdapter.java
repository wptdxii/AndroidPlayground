package com.wptdxii.playground.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
 * Created by Administrator on 2016/6/7.
 */
public class AlbumInSearchAdapter extends BaseAdapter {

    private Context context;
    private List<AlbumModel> albums;
    private String babyNick;
//    private int ablumLayoutW;
    private String search;

    public AlbumInSearchAdapter(Context context, List<AlbumModel> albums, String search) {
        this.context = context;
        this.albums = albums;
        this.search = search;
        babyNick = UserManager.getInstance().findPrimaryBaby().getNickname();
//        ablumLayoutW = Constants.SCREENW - DensityUtils.dip2px(context, (float) (75.3 + 8.3 + 12));
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
    public View getView(int position, View view, ViewGroup parent) {
        //专辑
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.listitem_album_list_item, null);
            holder.albumIv = (BorderImageView) view.findViewById(R.id.albumIv);
            holder.albumTvTop = (CustomTextView) view.findViewById(R.id.album_tv_top);
            holder.albumAgeTv = (CustomTextView) view.findViewById(R.id.album_age_tv);
            holder.albumCountTv = (CustomTextView) view.findViewById(R.id.album_count_tv);
            holder.containsTv = (CustomTextView) view.findViewById(R.id.containsTv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        AlbumModel album = albums.get(position);
        String title = album.getTitle();
        if (!TextUtils.isEmpty(title)) {
            if (title.toLowerCase().contains(search.toLowerCase())) {
                holder.albumTvTop.setText(textColorBuilder(title));
            } else {
                holder.albumTvTop.setText(album.getTitle());
            }

        }
        if (album.isHasExactMatch()) {
            holder.containsTv.setVisibility(View.VISIBLE);
            holder.containsTv.setText("(包含\"" + search + "\")");
        } else {
            holder.containsTv.setVisibility(View.GONE);
        }
//        ImageHelper.displayImage(holder.albumIv, album.getAlbumIcon(), R.drawable.pic_default_album);
        holder.albumIv.displayImage(ImageHelper.generateAlbumIconUrl(album.getImgUrl()));
//        holder.albumIv.setBorderSize(Constants.BORDERW, Constants.BORDERH, null);
//        holder.albumIv.setBackground(album.getRecommendImage(Constants.BORDERW), 1);
        holder.albumCountTv.setText("共" + album.getAudioCount() + "个");
        if (AlbumModelManger.getIsRightAge(album)) {
            holder.albumAgeTv.setText("适合" + babyNick);
            holder.albumAgeTv.setTextColor(context.getResources().getColor((R.color.color_ffe400)));
            holder.albumAgeTv.setBackgroundResource(R.drawable.border_rectangle_blue);
        } else {
            holder.albumAgeTv.setText("适合" + Utils.getAge(album.getAgeStart()) + "-" + Utils.getAge(album.getAgeEnd()) + "岁");
            holder.albumAgeTv.setTextColor(context.getResources().getColor((R.color.color_ededed)));
            holder.albumAgeTv.setBackgroundResource(R.drawable.border_rectangle_gray);
        }

//        Utils.setListViewItemWidth(context, holder.albumTvTop, holder.albumAgeTv, ablumLayoutW);
        return view;
    }

    public void notifyDataSetChanged(List<AlbumModel> albums, String search) {
        this.search = search;
        this.albums = albums;
        this.notifyDataSetChanged();
    }

    class ViewHolder {
        BorderImageView albumIv;
        CustomTextView albumTvTop;
        CustomTextView albumAgeTv;
        CustomTextView albumCountTv;
        CustomTextView containsTv;
    }

    private SpannableStringBuilder textColorBuilder(String title) {
        String toLower = title.toLowerCase();
        String searchToLower = search.toLowerCase();
        int start = toLower.indexOf(searchToLower);
        int end = start + search.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(title);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.color_2dbdff));
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

}
