package com.xiaoshangkou.example.myapplication.SEARCH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoshangkou.example.myapplication.R;
import com.xiaoshangkou.example.myapplication.SEARCH.myThread2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class NovelMulu extends AppCompatActivity {
    private boolean zhenxun=true;//是否是正序
    private Handler handler;//更新组件的用处
    ArrayList<Object> Novel;//小说的信息集合
    //获取用户选择的小说链接
    String chooseUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_mulu);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        //获取用户选择的小说链接
        chooseUrl=bundle.getString("url");

        TextView textView=findViewById(R.id.muluload);
        ProgressBar progressBar=findViewById(R.id.loading);
        //获取目录的列表视图
        ListView listView=findViewById(R.id.listmulu);

        //System.out.println(chooseUrl);
        handler=new Handler(new Handler.Callback() {
            private MuluBaseAdapter muluBaseAdapter;
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what==0x110){
                    //设置加载不可见
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);

                    Novel=(ArrayList<Object>)message.obj;

                    //获取是正序还是倒序,2倒序 1正序
                    int test=message.arg1;

                    //小说的目录
                    ArrayList<Map<String,String>> NovelMuLu=(ArrayList<Map<String,String>>)Novel.get(0);

                    //System.out.println(NovelMuLu);
                    if (test==2) {
                        //顺序颠倒
                        Collections.reverse(NovelMuLu);
                    }if (test==1){
                        Collections.reverse(NovelMuLu);
                    }
                    muluBaseAdapter = new MuluBaseAdapter(NovelMulu.this, NovelMuLu);
                    listView.setAdapter(muluBaseAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent1=new Intent(NovelMulu.this,NovelContent.class);
                            Bundle bundle1=new Bundle();
                            bundle1.putString("url",chooseUrl);

                            //如果顺序不是反的，说明此时i就是目录
                            if (zhenxun) {
                                bundle1.putInt("zhangjie", i);
                            }
                            else if (!zhenxun){//如果顺序是倒叙，最后的就是第一项
                                bundle1.putInt("zhangjie", NovelMuLu.size()-1-i);
                            }
                            if (NovelContent.novelContent!=null){
                                NovelContent.novelContent.finish();
                            }
                            if (NovelContent.runnable!=null) {
                                NovelContent.handler3.removeCallbacks(NovelContent.runnable);
                            }
                            finish();
                            intent1.putExtras(bundle1);
                            startActivity(intent1);
                        }
                    });
                }
                return false;
            }
        });

        //爬取小说的信息和目录的线程
        //System.out.println(url);
        Thread t3 = new Thread(new myThread2(handler,chooseUrl));
        t3.setName("小说目录");
        //启动线程
        t3.start();
    }

    /**
     *
     * @param menu  actinbar上的序列按钮
     * @return 将自己定义的mian布局定义到menu中，并返回
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mian, menu);
        return true;
    }
    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shunxu:
                //实例化一个消息对象
                Message m=new Message();
                //发送结束消息0x110
                m.what=0x110;
                //重新将小说的信息发送,不然会空指针异常
                m.obj=Novel;
                //如果用户选择倒序发送信息2,并切换图片
                if (zhenxun) {
                    //发送小说信息集合
                    item.setIcon(getResources().getDrawable(R.drawable.daoxu));
                    m.arg1 = 2;
                    zhenxun=false;
                //如果选择正序发送信息1,并切换图片
                }else if (!zhenxun){
                    //发送小说信息集合
                    item.setIcon(getResources().getDrawable(R.drawable.zhenxu));
                    m.arg1 = 1;
                    zhenxun=true;
                }
                //定义完之后发送一个消息
                handler.sendMessage(m);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

//自定义适配器
class MuluBaseAdapter extends BaseAdapter {
    private ArrayList<Map<String,String>> NovelMuLu;
    private final Context mContext;
    public MuluBaseAdapter(Context mContext, ArrayList<Map<String,String>> NovelMuLu) {
        super();
        this.mContext = mContext;
        this.NovelMuLu = NovelMuLu;
    }
    @Override
    public int getCount() {
        return NovelMuLu.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //将自定义布局添加到listview中，并更新其中的内容
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View convertView = inflater.inflate(R.layout.mulu_view,null);
        TextView textView=convertView.findViewById(R.id.mulunovel);
        textView.setText(NovelMuLu.get(i).get("muLuName"));
        return convertView;
    }
}