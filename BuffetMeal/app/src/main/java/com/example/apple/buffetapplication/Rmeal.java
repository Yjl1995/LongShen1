package com.example.apple.buffetapplication;

/**
 * Created by apple on 2016/8/3.
 */
public class Rmeal {
    public Rmeal(double x1,String x2,String x3,double x4,int x5,String x6,int x7,int x8){
        price = x1;
        food_name = x2;
        introduce = x3;
        price2 = x4;
        number = x5;
        image = x6;
        star = x7;
        food_id = x8;
    }
    public String food_name = "";
    public int food_id = 0;
    public double price = 0;
    public String introduce = "";
    public double price2 = 0;
    public int number = 0;
    public String image = "";
    public int star = 0;
    public void putdata(String s1,String s2,double s3,int s4,String s5,double s6,int s7){
        food_name = s1;
        introduce = s2;
        price = s3;
        number = s4;
        image = s5;
        price2 = s6;
        star = s7;
    }
    public void outdata(){
        System.out.println("food_name = " + food_name+" "+"introduce = " + introduce+" "
                +"price = " + price+" "+"price2 = "+price2+" "+"number = "+number+" "+"image = " + image+" star = "+star);
    }
}
