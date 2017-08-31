package org.androidtown.prototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mBusLayout;
    private LinearLayout mMenuLayout;
    private LinearLayout mSleepLayout;
    private LinearLayout mNoticeLayout;
    private LinearLayout mRepairLayout;
    private LinearLayout mLogOutLayout;

    private DatabaseReference mDatabase;


    private LinearLayout mMainLeftLayout;
    private LinearLayout mMainRightLayout;


    private User user;
    private String mClass;

    private FirebaseAuth mAuth;

    private ProgressBarHandler mProgressBarHandler;

    private boolean sleepChecker = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBarHandler = new ProgressBarHandler(this); // In onCreate
        mProgressBarHandler.show(); // To show the progress bar

        mMainLeftLayout = (LinearLayout) findViewById(R.id.main_left_layout);
        mMainRightLayout = (LinearLayout) findViewById(R.id.main_right_layout);

        mMainLeftLayout.setVisibility(View.INVISIBLE);
        mMainRightLayout.setVisibility(View.INVISIBLE);






        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            final String user_id = mAuth.getCurrentUser().getUid().toString();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            ValueEventListener classListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    mClass = user.getmClass();
                    if(!mClass.equals("재사생")){
                        sleepChecker = false;
                    }else{
                        sleepChecker = true;
                    }
                    mMainLeftLayout.setVisibility(View.VISIBLE);
                    mMainRightLayout.setVisibility(View.VISIBLE);

                    mProgressBarHandler.hide();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabase.addListenerForSingleValueEvent(classListener);

        }


        mBusLayout = (LinearLayout) findViewById(R.id.main_bus_layout);
        mMenuLayout = (LinearLayout) findViewById(R.id.main_menu_layout);
        mLogOutLayout =(LinearLayout) findViewById(R.id.main_logout_layout);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            sendToStart();

        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(startIntent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
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
        /*
        Animation menuAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(menuAnim);

        */
        Animation sleepAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(sleepAnim);
        if(sleepChecker) {
            sleepAnim.setAnimationListener(animationSleepListener);
        }else{
            Toast.makeText(MainActivity.this, "외박신청은 재사생만 할 수 있습니다.",Toast.LENGTH_SHORT).show();
        }

    }
    public void onLogOutClicked(View v) {
        FirebaseAuth.getInstance().signOut();
        sendToStart();
    }
    public void onNoticeClicked(View v) {
        Animation noticeAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(noticeAnim);
        noticeAnim.setAnimationListener(animationNoticeListener);
    }

    public void onAnonymousClicked(View v) {
        Animation anonymousAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(anonymousAnim);
        anonymousAnim.setAnimationListener(animationAnonymousListener);
    }

    public void onBoardClicked(View v) {
        Animation boardAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(boardAnim);
        boardAnim.setAnimationListener(animationBoardListener);

    }
    public void onRepairClicked(View v) {
        Animation repairAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        v.startAnimation(repairAnim);
        repairAnim.setAnimationListener(animationRepairListener);
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),BusActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);
            //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        }
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener animationMenuListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);
            //
        }
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener animationNoticeListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),NoticeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);
            //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        }
        public void onAnimationRepeat(Animation animation) {}
    };
    Animation.AnimationListener animationBoardListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),BoardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);
            //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        }
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener animationAnonymousListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),AnonymousActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);
            //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        }
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener animationSleepListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),SleepActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);
            //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        }
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener animationRepairListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) {}
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(),RepairActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in,R.anim.left_out);
            //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            //overridePendingTransition(R.anim.left_in,R.anim.left_out);
        }
        public void onAnimationRepeat(Animation animation) {}
    };

    public class ProgressBarHandler {
        private ProgressBar mProgressBar;
        private Context mContext;

        public ProgressBarHandler(Context context) {
            mContext = context;

            ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();

            mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
            mProgressBar.setIndeterminate(true);

            RelativeLayout.LayoutParams params = new
                    RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rl = new RelativeLayout(context);

            rl.setGravity(Gravity.CENTER);
            rl.addView(mProgressBar);

            layout.addView(rl, params);

            hide();
        }

        public void show() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        public void hide() {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}

