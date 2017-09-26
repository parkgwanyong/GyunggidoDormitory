package org.androidtown.prototype;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SleepActivity extends AppCompatActivity {

    private TextView mSleepNameTxt;
    private TextView mSleepBuildingTxt;
    private TextView mSleepRoomNumberTxt;
    private TextView mSleepDateFromSelectTxt;
    private TextView mSleepDateToSelectTxt;
    private TextView mSleepDateFromDisplayTxt;
    private TextView mSleepDateToDisplayTxt;
    private TextView mSleepDestinationDisplayTxt;
    private EditText mSleepDestinationEdit;
    private Button mSleepSubmitBtn;
    private Button mSleepCancelBtn;

    private ProgressDialog mSleepProgress;
    private ProgressDialog mSleepLoadingProgress;
    private ProgressDialog mSleepDeleteProgress;

    private static final int DIALOG_ID_FIRST = 0;
    private static final int DIALOG_ID_SECOND = 1;
    Calendar initialCalendar = Calendar.getInstance( );
    private int year_x = initialCalendar.get(Calendar.YEAR);
    private int month_x = initialCalendar.get(Calendar.MONTH) + 1;
    private int day_x = initialCalendar.get(Calendar.DAY_OF_MONTH);
    private int year_y = initialCalendar.get(Calendar.YEAR);
    private int month_y = initialCalendar.get(Calendar.MONTH) + 1;
    private int day_y = initialCalendar.get(Calendar.DAY_OF_MONTH) +1;

    private int mHour = initialCalendar.get(Calendar.HOUR_OF_DAY);

    private long start_time = System.currentTimeMillis();
    private DatePickerDialog datePickerDialog;

    private LinearLayout mSleepDisplayLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mSleepStaffDatabase;
    private DatabaseReference mSleepOnceDatabase;

    private User user;
    private SleepDisposal sleepDisposal;

    private static final String TAG = "qkrrhksdyd";

    private boolean submitExist = false;


    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);


        mSleepDisplayLayout = (LinearLayout) findViewById(R.id.sleep_display_layout);


        mSleepProgress = new ProgressDialog(this);
        mSleepLoadingProgress = new ProgressDialog(this);
        mSleepDeleteProgress = new ProgressDialog(this);

        mSleepLoadingProgress.setTitle("로딩중");
        mSleepLoadingProgress.setMessage("잠시만 기다려 주세요.");
        mSleepLoadingProgress.setCanceledOnTouchOutside(false);
        mSleepLoadingProgress.show();

        mSleepDateFromSelectTxt = (TextView) findViewById(R.id.sleep_date_from_select_text);
        mSleepDateToSelectTxt = (TextView) findViewById(R.id.sleep_date_to_select_text);
        mSleepNameTxt = (TextView) findViewById(R.id.sleep_name_text);
        mSleepBuildingTxt = (TextView) findViewById(R.id.sleep_building_text);
        mSleepRoomNumberTxt = (TextView) findViewById(R.id.sleep_roomNumber_text);
        mSleepDestinationEdit = (EditText) findViewById(R.id.sleep_destination_edit);
        mSleepSubmitBtn = (Button) findViewById(R.id.sleep_submit_btn);
        mSleepCancelBtn = (Button) findViewById(R.id.sleep_cancel_display_btn);
        mSleepDestinationDisplayTxt = (TextView) findViewById(R.id.sleep_destination_display_txt);
        mSleepDateFromDisplayTxt = (TextView) findViewById(R.id.sleep_date_from_display_txt);
        mSleepDateToDisplayTxt = (TextView) findViewById(R.id.sleep_date_to_display_txt);

        mSleepDateFromSelectTxt.setText(year_x+ "년 "+ month_x+ "월 "+ day_x + "부터");
        mSleepDateToSelectTxt.setText(year_y+ "년 "+ month_y+ "월 "+ day_y + "까지");




        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();


        if ((mHour >= 00 && mHour <= 5)){
            mSleepSubmitBtn.setBackgroundColor(Color.parseColor("#8388a4"));

        }


        mSleepOnceDatabase = FirebaseDatabase.getInstance().getReference().child("SleepOut_disposal").child(uid);
        ValueEventListener sleepDisposalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()) {


                    java.text.SimpleDateFormat fd = new java.text.SimpleDateFormat("yyyy/ M/ dd", Locale.KOREA);
                    String check_date = fd.format(new Date(System.currentTimeMillis()));
                    sleepDisposal = dataSnapshot.getValue(SleepDisposal.class);

                    if (!sleepDisposal.getTo().equals(check_date)) {

                        submitExist = true;
                        mSleepSubmitBtn.setBackgroundColor(Color.parseColor("#8388a4"));
                        mSleepDisplayLayout.setVisibility(View.VISIBLE);
                        mSleepDisplayLayout.setEnabled(true);



                        mSleepDestinationDisplayTxt.setText(sleepDisposal.getDestination());
                        mSleepDateFromDisplayTxt.setText(sleepDisposal.getFrom());
                        mSleepDateToDisplayTxt.setText(sleepDisposal.getTo());
                        mSleepDestinationEdit.setText("");

                        mSleepCancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final AlertDialog alertDialog = new AlertDialog.Builder(SleepActivity.this)
                                        .setTitle("삭제")
                                        .setMessage("외박 신청을 취소하시겠습니까?")
                                        .setPositiveButton("네", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mSleepDeleteProgress.setTitle("삭제중");
                                                mSleepDeleteProgress.setMessage("잠시만 기다려 주세요.");
                                                mSleepDeleteProgress.setCanceledOnTouchOutside(false);
                                                mSleepDeleteProgress.show();
                                                mSleepOnceDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mSleepDeleteProgress.dismiss();
                                                            submitExist = false;
                                                            mSleepSubmitBtn.setBackgroundColor(Color.parseColor("#1C87B7"));
                                                        } else {
                                                            mSleepDeleteProgress.dismiss();
                                                            Toast.makeText(SleepActivity.this, "데이터베이스 오류! 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .create();
                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF1744"));
                                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                                    }
                                });
                                alertDialog.show();
                            }
                        });

                    } else{
                        mSleepOnceDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    submitExist = false;
                                    mSleepSubmitBtn.setBackgroundColor(Color.parseColor("#1C87B7"));
                                } else {
                                    Toast.makeText(SleepActivity.this, "데이터베이스 오류! 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }  else {
                    mSleepDisplayLayout.setVisibility(View.INVISIBLE);
                    mSleepDisplayLayout.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(SleepActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                // ...
            }
        };
        mSleepOnceDatabase.addValueEventListener(sleepDisposalListener);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        /*
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(SleepActivity.this,"여기까지 실행됨",Toast.LENGTH_SHORT).show();
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);

                mSleepNameTxt.setText(user.getName());
                mSleepBuildingTxt.setText(user.getBuilding());
                mSleepRoomNumberTxt.setText(user.getBuilding_number());
                mSleepLoadingProgress.dismiss();
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(SleepActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                mSleepLoadingProgress.dismiss();
                // ...
            }
        };
        mDatabase.addValueEventListener(userListener);
        */

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                mSleepNameTxt.setText(user.getName());
                mSleepBuildingTxt.setText(user.getBuilding());
                mSleepRoomNumberTxt.setText(user.getBuilding_number());
                mSleepLoadingProgress.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SleepActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                mSleepLoadingProgress.dismiss();

            }
        });



        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.sleepout_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("외박 신청");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        showDialogOnDateFromSelectClicked();
        showDialogOnDateToSelectClicked();

        mSleepSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(mHour >= 00 && mHour <= 5)){
                    if (submitExist == false) {
                        if (!TextUtils.isEmpty(mSleepDestinationEdit.getText().toString())) {

                            mSleepProgress.setTitle("신청중");
                            mSleepProgress.setMessage("잠시만 기다려 주세요.");
                            mSleepProgress.setCanceledOnTouchOutside(false);
                            mSleepProgress.show();

                            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                            String str_date = df.format(new Date(System.currentTimeMillis()));
                            String destination = mSleepDestinationEdit.getText().toString();

                            mSleepStaffDatabase = FirebaseDatabase.getInstance().getReference().child("SleepOut_staff").child(str_date).child(user.getName());

                            HashMap<String, String> userMap = new HashMap<String, String>();
                            userMap.put("date", year_x + "년 " + month_x + "월 " + day_x + "일 " + " ~ " + year_y + "년 " + month_y + "월 " + day_y + "일" + "까지");
                            userMap.put("destination", destination);
                            userMap.put("building", user.getBuilding() + " " + user.getBuilding_number());

                            HashMap<String, String> onceMap = new HashMap<String, String>();
                            onceMap.put("from", year_x + "/ " + month_x + "/ " + day_x);
                            onceMap.put("to", year_y + "/ " + month_y + "/ " + day_y);
                            onceMap.put("destination", destination);

                            mSleepOnceDatabase.setValue(onceMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        InputMethodManager inputManager = (InputMethodManager)
                                                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputManager.hideSoftInputFromWindow(mSleepDestinationDisplayTxt.getApplicationWindowToken(), 0);
                                        //inputManager.toggleSoftInput(0, 0);
                                        mSleepProgress.dismiss();
                                    } else {
                                        Toast.makeText(SleepActivity.this, "데이터베이스 오류! 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            mSleepStaffDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    } else {
                                        Toast.makeText(SleepActivity.this, "데이터베이스 오류! 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        } else {
                            Toast.makeText(SleepActivity.this, "목적지를 입력해 주세요", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(SleepActivity.this, "외박신청은 동시에 최대 한번만 가능합니다.", Toast.LENGTH_SHORT).show();

                    }
            }else{
                    Toast.makeText(SleepActivity.this, "00시부터 06시 까지는 외박신청이 불가능합니다", Toast.LENGTH_SHORT).show();

                }

            }
        });

        mSleepDestinationEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(mSleepDestinationDisplayTxt.getApplicationWindowToken(),0);
                    return true;
                }
                return false;
            }
        });





    }


    public void showDialogOnDateFromSelectClicked(){
        mSleepDateFromSelectTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(SleepActivity.this, dPickerListener, year_x,month_x-1,day_x);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                datePickerDialog.show();
            }
        });
    }
    public void showDialogOnDateToSelectClicked(){

        mSleepDateToSelectTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(SleepActivity.this, dPickerListenerTWO, year_y,month_y-1,day_y);
                datePickerDialog.getDatePicker().setMinDate(start_time);
                datePickerDialog.show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            year_x = year;
            month_x = month+1;
            day_x = dayOfMonth;


            Calendar calendar = Calendar.getInstance();
            calendar.set(year_x,month_x-1,dayOfMonth);
            start_time = calendar.getTimeInMillis();
            mSleepDateFromSelectTxt.setText(year_x+ "년 "+ month_x+ "월 "+ day_x + "부터");


            if(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) == day_x){
                //Toast.makeText(SleepActivity.this, String.valueOf(checkCalender.getActualMaximum(Calendar.DAY_OF_MONTH)),Toast.LENGTH_SHORT).show();
                if(month_x != 12){
                    mSleepDateToSelectTxt.setText(year_x+ "년 "+ (month_x+1) + "월 "+ 1 + "까지");
                    year_y = year_x;
                    month_y = month_x+1;
                    day_y = 1;
                } else{
                    mSleepDateToSelectTxt.setText(year_x+ "년 "+ 1 + "월 "+ 1 + "까지");
                    year_y = year_x;
                    month_y = 1;
                    day_y = 1;
                }

            } else{
                mSleepDateToSelectTxt.setText(year_x+ "년 "+ month_x + "월 "+ (day_x+1)+ "까지");
                year_y = year_x;
                month_y = month_x;
                day_y = day_x+1;

            }


        }
    };
    private DatePickerDialog.OnDateSetListener dPickerListenerTWO = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            year_y = year;
            month_y = month+1;
            day_y = dayOfMonth;

            mSleepDateToSelectTxt.setText(year_y+ "년 "+ month_y+ "월 "+ day_y + "까지");

        }
    };


    @Override
    public void onBackPressed(){
        Intent homeIntent = new Intent(SleepActivity.this, MainActivity.class);
        startActivity(homeIntent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();

    }
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(SleepActivity.this, MainActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };
}
