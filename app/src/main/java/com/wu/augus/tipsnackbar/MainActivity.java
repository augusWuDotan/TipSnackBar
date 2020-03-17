package com.wu.augus.tipsnackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;

import com.wu.augus.tipbar.TSnackbar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CoordinatorLayout cool = findViewById(R.id.cool);

        TSnackbar.Snackbar.INSTANCE.make(cool,"test",TSnackbar.LENGTH_LONG).show();
    }
}
