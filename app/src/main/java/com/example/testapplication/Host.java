package com.example.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

public class Host extends AppCompatImageView implements Device {
    private int x, y; // 画像の中心座標
    private int host_ID; // ホストの番号
    private boolean isSet = false; // 設定済かどうか
    private TextView hostname;

    public Host(Context context, int x, int y, int host_ID) {
        super(context);
        this.x = x;
        this.y = y;
        this.host_ID = host_ID;
        hostname = new TextView(context);
        // hostname.setText("Host" + host_ID);
        if (host_ID == 0) {
            hostname.setText("VLAN10"); // 仮のやつすぐに消す！
        } else {
            hostname.setText("VLAN20"); // 仮のやつすぐに消す！
        }

        hostname.setTextColor(Color.GREEN);
        this.setImageResource(R.drawable.host);
        this.setId(3000+host_ID);
    }

    public void setID(int host_ID) {
        this.host_ID = host_ID;
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
        return host_ID;
    }

    public TextView getHostname() {
        return hostname;
    }

}
