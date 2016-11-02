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
import android.widget.LinearLayout;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.AudioModel;
import cn.ubaby.ubaby.network.response.dto.manger.AudioModelManger;
import cn.ubaby.ubaby.ui.view.BorderImageView;
import cn.ubaby.ubaby.ui.view.CustomTextView;
import cn.ubaby.ubaby.ui.view.ImageTextView;
import cn.ubaby.ubaby.util.ImageHelper;
import cn.ubaby.ubaby.util.Utils;
import cn.ubaby.ubaby.util.cache.UserManager;

/**
 * Created by Administrator on 2016/6/7.
 */
public class SongInSearchAdapter extends BaseAdapter {

    private Context context;
    private List<AudioModel> songs;
    private String search;
    private String babyNick;

    public SongInSearchAdapter(Context context, List<AudioModel> songs, String search) {
        this.context = context;
        this.songs = songs;
        this.search = search;
        babyNick = UserManager.getInstance().findPrimaryBaby().getNickname();

    }

    public void notifyDataSetChanged(List<AudioModel> songs, String search) {
        this.search = search;
        this.songs=songs;
        this.notifyDataSetChanged();
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

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return null != songs.get(position);

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.listitem_song_list_item, null);
            holder.songIv = (BorderImageView) view.findViewById(R.id.songIv);
            holder.songNameTv = (CustomTextView) view.findViewById(R.id.songNameTv);
            holder.songSynopsisTv = (CustomTextView) view.findViewById(R.id.song_synopsis_tv);
            holder.songSourceTv = (CustomTextView) view.findViewById(R.id.song_source_tv);
            holder.songAgeTv = (CustomTextView) view.findViewById(R.id.song_age_tv);
            holder.sourceLayout = (LinearLayout) view.findViewById(R.id.sourceLayout);
            holder.notDownloadTv = (CustomTextView) view.findViewById(R.id.notDownloadTv);
            holder.favItv = (ImageTextView) view.findViewById(R.id.favItv);
            holder.downItv = (ImageTextView) view.findViewById(R.id.downloadItv);
            holder.shareItv = (ImageTextView) view.findViewById(R.id.shareItv);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        AudioModel song = songs.get(position);
        holder.songIv.displayImage(ImageHelper.generateIconUrl(song.getImgUrl()));
        String title = song.getTitle();
        if (!TextUtils.isEmpty(title)) {
            //TODO 不复现无法找出search为空的原因，理论上看search是不会为空的，这里暂时只能在这添加一个非空的判断
            if (!TextUtils.isEmpty(search) && title.toLowerCase().contains(search.toLowerCase())) {
                holder.songNameTv.setText(textColorBuilder(title));
            } else {
                holder.songNameTv.setText(song.getTitle());
            }
        }
        holder.songSynopsisTv.setText(song.getDesc());

        if (!Utils.isListNull(song.getAttribute())) {
            AudioModel.Attribute attribute = song.getAttribute().get(0);
            holder.sourceLayout.setVisibility(View.VISIBLE);
            holder.songSourceTv.setText(attribute.getName() + "：" + attribute.getValue());
        } else {
            holder.sourceLayout.setVisibility(View.INVISIBLE);
        }

        if (AudioModelManger.getIsRightAge(song)) {
            holder.songAgeTv.setText("适合" + babyNick);
            holder.songAgeTv.setTextColor(context.getResources().getColor((R.color.color_ffe400)));
            holder.songAgeTv.setBackgroundResource(R.drawable.border_rectangle_blue);
        } else {
            holder.songAgeTv.setText("适合" + Utils.getAge(song.getAgeStart()) + "-" + Utils.getAge(song.getAgeEnd()) + "岁");
            holder.songAgeTv.setTextColor(context.getResources().getColor((R.color.color_ededed)));
            holder.songAgeTv.setBackgroundResource(R.drawable.border_rectangle_gray);
        }

        if (!song.isDown()) {
            holder.notDownloadTv.setVisibility(View.VISIBLE);
        } else {
            holder.notDownloadTv.setVisibility(View.GONE);
        }

        Utils.setListViewItemChildView(context, song, holder.favItv, holder.downItv, holder.shareItv);
        return view;

    }

    class ViewHolder {
        BorderImageView songIv;
        CustomTextView songNameTv;
        CustomTextView songSynopsisTv;
        CustomTextView songSourceTv;
        CustomTextView songAgeTv;
        LinearLayout sourceLayout;
        ImageTextView favItv;
        ImageTextView downItv;
        ImageTextView shareItv;
        CustomTextView notDownloadTv;
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
