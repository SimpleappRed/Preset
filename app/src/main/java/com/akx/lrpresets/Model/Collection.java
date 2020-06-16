package com.akx.lrpresets.Model;

public class Collection {
    String id,name,imgUrl,desc,size;

    public Collection(String id, String name, String imgUrl, String desc, String size) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.desc = desc;
        this.size = size;
    }

    public Collection(){}

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
