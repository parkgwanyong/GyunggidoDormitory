package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NoticeWriteActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mNoticeWriteTitleEdit;
    private EditText mNoticeWriteContentEdit;

    private ImageView mNoticeWriteImageView;
    private Button mNoticeWriteImageBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mNoticeDatabase;
    private DatabaseReference mDatabase;
    private User user;
    private Uri imageUri = null;

    private ProgressDialog mNoticeUploadDialog;

    private StorageReference mStorageReference;

    private static final int GALLERY_REQUEST = 1;

    private boolean checkImage = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        mStorageReference = FirebaseStorage.getInstance().getReference();

        mNoticeWriteTitleEdit = (EditText) findViewById(R.id.notice_write_title);
        mNoticeWriteContentEdit = (EditText) findViewById(R.id.notice_write_contents);

        mNoticeWriteImageBtn = (Button) findViewById(R.id.notice_write_image_btn);
        mNoticeWriteImageView = (ImageView) findViewById(R.id.notice_write_image_view);

        mNoticeWriteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkImage == false){
                    //mNoticeWriteImageBtn.setBackgroundResource(R.drawable.image_upload);
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }else{
                    mNoticeWriteImageView.setImageURI(null);
                    mNoticeWriteImageBtn.setBackgroundResource(R.drawable.image_upload);
                    checkImage = false;
                }

            }
        });


        mToolbar = (Toolbar) findViewById(R.id.notice_write_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("글쓰기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.repair_write_cancel);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            mNoticeWriteImageBtn.setBackgroundResource(R.drawable.image_cancel);
            mNoticeWriteImageView.setImageURI(imageUri);
            checkImage = true;
        }
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

            mNoticeUploadDialog = new ProgressDialog(this);
            mNoticeUploadDialog.setTitle("업로딩");
            mNoticeUploadDialog.setMessage("잠시만 기다려주세요.");
            mNoticeUploadDialog.show();

            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = current_user.getUid();

            mNoticeDatabase = FirebaseDatabase.getInstance().getReference().child("Notice").push();

            final String textId = mNoticeDatabase.getKey().toString();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

            ValueEventListener noticeWriteListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    user = dataSnapshot.getValue(User.class);

                    final HashMap<String, String> noticeMap = new HashMap<String, String>();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/ M/ dd HH:mm");
                    final String currentDateandTime = sdf.format(new Date());
                    final Long pivotTime = 100000000000000000L;

                    if(checkImage == true){
                        StorageReference filepath = mStorageReference.child("Notice_Images").child(textId);
                        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                                noticeMap.put("title",title);
                                noticeMap.put("council",user.getCouncil());
                                noticeMap.put("contents",content);
                                noticeMap.put("nickname",user.getNickname());
                                noticeMap.put("userID",uid);
                                noticeMap.put("time",currentDateandTime.toString());
                                noticeMap.put("image_uri",downloadUri.toString());
                                noticeMap.put("order_time", String.valueOf((pivotTime-System.currentTimeMillis())));

                                mNoticeDatabase.setValue(noticeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mNoticeUploadDialog.dismiss();
                                            InputMethodManager inputManager = (InputMethodManager)
                                                    getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputManager.hideSoftInputFromWindow(mNoticeWriteContentEdit.getApplicationWindowToken(), 0);
                                            Intent i = new Intent(NoticeWriteActivity.this, NoticeActivity.class);
                                            startActivity(i);
                                            finish();
                                            overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
                                        } else {
                                            mNoticeUploadDialog.dismiss();
                                            Toast.makeText(NoticeWriteActivity.this, "오류!, 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        });
                    }else{
                        noticeMap.put("title",title);
                        noticeMap.put("council",user.getCouncil());
                        noticeMap.put("contents",content);
                        noticeMap.put("nickname",user.getNickname());
                        noticeMap.put("userID",uid);
                        noticeMap.put("time",currentDateandTime.toString());
                        noticeMap.put("image_uri","empty");
                        noticeMap.put("order_time", String.valueOf((pivotTime-System.currentTimeMillis())));

                        mNoticeDatabase.setValue(noticeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mNoticeUploadDialog.dismiss();
                                    InputMethodManager inputManager = (InputMethodManager)
                                            getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(mNoticeWriteContentEdit.getApplicationWindowToken(), 0);
                                    Intent i = new Intent(NoticeWriteActivity.this, NoticeActivity.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
                                } else {
                                    mNoticeUploadDialog.dismiss();
                                    Toast.makeText(NoticeWriteActivity.this, "오류!, 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }



                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    mNoticeUploadDialog.dismiss();
                    Toast.makeText(NoticeWriteActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                    // ...
                }
            };
            mDatabase.addValueEventListener(noticeWriteListener);
        }
    }
}
