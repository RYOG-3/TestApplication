package com.example.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * カスタムビューを作ってみた
 * これでImageViewとルータのクラスをまとめることができたはず
 */

public class Router extends AppCompatImageView implements Device {
    private float x, y; // 画像の中心座標
    private int router_ID; // ルータの番号
    private boolean isSet = false; // 設定済かどうか
    private TextView hostname;

    public Router(Context context, float x, float y, int router_ID) {
        super(context);
        this.x = x;
        this.y = y;
        this.router_ID = router_ID;
        hostname = new TextView(context);
        hostname.setText("Router" + router_ID);
        hostname.setTextColor(Color.GREEN);
        this.setImageResource(R.drawable.router);
        this.setId(1000+router_ID);
    }

    public void setID(int router_ID) {
        this.router_ID = router_ID;
    }

    public void setHostname(TextView hostname) {
        this.hostname = hostname;
    }

    public float getCenterX() {
        return x;
    }

    public float getCenterY() {
        return y;
    }

    public boolean getIsSet() {
        return isSet;
    }

    public int getID() {
        return router_ID;
    }

    public TextView getHostname() {
        return hostname;
    }

}
