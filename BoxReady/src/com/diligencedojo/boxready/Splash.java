package com.diligencedojo.boxready;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Splash extends Activity {
        @Override
        public void onCreate(Bundle icicle) {
                super.onCreate(icicle);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.splash_screen,
                                (ViewGroup) findViewById(R.id.splash));
                
                final Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 1, 1);                
                toast.setView(layout);
                toast.show();

                new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                                Splash.this.startActivity(new Intent(Splash.this, RegistrationActivity.class));
                                Splash.this.finish();
                        }
                });
        }
}