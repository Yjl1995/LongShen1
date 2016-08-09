package com.example.apple.buffetapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static String Key = "NULL";
    static String Nick_name = "NULL";
    static String Email = "NULL";
    static String Image_url = "NULL";
    static String CODE = "";
    static String baseUrl = "http://115.159.212.180/API/login/logout.php?key=";
    static String imageUrl = "http://115.159.212.180/API/getRecommend/getRecommend.php";
    static int ASK = -1;
    TextView textViewnick_name;
    TextView textView_email;
    SimpleDraweeView draweeView;
    static String MESSAGE = "您网络不稳定，请检查网络连接！";

    static String url3 = "http://115.159.212.180/API/getNews/getNews.php";
    private ListView listview;
    static String Alldata2 = "";
    static String[] title = new String[100];
    static String[] short_title = new String[100];
    static String[] intro = new String[100];
    static String[] content1 = new String[100];
    static String[] imageUrl1 = new String[100];
    static String[] news_from = new String[100];
    static int K = 0;



    public static final int UPDATE = 1;
    static String Alldata = "";
    static String []IMAGE_URL = new String[4];
    static String []FOOD_ID = new String[4];
    static int NUM = 0;


    //统计下载了几张图片
    int n=0;
    //统计当前viewpager轮播到第几页
    int p=0;
    private ViewPager vp;
    private List<ImageView> data;
    //控制图片是否开始轮播的开关,默认关的
    private boolean isStart=false;
    //开始图片轮播的线程
    private MyThread t;
    //存放代表viewpager播到第几张的小圆点
    private LinearLayout ll_tag;
    //存储小圆点的一维数组
    private ImageView tag[];


    private Handler YHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    Alldata2 = msg.obj.toString();
                    exjson();
                    listview = (ListView) findViewById(R.id.list);
                    MyAdapter1 adapter = new MyAdapter1();
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new ItemClickEvent());
                    break;
                default:
                    break;
            }
        }

    };

    class PYThread implements Runnable {
        public PYThread() {
        }

        public void run() {
            try {

                URL url1 = new URL(url3);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(5000);
                connection.connect();
                InputStream inStream = connection.getInputStream();
                String flag = new String(inputtostring(inStream));
                System.out.println("flag = " + flag);
                Message message = new Message();
                message.what = UPDATE;
                message.obj = flag;
                YHandler.sendMessage(message);
                inStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class PrThread implements Runnable {


        public PrThread(){}
        public void run() {
            try {
                URL url = new URL(baseUrl +Key);
                System.out.println("url" + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(3000);
                connection.connect();
                InputStream inStream = connection.getInputStream();
                String flag = new String(inputtostring(inStream));
                System.out.println("flag = " + flag);

                JSONObject jsonObject = new JSONObject(flag);
                String code = jsonObject.getString("code");
                final String message = jsonObject.getString("message");
                System.out.println("code = "+code);
                System.out.println("message = "+message);

                if (code.equals("200")) {
                    {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //更新UI
                                ASK = 1;
                                MESSAGE = message;
                            }

                        });
                    }
                } else {
                    {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //更新UI
                                ASK = 0;
                                MESSAGE = message;
                            }

                        });
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    class PPThread implements Runnable {
        public PPThread() {
        }
        public void run() {
            try {
                URL url1 = new URL(imageUrl);
                System.out.println("您发送的请求为："+url1);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(5000);
                connection.connect();
                InputStream inStream = connection.getInputStream();
                String flag = new String(inputtostring(inStream));
                System.out.println("flag = " + flag);
                Message message = new Message();
                message.what = UPDATE;
                message.obj = flag;
                mHandler.sendMessage(message);
                inStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    Alldata = msg.obj.toString();
                    System.out.println("Alldata = "+Alldata);
                    ExcJson();
                    if(CODE.equals("200")){
                        init();
                        for(int i = 0;i < 4;i++){
                            System.out.println("图片地址 = "+IMAGE_URL[i]);
                            System.out.println("食物ID = "+FOOD_ID[i]);
                        }
                    }
                    else{
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private Handler mmHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
                case 0:
                    n++;
                    Bitmap bitmap=(Bitmap) msg.obj;
                    ImageView iv=new ImageView(NewMainActivity.this);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setImageBitmap(bitmap);
                    //把图片添加到集合里
                    data.add(iv);
                    //当接收到第三张图片的时候，设置适配器,
                    if(n==IMAGE_URL.length){
                        vp.setAdapter(new MyAdapter(data,NewMainActivity.this));
                        //创建小圆点
                        creatTag();
                        //把开关打开
                        isStart=true;
                        t=new MyThread();
                        //启动轮播图片线程
                        t.start();

                    }
                    break;
                case 1:
                    //接受到的线程发过来的p数字
                    int page=(Integer) msg.obj;
                    vp.setCurrentItem(page);

                    break;

            }
        };
    };
    //inputtostring
    public static String inputtostring(InputStream in_st){
        BufferedReader in = new BufferedReader(new InputStreamReader(in_st));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null){
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    private void ExcJson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata);
            CODE = jsonObject.getString("code");
            JSONArray jsondata = jsonObject.getJSONArray("data");
            NUM = jsondata.length();
            for(int i = 0;i < jsondata.length();i++){
                JSONObject FOOD = jsondata.getJSONObject(i);
                FOOD_ID[i] = FOOD.getString("foodId");
                IMAGE_URL[i] = FOOD.getString("imageUrl");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        PPThread shim = new PPThread();
        Thread tt = new Thread(shim);
        tt.start();

        Fresco.initialize(this);
        PYThread shim1 = new PYThread();
        Thread tt1 = new Thread(shim1);
        tt1.start();
        ASK = -1;
        MESSAGE = "您网络不稳定，请检查网络连接！";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String KEY = intent.getStringExtra("KEY");
        String NICK_NAME = intent.getStringExtra("NICK_NAME");
        String EMAIL = intent.getStringExtra("EMAIL");
        String IMAGE_URL = intent.getStringExtra("IMAGE_URL");
        System.out.printf("KEY = ",KEY);
        System.out.printf("NICK_NAME = ",NICK_NAME);
        System.out.printf("EMAIL = ",EMAIL);
        System.out.printf("IMAGE_URL = ",IMAGE_URL);
        Key = KEY;
        Nick_name = NICK_NAME;
        Email = EMAIL;
        Image_url = IMAGE_URL;
        Uri uri = Uri.parse(Image_url);
        ImagePipeline image = Fresco.getImagePipeline();
        image.evictFromCache(uri);
        Util.putValue(this, "KEY", Key);
        Util.putValue(this,"NICK_NAME",Nick_name);
        Util.putValue(this,"EMAIL",Email);
        Util.putValue(this, "IMAGE_URL", Image_url);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(NewMainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_new_main);
        textViewnick_name = (TextView) headerLayout.findViewById(R.id.name);
        textView_email = (TextView) headerLayout.findViewById(R.id.email);
        draweeView = (SimpleDraweeView) headerLayout.findViewById(R.id.my_image_view);
        textViewnick_name.setText(Nick_name);
        textView_email.setText(Email);
        draweeView.setImageURI(uri);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            // Handle the camera action
            Intent intent = new Intent(this,HistoryActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_modify) {
            Intent intent = new Intent(this,ModifyActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_collection) {
            Intent intent = new Intent(this,MyCollectionActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确认注销?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LogOut();
                            //finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
            builder.create().show();

        }
        else if (id == R.id.nav_setting) {

        }
        else if (id == R.id.nav_ours) {
            Intent intent=new Intent(this,AboutUsActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void LogOut(){


        final ProgressDialog progressDialog = new ProgressDialog(NewMainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("注销中...");
        progressDialog.show();

        PrThread shim = new PrThread();
        Thread t = new Thread(shim);
        t.start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (ASK == 1) {
                            Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();

                        } else {
                            onLogoutFailed();
                        }
                        onLogoutSuccess();
                    }
                }, 1100);
    }

    public void onLogoutSuccess() {
        Util.putBooleanValue(this,
                "LoginState", false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onLogoutFailed() {
        Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();

    }


    private int keyBackClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
                case 0:
                    Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
//                    EMChatManager.getInstance().logout();// 退出环信聊天
//                    App.getInstance2().exit();
                    finish();
//                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private void init() {
        // TODO Auto-generated method stub
        vp=(ViewPager) findViewById(R.id.vp);
        ll_tag=(LinearLayout) findViewById(R.id.ll_tag);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                //把当前的页数赋值给P
                p = position;
                //得到当前图片的索引,如果图片只有三张，那么只有0，1，2这三种情况
                int currentIndex = (position % IMAGE_URL.length);
                for (int i = 0; i < tag.length; i++) {
                    if (i == currentIndex) {
                        tag[i].setBackgroundResource(R.drawable.on);
                    } else {
                        tag[i].setBackgroundResource(R.drawable.off);
                    }
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //构造一个存储照片的集合
        data=new ArrayList<ImageView>();
        //从网络上把图片下载下来
        for(int i=0;i<IMAGE_URL.length;i++){
            getImageFromNet(IMAGE_URL[i]);

        }




    }

    private void getImageFromNet(final String imagePath) {
        // TODO Auto-generated method stub
        new Thread(){
            public void run() {
                try {
                    URL url=new URL(imagePath);
                    HttpURLConnection con=(HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(10*1000);
                    InputStream is=con.getInputStream();
                    //把流转换为bitmap
                    Bitmap bitmap= BitmapFactory.decodeStream(is);
                    Message message=new Message();
                    message.what=0;
                    message.obj=bitmap;
                    //把这个bitmap发送到hanlder那里去处理
                    mmHandler.sendMessage(message);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            };
        }.start();

    }
    //控制图片轮播
    class MyThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(isStart){
                Message message=new Message();
                message.what=1;
                message.obj=p;
                mmHandler.sendMessage(message);
                try {
                    //睡眠3秒,在isStart为真的情况下，一直每隔三秒循环
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                p++;
            }
        }
    }
    protected void creatTag() {
        tag=new ImageView[IMAGE_URL.length];
        for(int i=0;i<IMAGE_URL.length;i++){

            tag[i]=new ImageView(NewMainActivity.this);
            //第一张图片画的小圆点是白点
            if(i==0){
                tag[i].setBackgroundResource(R.drawable.on);
            }else{
                //其它的画灰点
                tag[i].setBackgroundResource(R.drawable.off);
            }
            //设置上下左右的间隔
            tag[i].setPadding(10, 10, 10, 10);
            tag[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //添加到viewpager底部的线性布局里面
            ll_tag.addView(tag[i]);
        }

    }


    private class MyAdapter1 extends BaseAdapter {


        private AsyncBitmapLoader asyncBitmapLoader;
        public MyAdapter1(){
            asyncBitmapLoader=new AsyncBitmapLoader();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return K;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.news_item, null);
            }
            Uri uri = Uri.parse(imageUrl1[position]);
            SimpleDraweeView draweeViewq1 = (SimpleDraweeView) convertView.findViewById(R.id.img1);
            draweeViewq1.setImageURI(uri);
            TextView textView1 = (TextView) convertView.findViewById(R.id.te1);
            textView1.setText(title[position]);
//            TextView textView2 = (TextView) convertView.findViewById(R.id.te2);
//            textView2.setText(intro[position]);
            return convertView;
        }

    }

    public void exjson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata2);
            JSONArray data = jsonObject.getJSONArray("data");
            K = data.length();
            for(int i = 0;i < data.length();i++){
                JSONObject x = data.getJSONObject(i);
                title[i] = x.getString("title");
                System.out.println("shopName = "+title[i]);
                short_title[i] = x.getString("short_title");
                intro[i] = x.getString("intro");
                content1[i] = x.getString("content");
                imageUrl1[i] = x.getString("imageUrl");
                news_from[i] = x.getString("news_from");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private final class ItemClickEvent implements AdapterView.OnItemClickListener {
        @Override
        //这里需要注意的是第三个参数arg2，这是代表单击第几个选项
        public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                long arg3) {
            //通过单击事件，获得单击选项的内容
            Intent intent = new Intent(NewMainActivity.this,NewsActivity.class);
            intent.putExtra("title",title[arg2]);
            intent.putExtra("short_title",short_title[arg2]);
            intent.putExtra("content",content1[arg2]);
            intent.putExtra("imageUrl",imageUrl1[arg2]);
            intent.putExtra("news_from",news_from[arg2]);
            startActivity(intent);
        }
    }

}
