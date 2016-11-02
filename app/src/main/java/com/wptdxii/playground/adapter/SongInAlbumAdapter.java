package com.wptdxii.playground.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.AudioModel;
import cn.ubaby.ubaby.ui.view.BorderImageView;
import cn.ubaby.ubaby.ui.view.CustomTextView;
import cn.ubaby.ubaby.ui.view.ImageTextView;
import cn.ubaby.ubaby.util.ImageHelper;
import cn.ubaby.ubaby.util.Utils;

/**
 * 专辑中歌曲列表
 * Created by wenlong.lu
 * on 2015/7/16.
 */
public class SongInAlbumAdapter extends BaseAdapter {

    private List<AudioModel> audios;
    private Context context;

    public SongInAlbumAdapter(Context context, List<AudioModel> songs) {
        this.context = context;
        this.audios = songs;

    }


    @Override
    public int getCount() {
        return audios.size();
    }

    @Override
    public Object getItem(int position) {
        return audios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listitem_song_list_item, null);
            holder = new ViewHolder();
            holder.songIv = (BorderImageView) view.findViewById(R.id.songIv);
            holder.songNameTv = (CustomTextView) view.findViewById(R.id.songNameTv);
            holder.song_synopsis_tv = (CustomTextView) view.findViewById(R.id.song_synopsis_tv);
            holder.song_source_tv = (CustomTextView) view.findViewById(R.id.song_source_tv);
            holder.song_age_tv = (CustomTextView) view.findViewById(R.id.song_age_tv);
            holder.songAgeTv = (CustomTextView) view.findViewById(R.id.song_age_tv);
            holder.notDownloadTv = (CustomTextView) view.findViewById(R.id.notDownloadTv);
            holder.sourceLayout = (LinearLayout) view.findViewById(R.id.sourceLayout);
            holder.noMoreDataTv = (CustomTextView) view.findViewById(R.id.no_more_data_tv);
            holder.favItv = (ImageTextView) view.findViewById(R.id.favItv);
            holder.downItv = (ImageTextView) view.findViewById(R.id.downloadItv);
            holder.shareItv = (ImageTextView) view.findViewById(R.id.shareItv);
            holder.songAgeTv.setVisibility(View.GONE);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        AudioModel audio = audios.get(position);
        holder.songIv.displayImage(ImageHelper.generateIconUrl(audio.getImgUrl()));
        holder.songNameTv.setText(audio.getTitle());
        holder.song_synopsis_tv.setText(audio.getDesc());

        if (!Utils.isListNull(audio.getAttribute())) {
            AudioModel.Attribute attribute = audio.getAttribute().get(0);
            holder.sourceLayout.setVisibility(View.VISIBLE);
            holder.song_source_tv.setText(attribute.getName() + "：" + attribute.getValue());
        } else {
            holder.sourceLayout.setVisibility(View.INVISIBLE);
        }

        holder.song_age_tv.setVisibility(View.GONE);

        if (!audio.isDown()) {
            holder.notDownloadTv.setVisibility(View.VISIBLE);
        } else {
            holder.notDownloadTv.setVisibility(View.GONE);
        }

        Utils.setListViewItemChildView(context, audio, holder.favItv, holder.downItv, holder.shareItv);

        return view;
    }


    public void notifyDataSetChanged(List<AudioModel> songs) {
        this.audios=songs;
        this.notifyDataSetChanged();
    }


    class ViewHolder {
        BorderImageView songIv;
        CustomTextView songNameTv;
        CustomTextView song_synopsis_tv;
        CustomTextView song_source_tv;
        CustomTextView song_age_tv;
        LinearLayout sourceLayout;
        TextView noMoreDataTv;
        CustomTextView songAgeTv;
        CustomTextView notDownloadTv;
        ImageTextView favItv;
        ImageTextView downItv;
        ImageTextView shareItv;
    }
}
