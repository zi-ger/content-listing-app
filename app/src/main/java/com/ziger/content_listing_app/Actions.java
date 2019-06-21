package com.ziger.content_listing_app;

public interface Actions {

    public void undo();

    public void toast(Stone stone);

    public void editStone(int pos);

    public void editCategory(int pos);
}