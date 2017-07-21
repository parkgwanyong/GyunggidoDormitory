package org.androidtown.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView imageBus;
    private ImageView imageDailyMenu;
    private ImageView imageSleepOut;
    private ImageView imageNotice;
    private ImageView imageRepari;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageBus = (ImageView) findViewById(R.id.imageBus);
        imageDailyMenu = (ImageView) findViewById(R.id.imageDailyMenu);

    }

    public void onBusClicked(View v) {
        Animation busAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(busAnim);
        busAnim.setAnimationListener(animationListener);
    }
    public void onMenuClicked(View v) {
        Animation menuAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(menuAnim);
        menuAnim.setAnimationListener(animationMenuListener);
    }
    public void onSleepOutClicked(View v) {
        Animation menuAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(menuAnim);
        Toast.makeText(getApplicationContext(),"구현 중 입니다.",Toast.LENGTH_SHORT).show();
    }
    public void onNoticeClicked(View v) {
        Animation menuAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(menuAnim);
        Toast.makeText(getApplicationContext(),"구현 중 입니다.",Toast.LENGTH_SHORT).show();
    }
    public void onRepairClicked(View v) {
        Animation menuAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(menuAnim);
        Toast.makeText(getApplicationContext(),"구현 중 입니다.",Toast.LENGTH_SHORT).show();
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),BusActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        }
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener animationMenuListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        }
        public void onAnimationRepeat(Animation animation) {}
    };

}

