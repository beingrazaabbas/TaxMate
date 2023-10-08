package com.example.taxmate;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class whoru extends AppCompatActivity {

    private CheckBox mCheckBox;
    private CheckBox nCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whoru);

        mCheckBox = findViewById(R.id.c1);
        nCheckBox = findViewById(R.id.c2);

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCheckBox.isChecked()) {
                    Intent intent = new Intent(whoru.this, dashboardnav.class);
                    startActivity(intent);
                }
            }
        });

        nCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nCheckBox.isChecked()) {
                    Intent intent = new Intent(whoru.this, dashboardnav.class);
                    startActivity(intent);
                }
            }
        });
    }
}
