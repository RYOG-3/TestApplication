package com.example.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * ルーティング図で使用するルータに対して円を囲むための描画と対象のルータを判定するためのクラス
 * View を継承するとき全てのパターンのコンストラクタを追加しておくとエラーが発生しない
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

    private int lineID = -1;

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
            if (event.getAction() == MotionEvent.ACTION_DOWN && (lineID = onTouch(x, y)) != -1) {
                MainActivity.getInstance().startRoutingActivity(lineID);
            } else if (lineID == -1) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        start_X = x;
                        start_Y = y;
                        this.path.moveTo(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if ((x > start_X + 10 || start_X - 10 > x) && (y > start_Y + 10 || start_Y - 10 > y)) {
                            this.path.lineTo(x, y);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if ((x > start_X + 10 || start_X - 10 > x) && (y > start_Y + 10 || start_Y - 10 > y)) {
                            this.path.lineTo(start_X, start_Y);
                            // 指を離したので、履歴に追加して、判定を行う
                            this.lines.add(new RoutingDrawLine(lines.size(), this.path, this.paint));
                            routerInCircle(this.path);
                            // パスをリセットする
                            // これを忘れると、全ての線の色が変わってしまう
                        }
                        this.path.reset();
                        break;
                }
            }
            invalidate();

            return true;
        } else {
            return false;
        }
    }

    /**
     * 削除ボタンが押されたときに円を削除するメソッド
     * 履歴の lines から対象の円を削除する
     */
    public void removeLine(int targetID) {
        for (RoutingDrawLine line : this.lines) {
            if (line.getLineID() == targetID) {
                lines.remove(line);
                System.out.println("円が削除されました");
                break;
            }
        }
        System.out.println("円は削除されませんでした");
        invalidate();
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
                } else if (router.getCenterX() < aCoordinate[0] + 100 && router.getCenterY() < aCoordinate[1] + 100) { // 100は下駄を履かせてる
                    judge[1] = true;
                } else if (router.getCenterX() > aCoordinate[0] && router.getCenterY() < aCoordinate[1] + 100) {
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
            for (int j = 0; j <= 3; j++) {
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
     *
     * @param x,y x座標とy座標
     */
    protected int onTouch(float x, float y) {
        int judge = -1;
        float aCoordinate[] = {0f, 0f}; // 必要な座標を取得するための配列を用意

        for (RoutingDrawLine line : lines) {
            float split = 0.0f;
            while (split < 1.0f) {
                PathMeasure pm = new PathMeasure(line.path, true); // Pathを測定するクラスを用意
                pm.getPosTan(pm.getLength() * split, aCoordinate, null);

                System.out.println(aCoordinate[0] - x);
                System.out.println(aCoordinate[1] - y);
                if (Math.abs(aCoordinate[0] - x) < 10.0f && Math.abs(aCoordinate[1] - y) < 10.0f) {
                    judge = line.getLineID();
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

    // 線の履歴(座標＋色)
    protected class RoutingDrawLine {
        private int lineID;
        private Paint paint;
        private Path path;

        RoutingDrawLine(int lineID, Path path, Paint paint) {
            this.lineID = lineID;
            this.paint = new Paint(paint);
            this.path = new Path(path);
        }

        int getLineID() {
            return lineID;
        }

        void draw(Canvas canvas) {
            canvas.drawPath(this.path, this.paint);
        }
    }
}
