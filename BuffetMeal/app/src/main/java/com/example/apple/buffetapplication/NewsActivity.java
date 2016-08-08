package com.example.apple.buffetapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

public class NewsActivity extends AppCompatActivity {
    static String title = "";
    static String short_title = "";
    static String content = "";
    static String imageUrl = "";
    static String news_from = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        short_title = intent.getStringExtra("short_title");
        content = intent.getStringExtra("content");
        imageUrl = intent.getStringExtra("imageUrl");
        news_from = intent.getStringExtra("news_from");
        TextView textView1 = (TextView) findViewById(R.id.title);
        textView1.setText(title);
        TextView textView2 = (TextView) findViewById(R.id.short_title);
        textView2.setText(short_title);
        TextView textView3 = (TextView) findViewById(R.id.text);
        textView3.setText(content);
        TextView textView4 = (TextView) findViewById(R.id.from);
        textView4.setText(news_from);
        Uri urii1 = Uri.parse(imageUrl);
        SimpleDraweeView draweeViewq1 = (SimpleDraweeView) findViewById(R.id.image);
        draweeViewq1.setImageURI(urii1);
    }
}
