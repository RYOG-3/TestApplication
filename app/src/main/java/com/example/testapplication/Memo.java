package com.example.testapplication;

import android.content.Context;
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
 * メモモードのクラス
 * メモをするために使いモードを切り替えると消える
 * 削除機能も搭載
 */
public class Memo extends View {
    // 履歴
    private List<MemoDrawLine> lines;
    // 現在描いている線の情報
    private Paint paint;
    private Path path;

    // 線の履歴(座標＋色)
    protected class MemoDrawLine {
        private Paint paint;
        private Path path;

        MemoDrawLine(Path path, Paint paint) {
            this.paint = new Paint(paint);
            this.path = new Path(path);
        }

        void draw(Canvas canvas) {
            canvas.drawPath(this.path, this.paint);
        }
    }

    public Memo(Context context) {
        super(context);
        path = new Path();
        paint = new Paint();
    }

    public Memo(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        lines = new ArrayList<MemoDrawLine>();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
    }

    public Memo(Context context, AttributeSet attrs, int defStyle) {
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

        if (MainActivity.mode == Mode.Memo) {
            // キャンバスをクリア
            canvas.drawColor(Color.TRANSPARENT);
            // 履歴から線を描画
            for (MemoDrawLine line : this.lines) {
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

        if (MainActivity.mode == Mode.Memo) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.path.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.path.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    this.path.lineTo(x, y);
                    // 指を離したので、履歴に追加する
                    this.lines.add(new MemoDrawLine(this.path, this.paint));
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

    public void memoInvalidate() {
        invalidate();
    }
}
