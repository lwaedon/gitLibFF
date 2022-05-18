package com.mabeijianxi.smallvideo2;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class TestActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        editText = findViewById(R.id.et_input);
    }

    public void tvgo(View view) {
        String etContent = FileUtil.getEtContent(editText);


        if (FileUtil.isPhoneNumberValid(etContent)) {
            startActivity(new Intent(TestActivity.this, LanchActivity.class).putExtra("phone",etContent));
            finish();
        }
    }
}