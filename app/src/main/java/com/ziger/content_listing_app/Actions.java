package com.ziger.content_listing_app;

public interface Actions {

    void undo();

    void toast(String str);

    void editStone(int pos);

    void editCategory(int pos);
}