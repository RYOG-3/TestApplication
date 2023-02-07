package com.example.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RoutingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routing_activity);
    }

    public void onOKButtonClick(View view) {
        finish();
    }

    // 削除ボタンをクリックするとアクティビティが終了し, 円が削除される
    public void onDeleteButtonClick(View view) {
        Intent intentSub = new Intent();
        intentSub.putExtra("Remove", true);
        setResult(RESULT_OK, intentSub);
        System.out.println("削除ボタンが押されました");
        finish();
    }
}
