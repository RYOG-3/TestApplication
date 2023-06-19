package com.example.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CableActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String type;
    private ArrayList<Integer> vlanIds;
    private ArrayList<String> vlanNames;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MainActivityから情報を受け取る
        Intent intentMain = getIntent();
        type = intentMain.getStringExtra("Type");
        if (type.equals("Two_Router")) {
            setContentView(R.layout.cable_2router_activity);
            TextView targetText = findViewById(R.id.router_interface1).findViewById(R.id.hostname);
            TextView targetText2 = findViewById(R.id.router_interface2).findViewById(R.id.hostname);
            targetText.setText(intentMain.getStringExtra("TargetHostname1"));
            targetText2.setText(intentMain.getStringExtra("TargetHostname2"));
        } else if (type.equals("Two_Switch")) {
            setContentView(R.layout.cable_2switch_activity);
        } else if (type.equals("RouterAndSwitch")) {
            setContentView(R.layout.cable_routerandswitch_activity);
        } else if (type.equals("Only_Router")) {
            setContentView(R.layout.cable_only_router_activity);
        } else if (type.equals("Only_Switch")) {
            setContentView(R.layout.cable_only_switch_activity);
            ListView listView = findViewById(R.id.vlan_list);
            vlanIds = new ArrayList<>();
            vlanNames = new ArrayList<>();
            adapter = new ListViewAdapter(this, R.layout.vlan_list, vlanIds, vlanNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.getParent().requestDisallowInterceptTouchEvent(true); // ScrollView の中に ListView を入れるとスクロールができないが，このメソッドを入れると両方スクロールできる
                    return false;
                }
            });

        }



    }

    // OKボタンをクリックするとアクティビティが終了する
    public void onOKButtonClick(View view) {
        Intent intentSub = new Intent();
        intentSub.putExtra("type", type);
        intentSub.putIntegerArrayListExtra("vlanIds", vlanIds);
        intentSub.putStringArrayListExtra("vlanNames", vlanNames);
        setResult(RESULT_OK, intentSub);
        finish();
    }

    // ボタンをクリックすると VLAN の追加がされる
    public void onPlusButtonClick(View view) {
        int id = 0;
        String name = "";

        System.out.println("VLAN追加ボタンが押されました");
        if (type.equals("Only_Switch")) {
            EditText vlanIdText = findViewById(R.id.switch_interface1).findViewById(R.id.vlan_number);
            EditText vlanNameText = findViewById(R.id.switch_interface1).findViewById(R.id.vlan_name);
            id = Integer.parseInt(vlanIdText.getText().toString());
            name = vlanNameText.getText().toString();
            System.out.println("VLAN ID: " + id + " VLAN NAME: " + name);
            vlanIds.add(id); // 直接 ArrayList を更新する
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
}
