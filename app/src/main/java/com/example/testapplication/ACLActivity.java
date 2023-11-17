package com.example.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ACLActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<Boolean> permissions;
    private List<String> aclLists_ipaddress;
    private List<String> aclLists_wildcard;
    private Spinner permission_spinner;
    private List<EditText> edit_IPAddress; // UI上のIPアドレス
    private List<EditText> edit_wildcard;
    private ListView listView;
    private BaseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acl_activity);

        Intent intentSub = getIntent();
        permissions = new ArrayList<>();
        Boolean[] p = (Boolean[]) intentSub.getSerializableExtra("permissions");
        if (p != null) {
            permissions = new ArrayList<>(Arrays.asList(p));
            aclLists_ipaddress = intentSub.getStringArrayListExtra("ipAddress");
            aclLists_wildcard = intentSub.getStringArrayListExtra("wildcard");
            System.out.println("実行");
        } else {
            aclLists_ipaddress = new ArrayList<>();
            aclLists_wildcard = new ArrayList<>();
        }

        adapter = new AclListViewAdapter(this, R.layout.acl_list, permissions, aclLists_ipaddress, aclLists_wildcard);
        listView = findViewById(R.id.acl_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        permission_spinner = (Spinner) findViewById(R.id.permission);
        edit_IPAddress = new ArrayList<>();
        edit_IPAddress.addAll(Arrays.asList(findViewById(R.id.ipaddress1), findViewById(R.id.ipaddress2), findViewById(R.id.ipaddress3), findViewById(R.id.ipaddress4)));
        edit_wildcard = new ArrayList<>();
        edit_wildcard.addAll(Arrays.asList(findViewById(R.id.wildcard1), findViewById(R.id.wildcard2), findViewById(R.id.wildcard3), findViewById(R.id.wildcard4)));
    }

    public void onOKButtonClick(View view) {
        Intent intentSub = new Intent();

        Boolean[] array_permissions = permissions.toArray(new Boolean[permissions.size()]);
        intentSub.putExtra("permission", array_permissions);
        intentSub.putStringArrayListExtra("ipaddress", (ArrayList)aclLists_ipaddress);
        intentSub.putStringArrayListExtra("wildcard", (ArrayList)aclLists_wildcard);

        setResult(RESULT_OK, intentSub);
        finish();
    }

    // 削除ボタンをクリックするとアクティビティが終了し, 矢印が削除される
    public void onDeleteButtonClick(View view) {
        Intent intentSub = new Intent();
        intentSub.putExtra("Remove", true);
        setResult(RESULT_OK, intentSub);
        finish();
    }

    /**
     * プラスボタンをクリックすると ACL が追加される
     */
    public void onPlusButtonClick(View view) {
        String ipAddress;
        String wildcard;

        System.out.println("ACL追加ボタンが押されました");
        if (((String)permission_spinner.getSelectedItem()).equals("permit")) {
            permissions.add(true);
        } else {
            permissions.add(false);
        }

        ipAddress = getAddress("ip");
        wildcard = getAddress("wild");

        if (!ipAddress.equals("") && !wildcard.equals("")) {
            aclLists_ipaddress.add(ipAddress);
            aclLists_wildcard.add(wildcard);

            adapter.notifyDataSetChanged(); // VLANリストの更新をアダプターに知らせる
        } else {
            System.out.println("正しくアドレスを入力してください");
        }
    }


    /**
     * ListView のアイテムをクリックしたときアラートダイアログを表示するメソッド
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        // AlertDialogのタイトル設定します
        alert.setTitle("削除");

        // AlertDialogのメッセージ設定
        alert.setMessage("選択したACLを削除しますか？");

        // AlertDialogのYesボタンのコールバックリスナーを登録
        alert.setPositiveButton("Yes", (dialog, which) -> {
            permissions.remove(position);
            aclLists_ipaddress.remove(position);
            aclLists_wildcard.remove(position);
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
     * IPアドレスまたはワイルドカードを文字列で返すメソッド
     * @return IPアドレス
     */
    public String getAddress(String target_address) {
        String address = "";
        List<EditText> editAddress;

        if (target_address.equals("ip")) editAddress = edit_IPAddress;
        else editAddress = edit_wildcard;

        for (int i = 0; i < 4; i++) {
            if (editAddress.get(i).getText().toString().equals("")) {
                address = "";

                return address;
            }

            if (i != 3) {
                address += (editAddress.get(i).getText().toString() + ".");
            } else {
                address += editAddress.get(i).getText().toString();
            }
        }

        return address;
    }
}
