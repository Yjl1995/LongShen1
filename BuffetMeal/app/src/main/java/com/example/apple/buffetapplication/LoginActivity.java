package com.example.apple.buffetapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    static String baseUrl = "http://115.159.212.180/API/login/login.php?";
    static int ASK = -1;
    static String MESSAGE = "您网络不稳定，请检查网络连接！";
    static String KEY = "key";
    static String NICK_NAME = "nick_name";
    static String EMAIL = "email";
    static String IMAGE_URL = "image_url";

    class PrThread implements Runnable{
        private String _name;
        private String _password;


        public PrThread(String name,String password){
            _name = name;
            _password = password;

        }
        public void run(){
            try{
                URL url = new URL(baseUrl+"username="+_name+"&password="+_password);
                System.out.println("url"+url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(1000);
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
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                    final String key = jsonObject1.getString("key");
                    final String nick_name = jsonObject1.getString("nick_name");
                    final String email = jsonObject1.getString("email");
                    final String image_url = jsonObject1.getString("image_url");
                    System.out.println("key = "+key);
                    System.out.println("nick_name = "+nick_name);
                    System.out.println("email = "+email);
                    System.out.println("Image_url = "+image_url);

                        {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //更新UI
                                ASK = 1;
                                MESSAGE = message;
                                KEY = key;
                                NICK_NAME = nick_name;
                                EMAIL = email;
                                IMAGE_URL = image_url;

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

    //main
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ASK = -1;
        MESSAGE = "您网络不稳定，请检查网络连接！";
        if(Util.getBooleanValue(LoginActivity.this,"LoginState")==true){
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, NewMainActivity.class);
            intent.putExtra("KEY", Util.getValue(this, "KEY"));
            intent.putExtra("NICK_NAME",Util.getValue(this,"NICK_NAME"));
            intent.putExtra("EMAIL",Util.getValue(this,"EMAIL"));
            intent.putExtra("IMAGE_URL", Util.getValue(this, "IMAGE_URL"));
            System.out.println("图片下载地址："+Util.getValue(this,"IMAGE_URL"));
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            if(Util.hasValue(LoginActivity.this,"NAME")) {
                _usernameText.setText(Util.getValue(LoginActivity.this, "NAME"));
            }
            _loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    login();
                }
            });

            _signupLink.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Start the Signup activity
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }
            });
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            _loginButton.setEnabled(true);
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        PrThread shim = new PrThread(username,password);
        Thread t = new Thread(shim);
        t.start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        // On complete call either onLoginSuccess or onLoginFailed
                        if(ASK==1) {
                            Toast.makeText(getBaseContext(),MESSAGE,Toast.LENGTH_LONG).show();
                            onLoginSuccess();
                        }
                        else{
                            onLoginFailed();
                        }


                        // onLoginFailed();

                    }
                }, 1100);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        String username=_usernameText.getText().toString();
        String password=_passwordText.getText().toString();
        //存储用户数据
        Util.putBooleanValue(LoginActivity.this,
                "LoginState", true);
        Util.putValue(LoginActivity.this, "NAME", username);
        Util.putValue(LoginActivity.this, "PWD",
                password);
        Intent intent = new Intent(this,NewMainActivity.class);
        intent.putExtra("KEY", KEY);
        intent.putExtra("NICK_NAME",NICK_NAME);
        intent.putExtra("EMAIL",EMAIL);
        intent.putExtra("IMAGE_URL",IMAGE_URL);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();


        if (username.isEmpty() || username.length() < 6 ||username.length() > 16) {
            _usernameText.setError("用户名应该有6到16位！");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            _passwordText.setError("密码应该有6到16位！");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
