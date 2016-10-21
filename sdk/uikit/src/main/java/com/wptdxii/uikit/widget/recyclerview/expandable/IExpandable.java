package com.wptdxii.uikit.widget.recyclerview.expandable;

import java.util.List;

/**
 * must implement this interface if the item is expandable
 * Created by wptdxii on 2016/10/14 0014.
 */

public interface IExpandable<T> {
    boolean isExpandable();

    void setExpandable(boolean expandable);

    List<T> getData();

    /**
     * return the item level.
     * the level start form 0, if needn't care about the level, just return a negative.
     * @return
     */
    int getLevel();
}
