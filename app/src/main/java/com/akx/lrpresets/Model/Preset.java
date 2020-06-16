package com.akx.lrpresets.Model;

public class Preset {

    String id,name,imgAfter,imgBefore,dng,desc;
    public boolean isLocked;

    public Preset(String id, String name, String desc,  String imgAfter, String imgBefore, String dng , boolean isLocked) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.imgAfter = imgAfter;
        this.imgBefore = imgBefore;
        this.dng = dng;
        this.isLocked = isLocked;
    }

    public Preset(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgAfter() {
        return imgAfter;
    }

    public void setImgAfter(String imgAfter) {
        this.imgAfter = imgAfter;
    }

    public String getImgBefore() {
        return imgBefore;
    }

    public void setImgBefore(String imgBefore) {
        this.imgBefore = imgBefore;
    }

    public String getDng() {
        return dng;
    }

    public void setDng(String dng) {
        this.dng = dng;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
