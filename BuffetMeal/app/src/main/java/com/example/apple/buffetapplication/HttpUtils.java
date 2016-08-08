package com.example.apple.buffetapplication;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by apple on 2016/8/1.
 */
public class HttpUtils {
    public static InputStream getStreamFromURL(String imageURL) {
        InputStream in=null;
        try {
            URL url=new URL(imageURL);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            in=connection.getInputStream();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return in;

    }
}
