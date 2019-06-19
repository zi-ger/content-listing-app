package com.ziger.content_listing_app;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

    private int id;
    private String name;

    public Category() {}

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String name) {
        this.name = name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    public void readFromParcel(Parcel p) {
        //mesma ordem do WriteToParcel

        this.id = p.readInt();
        this.name = p.readString();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

        @Override
        public Category createFromParcel(Parcel source) {
            Category c = new Category();
            c.readFromParcel(source);
            return c;
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[0];
        }
    };
}
