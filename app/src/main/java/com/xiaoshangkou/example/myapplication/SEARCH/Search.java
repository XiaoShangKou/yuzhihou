package com.xiaoshangkou.example.myapplication.SEARCH;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiaoshangkou.example.myapplication.R;

/**
 *建立新的Activity作为搜索界面并将editTextview中的内容传入搜索界面中
 */
public class Search extends Fragment {
    Editable temp;
    private boolean diJi;

    //当返回这个Acitivity时重新刷新
    @Override
    public void onResume() {
        super.onResume();
        //只允许点击一次
        diJi=true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view=inflater.inflate(R.layout.search,null);
        ImageView imageView=view.findViewById(R.id.image_search);
        EditText etNoteContent=view.findViewById(R.id.name1);
        etNoteContent.setText("爱情");
        //设置键盘的换行为搜索
        etNoteContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        //获取搜索框的内容
        temp=etNoteContent.getText();//获取搜索框的内容

        //在该Editview获得焦点的时候将“回车”键改为“搜索”
        etNoteContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etNoteContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        //不然回车【搜索】会换行
        etNoteContent.setSingleLine(true);

        //为搜索文本的搜索图片按钮建立点击事件监听器
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView();
            }
        });


        //当点击键盘的的搜索按钮时
        etNoteContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((i == EditorInfo.IME_ACTION_UNSPECIFIED || i == EditorInfo.IME_ACTION_SEARCH) && keyEvent != null)
                {
                    searchView();
                    return true;
                }
                return false;
            }
        });
        return view;

    }

    public void searchView(){
        //计算用户输入的内容的字节数
        byte[]  buff = temp.toString().getBytes();
        int length = buff.length;

        if (TextUtils.isEmpty(temp)){
            Toast.makeText(getActivity(), "输入的值为空", Toast.LENGTH_SHORT).show();
        }else if (length<=4){
            Toast.makeText(getActivity(), "输入的字节数至少要大于4个字节", Toast.LENGTH_SHORT).show();
        }
        else {
            if (diJi) {
                diJi=false;
                Intent intent = new Intent(getActivity(), SearchResults.class);
                //通过Bundle保存输入的信息
                Bundle bundle = new Bundle();
                bundle.putCharSequence("temp", String.valueOf(temp));
                intent.putExtras(bundle);
                //启动Activity
                startActivity(intent);
            }
        }
    }

}
