package com.wptdxii.playground.adapter;


import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.AudioModel;
import cn.ubaby.ubaby.ui.view.CustomTextView;

/**
 * Created by wenlong.lu
 * on 2015/11/2.
 */
public class PlayListAdapter extends BaseAdapter {

    private List<AudioModel> songs;

    private int currentIndex;

    private boolean isPlaying=true;

    public PlayListAdapter(List<AudioModel> songs) {
        this.songs = songs;
    }

    public void setCurrentIndex(int index) {
        currentIndex = index;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    public void setPlayAnimStatus(boolean isPlaying){
        this.isPlaying=isPlaying;
        this.notifyDataSetChanged();
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_categoty_music_list, null);
            holder = new ViewHolder();
            holder.itemLayout=(RelativeLayout)view.findViewById(R.id.itemLayout);
            holder.itemId = (CustomTextView) view.findViewById(R.id.itemId);
            holder.songNameView = (CustomTextView) view.findViewById(R.id.tv_song_name);
            holder.playingIv=(ImageView)view.findViewById(R.id.playingIv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final AudioModel song = songs.get(position);
        holder.songNameView.setText(song.getTitle());

        holder.itemId.setText((position + 1)+"");

        if (position == currentIndex) {
            holder.itemId.setTextColor(parent.getContext().getResources().getColor(R.color.color_ffe400));
            holder.songNameView.setTextColor(parent.getContext().getResources().getColor(R.color.color_ffe400));
            holder.itemId.setBackgroundResource(R.drawable.border_round_player_song_id_press);
            holder.itemLayout.setBackgroundResource(R.drawable.border_rectangle_player_song_item_press);
            holder.playingIv.setVisibility(View.VISIBLE);
            AnimationDrawable  anim = (AnimationDrawable) holder.playingIv.getBackground();
            anim.setOneShot(false);
            if(isPlaying){
                anim.start();
            }else{
                anim.stop();
            }
        } else {
            holder.playingIv.setVisibility(View.INVISIBLE);
            holder.itemId.setTextColor(parent.getContext().getResources().getColor(R.color.color_fff7b3));
            holder.songNameView.setTextColor(parent.getContext().getResources().getColor(R.color.color_fff7b3));
            holder.itemId.setBackgroundResource(R.drawable.border_round_player_song_id_normal);
            holder.itemLayout.setBackgroundResource(R.drawable.border_rectangle_player_song_item_normal);
        }

        return view;

    }

    private class ViewHolder {
        private RelativeLayout itemLayout;
        private CustomTextView itemId;
        private CustomTextView songNameView;
        private ImageView playingIv;
    }
}
