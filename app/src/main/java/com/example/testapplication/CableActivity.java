package com.example.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CableActivity extends AppCompatActivity {

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MainActivityから情報を受け取る
        Intent intentMain = getIntent();
        message = intentMain.getStringExtra("Type");
        if (message.equals("Router")) {
            setContentView(R.layout.cable_router_activity);
            TextView targetText = findViewById(R.id.hostname);
            TextView targetText2 = findViewById(R.id.hostname2);
            targetText.setText(intentMain.getStringExtra("TargetHostname1"));
            targetText2.setText(intentMain.getStringExtra("TargetHostname2"));
        } else if (message.equals("Switch")) {
            setContentView(R.layout.cable_switch_activity);
        } else if (message.equals("RouterAndSwitch")) {
            setContentView(R.layout.cable_routerandswitch_activity);
        }



    }

    // OKボタンをクリックするとアクティビティが終了する
    public void onOKButtonClick(View view) {
        Intent intentSub = new Intent();
        setResult(RESULT_OK, intentSub);
        finish();
    }
}
