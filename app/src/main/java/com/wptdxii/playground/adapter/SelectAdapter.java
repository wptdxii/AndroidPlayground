package com.wptdxii.playground.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.AudioModel;
import cn.ubaby.ubaby.ui.view.BorderImageView;
import cn.ubaby.ubaby.ui.view.CustomTextView;
import cn.ubaby.ubaby.util.Utils;

/**
 * Created by Administrator on 2016/5/12.
 */
public class SelectAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<AudioModel> songs;
    private String type;
    private Callback callback;

    public SelectAdapter(Context context, List<AudioModel> songs, String type, Callback callback) {
        this.context = context;
        this.callback = callback;
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

    public void checkAll() {
        for (AudioModel song : songs) {
            song.setIsSelector(true);
        }
        this.notifyDataSetChanged();
    }

    public void clearAll() {
        for (AudioModel song : songs) {
            song.setIsSelector(false);
        }
        this.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(List<AudioModel> songs) {
        this.songs = songs;
        this.notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.base_music_select_list_item, null);
            holder.songIv = (BorderImageView) view.findViewById(R.id.songIv);
            holder.songNameTv = (CustomTextView) view.findViewById(R.id.songNameTv);
            holder.song_synopsis_tv = (CustomTextView) view.findViewById(R.id.song_synopsis_tv);
            holder.song_source_tv = (CustomTextView) view.findViewById(R.id.song_source_tv);
            holder.song_age_tv = (CustomTextView) view.findViewById(R.id.song_age_tv);
            holder.music_cb = (CheckBox) view.findViewById(R.id.music_cb);
            holder.sourceLayout = (LinearLayout) view.findViewById(R.id.sourceLayout);
            holder.notDownloadTv = (CustomTextView) view.findViewById(R.id.notDownloadTv);
            holder.music_cb.setOnClickListener(this);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        AudioModel song = songs.get(position);
        holder.music_cb.setTag(R.id.tag_song, song);
        holder.songIv.displayImage(song.getImgUrl());
        holder.songNameTv.setText(song.getTitle());
        holder.song_synopsis_tv.setText(song.getDesc());
        holder.music_cb.setChecked(song.isSelector());

        if (type.equals("多选")) {
            holder.songNameTv.setLines(1);
            holder.songNameTv.setSingleLine(true);
            holder.songNameTv.setEllipsize(TextUtils.TruncateAt.END);
            holder.song_synopsis_tv.setVisibility(View.VISIBLE);
        } else {
            holder.songNameTv.setMaxLines(2);
            holder.songNameTv.setEllipsize(TextUtils.TruncateAt.END);
            holder.song_synopsis_tv.setVisibility(View.GONE);
        }

        holder.song_age_tv.setVisibility(View.VISIBLE);
        holder.sourceLayout.setVisibility(View.GONE);

        if (song.isDown() && !type.equals("多选")) {
            String ctgTitle = song.getCtgTitle();
            holder.song_age_tv.setVisibility(View.VISIBLE);
            holder.song_age_tv.setText(ctgTitle);
        } else if (song.isDown() && type.equals("多选")) {
            holder.song_age_tv.setVisibility(View.GONE);
        } else if (!song.isDown() && type.equals("多选")) {
            holder.song_age_tv.setVisibility(View.GONE);
            holder.notDownloadTv.setVisibility(View.VISIBLE);
        }

        if (!Utils.isListNull(song.getAttribute())) {
            AudioModel.Attribute attribute = song.getAttribute().get(0);
            holder.sourceLayout.setVisibility(View.VISIBLE);
            holder.song_source_tv.setText(attribute.getName() + "：" + attribute.getValue());
        } else {
            holder.sourceLayout.setVisibility(View.INVISIBLE);
        }

        if (type.equals("离线收听")) {
            holder.song_source_tv.setTextColor(context.getResources().getColor(R.color.source));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_cb:
                AudioModel song = (AudioModel) v.getTag(R.id.tag_song);
                callback.checkBoxClick(v, song);
                break;
        }
    }

    public interface Callback {
        void checkBoxClick(View v, AudioModel song);
    }


    class ViewHolder {
        CheckBox music_cb;
        BorderImageView songIv;
        CustomTextView songNameTv;
        CustomTextView song_synopsis_tv;
        CustomTextView song_age_tv;
        CustomTextView song_source_tv;
        LinearLayout sourceLayout;
        CustomTextView notDownloadTv;
    }

}
