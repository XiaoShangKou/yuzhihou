package com.xiaoshangkou.example.myapplication.ABOUTME;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.xiaoshangkou.example.myapplication.R;

/**
 * 关于的fragment里面的
 * 列表视图中的关于作者选项的activity
 */
public class AboutAuthor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_author);
        //获取返回按钮
        ImageButton imageButton1=findViewById(R.id.return1);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭当前Activity
                finish();
            }
        });
    }
}