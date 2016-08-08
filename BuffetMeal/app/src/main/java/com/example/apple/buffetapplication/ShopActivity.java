package com.example.apple.buffetapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

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

public class ShopActivity extends AppCompatActivity {
    static String url = "http://115.159.212.180/API/getShopInfo/getShopInfo.php?shopId=";
    static String shopurl = "http://115.159.212.180/API/getShopInfo/getShopFeature.php?shopId=";
    static String Mdata = "hah";
    static String burl = "http://115.159.212.180/API/getShopRec/getMostHot.php?shopId=";
    static String Shop_id = "";
    static String Desk_id = "";
    static String Alldata = "";
    static String Allpic = "";
    static String CODE = "";
    static String CODE1 = "";
    static String MESSAGE = "";
    static String []IMAGE_URL = new String[4];
    static String shopName = "";
    static String shopInfo = "";
    static String imageUrl = "";
    static String address = "";
    static String tel = "";
//收藏 相关数据
    static String starUrl="http://115.159.212.180/API/userFavor/addUserFavor.php?userName=";
    static String unstarUrl="http://115.159.212.180/API/userFavor/cancelFavor.php?userName=";
    static String requestStarUrl="http://115.159.212.180/API/userFavor/getUserFavor.php?userName=";
    static String judgeStarUrl="http://115.159.212.180/API/userFavor/is_favor.php?userName=";
    static String starData="";
    static String userName="";
    static int star_num=0;

    SQLiteDatabase db;
    //统计下载了几张图片
    int n=0;
    //统计当前viewpager轮播到第几页
    int p=0;
    private ViewPager vp;
    //准备好三张网络图片的地址
    //装载下载图片的集合
    private List<ImageView> data;
    //控制图片是否开始轮播的开关,默认关的
    private boolean isStart=false;
    //开始图片轮播的线程
    private MyThread t;
    //存放代表viewpager播到第几张的小圆点
    private LinearLayout ll_tag;
    //存储小圆点的一维数组
    private ImageView tag[];

    private TextView textViewname;
    private TextView textViewtel;
    private TextView textViewaddress;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Alldata = msg.obj.toString();
                    ExcJson();
                    textViewname = (TextView) ShopActivity.this.findViewById(R.id.textViewname);
                    textViewname.setText(shopName);
                    textViewname.setTextSize(20);

                    textViewtel = (TextView) ShopActivity.this.findViewById(R.id.textViewtel);
                    textViewtel.setText(tel);
                    textViewtel.setTextSize(15);

                    textViewaddress = (TextView) ShopActivity.this.findViewById(R.id.textViewaddress);
                    textViewaddress.setText(address);
                    textViewaddress.setTextSize(15);
                    Uri uri = Uri.parse(imageUrl);
                    SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
                    int width = 200, height = 200;
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .setResizeOptions(new ResizeOptions(width, height))
                            .build();
                    PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                            .setOldController(draweeView.getController())
                            .setImageRequest(request)
                            .build();
                    draweeView.setController(controller);
                    if(CODE.equals("200")){
                    }
                    else{
                        Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();
                        ShopActivity.this.finish();
                    }
                    break;
                default:
                    break;
            }
        }

    };
    private Handler mmHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Allpic = msg.obj.toString();
                    ExcJsonAllpic();
                    if(CODE1.equals("200")){
                        init();
                        for(int i = 0;i <4;i++){
                            System.out.println("图片地址="+IMAGE_URL[i]);
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
    private Handler hHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
                case 0:
                    n++;
                    Bitmap bitmap=(Bitmap) msg.obj;
                    ImageView iv=new ImageView(ShopActivity.this);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setImageBitmap(bitmap);
                    //把图片添加到集合里
                    data.add(iv);
                    //当接收到第三张图片的时候，设置适配器,
                    if(n==IMAGE_URL.length){
                        vp.setAdapter(new MyAdapter(data,ShopActivity.this));
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

    //榜单handler
    private Handler BHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Mdata = msg.obj.toString();
                    String[] n1 = new String[3];
                    String[] d1 = new String[3];
                    int[] n2 = new int[3];
                    double[] n3 = new double[3];
                    try {
                        JSONObject yi1 = new JSONObject(Mdata);
                        JSONArray x1 = yi1.getJSONArray("data");
                        for(int f = 0;f < x1.length();f++){
                            JSONObject dfd = x1.getJSONObject(f);
                            d1[f] = dfd.getString("image");
                            n1[f] = dfd.getString("food_name");
                            n2[f] = dfd.getInt("star");
                            n3[f] = dfd.getDouble("price");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Uri urii1 = Uri.parse(d1[0]);
                    SimpleDraweeView draweeViewq1 = (SimpleDraweeView) findViewById(R.id.imgg1);
                    draweeViewq1.setImageURI(urii1);
                    Uri urii2 = Uri.parse(d1[1]);
                    SimpleDraweeView draweeViewq2 = (SimpleDraweeView) findViewById(R.id.imgg2);
                    draweeViewq2.setImageURI(urii2);
                    Uri urii3 = Uri.parse(d1[2]);
                    SimpleDraweeView draweeViewq3 = (SimpleDraweeView) findViewById(R.id.imgg3);
                    draweeViewq3.setImageURI(urii3);
                    TextView jin1 = (TextView) findViewById(R.id.jin1);
                    jin1.setText(n1[0]);
                    TextView yin1 = (TextView) findViewById(R.id.yin1);
                    yin1.setText(n1[1]);
                    TextView tong1 = (TextView) findViewById(R.id.tong1);
                    tong1.setText(n1[2]);

                    RatingBar jin2 = (RatingBar) findViewById(R.id.jin2);
                    jin2.setMax(5);
                    jin2.setRating((float) n2[0]);
                    TextView rating_text1 = (TextView) findViewById(R.id.rating_text1);
                    rating_text1.setText(Integer.toString(n2[0]));


                    RatingBar yin2 = (RatingBar) findViewById(R.id.yin2);
                    yin2.setMax(5);
                    yin2.setRating((float) n2[0]);
                    TextView rating_text2 = (TextView) findViewById(R.id.rating_text2);
                    rating_text2.setText(Integer.toString(n2[0]));


                    RatingBar tong2 = (RatingBar) findViewById(R.id.tong2);
                    tong2.setMax(5);
                    tong2.setRating((float) n2[0]);
                    TextView rating_text3 = (TextView) findViewById(R.id.rating_text3);
                    rating_text3.setText(Integer.toString(n2[0]));

                    TextView jin3 = (TextView) findViewById(R.id.jin3);
                    jin3.setText("￥"+Double.toString(n3[0]));
                    TextView yin3 = (TextView) findViewById(R.id.yin3);
                    yin3.setText("￥"+Double.toString(n3[1]));
                    TextView tong3 = (TextView) findViewById(R.id.tong3);
                    tong3.setText("￥"+Double.toString(n3[2]));
                    break;
                default:
                    break;
            }
        }

    };

    //收藏Handler
    // TODO：未修改完
    private Handler starHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            starData=msg.obj.toString();
            String starCode=new String();
            try{
                JSONObject starJson=new JSONObject(starData);
                starCode=starJson.getString("code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch(msg.what){
                case 0:

                    if(starCode.equals("200")){
                        Toast.makeText(ShopActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                    }

                    break;


                case 1:
                    if(starCode.equals("200")){
                        Toast.makeText(ShopActivity.this,"取消收藏成功",Toast.LENGTH_SHORT).show();
                    }

                    break;

                case 2:
                    CheckBox checkbox_star = (CheckBox) findViewById(R.id.checkbox_star);
                    if(starCode.equals("200")){
                        checkbox_star.setChecked(true);
                    }
                    else{
                        checkbox_star.setChecked(false);
                    }

                    break;

                default:
                    break;
            }
        };
    };

    //收藏的线程类
    /*
     *@params userName
     *@params shopId
     *@params type
     * return starHandler.sendMessage
     */
    //type 分别为0 1 2 时分别代表 收藏 取消收藏 判断是否收藏
    //  message.what 分别为       0     1      2
    class starThread implements Runnable{
        String user_name,shop_id;
        int type;
        public starThread(String userName){
            user_name=userName;
            shop_id="";
            type=-1;
        }
        public starThread(String userName ,String shopId,int Type){
            user_name=userName;
            shop_id=shopId;
            type=Type;
        }
        public void  run(){
            try{
                URL s_url;
                if(shop_id.equals("")){
                    s_url = new URL(requestStarUrl+user_name);
                }
                else{
                    if(type==0){
                        s_url = new URL(starUrl+user_name+"&shopId="+shop_id);
                    }
                    else if(type==1){
                        s_url =new URL(unstarUrl+user_name+"&shopId="+shop_id);
                    }
                    else if(type==2){
                        s_url =new URL(judgeStarUrl+user_name+"&shopId="+shop_id);
                    }
                    else{
                        return ;
                    }
                }


                HttpURLConnection connection = (HttpURLConnection) s_url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(5000);
                connection.connect();
                InputStream inStream = connection.getInputStream();
                String flag = new String(inputtostring(inStream));
                System.out.println("flag = " + flag);
                Message message = new Message();
                if(shop_id.equals("")){
                    message.what=3;
                }
                else{
                    if(type==0){
                        message.what=0;
                    }
                    else if(type==1){
                        message.what=1;
                    }
                    else if(type==2){
                        message.what=2;
                    }
                    else{
                        message.what=-1;
                    }
                }
                message.obj=flag;
                starHandler.sendMessage(message);
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
        public PrThread() {
        }
        public void run() {
            try {
                URL url1 = new URL(url+Shop_id);

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
                message.what = 1;
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
    class PPThread implements Runnable {
        public PPThread() {
        }
        public void run() {
            try {
                URL url1 = new URL(shopurl+Shop_id);

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
                message.what = 1;
                message.obj = flag;
                mmHandler.sendMessage(message);
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
//榜单线程
    class PThread3 implements Runnable {
        public PThread3() {
        }
        public void run() {
            try {
                URL url1 = new URL(burl+Shop_id);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(5000);
                connection.connect();
                InputStream inStream = connection.getInputStream();
                String flag = new String(inputtostring(inStream));
                //System.out.println("flag = " + flag);
                Message bmessage = new Message();
                bmessage.what = 1;
                bmessage.obj = flag;
                BHandler.sendMessage(bmessage);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private CheckBox checkbox_star;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        Intent intent = getIntent();
        Shop_id = intent.getStringExtra("shopid");
        Desk_id = intent.getStringExtra("deskid");
        System.out.println("Shop_id = " + Shop_id);
        PrThread shim = new PrThread();
        PPThread shimm = new PPThread();
        Thread pp = new Thread(shimm);
        Thread tt = new Thread(shim);
        tt.start();
        pp.start();
        PThread3 pThread3 = new PThread3();
        Thread ll = new Thread(pThread3);
        ll.start();

        userName = Util.getValue(this,"NAME");
        //judge是否收藏
        starThread judge_Sthread =new starThread(userName,Shop_id,2);
        Thread judge_thread = new Thread(judge_Sthread);
        judge_thread.start();

        checkbox_star = (CheckBox) findViewById(R.id.checkbox_star);
        checkbox_star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (star_num != 0) {
                    if (isChecked) {
                        starThread star0_thread = new starThread(userName, Shop_id, 0);
                        Thread star0thread = new Thread(star0_thread);
                        star0thread.start();

                    } else {
                        starThread star1_thread = new starThread(userName, Shop_id, 1);
                        Thread star1thread = new Thread(star1_thread);
                        star1thread.start();
                    }
                }
                star_num++;
            }
        });

        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.apple.buffetapplication/yjl.db", null);
        db.execSQL("DROP TABLE IF EXISTS meal");
        db.execSQL("create table meal(_id integer primary key autoincrement,foodid text,name text,introduce text,price text,number text)");
    }
    public void ToMerchans(View v){
        Intent intent = new Intent(this,MerchantsActivity.class);
        intent.putExtra("shopId",Shop_id);
        startActivity(intent);
    }
    public void ToRecommended(View v){
        Intent intent = new Intent(this,RecommendedActivity.class);
        intent.putExtra("shopid", Shop_id);
        intent.putExtra("deskid",Desk_id);
        startActivity(intent);
    }
    public void ToRecipe(View v){
        Intent intent = new Intent(this,RecipeActivity.class);
        intent.putExtra("shopid",Shop_id);
        intent.putExtra("deskid",Desk_id);
        startActivity(intent);
    }
    public void ToOrder(View v){
        Intent intent = new Intent(this,OrderActivity.class);
        intent.putExtra("shopid",Shop_id);
        intent.putExtra("deskid",Desk_id);
        startActivity(intent);
    }

    private void ExcJson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata);
            CODE = jsonObject.getString("code");
            MESSAGE = jsonObject.getString("message");
            JSONObject jsondata = jsonObject.getJSONObject("data");
            shopName = jsondata.getString("shopName");
            shopInfo = jsondata.getString("shopInfo");
            imageUrl = jsondata.getString("imageUrl");
            address = jsondata.getString("address");
            tel = jsondata.getString("tel");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void ExcJsonAllpic(){
        try {
            JSONObject jsonObject = new JSONObject(Allpic);
            CODE1= jsonObject.getString("code");
            JSONArray jsondata = jsonObject.getJSONArray("data");
            for(int i = 0;i <4;i++){
                IMAGE_URL[i] = jsondata.getString(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
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





    private void init() {
        // TODO Auto-generated method stub
        vp=(ViewPager) findViewById(R.id.vp2);
        ll_tag=(LinearLayout) findViewById(R.id.ll_tag2);
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
                    hHandler.sendMessage(message);

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
                hHandler.sendMessage(message);
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

            tag[i]=new ImageView(ShopActivity.this);
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



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确认离开店铺?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ShopActivity.this.finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
            builder.create().show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
