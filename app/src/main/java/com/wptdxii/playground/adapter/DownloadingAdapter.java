package com.wptdxii.playground.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.AudioModel;
import cn.ubaby.ubaby.ui.view.BorderImageView;
import cn.ubaby.ubaby.util.ImageHelper;
import cn.ubaby.ubaby.util.Trace;
import git.dzc.downloadmanagerlib.download.DownloadManager;
import git.dzc.downloadmanagerlib.download.DownloadStatus;

/**
 * Created by Administrator on 2016/4/28.
 */
public class DownloadingAdapter extends BaseAdapter {

    private Context context;
    private List<AudioModel> songs;

    private Map<String, Float> percentMap = new HashMap<>();
    private Map<String, String> sizeMap = new HashMap<>();

    public DownloadingAdapter(Context context, List<AudioModel> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public void notifyDataSetChanged() {
        Trace.i("songs hash" + songs.hashCode());

        Collections.sort(songs, new Comparator<AudioModel>() {
            public int compare(AudioModel arg0, AudioModel arg1) {
                return (int) (arg0.getId() - arg1.getId());
            }
        });
        super.notifyDataSetChanged();
    }

    public void updateProgress(long id, float progress, String bytesWritten, String totalSize) {

        percentMap.put(String.valueOf(id), progress);
        sizeMap.put(String.valueOf(id), bytesWritten + "/" + totalSize);

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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.listitem_downloading, null);
            holder.imageView = (BorderImageView) view.findViewById(R.id.songIv);
            holder.songNameTextView = (TextView) view.findViewById(R.id.songNameTv);
            holder.songTypeTextView = (TextView) view.findViewById(R.id.song_age_tv);
            holder.statusTv = (TextView) view.findViewById(R.id.statusTv);
            holder.progressTv = (TextView) view.findViewById(R.id.progressTv);
            holder.lineView = view.findViewById(R.id.lineView);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final AudioModel song = songs.get(position);

        if (null != percentMap.get(String.valueOf(song.getId()))) {
            Float aFloat = percentMap.get(String.valueOf(song.getId()));
            holder.progressBar.setProgress(aFloat.intValue());
        } else {
            holder.progressBar.setProgress(0);
        }

        if (null != sizeMap.get(String.valueOf(song.getId()))) {
            holder.progressTv.setText(sizeMap.get(String.valueOf(song.getId())));
        } else {
            holder.progressTv.setText("");
        }
        int downloadStatus = DownloadManager.getInstance().getDownloadStatus(song.downloadTaskId());
        Log.i(" 下载状态", song.getTitle() + "--->  " + downloadStatus);
        if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
            holder.statusTv.setVisibility(View.VISIBLE);
            holder.statusTv.setText("离线失败");
            view.setEnabled(true);
        }
//        else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE){
//            holder.statusTv.setVisibility(View.VISIBLE);
//            holder.statusTv.setText("等待下载");
//            view.setEnabled(true);
//
//        }
        else  {
            holder.statusTv.setVisibility(View.GONE);
            view.setEnabled(false);
        }



        init(holder, holder.imageView, holder.songNameTextView, holder.songTypeTextView, holder.lineView, song, position);
        view.setTag(R.id.tag_song, song);
        return view;
    }



    private void init(ViewHolder holder, BorderImageView imageView, TextView songNameTextView, TextView songTypeTextView, View lineView, AudioModel song, int position) {


        if (!song.getImgUrl().equals(holder.imageUrl)) {
            holder.imageUrl = song.getImgUrl();
            imageView.displayImage(ImageHelper.generateIconUrl(song.getImgUrl()));
        }

        songNameTextView.setText(song.getTitle());

        if (TextUtils.isEmpty(song.getCtgTitle())) {
            songTypeTextView.setVisibility(View.GONE);
        } else {
            String ctgTitle = song.getCtgTitle();
            songTypeTextView.setVisibility(View.VISIBLE);
            songTypeTextView.setText(ctgTitle);
        }

        if (songs.size() - 1 == position) {
            lineView.setVisibility(View.GONE);
        } else {
            lineView.setVisibility(View.VISIBLE);
        }
    }

    class ViewHolder {
        String imageUrl;
        BorderImageView imageView;
        TextView songNameTextView;
        TextView songTypeTextView;
        TextView statusTv;
        TextView progressTv;
        ProgressBar progressBar;
        View lineView;
    }

}
