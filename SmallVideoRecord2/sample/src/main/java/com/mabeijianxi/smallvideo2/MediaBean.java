package com.mabeijianxi.smallvideo2;

import android.graphics.Bitmap;

public class MediaBean {
    String path;

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsolutePath() {
        return AbsolutePath == null ? "" : AbsolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        AbsolutePath = absolutePath;
    }

    String name;
    String AbsolutePath;

    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath == null ? "" : thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getDuration() {

        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSize() {

        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Bitmap getObj() {

        return obj;
    }

    public void setObj(Bitmap obj) {
        this.obj = obj;
    }

    String thumbPath;
    String displayName;
    long duration;
    String size;
    Bitmap obj;

    public MediaBean(String path, String thumbPath, int duration, String size, String displayName, Bitmap obj) {
        this.path = path;
        this.thumbPath = thumbPath;
        this.displayName = displayName;
        this.duration = duration;
        this.size = size;
        this.obj = obj;
    }

    public MediaBean() {
    }
}
