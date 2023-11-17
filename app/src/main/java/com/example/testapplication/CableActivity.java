package com.example.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CableActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private TextView targetText; // to 〇〇の部分
    private TextView targetText2;
    private String type; // ケーブルで接続された機器の構成
    private List<EditText> edit_IPAddress; // UI上のIPアドレス
    private List<EditText> edit_subnet_mask;
    private ArrayList<Integer> vlanIds;
    private ArrayList<String> vlanNames;
    private InterfaceType interfaceType;
    private RadioGroup rg_shutdown;
    private RadioButton radioButton;
    private RadioGroup rg_SwitchPortType;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MainActivityから情報を受け取る
        Intent intentMain = getIntent();
        type = intentMain.getStringExtra("Type");
        if (type.equals("Two_Router")) {
            setContentView(R.layout.cable_2router_activity);
            targetText = findViewById(R.id.router_interface1).findViewById(R.id.hostname);
            targetText2 = findViewById(R.id.router_interface2).findViewById(R.id.hostname);
            targetText.setText(intentMain.getStringExtra("TargetHostname1"));
            targetText2.setText(intentMain.getStringExtra("TargetHostname2"));
        } else if (type.equals("Two_Switch")) {
            setContentView(R.layout.cable_2switch_activity);
        } else if (type.equals("RouterAndSwitch")) {
            setContentView(R.layout.cable_routerandswitch_activity);
        } else if (type.equals("Only_Router")) {
            setContentView(R.layout.cable_only_router_activity);
            targetText = findViewById(R.id.router_interface1).findViewById(R.id.hostname);
            targetText.setText(intentMain.getStringExtra("TargetHostname1"));

            if (!intentMain.getStringExtra("interfaceType").equals("")) {
                interfaceType = InterfaceType.valueOf(intentMain.getStringExtra("interfaceType"));
                setInterfaceType(interfaceType);
            }

            edit_IPAddress = new ArrayList<>();
            edit_IPAddress.add(findViewById(R.id.router_interface1).findViewById(R.id.ipaddress1));
            edit_IPAddress.add(findViewById(R.id.router_interface1).findViewById(R.id.ipaddress2));
            edit_IPAddress.add(findViewById(R.id.router_interface1).findViewById(R.id.ipaddress3));
            edit_IPAddress.add(findViewById(R.id.router_interface1).findViewById(R.id.ipaddress4));

            edit_subnet_mask = new ArrayList<>();
            edit_subnet_mask.add(findViewById(R.id.router_interface1).findViewById(R.id.subnet1));
            edit_subnet_mask.add(findViewById(R.id.router_interface1).findViewById(R.id.subnet2));
            edit_subnet_mask.add(findViewById(R.id.router_interface1).findViewById(R.id.subnet3));
            edit_subnet_mask.add(findViewById(R.id.router_interface1).findViewById(R.id.subnet4));

            setIPAddress(intentMain.getStringExtra("ipv4"));
            setSubnet(intentMain.getStringExtra("subnet_mask"));

            rg_shutdown = findViewById(R.id.router_interface1).findViewById(R.id.shutdown);
            if (intentMain.getBooleanExtra("shutdown", false)) {
                rg_shutdown.check(findViewById(R.id.router_interface1).findViewById(R.id.enable).getId());
                System.out.println("Trueだよ");
            } else {
                rg_shutdown.check(findViewById(R.id.router_interface1).findViewById(R.id.disable).getId());
            }

        } else if (type.equals("Only_Switch")) {
            setContentView(R.layout.cable_only_switch_activity);
            ListView listView = findViewById(R.id.vlan_list);

            if (!intentMain.getStringExtra("interfaceType").equals("")) {
                interfaceType = InterfaceType.valueOf(intentMain.getStringExtra("interfaceType"));
                setInterfaceType(interfaceType);
            }

            rg_SwitchPortType = findViewById(R.id.switch_interface1).findViewById(R.id.switchPortType);
            if (intentMain.getBooleanExtra("switchPortType", true)) {
                rg_SwitchPortType.check(findViewById(R.id.switch_interface1).findViewById(R.id.access).getId());
            } else {
                rg_SwitchPortType.check(findViewById(R.id.switch_interface1).findViewById(R.id.trunk).getId());
            }

            vlanIds = intentMain.getIntegerArrayListExtra("vlanIds");
            vlanNames = intentMain.getStringArrayListExtra("vlanNames");

            if (vlanIds == null) {
                vlanIds = new ArrayList<>();
                vlanNames = new ArrayList<>();
            }


            adapter = new VlanListViewAdapter(this, R.layout.vlan_list, vlanIds, vlanNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true); // ScrollView の中に ListView を入れるとスクロールができないが，このメソッドを入れると両方スクロールできる
                    return false;
                }
            });

            rg_shutdown = findViewById(R.id.switch_interface1).findViewById(R.id.shutdown);
            if (intentMain.getBooleanExtra("shutdown", false)) {
                rg_shutdown.check(findViewById(R.id.switch_interface1).findViewById(R.id.enable).getId());
            } else {
                rg_shutdown.check(findViewById(R.id.switch_interface1).findViewById(R.id.disable).getId());
            }
        }



    }

    // OKボタンをクリックするとアクティビティが終了する
    public void onOKButtonClick(View view) {  // ルータとスイッチで分ける（上を参考に）type で分ける
        Intent intentSub = new Intent();
        intentSub.putExtra("type", type);

        if (type.equals("Two_Router")) {
            // 未実装
        } else if (type.equals("Two_Switch")) {
            // 未実装
        } else if (type.equals("RouterAndSwitch")) {
            // 未実装
        } else if (type.equals("Only_Router")) {
            Spinner sp1 = (Spinner) findViewById(R.id.router_interface1).findViewById(R.id.interface_name);
            Spinner sp2 = (Spinner) findViewById(R.id.router_interface1).findViewById(R.id.port_name);
            String interface_name = (String) sp1.getSelectedItem();
            String port_name = (String) sp2.getSelectedItem();

            // 指定したインターフェースの取得
            interfaceType = getInterfaceType(interface_name, port_name);
            intentSub.putExtra("interfaceType", interfaceType.toString());

            intentSub.putExtra("ipv4", getIPAddress());
            intentSub.putExtra("subnet_mask", getSubnet());

            radioButton = findViewById(rg_shutdown.getCheckedRadioButtonId());
            if (radioButton != null) {
                if (radioButton.getText().toString().equals("enable")) {
                    intentSub.putExtra("shutdown", true);
                } else {
                    intentSub.putExtra("shutdown", false);
                }
            }

            RadioButton radioButton = findViewById(rg_shutdown.getCheckedRadioButtonId());
            if (radioButton != null) {
                if (radioButton.getText().toString().equals("enable")) {
                    intentSub.putExtra("shutdown", true);
                } else {
                    intentSub.putExtra("shutdown", false);
                }
            }



        } else if (type.equals("Only_Switch")) {
            Spinner sp1 = (Spinner) findViewById(R.id.switch_interface1).findViewById(R.id.interface_name);
            Spinner sp2 = (Spinner) findViewById(R.id.switch_interface1).findViewById(R.id.port_name);
            String interface_name = (String) sp1.getSelectedItem();
            String port_name = (String) sp2.getSelectedItem();

            // 指定したインターフェースの取得
            interfaceType = getInterfaceType(interface_name, port_name);
            intentSub.putExtra("interfaceType", interfaceType.toString());

            // アクセスかトランクかの取得
            RadioGroup rg = (RadioGroup) findViewById(R.id.switch_interface1).findViewById(R.id.switchPortType);
            boolean switchPortType = false;
            if (rg.getCheckedRadioButtonId() == R.id.access) {
                switchPortType = true;
            } else if (rg.getCheckedRadioButtonId() == R.id.trunk) {
                switchPortType = false;
            } else {
                System.out.println("ラジオボタンが選択されていません");
            }
            intentSub.putExtra("switchPortType", switchPortType);

            intentSub.putIntegerArrayListExtra("vlanIds", vlanIds);
            intentSub.putStringArrayListExtra("vlanNames", vlanNames);

        }

        setResult(RESULT_OK, intentSub);
        finish();
    }

    // ボタンをクリックすると VLAN が追加される
    public void onPlusButtonClick(View view) {
        int id = 0;
        String name = "";

        System.out.println("VLAN追加ボタンが押されました");
        if (type.equals("Only_Switch")) {
            EditText vlanIdText = findViewById(R.id.switch_interface1).findViewById(R.id.vlan_number);
            EditText vlanNameText = findViewById(R.id.switch_interface1).findViewById(R.id.vlan_name);
            id = Integer.parseInt(vlanIdText.getText().toString());
            name = vlanNameText.getText().toString();
            System.out.println("VLAN ID: " + id + " VLAN NAME: " + name + "が追加されました");
            vlanIds.add(id); // 直接 ArrayList を更新するとアダプターによりUIも更新される
            vlanNames.add(name);
        }

        adapter.notifyDataSetChanged(); // VLANリストの更新をアダプターに知らせる
    }

    @Override
    /**
     * ListView のアイテムをクリックしたときアラートダイアログを表示するメソッド
     */
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        // AlertDialogのタイトル設定します
        alert.setTitle("削除");

        // AlertDialogのメッセージ設定
        alert.setMessage("選択したVLANを削除しますか？");

        // AlertDialogのYesボタンのコールバックリスナーを登録
        alert.setPositiveButton("Yes", (dialog, which) -> {
            vlanIds.remove(position);
            vlanNames.remove(position);
            adapter.notifyDataSetChanged();
        });

        // AlertDialogのNoボタンのコールバックリスナーを登録
        alert.setNeutralButton("No", (dialog, which) -> {
        });

        // AlertDialogのキャンセルができるように設定
        alert.setCancelable(true);

        AlertDialog alertDialog = alert.create();
        // AlertDialogの表示
        alertDialog.show();
    }

    public InterfaceType getInterfaceType(String interface_name, String port_name) {
        InterfaceType return_interfaceType = null;

        switch (interface_name) {
            case "Serial":
                System.out.println("シリアルは現在対応していません");
                break;
            case "FastEthernet":
                switch (port_name) {
                    case "0/0":
                        return_interfaceType = InterfaceType.FastEthernet_00;
                        break;
                    case "0/1":
                        return_interfaceType = InterfaceType.FastEthernet_01;
                        break;
                    case "0/2":
                        return_interfaceType = InterfaceType.FastEthernet_02;
                        break;
                    case "0/3":
                        return_interfaceType = InterfaceType.FastEthernet_03;
                        break;
                    case "0/4":
                        return_interfaceType = InterfaceType.FastEthernet_04;
                        break;
                    case "0/5":
                        return_interfaceType = InterfaceType.FastEthernet_05;
                        break;
                    case "0/6":
                        return_interfaceType = InterfaceType.FastEthernet_06;
                        break;
                    case "0/7":
                        return_interfaceType = InterfaceType.FastEthernet_07;
                        break;
                }
                break;
            case "GigabitEthernet":
                switch (port_name) {
                    case "0/0":
                        return_interfaceType = InterfaceType.GigabitEthernet_00;
                        break;
                    case "0/1":
                        return_interfaceType = InterfaceType.GigabitEthernet_01;
                        break;
                    case "0/2":
                        return_interfaceType = InterfaceType.GigabitEthernet_02;
                        break;
                    case "0/3":
                        return_interfaceType = InterfaceType.GigabitEthernet_03;
                        break;
                    case "0/4":
                        return_interfaceType = InterfaceType.GigabitEthernet_04;
                        break;
                    case "0/5":
                        return_interfaceType = InterfaceType.GigabitEthernet_05;
                        break;
                    case "0/6":
                        return_interfaceType = InterfaceType.GigabitEthernet_06;
                        break;
                    case "0/7":
                        return_interfaceType = InterfaceType.GigabitEthernet_07;
                        break;
                    case "0/0/0":
                        return_interfaceType = InterfaceType.GigabitEthernet_000;
                        break;
                    case "0/0/1":
                        return_interfaceType = InterfaceType.GigabitEthernet_001;
                        break;
                    case "1/0/1":
                        return_interfaceType = InterfaceType.GigabitEthernet_101;
                        break;
                    case "1/0/2":
                        return_interfaceType = InterfaceType.GigabitEthernet_102;
                        break;
                    case "1/0/3":
                        return_interfaceType = InterfaceType.GigabitEthernet_103;
                        break;
                    case "1/0/4":
                        return_interfaceType = InterfaceType.GigabitEthernet_104;
                        break;
                    case "1/0/5":
                        return_interfaceType = InterfaceType.GigabitEthernet_105;
                        break;
                    case "1/0/6":
                        return_interfaceType = InterfaceType.GigabitEthernet_106;
                        break;
                    case "1/0/7":
                        return_interfaceType = InterfaceType.GigabitEthernet_107;
                        break;
                    case "1/0/8":
                        return_interfaceType = InterfaceType.GigabitEthernet_108;
                        break;
                    case "1/0/9":
                        return_interfaceType = InterfaceType.GigabitEthernet_109;
                        break;
                    case "1/0/10":
                        return_interfaceType = InterfaceType.GigabitEthernet_1010;
                        break;
                    case "1/0/11":
                        return_interfaceType = InterfaceType.GigabitEthernet_1011;
                        break;
                    case "1/0/12":
                        return_interfaceType = InterfaceType.GigabitEthernet_1012;
                        break;
                }
                break;
        }
        return return_interfaceType;
    }

    public void setInterfaceType(InterfaceType interfaceType) {
        Spinner sp1 = (Spinner) findViewById(R.id.router_interface1).findViewById(R.id.interface_name);
        Spinner sp2 = (Spinner) findViewById(R.id.router_interface1).findViewById(R.id.port_name);
        int i = 0; // GigabitEthernet の位置
        int j = 1; // FastEthernet の位置

        switch (interfaceType) {
            case FastEthernet_00:
                sp1.setSelection(j);
                sp2.setSelection(19);
                break;
            case FastEthernet_01:
                sp1.setSelection(j);
                sp2.setSelection(20);
                break;
            case FastEthernet_02:
                sp1.setSelection(j);
                sp2.setSelection(21);
                break;
            case FastEthernet_03:
                sp1.setSelection(j);
                sp2.setSelection(22);
                break;
            case FastEthernet_04:
                sp1.setSelection(j);
                sp2.setSelection(23);
                break;
            case FastEthernet_05:
                sp1.setSelection(j);
                sp2.setSelection(24);
                break;
            case FastEthernet_06:
                sp1.setSelection(j);
                sp2.setSelection(25);
                break;
            case FastEthernet_07:
                sp1.setSelection(j);
                sp2.setSelection(26);
                break;
            case GigabitEthernet_00:
                sp1.setSelection(i);
                sp2.setSelection(19);
                break;
            case GigabitEthernet_01:
                sp1.setSelection(i);
                sp2.setSelection(20);
                break;
            case GigabitEthernet_02:
                sp1.setSelection(i);
                sp2.setSelection(21);
                break;
            case GigabitEthernet_03:
                sp1.setSelection(i);
                sp2.setSelection(22);
                break;
            case GigabitEthernet_04:
                sp1.setSelection(i);
                sp2.setSelection(23);
                break;
            case GigabitEthernet_05:
                sp1.setSelection(i);
                sp2.setSelection(24);
                break;
            case GigabitEthernet_06:
                sp1.setSelection(i);
                sp2.setSelection(25);
                break;
            case GigabitEthernet_07:
                sp1.setSelection(i);
                sp2.setSelection(26);
                break;
            case GigabitEthernet_000:
                sp1.setSelection(i);
                sp2.setSelection(0);
                break;
            case GigabitEthernet_001:
                sp1.setSelection(i);
                sp2.setSelection(1);
                break;
            case GigabitEthernet_101:
                sp1.setSelection(i);
                sp2.setSelection(4);
                break;
            case GigabitEthernet_102:
                sp1.setSelection(i);
                sp2.setSelection(5);
                break;
            case GigabitEthernet_103:
                sp1.setSelection(i);
                sp2.setSelection(6);
                break;
            case GigabitEthernet_104:
                sp1.setSelection(i);
                sp2.setSelection(7);
                break;
            case GigabitEthernet_105:
                sp1.setSelection(i);
                sp2.setSelection(8);
                break;
            case GigabitEthernet_106:
                sp1.setSelection(i);
                sp2.setSelection(9);
                break;
            case GigabitEthernet_107:
                sp1.setSelection(i);
                sp2.setSelection(10);
                break;
            case GigabitEthernet_108:
                sp1.setSelection(i);
                sp2.setSelection(11);
                break;
            case GigabitEthernet_109:
                sp1.setSelection(i);
                sp2.setSelection(12);
                break;
            case GigabitEthernet_1010:
                sp1.setSelection(i);
                sp2.setSelection(13);
                break;
            case GigabitEthernet_1011:
                sp1.setSelection(i);
                sp2.setSelection(14);
                break;
            case GigabitEthernet_1012:
                sp1.setSelection(i);
                sp2.setSelection(15);
                break;
        }
    }

    /**
     * IPアドレスを UI上 にセットするメソッド
     */
    public void setIPAddress(String IPAddress) {
        System.out.println(IPAddress);
        if (!IPAddress.equals("")) {
            String[] IPAddresses = IPAddress.split("\\.");

            for (int i = 0; i < 4; i++) {
                edit_IPAddress.get(i).setText(IPAddresses[i]);
            }
        }
    }

    /**
     * サブネットマスクを UI上 にセットするメソッド
     */
    public void setSubnet(String subnet_mask) {
        System.out.println(subnet_mask);
        if (!subnet_mask.equals("")) {
            String[] subnet_masks = subnet_mask.split("\\.");

            for (int i = 0; i < 4; i++) {
                edit_subnet_mask.get(i).setText(subnet_masks[i]);
            }
        }
    }

    /**
     * IPアドレスを文字列で返すメソッド
     * @return IPアドレス
     */
    public String getIPAddress() {
        String ipAddress = "";

        for (int i = 0; i < 4; i++) {
            if (edit_IPAddress.get(i).getText().toString().equals("")) {
                ipAddress = "";

                return ipAddress;
            }

            if (i != 3) {
                ipAddress += (edit_IPAddress.get(i).getText().toString() + ".");
            } else {
                ipAddress += edit_IPAddress.get(i).getText().toString();
            }
        }

        return ipAddress;
    }

    /**
     * サブネットマスクを文字列で返すメソッド
     */
    public String getSubnet() {
        String subnet_mask = "";

        for (int i = 0; i < 4; i++) {
            if (edit_subnet_mask.get(i).getText().toString().equals("")) {
                subnet_mask = "";

                return subnet_mask;
            }

            if (i != 3) {
                subnet_mask += (edit_subnet_mask.get(i).getText().toString() + ".");
            } else {
                subnet_mask += edit_subnet_mask.get(i).getText().toString();
            }
        }

        return subnet_mask;
    }
}
