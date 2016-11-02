package com.wptdxii.playground.adapter;

import android.content.Context;
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
 * 专辑中歌曲列表
 * Created by wenlong.lu
 * on 2015/7/16.
 */
public class CategorySongAdapter extends BaseAdapter {

    private Context context;
    private List<AudioModel> audios;
    private String babyNick;

    public CategorySongAdapter(Context context, List<AudioModel> audios) {
        this.context = context;
        this.audios = audios;
        babyNick = UserManager.getInstance().findPrimaryBaby().getNickname();

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
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.listitem_song_list_item, null);
            holder.songIv = (BorderImageView) view.findViewById(R.id.songIv);
            holder.songNameTv = (CustomTextView) view.findViewById(R.id.songNameTv);
            holder.song_synopsis_tv = (CustomTextView) view.findViewById(R.id.song_synopsis_tv);
            holder.song_source_tv = (CustomTextView) view.findViewById(R.id.song_source_tv);
            holder.ageTv = (CustomTextView) view.findViewById(R.id.song_age_tv);
            holder.sourceLayout = (LinearLayout) view.findViewById(R.id.sourceLayout);
            holder.notDownloadTv = (CustomTextView) view.findViewById(R.id.notDownloadTv);
            holder.favItv = (ImageTextView) view.findViewById(R.id.favItv);
            holder.downItv = (ImageTextView) view.findViewById(R.id.downloadItv);
            holder.shareItv = (ImageTextView) view.findViewById(R.id.shareItv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final AudioModel audio = audios.get(position);
        holder.songNameTv.setText(audio.getTitle());
        holder.songIv.displayImage(ImageHelper.generateIconUrl(audio.getImgUrl()));
        holder.song_synopsis_tv.setText(audio.getDesc());

        if (!Utils.isListNull(audio.getAttribute())) {
            AudioModel.Attribute attribute = audio.getAttribute().get(0);
            holder.sourceLayout.setVisibility(View.VISIBLE);
            holder.song_source_tv.setText(attribute.getName() + "：" + attribute.getValue());
        } else {
            holder.sourceLayout.setVisibility(View.INVISIBLE);
        }

        if (AudioModelManger.getIsRightAge(audio)) {
            holder.ageTv.setText("适合" + babyNick);
            holder.ageTv.setTextColor(context.getResources().getColor((R.color.color_ffe400)));
            holder.ageTv.setBackgroundResource(R.drawable.border_rectangle_blue);
        } else {
            holder.ageTv.setText("适合" + Utils.getAge(audio.getAgeStart()) + "-" + Utils.getAge(audio.getAgeEnd()) + "岁");
            holder.ageTv.setTextColor(context.getResources().getColor((R.color.color_ededed)));
            holder.ageTv.setBackgroundResource(R.drawable.border_rectangle_gray);
        }

        if (!audio.isDown()) {
            holder.notDownloadTv.setVisibility(View.VISIBLE);
        } else {
            holder.notDownloadTv.setVisibility(View.GONE);
        }

        Utils.setListViewItemChildView(context, audio, holder.favItv, holder.downItv, holder.shareItv);

        return view;

    }

    public void notifyDataSetChanged(List<AudioModel> audios) {
        this.audios = audios;
        super.notifyDataSetChanged();
    }


    class ViewHolder {
        BorderImageView songIv;
        CustomTextView songNameTv;
        CustomTextView song_synopsis_tv;
        CustomTextView song_source_tv;
        CustomTextView notDownloadTv;
        CustomTextView ageTv;
        LinearLayout sourceLayout;

        ImageTextView favItv;
        ImageTextView downItv;
        ImageTextView shareItv;
    }
}
