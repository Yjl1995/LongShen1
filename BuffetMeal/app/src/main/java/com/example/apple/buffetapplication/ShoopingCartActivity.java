package com.example.apple.buffetapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoopingCartActivity extends Activity implements ShoppingCartAdapter.goodsAddShoppingCartInterface,ShoppingCartAdapter.OnListRemovedListener{
    private List<ShoppingCart> lists;
    private ShoppingCartAdapter adapter;
    private ListView listView;
    private TextView shoppingNumber;//所有商品数量；
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    static String Shop_id = "";
    static String Desk_id = "";

    private Cursor cursor;
    static SQLiteDatabase db;
    private int K;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shooping_cart);
        Intent intent = getIntent();
        Shop_id = intent.getStringExtra("shopid");
        Desk_id = intent.getStringExtra("deskid");
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.apple.buffetapplication/yjl.db", null);
        cursor = db.query("meal", null, null, null, null, null, null);
        K = cursor.getCount();
        System.out.println("这里有 = "+K);
        initView();

    }

    //初始化控件
    public void initView() {

        sp = getSharedPreferences("shopping", 0);
        editor = sp.edit();

        listView = (ListView) findViewById(R.id.shoppinglist);
        shoppingNumber = (TextView) findViewById(R.id.shoppingcart_account_describe_tv);
        Addlist();
        adapter = new ShoppingCartAdapter(this, lists);
        adapter.setgoodsAddShoppingCartInterface(this);//设置数量变动，价格跟着变动监听
        adapter.setOnListRemovedListener(this);
        listView.setAdapter(adapter);

    }


    //初始化list；
    public void Addlist() {

        lists = new ArrayList<ShoppingCart>();
        cursor.moveToFirst();
        for(int i=0;i<K;i++) {
            ShoppingCart s1 = new ShoppingCart();
            // cursor.move(i);
            s1.setId(cursor.getString(1));
            System.out.println("食物ID = " + cursor.getString(1));
            s1.setTitle(cursor.getString(2));
            System.out.println("食物名称 = " + cursor.getString(2));
            s1.setSurplus(cursor.getString(4));
            System.out.println("食物单价 = " + cursor.getString(4));
            s1.setNumber(cursor.getString(5));
            System.out.println("食物数量 = " + cursor.getString(5));
            lists.add(s1);
            cursor.moveToNext();
        }


        DecimalFormat df = new DecimalFormat("###.0");
        double tot = getTotalPrice(lists);
        shoppingNumber.setText("共" + lists.size() + "种菜，总计：" + df.format(tot) + "元");


    }

    /**
     * @return 返回需要付费的总金额(每个商品单价都是1元)
     */
    private double getTotalPrice(List<ShoppingCart> mListData) {

        ShoppingCart bean = null;
        double totalPrice = 0;
        for (int i = 0; i < mListData.size(); i++) {
            bean = mListData.get(i);
            double price = Double.valueOf(bean.getSurplus());
            int number = Integer.valueOf(bean.getNumber());
                totalPrice = totalPrice+price*number;


        }
        return totalPrice;
    }


    //数量变动，价格跟着表动监听
    @Override
    public void clickDetial(List<ShoppingCart> lists) {

        this.lists = lists;
        adapter.notifyDataSetChanged();
        DecimalFormat df = new DecimalFormat("###.0");
        shoppingNumber.setText("共" + lists.size() + "个菜，总计：" +df.format(getTotalPrice(lists)) + "元");


    }

    //删除监听
    @Override
    public void onRemoved(final List<ShoppingCart> lists,final int position, int shopNumber) {

        this.lists = lists;

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("确定删除？");
        builder.setTitle("删除此商品么 ");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                editor.putString(lists.get(position).getId() + "", "").commit();//清空保存的数据
                lists.remove(position);
                adapter.notifyDataSetChanged();//重新刷新
                shoppingNumber.setText("共" + lists.size() + "个菜，总计：" + getTotalPrice(lists) + "元");

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }




    public  String changeJson(){
        try {
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            String username = Util.getValue(this,"NAME");
            object.put("username",username);
            object.put("shop_id",Shop_id);
            object.put("desk_id", Desk_id);
            object.put("totalprice",getTotalPrice(lists));
            Cursor cursor2 = db.query("meal", null, null, null, null, null, null);
            cursor2.moveToFirst();
            for(int i = 0;i < cursor2.getCount();i++){
                JSONObject y = new JSONObject();
                y.put("food_id",cursor2.getString(1));
                y.put("number",cursor2.getString(5));
                cursor2.moveToNext();
                array.put(y);
            }
            object.put("meal",array);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    class PrThread implements Runnable {
        public PrThread() {
        }
        public void run() {
            try {
                String json = changeJson();
                String url = "http://115.159.212.180/API/orders/submitOrder.php";
                URL url1 = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                //connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connection.setReadTimeout(5000);
                connection.connect();
                OutputStream outputStream = connection.getOutputStream();
                byte[] data = json.getBytes();
                outputStream.write(data);
                InputStream inStream = connection.getInputStream();
                String flag = new String(inputtostring(inStream));
                JSONObject jsonObject11 = new JSONObject(flag);
                String code = jsonObject11.getString("code");
                String message = jsonObject11.getString("message");
                inStream.close();
                outputStream.close();
                db.execSQL("DELETE FROM meal");
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
    public void jiesuan(View v){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认提交菜单");
        setPositiveButton(builder);
        setNegativeButton(builder);
        builder.create();
        builder.show();
    }
    private android.app.AlertDialog.Builder setPositiveButton(android.app.AlertDialog.Builder builder){
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrThread shim = new PrThread();
                Thread tt = new Thread(shim);
                tt.start();
                Toast.makeText(ShoopingCartActivity.this,"正在为您烹饪，请稍等！",Toast.LENGTH_SHORT);
                ShoopingCartActivity.this.finish();
            }
        });
    }
    private android.app.AlertDialog.Builder setNegativeButton(android.app.AlertDialog.Builder builder){
        return builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
}
