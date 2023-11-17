package com.example.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * ケーブルを描画し、その情報を保持するクラス(カスタムビュー)
 */

public class Cable extends View {
    private float x1, y1; // 始点のx座標とy座標 (始点と終点は X1 <= x2 と定義する)
    private float x2, y2; // 終点のx座標とy座標
    private int cable_ID; // ケーブルの番号

    private Device device1; // ケーブルに繋がったネットワーク機器（タブレットから見て左側）
    private Device device2; // （タブレットから見て右側）

    public Cable(Context context, float x1, float y1, float x2, float y2, int cable_ID, Device device1, Device device2) {
        super(context);
        if (x1 <= x2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.device1 = device1;
            this.device2 = device2;
        } else {
            this.x1 = x2;
            this.y1 = y2;
            this.x2 = x1;
            this.y2 = y1;
            this.device1 = device2;
            this.device2 = device1;
        }

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
    protected boolean disconnected(float start_X, float start_Y, float end_X, float end_Y) {
        boolean judge = false;

        double ta = (start_X - end_X) * (y1 - start_Y) + (start_Y - end_Y) * (start_X - x1); // 何も考えずにintにしてたからオーバーフローになっていたけど、doubleにしたらいけた！ 型って大事！
        double tb = (start_X - end_X) * (y2 - start_Y) + (start_Y - end_Y) * (start_X - x2);
        double tc = (x1 - x2) * (start_Y - y1) + (y1 - y2) * (x1 - start_X);
        double td = (x1 - x2) * (end_Y - y1) + (y1 - y2) * (x1 - end_X);


        System.out.printf("%f, %f, %f, %f, %f, %f, %f, %f\n", start_X, start_Y, end_X, end_Y, x1, y1, x2, y2);
        System.out.println(ta*tb);
        System.out.println(tc*td);

        if (ta*tb < 0 && tc*td < 0) {
            judge = true;
        }

        return judge;

    }

    /**
     * ケーブルがタッチされたかを判断するメソッド
     * @param x,y x座標とy座標
     */
    protected boolean onTouch(float x, float y) {
        boolean judge = false;

        if (x2 + 10 >= x && x >= x1 - 10) { // x1とx2の間にx座標がある
            if (Math.max(y1, y2) + 10 >= y && y >= Math.min(y1, y2) - 10) { // y1とy2の間にy座標がある
                if (-10000 <= ((y2 - y1) * (x - x1) - (x2 - x1) * (y - y1)) &&
                        ((y1 - y2) * (x - x1) - (y - y1) * (x1 - x2)) <= 10000) {// ケーブル上に点がある(ざっくり) 下限と上限の数値は大きければ大きいほど判定が曖昧になる(点と直線の距離の公式を参照)
                    judge = true;
                }
            }
        }
        System.out.println((y1 - y2) * (x - x1) - (y - y1) * (x1 - x2));
        System.out.println("テスト");

        return judge;
    }

    /**
     * ケーブルの始点の座標を返すメソッド
     */
    public float[] getStartPoint() {
        float[] start = {x1, y1};
        return start;
    }

    /**
     * ケーブルの終点の座標を返すメソッド
     */
    public float[] getEndPoint() {
        float[] end = {x2, y2};
        return end;
    }

    /**
     * device1のゲッター
     */
    public Device getDevice1() {
        return device1;
    }

    /**
     * device2のゲッター
     */
    public Device getDevice2() {
        return device2;
    }

    /**
     * device のセッター
     * @param device
     */
    public void setDevice(Device device) {
        if (device.getCenterX() == this.device1.getCenterX()) {
            this.device1 = device;
        } else {
            this.device2 = device;
        }
    }

}
