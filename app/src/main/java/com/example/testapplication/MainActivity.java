package com.example.testapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private int routerNum = 0, switchNum = 0, hostNum = 0; // それぞれのネットワーク機器台数
    private int cableNum = 0; // 結線したケーブルの数
    private float start_X, start_Y; // ケーブルの削除をする時に使用する
    protected static ArrayList<Router> routers = new ArrayList<>(); // 配置したルータたち
    protected static ArrayList<Switch> switches = new ArrayList<>(); // 配置したスイッチたち
    protected static ArrayList<Host> hosts = new ArrayList<>(); // 配置したホストたち
    protected static ArrayList<Cable> cables = new ArrayList<>(); // 結線したケーブルたち
    protected static Mode mode = Mode.Physical; // 現在の状態(初期状態は物理構成)
    private RoutingCircle routingCircle; // ルーティング図で使う描画用クラス
    private Memo memo; // メモ機能で使う描画用クラス
    private static MainActivity instance = null;
    private final int RESULT_SUBACTIVITY = 1000;

    private Context context = this;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;


        routingCircle = findViewById(R.id.routingCircle);
        memo = findViewById(R.id.memo_class);

        /**
         * 物理構成図ボタンのリスナー設定
         */
        Button physical_bt = findViewById(R.id.physical);
        physical_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = Mode.Physical;
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                System.out.println("物理構成モードに変更");
            }
        });

        /**
         * 論理構成図ボタンのリスナー設定
         */
        Button logical_bt = findViewById(R.id.logical);
        logical_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = Mode.Logical;
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                System.out.println("論理構成モードに変更");
            }
        });

        /**
         * ルーティング図ボタンのリスナー設定
         */
        Button routing_bt = findViewById(R.id.routing);
        routing_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = Mode.Routing;
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                System.out.println("ルーティングモードに変更");
            }
        });

        /**
         * ACL図ボタンのリスナー設定
         */
        Button acl_bt = findViewById(R.id.acl);
        acl_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = Mode.ACL;
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                System.out.println("ACLモードに変更");
            }
        });

        /**
         * メモボタンのリスナー設定
         */
        Button memo_bt = findViewById(R.id.memo);
        memo_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = Mode.Memo;
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                System.out.println("メモモードに変更");
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int width = 200; // ネットワーク機器の幅
        final int height = 200; // ネットワーク機器の高さ
        /**
         * x,y 位置情報取得
         * getXはMotionEventを受け取るViewの左上をX=0, Y=0とした座標に変換される
         * getRawXは座標の補正は行わず、端末のスクリーン上の座標を示す
         */
        ConstraintLayout layout = findViewById(R.id.constraintlayout);

        switch (mode) {
            case Physical:
                switch (event.getAction()) {
                    // 単純にタッチされた
                    case MotionEvent.ACTION_DOWN: // タッチし始めた時
                        start_X = event.getRawX();
                        start_Y = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE: // ドラッグした時
                        // 何もしない
                        break;
                    case MotionEvent.ACTION_UP: // タッチを終了した時
                        boolean judge = false; // ネットワークの配置なのかケーブルの削除なのかを判断する
                        float end_X = event.getRawX();
                        float end_Y = event.getRawY();

                        for (Cable cable: cables) {
                            if (cable.onTouch(start_X, start_Y)) {
                                System.out.println("ケーブルをタッチしました");
                                judge = true;
                            }
                        }

                        if (judge == false){
                            for (Cable cable: cables) {
                                System.out.println("Test");
                                if (cable.disconnected(start_X, start_Y, end_X, end_Y)) {
                                    judge = true;
                                    cables.remove(cable.getCable_ID());
                                    --cableNum;
                                    layout.removeView(cable); // ケーブルを削除する
                                    System.out.println("ケーブルを削除しました");
                                }
                            }
                        }

                        if (!judge) {
                            System.out.println("ルータを配置");
                            float new_X = (int)event.getRawX();
                            float new_Y = (int)event.getRawY();
                            Router router = new Router(this, new_X, new_Y, routerNum++);
                            routers.add(router);
                            DeviceListener listener = new DeviceListener();
                            router.setOnTouchListener(listener);
                            router.setElevation(2); // Viewを一番上に表示する
                            layout.addView(router); // 先にaddViewをしないとヌルポになる

                            router.setLayoutParams(new ConstraintLayout.LayoutParams(width, height)); // パラメータで画像の幅と高さの設定
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)router.getLayoutParams();
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)layoutParams;
                            mlp.leftMargin = (int)(new_X-width/2); // 左のマージンを設定する
                            mlp.topMargin = (int)(new_Y-height/2); // 上のマージンを設定する
                            // mlp.setMargins(new_X, new_Y,new_X,new_Y); //上下左右のマージンを設定する

                            /**
                             * マージンの対象範囲を決める
                             */
                            // layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                            // layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // 左側
                            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; // 上側

                            router.setLayoutParams(layoutParams); // 作成したパラメーターを設定する

                            /**
                             * ラベルの設定
                             */
                            TextView hostname = router.getHostname();
                            hostname.setElevation(2);
                            layout.addView(hostname);
                            layoutParams = (ConstraintLayout.LayoutParams)hostname.getLayoutParams();
                            mlp = (ViewGroup.MarginLayoutParams)layoutParams;
                            mlp.leftMargin = (int)(new_X-width/2 + 50); // 左のマージンを設定する
                            mlp.topMargin = 0; // 上のマージンを設定する
                            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                            layoutParams.topToBottom = router.getId();
                            hostname.setLayoutParams(layoutParams);
                            router.setHostname(hostname); // これで擬似的にViewの親子関係が成り立つ(ほんとはGroupViewを作るべきやったかもしれない...)
                        }
                        break;
                    default:
                        break;
                }
                break;
            case Logical:
                // 多分使わない
                break;
            case Routing:
                /**
                RoutingCircle routingCircle = new RoutingCircle(this);
                Path path = routingCircle.getPath();

                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        path.moveTo(x, y);
                        routingCircle.invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        path.lineTo(x, y);
                        routingCircle.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        path.lineTo(x, y);
                        routingCircle.invalidate();
                        break;
                 */
                break;
            case ACL:
                break;
            case Memo:
                break;
            default:
                break;
        }

        return false;
    }

    /**
     * ネットワーク機器に対するイベントリスナーのメンバークラス
     * 物理構成図と論理構成図で使用する予定
     */
    private class DeviceListener implements View.OnTouchListener {
        private ConstraintLayout layout = findViewById(R.id.constraintlayout);
        private PhysicalChangeListener physicalChangeListener = new PhysicalChangeListener();
        private SetInformationListener setInformationListener = new SetInformationListener();

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (mode) {
                case Physical: // 物理構成モードのときにネットワーク機器をタッチすると発生するイベント
                    System.out.println("物理イベント発生");
                    physicalChangeListener.onTouch(v, event);
                    break;

                case Logical: // 論理構成モードのときにネットワーク機器をタッチすると発生するイベント
                    System.out.println("論理イベント発生");
                    setInformationListener.onTouch(v, event);
                    break;

                default:
                    break;
            }
            /**
             * 戻り値をtrueにすることで親のLayoutのタッチイベントが発生しない
             */
            return true;
        }
    }

    private class PhysicalChangeListener implements View.OnTouchListener {
        private ConstraintLayout layout = findViewById(R.id.constraintlayout);
        private boolean connecting = false; // 結線中


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    connecting = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    /**
                     * タッチしたネットワーク機器の範囲外に出た時に結線中とする
                     */
                    if (event.getRawX() > ((Device)v).getCenterX()+100 || event.getRawX() < ((Device)v).getCenterX()-100 ||
                    event.getRawY() > ((Device)v).getCenterY()+100 || event.getRawY() < ((Device)v).getCenterY()-100) {
                        connecting = true;
                        System.out.println("結線中");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (connecting) {
                        boolean find = false; // 対象のネットワーク機器を発見したかどうか
                        Cable cable;
                        for (Router router: routers) {
                            if (router.getCenterX()-100 < event.getRawX() && event.getRawX() < router.getCenterX()+100 &&
                            router.getCenterY()-100 < event.getRawY() && event.getRawY() < router.getCenterY()+100) {
                                cable = new Cable(context, ((Device)v).getCenterX(), ((Device)v).getCenterY(), router.getCenterX(), router.getCenterY(), cableNum++);
                                cables.add(cable);
                                layout.addView(cable);
                                System.out.println("結線完了");
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            for (Switch s: switches) {
                                if (s.getCenterX() - 100 < event.getRawX() && event.getRawX() < s.getCenterX() + 100 &&
                                        s.getCenterY() - 100 < event.getRawY() && event.getRawY() < s.getCenterY() + 100) {
                                    cable = new Cable(context, ((Device)v).getCenterX(), ((Device)v).getCenterY(), s.getCenterX(), s.getCenterY(), cableNum++);
                                    cables.add(cable);
                                    layout.addView(cable);
                                    System.out.println("結線完了");
                                    find = true;
                                    break;
                                }
                            }
                        }
                        if (!find) {
                            for (Host host: hosts) {
                                if (host.getCenterX() - 100 < event.getRawX() && event.getRawX() < host.getCenterX() + 100 &&
                                        host.getCenterY() - 100 < event.getRawY() && event.getRawY() < host.getCenterY() + 100) {
                                    cable = new Cable(context, ((Device)v).getCenterX(), ((Device)v).getCenterY(), host.getCenterX(), host.getCenterY(), cableNum++);
                                    cables.add(cable);
                                    layout.addView(cable);
                                    System.out.println("結線完了");
                                    find = true;
                                    break;
                                }
                            }
                            if (!find) {
                                System.out.println("結線失敗");
                            }
                        }

                    } else {
                        if (v.getClass() == Router.class) { // getClassでViewクラスではなく子クラスが取り出されることが分かった
                            Router router = (Router) v;
                            Switch s = new Switch(context, router.getCenterX(), router.getCenterY(), switchNum++);
                            s.setLayoutParams(router.getLayoutParams());
                            switches.add(s);
                            s.setElevation(2);
                            TextView hostname = s.getHostname();
                            ConstraintLayout.LayoutParams l = (ConstraintLayout.LayoutParams)router.getHostname().getLayoutParams();
                            l.topToBottom = s.getId();
                            hostname.setLayoutParams(l);
                            hostname.setElevation(2);
                            routers.remove(((Router) v).getID());
                            s.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            layout.removeView(router.getHostname()); // 前のラベルを削除
                            routerNum--;
                            layout.addView(s);
                            layout.addView(hostname);
                            System.out.println("スイッチに変更");
                        } else if (v.getClass() == Switch.class) {
                            Switch s = (Switch) v;
                            Host host = new Host(context, s.getCenterX(), s.getCenterY(), hostNum++);
                            host.setLayoutParams(s.getLayoutParams());
                            hosts.add(host);
                            host.setElevation(2);
                            TextView hostname = host.getHostname();
                            ConstraintLayout.LayoutParams l = (ConstraintLayout.LayoutParams)s.getHostname().getLayoutParams();
                            l.topToBottom = host.getId();
                            hostname.setLayoutParams(l);
                            hostname.setElevation(2);
                            switches.remove(((Switch) v).getID());
                            switchNum--;
                            host.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            layout.removeView(s.getHostname()); // 前のラベルを削除
                            layout.addView(host);
                            layout.addView(hostname);
                            if (host.getID() == 0) {
                                layout.addView(new VLAN(context, host.getCenterX(), host.getCenterY(), 1));
                            } else {
                                layout.addView(new VLAN(context, host.getCenterX(), host.getCenterY(), 10));
                            }

                            System.out.println("ホストに変更");
                        } else {
                            Host host = (Host) v;
                            Router router = new Router(context, host.getCenterX(), host.getCenterY(), routerNum++);
                            router.setLayoutParams(host.getLayoutParams());
                            routers.add(router);
                            router.setElevation(2);
                            TextView hostname = router.getHostname();
                            ConstraintLayout.LayoutParams l = (ConstraintLayout.LayoutParams)host.getHostname().getLayoutParams();
                            l.topToBottom = router.getId();
                            hostname.setLayoutParams(l);
                            hostname.setElevation(2);
                            hosts.remove(((Host) v).getID());
                            hostNum--;
                            router.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            layout.removeView(router.getHostname()); // 前のラベルを削除
                            layout.addView(router);
                            layout.addView(hostname);
                            System.out.println("ルータに変更");
                        }

                        System.out.println("Change");
                    }
                    break;

                default:
                    break;

            }

            return true;
        }
    }

    // SubActivityの開始を決めるクラス
    private class SetInformationListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    Intent intent;
                    if (v.getClass() == Router.class) {
                        intent = new Intent(MainActivity.this, LogicalRouterActivity.class);
                    } else if (v.getClass() == Switch.class) {
                        intent = new Intent(MainActivity.this, LogicalSwitchActivity.class);
                    } else if (v.getClass() == Host.class) {
                        intent = new Intent(MainActivity.this, LogicalHostActivity.class);
                    } else {
                        intent = null;
                    }

                    startActivity(intent);
                    break;
            }
            return true;
        }
    }

    protected void startRoutingActivity() {
        Intent intent;
        intent = new Intent(MainActivity.this, RoutingActivity.class);
        startActivity(intent);
    }

    protected void startACLActivity() {
        Intent intent;
        intent = new Intent(MainActivity.this, ACLActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == RESULT_SUBACTIVITY && intent != null) {
            // ここから遷移先からのデータを受け取る
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }


}