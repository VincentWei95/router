package com.example.app.member;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.router.annotation.Autowired;
import com.example.router.annotation.Route;
import com.example.router.core.Router;

@Route(path = "/member/member", group = "member")
public class MemberActivity extends MemberParentActivity {
    private static final String TAG = "MemberActivity";


    @Autowired
    int age = 10;

    @Autowired
    int height = 175;

    @Autowired(name = "boy", required = true)
    boolean girl;

    @Autowired
    char ch = 'A';

    @Autowired
    float fl = 12.00f;

    @Autowired
    double dou = 12.01d;

    @Autowired(required = true)
    String name;

    @Autowired
    TestSerializable ser;

    @Autowired
    TestParcelable pac;

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

        Router.inject(this);

        String params = String.format(
                "age=%s, \n height=%s, \n girl=%s, \n ch=%s \n fl = %s, \n dou = %s \n parentName = %s \n name = %s \n ser=%s, \n pac=%s",
                age,
                height,
                girl,
                ch,
                fl,
                dou,
                parentName,
                name,
                ser,
                pac
        );
        Log.v(TAG, params);
    }
}
