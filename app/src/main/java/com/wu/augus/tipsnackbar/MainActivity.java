package com.wu.augus.tipsnackbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.wu.augus.tipbar.TSnackbar;


public class MainActivity extends AppCompatActivity {
    CoordinatorLayout cool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
        CoordinatorLayout cool = findViewById(R.id.cool);

        TSnackbar.Snackbar.INSTANCE.make(cool,"test",TSnackbar.LENGTH_LONG).show();
=======
        cool = findViewById(R.id.cool);

//        TSnackbar.Snackbar.INSTANCE.make(cool,"test",TSnackbar.LENGTH_LONG).show();

    }

    public void onClick(View view) {
        //長延遲 LONG_DURATION_MS 2750  / (顯示/收回)動畫速度 mShowAndHideTime = 200L
//        TSnackbar.Snackbar.INSTANCE.make(cool,TSnackbar.LENGTH_LONG).show();
        //短延遲 SHORT_DURATION_MS 1500  / (顯示/收回)動畫速度 mShowAndHideTime = 200L
//        TSnackbar.Snackbar.INSTANCE.make(cool,TSnackbar.LENGTH_SHORT).show();

        // LENGTH_INDEFINITE 不主動收回  / 自定義View R.layout.view_tip_test / 自定義(顯示/收回)動畫速度 mShowAndHideTime = 400L
//        final TSnackbar mTSnackbar = TSnackbar.Snackbar.INSTANCE.make(cool,TSnackbar.LENGTH_INDEFINITE,R.layout.view_tip_test,400);
//        //顯示
//        mTSnackbar.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //收回
//                mTSnackbar.dismiss();
//            }
//        },6000);

        //修改自定義View內的畫面
        //自定義停留時間 3000 / 自定義View R.layout.view_tip_test / 自定義(顯示/收回)動畫速度 mShowAndHideTime = 400L
//        final TSnackbar mTSnackbar = TSnackbar.Snackbar.INSTANCE.make(cool, 3000, R.layout.view_tip_test, 400);
//        View mShowLayout = mTSnackbar.getView();
//        TextView t1 = mShowLayout.findViewById(R.id.t1);
//        t1.setText("2222111122221111");
//        mTSnackbar.show();
>>>>>>> Feature/1-自定義View
    }
}
