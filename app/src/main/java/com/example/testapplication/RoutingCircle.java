package com.example.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * ルーティング図で使用するルータに対して円を囲むための描画と対象のルータを判定するためのクラス
 * 全てのパターンのコンストラクタを追加しておくとエラーが発生しない(なぜかは勉強していない...)
 */
public class RoutingCircle extends View {
    // 履歴
    private List<RoutingDrawLine> lines;
    // 現在描いている線の情報
    private Paint paint;
    private Path path;

    // Pathの最初の座標
    private float start_X = 0;
    private float start_Y = 0;

    // 線の履歴(座標＋色)
    protected class RoutingDrawLine {
        private Paint paint;
        private Path path;

        RoutingDrawLine(Path path, Paint paint) {
            this.paint = new Paint(paint);
            this.path = new Path(path);
        }

        void draw(Canvas canvas) {
            canvas.drawPath(this.path, this.paint);
        }
    }

    public RoutingCircle(Context context) {
        super(context);
        path = new Path();
        paint = new Paint();
    }

    public RoutingCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        lines = new ArrayList<RoutingDrawLine>();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
    }

    public RoutingCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        path = new Path();
        paint = new Paint();
        /**
         paint.setColor(0xFF000000);
         paint.setStyle(Paint.Style.STROKE);
         paint.setStrokeJoin(Paint.Join.ROUND);
         paint.setStrokeCap(Paint.Cap.ROUND);
         paint.setStrokeWidth(10);
         */
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw((canvas));

        if (MainActivity.mode == Mode.Routing) {
            // キャンバスをクリア
            canvas.drawColor(Color.TRANSPARENT);
            // 履歴から線を描画
            for (RoutingDrawLine line : this.lines) {
                line.draw(canvas);
            }
            // 現在、描いている線を描画
            canvas.drawPath(path, paint);
        } else {
            canvas.drawColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (MainActivity.mode == Mode.Routing) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    start_X = x;
                    start_Y = y;
                    this.path.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.path.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    this.path.lineTo(start_X, start_Y);
                    // 指を離したので、履歴に追加する
                    this.lines.add(new RoutingDrawLine(this.path, this.paint));
                    // パスをリセットする
                    // これを忘れると、全ての線の色が変わってしまう
                    this.path.reset();

                    break;
            }
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public Paint getPaint() {
        return paint;
    }

    public Path getPath() {
        return path;
    }

    public void routingInvalidate() {
        invalidate();
    }
}
