package com.example.testapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class RoutingActivity extends AppCompatActivity {
    CheckBox checkBox_rip;
    CheckBox checkBox_ospf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routing_activity);

        checkBox_rip = findViewById(R.id.rip);
        checkBox_ospf = findViewById(R.id.ospf);

        Intent intent = getIntent();

        checkBox_rip.setChecked(intent.getBooleanExtra("rip", false));
        checkBox_ospf.setChecked(intent.getBooleanExtra("ospf", false));
    }

    public void onOKButtonClick(View view) {
        Intent intentSub = new Intent();

        intentSub.putExtra("rip", checkBox_rip.isChecked());
        intentSub.putExtra("ospf", checkBox_ospf.isChecked());

        setResult(RESULT_OK, intentSub);
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
