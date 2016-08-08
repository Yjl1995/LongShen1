package com.example.apple.buffetapplication;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeActivity extends AppCompatActivity{
    private ListView listview1;
    private ListView listview2;
    private ListView listview3;
    private ListView listview4;
    SQLiteDatabase db;
    static String Shop_id = "";
    static String url = "http://115.159.212.180/API/getFoodMenu/getFoodMenu.php?shopId=";
    static String Alldata = "";
    static String CODE = "";
    static String MESSAGE = "";
    static String Desk_id = "";
    static Meal[] type_1 = new Meal[200];
    static int type_1num = 0;
    static Meal[] type_2 = new Meal[200];
    static int type_2num = 0;
    static Meal[] type_3 = new Meal[200];
    static int type_3num = 0;
    static Meal[] type_4 = new Meal[200];
    static int type_4num = 0;
    public static final int UPDATE = 1;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    Alldata = msg.obj.toString();
                    ExcJson();
                    for(int i = 0;i < type_3num;i++){
                        type_3[i].outdata();
                    }
                    for(int i = 0;i < type_4num;i++){
                        type_4[i].outdata();
                    }
                    View view1 = LayoutInflater.from(RecipeActivity.this).inflate(R.layout.tab1, null);
                    listview1 = (ListView)view1.findViewById(R.id.list_item);
                    View view2 = LayoutInflater.from(RecipeActivity.this).inflate(R.layout.tab2, null);
                    listview2 = (ListView)view2.findViewById(R.id.list_item2);
                    View view3 = LayoutInflater.from(RecipeActivity.this).inflate(R.layout.tab3, null);
                    listview3 = (ListView)view3.findViewById(R.id.list_item3);
                    View view4 = LayoutInflater.from(RecipeActivity.this).inflate(R.layout.tab4, null);
                    listview4 = (ListView)view4.findViewById(R.id.list_item4);

                    viewContainter.add(view1);
                    viewContainter.add(view2);
                    viewContainter.add(view3);
                    viewContainter.add(view4);
                    titleContainer.add("荤菜");
                    titleContainer.add("素菜");
                    titleContainer.add("汤类");
                    titleContainer.add("酒水");

                    pager.setAdapter(new PagerAdapter() {

                        //viewpager中的组件数量
                        @Override
                        public int getCount() {
                            return viewContainter.size();
                        }

                        //滑动切换的时候销毁当前的组件
                        @Override
                        public void destroyItem(ViewGroup container, int position,
                                                Object object) {
                            ((ViewPager) container).removeView(viewContainter.get(position));
                        }

                        //每次滑动的时候生成的组件
                        @Override
                        public Object instantiateItem(ViewGroup container, int position) {
                            ((ViewPager) container).addView(viewContainter.get(position));
                            return viewContainter.get(position);
                        }

                        @Override
                        public boolean isViewFromObject(View arg0, Object arg1) {
                            return arg0 == arg1;
                        }

                        @Override
                        public int getItemPosition(Object object) {
                            return super.getItemPosition(object);
                        }

                        @Override
                        public CharSequence getPageTitle(int position) {
                            return titleContainer.get(position);
                        }
                    });
                    // listview1=(ListView) findViewById(R.id.list_item);
                    MyAdapter1 adapter1=new MyAdapter1();
                    listview2.setAdapter(adapter1);
                    listview2.setOnItemClickListener(new ItemClickEvent1());
                    //listview1.setOnItemClickListener(listener);
                    // listview2=(ListView) findViewById(R.id.list_item2);
                    MyAdapter2 adapter2=new MyAdapter2();
                    listview1.setAdapter(adapter2);
                    listview1.setOnItemClickListener(new ItemClickEvent2());
                    //listview3=(ListView) findViewById(R.id.list_item3);
                    MyAdapter3 adapter3=new MyAdapter3();
                    listview4.setAdapter(adapter3);
                    listview4.setOnItemClickListener(new ItemClickEvent3());
                    // listview3.setOnItemClickListener(listener);
                    //listview4=(ListView) findViewById(R.id.list_item4);
                    MyAdapter4 adapter4=new MyAdapter4();
                    listview3.setAdapter(adapter4);
                    listview3.setOnItemClickListener(new ItemClickEvent4());
                    // listview4.setOnItemClickListener(listener);
                    //System.out.println("ALLdata = "+Alldata);
                    break;
                default:
                    break;
            }
        }

    };

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
                //System.out.println("flag = " + flag);
                Message message = new Message();
                message.what = UPDATE;
                message.obj = flag;
                mHandler.sendMessage(message);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    ViewPager pager = null;
    PagerTabStrip tabStrip = null;
    ArrayList<View> viewContainter = new ArrayList<View>();
    ArrayList<String> titleContainer = new ArrayList<String>();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Intent intent = getIntent();
        Shop_id = intent.getStringExtra("shopid");
        Desk_id = intent.getStringExtra("deskid");
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.apple.buffetapplication/yjl.db", null);
        type_1num = 0;
        type_2num = 0;
        type_3num = 0;
        type_4num = 0;
        PrThread shim = new PrThread();
        Thread tt = new Thread(shim);
        tt.start();

        pager = (ViewPager) this.findViewById(R.id.viewpager);
        tabStrip = (PagerTabStrip) this.findViewById(R.id.tabstrip);
        //取消tab下面的长横线
        tabStrip.setDrawFullUnderline(false);
        //设置tab的背景色
        tabStrip.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
        //设置当前tab页签的下划线颜色
        tabStrip.setTabIndicatorColor(this.getResources().getColor(R.color.colorAccent));
        tabStrip.setTextSpacing(200);

        /*View view1 = LayoutInflater.from(this).inflate(R.layout.tab1, null);
        ListView listview1 = (ListView)view1.findViewById(R.id.list_item);
        View view2 = LayoutInflater.from(this).inflate(R.layout.tab2, null);
        ListView listview2 = (ListView)view2.findViewById(R.id.list_item2);
        View view3 = LayoutInflater.from(this).inflate(R.layout.tab3, null);
        ListView listview3 = (ListView)view3.findViewById(R.id.list_item3);
        View view4 = LayoutInflater.from(this).inflate(R.layout.tab4, null);
        ListView listview4 = (ListView)view4.findViewById(R.id.list_item4);*/
        //viewpager开始添加view
       /* viewContainter.add(view1);
        viewContainter.add(view2);
        viewContainter.add(view3);
        viewContainter.add(view4);*/
        //页签项
     /*   titleContainer.add("荤菜");
        titleContainer.add("素菜");
        titleContainer.add("汤类");
        titleContainer.add("酒水");

        pager.setAdapter(new PagerAdapter() {

            //viewpager中的组件数量
            @Override
            public int getCount() {
                return viewContainter.size();
            }

            //滑动切换的时候销毁当前的组件
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                ((ViewPager) container).removeView(viewContainter.get(position));
            }

            //每次滑动的时候生成的组件
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(viewContainter.get(position));
                return viewContainter.get(position);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleContainer.get(position);
            }
        });*/




    }
    private void ExcJson(){
        try {
            System.out.println("Alldata = "+Alldata);
            JSONObject jsonObject = new JSONObject(Alldata);
            CODE = jsonObject.getString("code");
            System.out.println("code = "+CODE);
            MESSAGE = jsonObject.getString("message");
            System.out.println("MESSAGE = "+MESSAGE);
            JSONObject jsondata = jsonObject.getJSONObject("data");
            JSONArray jsontype_1 = jsondata.getJSONArray("type_1");
            // JSONObject jsontype_1 = jsondata.getJSONObject("type_1");
            JSONArray jsontype_2 = jsondata.getJSONArray("type_2");
            //JSONObject jsontype_2 = jsondata.getJSONObject("type_2");
            JSONArray jsontype_3 = jsondata.getJSONArray("type_3");
            //JSONObject jsontype_3 = jsondata.getJSONObject("type_3");
            JSONArray jsontype_4 = jsondata.getJSONArray("type_4");
            //JSONObject jsontype_4 = jsondata.getJSONObject("type_4");
            // int type_1n = jsontype_1.getInt("type_1num");
            //int type_2n = jsontype_2.getInt("type_2num");
            // int type_3n = jsontype_3.getInt("type_3num");
            // int type_4n = jsontype_4.getInt("type_4num");
            for(int i = 2;i < jsontype_1.length();i++){
                JSONObject jsontype_1JSONObject = jsontype_1.getJSONObject(i);
                type_1[type_1num] = new Meal(0,"","",0,0,"");
                type_1[type_1num].food_id = jsontype_1JSONObject.getInt("food_id");
                type_1[type_1num].food_name = jsontype_1JSONObject.getString("food_name");
                type_1[type_1num].introduce = jsontype_1JSONObject.getString("introduce");
                type_1[type_1num].price = jsontype_1JSONObject.getDouble("price");
                type_1[type_1num].star = jsontype_1JSONObject.getInt("star");
                type_1[type_1num].image = jsontype_1JSONObject.getString("image");
                type_1num++;
            }
            for(int i = 2;i < jsontype_2.length();i++){
                JSONObject jsontype_2JSONObject = jsontype_2.getJSONObject(i);
                type_2[type_2num] = new Meal(0,"","",0,0,"");
                type_2[type_2num].food_id = jsontype_2JSONObject.getInt("food_id");
                type_2[type_2num].food_name = jsontype_2JSONObject.getString("food_name");
                type_2[type_2num].introduce = jsontype_2JSONObject.getString("introduce");
                type_2[type_2num].price = jsontype_2JSONObject.getDouble("price");
                type_2[type_2num].star = jsontype_2JSONObject.getInt("star");
                type_2[type_2num].image = jsontype_2JSONObject.getString("image");
                type_2num++;
            }
            for(int i = 2;i < jsontype_3.length();i++){
                JSONObject jsontype_3JSONObject = jsontype_3.getJSONObject(i);
                type_3[type_3num] = new Meal(0,"","",0,0,"");
                type_3[type_3num].food_id = jsontype_3JSONObject.getInt("food_id");
                type_3[type_3num].food_name = jsontype_3JSONObject.getString("food_name");
                type_3[type_3num].introduce = jsontype_3JSONObject.getString("introduce");
                type_3[type_3num].price = jsontype_3JSONObject.getDouble("price");
                type_3[type_3num].star = jsontype_3JSONObject.getInt("star");
                type_3[type_3num].image = jsontype_3JSONObject.getString("image");
                type_3num++;
            }
            for(int i = 2;i < jsontype_4.length();i++){
                JSONObject jsontype_4JSONObject = jsontype_4.getJSONObject(i);
                type_4[type_4num] = new Meal(0,"","",0,0,"");
                type_4[type_4num].food_id = jsontype_4JSONObject.getInt("food_id");
                type_4[type_4num].food_name = jsontype_4JSONObject.getString("food_name");
                type_4[type_4num].introduce = jsontype_4JSONObject.getString("introduce");
                type_4[type_4num].price = jsontype_4JSONObject.getDouble("price");
                type_4[type_4num].star = jsontype_4JSONObject.getInt("star");
                type_4[type_4num].image = jsontype_4JSONObject.getString("image");
                type_4num++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
            return type_1num;
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
            if(convertView==null){
                convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null);
            }
            ImageView image=(ImageView) convertView.findViewById(R.id.img1);
            String imageURL=type_1[position].image;
            System.out.println("position1 = "+position);
            Bitmap bitmap=asyncBitmapLoader.loadBitmap(image, imageURL, new AsyncBitmapLoader.ImageCallBack() {

                @Override
                public void imageLoad(ImageView imageView, Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    imageView.setImageBitmap(bitmap);
                }
            });
            if(bitmap == null)
            {
                image.setImageResource(R.drawable.ic_search);
            }
            else
            {
                image.setImageBitmap(bitmap);
            }
            TextView textView1 = (TextView) convertView.findViewById(R.id.name1);
            textView1.setText(type_1[position].food_name);
            TextView textView2 = (TextView) convertView.findViewById(R.id.introduce1);
            textView2.setText(type_1[position].introduce);
            TextView textView3 = (TextView) convertView.findViewById(R.id.price1);
            textView3.setText(Double.toString(type_1[position].price)+"￥");
            return convertView;
        }

    }
    private class MyAdapter2 extends BaseAdapter {

        private AsyncBitmapLoader asyncBitmapLoader;
        public MyAdapter2(){
            asyncBitmapLoader=new AsyncBitmapLoader();
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return type_2num;
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
            if(convertView==null){
                convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.item2, null);
            }
            ImageView image=(ImageView) convertView.findViewById(R.id.img2);
            String imageURL=type_2[position].image;
            System.out.println("position2 = " + position);
            Bitmap bitmap=asyncBitmapLoader.loadBitmap(image, imageURL, new AsyncBitmapLoader.ImageCallBack() {

                @Override
                public void imageLoad(ImageView imageView, Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    imageView.setImageBitmap(bitmap);
                }
            });
            if(bitmap == null)
            {
                image.setImageResource(R.drawable.ic_search);
            }
            else
            {
                image.setImageBitmap(bitmap);
            }
            TextView textView1 = (TextView) convertView.findViewById(R.id.name2);
            textView1.setText(type_2[position].food_name);
            TextView textView2 = (TextView) convertView.findViewById(R.id.introduce2);
            textView2.setText(type_2[position].introduce);
            TextView textView3 = (TextView) convertView.findViewById(R.id.price2);
            textView3.setText(Double.toString(type_2[position].price)+"￥");
            return convertView;
        }

    }
    private class MyAdapter3 extends BaseAdapter {

        private AsyncBitmapLoader asyncBitmapLoader;
        public MyAdapter3(){
            asyncBitmapLoader=new AsyncBitmapLoader();
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return type_3num;
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
            if(convertView==null){
                convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.item3, null);
            }
            ImageView image=(ImageView) convertView.findViewById(R.id.img3);
            String imageURL=type_3[position].image;
            System.out.println("position3 = " + position);
            Bitmap bitmap=asyncBitmapLoader.loadBitmap(image, imageURL, new AsyncBitmapLoader.ImageCallBack() {

                @Override
                public void imageLoad(ImageView imageView, Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    imageView.setImageBitmap(bitmap);
                }
            });
            if(bitmap == null)
            {
                image.setImageResource(R.drawable.ic_search);
            }
            else
            {
                image.setImageBitmap(bitmap);
            }
            TextView textView1 = (TextView) convertView.findViewById(R.id.name3);
            textView1.setText(type_3[position].food_name);
            TextView textView2 = (TextView) convertView.findViewById(R.id.introduce3);
            textView2.setText(type_3[position].introduce);
            TextView textView3 = (TextView) convertView.findViewById(R.id.price3);
            textView3.setText(Double.toString(type_3[position].price)+"￥");
            return convertView;
        }

    }
    private class MyAdapter4 extends BaseAdapter {

        private AsyncBitmapLoader asyncBitmapLoader;
        public MyAdapter4(){
            asyncBitmapLoader=new AsyncBitmapLoader();
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return type_4num;
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
            if(convertView==null){
                convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.item4, null);
            }
            ImageView image=(ImageView) convertView.findViewById(R.id.img4);
            String imageURL=type_4[position].image;
            System.out.println("position4 = " + position);
            Bitmap bitmap=asyncBitmapLoader.loadBitmap(image, imageURL, new AsyncBitmapLoader.ImageCallBack() {

                @Override
                public void imageLoad(ImageView imageView, Bitmap bitmap) {
                    // TODO Auto-generated method stub
                    imageView.setImageBitmap(bitmap);
                }
            });
            if(bitmap == null)
            {
                image.setImageResource(R.drawable.ic_search);
            }
            else
            {
                image.setImageBitmap(bitmap);
            }
            TextView textView1 = (TextView) convertView.findViewById(R.id.name4);
            textView1.setText(type_4[position].food_name);
            TextView textView2 = (TextView) convertView.findViewById(R.id.introduce4);
            textView2.setText(type_4[position].introduce);
            TextView textView3 = (TextView) convertView.findViewById(R.id.price4);
            textView3.setText(Double.toString(type_4[position].price)+"￥");
            return convertView;
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
   /* private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
                                long id) {
            ListView lv = (ListView)parent;
            HashMap<String,Object> person = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
            Toast.makeText(getApplicationContext(), person.toString(), Toast.LENGTH_SHORT).show();
        }
    };*/
   private final class ItemClickEvent1 implements AdapterView.OnItemClickListener {
       @Override
       //这里需要注意的是第三个参数arg2，这是代表单击第几个选项
       public void onItemClick(AdapterView arg0, View arg1, int arg2,
                               long arg3) {
           //通过单击事件，获得单击选项的内容

           Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{Integer.toString(type_1[arg2].food_id)}, null, null, null);
           boolean bool = cursor.moveToFirst();
           System.out.println("bool = " + bool);
           if (cursor.getCount() == 0) {

               ContentValues cValue = new ContentValues();
               cValue.put("foodid", Integer.toString(type_1[arg2].food_id));
               cValue.put("name", type_1[arg2].food_name);
               cValue.put("introduce", type_1[arg2].introduce);
               cValue.put("price", type_1[arg2].price);
               cValue.put("number", 1);
               db.insert("meal", null, cValue);
           } else {
               ContentValues contentValues = new ContentValues();
               contentValues.put("number", cursor.getInt(0) + 1);
               int flat1 = db.update("meal", contentValues, "name = ?", new String[]{type_1[arg2].food_name});
               System.out.println("flat1 = " + flat1);
           }
           cursor.close();
       }
   }
    private final class ItemClickEvent2 implements AdapterView.OnItemClickListener {
        @Override
        //这里需要注意的是第三个参数arg2，这是代表单击第几个选项
        public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                long arg3) {
            //通过单击事件，获得单击选项的内容
            System.out.println("arg = "+arg2);
            Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{Integer.toString(type_2[arg2].food_id)}, null, null, null);
            boolean bool = cursor.moveToFirst();
            System.out.println("bool = " + bool);
            if (cursor.getCount() == 0) {

                ContentValues cValue = new ContentValues();
                cValue.put("foodid", Integer.toString(type_2[arg2].food_id));
                cValue.put("name", type_2[arg2].food_name);
                cValue.put("introduce", type_2[arg2].introduce);
                cValue.put("price", type_2[arg2].price);
                cValue.put("number", 1);
                db.insert("meal", null, cValue);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("number", cursor.getInt(0) + 1);
                int flat1 = db.update("meal", contentValues, "name = ?", new String[]{type_2[arg2].food_name});
                System.out.println("flat1 = " + flat1);
            }
            cursor.close();
        }
    }
    private final class ItemClickEvent3 implements AdapterView.OnItemClickListener {
        @Override
        //这里需要注意的是第三个参数arg2，这是代表单击第几个选项
        public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                long arg3) {
            //通过单击事件，获得单击选项的内容

            Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{Integer.toString(type_3[arg2].food_id)}, null, null, null);
            boolean bool = cursor.moveToFirst();
            System.out.println("bool = " + bool);
            if (cursor.getCount() == 0) {

                ContentValues cValue = new ContentValues();
                cValue.put("foodid", Integer.toString(type_3[arg2].food_id));
                cValue.put("name", type_3[arg2].food_name);
                cValue.put("introduce", type_3[arg2].introduce);
                cValue.put("price", type_3[arg2].price);
                cValue.put("number", 1);
                db.insert("meal", null, cValue);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("number", cursor.getInt(0) + 1);
                int flat1 = db.update("meal", contentValues, "name = ?", new String[]{type_3[arg2].food_name});
                System.out.println("flat1 = " + flat1);
            }
            cursor.close();
        }
    }
    private final class ItemClickEvent4 implements AdapterView.OnItemClickListener {
        @Override
        //这里需要注意的是第三个参数arg2，这是代表单击第几个选项
        public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                long arg3) {
            //通过单击事件，获得单击选项的内容

            Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{Integer.toString(type_4[arg2].food_id)}, null, null, null);
            boolean bool = cursor.moveToFirst();
            System.out.println("bool = " + bool);
            if (cursor.getCount() == 0) {

                ContentValues cValue = new ContentValues();
                cValue.put("foodid", Integer.toString(type_4[arg2].food_id));
                cValue.put("name", type_4[arg2].food_name);
                cValue.put("introduce", type_4[arg2].introduce);
                cValue.put("price", type_4[arg2].price);
                cValue.put("number", 1);
                db.insert("meal", null, cValue);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("number", cursor.getInt(0) + 1);
                int flat1 = db.update("meal", contentValues, "name = ?", new String[]{type_4[arg2].food_name});
                System.out.println("flat1 = " + flat1);
            }
            cursor.close();
        }
    }


    public void GoShoppingcar(View view){
        Intent intent = new Intent(this,ShoopingCartActivity.class);
        intent.putExtra("shopid",Shop_id);
        intent.putExtra("deskid",Desk_id);
        startActivity(intent);
    }

}
