package com.ziger.content_listing_app;

import android.os.Parcel;
import android.os.Parcelable;

public class Stone implements Parcelable {

    private int id;
    private String name;
    private String color;
    private int category;
    private String url;
    private byte[] image;

    public Stone() {}

    public Stone(int id, String name, String color, int category, String url, byte[] image) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.category = category;
        this.url = url;
        this.image = image;
    }

    public Stone(String name, String color, int category, String url, byte[] image) {
        this.name = name;
        this.color = color;
        this.category = category;
        this.url = url;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.color);
        dest.writeInt(this.category);
        dest.writeString(this.url);
        dest.writeByteArray(this.image);
    }

    public void readFromParcel(Parcel p) {
        //mesma ordem do WriteToParcel

        this.id = p.readInt();
        this.name = p.readString();
        this.color = p.readString();
        this.category = p.readInt();
        this.url = p.readString();
        this.image = p.createByteArray();
    }

    public static final Parcelable.Creator<Stone> CREATOR = new Parcelable.Creator<Stone>() {

        @Override
        public Stone createFromParcel(Parcel source) {
            Stone s = new Stone();
            s.readFromParcel(source);
            return s;
        }

        @Override
        public Stone[] newArray(int size) {
            return new Stone[0];
        }
    };
}
