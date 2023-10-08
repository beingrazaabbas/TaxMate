package com.example.taxmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.taxmate.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class dashboardnav extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    taxavingfragment taxavingfragment = new taxavingfragment();
    taxtrackfragment taxtrackfragment = new taxtrackfragment();
    dashboardfragment dashboardfragment = new dashboardfragment();
    itrfragment itrfragment = new itrfragment();
    morefragment morefragment = new morefragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboardnav);

        bottomNavigationView = findViewById(R.id.navbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,dashboardfragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboardbtn:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,dashboardfragment).commit();
                        return true;
                    case R.id.taxsavingbtn:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,taxavingfragment).commit();
                        return true;
                    case R.id.taxtrackicon:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,taxtrackfragment).commit();
                        return true;
                    case R.id.filestatusbtn:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,itrfragment).commit();
                        return true;
                    case R.id.morebtn:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,morefragment).commit();
                        return true;

                }
return false;
            }
        });



    }
}