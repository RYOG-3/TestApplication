package com.example.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

public class Switch extends AppCompatImageView implements Device {
    private int x, y; // 画像の中心座標
    private int switch_ID; // ルータの番号
    private boolean isSet = false; // 設定済かどうか
    private int[] vlan; // 設定しているVLANの番号
    private TextView hostname;

    public Switch(Context context, int x, int y, int switch_ID) {
        super(context);
        this.x = x;
        this.y = y;
        this.switch_ID = switch_ID;
        hostname = new TextView(context);
        hostname.setText("Switch" + switch_ID);
        hostname.setTextColor(Color.GREEN);
        this.setImageResource(R.drawable.resource_switch);
        this.setId(2000+switch_ID);
    }

    public void setID(int switch_ID) {
        this.switch_ID = switch_ID;
    }

    public void setHostname(TextView hostname) {
        this.hostname = hostname;
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

    public TextView getHostname() {
        return hostname;
    }

}
