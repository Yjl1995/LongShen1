package com.example.apple.kitchenapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import java.io.SyncFailedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    static String url = "http://115.159.212.180/API/admin/getOrders.php?shopId=";
    static String callurl = "http://115.159.212.180/API/admin/getCallWaiterList.php?shopId=";
    private ListView listview;
    static String Alldata = "";
    static String Alldata1 = "";
    static String[] date = new String[100];
    static int[] deskid = new int[100];
    static int[] orderid = new int[100];
    static double[] total = new double[100];
    static String[][] name1 = new String[100][100];
    static int[][] number = new int[100][100];
    static int K = 0;
    static int K1 = 0;
    static int[] mm = new int[100];
    static int[] KK = new int[100];
    static String shopid = "shopid";
    static String shopname = "shopname";
    public static final int UPDATE = 1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    Alldata = msg.obj.toString();
                    exjson();
                    listview = (ListView) findViewById(R.id.listview1);
                    MyAdapter adapter = new MyAdapter();
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new ItemClickEvent());
                    break;
                default:
                    break;
            }
        }

    };

    private Handler cHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    String CALL = "请注意 ";
                    Alldata1 = msg.obj.toString();
                    ecjson();
                    if(K1!=0)
                    {
                        for(int i=0;i<K1-1;i++)
                        {
                            CALL = CALL+Integer.toString(mm[i])+"、";
                        }
                        CALL = CALL+Integer.toString(mm[K1-1]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("提示")
                                .setMessage(CALL+"桌呼叫服务员！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.create().show();
                    }
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

                    URL url1 = new URL(url+shopid);

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

    class PcThread implements Runnable {
        public PcThread() {
        }

        public void run() {
            while (true) {
                try {

                    URL url1 = new URL(callurl+shopid);

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
                    cHandler.sendMessage(message);
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
        setContentView(R.layout.activity_main);
        shopid = Util.getValue(MainActivity.this, "SHOP_ID");
        shopname = Util.getValue(MainActivity.this, "SHOP_NAME");
        TextView textView11 = (TextView) findViewById(R.id.dian);
        textView11.setText(shopname);
        PcThread call = new PcThread();
        PrThread shim = new PrThread();
        Thread cc = new Thread(call);
        Thread tt = new Thread(shim);
        cc.start();
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null);
            }
            DecimalFormat df = new DecimalFormat("###.0");
            TextView textView1 = (TextView) convertView.findViewById(R.id.text2);
            textView1.setText(date[position]);
            TextView textView2 = (TextView) convertView.findViewById(R.id.text1);
            textView2.setText(Integer.toString(deskid[position]));
            TextView textView3 = (TextView) convertView.findViewById(R.id.text3);
            textView3.setText(df.format(total[position])+"￥");
            return convertView;
        }

    }
    public void exjson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata);
            JSONArray data = jsonObject.getJSONArray("data");
            K = data.length();
            System.out.println("K = " + K);
            for(int i = 0;i < data.length();i++){
                JSONObject x = data.getJSONObject(i);
                date[i] = x.getString("submitTime");
                total[i] = x.getDouble("total");
                deskid[i] = x.getInt("deskId");
                orderid[i] = x.getInt("orderId");
                JSONArray x1 = x.getJSONArray("foodList");
                KK[i] = x1.length();
                for(int j = 0;j < x1.length();j++){
                    JSONObject q1 = x1.getJSONObject(j);
                    name1[i][j] = q1.getString("foodName");
                    number[i][j] = q1.getInt("foodNum");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void ecjson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata1);
            JSONArray data = jsonObject.getJSONArray("data");
            K1 = data.length();
            System.out.println("K1 = " + K1);
            for(int i = 0;i < data.length();i++){
                mm[i] = data.getInt(i);
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
            Intent intent = new Intent(MainActivity.this,DetailActivity.class);
            intent.putExtra("name",name1[arg2]);
            intent.putExtra("number",number[arg2]);
            intent.putExtra("K",KK[arg2]);
            intent.putExtra("orderid",orderid[arg2]);
            startActivity(intent);
        }
    }
    public void to2(View view){
        Intent intent = new Intent(MainActivity.this,Main2Activity.class);
        intent.putExtra("shopid",shopid);
        startActivity(intent);
    }
}
