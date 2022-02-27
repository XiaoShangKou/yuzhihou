package com.xiaoshangkou.example.myapplication.SEARCH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoshangkou.example.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NovelActivity extends AppCompatActivity {

    //获取用户点击的小说链接
    private String url;

    //存储小说信息
    private Map<String, String> novelFormation;

    private int scrollWeizhi;
    private int zhangjie;
    /**
     * 动态获取存储权限
     */
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> mPermissionList = new ArrayList<>();
    // private ImageView welcomeImg = null;
    private static final int PERMISSION_REQUEST = 1;
    // 检查权限
    private void checkPermission() {
        mPermissionList.clear();

        //判断哪些权限未授予
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        /**
         * 判断是否为空
         */
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了

        } else {//请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(NovelActivity.this, permissions, PERMISSION_REQUEST);
        }
    }

    /**
     * 响应授权
     * 这里不管用户是否拒绝，都进入首页，不再重复申请权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }





    private String readStringFromInputStream(FileInputStream fis) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toString();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);

        //获取intent
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        url=bundle.getString("url");
        //获取组件对象
        ImageView imageView=findViewById(R.id.novel_image);
        //小说名称
        TextView novelName=findViewById(R.id.nameNovle);
        //小说是否完结
        TextView novelState=findViewById(R.id.state);
        //小说人气状况
        TextView novelperson=findViewById(R.id.person);
        //小说作者
        TextView novelAuthor=findViewById(R.id.author);
        //目录
        TextView novelMulu=findViewById(R.id.mulu);
        //小说简介
        TextView novelIntroduction=findViewById(R.id.introduction);
        //开始阅读
        TextView beginRead=findViewById(R.id.beginread);
        //加入书架按钮
        Button button=findViewById(R.id.shujia);
        button.setText("加入书架");

        //用于处理消息的对象
        //安卓不支持在主线程中更新activity的UI组件,所以安卓提供了Handler对象
        Handler mHandler=new Handler(new Handler.Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean handleMessage(@NonNull Message message) {

                ArrayList<Object> Novel;

                if (message.what==0x110){
                    Novel = (ArrayList<Object>) message.obj;
                    novelFormation=(Map<String, String>)Novel.get(1);
                    //显示图片
                    //Glide设置图片圆角角度
                    RoundedCorners roundedCorners = new RoundedCorners(10);
                    //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
                    // RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(20, 20);
                    RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String fileName = novelFormation.get("name")+ novelFormation.get("author")+".txt";
                            //获取要写入的文件目录  storage/sdcard/Android/data/包名/files/xxx
                            File externalFilesDir = getExternalFilesDir(null);
                            String externalFilesDirPath = externalFilesDir.getPath();

                            Log.d("gatsby", "文件路径->" + externalFilesDirPath);

                            //添加书籍信息保存到文件
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
                                    Toast.makeText(NovelActivity.this, "书籍添加成功", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(NovelActivity.this, "书籍已经在书架中", Toast.LENGTH_SHORT).show();
                            }
                            //read();

                    }
                    });
                    String imageUrl=novelFormation.get("image");
                    Glide.with(NovelActivity.this)
                            .load(imageUrl)
                            .apply(options)
                            .placeholder(R.drawable.loadimg)
                            .error(R.drawable.exception)
                            .into(imageView);

                    //显示小说名称
                    String textName=novelFormation.get("name");
                    novelName.setText(textName);

                    //显示作者
                    String textAuthor=novelFormation.get("author");
                    novelAuthor.setText(textAuthor);

                    //显示是否完结
                    String state=novelFormation.get("isEnd");
                    novelState.setText(state);

                    //显示人气
                    String person=novelFormation.get("person");
                    novelperson.setText(person);


                    //显示小说简介
                    String introduction=novelFormation.get("introduction");
                    novelIntroduction.setText("简介: "+introduction);

                    //目录添加下划线
                    novelMulu.setText("目录");
                    novelMulu.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    novelMulu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent1=new Intent(NovelActivity.this, NovelMulu.class);
                            Bundle bundle1=new Bundle();
                            bundle1.putString("url",url);
                            intent1.putExtras(bundle1);
                            startActivity(intent1);
                        }
                    });

                    //开始阅读
                    beginRead.setText("开始阅读-->");
                    beginRead.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    beginRead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //读取保存的文件
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                Log.d("gatsby", "read view!");
                                String fileName = novelFormation.get("name") + novelFormation.get("author") + ".txt";
                                File externalFilesDir = getExternalFilesDir(null);

                                File file = new File(externalFilesDir, fileName);
                                if (file.exists()) {
                                    Log.d("gatsby", "file.exists!!!");
                                    FileInputStream fis = null;
                                    try {
                                        fis = new FileInputStream(file);
                                        //从输入流中读取内容
                                        String content = readStringFromInputStream(fis);
                                        //用逗号进行分割
                                        String[] sumContent = content.split(",");
                                        scrollWeizhi=Integer.parseInt(sumContent[3]);
                                        zhangjie=Integer.parseInt(sumContent[2]);
                                        System.out.println(content);
                                        Log.d("gatsby", "content->" + content);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (fis != null) {
                                            try {
                                                fis.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }else {
                                scrollWeizhi=0;
                                zhangjie=0;
                            }


                            Intent intent2=new Intent(NovelActivity.this,NovelContent.class);
                            Bundle bundle2=new Bundle();
                            bundle2.putString("url",url);
                            bundle2.putInt("zhangjie",zhangjie);

                            bundle2.putInt("scrollWeizhi",scrollWeizhi);
                            intent2.putExtras(bundle2);
                            Toast.makeText(NovelActivity.this, "开始阅读", Toast.LENGTH_SHORT).show();
                            startActivity(intent2);
                        }
                    });
                }
                return false;
            }
        });
        //System.out.println(url);
        Thread t2 = new Thread(new myThread2(mHandler,url));
        t2.setName("小说界面");
        //启动线程
        t2.start();
    }


}


