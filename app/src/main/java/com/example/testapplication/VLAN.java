package com.example.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * VLANの範囲を可視化するクラス
 */
public class VLAN extends View {
    private float x, y; // 中心座標
    private int left, top, right, bottom; // 四角形の左上と右下
    private int vlan_ID; // VLAN番号

    public VLAN(Context context, float x, float y, int vlan_ID) {
        super(context);
        this.x = x;
        this.y = y;

        this.left = (int)x - 150;
        this.top = (int)y - 150;
        this.right = (int)x + 150;
        this.bottom = (int)y + 150;

        this.vlan_ID = vlan_ID;
    }

    // 描画機能を記述
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        // すぐ消す！
        if (vlan_ID == 10) {
            paint.setColor(Color.BLUE);
        } else {
            paint.setColor(Color.RED);
        }

        Rect rect = new Rect(left, top, right, bottom);

        canvas.drawRect(rect, paint);
    }

    protected int getVlan_ID() {
        return vlan_ID;
    }


}
