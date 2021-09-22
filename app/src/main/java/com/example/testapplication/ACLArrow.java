package com.example.testapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * ACL図でインバウンドとアウトバウンドを設定するクラス
 * それぞれを矢印で表した画像で表示する
 */
public class ACLArrow extends View {
    // 履歴
    private List<DrawArrow> arrows = new ArrayList<>();
    private boolean judge = false; // タッチイベントの判定
    private float start_X, start_Y; // スワイプした時の始点
    private float end_X, end_Y; // スワイプした時の終点
    float target[]; // ターゲットの座標
    private Bitmap blueArrow = BitmapFactory.decodeResource(getResources(), R.drawable.blue_arrow); // インバウンドを表す矢印
    private Bitmap pinkArrow = BitmapFactory.decodeResource(getResources(), R.drawable.pink_arrow); // アウトバウンドを表す矢印


    public ACLArrow(Context context) {
        super(context);
        blueArrow = Bitmap.createScaledBitmap(blueArrow, 50, 50, true); // 画像のサイズを変更
        pinkArrow = Bitmap.createScaledBitmap(pinkArrow, 50, 50, true); // 画像のサイズを変更
    }

    public ACLArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
        blueArrow = Bitmap.createScaledBitmap(blueArrow, 50, 50, true); // 画像のサイズを変更
        pinkArrow = Bitmap.createScaledBitmap(pinkArrow, 50, 50, true); // 画像のサイズを変更
    }

    public ACLArrow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        pinkArrow = Bitmap.createScaledBitmap(pinkArrow, 50, 50, true); // 画像のサイズを変更
    }

    // 矢印の履歴(座標)
    protected class DrawArrow {
        private Bitmap arrow;
        private float x, y;

        DrawArrow(Bitmap arrow, float x, float y) {
            this.arrow = arrow;
            this.x = x;
            this.y = y;
        }

        void draw(Canvas canvas) {
            canvas.drawBitmap(arrow, x, y, null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw((canvas));

        if (MainActivity.mode == Mode.ACL) {
            // キャンバスをクリア
            canvas.drawColor(Color.TRANSPARENT);
            // 履歴から矢印を描画
            for (DrawArrow arrow : this.arrows) {
                arrow.draw(canvas);
            }
        } else {
            canvas.drawColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (MainActivity.mode == Mode.ACL) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && arrows.size() == 2) { // 円をタッチしたらアクティビティを開始
                MainActivity.getInstance().startACLActivity();
                judge = true;
            } else if (!judge) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        start_X = x;
                        start_Y = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        end_X = x;
                        end_Y = y;
                        showArrow(start_X, start_Y, end_X, end_Y);
                        break;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    public void showArrow(float start_X, float start_Y, float end_X, float end_Y) {
        double distanceFromStart, distanceFromStart2; // 始点との距離
        double distanceFromEnd, distanceFromEnd2; // 終点との距離

        for (Cable cable : MainActivity.cables) {
            distanceFromStart = Math.sqrt((start_X-cable.getStartPoint()[0]) * (start_X-cable.getStartPoint()[0]) + (start_Y-cable.getStartPoint()[1]) * (start_Y-cable.getStartPoint()[1]));
            distanceFromEnd = Math.sqrt((end_X-cable.getStartPoint()[0]) * (end_X-cable.getStartPoint()[0]) + (end_Y-cable.getStartPoint()[1]) * (end_Y-cable.getStartPoint()[1]));
            distanceFromStart2 = Math.sqrt((start_X-cable.getEndPoint()[0]) * (start_X-cable.getEndPoint()[0]) + (start_Y-cable.getEndPoint()[1]) * (start_Y-cable.getEndPoint()[1]));
            distanceFromEnd2 = Math.sqrt((end_X-cable.getEndPoint()[0]) * (end_X-cable.getEndPoint()[0]) + (end_Y-cable.getEndPoint()[1]) * (end_Y-cable.getEndPoint()[1]));
            if (distanceFromStart+distanceFromEnd < distanceFromStart2+distanceFromEnd2) {
                target = cable.getStartPoint();
                if (distanceFromStart > distanceFromEnd) {
                    // 履歴に追加
                    this.arrows.add(new DrawArrow(blueArrow, target[0]-150, target[1]-100));
                } else {
                    // 履歴に追加
                    this.arrows.add(new DrawArrow(pinkArrow, target[0], target[1]));
                }
            } else {
                target = cable.getEndPoint();
                if (distanceFromStart2 > distanceFromEnd2) {
                    // 履歴に追加
                    this.arrows.add(new DrawArrow(blueArrow, target[0]-150, target[1]-100));
                } else {
                    // 履歴に追加
                    this.arrows.add(new DrawArrow(pinkArrow, target[0]-150, target[1]-150));
                }
            }
        }


        invalidate();
    }

}
