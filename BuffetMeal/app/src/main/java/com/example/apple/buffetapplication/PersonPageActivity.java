package com.example.apple.buffetapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
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

public class PersonPageActivity extends AppCompatActivity {
    static String baseUrl = "http://115.159.212.180/API/login/logout.php?key=";
    static int ASK = -1;
    static String MESSAGE = "您网络不稳定，请检查网络连接！";
    static String Key = "NULL";
    class PrThread implements Runnable {


        public PrThread(){}
        public void run() {
            try {
                URL url = new URL(baseUrl +Key);
                System.out.println("url" + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(2000);
                connection.connect();
                InputStream inStream = connection.getInputStream();
                String flag = new String(inputtostring(inStream));
                System.out.println("flag = " + flag);

                JSONObject jsonObject = new JSONObject(flag);
                String code = jsonObject.getString("code");
                final String message = jsonObject.getString("message");
                System.out.println("code = "+code);
                System.out.println("message = "+message);

                if (code.equals("200")) {
                    {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //更新UI
                                ASK = 1;
                                MESSAGE = message;
                            }

                        });
                    }
                } else {
                    {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //更新UI
                                ASK = 0;
                                MESSAGE = message;
                            }

                        });
                    }
                }
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
    //inputtostring
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_page);
        Intent intent = getIntent();
        String KEY = intent.getStringExtra("KEY");
        Key = KEY;
        Util.putValue(this,"KEY",Key);
    }




    public void ToLogin_or_up(View v){

        Util.putBooleanValue(this,
                "LoginState", false);

        final ProgressDialog progressDialog = new ProgressDialog(PersonPageActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        PrThread shim = new PrThread();
        Thread t = new Thread(shim);
        t.start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (ASK == 1) {
                            Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();
                            onLogoutSuccess();
                        } else {
                            onLogoutFailed();
                        }

                    }
                }, 2100);
    }

    public void onLogoutSuccess() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onLogoutFailed() {
        Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();

    }
}