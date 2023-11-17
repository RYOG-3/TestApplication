package com.example.testapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected static ArrayList<Router> routers = new ArrayList<>(); // 配置したルータたち(削除したのも含む)
    protected static ArrayList<Switch> switches = new ArrayList<>(); // 配置したスイッチたち(削除したのも含む)
    protected static ArrayList<Host> hosts = new ArrayList<>(); // 配置したホストたち(削除したのも含む)
    protected static ArrayList<Cable> cables = new ArrayList<>(); // 結線したケーブルたち(削除したのも含む)
    protected static ArrayList<ACLArrow> arrows = new ArrayList<>(); // ACLの矢印たち(削除したのも含む)
    protected static Mode mode = Mode.Physical; // 現在の状態(初期状態は物理構成)
    private static MainActivity instance = null;
    private final int RESULT_LOGICALROUTER = 1000;
    private final int RESULT_LOGICALSWITCH = 2000;
    private final int RESULT_LOGICALHOST = 3000;
    private final int RESULT_ROUTINGACTIVITY = 4000;
    private final int RESULT_PINKACLACTIVITY = 5000;
    private final int RESULT_BLUEACLACTIVITY = 6000;
    private final int RESULT_CABLEACLACTIVITY = 7000;
    private int routerNum = 0, switchNum = 0, hostNum = 0; // それぞれのネットワーク機器台数
    private int cableNum = 0; // 結線したケーブルの数
    private int arrowNum = 0; // 矢印の数
    private float start_X, start_Y; // ケーブルの削除をする時に使用する
    private RoutingCircle routingCircle; // ルーティング図で使う描画用クラス
    private Memo memo; // メモ機能で使う描画用クラス
    private Device targetDevice; // サブアクティビティを開始した時にどのネットワーク機器を対象にしているかを示す変数
    private Cable targetCable; // サブアクティビティを開始したときにどのケーブルを対象にしているかを示す変数
    private ACLArrow targetArrow; // サブアクティビティを開始したときにどの矢印を対象にしているかを示す変数
    private int targetCircle; // サブアクティビティを開始したときにどの円を対象にしているかを示す変数
    private Context context = this;

    public static MainActivity getInstance() {
        return instance;
    }

    /**
     * routers の null を除いたルータのArrayList を返す
     *
     * @return 配置したルータのリスト
     */
    protected static ArrayList<Router> getAvailableRouters() {
        ArrayList<Router> availableRouters = new ArrayList<>();

        for (Router router : routers) {
            if (router != null) {
                availableRouters.add(router);
            }
        }
        return availableRouters;
    }

    /**
     * switches の null を除いたスイッチの ArrayList を返す
     *
     * @return 配置したスイッチのリスト
     */
    protected static ArrayList<Switch> getAvailableSwitches() {
        ArrayList<Switch> availableSwitches = new ArrayList<>();

        for (Switch s : switches) {
            if (s != null) {
                availableSwitches.add(s);
            }
        }
        return availableSwitches;
    }

    /**
     * hosts の null を除いたホストの ArrayList を返す
     *
     * @return 配置したホストのリスト
     */
    protected static ArrayList<Host> getAvailableHost() {
        ArrayList<Host> availableHosts = new ArrayList<>();

        for (Host host : hosts) {
            if (host != null) {
                availableHosts.add(host);
            }
        }
        return availableHosts;
    }

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
                changeButtonState();
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                for (ACLArrow arrow : arrows) {
                    arrow.setVisibility(View.INVISIBLE);
                }
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
                changeButtonState();
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                for (ACLArrow arrow : arrows) {
                    arrow.setVisibility(View.INVISIBLE);
                }
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
                changeButtonState();
                routingCircle.routingInvalidate(); // onDraw() が呼び出される
                memo.memoInvalidate();
                for (ACLArrow arrow : arrows) {
                    arrow.setVisibility(View.INVISIBLE);
                }
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
                changeButtonState();
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                for (ACLArrow arrow : arrows) {
                    arrow.setVisibility(View.VISIBLE);
                }
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
                changeButtonState();
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                for (ACLArrow arrow : arrows) {
                    arrow.setVisibility(View.INVISIBLE);
                }
                System.out.println("メモモードに変更");
            }
        });

        /**
         * 発行ボタンのリスナー設定
         */
        Button issue_bt = findViewById(R.id.issue);
        issue_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = Mode.Issue;
                changeButtonState();
                routingCircle.routingInvalidate();
                memo.memoInvalidate();
                for (ACLArrow arrow : arrows) {
                    arrow.setVisibility(View.INVISIBLE);
                }
                System.out.println("管理サーバーに設定情報を送信します");
                CreateJSONData createJSONData = new CreateJSONData(context);
                if (createJSONData.setData()) {
                    new HttpRequester(
                            MainActivity.this, // コンテキスト
                            // "http://10.0.2.2:10000/", // 接続先URL テスト環境ではエミュレータからローカルマシンにブリッジするIPアドレス
                            // 実機タブレットを使用するときの宛先IPアドレス
                            "http://172.18.0.106:10000/",
                            "発行中...", // 通信中に砂時計に表示するメッセージ
                            createJSONData.getJson(), // 送信するJSONデータ
                            b -> {
                                // 通信成功時のコールバック処理。bはレスポンスのbyte配列。
                                System.out.println(new String(b));
                            },
                            e -> {
                                // 通信失敗時のコールバック処理。eはException。
                                System.out.println(e.toString());
                            }
                    ).execute();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "エラーにより発行できませんでした";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
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

                        for (Cable cable : cables) {
                            if (cable == null) {
                                continue;
                            } else if (cable.onTouch(start_X, start_Y)) {
                                System.out.println("ケーブルをタッチしました");
                                judge = true;
                            }
                        }

                        if (judge == false) {
                            for (Cable cable : cables) {
                                System.out.println("Test");
                                if (cable == null) {
                                    continue;
                                } else if (cable.disconnected(start_X, start_Y, end_X, end_Y)) {
                                    judge = true;
                                    cables.set(cable.getCable_ID(), null);
                                    layout.removeView(cable); // ケーブルを削除する
                                    System.out.println("ケーブルを削除しました");
                                }
                            }
                        }

                        if (!judge) {
                            System.out.println("ルータを配置");
                            float new_X = (int) event.getRawX();
                            float new_Y = (int) event.getRawY();
                            Router router = new Router(this, new_X, new_Y, routerNum++);
                            routers.add(router);
                            DeviceListener listener = new DeviceListener();
                            router.setOnTouchListener(listener);
                            router.setElevation(2); // Viewを一番上に表示する
                            layout.addView(router); // 先にaddViewをしないとヌルポになる

                            router.setLayoutParams(new ConstraintLayout.LayoutParams(width, height)); // パラメータで画像の幅と高さの設定
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) router.getLayoutParams();
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) layoutParams;
                            mlp.leftMargin = (int) (new_X - width / 2); // 左のマージンを設定する
                            mlp.topMargin = (int) (new_Y - height / 2); // 上のマージンを設定する
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
                            layoutParams = (ConstraintLayout.LayoutParams) hostname.getLayoutParams();
                            mlp = (ViewGroup.MarginLayoutParams) layoutParams;
                            mlp.leftMargin = (int) (new_X - width / 2); // 左のマージンを設定する
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

                        for (Cable cable : cables) {
                            if (cable == null) {
                                continue;
                            } else if (cable.onTouch(start_X, start_Y)) {
                                System.out.println("インターフェイスとipアドレスまたはVLANを選択してください");
                                startCableActivity(cable);
                            }
                        }
                }
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
                // ケーブルに沿って線をなぞると矢印が表示されるようにする
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
                        float end_X = event.getRawX();
                        float end_Y = event.getRawY();
                        for (Cable cable : MainActivity.cables) {
                            if (cable == null) ;
                            else if (cable.onTouch(start_X, start_Y)) {
                                System.out.println("ケーブルをなぞりました");
                                if (ACLArrow.checkConnectedRouter(start_X, start_Y, end_X, end_Y, cable)) {
                                    ACLArrow arrow = new ACLArrow(this, start_X, start_Y, end_X, end_Y, arrowNum++, cable);
                                    if (arrow.getTargetRouter() != null) {
                                        arrows.add(arrow);
                                        layout.addView(arrow);
                                        arrow.setElevation(2);
                                        arrow.setLayoutParams(new ConstraintLayout.LayoutParams(50, 50)); // パラメータで画像の幅と高さの設定
                                        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) arrow.getLayoutParams();
                                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                                        mlp.leftMargin = (int) arrow.getCenterX();
                                        mlp.topMargin = (int) arrow.getCenterY();
                                        lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                                        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                                        arrow.setLayoutParams(lp);
                                        Bitmap bitmap = ((BitmapDrawable) arrow.getDrawable()).getBitmap(); // arrowのBitmap情報を取得
                                        Bitmap rotate_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), arrow.getMatrix(), true);
                                        arrow.setImageBitmap(rotate_bitmap);
                                        arrow.setOnTouchListener(new ArrowTouchListener());
                                        break;
                                    }
                                } else {
                                    System.out.println("ルータ以外には矢印を表示できません");
                                }
                            }
                        }
                }
                break;
            case Memo:
                break;
            default:
                break;
        }

        return false;
    }

    protected void startRoutingActivity(int targetCircle) {
        this.targetCircle = targetCircle;

        List<Router> routers = routingCircle.getRouters(targetCircle);

        if (routers != null) {
            Intent intent;
            intent = new Intent(MainActivity.this, RoutingActivity.class);
            intent.putExtra("rip", routers.get(0).getRIP());
            intent.putExtra("ospf", routers.get(0).getOSPF());
            int requestCode = 4000;
            startActivityForResult(intent, requestCode);
        }
    }

    protected void startCableActivity(Cable targetCable) {
        this.targetCable = targetCable;
        boolean judge = true;
        String type;
        Intent intent;
        intent = new Intent(MainActivity.this, CableActivity.class);
        int requestCode = 7000;
        Device device1 = targetCable.getDevice1();
        Device device2 = targetCable.getDevice2();


        if ((device1.getClass() == Switch.class && device2.getClass() == Router.class) ||
                (device1.getClass() == Router.class && device2.getClass() == Switch.class)) {
            type = "RouterAndSwitch";
            intent.putExtra("Type", type);
        } else if (device1.getClass() == Router.class && device2.getClass() == Router.class) {
            type = "Two_Router";
            intent.putExtra("Type", type);
            intent.putExtra("TargetHostname1", device1.getHostname().getText());
            intent.putExtra("TargetHostname2", device2.getHostname().getText());
        } else if (device1.getClass() == Switch.class && device2.getClass() == Switch.class) {
            type = "Two_Switch";
            intent.putExtra("Type", type);
            intent.putExtra("TargetHostname1", device1.getHostname().getText());
            intent.putExtra("TargetHostname2", device2.getHostname().getText());
        } else if (device1.getClass() == Router.class || device2.getClass() == Router.class) {
            Router targetRouter;
            List<Interface> interfaces;
            boolean new_Interface = true;
            type = "Only_Router";
            if (device1.getClass() == Router.class) {
                targetRouter = (Router) device1;

            } else {
                targetRouter = (Router) device2;
            }
            intent.putExtra("Type", type);
            intent.putExtra("TargetHostname", targetRouter.getHostname().getText());
            interfaces = targetRouter.getInterfaces();


            if (interfaces.size() == 0) new_Interface = true;
            for (int i = 0; i < interfaces.size(); i++) {
                if (interfaces.get(i).getConnectedCableID() == targetCable.getCable_ID()) {
                    intent.putExtra("interfaceType", interfaces.get(i).getInterfaceType().toString());
                    intent.putExtra("ipv4", interfaces.get(i).getIPv4());
                    intent.putExtra("subnet_mask", interfaces.get(i).getSubnet_mask());
                    intent.putExtra("shutdown", interfaces.get(i).getShutdown());
                    new_Interface = false;
                    break;
                }
                if (i == interfaces.size()-1) {
                    new_Interface = true;
                }
            }
            if (new_Interface) {
                intent.putExtra("interfaceType", "");
                intent.putExtra("ipv4", "");
                intent.putExtra("subnet_mask", "");
                intent.putExtra("shutdown", false);
                System.out.println("新規インターフェース");
            }

        } else if (device1.getClass() == Switch.class || device2.getClass() == Switch.class) {
            Switch targetSwitch;
            type = "Only_Switch";
            if (device1.getClass() == Switch.class) {
                targetSwitch = (Switch) device1;
            } else {
                targetSwitch = (Switch) device2;
            }
            intent.putExtra("Type", type);
            intent.putExtra("TargetHostname", targetSwitch.getHostname().getText());
            List<VLAN> vlanList = targetSwitch.getVlanList();
            if (vlanList.size() != 0) {
                ArrayList<Integer> vlanIds = new ArrayList<>();
                ArrayList<String> vlanNames = new ArrayList<>();
                for (VLAN vlan: vlanList) {
                    vlanIds.add(vlan.getVlanID());
                    vlanNames.add(vlan.getVlanName());
                }
                intent.putExtra("interfaceType", ""); // TODO 既存のインターフェースの値を入れる
                intent.putExtra("switchPortType", false);
                intent.putExtra("shutdown", false);
                intent.putIntegerArrayListExtra("vlanIds", vlanIds);
                intent.putStringArrayListExtra("vlanNames", vlanNames);
            } else {
                intent.putExtra("interfaceType", "");
                intent.putExtra("shutdown", false);
                System.out.println("新規インターフェース");
            }
        } else {
            System.out.println("エラー");
            judge = false;
        }
        if (judge) {
            startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 遷移先のアクティビティからのデータを受け取るメソッド
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        ConstraintLayout layout = findViewById(R.id.constraintlayout);
        System.out.println(requestCode);

        if (resultCode == RESULT_OK && intent != null) {
            String hostname;
            Device device = getTargetDevice(); // ルータやスイッチをタッチしたときに使う

            switch (requestCode) {
                case RESULT_LOGICALROUTER: // ルータの論理構成図
                    // String enablePassword = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
                    if (intent.getBooleanExtra("Remove", false)) { // ルータの削除の処理(ラベルも消す)
                        routers.set(device.getID(), null);
                        System.out.println(routers);
                        layout.removeView((View) device);
                        layout.removeView(device.getHostname());
                    } else {
                        hostname = intent.getStringExtra("hostname");

                        Router router = (Router) device;
                        if (hostname != null) {
                            router.getHostname().setText(hostname);
                        }

                        router.setManagement_ipAddress(intent.getStringExtra("management_IPAddress"));
                        router.setIp_domain_lookup(intent.getBooleanExtra("ip_domain_lookup", false));
                        router.setBanner(intent.getStringExtra("banner"));
                        router.setService_password(intent.getBooleanExtra("encryption", false));
                        router.setDomain_name(intent.getStringExtra("domain"));
                        router.setEnable_secret(intent.getStringExtra("enable_secret"));
                        router.setPassword(intent.getStringExtra("console_password"));
                        router.setLogging(intent.getBooleanExtra("logging", false));
                        System.out.println(hostname);
                    }
                    break;
                case RESULT_LOGICALSWITCH: // スイッチの論理構成図
                    if (intent.getBooleanExtra("Remove", false)) { // スイッチの削除の処理(ラベルも消す)
                        switches.set(device.getID(), null);
                        layout.removeView((View) device);
                        layout.removeView(device.getHostname());
                    } else {
                        hostname = intent.getStringExtra("hostname");

                        Switch s = (Switch) device;
                        if (hostname != null) {
                            s.getHostname().setText(hostname);
                        }

                        s.setManagement_ipAddress(intent.getStringExtra("management_IPAddress"));
                        s.setIp_domain_lookup(intent.getBooleanExtra("ip_domain_lookup", false));
                        s.setBanner(intent.getStringExtra("banner"));
                        s.setService_password(intent.getBooleanExtra("encryption", false));
                        s.setDomain_name(intent.getStringExtra("domain"));
                        s.setEnable_secret(intent.getStringExtra("enable_secret"));
                        s.setPassword(intent.getStringExtra("console_password"));
                        s.setLogging(intent.getBooleanExtra("logging", false));
                        System.out.println(hostname);
                    }
                    break;
                case RESULT_LOGICALHOST: // ホストの論理構成図
                    if (intent.getBooleanExtra("Remove", false)) { // ホストの削除の処理(ラベルも消す)
                        hosts.set(device.getID(), null);
                        layout.removeView((View) device);
                        layout.removeView(device.getHostname());
                    }
                    hostname = intent.getStringExtra("hostname");
                    if (hostname != null) {
                        device.getHostname().setText(hostname);
                    }
                    System.out.println(hostname);
                    break;
                case RESULT_ROUTINGACTIVITY: // ルーティング図のアクティビティ
                    if (intent.getBooleanExtra("Remove", false)) { // 囲われた円を削除する
                        routingCircle.removeLine(this.targetCircle);
                    } else { // OKボタンが押されたとき
                        List<Router> routers = routingCircle.getRouters(targetCircle);

                        for (Router router : routers) {
                            router.setRIP(intent.getBooleanExtra("rip", false));
                            router.setOSPF(intent.getBooleanExtra("ospf", false));
                        }
                    }
                    break;
                case RESULT_PINKACLACTIVITY: // ACL図のアクティビティ
                case RESULT_BLUEACLACTIVITY:
                    if (intent.getBooleanExtra("Remove", false)) { // 矢印を削除する
                        arrows.remove(this.targetArrow);
                        layout.removeView(this.targetArrow);
                        System.out.println("矢印が削除されました");
                    } else {
                        // targetArrow.setPermission(intent.getBooleanArrayExtra("permission"));
                        targetArrow.setPermission((Boolean[]) intent.getSerializableExtra("permission"));
                        targetArrow.setIpAddresses(intent.getStringArrayListExtra("ipaddress"));
                        targetArrow.setWildcards(intent.getStringArrayListExtra("wildcard"));
                    }
                    break;
                case RESULT_CABLEACLACTIVITY: // ケーブルアクティビティ
                    String type = intent.getStringExtra("type");
                    Device connectedDevice1 = targetCable.getDevice1();
                    Device connectedDevice2 = targetCable.getDevice2();
                    ArrayList<Integer> vlanIds;
                    ArrayList<String> vlanNames;
                    InterfaceType interfaceType;
                    boolean switchPortType;
                    boolean new_Interface;

                    switch (type) {
                        case "RouterAndSwitch":
                            System.out.println("ルータとスイッチの組み合わせは未実装");
                            break;
                        case "Only_Router":
                            Router connectedRouter;
                            if (connectedDevice1.getClass() == Router.class) {
                                connectedRouter = (Router)connectedDevice1;
                            } else {
                                connectedRouter = (Router)connectedDevice2;
                            }
                            interfaceType = InterfaceType.valueOf(intent.getStringExtra("interfaceType"));
                            String ipv4 = intent.getStringExtra("ipv4");
                            String subnet_mask = intent.getStringExtra("subnet_mask");
                            boolean shutdown = intent.getBooleanExtra("shutdown", false);
                            Interface router_interface;
                            List<Interface> interfaces;
                            new_Interface = true;

                            if (connectedRouter.getInterfaces().size() == 0) {
                                new_Interface = true;
                            }
                            interfaces = connectedRouter.getInterfaces();
                            for (int i = 0; i < interfaces.size(); i++) {
                                if (interfaces.get(i).getConnectedCableID() == targetCable.getCable_ID()) {
                                    interfaces.get(i).setInterfaceType(interfaceType);
                                    interfaces.get(i).setIpv4(ipv4);
                                    interfaces.get(i).setSubnet_mask(subnet_mask);
                                    interfaces.get(i).setShutdown(shutdown);
                                    new_Interface = false;
                                    break;
                                }
                            }
                            if (new_Interface) {
                                router_interface = new Interface(interfaceType, ipv4, subnet_mask, shutdown, targetCable.getCable_ID());
                                connectedRouter.addInterface(router_interface);
                            }
                            break;
                        case "Two_Router":
                            System.out.println("ルータ同士は未実装");
                            break;
                        case "Only_Switch":
                            Switch connectedSwitch;
                            if (connectedDevice1.getClass() == Switch.class) {
                                connectedSwitch = (Switch)connectedDevice1;
                            } else {
                                connectedSwitch = (Switch)connectedDevice2;
                            }

                            new_Interface = true;
                            vlanIds = intent.getIntegerArrayListExtra("vlanIds");
                            vlanNames = intent.getStringArrayListExtra("vlanNames");
                            interfaceType = InterfaceType.valueOf(intent.getStringExtra("interfaceType"));
                            switchPortType = intent.getBooleanExtra("switchPortType", false);

                            interfaces = connectedSwitch.getInterfaces();
                            for (int i = 0; i < interfaces.size(); i++) {
                                if (interfaces.get(i).getConnectedCableID() == targetCable.getCable_ID()) {
                                    interfaces.get(i).setInterfaceType(interfaceType);
                                    interfaces.get(i).setShutdown(false);
                                    new_Interface = false;
                                    break;
                                }
                            }

                            if (new_Interface && vlanIds.size()!=0) {
                                Interface switch_Interface = new Interface(interfaceType, shutdown, targetCable.getCable_ID());
                                connectedSwitch.addInterface(switch_Interface);
                            }


                            List<VLAN> vlanList = new ArrayList<>();
                            for (int i = 0; i < vlanIds.size(); i++) {
                                VLAN vlan = new VLAN(this, 0, 0, vlanIds.get(i), vlanNames.get(i),
                                        interfaceType, switchPortType); // x, y はとりあえず 0 VLANの色表示で使うかも？
                                vlanList.add(vlan);
                            }
                            connectedSwitch.addVlanList(vlanList);
                            System.out.println("VLAN情報登録完了");
                            break;
                        case "Two_Switch":
                            System.out.println("スイッチ同士は未実装"); // ２つのスイッチに対して addVlan メソッド
                            break;
                    }

                    break;
            }
        }
    }

    public Device getTargetDevice() {
        return targetDevice;
    }

    /**
     * targetDeviceのセッター
     */
    public void setTargetDevice(Device device) {
        targetDevice = device;
    }

    private void changeButtonState() {
        Button physical_bt = findViewById(R.id.physical);
        Button logical_bt = findViewById(R.id.logical);
        Button routing_bt = findViewById(R.id.routing);
        Button acl_bt = findViewById(R.id.acl);
        Button memo_bt = findViewById(R.id.memo);
        Button issue_bt = findViewById(R.id.issue);

        switch (mode) {
            case Physical:
                physical_bt.setEnabled(false);
                logical_bt.setEnabled(true);
                routing_bt.setEnabled(true);
                acl_bt.setEnabled(true);
                memo_bt.setEnabled(true);
                issue_bt.setEnabled(true);
                break;
            case Logical:
                physical_bt.setEnabled(true);
                logical_bt.setEnabled(false);
                routing_bt.setEnabled(true);
                acl_bt.setEnabled(true);
                memo_bt.setEnabled(true);
                issue_bt.setEnabled(true);
                break;
            case Routing:
                physical_bt.setEnabled(true);
                logical_bt.setEnabled(true);
                routing_bt.setEnabled(false);
                acl_bt.setEnabled(true);
                memo_bt.setEnabled(true);
                issue_bt.setEnabled(true);
                break;
            case ACL:
                physical_bt.setEnabled(true);
                logical_bt.setEnabled(true);
                routing_bt.setEnabled(true);
                acl_bt.setEnabled(false);
                memo_bt.setEnabled(true);
                issue_bt.setEnabled(true);
                break;
            case Memo:
                physical_bt.setEnabled(true);
                logical_bt.setEnabled(true);
                routing_bt.setEnabled(true);
                acl_bt.setEnabled(true);
                memo_bt.setEnabled(false);
                issue_bt.setEnabled(true);
                break;
            case Issue:
                physical_bt.setEnabled(true);
                logical_bt.setEnabled(true);
                routing_bt.setEnabled(true);
                acl_bt.setEnabled(true);
                memo_bt.setEnabled(true);
                issue_bt.setEnabled(false);
                break;
        }
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
                    if (event.getRawX() > ((Device) v).getCenterX() + 100 || event.getRawX() < ((Device) v).getCenterX() - 100 ||
                            event.getRawY() > ((Device) v).getCenterY() + 100 || event.getRawY() < ((Device) v).getCenterY() - 100) {
                        connecting = true;
                        System.out.println("結線中");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (connecting) {
                        boolean find = false; // 対象のネットワーク機器を発見したかどうか
                        Cable cable;
                        for (Router router : routers) {
                            if (router == null) {
                                continue;
                            } else if (router.getCenterX() - 100 < event.getRawX() && event.getRawX() < router.getCenterX() + 100 &&
                                    router.getCenterY() - 100 < event.getRawY() && event.getRawY() < router.getCenterY() + 100) {
                                cable = new Cable(context, ((Device) v).getCenterX(), ((Device) v).getCenterY(), router.getCenterX(), router.getCenterY(), cableNum++, (Device) v, (Device) router);
                                cables.add(cable);
                                ((Device) v).setCable(cable);
                                router.setCable(cable);
                                layout.addView(cable);
                                System.out.println("結線完了");
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            for (Switch s : switches) {
                                if (s == null) {
                                    continue;
                                } else if (s.getCenterX() - 100 < event.getRawX() && event.getRawX() < s.getCenterX() + 100 &&
                                        s.getCenterY() - 100 < event.getRawY() && event.getRawY() < s.getCenterY() + 100) {
                                    cable = new Cable(context, ((Device) v).getCenterX(), ((Device) v).getCenterY(), s.getCenterX(), s.getCenterY(), cableNum++, (Device) v, (Device) s);
                                    cables.add(cable);
                                    ((Device) v).setCable(cable);
                                    s.setCable(cable);
                                    layout.addView(cable);
                                    System.out.println("結線完了");
                                    find = true;
                                    break;
                                }
                            }
                        }
                        if (!find) {
                            for (Host host : hosts) {
                                if (host == null) {
                                    continue;
                                } else if (host.getCenterX() - 100 < event.getRawX() && event.getRawX() < host.getCenterX() + 100 &&
                                        host.getCenterY() - 100 < event.getRawY() && event.getRawY() < host.getCenterY() + 100) {
                                    cable = new Cable(context, ((Device) v).getCenterX(), ((Device) v).getCenterY(), host.getCenterX(), host.getCenterY(), cableNum++, (Device) v, (Device) host);
                                    cables.add(cable);
                                    ((Device) v).setCable(cable);
                                    host.setCable(cable);
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
                            ConstraintLayout.LayoutParams l = (ConstraintLayout.LayoutParams) router.getHostname().getLayoutParams();
                            l.topToBottom = s.getId();
                            hostname.setLayoutParams(l);
                            hostname.setElevation(2);
                            routers.set(((Router) v).getID(), null);
                            s.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            layout.removeView(router.getHostname()); // 前のラベルを削除
                            layout.addView(s);
                            layout.addView(hostname);

                            Cable cable = router.getCable();
                            if (cable != null) {
                                s.setCable(cable);
                                cable.setDevice(s);
                            }

                            System.out.println("スイッチに変更");
                        } else if (v.getClass() == Switch.class) {
                            Switch s = (Switch) v;
                            Host host = new Host(context, s.getCenterX(), s.getCenterY(), hostNum++);
                            host.setLayoutParams(s.getLayoutParams());
                            hosts.add(host);
                            host.setElevation(2);
                            TextView hostname = host.getHostname();
                            ConstraintLayout.LayoutParams l = (ConstraintLayout.LayoutParams) s.getHostname().getLayoutParams();
                            l.topToBottom = host.getId();
                            hostname.setLayoutParams(l);
                            hostname.setElevation(2);
                            switches.set(((Switch) v).getID(), null);
                            host.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            layout.removeView(s.getHostname()); // 前のラベルを削除
                            layout.addView(host);
                            layout.addView(hostname);
                            /**
                             if (host.getID() == 0) {
                             layout.addView(new VLAN(context, host.getCenterX(), host.getCenterY(), 1));
                             } else {
                             layout.addView(new VLAN(context, host.getCenterX(), host.getCenterY(), 10));
                             }
                             */

                            Cable cable = s.getCable();
                            if (cable != null) {
                                host.setCable(cable);
                                cable.setDevice(host);
                            }

                            System.out.println("ホストに変更");
                        } else {
                            Host host = (Host) v;
                            Router router = new Router(context, host.getCenterX(), host.getCenterY(), routerNum++);
                            router.setLayoutParams(host.getLayoutParams());
                            routers.add(router);
                            router.setElevation(2);
                            TextView hostname = router.getHostname();
                            ConstraintLayout.LayoutParams l = (ConstraintLayout.LayoutParams) host.getHostname().getLayoutParams();
                            l.topToBottom = router.getId();
                            hostname.setLayoutParams(l);
                            hostname.setElevation(2);
                            hosts.set(((Host) v).getID(), null);
                            router.setOnTouchListener(new DeviceListener());
                            layout.removeView(v); // タッチしたViewを削除する
                            layout.removeView(host.getHostname()); // 前のラベルを削除
                            layout.addView(router);
                            layout.addView(hostname);

                            Cable cable = host.getCable();
                            if (cable != null) {
                                router.setCable(cable);
                                cable.setDevice(router);
                            }

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
        int requestCode;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    Intent intent;
                    if (v.getClass() == Router.class) { // Router の場合の SubActivity
                        intent = new Intent(MainActivity.this, LogicalRouterActivity.class);
                        Router router = (Router) v;
                        System.out.println("TEST" + router.getManagement_ipAddress());
                        intent.putExtra("management_IPAddress", router.getManagement_ipAddress());
                        intent.putExtra("hostname", router.getHostname().getText());
                        intent.putExtra("ip_domain_lookup", router.getIp_domain_lookup());
                        intent.putExtra("banner", router.getBanner());
                        intent.putExtra("service_password", router.getService_password());
                        intent.putExtra("ip_domain_name", router.getDomain_name());
                        intent.putExtra("enable_secret", router.getEnable_secret());
                        intent.putExtra("password", router.getPassword());
                        intent.putExtra("logging", router.getLogging());
                        requestCode = 1000;
                        setTargetDevice((Device) v);
                    } else if (v.getClass() == Switch.class) { // Switch の場合の SubActivity
                        intent = new Intent(MainActivity.this, LogicalSwitchActivity.class);
                        Switch s = (Switch) v;
                        intent.putExtra("management_IPAddress", s.getManagement_ipAddress());
                        intent.putExtra("hostname", s.getHostname().getText());
                        intent.putExtra("ip_domain_lookup", s.getIp_domain_lookup());
                        intent.putExtra("banner", s.getBanner());
                        intent.putExtra("service_password", s.getService_password());
                        intent.putExtra("ip_domain_name", s.getDomain_name());
                        intent.putExtra("enable_secret", s.getEnable_secret());
                        intent.putExtra("password", s.getPassword());
                        intent.putExtra("logging", s.getLogging());
                        requestCode = 2000;
                        setTargetDevice((Device) v);
                    } else if (v.getClass() == Host.class) {
                        intent = new Intent(MainActivity.this, LogicalHostActivity.class);
                        intent.putExtra("hostname", ((Host) v).getHostname().getText());
                        requestCode = 3000;
                        setTargetDevice((Device) v);
                    } else {
                        intent = null;
                        requestCode = 0;
                    }

                    startActivityForResult(intent, requestCode);
                    break;
            }
            return true;
        }
    }

    private class ArrowTouchListener implements View.OnTouchListener {
        int requestCode;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    Intent intent;
                    ACLArrow aclArrow = (ACLArrow) v;


                    if (aclArrow.getIsJudge()) {
                        intent = new Intent(MainActivity.this, ACLActivity.class);
                        System.out.println("インバウンド");
                        requestCode = 5000; // インバウンド用
                        targetArrow = (ACLArrow) v;
                        if (targetArrow.getPermission() != null) {
                            intent.putExtra("permissions", targetArrow.getPermission());
                            intent.putStringArrayListExtra("ipAddress", (ArrayList)targetArrow.getIpAddresses());
                            intent.putStringArrayListExtra("wildcard", (ArrayList)targetArrow.getWildcards());
                        }
                    } else if (!aclArrow.getIsJudge()) {
                        intent = new Intent(MainActivity.this, ACLActivity.class);
                        System.out.println("アウトバウンド");
                        requestCode = 6000; // アウトバウンド用
                        targetArrow = (ACLArrow) v;
                        if (targetArrow.getPermission() != null) {
                            intent.putExtra("permissions", targetArrow.getPermission());
                            intent.putStringArrayListExtra("ipAddress", (ArrayList)targetArrow.getIpAddresses());
                            intent.putStringArrayListExtra("wildcard", (ArrayList)targetArrow.getWildcards());
                        }
                    } else {
                        intent = null;
                        requestCode = 0;
                    }

                    startActivityForResult(intent, requestCode);
                    break;
            }
            return true;
        }
    }

}