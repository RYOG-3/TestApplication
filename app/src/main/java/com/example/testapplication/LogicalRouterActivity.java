package com.example.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * ルータの論理構成図で詳細なパラメータを入力するクラス
 */
public class LogicalRouterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logical_router_activity);


    }

    // OKボタンをクリックするとアクティビティが終了する
    public void onOKButtonClick(View view) {
        Intent intentSub = new Intent();
        setResult(RESULT_OK, intentSub);
        finish();
    }
}