package com.huhapp.android.menu;

/**
 * Created by igbopie on 7/10/15.
 */
public class MenuItem {
    private String text;
    private Integer tag;
    private boolean isTitle;

    public MenuItem(String text, Integer tag, boolean isTitle) {
        this.text = text;
        this.tag = tag;
        this.isTitle = isTitle;
    }

    public MenuItem(String text, boolean isTitle) {
        this.text = text;
        this.isTitle = isTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean isTitle) {
        this.isTitle = isTitle;
    }
}
