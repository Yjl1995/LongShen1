package com.example.apple.buffetapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;

public class OrderActivity extends AppCompatActivity {
    static String url = "http://115.159.212.180/API/orders/getNoPayment.php?userName=";
    private ListView listview;
    static String Alldata = "";
    static int K = 0;
    static int KK = 0;
    static int[] orderid = new int[100];
    static double[] total = new double[100];
    static int[] foodnum = new int[150];
    static String[] foodName = new String[150];
    static double[] price = new double[150];

    public static final int UPDATE = 1;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    Alldata = msg.obj.toString();
                    exjson();
                    double sum = 0;
                    for(int i = 0;i < KK;i++){
                        sum = sum+total[i];
                    }
                    TextView textView1 = (TextView) findViewById(R.id.heji);
                    DecimalFormat df = new DecimalFormat("###.0");
                    textView1.setText("￥"+df.format(sum));
                    listview = (ListView) findViewById(R.id.orderlist);
                    MyAdapter adapter = new MyAdapter();
                    listview.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }

    };

    private Handler YHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    OrderActivity.this.finish();
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

                URL url1 = new URL(url+Util.getValue(OrderActivity.this,"NAME"));

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
        setContentView(R.layout.activity_order);
        K = 0;
        KK = 0;
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.order_item, null);
            }

            TextView textView1 = (TextView) convertView.findViewById(R.id.order1);
            textView1.setText(foodName[position]);
            TextView textView2 = (TextView) convertView.findViewById(R.id.order2);
            DecimalFormat df = new DecimalFormat("###.0");

            textView2.setText(df.format(price[position]));
            TextView textView3 = (TextView) convertView.findViewById(R.id.order3);
            textView3.setText(Integer.toString(foodnum[position]));
            TextView textView4 = (TextView) convertView.findViewById(R.id.order4);
            textView4.setText(df.format(foodnum[position]*price[position]));
            return convertView;
        }

    }
    public void exjson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata);
            System.out.println("alldata = "+Alldata);
            int code = jsonObject.getInt("code");
            if(code == 200){
                JSONArray data = jsonObject.getJSONArray("data");
                KK = data.length();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject x = data.getJSONObject(i);
                    orderid[i] = x.getInt("orderId");
                    total[i] = x.getDouble("total");
                    JSONArray x1 = x.getJSONArray("foodList");
                    for (int j = 0; j < x1.length(); j++) {
                        JSONObject q1 = x1.getJSONObject(j);
                        foodName[K] = q1.getString("foodName");
                        price[K] = q1.getDouble("price");
                        foodnum[K] = q1.getInt("foodNum");
                        K++;
                    }
                }
            }
            else{
                K = 0;
                KK = 0;
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

    class PPThread implements Runnable {
        public PPThread() {
        }
        public void run() {
            try {
                String json = changeJson();
                String url = "http://115.159.212.180/API/orders/payOrders.php";
                URL url1 = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
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
                if(code.equals("200")){
                    Message message1 = new Message();
                    message1.what = UPDATE;
                    YHandler.sendMessage(message1);
                }
                inStream.close();
                outputStream.close();
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
    public  String changeJson(){
        try {

            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            String username = Util.getValue(this, "NAME");
            object.put("username", username);
            for(int i = 0;i < KK;i++){
                array.put(orderid[i]);
            }
            object.put("order_id",array);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void pay(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认结算");
        setPositiveButton(builder);
        setNegativeButton(builder);
        builder.create();
        builder.show();
    }
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder){
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PPThread shim1 = new PPThread();
                Thread tt1 = new Thread(shim1);
                tt1.start();
            }
        });
    }
    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder){
        return builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
}
