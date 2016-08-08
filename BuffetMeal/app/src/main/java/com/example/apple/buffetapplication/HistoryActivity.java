package com.example.apple.buffetapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

public class HistoryActivity extends AppCompatActivity {
    static String url = "http://115.159.212.180/API/orders/getOrderList.php?userName=";
    private ListView listview;
    static String Alldata = "";
    static String[] date = new String[100];
    static String[] Name = new String[100];
    static double[] total = new double[100];
    static String[][] name1 = new String[100][100];
    static int[][] number = new int[100][100];
    static double[][] price = new double[100][100];
    static int K = 0;
    static int[] KK = new int[100];
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
            try {

                URL url1 = new URL(url+Util.getValue(HistoryActivity.this,"NAME"));

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        PrThread shim = new PrThread();
        Thread tt = new Thread(shim);
        tt.start();
        //用一个加载对话框
        final ProgressDialog progressDialog = new ProgressDialog(HistoryActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("加载中...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();

                    }
                }, 1100);
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.histoy_item, null);
            }
            DecimalFormat df = new DecimalFormat("###.0");
            TextView textView1 = (TextView) convertView.findViewById(R.id.date);
            textView1.setText("订单时间："+date[position]);
            TextView textView2 = (TextView) convertView.findViewById(R.id.shop);
            textView2.setText("餐厅名称："+Name[position]);
            TextView textView3 = (TextView) convertView.findViewById(R.id.total);
            textView3.setText("共消费：" + df.format(total[position])+"￥");
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
                Name[i] = x.getString("shopName");
                System.out.println("shopName = "+Name[i]);
                date[i] = x.getString("submitTime");
                total[i] = x.getDouble("total");
                JSONArray x1 = x.getJSONArray("foodList");
                KK[i] = x1.length();
                for(int j = 0;j < x1.length();j++){
                    JSONObject q1 = x1.getJSONObject(j);
                    name1[i][j] = q1.getString("foodName");
                    price[i][j] = q1.getDouble("price");
                    number[i][j] = q1.getInt("foodNum");
                }
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
            Intent intent = new Intent(HistoryActivity.this,DetailsActivity.class);
            intent.putExtra("name",name1[arg2]);
            intent.putExtra("number",number[arg2]);
            intent.putExtra("price",price[arg2]);
            intent.putExtra("K",KK[arg2]);
            startActivity(intent);
        }
    }

}
