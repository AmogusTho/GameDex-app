package com.example.gamedexter;

import android.widget.ImageView;

public class MapMarker {
    public float MapX;
    public float MapY;
    public ImageView view;

    public String title;
    public String type;
    public String description;

    public MapMarker(float x, float y, ImageView view, String title, String type, String description) {
        this.MapX = x;
        this.MapY = y;
        this.view = view;
        this.title = title;
        this.type = type;
        this.description = description;
    }
}
