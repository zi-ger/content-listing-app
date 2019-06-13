package com.ziger.content_listing_app;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Stone implements Parcelable {

    private String name;
    private String color;
    private String url;
    private Bitmap image;

    public Stone() {}

    public Stone(String name, String color, Bitmap image) {
        this.name = name;
        this.color = color;
        this.image = image;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.color);
        dest.writeString(this.url);
        dest.writeValue(this.image);

    }

    public void readFromParcel(Parcel p) {
        //mesma ordem do WriteToParcel

        this.name = p.readString();
        this.color = p.readString();
        this.url = p.readString();
        this.image = (Bitmap) p.readValue(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<Stone> CREATOR = new Parcelable.Creator<Stone>() {

        @Override
        public Stone createFromParcel(Parcel source) {
            Stone f = new Stone();
            f.readFromParcel(source);
            return f;
        }

        @Override
        public Stone[] newArray(int size) {
            return new Stone[0];
        }
    };
}
