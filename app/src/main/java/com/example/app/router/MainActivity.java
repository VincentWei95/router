package com.example.app.router;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.router.callback.OnNavigationCallback;
import com.example.router.core.RouteRequest;
import com.example.router.core.Router;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Router.init();

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
                sp.edit().putBoolean("is_login", true).apply();
            }
        });
        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
                sp.edit().putBoolean("is_login", false).apply();
            }
        });
        findViewById(R.id.btn_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.request("/member/member")
                        .addInterceptor("login")
                        .addInterceptor("login2")
                        .addInterceptor("member")
                        .build()
                        .navigation(MainActivity.this, new OnNavigationCallback() {
                            @Override
                            public void onFound(RouteRequest request) {
                                Log.v(TAG, "onFound");
                            }

                            @Override
                            public void onLost(RouteRequest request) {
                                Log.v(TAG, "onLost");
                            }

                            @Override
                            public void onArrival(RouteRequest request) {
                                Log.v(TAG, "onArrival");
                            }

                            @Override
                            public void onInterrupt(RouteRequest request) {
                                Log.v(TAG, "onInterrupt");
                            }
                        });
            }
        });
    }
}