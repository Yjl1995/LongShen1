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

public class SignupActivity extends AppCompatActivity {
    static int ASK = -1;
    static String MESSAGE = "您网络不稳定，请检查网络连接！";
    static String baseUrl = "http://115.159.212.180/API/register/register.php?";
    class PrThread implements Runnable{
        String _nickname = null;
        String _emailpos = null;
        String _emailpre = null;
        String _password = null;
        String _username = null;

        public PrThread(String username,String password,String emailpre,String emailpos,String nickname){
            _nickname = nickname;
            _emailpos = emailpos;
            _emailpre = emailpre;
            _password = password;
            _username = username;
        }
        public void run(){
            try{
                System.out.println("run start");
                URL url = new URL(baseUrl+"username="+_username+"&password="+_password
                        +"&emailpre="+_emailpre+"&emailpos="+_emailpos+"&nickname="+_nickname+
                        "&key=25678C5B1288AA70");
                System.out.println("url" + url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(2000);
                connection.connect();
                System.out.println("开始连接");
                InputStream inStream = connection.getInputStream();
                System.out.println("连接成功");
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

    //main
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_nickname) EditText _nicknameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_password2)EditText _password2Text;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            _signupButton.setEnabled(true);
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String nickname = _nicknameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String password2 = _password2Text.getText().toString();

        // TODO: Implement your own signup logic here.
        //signup logic below
        int k = email.indexOf("@");
        String emailpre = email.substring(0, k - 1);
        String emailpos = email.substring(k + 1, email.length() );
        PrThread shim = new PrThread(username, password,emailpre,emailpos,nickname);
        Thread t = new Thread(shim);
        t.start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        progressDialog.dismiss();

                        System.out.println("ask="+ASK);
                        if(ASK==1) {
                            Toast.makeText(getBaseContext(),MESSAGE,Toast.LENGTH_LONG).show();
                            onSignupSuccess();
                        }
                        else {
                            onSignupFailed();
                        }
                        // onSignupFailed();

                    }
                }, 2100);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent =new Intent(this,LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), MESSAGE, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String nickname = _nicknameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String password2 = _password2Text.getText().toString();

        if (username.isEmpty() || username.length() < 6||username.length()>16) {
            _usernameText.setError("用户名应该有6到16位！");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (nickname.isEmpty()) {
            _usernameText.setError("昵称不能为空！");
            valid = false;
        } else {
            _nicknameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("请输入正确的Email！");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            _passwordText.setError("密码应该有6到16位！");
            valid = false;
        } else if (!password.equals(password2)){
            _password2Text.setError("输入密码不相同！");
            valid = false;
        } else
         {
            _passwordText.setError(null);
        }

        return valid;
    }
}