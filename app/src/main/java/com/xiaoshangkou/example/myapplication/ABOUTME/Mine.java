package com.xiaoshangkou.example.myapplication.ABOUTME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiaoshangkou.example.myapplication.ABOUTME.AboutAuthor;
import com.xiaoshangkou.example.myapplication.ABOUTME.SoftwareNotice;
import com.xiaoshangkou.example.myapplication.ABOUTME.UpdateSuggestion;
import com.xiaoshangkou.example.myapplication.R;

/**
 * 设置关于的fragment，布局了一个列表视图,
 * 通过获取点击事件，来建立不同activity
 */
public class Mine extends Fragment {
    //创建Fragment的布局。
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view=inflater.inflate(R.layout.mine,null);
        //获取列表视图的对象
        ListView listView =view.findViewById(R.id.listview);

        String[] strings=new String[]{
                "关于作者","软件声明"
        };
        //适配器
        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,strings);
        listView.setAdapter(arrayAdapter);

        //为列表视图绑定监听事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity(), strings[i], Toast.LENGTH_SHORT).show();
                //如果用户点击了关于作者下面同理
                if ("关于作者".equals(strings[i])){
                    Intent intent1=new Intent(getActivity(), AboutAuthor.class);
                    //启动Activity
                    startActivity(intent1);
                }
                else if ("软件声明".equals(strings[i])){
                    Intent intent2=new Intent(getActivity(), SoftwareNotice.class);
                    //启动Activity
                    startActivity(intent2);
                }

            }
        });
        return view;
    }

}
