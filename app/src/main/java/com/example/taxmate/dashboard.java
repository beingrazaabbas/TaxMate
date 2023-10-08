package com.example.taxmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboardnav);
        Button c = (Button) findViewById(R.id.tax);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(view.getContext(), itcalc.class);
                startActivity(intent);
            }

        });




//        Button x = (Button) findViewById(R.id.income_tracking);
//        x.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                Intent intent = new Intent(menu.this, income.class);
//                startActivity(intent);
//            }
//
//        });
    }
}
