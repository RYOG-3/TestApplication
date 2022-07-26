package com.example.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * ルータの論理構成図で詳細なパラメータを入力するクラス
 */
public class LogicalRouterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logical_router_activity);

        Intent intent = getIntent();
        EditText edit = findViewById(R.id.hostname);
        edit.setText(intent.getStringExtra("hostname"));


    }

    // OKボタンをクリックするとアクティビティが終了する
    public void onOKButtonClick(View view) {
        Intent intentSub = new Intent();
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
}