package com.xiaoshangkou.example.myapplication.MAIN;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaoshangkou.example.myapplication.R;


public class MainActivity extends AppCompatActivity {
    private long exitTime;
    public static String readJilu;
    public static MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView=findViewById(R.id.imageBegin);

        //用于处理消息的对象
        //安卓不支持在主线程中更新activity的UI组件,所以安卓提供了Handler对象
        Handler mHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what==0x110){
                    //设置启动界面不显示
                    imageView.setVisibility(View.GONE);
                    //实现书架,搜索,我的之间的切换功能
                    Main_Fragment myFragment=new Main_Fragment(MainActivity.this);
                    myFragment.Obtain();
                }
                return false;
            }
        });
        //建立一个线程
        myThread2 basicInformation=new myThread2(mHandler);
        Thread t1 = new Thread(basicInformation);
        t1.setName("启动界面");
        //启动线程
        t1.start();



    }
    //物理事件
    //第一步重写onKeyDown方法来拦截用户单击后退按钮事件

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击的是后退按钮
        if (keyCode==KeyEvent.KEYCODE_BACK){
            exit();//自己写的退出方法
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //第二部创建退出方法exit()
    public  void exit(){
        if ((System.currentTimeMillis()-exitTime)>2000){
            Toast.makeText(MainActivity.this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
            exitTime=System.currentTimeMillis();
        }else {
            finish();
            System.exit(0);
        }
    }

}

/**
 * 设置开机画面2s
 */
class myThread2 implements Runnable{
    private Handler handler;

    public myThread2(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        //实例化一个消息对象
        Message m=new Message();
        //发送结束消息0x110
        m.what=0x110;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //定义完之后发送一个消息
        handler.sendMessage(m);
    }
}
