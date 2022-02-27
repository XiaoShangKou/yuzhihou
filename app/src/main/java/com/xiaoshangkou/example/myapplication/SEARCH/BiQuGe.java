package com.xiaoshangkou.example.myapplication.SEARCH;

import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取笔趣阁的小说的基本信息
 */
public class BiQuGe {
    /*属性*/
    //用户输入的内容
    private String temp;
    //页数
    private int sumPage;

    //书籍是否找到
    private boolean isfind;
    /*构造方法*/
    public BiQuGe(String temp) {
        this.temp = temp;
    }

    /*get*/
    //获取搜索的页数
    public int getSumPage() {
        return sumPage;
    }

    //是否找到书籍
    public boolean isIsfind() {
        return isfind;
    }


    /**
     *
     * @param pageNovel 爬取第几页的小说
     * @return 存储爬取的每本小说的基本信息
     */
    public synchronized ArrayList<Map<String,String>> search(int pageNovel) {
        ArrayList<Map<String,String>> sumNovel=new ArrayList<>();
        String text=convert(temp);
        String url="https://www.bbiquge.net/modules/article/search.php?searchkey="+text+"&submit=%CB%D1%CB%F7&page="+pageNovel;

        try {
            Document doc = Jsoup.connect(url)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .timeout(4000)
                    .cookie("auth", "token")
                    .post();

            //获取所有的带有class=odd属性的td标签
            Elements links=doc.select("td[class=odd]");
            //System.out.println(links);
            //获取所有的带有class=even属性的td标签
            Elements links3=doc.select("td[class=even]");

            //如果找不到td标签(搜索结果只有一本小说)
            if (links.size()==0){

                //获取本链接判断是找不到此书籍还是此小说的界面
                //如果是此小说的界面重新爬取获取小说的作者名称和小说界面的链接
                //用是否以https://www.bbiquge.net/book开头来判断
                String url2=doc.baseUri();
                if (url2.startsWith("https://www.bbiquge.net/book")) {
                    Map<String,String> novelFormation2=new HashMap<>();
                    SearchFormation searchFormation=new SearchFormation();
                    searchFormation.searchNovel(url2);
                    Map<String,String> novelFormation=searchFormation.getNovelFormation();
                    novelFormation2.put("author","作者:"+novelFormation.get("author"));
                    novelFormation2.put("name", novelFormation.get("name"));
                    novelFormation2.put("time", "");
                    novelFormation2.put("URL", url2);
                    novelFormation2.put("latestChapter",novelFormation.get("update").split("2")[0].substring(0, novelFormation.get("update").split("2")[0].length()-1));//最新章节
                    novelFormation2.put("wordCount","");//字数
                    novelFormation2.put("state",novelFormation.get("isEnd"));//更新状态
                    sumNovel.add(novelFormation2);
                    sumPage=1;
                    isfind=true;
                }else {
                    isfind=false;
                }
            }
            else {
                //爬取总页数
                Elements page=doc.select("em");
                isfind=true;
                //将总页数转换int类型赋给sumPage
                sumPage=Integer.parseInt(page.text().split("/")[1]);
                //信息是三个td标签为一个小说的作者,时间,小说名称所以为 i += 3)
                for (int i = 0; i < links.size(); i += 3) {
                    Map<String, String> novel = new HashMap<>();
                    novel.put("author", links.get(i + 1).text());
                    novel.put("name", links.get(i).text());
                    novel.put("time", "更新时间:"+links.get(i+2).text());
                    novel.put("URL", links.get(i).getElementsByTag("a").attr("href"));
                    novel.put("latestChapter","最新章节:"+links3.get(i).text());//最新章节
                    novel.put("wordCount","字数:"+links3.get(i+1).text());//字数
                    novel.put("state",links3.get(i+2).text());//更新状态
                    sumNovel.add(novel);

                }

                //打印集合
                /*for (Map<String, String> sum : sumNovel
                ) {
                    System.out.println(sum);
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sumNovel;
    }

    /**
     *
     * @param text1 要转gbk编码的文本
     * @return 转换成gbk的编码 String类型
     */
    public static String convert(String text1){
        String text2=null;
        try {
            text2=java.net.URLEncoder.encode(text1, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text2;
    }
}

/**
 * 爬取小说简介，封面等
 */
class SearchFormation{
    //存放用户点击的小说信息
    private Map<String,String> novelFormation=new HashMap<>();

    //小说目录
    private ArrayList<Map<String,String>> novelMulu=new ArrayList<>();
    //getter
    public Map<String, String> getNovelFormation() {
        return novelFormation;
    }

    //小说目录
    public ArrayList<Map<String,String>> MuluNouvel(String url){
        Document  doc = null;
        try {
            doc = Jsoup.connect(url)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(5000)
                    .post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc!=null){
            Elements links2=doc.select("dd");

            //小说目录
            for (int i=0;i<links2.size();i++) {
                //如果dd标签的内容不为空才提取添加
                if (!"".equals(links2.get(i).select("a").text())) {
                    Map<String, String> novelZhangJie = new HashMap<>();
                    novelZhangJie.put("muLuName", links2.get(i).select("a").text());
                    novelZhangJie.put("url", links2.get(i).select("a").attr("href"));
                    novelMulu.add(novelZhangJie);
                }
            }
        }else {
            Log.d("error","出现错误");
        }

        return novelMulu;

    }
    //爬取用户点击的小说信息
    public Map<String, String>  searchNovel(String url){
        try {
            Document  doc = Jsoup.connect(url)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(5000)
                    .post();

            Elements links=doc.select("img");


            //System.out.println(novelMulu);

            //小说封面
            String image=links.get(1).attr("src");
            //System.out.println(image);
            novelFormation.put("image",image);

            //小说名称
            String name=links.get(1).attr("title");
            //System.out.println(name);
            novelFormation.put("name",name);

            //小说作者
            String author=doc.select("small").text().split("/")[1];
            //System.out.println(author);
            novelFormation.put("author",author);

            //小说简介
            String introduction=doc.select("div[id=intro]").text();
            //System.out.println(introduction);
            novelFormation.put("introduction",introduction);

            //更新状况
            String update=doc.select("div[class=update]").text();
            //System.out.println(update);
            novelFormation.put("update",update);

            //是否完结
            String isEnd=doc.select("span[class]").get(1).text();
            //System.out.println(isEnd);
            novelFormation.put("isEnd",isEnd);

            //人气
            String person=doc.select("span[class]").get(0).text();
            //System.out.println(person);
            novelFormation.put("person",person);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return novelFormation;
    }
    public List<String> contentNovel(ArrayList<Map<String,String>> novelMulu,int zhangjie,String url) throws NovelException {
        String contentUrl=url+novelMulu.get(zhangjie).get("url");
        List<String> novelContent=new ArrayList<>();
        Document  doc = null;
        try {
            doc = Jsoup.connect(contentUrl)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .post();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc!=null){
            Elements links2=doc.select("div[id=content]");
            Elements links3=doc.select("h1");
            List<TextNode> list=links2.textNodes();
            novelContent.add(links3.text());
            for (int i=2;i<list.size();i++){

                novelContent.add(list.get(i).text());

            }
        }else {
            throw new NovelException("抱歉小说加载失败请重新尝试");
        }
        return novelContent;
    }
}


/*
 //获取intent


*/



