package com.wptdxii.uikit.widget.recyclerview.section;

/**
 * Created by wptdxii on 2016/10/26 0026.
 */

public class SectionEntity<T> {
    public boolean isHeader;
    public String header;
    public T t;

    public SectionEntity(T t) {
        this.isHeader = false;
        this.header = null;
        this.t = t;
    }

    public SectionEntity(boolean isHeader, String header) {
        this.isHeader = isHeader;
        this.header = header;
        this.t = null;
    }
}
