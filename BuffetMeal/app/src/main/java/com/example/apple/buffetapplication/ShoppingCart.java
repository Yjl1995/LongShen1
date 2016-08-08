package com.example.apple.buffetapplication;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/11 0011.
 *
 * 购物车的信息
 *
 */
public class ShoppingCart implements Serializable {

    private String id;//商品id唯一
    private String title;//名称
    private String surplus;//价格
    private String Number;//购买数量

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getSurplus() {
        return surplus;
    }

    public void setSurplus(String surplus) {
        this.surplus = surplus;
    }


    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

}