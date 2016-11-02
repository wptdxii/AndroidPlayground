package com.wptdxii.playground.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.ubaby.ubaby.R;
import cn.ubaby.ubaby.network.response.dto.SceneModel;
import cn.ubaby.ubaby.ui.view.SceneView;
import cn.ubaby.ubaby.util.ImageHelper;
import cn.ubaby.ubaby.util.Trace;

/**
 * Created by Administrator on 2016/4/20.
 */
public class SceneAdapter extends BaseAdapter implements View.OnClickListener {

    private List<SceneModel> scenes;
    private List<SceneCell> sceneCells = new ArrayList<>();

    private Callback callback;

    public SceneAdapter(List<SceneModel> scenes, Callback callback) {
        this.scenes = scenes;
        setScenes();
        this.callback = callback;
    }


    @Override
    public void notifyDataSetChanged() {
        setScenes();
        super.notifyDataSetChanged();
    }

    /**
     * 重新解析数据
     */
    private void setScenes() {
        sceneCells.clear();
        for (int i = 0; i < scenes.size(); i += 3) {
            SceneCell sceneCell = new SceneCell();
            sceneCell.leftScene = scenes.get(i);
            if ((i + 1) < scenes.size()) {
                sceneCell.centerScene = scenes.get(i + 1);
            }
            if ((i + 2) < scenes.size()) {
                sceneCell.rightScene = scenes.get(i + 2);
            }
            sceneCells.add(sceneCell);
        }
    }

    @Override
    public int getCount() {
        return sceneCells.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_scene, null);
            holder.imageViewLeft = (SceneView) view.findViewById(R.id.imageViewLeft);
            holder.imageViewCenter = (SceneView) view.findViewById(R.id.imageViewCenter);
            holder.imageViewRight = (SceneView) view.findViewById(R.id.imageViewRight);

            holder.imageViewLeft.setOnClickListener(this);
            holder.imageViewCenter.setOnClickListener(this);
            holder.imageViewRight.setOnClickListener(this);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Trace.i("KnowledgeFilterAdapter", "-------------");
        /**
         *  左边数据
         */
        SceneCell item =  sceneCells.get(position);
        String sceneImgeUrl = ImageHelper.generateSceneImgeUrl(item.leftScene.getImgUrl());
        holder.imageViewLeft.setBackgroundPath(sceneImgeUrl);
        holder.imageViewLeft.setText(item.leftScene.getTitle());

        holder.imageViewCenter.setVisibility(View.INVISIBLE);
        holder.imageViewRight.setVisibility(View.INVISIBLE);
        holder.imageViewLeft.setTag(R.string.data, item.leftScene);

        /**
         * 中间数据
         */
        if (item.centerScene != null) {
            holder.imageViewCenter.setVisibility(View.VISIBLE);
            holder.imageViewRight.setVisibility(View.INVISIBLE);
            String centerImgUrl = ImageHelper.generateSceneImgeUrl(item.centerScene.getImgUrl());
            holder.imageViewCenter.setBackgroundPath(centerImgUrl);
            holder.imageViewCenter.setText(item.centerScene.getTitle());
            holder.imageViewCenter.setTag(R.string.data, item.centerScene);
        }
        /**
         * 右边数据
         */
        if (item.rightScene != null) {
            holder.imageViewRight.setVisibility(View.VISIBLE);
            String rightImgUrl = ImageHelper.generateSceneImgeUrl(item.rightScene.getImgUrl());
            holder.imageViewRight.setBackgroundPath(rightImgUrl);
            holder.imageViewRight.setText(item.rightScene.getTitle());
            holder.imageViewRight.setTag(R.string.data, item.rightScene);
        }
        return view;
    }

    class ViewHolder {
        SceneView imageViewLeft;
        SceneView imageViewCenter;
        SceneView imageViewRight;
    }



    class SceneCell{
        public SceneModel leftScene= null;

        public SceneModel centerScene = null;

        public SceneModel rightScene = null;
    }

    @Override
    public void onClick(View v) {
        SceneModel scene = (SceneModel) v.getTag(R.string.data);
        switch (v.getId()) {
            case R.id.imageViewLeft:
                callback.onClick(scene);
                break;
            case R.id.imageViewCenter:
                callback.onClick(scene);
                break;
            case R.id.imageViewRight:
                callback.onClick(scene);
                break;
        }

    }

    public interface Callback {
        void onClick(SceneModel scene);
    }


}
