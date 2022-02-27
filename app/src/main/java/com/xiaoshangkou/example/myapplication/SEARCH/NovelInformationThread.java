package com.xiaoshangkou.example.myapplication.SEARCH;

import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 建立一个线程调用 SearchName 的 search 方法
 * 获取小说的基本信息
 */
public class NovelInformationThread implements Runnable{

    private SumNovel sum;
    private Handler handler;
    //页数
    private int page;

    //用户输入的内容
    private String temp;

    //存放所有的小说信息
    public NovelInformationThread(Handler handler,String temp,SumNovel sum,int page) {
        this.temp = temp;
        this.sum=sum;
        this.page=page;
        this.handler=handler;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {

        BiQuGe searchName=new BiQuGe(temp);
        //获取小说信息集合
        sum.setSumNovel(searchName.search(page));
        sum.setPage(searchName.getSumPage());
        sum.setIsfind(searchName.isIsfind());
        //实例化一个消息对象
        Message m=new Message();

        //发送结束消息0x110
        m.what=0x110;

        //发送小说信息集合
        m.obj=sum;

        //定义完之后发送一个消息
        handler.sendMessage(m);
    }

}
class NovelInformationThread2 implements Runnable{

    private SumNovel sum;
    //页数
    private int page;

    //用户输入的内容
    private String temp;

    //存放所有的小说信息
    public NovelInformationThread2(String temp,SumNovel sum,int page) {
        this.temp = temp;
        this.sum=sum;
        this.page=page;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {

        BiQuGe searchName=new BiQuGe(temp);
        //获取小说信息集合
        sum.setSumNovel(searchName.search(page));
        sum.setPage(searchName.getSumPage());
        sum.setIsfind(searchName.isIsfind());
    }

}
//存储爬虫数据的类
class SumNovel{
    //存储小说的信息
    private List<Map<String, String>> sumNovel=new ArrayList<>();
    //页数
    private int page=1;
    //是否找到
    private boolean isfind=true;

    public SumNovel() {

    }

    //set and get
    public List<Map<String, String>> getSumNovel() {
        return sumNovel;
    }
    public void setSumNovel(ArrayList<Map<String, String>> sumNovel1) {
        this.sumNovel.addAll(sumNovel1);
        System.out.println(sumNovel.size());
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isIsfind() {
        return isfind;
    }

    public void setIsfind(boolean isfind) {
        this.isfind = isfind;
    }
}

/**
 * 实现爬取小说简介，封面的线程
 */
class myThread2 implements Runnable{
    private Handler handler;
    private String url;
    //小说
    private ArrayList<Object> Novel=new ArrayList<>();
    //获取存放用户点击的小说信息集合 novelFormation
    private Map<String,String> novelFormation;

    public myThread2(Handler handler, String url) {
        this.handler = handler;
        this.url = url;
    }

    @Override
    public void run() {
        SearchFormation searchFormation=new SearchFormation();
        searchFormation.searchNovel(url);
        this.novelFormation=searchFormation.getNovelFormation();
        this.Novel.add(searchFormation.MuluNouvel(url));
        this.Novel.add(novelFormation);
        //实例化一个消息对象
        Message m=new Message();

        //发送结束消息0x110
        m.what=0x110;

        //发送小说信息集合
        m.obj=Novel;
        //定义完之后发送一个消息
        handler.sendMessage(m);
    }
}
/**
 * 获取小说内容的线程
 */
class ContentNovel implements Runnable{
    private Handler handler;
    private String url;
    private int zhangjie;
    private List<Object> SumInformation=new ArrayList<>();
    public ContentNovel(Handler handler, String url,int zhangjie) {
        this.handler = handler;
        this.url = url;
        this.zhangjie=zhangjie;
    }

    @Override
    public void run() {
        SearchFormation searchFormation=new SearchFormation();
        //小说目录
        //实例化一个消息对象
        Message m=new Message();

        //发送结束消息0x110
        m.what=0x110;
        //发送小说内容
       // System.out.println(url);
        ArrayList<Map<String,String>> novelMulu=searchFormation.MuluNouvel(url);
        m.arg1=novelMulu.size();
        SumInformation.add(novelMulu);

        try {
            if (novelMulu.size()!=0) {
                SumInformation.add(searchFormation.contentNovel(novelMulu, zhangjie, url));
                SumInformation.add(searchFormation.searchNovel(url));
            }else {
                SumInformation.add(0);
                SumInformation.add(0);
            }
        } catch (NovelException e) {
            m.arg2=1;
            e.printStackTrace();

        }finally {
            //arg2为0时小说加载成功 1小说加载失败
            m.obj=SumInformation;
            //定义完之后发送一个消息
            handler.sendMessage(m);
        }

    }
}
/**
 * 获取小说内容的线程
 */
class ContentNovel2 implements Runnable{
    private Handler handler;
    private String url;
    private int zhangjie;
    private ArrayList<Map<String,String>> novelMulu;
    private List<Object> SumInformation=new ArrayList<>();
    public ContentNovel2(Handler handler, String url,int zhangjie,ArrayList<Map<String,String>> novelMulu) {
        this.handler = handler;
        this.url = url;
        this.zhangjie=zhangjie;
        this.novelMulu=novelMulu;
    }

    @Override
    public void run() {
        SearchFormation searchFormation=new SearchFormation();
        //小说目录
        //实例化一个消息对象
        Message m=new Message();

        //发送结束消息0x110
        m.what=0x110;
        //发送小说内容
        // System.out.println(url);
        m.arg1=novelMulu.size();
        try {
            m.obj=searchFormation.contentNovel(novelMulu,zhangjie,url);
        } catch (NovelException e) {
            m.arg2=1;
            e.printStackTrace();

        }finally {
            //定义完之后发送一个消息
            handler.sendMessage(m);
        }


    }
}