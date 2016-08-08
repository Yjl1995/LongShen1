package com.example.apple.buffetapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.Objects;

public class MyCollectionActivity extends AppCompatActivity {

    String userFavorUrl="http://115.159.212.180/API/userFavor/getUserFavor.php?userName=";
    String shopInfoUrl="http://115.159.212.180/API/getShopInfo/getShopInfo.php?shopId=";
    String collectionData="";
    List<Map<String,Object>> listItems;

    String MESSAGE="请检查网络连接!"; //用于判断是否加载成功

    //线程
    class collectionThread extends Thread{
        String shopId="";
        String userName="";
        URL url;

        //两个不同的参数有不同的用法
        public collectionThread() throws MalformedURLException {
            userName = Util.getValue(MyCollectionActivity.this, "NAME");
            System.out.println("userName"+userName);
            url = new URL(userFavorUrl + userName);
        }
        public collectionThread(String shop_id) throws MalformedURLException {
            shopId=shop_id;
            url = new URL(shopInfoUrl+shopId);
        }
        @Override
        public void run(){
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(5000);
                connection.connect();
                InputStream inStream = connection.getInputStream();
                String flag = new String(inputtostring(inStream));
                JSONObject jsonObject=new JSONObject(flag);
                System.out.println("flag = " + flag);
                if(jsonObject.getString("code").equals("200")) {
                    //用于获得收藏的商铺的id
                    if(shopId.equals("")){
                        System.out.println("开始执行第一种线程");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int shop_id_i = jsonArray.getInt(i);
                            System.out.println("shop_id_i="+String.valueOf(shop_id_i));
                            collectionThread shopInfothread = new collectionThread(String.valueOf(shop_id_i));
                            Thread thread_i = new Thread(shopInfothread);
                            shopInfothread.start();
                        }
                    }
                    //用于获得已知商铺id 的是商铺信息
                    else{
                        JSONObject jsonObjectShop=jsonObject.getJSONObject("data");
                        jsonObjectShop.put("shopId",shopId);
                        Message message=new Message();
                        message.what=1;
                        message.obj=jsonObjectShop;
                        shop_info_handler.sendMessage(message);
                        System.out.println("开始获取商铺信息");
                    }

                }
                //如果出错，则启用错误Handler
                else{
                    Message message=new Message();
                    message.what=4;
                    error_handler.sendMessage(message);
                }


                inStream.close();
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


    private Handler shop_info_handler=new Handler(){
        JSONObject jsonObject;
        @Override
        public void handleMessage(Message msg) {
            //如果what为4则说明错误
            if(msg.what==1) {
                MESSAGE="加载成功";
                System.out.println("开始加载");
                jsonObject = (JSONObject) msg.obj;
                Map<String, Object> listItem = new HashMap<String, Object>();
                try {
                    listItem.put("shopId",jsonObject.getString("shopId"));
                    listItem.put("shopName",jsonObject.getString("shopName"));
                    listItem.put("star", "店铺星级："+jsonObject.getInt("star")+"星");
                    listItem.put("average","平均消费："+ jsonObject.getDouble("average"));
                    listItem.put("address", "店铺地址："+jsonObject.getString("address"));
                    listItem.put("tel", "订餐热线："+jsonObject.getString("tel"));
                    listItems.add(listItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                ;MESSAGE="您还没有收藏的店铺!";
            }
        }
    };

    private Handler error_handler= new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==4)
                MESSAGE="您还没有收藏的店铺!";
        }
    };
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MESSAGE="请检查网络连接!";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

        //用一个加载对话框
        final ProgressDialog progressDialog = new ProgressDialog(MyCollectionActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("加载中...");
        progressDialog.show();

        //创建一个list 用于记录所有的需要加载的列表信息
        listItems = new ArrayList<Map<String,Object>>();

        //
        collectionThread cthread= null;
        try {
            cthread = new collectionThread();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Thread thread =new Thread(cthread);
        System.out.println("开始执行collection中的线程");
        cthread.start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        // On complete call either onLoginSuccess or onLoginFailed
                        Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_SHORT).show();

                        SimpleAdapter simpleAdapter = new SimpleAdapter(MyCollectionActivity.this,listItems,R.layout.my_collection_item,
                                new String[]{"shopName","star","average","address","tel"},
                                new int[]{R.id.my_collection_item_shopName,R.id.my_collection_item_shopStar,
                                        R.id.my_collection_item_shopAverage,R.id.my_collection_item_shopDress,
                                        R.id.my_collection_item_shopTel});
                        ListView list=(ListView)findViewById(R.id.my_collection_listview);
                        list.setAdapter(simpleAdapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                String shopid=(String)listItems.get(position).get("shopId");
                                Toast.makeText(MyCollectionActivity.this,shopid,Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(MyCollectionActivity.this,MerchantsActivity.class);
                                intent.putExtra("shopId",shopid);
                                startActivity(intent);
                            }
                        });



                    }
                }, 2100);
    }

}
