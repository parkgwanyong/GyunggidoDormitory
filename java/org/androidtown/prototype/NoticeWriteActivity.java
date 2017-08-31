package org.androidtown.prototype;

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
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NoticeWriteActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mNoticeWriteTitleEdit;
    private EditText mNoticeWriteContentEdit;

    private FirebaseAuth mAuth;
    private DatabaseReference mNoticeDatabase;
    private DatabaseReference mDatabase;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        mNoticeWriteTitleEdit = (EditText) findViewById(R.id.notice_write_title);
        mNoticeWriteContentEdit = (EditText) findViewById(R.id.notice_write_contents);

        mToolbar = (Toolbar) findViewById(R.id.notice_write_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("글쓰기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.repair_write_cancel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repair_write_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                final String title = mNoticeWriteTitleEdit.getText().toString();
                final String content = mNoticeWriteContentEdit.getText().toString();

                if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content)){
                    Intent intent = new Intent(getApplicationContext(),NoticeActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
                }else{
                    final AlertDialog alertDialog = new AlertDialog.Builder(NoticeWriteActivity.this)
                            .setTitle("뒤로가기")
                            .setMessage("작성을 취소하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(),NoticeActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
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
                return true;
            case R.id.action_repair_write_done:
                submitRequest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        final String title = mNoticeWriteTitleEdit.getText().toString();
        final String content = mNoticeWriteContentEdit.getText().toString();

        if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content)){
            Intent intent = new Intent(getApplicationContext(),NoticeActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
        }else{
            final AlertDialog alertDialog = new AlertDialog.Builder(NoticeWriteActivity.this)
                    .setTitle("뒤로가기")
                    .setMessage("작성을 취소하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(NoticeWriteActivity.this, NoticeActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);

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
    }

    private void submitRequest() {

        final String title = mNoticeWriteTitleEdit.getText().toString();
        final String content = mNoticeWriteContentEdit.getText().toString();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(NoticeWriteActivity.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(content)) {
            Toast.makeText(NoticeWriteActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = current_user.getUid();

            mNoticeDatabase = FirebaseDatabase.getInstance().getReference().child("Notice").push();



            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            ValueEventListener noticeWriteListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    user = dataSnapshot.getValue(User.class);

                    HashMap<String, String> noticeMap = new HashMap<String, String>();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/ M/ dd HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    Long pivotTime = 100000000000000000L;

                    noticeMap.put("title",title);
                    noticeMap.put("council",user.getCouncil());
                    noticeMap.put("contents",content);
                    noticeMap.put("nickname",user.getNickname());
                    noticeMap.put("userID",uid);
                    noticeMap.put("time",currentDateandTime.toString());
                    noticeMap.put("order_time", String.valueOf((pivotTime-System.currentTimeMillis())));

                    mNoticeDatabase.setValue(noticeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                InputMethodManager inputManager = (InputMethodManager)
                                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.hideSoftInputFromWindow(mNoticeWriteContentEdit.getApplicationWindowToken(), 0);
                                Intent i = new Intent(NoticeWriteActivity.this, NoticeActivity.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
                            }else{
                                Toast.makeText(NoticeWriteActivity.this,"오류!, 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(NoticeWriteActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                    // ...
                }
            };
            mDatabase.addValueEventListener(noticeWriteListener);
        }
    }
}
