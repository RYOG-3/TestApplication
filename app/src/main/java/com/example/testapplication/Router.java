package com.example.testapplication;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * カスタムビューを作ってみた
 * これでImageViewとルータのクラスをまとめることができたはず
 */

public class Router extends AppCompatImageView implements Device {
    private int x, y; // 画像の中心座標
    private int router_ID; // ルータの番号
    private boolean isSet = false; // 設定済かどうか

    public Router(Context context, int x, int y, int router_ID) {
        super(context);
        this.x = x;
        this.y = y;
        this.router_ID = router_ID;
        this.setImageResource(R.drawable.router);
    }

    public void setID(int router_ID) {
        this.router_ID = router_ID;
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
        return router_ID;
    }

}
