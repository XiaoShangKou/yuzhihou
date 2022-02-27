package com.xiaoshangkou.example.myapplication.MAIN;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xiaoshangkou.example.myapplication.BOOKSHELF.BookShelf;
import com.xiaoshangkou.example.myapplication.ABOUTME.Mine;
import com.xiaoshangkou.example.myapplication.R;
import com.xiaoshangkou.example.myapplication.SEARCH.Search;

/*
 * 用途:
 * 实现主界面的书架，搜索，我的三个图片按钮的fragment
 *
 * 实例变量:
 * bookshelfButton 进入书架fragment的按钮
 * searchButton 进入搜索fragment的按钮
 * mineButton 进入我的fragment的按钮
 *
 *
 * */
public class Main_Fragment {


    private final MainActivity mainActivity;
    private ImageButton bookshelfButton;
    private ImageButton searchButton;
    private ImageButton setButton;

    public Main_Fragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    /**
     * 实例化图片按钮,为ImageButton绑定点击事件监听器
     */
    public void Obtain(){
        bookshelfButton=mainActivity.findViewById(R.id.bookshelf_fragment);
        searchButton=mainActivity.findViewById(R.id.search_fragment);
        setButton=mainActivity.findViewById(R.id.set_fragment);


        bookshelfButton.setOnClickListener(l);
        searchButton.setOnClickListener(l);
        setButton.setOnClickListener(l);

        //设置默认的fragment
        setDefaultFragment();
    }

    /**
     * 设置默认fragment
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setDefaultFragment() {
        FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl, new Search());
        ft.commit();
    }


    /**
     * 点击事件监听器
     */
    View.OnClickListener l=new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
            Fragment f = null;
            if (view.getId() == R.id.bookshelf_fragment) {//如果点击书架
                f = new BookShelf();
                //创建动画对象
                Animation anim= AnimationUtils.loadAnimation(mainActivity,R.anim.scale_icon);
                bookshelfButton.startAnimation(anim);//开启动画
            } else if (view.getId() == R.id.search_fragment) {//如果点击搜索
                f = new Search();
                //创建动画对象
                Animation anim= AnimationUtils.loadAnimation(mainActivity,R.anim.scale_icon);
                searchButton.startAnimation(anim);//开启动画

            } else if (view.getId() == R.id.set_fragment) {//如果点击设置
                f = new Mine();
                //创建动画对象
                Animation anim= AnimationUtils.loadAnimation(mainActivity,R.anim.scale_icon);
                setButton.startAnimation(anim);//开启动画
            }
            if (f != null) {
                ft.replace(R.id.fl, f);
                //提交
                ft.commit();
            } else {
                Toast.makeText(mainActivity, "点击事件为null", Toast.LENGTH_SHORT).show();
            }
        }

    };

}
