package com.example.apple.kitchenapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class DetailActivity extends AppCompatActivity {
    static String url = "http://115.159.212.180/API/admin/serve.php?orderId=";
    static String[] NAME = new String[100];
    static String Alldata = "";
    static int[] NUMBER = new int[100];
    static int orderid = 0;
    static int K = 0;
    static ListView listView;
    public static final int UPDATE = 1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE:
                    Alldata = msg.obj.toString();
                    try {
                        JSONObject jsonObject = new JSONObject(Alldata);
                        String code = jsonObject.getString("code");
                        String MESSAGE = jsonObject.getString("message");
                        if(code.equals("200")){
                            Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        K = intent.getIntExtra("K", 0);
        System.out.println("K = "+K);
        NAME = intent.getStringArrayExtra("name");
        NUMBER = intent.getIntArrayExtra("number");
        orderid = intent.getIntExtra("orderid",0);
        listView = (ListView) findViewById(R.id.listview2);
        MyAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    class PrThread implements Runnable {
        public PrThread() {
        }

        public void run() {
            try {

                URL url1 = new URL(url+orderid);

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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.detailitem, null);
            }
            DecimalFormat df = new DecimalFormat("###.0");
            TextView textView1 = (TextView) convertView.findViewById(R.id.y1);
            textView1.setText(NAME[position]);
            TextView textView2 = (TextView) convertView.findViewById(R.id.y2);
            textView2.setText(Integer.toString(NUMBER[position]));
            return convertView;
        }

    }
    public void fin(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认上菜");
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
                DetailActivity.this.finish();
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
