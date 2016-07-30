package com.example.apple.buffetapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
    }
    public void ToMerchans(View v){
        Intent intent = new Intent(this,MerchantsActivity.class);
        startActivity(intent);
    }
    public void ToRecommended(View v){
        Intent intent = new Intent(this,RecommendedActivity.class);
        startActivity(intent);
    }
    public void ToRecipe(View v){
        Intent intent = new Intent(this,RecipeActivity.class);
        startActivity(intent);
    }
    public void ToShoopingCart(View v){
        Intent intent = new Intent(this,ShoopingCartActivity.class);
        startActivity(intent);
    }
}
