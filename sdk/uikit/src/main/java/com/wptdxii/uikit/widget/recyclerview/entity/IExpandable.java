package com.wptdxii.uikit.widget.recyclerview.entity;

import java.util.List;

/**
 * implement the interface if the item is expandable
 * Created by luoxw on 2016/8/8.
 */
public interface IExpandable<T> {
    boolean isExpanded();
    void setExpanded(boolean expanded);
    List<T> getSubItems();
}
