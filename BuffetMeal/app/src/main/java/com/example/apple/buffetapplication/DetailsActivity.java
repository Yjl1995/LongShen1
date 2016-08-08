package com.example.apple.buffetapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;

public class DetailsActivity extends AppCompatActivity {
    static String[] NAME = new String[100];
    static int[] NUMBER = new int[100];
    static double[] PRICE = new double[100];
    static int K = 0;
    static ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        K = intent.getIntExtra("K",0);
        NAME = intent.getStringArrayExtra("name");
        NUMBER = intent.getIntArrayExtra("number");
        PRICE = intent.getDoubleArrayExtra("price");
        listView = (ListView) findViewById(R.id.list_item6);
        MyAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }
    private class MyAdapter extends BaseAdapter {

        private AsyncBitmapLoader asyncBitmapLoader;

        public MyAdapter() {
            asyncBitmapLoader = new AsyncBitmapLoader();
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.detailitem, null);
            }
            DecimalFormat df = new DecimalFormat("###.0");
            TextView textView1 = (TextView) convertView.findViewById(R.id.y1);
            textView1.setText(NAME[position]);
            TextView textView2 = (TextView) convertView.findViewById(R.id.y2);
            textView2.setText("数量： "+NUMBER[position]);
            TextView textView3 = (TextView) convertView.findViewById(R.id.y3);
            textView3.setText("单价: "+"￥" + df.format(PRICE[position]));
            return convertView;
        }

    }
}
