package com.example.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * ルータの論理構成図で詳細なパラメータを入力するクラス
 */
public class LogicalRouterActivity extends AppCompatActivity {

    private List<EditText> edit_Management_IPAddress;
    private EditText editText;
    private RadioGroup rg_lookup;
    private RadioGroup rg_encrypt;
    private RadioGroup rg_logging;

    @Override
    /**
     * アクティビティが呼び出された直後に呼ばれるメソッド
     * 必要なパラメーターはここで事前設定しておく
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logical_router_activity);

        edit_Management_IPAddress = new ArrayList<>();
        edit_Management_IPAddress.add(findViewById(R.id.management_ipAddress1));
        edit_Management_IPAddress.add(findViewById(R.id.management_ipAddress2));
        edit_Management_IPAddress.add(findViewById(R.id.management_ipAddress3));
        edit_Management_IPAddress.add(findViewById(R.id.management_ipAddress4));

        System.out.println(edit_Management_IPAddress.size());

        Intent intent = getIntent();

        setManagement_IPAddress(intent.getStringExtra("management_IPAddress"));

        editText = findViewById(R.id.hostname);
        editText.setText(intent.getStringExtra("hostname"));

        rg_lookup = findViewById(R.id.ip_domain_lookup); // ラジオボタンのデフォルト設定
        if (intent.getBooleanExtra("ip_domain_lookup", true)) {
            rg_lookup.check(R.id.enable1);
        } else {
            rg_lookup.check(R.id.disable1);
        }

        editText = findViewById(R.id.banner);
        editText.setText(intent.getStringExtra("banner"));

        rg_encrypt = findViewById(R.id.encryption);
        if (intent.getBooleanExtra("service_password", true)) {
            rg_encrypt.check(R.id.enable2);
        } else {
            rg_encrypt.check(R.id.disable2);
        }

        editText = findViewById(R.id.domain);
        editText.setText(intent.getStringExtra("ip_domain_name"));

        editText = findViewById(R.id.enable_secret);
        editText.setText(intent.getStringExtra("enable_secret"));

        editText = findViewById(R.id.console_password);
        editText.setText(intent.getStringExtra("password"));

        rg_logging = findViewById(R.id.logging);
        if (intent.getBooleanExtra("logging", true)) {
            rg_logging.check(R.id.enable3);
        } else {
            rg_logging.check(R.id.disable3);
        }



    }

    // OKボタンをクリックするとアクティビティが終了する
    public void onOKButtonClick(View view) {
        Intent intentSub = new Intent();

        String management_ipAddress = getManagement_IPAddress();
        intentSub.putExtra("management_IPAddress", management_ipAddress);

        EditText edit = findViewById(R.id.hostname);
        intentSub.putExtra("hostname", edit.getText().toString());
        System.out.println(edit.getText());

        RadioGroup rg = findViewById(R.id.ip_domain_lookup);
        RadioButton radioButton = findViewById(rg.getCheckedRadioButtonId());
        if (radioButton != null) {
            if (radioButton.getText().toString().equals("enable")) {
                intentSub.putExtra("ip_domain_lookup", true);
            } else {
                intentSub.putExtra("ip_domain_lookup", false);
            }
        }
        edit = findViewById(R.id.banner);
        intentSub.putExtra("banner", edit.getText().toString());
        rg = findViewById(R.id.encryption);
        radioButton = findViewById(rg.getCheckedRadioButtonId());
        if (radioButton != null) {
            if (radioButton.getText().toString().equals("enable")) {
                intentSub.putExtra("encryption", true);
            } else {
                intentSub.putExtra("encryption", false);
            }
        }
        edit = findViewById(R.id.domain);
        intentSub.putExtra("domain", edit.getText().toString());
        edit = findViewById(R.id.enable_secret);
        intentSub.putExtra("enable_secret", edit.getText().toString());
        edit = findViewById(R.id.console_password);
        intentSub.putExtra("console_password", edit.getText().toString());
        rg = findViewById(R.id.logging);
        radioButton = findViewById(rg.getCheckedRadioButtonId());
        if (radioButton != null) {
            if (radioButton.getText().toString().equals("enable")) {
                intentSub.putExtra("logging", true);
            } else {
                intentSub.putExtra("logging", false);
            }
        }

        setResult(RESULT_OK, intentSub);
        finish();
    }

    // 削除ボタンをクリックするとアクティビティが終了し, 機器が削除される
    public void onDeleteButtonClick(View view) {
        Intent intentSub = new Intent();
        intentSub.putExtra("Remove", true);
        setResult(RESULT_OK, intentSub);
        finish();
    }

    /**
     * 管理用IPアドレスを UI上 にセットするメソッド
     */
    public void setManagement_IPAddress(String management_IPAddress) {
        System.out.println(management_IPAddress);
        if (!management_IPAddress.equals("")) {
            String[] IPAddresses = management_IPAddress.split("\\.");

            for (int i = 0; i < 4; i++) {
                edit_Management_IPAddress.get(i).setText(IPAddresses[i]);
            }
        }
    }

    /**
     * 管理用IPアドレスを文字列で返すメソッド
     * @return 管理用IPアドレス
     */
    public String getManagement_IPAddress() {
        String management_IPAddress = "";

        for (int i = 0; i < 4; i++) {
            if (edit_Management_IPAddress.get(i).getText().toString().equals("")) {
                management_IPAddress = "";

                return management_IPAddress;
            }

            if (i != 3) {
                management_IPAddress += (edit_Management_IPAddress.get(i).getText().toString() + ".");
            } else {
                management_IPAddress += edit_Management_IPAddress.get(i).getText().toString();
            }
        }

        return management_IPAddress;
    }

}