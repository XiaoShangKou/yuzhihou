package com.xiaoshangkou.example.myapplication.BOOKSHELF;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoshangkou.example.myapplication.MAIN.MainActivity;
import com.xiaoshangkou.example.myapplication.R;
import com.xiaoshangkou.example.myapplication.SEARCH.NovelActivity;
import com.xiaoshangkou.example.myapplication.SEARCH.NovelContent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class BookShelf extends Fragment {
    //要删除的小说名称
    private String name;
    ///要删除的小说的文件路径
    private  File fileNovel;
    private String author;
    //是否第一次加载
    private boolean isFirstLoading = true;

    File[] files;

    /**
     * 从其他activity返回时，会调用这个方法，可以在这里进行fragmen的刷新
     */
    @Override
    public void onResume() {
        super.onResume();

        if (!isFirstLoading) {
            //如果不是第一次加载，刷新数据
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment f = new BookShelf();
            ft.replace(R.id.fl, f);
            //提交
            ft.commit();

        }

        isFirstLoading = false;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add("删除"+name);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //System.out.println(namNovel);
        //获取存储文件的路径
        File externalFilesDir =getContext().getExternalFilesDir(null);
        String externalFilesDirPath =externalFilesDir.getPath() ;
        fileNovel=new File(externalFilesDirPath,name+author+".txt");
        //System.out.println(name+author+".txt");
        fileNovel.delete();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment f = new BookShelf();
        ft.replace(R.id.fl, f);
        //提交
        ft.commit();
        return true;
    }
    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view=inflater.inflate(R.layout.bookshelf,null);

        isFirstLoading = true;

        //显示图片
        //Glide设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(10);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        // RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(20, 20);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);

        AutoNextLineLinearlayout mylinear=view.findViewById(R.id.mylinear);

        //获取存储文件的路径
        File externalFilesDir =view.getContext().getExternalFilesDir(null);
        String externalFilesDirPath =externalFilesDir.getPath() ;
        //读取此文件夹的所有文件内容
        File f=new File(externalFilesDirPath);
        files=f.listFiles();
       // Log.d("length",String.valueOf(files.length));
        if (files.length!=0) {
            for (int i = 0; i < files.length; i++) {
                TextView textView = new TextView(getActivity());
                RelativeLayout relativeLayout = new RelativeLayout(getActivity());
                ImageView imageView = new ImageView(getActivity());
                //RelativeLayout.LayoutParams image_Params = new RelativeLayout.LayoutParams();

                //设置imageview的大小和布局
                ViewGroup.MarginLayoutParams mp = new ViewGroup.MarginLayoutParams(300, 320);  //item的宽高
                mp.setMargins(0, 0, 0, 0);//分别是margin_top那四个属性
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mp);
                lp.addRule(RelativeLayout.ALIGN_RIGHT);
                imageView.setLayoutParams(lp);

                //设置id为2
                imageView.setId(2);

                //设置textview的大小和布局
                ViewGroup.MarginLayoutParams mp2 = new ViewGroup.MarginLayoutParams(250, 50);  //item的宽高
                mp2.setMargins(40, 0, 0, 0);//分别是margin_top那四个属性
                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mp2);
                //lp2.addRule(RelativeLayout.ALIGN_RIGHT);
                //设置成在imageview的下方
                lp2.addRule(RelativeLayout.BELOW, 2);

                textView.setLayoutParams(lp2);

                File file = new File(externalFilesDir, files[i].getName());
                if (file.exists()) {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        //从输入流中读取内容
                        String content = readStringFromInputStream(fis);
                       //System.out.println(content);
                        //用逗号进行分割
                        String[] sumContent = content.split(",");

                        //日志
                        Log.d("gatsby", "content->" + content);

                        //设置文本，字体大小

                        textView.setText(sumContent[0]);
                        textView.setTextSize(12);

                        //图片链接
                        String imageUrl = sumContent[1];
                        //显示图片
                        Glide.with(BookShelf.this)
                                .load(imageUrl)
                                .apply(options)
                                //.override(200, 180) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                                .placeholder(R.drawable.loadimg)
                                .error(R.drawable.exception)
                                .into(imageView);

                        //将文本，图片组件添加到relativelayout布局管理器中
                        relativeLayout.addView(imageView);
                        relativeLayout.addView(textView);

                        //再将relativelayout添加到自定义的布局管理中
                        mylinear.addView(relativeLayout);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent2=new Intent(getActivity(), NovelContent.class);
                                Bundle bundle2=new Bundle();
                                bundle2.putString("url",sumContent[4]);
                                bundle2.putInt("zhangjie",Integer.parseInt(sumContent[2]));

                                bundle2.putInt("scrollWeizhi",Integer.parseInt(sumContent[3]));
                                intent2.putExtras(bundle2);
                                Toast.makeText(getActivity(), "开始阅读", Toast.LENGTH_SHORT).show();
                                startActivity(intent2);
                            }
                        });

                        //长按弹出删除选项
                        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                registerForContextMenu(view);//注册菜单
                                //要删除的小说名称
                                name=sumContent[0];
                                author=sumContent[5];
                                getActivity().openContextMenu(view);//打开菜单
                                return true;
                            }
                        });
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
                } else {
                    Toast.makeText(getActivity(), "该书籍不存在", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            RelativeLayout relativeLayout = new RelativeLayout(getActivity());
            ImageView imageView2 = new ImageView(getActivity());
            //RelativeLayout.LayoutParams image_Params = new RelativeLayout.LayoutParams();

            //设置imageview的大小和布局
            ViewGroup.MarginLayoutParams mp2 = new ViewGroup.MarginLayoutParams(400, 420);  //item的宽高
            mp2.setMargins(5, 0, 0, 0);//分别是margin_top那四个属性
            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(mp2);
            lp3.addRule(RelativeLayout.ALIGN_LEFT);
            imageView2.setLayoutParams(lp3);

            //设置id为3
            imageView2.setId(3);
            //显示图片
            Glide.with(BookShelf.this)
                    .load(R.drawable.notfind)
                    .apply(options)
                    //.override(200, 180) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                    .placeholder(R.drawable.loadimg)
                    .error(R.drawable.exception)
                    .into(imageView2);
            relativeLayout.addView(imageView2);


            TextView textView=new TextView(getActivity());
            textView.setText(" 您还没有添加书籍哦~");
            textView.setTextSize(15);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mp2);
            lp.addRule(RelativeLayout.ALIGN_RIGHT);
            textView.setLayoutParams(lp);
            relativeLayout.addView(textView);
            //再将relativelayout添加到自定义的布局管理中
            mylinear.addView(relativeLayout);

        }
        return view;
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


}
