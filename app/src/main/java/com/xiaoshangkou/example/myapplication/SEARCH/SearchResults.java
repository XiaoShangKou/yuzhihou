package com.xiaoshangkou.example.myapplication.SEARCH;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.xiaoshangkou.example.myapplication.R;
/**
 * 搜索界面
 */

public class SearchResults extends AppCompatActivity implements AbsListView.OnScrollListener{

    private View loadmoreView;
    private LayoutInflater inflater;
    //列表视图组件
    private ListView listView;
    //判断是否下拉到最后
    private int last_index;
    private int total_index;
    private boolean isLoading = false;//表示是否正处于加载状态
    //适配器
    private MyBaseAdapter adapter;
    //实例化爬虫存储数据的类
    private SumNovel sumNovel1;
    //用户输入的内容
    private String temp;
    //总页数
    private int page=2;
    //当前爬取的页数
    private int page_ing=1;
    private Button button;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //获取intent
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        //获取用户输入的信息
        temp=bundle.getString("temp");
        sumNovel1=new SumNovel();
        inflater = LayoutInflater.from(this);
        loadmoreView = inflater.inflate(R.layout.load_more, null);//获得刷新视图
        listView=findViewById(R.id.listview1);
        button=findViewById(R.id.shuxin);
        ProgressBar progressBar=findViewById(R.id.load);
        TextView textView=findViewById(R.id.load2);
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);//设置刷新视图默认情况下是不可见的
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what == 0x110) {
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                   //sumNovel1 =(SumNovel) message.obj;
                    //获取总页数
                    page = sumNovel1.getPage();
                    //获取适配器
                    adapter = new MyBaseAdapter(SearchResults.this, sumNovel1);
                    listView.setOnScrollListener(SearchResults.this);
                    listView.addFooterView(loadmoreView, null, false);
                    listView.setAdapter(adapter);

                    //如果页数只有一页，加载视图就不出现
                    if (sumNovel1.getPage() == 1) {
                        loadmoreView.setVisibility(View.GONE);//设置刷新视图默认情况下是不可见的
                    }
                    //看是否找到书籍
                    else if (sumNovel1.isIsfind()) {
                        loadmoreView.setVisibility(View.VISIBLE);//设置刷新视图默认情况下是可见的
                    } else {
                        loadmoreView.setVisibility(View.GONE);//设置刷新视图默认情况下是不可见的
                        ImageView imageView = findViewById(R.id.not_find);
                        TextView textView = findViewById(R.id.findtext);
                        textView.setText("抱歉没找到此书籍\n也有可能是网络原因");
                        button.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.notfind);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(SearchResults.this, NovelActivity.class);
                            //通过Bundle保存输入的信息
                            Bundle bundle = new Bundle();
                            bundle.putCharSequence("url", sumNovel1.getSumNovel().get(i).get("URL"));
                            intent.putExtras(bundle);
                            //启动Activity
                            startActivity(intent);
                        }
                    });

                }
                return false;
            }
        });
        Thread t1 = new Thread(new NovelInformationThread(handler,temp, sumNovel1,page_ing));
        t1.setName("小说");
        //启动线程
        t1.start();

    }

    /**
     *
     * @param view 列表视图组件
     * @param firstVisibleItem 第一个
     * @param visibleItemCount 最后一个
     * @param totalItemCount 当前屏幕末尾是多少个
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        last_index = firstVisibleItem+visibleItemCount;
        total_index = totalItemCount;
        //System.out.println(last_index);
        //+System.out.println(total_index);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if(last_index == total_index && (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE))
        {
            System.out.println("你好");
            //表示此时需要显示刷新视图界面进行新数据的加载(要等滑动停止)
            if(!isLoading&&page!=1)//页数也不能为1页
            {
                //不处于加载状态的话对其进行加载
                isLoading = true;
                //设置刷新界面可见
                loadmoreView.setVisibility(View.VISIBLE);
                onLoad();
            }
        }
    }
    /**
     * 刷新加载
     */
    public void onLoad()
    {
        //如果爬取的页数小于总页数则继续爬取
        if (page_ing<page) {

            page_ing++;
            Thread t2 = new Thread(new NovelInformationThread2(temp, sumNovel1,page_ing));
            t2.setName("小说");
            //启动线程
            t2.start();
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(adapter == null)
            {
                adapter = new MyBaseAdapter(this, sumNovel1);
                listView.setAdapter(adapter);
            }else
            {
                //更新数据
                adapter.updateView(sumNovel1);
            }
            loadComplete();//刷新结束
        }
    }

    /**
     * 加载完成
     */
    public void loadComplete()
    {
        isLoading = false;//设置正在刷新标志位false
        SearchResults.this.invalidateOptionsMenu();
        if (page_ing==page) {//如果爬取完毕不在刷新
            loadmoreView.setVisibility(View.GONE);//设置刷新界面不可见
            listView.removeFooterView(loadmoreView);//如果是最后一页的话，则将其从ListView中移出
        }
    }

}


//自定义适配器
class MyBaseAdapter extends BaseAdapter {
    private SumNovel sumNovel;
    private final Context mContext;
    public MyBaseAdapter(Context mContext, SumNovel sumNovel) {
        super();
        this.mContext = mContext;
        this.sumNovel = sumNovel;
    }
    @Override
    public int getCount() {
        return sumNovel.getSumNovel().size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public void updateView(SumNovel sumNovel)
    {
        this.sumNovel=sumNovel;
        this.notifyDataSetChanged();//强制动态刷新数据进而调用getView方法
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        @SuppressLint({"ViewHolder", "InflateParams"})
        View convertView = inflater.inflate(R.layout.listview,null);
        //设置搜索之后的内容
        TextView topic = convertView.findViewById(R.id.topic1);
        TextView author = convertView.findViewById(R.id.author1);
        TextView wordCount = convertView.findViewById(R.id.wordCount1);
        TextView state = convertView.findViewById(R.id.state1);
        TextView update = convertView.findViewById(R.id.update1);
        TextView time = convertView.findViewById(R.id.time1);

        topic.setText(sumNovel.getSumNovel().get(i).get("name"));
        author.setText(sumNovel.getSumNovel().get(i).get("author"));
        wordCount.setText(sumNovel.getSumNovel().get(i).get("wordCount"));
        state.setText(sumNovel.getSumNovel().get(i).get("state"));
        update.setText(sumNovel.getSumNovel().get(i).get("latestChapter"));
        time.setText(sumNovel.getSumNovel().get(i).get("time"));
        return convertView;
    }
}