package com.example.testapplication;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class Switch extends AppCompatImageView implements Device {
    private int x, y; // 画像の中心座標
    private int switch_ID; // ルータの番号
    private boolean isSet = false; // 設定済かどうか

    public Switch(Context context, int x, int y, int switch_ID) {
        super(context);
        this.x = x;
        this.y = y;
        this.switch_ID = switch_ID;
        this.setImageResource(R.drawable.resource_switch);
    }

    public void setID(int switch_ID) {
        this.switch_ID = switch_ID;
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
        return switch_ID;
    }

}
