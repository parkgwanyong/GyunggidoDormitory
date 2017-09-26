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

public class RepairWriteActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mRepairWriteTitleEdit;
    private EditText mRepairWriteContentEdit;

    private ImageView mRepairWriteImageView;
    private Button mRepairWriteImageBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mRepairWriteDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mRepairStaffDatabase;
    private User user;

    private Uri imageUri = null;

    private ProgressDialog mRepairUploadDialog;

    private StorageReference mStorageReference;

    private static final int GALLERY_REQUEST = 1;

    private boolean checkImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_write);

        mStorageReference = FirebaseStorage.getInstance().getReference();

        mRepairWriteImageBtn = (Button) findViewById(R.id.repair_write_image_btn);
        mRepairWriteImageView = (ImageView) findViewById(R.id.repair_write_image_view);

        mRepairWriteTitleEdit = (EditText) findViewById(R.id.repair_write_title);
        mRepairWriteContentEdit = (EditText) findViewById(R.id.repair_write_contents);

        mRepairWriteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkImage == false){
                    //mNoticeWriteImageBtn.setBackgroundResource(R.drawable.image_upload);
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }else{
                    mRepairWriteImageView.setImageURI(null);
                    mRepairWriteImageBtn.setBackgroundResource(R.drawable.image_upload);
                    checkImage = false;
                }

            }
        });

        mToolbar = (Toolbar) findViewById(R.id.repair_toolbar);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            mRepairWriteImageBtn.setBackgroundResource(R.drawable.image_cancel);
            mRepairWriteImageView.setImageURI(imageUri);
            checkImage = true;
        }
    }

    @Override
    public void onBackPressed(){

        final String title = mRepairWriteTitleEdit.getText().toString();
        final String content = mRepairWriteContentEdit.getText().toString();

        if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content)){
            Intent intent = new Intent(getApplicationContext(),RepairActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);

        } else{
            final AlertDialog alertDialog = new AlertDialog.Builder(RepairWriteActivity.this)
                    .setTitle("뒤로가기")
                    .setMessage("작성을 취소하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(),RepairActivity.class);
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

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                final String title = mRepairWriteTitleEdit.getText().toString();
                final String content = mRepairWriteContentEdit.getText().toString();

                if(TextUtils.isEmpty(title) && TextUtils.isEmpty(content)){
                    Intent intent = new Intent(getApplicationContext(),RepairActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);

                }else{
                    final AlertDialog alertDialog = new AlertDialog.Builder(RepairWriteActivity.this)
                            .setTitle("뒤로가기")
                            .setMessage("작성을 취소하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(),RepairActivity.class);
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

    private void submitRequest() {


        final String mTitle = mRepairWriteTitleEdit.getText().toString();
        final String mContents = mRepairWriteContentEdit.getText().toString();


        if(TextUtils.isEmpty(mTitle)){
            Toast.makeText(RepairWriteActivity.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(mContents)) {
            Toast.makeText(RepairWriteActivity.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }else{

            mRepairUploadDialog = new ProgressDialog(this);
            mRepairUploadDialog.setTitle("업로딩");
            mRepairUploadDialog.setMessage("잠시만 기다려주세요.");
            mRepairUploadDialog.show();

            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = current_user.getUid();

            mRepairWriteDatabase = FirebaseDatabase.getInstance().getReference().child("Repair").child(uid).push();
            String textKey = mRepairWriteDatabase.getKey().toString();

            mRepairStaffDatabase = FirebaseDatabase.getInstance().getReference().child("Repair_staff").child(textKey);

            final String textId = textKey;

            final String mStatus = "미확인";

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


            ValueEventListener repairWriteListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    user = dataSnapshot.getValue(User.class);

                    final HashMap<String, String> repairMap = new HashMap<String, String>();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/ M/ dd HH:mm");
                    final String currentDateandTime = sdf.format(new Date());
                    final Long pivotTime = 100000000000000000L;

                    if(checkImage == true){
                        StorageReference filepath = mStorageReference.child("Board_Images").child(textId);
                        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                                repairMap.put("title",mTitle);
                                repairMap.put("contents",mContents);
                                repairMap.put("status",mStatus);
                                repairMap.put("nickname",user.getNickname());
                                repairMap.put("userID",uid);
                                repairMap.put("image_uri",downloadUri.toString());
                                repairMap.put("time",currentDateandTime.toString());
                                repairMap.put("order_time", String.valueOf((pivotTime-System.currentTimeMillis())));

                                mRepairWriteDatabase.setValue(repairMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mRepairUploadDialog.dismiss();
                                            InputMethodManager inputManager = (InputMethodManager)
                                                    getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputManager.hideSoftInputFromWindow(mRepairWriteContentEdit.getApplicationWindowToken(), 0);

                                            Intent i = new Intent(RepairWriteActivity.this, RepairActivity.class);
                                            startActivity(i);
                                            finish();
                                            overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
                                        }else{
                                            mRepairUploadDialog.dismiss();
                                            Toast.makeText(RepairWriteActivity.this,"Error, try again",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                mRepairStaffDatabase.setValue(repairMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                        }else{
                                            Toast.makeText(RepairWriteActivity.this,"직원에게 전달되지 않았습니다. 다시 작성해주세요.",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }else{
                        repairMap.put("title",mTitle);
                        repairMap.put("contents",mContents);
                        repairMap.put("status",mStatus);
                        repairMap.put("nickname",user.getNickname());
                        repairMap.put("userID",uid);
                        repairMap.put("image_uri","empty");
                        repairMap.put("time",currentDateandTime.toString());
                        repairMap.put("order_time", String.valueOf((pivotTime-System.currentTimeMillis())));

                        mRepairWriteDatabase.setValue(repairMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mRepairUploadDialog.dismiss();
                                    InputMethodManager inputManager = (InputMethodManager)
                                            getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputManager.hideSoftInputFromWindow(mRepairWriteContentEdit.getApplicationWindowToken(), 0);

                                    Intent i = new Intent(RepairWriteActivity.this, RepairActivity.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(R.anim.below_cancel_out, R.anim.below_cancel_in);
                                }else{
                                    mRepairUploadDialog.dismiss();
                                    Toast.makeText(RepairWriteActivity.this,"Error, try again",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        mRepairStaffDatabase.setValue(repairMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                }else{
                                    Toast.makeText(RepairWriteActivity.this,"직원에게 전달되지 않았습니다. 다시 작성해주세요.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(RepairWriteActivity.this, databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
                    // ...
                }
            };
            mDatabase.addValueEventListener(repairWriteListener);
        }
    }
}
