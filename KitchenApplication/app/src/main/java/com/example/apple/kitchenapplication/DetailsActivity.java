package com.example.apple.kitchenapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by ningdaihao on 2016/9/8.
 */
public class DetailsActivity extends AppCompatActivity {
    static String[] NAME = new String[1000];
    static int[] NUMBER = new int[1000];
    static int K = 0;
    static String Alldata = "";
    static double total = 0;
    static String shopid = "shopid";
    static int deskid = 0;
    static ListView listView;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Alldata = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(Alldata);
                        String code = jsonObject.getString("code");
                        String MESSAGE = jsonObject.getString("message");
                        if(code.equals("200")){
                            DetailsActivity.this.finish();
                            Toast.makeText(getBaseContext(), "交易完成", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getBaseContext(), "交易失败", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
            try {

                URL url1 = new URL("http://115.159.212.180/API/admin/payOrders.php?shopId="+shopid+"&deskId="+Integer.toString(deskid));

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        K = intent.getIntExtra("K", 0);
        NAME = intent.getStringArrayExtra("name");
        NUMBER = intent.getIntArrayExtra("number");
        total = intent.getDoubleExtra("total", 0);
        shopid = intent.getStringExtra("shopid");
        deskid = intent.getIntExtra("deskid",0);
        listView = (ListView) findViewById(R.id.list_item6);
        MyAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);
        DecimalFormat df = new DecimalFormat("###.0");
        TextView textView = (TextView) findViewById(R.id.shoppingcart_account_describe_tv);
        textView.setText("共" + Integer.toString(K)+"道菜，总计"+df.format(total)+"元");
    }
    private class MyAdapter extends BaseAdapter {


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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ditem, null);
            }
            DecimalFormat df = new DecimalFormat("###.0");
            TextView textView1 = (TextView) convertView.findViewById(R.id.y11);
            textView1.setText(NAME[position]);
            TextView textView2 = (TextView) convertView.findViewById(R.id.y12);
            textView2.setText("数量： "+NUMBER[position]);
            return convertView;
        }

    }
    public void OK(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认交易");
        setPositiveButton(builder);
        setNegativeButton(builder);
        builder.create();
        builder.show();
    }
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder){
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrThread shim1 = new PrThread();
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
}
