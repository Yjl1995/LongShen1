package com.example.apple.buffetapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2016/1/11 0011.
 * <p/>
 * 购物车适配器
 */
public class ShoppingCartAdapter extends BaseAdapter {

    private Cursor cursor;
    SQLiteDatabase db;


    static int pick = 0;

    public static List<ShoppingCart> lists;
    private Context context;

    private int position;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private goodsAddShoppingCartInterface goodsAddShoppingCartInterface = null;//
    private OnListRemovedListener mListener;

    public ShoppingCartAdapter(Context context, List<ShoppingCart> lists) {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.apple.buffetapplication/yjl.db", null);
        this.context = context;
        this.lists = lists;

        //保存数量到SharedPreferences里面
        sp = context.getSharedPreferences("shopping", 0);
        editor = sp.edit();

        //监听数量变动，价格跟着变动
        this.goodsAddShoppingCartInterface = new goodsAddShoppingCartInterface() {
            @Override
            public void clickDetial(List<ShoppingCart> lists) {

            }

        };

    }

    //商品数量添加时价格变动监听
    public interface goodsAddShoppingCartInterface {

        void clickDetial(List<ShoppingCart> lists);//修改价格监听

    }
    //设置监听
    public void setgoodsAddShoppingCartInterface(goodsAddShoppingCartInterface Listener) {
        goodsAddShoppingCartInterface = Listener;
    }

    //删除回调监听
    public void setOnListRemovedListener(OnListRemovedListener listener) {
        this.mListener = listener;
    }

    //删除操作回调
    public interface OnListRemovedListener {
        public void onRemoved(List<ShoppingCart> lists,int position, int shopNumber);
    }




    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {

        this.position = position;
        final ViewHolder holder;


        if (v == null) {

            //初始化控件
            v = LayoutInflater.from(context).inflate(R.layout.shopping_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) v.findViewById(R.id.item_title);
            holder.surplus = (TextView) v.findViewById(R.id.item_surplus);
            holder.number = (TextView) v.findViewById(R.id.item_number);
            holder.iv_cancel = (ImageView) v.findViewById(R.id.item_cancel);
            holder.iv_plus = (ImageView) v.findViewById(R.id.iv_plus);
            holder.iv_less = (ImageView) v.findViewById(R.id.iv_less);
            v.setTag(holder);

        } else {

            holder = (ViewHolder) v.getTag();

        }


        ShoppingCart sc = lists.get(position);

        holder.title.setText(sc.getTitle());//设置标题
        holder.surplus.setText("单价："+sc.getSurplus()+"元");//设置剩余数

        holder.number.setTag(position);//设置标签，以后可以用getTag()将这个数据取出来。

        if (!sp.getString(sc.getId(), "").equals("")) { //判断数量是否已经有存储过了，没有就默认为1

            holder.number.setText(sc.getNumber());

        } else {

            holder.number.setText(sc.getNumber());

        }

        //设置number的监听事件，弹出框输入数量
        holder.number.setOnClickListener(new ShopNumberClickListener());

        //删除
        holder.iv_cancel.setOnClickListener(new MyOnClickListener(holder) {
            @Override
            public void onClick(View v, ViewHolder holder) {

                if (mListener != null) {
                    String id = lists.get(position).getId();
                    db.delete("meal","foodid=?",new String[]{id});
                    mListener.onRemoved(lists,position, Integer.valueOf(holder.number.getText().toString()));  //通知主线程更新Adapter
                }

            }
        });


        //加按钮
        holder.iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = lists.get(position).getId();
                Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{id}, null, null, null);
                cursor.moveToFirst();
                ContentValues contentValues = new ContentValues();
                contentValues.put("number",cursor.getInt(0)+1);
                int flat1 = db.update("meal",contentValues,"foodid = ?",new String[]{id});


                int i = Integer.valueOf(holder.number.getText().toString());
                i++;
                holder.number.setText(i + "");
                lists.get(position).setNumber(i + "");
                editor.putString(lists.get(position).getId(), i + "").commit();//保存数量
                goodsAddShoppingCartInterface.clickDetial(lists);//通知更新





            }
        });

        //减按钮
        holder.iv_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = lists.get(position).getId();
                Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{id}, null, null, null);
                cursor.moveToFirst();
                ContentValues contentValues = new ContentValues();

                int i = Integer.valueOf(holder.number.getText().toString());
                if(i>1){
                    contentValues.put("number",cursor.getInt(0)-1);
                    int flat1 = db.update("meal",contentValues,"foodid = ?",new String[]{id});

                    i--;
                    holder.number.setText(i + "");
                    lists.get(position).setNumber(i + "");
                    editor.putString(lists.get(position).getId(), i + "").commit();
                    goodsAddShoppingCartInterface.clickDetial(lists);//通知更新

                }



            }
        });



        return v;

    }


    public class ViewHolder {

        public ImageView image;//左边图片
        public TextView title;//标题
        public TextView surplus;//剩余数
        public ImageView iv_cancel;//删除订单
        public TextView number;//输入数量
        public ImageView iv_plus;//加按钮
        public ImageView iv_less;//减按钮

    }

    //数量TextView点击监听器
    private final class ShopNumberClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //获取商品的数量
            String str = ((TextView)v).getText().toString();
            int shopNum = Integer.valueOf(str);
            showDialog(shopNum,(TextView)v);
        }
    }


    private int count = 0;			//记录对话框中的数量
    private EditText editText;		//对话框中数量编辑器
    /**
     * 弹出对话框更改商品的数量
     * @param shopNum	商品原来的数量
     * @param textNum	Item中显示商品数量的控件
     */
    private void showDialog(int shopNum,final TextView textNum){
        View view = LayoutInflater.from(context).inflate(R.layout.update_number, null);
        Button btnSub = (Button)view.findViewById(R.id.numSub);
        Button btnAdd = (Button)view.findViewById(R.id.numAdd);
        editText = (EditText)view.findViewById(R.id.edt);
        editText.setText(String.valueOf(shopNum));
        count = shopNum;
        pick = (Integer)textNum.getTag();
        btnSub.setOnClickListener(new ButtonClickListener());
        btnAdd.setOnClickListener(new ButtonClickListener());
        new AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //将用户更改的商品数量更新到服务器
                        int position = (Integer)textNum.getTag();

                        if(editText.getText().toString().equals("")){
                            return;
                        }

                        count = Integer.valueOf(editText.getText().toString());

                        if(count < 1){

                            Toast.makeText(context, "数量不能小于1", Toast.LENGTH_LONG).show();

                        }else {

                            String id = lists.get(position).getId();
                            Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{id}, null, null, null);
                            cursor.moveToFirst();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("number", count);
                            int flat1 = db.update("meal", contentValues, "foodid = ?", new String[]{id});

                            lists.get(position).setNumber(count + "");//更改保存
                            editor.putString(lists.get(position).getId(), count+"").commit();
                            handler.sendMessage(handler.obtainMessage(1, textNum));
                            goodsAddShoppingCartInterface.clickDetial(lists);//通知更新
                        }
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){		//更改商品数量
                ((TextView)msg.obj).setText(String.valueOf(count));

            }else if(msg.what == 2){

                //更改对话框中的数量
                editText.setText(String.valueOf(count));

            }
        }
    };


    //Button点击监听器
    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.numSub){

                if(count > 1){

                    String id = lists.get(pick).getId();
                    Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{id}, null, null, null);
                    cursor.moveToFirst();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("number", count-1);
                    int flat1 = db.update("meal", contentValues, "foodid = ?", new String[]{id});

                    count--;
                    handler.sendEmptyMessage(2);

                }

            }else if(v.getId() == R.id.numAdd){
                String id = lists.get(pick).getId();
                Cursor cursor = db.query("meal", new String[]{"number"}, "foodid=?", new String[]{id}, null, null, null);
                cursor.moveToFirst();
                ContentValues contentValues = new ContentValues();
                contentValues.put("number", count+1);
                int flat1 = db.update("meal", contentValues, "foodid = ?", new String[]{id});

                count++;
                handler.sendEmptyMessage(2);

            }
        }
    }


    //删除
    private abstract class MyOnClickListener implements View.OnClickListener {

        private ViewHolder mHolder;

        public MyOnClickListener(ViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, ViewHolder holder);

    }


}