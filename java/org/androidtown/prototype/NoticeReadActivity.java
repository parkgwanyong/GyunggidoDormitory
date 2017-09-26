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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class NoticeReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference mNoticeDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mNoticeCommentDatabse;
    private Notice noticeText;

    private TextView mNoticeReadTitle;
    private TextView mNoticeReadContent;
    private EditText mNoticeReadComments;
    private Button mNoticeReadSubmitBtn;

    private ImageView mNoticeReadImage;

    private ProgressDialog mNoticeCommentDialog;
    private ProgressDialog mNoticeDeleteDialog;
    private ProgressDialog mNoticeLoadDialog;

    private RecyclerView mNoticeReadList;

    private MenuItem deleteBtn;

    private String imageUri = "empty";

    private User user;
    private String mNickname = "";

    private boolean checkDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_read);

        mNoticeLoadDialog = new ProgressDialog(this);
        mNoticeLoadDialog.setTitle("로딩중");
        mNoticeLoadDialog.setMessage("잠시만 기다려주세요.");
        mNoticeLoadDialog.show();

        mNoticeReadList = (RecyclerView) findViewById(R.id.notice_read_comment_list);
        mNoticeReadList.setNestedScrollingEnabled(false);
        mNoticeReadList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.notice_read_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("공지사항");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNoticeReadComments = (EditText) findViewById(R.id.notice_read_comment_edit);
        mNoticeReadSubmitBtn = (Button) findViewById(R.id.notice_read_comment_submit_btn);
        mNoticeReadTitle = (TextView) findViewById(R.id.notice_read_title);
        mNoticeReadContent = (TextView) findViewById(R.id.notice_read_contents);

        mNoticeReadImage = (ImageView) findViewById(R.id.notice_read_image_view);

        final String user_id = getIntent().getStringExtra("user_id");
        //글 작성자
        final String text_id = getIntent().getStringExtra("text_id");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String current_user_id = current_user.getUid();
        //로그인한 유저

        if(user_id.equals(current_user_id)){
            checkDelete = true;

        }else{
            checkDelete = false;
        }
        invalidateOptionsMenu();

        mNoticeDatabase = FirebaseDatabase.getInstance().getReference().child("Notice").child(text_id);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        //현재 계정

        ValueEventListener noticeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    noticeText = dataSnapshot.getValue(Notice.class);

                    mNoticeReadTitle.setText(noticeText.getTitle());
                    mNoticeReadContent.setText(noticeText.getContents());
                    imageUri = noticeText.getImage_uri();
                    if(!imageUri.equals("empty")){
                        Picasso.with(getApplicationContext()).load(imageUri).into(mNoticeReadImage, new com.squareup.picasso.Callback(){
                            @Override
                            public void onSuccess() {
                                mNoticeLoadDialog.dismiss();
                            }

                            @Override
                            public void onError() {
                                mNoticeLoadDialog.dismiss();
                                Toast.makeText(NoticeReadActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mNoticeLoadDialog.dismiss();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mNoticeLoadDialog.dismiss();
                Toast.makeText(NoticeReadActivity.this, "오류 :" + databaseError.toString(),Toast.LENGTH_SHORT).show();

            }
        };
        mNoticeDatabase.addValueEventListener(noticeListener);

        mNoticeReadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageUri.equals("empty")){
                    Intent intent = new Intent(NoticeReadActivity.this, ImageZoomActivity.class);
                    intent.putExtra("image_uri",imageUri);
                    startActivity(intent);
                    overridePendingTransition(R.anim.below_in, R.anim.below_out);
                }
            }
        });

        ValueEventListener nicknameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                    if(user.getCouncil().equals("자율회")){
                        mNickname = "자율회";
                    }else if(user.getmClass().equals("직원")){
                        mNickname = "직원";
                    } else{
                        mNickname = user.getNickname();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(nicknameListener);

        mNoticeReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = mNoticeReadComments.getText().toString();
                commentsUpdate(comments);
                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mNoticeReadComments.getApplicationWindowToken(), 0);

                onStart();
            }
        });





    }

    private void commentsUpdate(String comments) {

        mNoticeCommentDialog = new ProgressDialog(this);
        mNoticeCommentDialog.setTitle("저장중");
        mNoticeCommentDialog.setMessage("잠시만 기다려주세요.");
        mNoticeCommentDialog.show();

        final HashMap<String, String> commentMap = new HashMap<String, String>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/ M/ dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("comment",comments);
        commentMap.put("nickname",mNickname);
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("council",user.getCouncil());
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("order_time", String.valueOf((System.currentTimeMillis())));
        mNoticeCommentDatabse = mNoticeDatabase.child("comments").push();
        //final String commentTextKey = mNoticeCommentDatabse.getKey();

        mNoticeCommentDatabse.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mNoticeReadComments.setText("");
                    mNoticeCommentDialog.dismiss();

                }else{
                    mNoticeCommentDialog.dismiss();
                    Toast.makeText(NoticeReadActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_read_bar, menu);
        deleteBtn = (MenuItem) menu.findItem(R.id.action_notice_read_delete);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(checkDelete){
            deleteBtn.setVisible(true);
            deleteBtn.setEnabled(true);
        }else{
            deleteBtn.setVisible(false);
            deleteBtn.setEnabled(false);
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }


    @Override
    public void onBackPressed(){
        Intent i = new Intent(NoticeReadActivity.this, NoticeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

    }
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(NoticeReadActivity.this, NoticeActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_notice_read_delete:
                deleteNotice();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteNotice() {

        final AlertDialog alertDialog = new AlertDialog.Builder(NoticeReadActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNoticeDeleteDialog = new ProgressDialog(NoticeReadActivity.this);
                        mNoticeDeleteDialog.setTitle("삭제중");
                        mNoticeDeleteDialog.setMessage("잠시만 기다려주세요.");
                        mNoticeDeleteDialog.setCanceledOnTouchOutside(false);
                        mNoticeDeleteDialog.show();
                        mNoticeDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mNoticeDeleteDialog.dismiss();
                                    Intent i = new Intent(NoticeReadActivity.this, NoticeActivity.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                                }else{
                                    mNoticeDeleteDialog.dismiss();
                                    Toast.makeText(NoticeReadActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();

        Query noticeDatabaseQuery;
        noticeDatabaseQuery = mNoticeDatabase.child("comments").orderByChild("order_time");

        FirebaseRecyclerAdapter<NoticeComment, noticeCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<NoticeComment, noticeCommentViewHolder>(
                NoticeComment.class,
                R.layout.notice_comments_layout,
                noticeCommentViewHolder.class,
                noticeDatabaseQuery

        ){
            @Override
            protected void populateViewHolder(noticeCommentViewHolder viewHolder, NoticeComment model, int position) {
                viewHolder.setDate(model.getTime());
                viewHolder.setContent(model.getComment());
                viewHolder.setNickname(model.getNickname());


            }
        };
        mNoticeReadList.setAdapter(firebaseCommentRecyclerAdapter);
    }

    public static class noticeCommentViewHolder extends RecyclerView.ViewHolder{

        View mView;
        private TextView mNoticeCommentContent;
        private TextView mNoticeCommentNickname;
        private TextView mNoticeCommentDate;

        public noticeCommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setContent(String content){
            mNoticeCommentContent = (TextView) mView.findViewById(R.id.notice_comments_content);
            mNoticeCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            mNoticeCommentNickname = (TextView) mView.findViewById(R.id.notice_comments_nickname);
            mNoticeCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            mNoticeCommentDate = (TextView) mView.findViewById(R.id.notice_comments_date);
            mNoticeCommentDate.setText(date);
        }

    }
}
