package com.example.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LogicalSwitchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logical_switch_activity);

        Intent intent = getIntent();
        EditText edit = findViewById(R.id.hostname);
        edit.setText(intent.getStringExtra("hostname"));

    }

    public void onOKButtonClick(View view) {
        Intent intentSub = new Intent();
        EditText edit = findViewById(R.id.hostname);
        if (edit.getText() != null) {
            intentSub.putExtra("hostname", edit.getText().toString());
        }
        System.out.println(edit.getText());
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