package com.example.apple.buffetapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

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

public class RecommendedActivity extends AppCompatActivity {
    SQLiteDatabase db;
    static Rmeal[] rmeals = new Rmeal[200];
    static int K = 0;
    static String Alldata = "";
    static String CODE = "";
    static String MESSAGE = "";
    static String Desk_id = "";
    private ListView listView;
    static String Shop_id = "";
    public static final int UPDATE = 1;
    static String url = "http://115.159.212.180/API/getShopRec/getSetMeal.php?shopId=";
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    Alldata = msg.obj.toString();
                    System.out.println("Alldata = "+Alldata);
                    ExcJson();
                    Uri uri = Uri.parse(rmeals[0].image);
                    SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view1);
                    draweeView.setImageURI(uri);
                    Uri uri1 = Uri.parse(rmeals[1].image);
                    SimpleDraweeView draweeView1 = (SimpleDraweeView) findViewById(R.id.my_image_view2);
                    draweeView1.setImageURI(uri1);
                    Uri uri2 = Uri.parse(rmeals[2].image);
                    SimpleDraweeView draweeView2 = (SimpleDraweeView) findViewById(R.id.my_image_view3);
                    draweeView2.setImageURI(uri2);
                    TextView textView1 = (TextView) findViewById(R.id.text_11);
                    textView1.setText(rmeals[0].food_name);
                    textView1.setTextSize(15);
                    TextView textView2 = (TextView) findViewById(R.id.text_21);
                    textView2.setText(rmeals[1].food_name);
                    textView2.setTextSize(15);
                    TextView textView3 = (TextView) findViewById(R.id.text_31);
                    textView3.setText(rmeals[2].food_name);
                    textView3.setTextSize(15);
                    TextView textView4 = (TextView) findViewById(R.id.text_12);
                    textView4.setText("￥"+Double.toString(rmeals[0].price));
                    textView4.setTextSize(10);
                    TextView textView5 = (TextView) findViewById(R.id.text_22);
                    textView5.setText("￥"+Double.toString(rmeals[1].price));
                    textView5.setTextSize(10);
                    TextView textView6 = (TextView) findViewById(R.id.text_32);
                    textView6.setText("￥"+Double.toString(rmeals[2].price));
                    textView6.setTextSize(10);
                    listView = (ListView) RecommendedActivity.this.findViewById(R.id.list_item);
                    MyAdapter adapter=new MyAdapter();
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new ItemClickEvent());
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended);
        Intent intent = getIntent();
        Shop_id = intent.getStringExtra("shopid");
        Desk_id = intent.getStringExtra("deskid");
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.apple.buffetapplication/yjl.db", null);
        PrThread shim = new PrThread();
        Thread tt = new Thread(shim);
        tt.start();
    }
    private class MyAdapter extends BaseAdapter {

        private AsyncBitmapLoader asyncBitmapLoader;
        public MyAdapter(){
            asyncBitmapLoader=new AsyncBitmapLoader();
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return K-3;
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
                convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.reitem, null);
            }
            ImageView image=(ImageView) convertView.findViewById(R.id.img);
            String imageURL=rmeals[position+3].image;
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
            TextView textView1 = (TextView) convertView.findViewById(R.id.name);
            textView1.setText(rmeals[position+3].food_name);
            TextView textView2 = (TextView) convertView.findViewById(R.id.introduce);
            textView2.setText(rmeals[position+3].introduce);
            TextView textView3 = (TextView) convertView.findViewById(R.id.price);
            textView3.setText("￥"+Double.toString(rmeals[position+3].price));
            TextView textView4 = (TextView) convertView.findViewById(R.id.price2);
            textView4.setText("原价"+Double.toString(rmeals[position+3].price2));
            TextView textView5 = (TextView) convertView.findViewById(R.id.number);
            textView5.setText("已售"+Integer.toString(rmeals[position+3].number));
            return convertView;
        }

    }
    public void ExcJson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata);
            CODE = jsonObject.getString("code");
            MESSAGE = jsonObject.getString("message");
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            K = jsonArray.length();
            for(int i = 0;i < jsonArray.length();i++){
                JSONObject x = jsonArray.getJSONObject(i);
                rmeals[i] = new Rmeal(0,"","",0,0,"",0,0);
                rmeals[i].food_id = x.getInt("set_id");
                rmeals[i].food_name = x.getString("set_name");
                rmeals[i].number = x.getInt("sold_number");
                rmeals[i].price = x.getDouble("set_price");
                rmeals[i].price2 = x.getDouble("set_price_ori");
                rmeals[i].image = x.getString("image");
                rmeals[i].star = x.getInt("star");
                rmeals[i].introduce = x.getString("set_intro");
                rmeals[i].outdata();
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
    private final class ItemClickEvent implements AdapterView.OnItemClickListener {
        @Override
        //这里需要注意的是第三个参数arg2，这是代表单击第几个选项
        public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                long arg3) {
            //通过单击事件，获得单击选项的内容
            Toast.makeText(getBaseContext(), "菜品添加成功", Toast.LENGTH_SHORT).show();
            Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{Integer.toString(rmeals[arg2+3].food_id)}, null, null, null);
            boolean bool = cursor.moveToFirst();
            System.out.println("bool = " + bool);
            if (cursor.getCount() == 0) {

                ContentValues cValue = new ContentValues();
                cValue.put("foodid", Integer.toString(rmeals[arg2+3].food_id));
                cValue.put("name", rmeals[arg2+3].food_name);
                cValue.put("introduce", rmeals[arg2+3].introduce);
                cValue.put("price", rmeals[arg2+3].price);
                cValue.put("number", 1);
                db.insert("meal", null, cValue);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("number", cursor.getInt(0) + 1);
                int flat1 = db.update("meal", contentValues, "name = ?", new String[]{rmeals[arg2+3].food_name});
                System.out.println("flat1 = " + flat1);
            }
            cursor.close();
        }
    }
    public void onclick1(View v){
        Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{Integer.toString(rmeals[0].food_id)}, null, null, null);
        boolean bool = cursor.moveToFirst();
        System.out.println("bool = " + bool);
        Toast.makeText(getBaseContext(), "菜品添加成功", Toast.LENGTH_SHORT).show();
        if (cursor.getCount() == 0) {

            ContentValues cValue = new ContentValues();
            cValue.put("foodid", Integer.toString(rmeals[0].food_id));
            cValue.put("name", rmeals[0].food_name);
            cValue.put("introduce", rmeals[0].introduce);
            cValue.put("price", rmeals[0].price);
            cValue.put("number", 1);
            db.insert("meal", null, cValue);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("number", cursor.getInt(0) + 1);
            int flat1 = db.update("meal", contentValues, "name = ?", new String[]{rmeals[0].food_name});
            System.out.println("flat1 = " + flat1);
        }
        cursor.close();
    }
    public void onclick2(View v){
        Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{Integer.toString(rmeals[1].food_id)}, null, null, null);
        boolean bool = cursor.moveToFirst();
        System.out.println("bool = " + bool);
        Toast.makeText(getBaseContext(), "菜品添加成功", Toast.LENGTH_SHORT).show();
        if (cursor.getCount() == 0) {

            ContentValues cValue = new ContentValues();
            cValue.put("foodid",Integer.toString(rmeals[1].food_id));
            cValue.put("name", rmeals[1].food_name);
            cValue.put("introduce", rmeals[0].introduce);
            cValue.put("price", rmeals[1].price);
            cValue.put("number", 1);
            db.insert("meal", null, cValue);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("number", cursor.getInt(0) + 1);
            int flat1 = db.update("meal", contentValues, "name = ?", new String[]{rmeals[1].food_name});
            System.out.println("flat1 = " + flat1);
        }
        cursor.close();
    }
    public void onclick3(View v){
        Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{Integer.toString(rmeals[2].food_id)}, null, null, null);
        boolean bool = cursor.moveToFirst();
        System.out.println("bool = " + bool);
        Toast.makeText(getBaseContext(), "菜品添加成功", Toast.LENGTH_SHORT).show();
        if (cursor.getCount() == 0) {

            ContentValues cValue = new ContentValues();
            cValue.put("foodid", Integer.toString(rmeals[2].food_id));
            cValue.put("name", rmeals[2].food_name);
            cValue.put("introduce", rmeals[2].introduce);
            cValue.put("price", rmeals[2].price);
            cValue.put("number", 1);
            db.insert("meal", null, cValue);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("number", cursor.getInt(0) + 1);
            int flat1 = db.update("meal", contentValues, "name = ?", new String[]{rmeals[2].food_name});
            System.out.println("flat1 = " + flat1);
        }
        cursor.close();
    }


    public  void goShoppingcar(View view){
        Intent intent = new Intent(this,ShoopingCartActivity.class);
        intent.putExtra("shopid",Shop_id);
        intent.putExtra("deskid",Desk_id);
        startActivity(intent);

    }
}
