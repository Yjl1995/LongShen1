package com.example.apple.buffetapplication;

/**
 * Created by apple on 2016/7/30.
 */
public class Meal {
    public Meal(int x1,String x2,String x3,double x4,int x5,String x6){
        food_id = x1;
        food_name = x2;
        introduce = x3;
        price = x4;
        star = x5;
        image = x6;
    }
    public String food_name = "";
    public int food_id = 0;
    public String introduce = "";
    public double price = 0;
    public int star = 0;
    public String image = "";
    public void putdata(String s1,String s2,double s3,int s4,String s5,int s6){
        food_name = s1;
        introduce = s2;
        price = s3;
        star = s4;
        image = s5;
        food_id = s6;
    }
    public void outdata(){
        System.out.println("food_id = "+food_id+"food_name = " + food_name+" "+"introduce = " + introduce+" "
                +"price = " + price+" "+"star = " + star+" "+"image = " + image);
    }
}
