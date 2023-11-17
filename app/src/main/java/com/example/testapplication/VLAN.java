package com.example.testapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * VLANの情報を定義するクラス
 *
 * 定義する内容
 * VLAN名とVLAN番号
 * どのインターフェースに登録するのか
 * アクセスかトランクか
 * 画面上で表示させるVLANの範囲を表す色画像の情報（色や座標）
 *
 */
public class VLAN extends View {
    private float x, y; // 中心座標
    private int left, top, right, bottom; // 四角形の左上と右下
    private int vlanID; // VLAN番号
    private String vlanName; // VLAN名
    private InterfaceType interfaceType; // interface の種類
    private boolean switchPortMode = false; // switch のポートの状態（false はトランク）

    public VLAN(Context context, float x, float y, int vlanID, String vlanName, InterfaceType interfaceType, boolean switchPortMode) {
        super(context);
        this.x = x;
        this.y = y;

        this.left = (int)x - 150;
        this.top = (int)y - 150;
        this.right = (int)x + 150;
        this.bottom = (int)y + 150;

        this.vlanID = vlanID;
        this.vlanName = vlanName;
        this.interfaceType = interfaceType;
        this.switchPortMode = switchPortMode;
    }

    // 描画機能を記述
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        // すぐ消す！
        if (vlanID == 10) {
            paint.setColor(Color.BLUE);
        } else {
            paint.setColor(Color.RED);
        }

        Rect rect = new Rect(left, top, right, bottom);

        canvas.drawRect(rect, paint);
    }

    protected int getVlanID() {
        return vlanID;
    }

    protected String getVlanName() {
        return vlanName;
    }

    protected boolean getSwitchPortMode() {
        return switchPortMode;
    }


    protected String getInterfaceType() {
        return interfaceType.toString();
    }


}
