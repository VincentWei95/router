package com.example.app.member;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.router.annotation.Route;

@Route(path = "/member/member", group = "member")
public class MemberActivity extends AppCompatActivity {
    private static final String TAG = "MemberActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        boolean isLogin = getIntent().getBooleanExtra("is_login", false);
        Log.v(TAG, "isLogin = " + isLogin);

        boolean isLogin2 = getIntent().getBooleanExtra("is_login2", false);
        Log.v(TAG, "isLogin2 = " + isLogin2);

        String member = getIntent().getStringExtra("member");
        Log.v(TAG, "member = " + member);
    }
}
