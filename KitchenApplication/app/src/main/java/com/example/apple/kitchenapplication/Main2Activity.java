package com.example.apple.kitchenapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import java.text.DecimalFormat;

public class Main2Activity extends AppCompatActivity {
    static String url = "http://115.159.212.180/API/admin/getNoPay.php?shopId=";
    private ListView listview;
    static String Alldata = "";
    static String shop_id = "shopid";
    static double[] total_price = new double[300];
    static int[] desk_id = new int[300];
    static String[] time = new String[300];
    static int[][] foodNum = new int[300][100];
    static String[][] foodName = new String[300][100];
    static int K = 0;
    static int[] KK = new int[300];
    public static final int UPDATE = 1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    Alldata = msg.obj.toString();
                    exjson();
                    listview = (ListView) findViewById(R.id.list_item5);
                    MyAdapter adapter = new MyAdapter();
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new ItemClickEvent());
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
            while (true) {
                try {
                    URL url1 = new URL(url+shop_id);
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
                    Thread.sleep(3000);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        shop_id = intent.getStringExtra("shopid");
        PrThread shim = new PrThread();
        Thread tt = new Thread(shim);
        tt.start();
    }
    private class MyAdapter extends BaseAdapter {



        public MyAdapter() {

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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.main2item, null);
            }
            DecimalFormat df = new DecimalFormat("###.0");
            TextView textView1 = (TextView) convertView.findViewById(R.id.time1);
            textView1.setText("订单时间"+time[position]);
            TextView textView2 = (TextView) convertView.findViewById(R.id.deskid);
            textView2.setText("桌号"+Integer.toString(desk_id[position]));
            TextView textView3 = (TextView) convertView.findViewById(R.id.total);
            textView3.setText("总价"+df.format(total_price[position])+"￥");
            return convertView;
        }

    }
    public void exjson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata);
            JSONArray data = jsonObject.getJSONArray("data");
            K = data.length();
            for(int i = 0;i < data.length();i++){
                JSONObject x = data.getJSONObject(i);
                desk_id[i] = x.getInt("desk_id");
                total_price[i] = x.getDouble("total_price");
                JSONArray o1 = x.getJSONArray("desk_order");
                int num = 0;
                for(int t = 0;t < o1.length();t++){
                    JSONObject w1 = o1.getJSONObject(t);
                    if(t == 0) time[i] = w1.getString("submit_time");
                    JSONArray x1 = w1.getJSONArray("food_list");
                    System.out.println("KK = "+KK[i]);
                    for(int j = 0;j < x1.length();j++){
                        JSONObject q1 = x1.getJSONObject(j);
                        foodName[i][num] = q1.getString("foodName");
                        foodNum[i][num] = q1.getInt("foodNum");
                        num++;
                    }

                }
                System.out.println("num = "+num);
                KK[i] = num;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static String inputtostring(InputStream in_st) {
        BufferedReader in = new BufferedReader(new InputStreamReader(in_st));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
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
            Intent intent = new Intent(Main2Activity.this,DetailsActivity.class);
            intent.putExtra("name",foodName[arg2]);
            intent.putExtra("number",foodNum[arg2]);
            intent.putExtra("K",KK[arg2]);
            intent.putExtra("total",total_price[arg2]);
            intent.putExtra("shopid",shop_id);
            intent.putExtra("deskid",desk_id[arg2]);
            startActivity(intent);
        }
    }

}
