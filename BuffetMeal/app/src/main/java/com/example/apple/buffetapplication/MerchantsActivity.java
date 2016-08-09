package com.example.apple.buffetapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

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

public class MerchantsActivity extends AppCompatActivity {
    static String Shop_id;
    static String Alldata = "";
    static String CODE = "";
    static String MESSAGE = "";
    static String url = "http://115.159.212.180/API/getShopInfo/getShopInfo.php?shopId=";
    static String shopName = "";
    static String shopInfo = "";
    static String imageUrl = "";
    static String address = "";
    static String tel = "";
    static double average =0.0;
    static int star = 0;
    static int service = 0;
    static int speed = 0;
    static int environment = 0;
    static int cheap_fine = 0;

    class PrThread implements Runnable {
        public PrThread() {
        }
        public void run() {
            try {
                URL url1 = new URL(url+Shop_id);

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


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Alldata = msg.obj.toString();
                    System.out.println("所有数据="+Alldata);
                    ExcJson();
                    if(CODE.equals("200")){
                        SimpleDraweeView imageView = (SimpleDraweeView) findViewById(R.id.shopP);
                        TextView shopname = (TextView) findViewById(R.id.shopN);
                        TextView shopaddress = (TextView) findViewById(R.id.shopAd);
                        TextView shoptel = (TextView) findViewById(R.id.shopTel);
                        TextView shopaverage = (TextView) findViewById(R.id.shopAve);
//                        TextView shopstar = (TextView) findViewById(R.id.shopStar);
                        RatingBar shopStar = (RatingBar) findViewById(R.id.shopStar);
                        shopStar.setRating((float) star);

                        TextView shopinfo = (TextView) findViewById(R.id.shopInfo);
//                        TextView shopenvironment = (TextView) findViewById(R.id.shopEn);
//                        TextView shopspeed = (TextView) findViewById(R.id.shopSpeed);
//                        TextView shopservice = (TextView) findViewById(R.id.shopSever);

                        RatingBar shopSever = (RatingBar) findViewById(R.id.shopSever);
                        shopSever.setRating((float) service);

                        RatingBar shopSpeed = (RatingBar) findViewById(R.id.shopSpeed);
                        shopSpeed.setRating((float) speed);

                        RatingBar shopEn = (RatingBar) findViewById(R.id.shopEn);
                        shopEn.setRating((float) environment);

                        RatingBar shopGood = (RatingBar) findViewById(R.id.shopGood);
                        shopGood.setRating((float) cheap_fine);

                        //TextView shopgood = (TextView) findViewById(R.id.shopGood);
                        Uri uri = Uri.parse(imageUrl);
                        imageView.setImageURI(uri);
                        shopname.setText(shopName);
                        shopaddress.setText(address);
                        shoptel.setText(tel);
                        shopaverage.setText("平均消费："+Double.toString(average)+"元");
//                        shopstar.setText("总评分："+Integer.toString(star));
                        shopinfo.setText("    "+shopInfo);
//                        shopenvironment.setText("餐厅环境："+Integer.toString(environment));
//                        shopspeed.setText("上菜速度："+Integer.toString(speed));
//                        shopservice.setText("服务质量："+Integer.toString(service));
//                        shopgood.setText("菜品反馈："+Integer.toString(cheap_fine));
                    }
                    else{
                        Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_merchants);
        Intent intent = getIntent();
        Shop_id = intent.getStringExtra("shopId");
        PrThread shim = new PrThread();
        Thread tt = new Thread(shim);
        tt.start();

    }

    private void ExcJson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata);
            CODE = jsonObject.getString("code");
            MESSAGE = jsonObject.getString("message");
            JSONObject jsondata = jsonObject.getJSONObject("data");
            shopName = jsondata.getString("shopName");
            shopInfo = jsondata.getString("shopInfo");
            imageUrl = jsondata.getString("imageUrl");
            address = jsondata.getString("address");
            tel = jsondata.getString("tel");
            average = jsondata.getDouble("average");
            star = jsondata.getInt("star");
            service = jsondata.getInt("service");
            speed = jsondata.getInt("speed");
            environment = jsondata.getInt("environment");
            cheap_fine = jsondata.getInt("cheap_fine");

        } catch (JSONException e) {
            e.printStackTrace();
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
}
