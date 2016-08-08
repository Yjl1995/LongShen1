package com.example.apple.buffetapplication;

/**
 * Created by ningdaihao on 2016/8/2.
 */
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ningdaihao on 2016/8/2.
 */
public class MyAdapter extends PagerAdapter {
    private List<ImageView> data;
    Context context;
    public MyAdapter(List<ImageView> data,Context context) {
        this.data=data;
        this.context=context;
    }

    @Override
    public int getCount() {
        //返回一个无穷大的值，
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0==arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //注意，这里什么也不做!!!

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView image=data.get(position%data.size());
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp=image.getParent();
        if(vp!=null){
            ViewGroup vg=(ViewGroup) vp;
            vg.removeView(image);
        }
        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "点击了图片", Toast.LENGTH_SHORT).show();
            }
        });
        container.addView(data.get(position%data.size()));
        return data.get(position%data.size());
    }


}