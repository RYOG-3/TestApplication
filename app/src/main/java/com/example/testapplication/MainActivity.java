package com.example.testapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Context;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private int routerNum = 0, switchNum = 0, hostNum = 0; // それぞれのネットワーク機器台数
    private int cableNum = 0; // 結線したケーブルの数
    private int start_X, start_Y; // ケーブルの削除をする時に使用する
    private ArrayList<Router> routers = new ArrayList<>(); // 配置したルータたち
    private ArrayList<Switch> switches = new ArrayList<>(); // 配置したスイッチたち
    private ArrayList<Host> hosts = new ArrayList<>(); // 配置したホストたち
    private ArrayList<Cable> cables = new ArrayList<>(); // 結線したケーブルたち
    private Mode mode = Mode.Physical; // 現在の状態(初期状態は物理構成)

    private Context context = this;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /**
         * 物理構成図ボタンのリスナー設定
         */
        Button physical_bt = findViewById(R.id.physical);
        physical_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = Mode.Physical;
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
                        start_X = (int)event.getRawX();
                        start_Y = (int)event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE: // ドラッグした時
                        // 何もしない
                        break;
                    case MotionEvent.ACTION_UP: // タッチを終了した時
                        boolean judge = false; // ネットワークの配置なのかケーブルの削除なのかを判断する
                        int end_X = (int)event.getRawX();
                        int end_Y = (int)event.getRawY();
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
                        if (!judge) {
                            System.out.println("ルータを配置");
                            int new_X = (int)event.getRawX();
                            int new_Y = (int)event.getRawY();
                            Router router = new Router(this, new_X, new_Y, routerNum++);
                            routers.add(router);
                            DeviceListener listener = new DeviceListener();
                            router.setOnTouchListener(listener);
                            router.setElevation(2); // Viewを一番上に表示する
                            layout.addView(router); // 先にaddViewをしないとヌルポになる

                            router.setLayoutParams(new ConstraintLayout.LayoutParams(width, height)); // パラメータで画像の幅と高さの設定
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)router.getLayoutParams();
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)layoutParams;
                            mlp.leftMargin = new_X-width/2; // 左のマージンを設定する
                            mlp.topMargin = new_Y-height/2; // 上のマージンを設定する
                            // mlp.setMargins(new_X, new_Y,new_X,new_Y); //上下左右のマージンを設定する

                            /**
                             * マージンの対象範囲を決める
                             */
                            // layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                            // layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // 左側
                            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; // 上側

                            router.setLayoutParams(layoutParams); // 作成したパラメーターを設定する
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
                            routers.remove(((Router) v).getID());
                            s.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            routerNum--;
                            layout.addView(s);
                            System.out.println("スイッチに変更");
                        } else if (v.getClass() == Switch.class) {
                            Switch s = (Switch) v;
                            Host host = new Host(context, s.getCenterX(), s.getCenterY(), hostNum++);
                            host.setLayoutParams(s.getLayoutParams());
                            hosts.add(host);
                            host.setElevation(2);
                            switches.remove(((Switch) v).getID());
                            switchNum--;
                            host.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            layout.addView(host);
                            System.out.println("ホストに変更");
                        } else {
                            Host host = (Host) v;
                            Router router = new Router(context, host.getCenterX(), host.getCenterY(), routerNum++);
                            router.setLayoutParams(host.getLayoutParams());
                            routers.add(router);
                            router.setElevation(2);
                            hosts.remove(((Host) v).getID());
                            hostNum--;
                            router.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            layout.addView(router);
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


}