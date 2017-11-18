package com.lvqingyang.androidart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private android.widget.TextView tv1;
    private android.widget.TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tv2 = (TextView) findViewById(R.id.tv2);
        this.tv1 = (TextView) findViewById(R.id.tv1);

        tv1.setBackground(new RoundRectDrawable(Color.RED));
        tv2.setBackground(new RoundRectDrawable(Color.BLUE));

    }
}
