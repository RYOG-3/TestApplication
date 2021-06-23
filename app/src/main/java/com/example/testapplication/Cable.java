package com.example.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * ケーブルを描画し、その情報を保持するクラス
 */

public class Cable extends View {
    private int x1, y1; // 始点のx座標とy座標
    private int x2, y2; // 終点のx座標とy座標
    private int cable_ID; // ケーブルの番号

    public Cable(Context context, int x1, int y1, int x2, int y2, int cable_ID) {
        super(context);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.cable_ID = cable_ID;
    }

    // 描画機能を記述
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();

        // 黒の中太の線
        paint.setStrokeWidth(10);
        float[] pts = {x1, y1, x2, y2};
        canvas.drawLines(pts, paint);
    }

    protected int getCable_ID() {
        return cable_ID;
    }

    /**
     * ケーブルを削除するか判断するメソッド
     * @param start_X,start_Y,end_X,end_Y なぞった線分の始点と終点のX,Y座標
     */
    protected boolean disconnected(int start_X, int start_Y, int end_X, int end_Y) {
        boolean judge = false;

        int ta = (start_X - end_X) * (y1 - start_Y) + (start_Y - end_Y) * (start_X - x1);
        int tb = (start_X - end_X) * (y2 - start_Y) + (start_Y - end_Y) * (start_X - x2);
        int tc = (x1 - x2) * (start_Y - y1) + (y1 - y2) * (x1 - start_X);
        int td = (x1 - x2) * (end_Y - y1) + (y1 - y2) * (x1 - end_X);

        /**
        int ta = (start_X - end_X) * (start_Y - y1) + (end_Y - start_Y) * (start_X - x1);
        int tb = (start_X - end_X) * (start_Y - y2) + (end_Y - start_Y) * (start_X - x2);
        int tc = (x1 - x2) * (y1 - start_Y) + (y2 - y1) * (x1 - start_X);
        int td = (x1 - x2) * (y1 - end_Y) + (y2 - y1) * (x1 - end_X);
         */

        System.out.printf("%d, %d, %d, %d, %d, %d, %d, %d\n", start_X, start_Y, end_X, end_Y, x1, y1, x2, y2);
        System.out.println(ta*tb);
        System.out.println(tc*td);

        if (ta*tb < 0 && tc*td < 0) {
            judge = true;
        }

        return judge;

    }


}
