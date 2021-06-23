package com.example.testapplication;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

public class Host extends AppCompatImageView implements Device {
    private int x, y; // 画像の中心座標
    private int host_ID; // ホストの番号
    private boolean isSet = false; // 設定済かどうか

    public Host(Context context, int x, int y, int host_ID) {
        super(context);
        this.x = x;
        this.y = y;
        this.host_ID = host_ID;
        this.setImageResource(R.drawable.host);
    }

    public void setID(int host_ID) {
        this.host_ID = host_ID;
    }

    public int getCenterX() {
        return x;
    }

    public int getCenterY() {
        return y;
    }

    public boolean getIsSet() {
        return isSet;
    }

    public int getID() {
        return host_ID;
    }

}
