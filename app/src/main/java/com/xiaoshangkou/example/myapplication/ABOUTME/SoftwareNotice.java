package com.xiaoshangkou.example.myapplication.ABOUTME;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.xiaoshangkou.example.myapplication.R;

/**
 * 关于的fragment里面的
 * 列表视图中的软件声明选项的activity
 */
public class SoftwareNotice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_notice);
        //获取返回按钮
        ImageButton imageButton2=findViewById(R.id.return2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭当前Activity
                finish();
            }
        });
    }
}