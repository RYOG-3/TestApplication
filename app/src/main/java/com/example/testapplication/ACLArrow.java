package com.example.testapplication;

import android.content.Context;
import android.graphics.Matrix;
import androidx.appcompat.widget.AppCompatImageView;
import java.util.List;

/**
 * ACL図でインバウンドとアウトバウンドを設定するクラス
 * それぞれを矢印で表した画像で表示する
 */
public class ACLArrow extends AppCompatImageView {
    private float x, y; // 画像の中心座標
    private int arrow_ID; // 矢印の番号
    private Cable cable; // 対象のケーブル
    private boolean isJudge; // インバウンド[青色]かアウトバウンド[赤色]か判定(インバウンドがtrueとする)
    private boolean isSet = false; // 設定済かどうか
    private Matrix matrix; // 矢印の向きや角度の情報を保持する
    private Router targetRouter = null; // 設定対象のルータ
    private int access_group_number; // インターフェースに適用するACLの番号
    private Boolean[] permission = null; // permit か deny か
    private List<String> ipAddresses = null;
    private List<String> wildcards = null;


    public ACLArrow(Context context, float start_X, float start_Y, float end_X, float end_Y, int arrow_ID, Cable cable) {
        super(context);

        this.arrow_ID = arrow_ID;
        this.cable = cable;
        this.matrix = new Matrix();
        aclType(start_X, start_Y, end_X, end_Y);
        if (isJudge) {
            setImageResource(R.drawable.blue_arrow);
            /**
             *
            Bitmap bitmap_origin = BitmapFactory.decodeResource(getResources(), R.drawable.blue_arrow);
            Bitmap bitmap_rotate = Bitmap.createBitmap(bitmap_origin, (int)x, (int)y, bitmap_origin.getWidth(), bitmap_origin.getHeight(), matrix, true);
            this.setImageBitmap(bitmap_rotate);
             */
        } else {
            setImageResource(R.drawable.pink_arrow);
            /**
             *
            Bitmap bitmap_origin = BitmapFactory.decodeResource(getResources(), R.drawable.pink_arrow);
            Bitmap bitmap_rotate = Bitmap.createBitmap(bitmap_origin, (int)x, (int)y, bitmap_origin.getWidth(), bitmap_origin.getHeight(), matrix, true);
            this.setImageBitmap(bitmap_rotate);
            */
        }
        this.setId(4000+arrow_ID);
    }

    public void setID(int arrow_ID) {
        this.arrow_ID = arrow_ID;
    }



    public float getCenterX() {
        System.out.println("x = " + x);
        return x;
    }

    public float getCenterY() {
        System.out.println("y = " + y);
        return y;
    }

    public boolean getIsSet() {
        return isSet;
    }

    public int getID() {
        return arrow_ID;
    }


    float target[]; // ターゲットの座標


    /**
    public ACLArrow(Context context) {
        super(context);
        blueArrow = Bitmap.createScaledBitmap(blueArrow, 50, 50, true); // 画像のサイズを変更
        pinkArrow = Bitmap.createScaledBitmap(pinkArrow, 50, 50, true); // 画像のサイズを変更
        setOnTouchListener(listener);
    }

    public ACLArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
        blueArrow = Bitmap.createScaledBitmap(blueArrow, 50, 50, true); // 画像のサイズを変更
        pinkArrow = Bitmap.createScaledBitmap(pinkArrow, 50, 50, true); // 画像のサイズを変更
        setOnTouchListener(listener);
    }

    public ACLArrow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        blueArrow = Bitmap.createScaledBitmap(blueArrow, 50, 50, true); // 画像のサイズを変更
        pinkArrow = Bitmap.createScaledBitmap(pinkArrow, 50, 50, true); // 画像のサイズを変更
        setOnTouchListener(listener);
    }
     */


    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }


    public static boolean checkConnectedRouter(float start_X, float start_Y, float end_X, float end_Y, Cable targetCable) {
        double distanceFromStart, distanceFromStart2; // 始点との距離(それぞれのネットワーク機器に対しての)
        double distanceFromEnd, distanceFromEnd2; // 終点との距離(それぞれのネットワーク機器に対しての)
        boolean checkRouter = false;

        distanceFromStart = Math.sqrt((start_X-targetCable.getStartPoint()[0]) * (start_X-targetCable.getStartPoint()[0]) + (start_Y-targetCable.getStartPoint()[1]) * (start_Y-targetCable.getStartPoint()[1]));
        distanceFromEnd = Math.sqrt((end_X-targetCable.getStartPoint()[0]) * (end_X-targetCable.getStartPoint()[0]) + (end_Y-targetCable.getStartPoint()[1]) * (end_Y-targetCable.getStartPoint()[1]));
        distanceFromStart2 = Math.sqrt((start_X-targetCable.getEndPoint()[0]) * (start_X-targetCable.getEndPoint()[0]) + (start_Y-targetCable.getEndPoint()[1]) * (start_Y-targetCable.getEndPoint()[1]));
        distanceFromEnd2 = Math.sqrt((end_X-targetCable.getEndPoint()[0]) * (end_X-targetCable.getEndPoint()[0]) + (end_Y-targetCable.getEndPoint()[1]) * (end_Y-targetCable.getEndPoint()[1]));

        if (distanceFromStart+distanceFromEnd < distanceFromStart2+distanceFromEnd2) { // どちらのネットワーク機器に対して始点と終点の距離の合計が短いかを判定、短い方を設定対象のネットワーク機器とする
            if (targetCable.getDevice1().getClass() == Router.class) {
                checkRouter = true;
            }
        } else {
            if (targetCable.getDevice2().getClass() == Router.class) {
                checkRouter = true;
            }
        }
        return checkRouter;
    }


    // インバウンドかアウトバウンドかを判定し, 対象の機器も判定する
    public void aclType(float start_X, float start_Y, float end_X, float end_Y) {
        double distanceFromStart, distanceFromStart2; // 始点との距離(それぞれのネットワーク機器に対しての)
        double distanceFromEnd, distanceFromEnd2; // 終点との距離(それぞれのネットワーク機器に対しての)
        double radian = Math.atan2(cable.getEndPoint()[1]-cable.getStartPoint()[1], cable.getEndPoint()[0]-cable.getStartPoint()[0]); // ケーブルの角度(ラジアン)を計算
        double degrees = Math.toDegrees(radian); // ケーブルの角度(度数)
        float default_x, default_y; // 初期の矢印の座標(ケーブルが回転するに応じて変化させる)


        distanceFromStart = Math.sqrt((start_X-cable.getStartPoint()[0]) * (start_X-cable.getStartPoint()[0]) + (start_Y-cable.getStartPoint()[1]) * (start_Y-cable.getStartPoint()[1]));
        distanceFromEnd = Math.sqrt((end_X-cable.getStartPoint()[0]) * (end_X-cable.getStartPoint()[0]) + (end_Y-cable.getStartPoint()[1]) * (end_Y-cable.getStartPoint()[1]));
        distanceFromStart2 = Math.sqrt((start_X-cable.getEndPoint()[0]) * (start_X-cable.getEndPoint()[0]) + (start_Y-cable.getEndPoint()[1]) * (start_Y-cable.getEndPoint()[1]));
        distanceFromEnd2 = Math.sqrt((end_X-cable.getEndPoint()[0]) * (end_X-cable.getEndPoint()[0]) + (end_Y-cable.getEndPoint()[1]) * (end_Y-cable.getEndPoint()[1]));

        if (distanceFromStart+distanceFromEnd < distanceFromStart2+distanceFromEnd2) { // どちらのネットワーク機器に対して始点と終点の距離の合計が短いかを判定、短い方を設定対象のネットワーク機器とする
            target = cable.getStartPoint();
            if (cable.getDevice1().getClass() == Router.class) {
                targetRouter = (Router) cable.getDevice1(); // startなので左側のルータがターゲット
                if (distanceFromStart > distanceFromEnd) { // 始点より終点の方が短い場合インバウンドの判定
                    // 座標と矢印の色を決定する
                    // this.arrows.add(new DrawArrow(blueArrow, target[0]-150, target[1]-100));
                    default_x = target[0] + 150;
                    default_y = target[1] + 55;
                    // x = (float)(Math.cos(radian) * 150 + target[0] + Math.cos(radian + Math.PI * 0.5) * 12); //表示位置
                    // y = (float)(Math.sin(radian) * 150 + target[1] + Math.sin(radian + Math.PI * 0.5) * 10);
                    x = (float)((default_x-target[0])*Math.cos(radian) - (default_y-target[1])*Math.sin(radian) + target[0]); // 原点以外を中心とした回転の公式から求める
                    y = (float)((default_x-target[0])*Math.sin(radian) + (default_y-target[1])*Math.cos(radian) + target[1]);
                    isJudge = true;
                } else {
                    // 座標と矢印の色を決定する
                    // this.arrows.add(new DrawArrow(pinkArrow, target[0], target[1]));
                    default_x = target[0] + 150;
                    default_y = target[1] - 90;
                    // x = (float)(Math.cos(radian) * 150 + target[0] - Math.cos(radian + Math.PI * 0.5) * 15);
                    // y = (float)(Math.sin(radian) * 150 - 70 + target[1] + Math.sin(radian + Math.PI * 0.5) * 10);
                    x = (float)((default_x-target[0])*Math.cos(radian) - (default_y-target[1])*Math.sin(radian) + target[0]);
                    y = (float)((default_x-target[0])*Math.sin(radian) + (default_y-target[1])*Math.cos(radian) + target[1]);
                    isJudge = false;
                }
                matrix.setRotate(180 + (float)degrees, x, y);
                System.out.println("角度 = " + degrees);
            }
        } else {
            target = cable.getEndPoint();
            if (cable.getDevice2().getClass() == Router.class) {
                targetRouter = (Router) cable.getDevice2(); // endなので右側のルータがターゲット
            }
            if (distanceFromStart2 > distanceFromEnd2) { // もう一方のネットワーク機器に対しての始点と終点の距離を判定
                // 座標と矢印の色を決定する
                // this.arrows.add(new DrawArrow(blueArrow, target[0]-150, target[1]-100));
                default_x = target[0] - 190;
                default_y = target[1] + 55;
                // x = (float)(Math.cos(radian) * (-150) - 25 + target[0] - Math.cos(radian + Math.PI * 0.5) * 10);
                // y = (float)(Math.sin(radian) * (-150) + target[1] + Math.sin(radian + Math.PI * 0.5) * 10);
                x = (float)((default_x-target[0])*Math.cos(radian) - (default_y-target[1])*Math.sin(radian) + target[0]);
                y = (float)((default_x-target[0])*Math.sin(radian) + (default_y-target[1])*Math.cos(radian) + target[1]);
                isJudge = true;
            } else {
                // 座標と矢印の色を決定する
                // this.arrows.add(new DrawArrow(pinkArrow, target[0]-150, target[1]-150));
                default_x = target[0] - 190;
                default_y = target[1] - 90;
                // x = (float)(Math.cos(radian) * (-150) - 25 + target[0] - Math.cos(radian + Math.PI * 0.5) * 10);
                // y = (float)(Math.sin(radian) * (-150) - 50 + target[1] - Math.sin(radian + Math.PI * 0.5) * 10);
                x = (float)((default_x-target[0])*Math.cos(radian) - (default_y-target[1])*Math.sin(radian) + target[0]);
                y = (float)((default_x-target[0])*Math.sin(radian) + (default_y-target[1])*Math.cos(radian) + target[1]);

                isJudge = false;
            }
            matrix.setRotate((float)degrees, x, y);
        }
    }

    public boolean getIsJudge() {
        return isJudge;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public Router getTargetRouter() {
        return targetRouter;
    }

    public void setTargetRouter(Router targetRouter) {
    this.targetRouter = targetRouter;
    }

    public void setPermission(Boolean[] permission) {
        this.permission = permission;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public void setWildcards(List<String> wildcards) {
        this.wildcards = wildcards;
    }

    public Boolean[] getPermission() {
        return this.permission;
    }

    public List<String> getIpAddresses() {
        return this.ipAddresses;
    }

    public List<String> getWildcards() {
        return this.wildcards;
    }
}
