package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RepairSingleActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private DatabaseReference mRepairDatabase;
    private DatabaseReference mRepairStaffDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mRepairCommentDatabase;
    private RepairMainDisplay text;

    private TextView mRepairReadTitle;
    private TextView mRepairReadContents;
    private EditText mRepairReadComments;
    private Button mRepairReadSubmitBtn;
    private Spinner mRepairReadSpinner;

    private ImageView mRepairReadImage;

    private RecyclerView mRepairCommentList;

    private LinearLayout mReadTitleLayout;

    private ProgressDialog mRepairCommentProgress;
    private ProgressDialog mRepairDeleteProgress;
    private ProgressDialog mRepairLoadDialog;

    private String imageUri = "empty";


    private String nickname;
    private String mClass;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_single);

        mRepairLoadDialog = new ProgressDialog(this);
        mRepairLoadDialog.setTitle("로딩중");
        mRepairLoadDialog.setMessage("잠시만 기다려주세요.");
        mRepairLoadDialog.show();


        mRepairCommentList = (RecyclerView) findViewById(R.id.repair_write_comments_list);
        mRepairCommentList.setNestedScrollingEnabled(false);
        //mRepairCommentList.setHasFixedSize(true);
        mRepairCommentList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.repair_read_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("수리 요청");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRepairReadComments = (EditText) findViewById(R.id.repair_read_comments);
        mRepairReadSubmitBtn = (Button) findViewById(R.id.repair_read_submit_btn);

        mReadTitleLayout = (LinearLayout) findViewById(R.id.repair_read_title_layout);

        mRepairReadImage = (ImageView) findViewById(R.id.repair_read_image_view);

        mRepairReadTitle = (TextView) findViewById(R.id.repair_read_title);
        mRepairReadContents = (TextView) findViewById(R.id.repair_read_contents);
        mRepairReadSpinner = (Spinner) findViewById(R.id.repair_read_spinner);

        final String user_id = getIntent().getStringExtra("user_id");
        final String text_id = getIntent().getStringExtra("text_id");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String current_user_id = current_user.getUid();

        mRepairDatabase = FirebaseDatabase.getInstance().getReference().child("Repair").child(user_id).child(text_id);
        mRepairStaffDatabase = FirebaseDatabase.getInstance().getReference().child("Repair_staff").child(text_id);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);


       /* ValueEventListener currentCommentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                onStart();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        mRepairDatabase.addValueEventListener(currentCommentListener);*/


        ValueEventListener currentUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                nickname = user.getNickname();
                mClass = user.getmClass();

                if(!(mClass.equals("직원") || nickname.equals("Developer")) ){
                    mRepairReadSpinner.setEnabled(false);
                }else{
                    mRepairReadSpinner.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getApplicationContext(), databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(currentUserListener);


        mRepairReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String comments = mRepairReadComments.getText().toString();
                commentsUpdate(comments);

                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mRepairReadComments.getApplicationWindowToken(), 0);

                onStart();

            }
        });

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {

                    text = dataSnapshot.getValue(RepairMainDisplay.class);

                    mRepairReadTitle.setText(text.getTitle());
                    mRepairReadContents.setText(text.getContents());
                    imageUri = text.getImage_uri();
                    if(!imageUri.equals("empty")){
                        Picasso.with(getApplicationContext()).load(imageUri).into(mRepairReadImage, new com.squareup.picasso.Callback(){
                            @Override
                            public void onSuccess() {
                                mRepairLoadDialog.dismiss();
                            }

                            @Override
                            public void onError() {
                                mRepairLoadDialog.dismiss();
                                Toast.makeText(RepairSingleActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mRepairLoadDialog.dismiss();
                    }

                final String mStatus = text.getStatus().toString();
                switch (mStatus) {
                    case "미확인":
                        mReadTitleLayout.setBackgroundColor(Color.parseColor("#fafafa"));
                        break;
                    case "수리중":
                        mReadTitleLayout.setBackgroundColor(Color.parseColor("#FFF176"));
                        break;
                    case "완료":
                        mReadTitleLayout.setBackgroundColor(Color.parseColor("#CCFF90"));
                        break;

                }


                ArrayAdapter<CharSequence> repairAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.repair_array, R.layout.repair_spinner_item);
                repairAdapter.setDropDownViewResource(R.layout.repair_spinner_item);
                mRepairReadSpinner.setAdapter(repairAdapter);
                if (!mStatus.equals(null)) {
                    int spinnerPosition = repairAdapter.getPosition(mStatus);
                    mRepairReadSpinner.setSelection(spinnerPosition);
                }
                String mClass = user.getmClass();

                // ...
                mRepairReadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String mStatus_update = mStatus;
                        switch (position) {
                            case 0:
                                mStatus_update = "미확인";

                                break;
                            case 1:
                                mStatus_update = "수리중";

                                break;
                            case 2:
                                mStatus_update = "완료";
                                break;
                        }
                        updateStatus(mStatus_update);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(RepairSingleActivity.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();

                // ...
            }
        };
        mRepairDatabase.addValueEventListener(userListener);

        mRepairReadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageUri.equals("empty")){
                    Intent intent = new Intent(RepairSingleActivity.this, ImageZoomActivity.class);
                    intent.putExtra("image_uri",imageUri);
                    startActivity(intent);
                    overridePendingTransition(R.anim.below_in, R.anim.below_out);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query repairDatabaseQuery;
        repairDatabaseQuery = mRepairDatabase.child("comments").orderByChild("order_time");

        FirebaseRecyclerAdapter<RepairComment, RepairCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<RepairComment, RepairCommentViewHolder>(
                RepairComment.class,
                R.layout.repair_comments_layout,
                RepairCommentViewHolder.class,
                repairDatabaseQuery
        ) {
            @Override
            protected void populateViewHolder(RepairCommentViewHolder viewHolder, RepairComment model, int position) {
                viewHolder.setDate(model.getTime());
                viewHolder.setNickname(model.getNickname());
                viewHolder.setContent(model.getComment());


            }
        };
        mRepairCommentList.setAdapter(firebaseCommentRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repair_read_bar, menu);
        return true;
    }

    private void commentsUpdate(String comments) {

        mRepairCommentProgress = new ProgressDialog(this);
        mRepairCommentProgress.setTitle("로딩중");
        mRepairCommentProgress.setMessage("잠시만 기다려주세요.");
        mRepairCommentProgress.show();

        final HashMap<String, String> commentMap = new HashMap<String, String>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/ M/ dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("comment",comments);
        commentMap.put("nickname",nickname);
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("order_time", String.valueOf((System.currentTimeMillis())));
        mRepairCommentDatabase = mRepairDatabase.child("comments").push();
        final String commentTextKey = mRepairCommentDatabase.getKey();

        mRepairCommentDatabase.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mRepairStaffDatabase.child("comments").child(commentTextKey).setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRepairReadComments.setText("");
                                mRepairCommentProgress.dismiss();
                            }else{
                                mRepairCommentProgress.dismiss();
                                Toast.makeText(RepairSingleActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    mRepairCommentProgress.dismiss();
                    Toast.makeText(RepairSingleActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void updateStatus(final String status) {
        mRepairDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mRepairStaffDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                Toast.makeText(RepairSingleActivity.this,"상태가 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RepairSingleActivity.this,"상태가 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(RepairSingleActivity.this, RepairActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

    }
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(RepairSingleActivity.this, RepairActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_repair_read_delete:
                deleteRepair();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRepair() {

        final AlertDialog alertDialog = new AlertDialog.Builder(RepairSingleActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRepairDeleteProgress = new ProgressDialog(RepairSingleActivity.this);
                        mRepairDeleteProgress.setTitle("삭제중");
                        mRepairDeleteProgress.setMessage("잠시만 기다려 주세요.");
                        mRepairDeleteProgress.setCanceledOnTouchOutside(false);
                        mRepairDeleteProgress.show();
                        mRepairDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mRepairStaffDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mRepairDeleteProgress.dismiss();
                                                Intent i = new Intent(RepairSingleActivity.this, RepairActivity.class);
                                                startActivity(i);
                                                finish();
                                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                                            }else{
                                                mRepairDeleteProgress.dismiss();
                                                Toast.makeText(RepairSingleActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    mRepairDeleteProgress.dismiss();
                                    Toast.makeText(RepairSingleActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();

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

    ;

    public static class RepairCommentViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private TextView repairCommentContent;
        private TextView repairCommentDate;
        private TextView repairCommentNickname;

        public RepairCommentViewHolder(View itemView){
            super(itemView);
            mView = itemView;

        }
        public void setContent(String content){
            repairCommentContent = (TextView) mView.findViewById(R.id.repair_comments_content);
            repairCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            repairCommentNickname = (TextView) mView.findViewById(R.id.repair_comments_nickname);
            repairCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            repairCommentDate = (TextView) mView.findViewById(R.id.repair_comments_date);
            repairCommentDate.setText(date);
        }

    }
}
