package com.example.testapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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

    private boolean judge = false; // タッチイベントの判定

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
            if (event.getAction() == MotionEvent.ACTION_DOWN && (judge = onTouch(x, y))) { // 円をタッチしたらアクティビティを開始
                MainActivity.getInstance().startRoutingActivity();
            } else if (!judge) {
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
                        // 指を離したので、履歴に追加して、判定を行う
                        this.lines.add(new RoutingDrawLine(this.path, this.paint));
                        routerInCircle(this.path);
                        // パスをリセットする
                        // これを忘れると、全ての線の色が変わってしまう
                        this.path.reset();
                        judge = false;

                        break;
                }
                invalidate();
            }

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

    // モードを切り替えた時に再描画するメソッド
    public void routingInvalidate() {
        invalidate();
    }

    // 円の中にあるルータを判定するメソッド
    public void routerInCircle(Path path) {
        PathMeasure pm = new PathMeasure(path, true); // Pathを測定するクラスを用意
        float split = 0f; // 指定した座標を取るための数値
        float aCoordinate[] = {0f, 0f}; // 必要な座標を取得するための配列を用意
        boolean judge[] = {false, false, false, false}; // ルータの右上右下左下左上に円の座標が存在するかを判断する
        int routingRouterNum = 0; // 対象ルータの個数

        System.out.println(pm.getLength());
        for (Router router : MainActivity.routers) {
            if (router == null) {
                continue;
            }
            split = 0.0f;
            while (split < 1.0f) {
                pm.getPosTan(pm.getLength() * split, aCoordinate, null);
                if (router.getCenterX() < aCoordinate[0] && router.getCenterY() > aCoordinate[1]) {
                    judge[0] = true;
                } else if (router.getCenterX() < aCoordinate[0] && router.getCenterY() < aCoordinate[1]) {
                    judge[1] = true;
                } else if (router.getCenterX() > aCoordinate[0] && router.getCenterY() < aCoordinate[1]) {
                    judge[2] = true;
                } else if (router.getCenterX() > aCoordinate[0] && router.getCenterY() > aCoordinate[1]) {
                    judge[3] = true;
                }
                split += 0.001f;
            }
            System.out.println(judge[0]);
            System.out.println(judge[1]);
            System.out.println(judge[2]);
            System.out.println(judge[3]);
            if (judge[0] && judge[1] && judge[2] && judge[3]) {
                routingRouterNum++;
            }
            for (int j=0; j<=3; j++) {
                judge[j] = false;
            }
            System.out.println(aCoordinate[0]);
            System.out.println(aCoordinate[1]);
        }
        Toast myToast = Toast.makeText(
                MainActivity.getInstance().getApplicationContext(),
                "対象のルーターは" + routingRouterNum + "個です",
                Toast.LENGTH_SHORT
        );
        myToast.show();
        System.out.printf("囲われたルーターの数は%d個です", routingRouterNum);
        System.out.println();
    }

    /**
     * 円がタッチされたかを判断するメソッド
     * @param x,y x座標とy座標
     */
    protected boolean onTouch(float x, float y) {
        boolean judge = false;
        float aCoordinate[] = {0f, 0f}; // 必要な座標を取得するための配列を用意

        for (RoutingDrawLine line : lines) {
            float split = 0.0f;
            while (split < 1.0f) {
                PathMeasure pm = new PathMeasure(line.path, true); // Pathを測定するクラスを用意
                pm.getPosTan(pm.getLength() * split, aCoordinate, null);

                System.out.println(aCoordinate[0]-x);
                System.out.println(aCoordinate[1]-y);
                if (Math.abs(aCoordinate[0]-x) < 10.0f && Math.abs(aCoordinate[1]-y) < 10.0f) {
                    judge = true;
                    System.out.println("円がタッチされました");
                    break;
                } else {
                    System.out.println("not Touch");
                }

                split += 0.001f;
            }
        }

        return judge;
    }
}
