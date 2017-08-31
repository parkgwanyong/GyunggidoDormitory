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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AnonymousReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference mAnonymousDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mAnonymousCommentDatabse;
    private Anonymous anonymousText;

    private TextView mAnonymousReadTitle;
    private TextView mAnonymousReadContent;
    private EditText mAnonymousReadComments;
    private Button mAnonymousReadSubmitBtn;

    private ProgressDialog mAnonymousCommentDialog;
    private ProgressDialog mAnonymousDeleteDialog;

    private RecyclerView mAnonymousReadList;

    private MenuItem deleteBtn;

    private User user;
    private String mName = "";

    private boolean checkDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_read);

        mAnonymousReadList = (RecyclerView) findViewById(R.id.anonymous_read_comment_list);
        mAnonymousReadList.setNestedScrollingEnabled(false);
        mAnonymousReadList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.anonymous_read_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("익명 게시판");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAnonymousReadComments = (EditText) findViewById(R.id.anonymous_read_comment_edit);
        mAnonymousReadSubmitBtn = (Button) findViewById(R.id.anonymous_read_comment_submit_btn);
        mAnonymousReadTitle = (TextView) findViewById(R.id.anonymous_read_title);
        mAnonymousReadContent = (TextView) findViewById(R.id.anonymous_read_contents);

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

        mAnonymousDatabase = FirebaseDatabase.getInstance().getReference().child("Anonymous").child(text_id);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        ValueEventListener anonymousListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    anonymousText = dataSnapshot.getValue(Anonymous.class);
                    mAnonymousReadTitle.setText(anonymousText.getTitle());
                    mAnonymousReadContent.setText(anonymousText.getContents());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AnonymousReadActivity.this,databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        };
        mAnonymousDatabase.addValueEventListener(anonymousListener);

        ValueEventListener nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                    mName = user.getName();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(nameListener);

        mAnonymousReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = mAnonymousReadComments.getText().toString();
                commentsUpdate(comments);
                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mAnonymousReadComments.getApplicationWindowToken(), 0);

                onStart();
            }
        });
    }

    private void commentsUpdate(String comments) {

        mAnonymousCommentDialog = new ProgressDialog(this);
        mAnonymousCommentDialog.setTitle("저장중");
        mAnonymousCommentDialog.setMessage("잠시만 기다려주세요.");
        mAnonymousCommentDialog.show();

        final HashMap<String, String> commentMap = new HashMap<String, String>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/ M/ dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("comment",comments);
        commentMap.put("name",mName);
        commentMap.put("time",currentDateandTime.toString());
        mAnonymousCommentDatabse = mAnonymousDatabase.child("comments").push();

        mAnonymousCommentDatabse.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mAnonymousReadComments.setText("");
                    mAnonymousCommentDialog.dismiss();

                }else{
                    mAnonymousCommentDialog.dismiss();
                    Toast.makeText(AnonymousReadActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anonymous_read_bar, menu);
        deleteBtn = (MenuItem) menu.findItem(R.id.action_anonymous_read_delete);
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
    public void onBackPressed() {
        Intent i = new Intent(AnonymousReadActivity.this, AnonymousActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(AnonymousReadActivity.this, AnonymousActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_anonymous_read_delete:
                deleteAnonymous();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAnonymous() {

        final AlertDialog alertDialog = new AlertDialog.Builder(AnonymousReadActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAnonymousDeleteDialog = new ProgressDialog(AnonymousReadActivity.this);
                        mAnonymousDeleteDialog.setTitle("삭제중");
                        mAnonymousDeleteDialog.setMessage("잠시만 기다려주세요.");
                        mAnonymousDeleteDialog.setCanceledOnTouchOutside(false);
                        mAnonymousDeleteDialog.show();

                        mAnonymousDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mAnonymousDeleteDialog.dismiss();
                                    Intent i = new Intent(AnonymousReadActivity.this, AnonymousActivity.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                                }else{
                                    mAnonymousDeleteDialog.dismiss();
                                    Toast.makeText(AnonymousReadActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
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
        FirebaseRecyclerAdapter<AnonymousComment, AnonymousCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<AnonymousComment, AnonymousCommentViewHolder>(
                AnonymousComment.class,
                R.layout.anonymous_comments_layout,
                AnonymousCommentViewHolder.class,
                mAnonymousDatabase.child("comments")

        ){
            @Override
            protected void populateViewHolder(AnonymousCommentViewHolder viewHolder, AnonymousComment model, int position) {
                viewHolder.setDate(model.getTime());
                viewHolder.setContent(model.getComment());
                viewHolder.setNickname("익명");

            }
        };
        mAnonymousReadList.setAdapter(firebaseCommentRecyclerAdapter);
    }

    public static class AnonymousCommentViewHolder extends RecyclerView.ViewHolder{

        View mView;
        private TextView mAnonymousCommentContent;
        private TextView mAnonymousCommentNickname;
        private TextView mAnonymousCommentDate;

        public AnonymousCommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setContent(String content){
            mAnonymousCommentContent = (TextView) mView.findViewById(R.id.anonymous_comments_content);
            mAnonymousCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            mAnonymousCommentNickname = (TextView) mView.findViewById(R.id.anonymous_comments_nickname);
            mAnonymousCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            mAnonymousCommentDate = (TextView) mView.findViewById(R.id.anonymous_comments_date);
            mAnonymousCommentDate.setText(date);
        }
    }
}
