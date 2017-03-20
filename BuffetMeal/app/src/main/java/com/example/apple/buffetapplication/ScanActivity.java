package com.example.apple.buffetapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends Activity implements QRCodeView.Delegate {

    private QRCodeView mQR;
    static String url = "http://115.159.212.180/API/getShopInfo/getShopInfo.php?shopId=";
    static String Alldata = "";
    static String MESSAGE = "您的网络不稳定，请检查网络连接！";
    static String CODE = "";
    static String Shop_id = "";
    static String Desk_id = "";
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Alldata = msg.obj.toString();
                    ExcJson();
                    if(CODE.equals("200")){
                        Intent intent =new Intent(ScanActivity.this,ShopActivity.class);
                        intent.putExtra("shopid",Shop_id);
                        intent.putExtra("deskid",Desk_id);
                        startActivity(intent);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_zxing);
        MESSAGE = "您的网络不稳定，请检查网络连接！";
        mQR = (ZXingView) findViewById(R.id.zx_view);

        //设置结果处理
        mQR.setResultHandler(this);

        //开始读取二维码
        mQR.startSpot();
    }

    /**
     * 扫描二维码方法大全（已知）
     *
     * mQR.startCamera();               开启预览，但是并未开始识别
     * mQR.stopCamera();                停止预览，并且隐藏扫描框
     * mQR.startSpot();                 开始识别二维码
     * mQR.stopSpot();                  停止识别
     * mQR.startSpotAndShowRect();      开始识别并显示扫描框
     * mQR.stopSpotAndHiddenRect();     停止识别并隐藏扫描框
     * mQR.showScanRect();              显示扫描框
     * mQR.hiddenScanRect();            隐藏扫描框
     * mQR.openFlashlight();            开启闪光灯
     * mQR.closeFlashlight();           关闭闪光灯
     *
     * mQR.startSpotDelay(ms)           延迟ms毫秒后开始识别
     */

    /**
     * 扫描二维码成功
     * @param result
     */
    @Override
    public void onScanQRCodeSuccess(String result) {

        String[] sourceStrArray = result.split("-");
        Shop_id = sourceStrArray[0];
        Desk_id = sourceStrArray[1];
        PrThread shim = new PrThread();
        Thread tt = new Thread(shim);
        tt.start();
        //Toast.makeText(ScanActivity.this, result, Toast.LENGTH_SHORT).show();
        //震动
        vibrate();
        //停止预览
        mQR.stopCamera();
        this.finish();

    }

    /**
     * 打开相机出错
     */
    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(ScanActivity.this, "打开相机出错！请检查是否开启权限！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //启动相机
        mQR.startCamera();
    }

    @Override
    protected void onStop() {
        mQR.stopCamera();
        super.onStop();
    }

    private void ExcJson(){
        try {
            JSONObject jsonObject = new JSONObject(Alldata);
            CODE = jsonObject.getString("code");
            MESSAGE = jsonObject.getString("message");
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

