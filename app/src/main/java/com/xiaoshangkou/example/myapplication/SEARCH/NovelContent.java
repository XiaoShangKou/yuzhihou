package com.xiaoshangkou.example.myapplication.SEARCH;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.xiaoshangkou.example.myapplication.BOOKSHELF.BookShelf;
import com.xiaoshangkou.example.myapplication.MAIN.MainActivity;
import com.xiaoshangkou.example.myapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NovelContent extends AppCompatActivity {
    //小说链接
    private String url;
    private Handler handler;
    private Handler handler2;
    public static Handler handler3;
    //小说信息
    private Map<String, String> novelFormation;

    //章节
    private int zhangjie;

    //获取总章节数
    private int sumZhang;
    //小说内容
    private List<String> content;
    //是否启动actionbar
    boolean startAcitonbar=true;

    //用来关闭此Activity
    public static NovelContent novelContent;

    //小说目录和内容
    private List<Object> SumInformation=new ArrayList<>();

    //小说目录
    private ArrayList<Map<String,String>> novelMulu;

    //滚动条当前位置
    private int scrollWeiZhi;

    //是否是上一章按钮
    private boolean shangyizhang;

    //计时器
    public static Runnable runnable;

    //记录
    private String jiLu;
    //物理事件
    //第一步重写onKeyDown方法来拦截用户单击后退按钮事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (runnable!=null) {
            //停止计时器
            handler3.removeCallbacks(runnable);
        }
        return super.onKeyDown(keyCode, event);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_content);
        novelContent=this;
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        url=bundle.getString("url");
        zhangjie=bundle.getInt("zhangjie");
        scrollWeiZhi=bundle.getInt("scrollWeizhi");



        Button addShujia=findViewById(R.id.add_shujia);
        TextView sumzongzhang=findViewById(R.id.zongzhang);
        TextView textView=findViewById(R.id.novel_text);
        LinearLayout linearLayout=findViewById(R.id.linear);
        ScrollView scrollView=findViewById(R.id.scrollView);
        Button nextZhang=findViewById(R.id.next_zhang);
        Button lastZhang=findViewById(R.id.last_zhang);
        Button novel_mulu=findViewById(R.id.mulu_novel);
        ProgressBar progressBar=findViewById(R.id.load_novel);
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);



        //添加书架按钮
        addShujia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //将用户添加到书架的书籍写入文件中
                String fileName =novelFormation.get("name")+ novelFormation.get("author")+".txt";
                //获取要写入的文件目录  storage/sdcard/Android/data/包名/files/xxx
                File externalFilesDir = getExternalFilesDir(null);
                String externalFilesDirPath = externalFilesDir.getPath();
                Log.d("gatsby", "文件路径->" + externalFilesDirPath);
                // 创建指定目录下的文件
                File file = new File(externalFilesDir, fileName);
                if (!file.exists()) {
                    //开始写文件
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        //获取要写出的文件内容
                        String content =novelFormation.get("name") + ","+ novelFormation.get("image")+","+0+","+0+","+url+","+novelFormation.get("author");
                        fos.write(content.getBytes("UTF-8"));
                        Toast.makeText(NovelContent.this, "书籍添加成功", Toast.LENGTH_LONG).show();

                        //添加文件的同时开启计时器
                        //用定时器存储当前位置，和章节
                         handler3 = new Handler();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                //要做的事情

                                //将记录的信息保存到文件中
                                jiLu=","+zhangjie+","+scrollWeiZhi;
                                String fileName = novelFormation.get("name") + novelFormation.get("author") + ".txt";
                                //获取要写入的文件目录  storage/sdcard/Android/data/包名/files/xxx
                                File externalFilesDir = getExternalFilesDir(null);
                                String externalFilesDirPath = externalFilesDir.getPath();
                                Log.d("gatsby", "文件路径->" + externalFilesDirPath);
                                // 创建指定目录下的文件
                                File file = new File(externalFilesDir, fileName);
                                //开始写文件
                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(file);
                                    //获取要写出的文件内容
                                    String content = novelFormation.get("name") + ","+ novelFormation.get("image")+jiLu+","+url+","+novelFormation.get("author");
                                    fos.write(content.getBytes("UTF-8"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (fos != null) {
                                        try {
                                            fos.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                Log.d("记录", jiLu);
                                handler3.postDelayed(this, 2000);
                            }
                        };
                        //启动计时器
                        handler3.postDelayed(runnable, 2000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else {
                    Toast.makeText(NovelContent.this, "书籍已经在书架中", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //滚动条当前位置
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    // 可以监听到ScrollView的滚动事件
                    //System.out.println(scrollView.getScrollY());
                    scrollWeiZhi=scrollView.getScrollY();
                }
                return false;
            }
        });

        //长触摸事件监听器
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //打开上下导航
                if (startAcitonbar) {
                    //创建动画对象
                    Animation anim= AnimationUtils.loadAnimation(NovelContent.this,R.anim.translate2);

                    linearLayout.startAnimation(anim);//开启动画
                    //下导航
                    linearLayout.setVisibility(View.VISIBLE);
                    //actionBar.show();
                    startAcitonbar=false;
                }else if (!startAcitonbar){//关闭上下导航
                    //linearLayout.setVisibility(View.INVISIBLE);
                    //actionBar.hide();
                    //创建动画对象
                    Animation anim= AnimationUtils.loadAnimation(NovelContent.this,R.anim.translate);

                    //下导航
                    linearLayout.startAnimation(anim);//开启动画
                    linearLayout.setVisibility(View.GONE);
                    startAcitonbar=true;
                }
                return false;
            }
        });

        //更新小说的内容
        handler=new Handler(new Handler.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what==0x110){
                    //加载条不可见
                    progressBar.setVisibility(View.GONE);
                    SumInformation = (List<Object>) message.obj;
                    novelMulu=(ArrayList<Map<String,String>>)SumInformation.get(0);
                    if (novelMulu.size()!=0){

                        content=(List<String>)SumInformation.get(1);
                        novelFormation=(Map<String, String>)SumInformation.get(2);
                        if (content!=null) {


                            String fileName = novelFormation.get("name") + novelFormation.get("author") + ".txt";
                            //获取要写入的文件目录  storage/sdcard/Android/data/包名/files/xxx
                            File externalFilesDir = getExternalFilesDir(null);
                            String externalFilesDirPath = externalFilesDir.getPath();

                            Log.d("gatsby", "文件路径->" + externalFilesDirPath);
                            // 创建指定目录下的文件
                            File file = new File(externalFilesDir, fileName);

                            //如果用户点击的小说信息，找得到此文件才进行计时器操作
                            if (file.exists()) {

                                //用定时器存储当前位置，和章节
                                handler3 = new Handler();
                                runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        //要做的事情

                                        //打开此文件将计时器记录的信息写入文件中
                                        jiLu=","+zhangjie+","+scrollWeiZhi;
                                        Log.d("记录", jiLu);
                                        String fileName = novelFormation.get("name") + novelFormation.get("author") + ".txt";
                                        //获取要写入的文件目录  storage/sdcard/Android/data/包名/files/xxx
                                        File externalFilesDir = getExternalFilesDir(null);
                                        String externalFilesDirPath = externalFilesDir.getPath();
                                        Log.d("gatsby", "文件路径->" + externalFilesDirPath);
                                        // 创建指定目录下的文件
                                        File file = new File(externalFilesDir, fileName);
                                        //开始写文件
                                        FileOutputStream fos = null;
                                        try {
                                            fos = new FileOutputStream(file);
                                            //获取要写出的文件内容
                                            String content = novelFormation.get("name") + ","+ novelFormation.get("image")+jiLu+","+url+","+novelFormation.get("author");
                                            fos.write(content.getBytes("UTF-8"));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            if (fos != null) {
                                                try {
                                                    fos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        handler3.postDelayed(this, 2000);
                                    }
                                };
                                //启动计时器
                                handler3.postDelayed(runnable, 2000);
                            }


                            //获取总章节数
                            sumZhang = message.arg1;
                            String join = String.join("\n\n   ", content);

                            //显示小说内容
                            textView.setTextSize(25);
                            textView.setText(join);

                            //显示章节数
                            sumzongzhang.setTextSize(10);
                            sumzongzhang.setText((zhangjie + 1) + "/" + sumZhang + "章");

                            //滚动条指定位置
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    //滚动到指定位置（滚动要跳过的控件的高度的距离）
                                    scrollView.scrollTo(0, scrollWeiZhi);
                                    //如果要平滑滚动，可以这样写
                                    //scrollView.smoothScrollTo(0, llNeedToSkip.getMeasuredHeight());
                                }
                            });
                        }else {
                            Toast.makeText(NovelContent.this, "抱歉加载失败，请重新尝试", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(NovelContent.this, "抱歉加载失败，请重新尝试", Toast.LENGTH_SHORT).show();
                        finish();
                    }


                }
                return false;
            }
        });


        //下一章节更新小说的内容
        handler2=new Handler(new Handler.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what==0x110){
                    //加载条不可见
                    progressBar.setVisibility(View.GONE);
                    content = (List<String>) message.obj;
                    if (content!=null) {
                        //获取总章节数
                        sumZhang = message.arg1;
                        String join = String.join("\n\n   ", content);

                        //显示小说内容
                        textView.setTextSize(25);
                        textView.setText(join);

                        //显示章节数
                        sumzongzhang.setTextSize(10);
                        sumzongzhang.setText((zhangjie + 1) + "/" + sumZhang + "章");

                        //滚动条指定位置
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                //滚动到指定位置（滚动要跳过的控件的高度的距离）
                                scrollView.scrollTo(0, 0);
                                //如果要平滑滚动，可以这样写
                                //scrollView.smoothScrollTo(0, llNeedToSkip.getMeasuredHeight());
                            }
                        });
                    }else {
                        Toast.makeText(NovelContent.this, "抱歉加载失败，请重新尝试", Toast.LENGTH_SHORT).show();
                        if (shangyizhang) {
                            zhangjie += 1;
                        }else {
                            zhangjie-=1;
                        }
                    }

                }
                return false;
            }
        });


        Thread t2 = new Thread(new ContentNovel(handler,url,zhangjie));
        t2.setName("小说内容");
        //启动线程
        t2.start();

        //下一章节的按钮
        nextZhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zhangjie<sumZhang-1){
                    //加载条可见
                    progressBar.setVisibility(View.VISIBLE);
                    //爬取下一章节，更新小说内容
                    shangyizhang=false;
                    zhangjie+=1;
                    Thread t3 = new Thread(new ContentNovel2(handler2,url,zhangjie,novelMulu));
                    t3.setName("小说内容");
                    //启动线程
                    t3.start();
                }else {
                    Toast.makeText(NovelContent.this, "当前已经是最后一章", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //上一章节的按钮
        lastZhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zhangjie>0){
                    //加载条可见
                    progressBar.setVisibility(View.VISIBLE);
                    //爬取上一章节，更新小说内容
                    zhangjie-=1;
                    shangyizhang=true;
                    Thread t3 = new Thread(new ContentNovel2(handler2,url,zhangjie,novelMulu));
                    t3.setName("小说内容");
                    //启动线程
                    t3.start();
                }else {
                    Toast.makeText(NovelContent.this, "当前已经是第一章", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //目录按钮
        novel_mulu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(NovelContent.this,NovelMulu.class);
                Bundle bundle1=new Bundle();
                bundle1.putString("url",url);
                intent1.putExtras(bundle1);
                startActivity(intent1);

            }
        });



    }
}

