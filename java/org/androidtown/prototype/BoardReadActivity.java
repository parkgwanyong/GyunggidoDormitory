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

public class BoardReadActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference mBoardDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mBoardCommentDatabse;
    private Board boardText;

    private TextView mBoardReadTitle;
    private TextView mBoardReadContent;
    private EditText mBoardReadComments;
    private Button mBoardReadSubmitBtn;

    private ImageView mBoardReadImage;

    private ProgressDialog mBoardCommentDialog;
    private ProgressDialog mBoardDeleteDialog;
    private ProgressDialog mBoardLoadDialog;

    private RecyclerView mBoardReadList;

    private MenuItem deleteBtn;

    private String imageUri = "empty";

    private User user;
    private String mNickname = "";

    private boolean checkDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_read);

        mBoardLoadDialog = new ProgressDialog(this);
        mBoardLoadDialog.setTitle("로딩중");
        mBoardLoadDialog.setMessage("잠시만 기다려주세요.");
        mBoardLoadDialog.show();

        mBoardReadList = (RecyclerView) findViewById(R.id.board_read_comment_list);
        mBoardReadList.setNestedScrollingEnabled(false);
        mBoardReadList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.board_read_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("자유 게시판");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBoardReadComments = (EditText) findViewById(R.id.board_read_comment_edit);
        mBoardReadSubmitBtn = (Button) findViewById(R.id.board_read_comment_submit_btn);
        mBoardReadTitle = (TextView) findViewById(R.id.board_read_title);
        mBoardReadContent = (TextView) findViewById(R.id.board_read_contents);

        mBoardReadImage = (ImageView) findViewById(R.id.board_read_image_view);

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

        mBoardDatabase = FirebaseDatabase.getInstance().getReference().child("Board").child(text_id);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        //로그인한 현재 계정

        ValueEventListener boardListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    boardText = dataSnapshot.getValue(Board.class);

                    mBoardReadTitle.setText(boardText.getTitle());
                    mBoardReadContent.setText(boardText.getContents());
                    imageUri = boardText.getImage_uri();
                    if(!imageUri.equals("empty")){
                        Picasso.with(getApplicationContext()).load(imageUri).into(mBoardReadImage, new com.squareup.picasso.Callback(){
                            @Override
                            public void onSuccess() {
                                mBoardLoadDialog.dismiss();
                            }

                            @Override
                            public void onError() {
                                mBoardLoadDialog.dismiss();
                                Toast.makeText(BoardReadActivity.this,"사진을 불러올 수 없습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mBoardLoadDialog.dismiss();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBoardLoadDialog.dismiss();
                Toast.makeText(BoardReadActivity.this,databaseError.toString(),Toast.LENGTH_SHORT).show();

            }
        };
        mBoardDatabase.addValueEventListener(boardListener);

        mBoardReadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageUri.equals("empty")){
                    Intent intent = new Intent(BoardReadActivity.this, ImageZoomActivity.class);
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
                    mNickname = user.getNickname();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(nicknameListener);

        mBoardReadSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = mBoardReadComments.getText().toString();
                commentsUpdate(comments);
                InputMethodManager inputManager = (InputMethodManager)
                        getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mBoardReadComments.getApplicationWindowToken(), 0);

                onStart();
            }
        });
    }

    private void commentsUpdate(String comments) {

        mBoardCommentDialog = new ProgressDialog(this);
        mBoardCommentDialog.setTitle("저장중");
        mBoardCommentDialog.setMessage("잠시만 기다려주세요.");
        mBoardCommentDialog.show();

        final HashMap<String, String> commentMap = new HashMap<String, String>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/ M/ dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        commentMap.put("comment",comments);
        commentMap.put("nickname",mNickname);
        commentMap.put("time",currentDateandTime.toString());
        commentMap.put("order_time", String.valueOf((System.currentTimeMillis())));
        mBoardCommentDatabse = mBoardDatabase.child("comments").push();

        mBoardCommentDatabse.setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mBoardReadComments.setText("");
                    mBoardCommentDialog.dismiss();

                }else{
                    mBoardCommentDialog.dismiss();
                    Toast.makeText(BoardReadActivity.this,"댓글이 저장되지 않았습니다.",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_read_bar, menu);
        deleteBtn = (MenuItem) menu.findItem(R.id.action_board_read_delete);
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
        Intent i = new Intent(BoardReadActivity.this, BoardActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(BoardReadActivity.this, BoardActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_board_read_delete:
                deleteBoard();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteBoard() {
        final AlertDialog alertDialog = new AlertDialog.Builder(BoardReadActivity.this)
                .setTitle("삭제")
                .setMessage("작성한 글을 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBoardDeleteDialog = new ProgressDialog(BoardReadActivity.this);
                        mBoardDeleteDialog.setTitle("삭제중");
                        mBoardDeleteDialog.setMessage("잠시만 기다려주세요.");
                        mBoardDeleteDialog.setCanceledOnTouchOutside(false);
                        mBoardDeleteDialog.show();

                        mBoardDatabase.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mBoardDeleteDialog.dismiss();
                                    Intent i = new Intent(BoardReadActivity.this, BoardActivity.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                                }else{
                                    mBoardDeleteDialog.dismiss();
                                    Toast.makeText(BoardReadActivity.this, "삭제되지 않았습니다. 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
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

        Query boardDatabaseQuery;
        boardDatabaseQuery = mBoardDatabase.child("comments").orderByChild("order_time");

        FirebaseRecyclerAdapter<BoardComment, boardCommentViewHolder> firebaseCommentRecyclerAdapter = new FirebaseRecyclerAdapter<BoardComment, boardCommentViewHolder>(
                BoardComment.class,
                R.layout.board_comments_layout,
                boardCommentViewHolder.class,
                boardDatabaseQuery

        ) {
            @Override
            protected void populateViewHolder(boardCommentViewHolder viewHolder, BoardComment model, int position) {
                viewHolder.setDate(model.getTime());
                viewHolder.setContent(model.getComment());
                viewHolder.setNickname(model.getNickname());


            }
        };
        mBoardReadList.setAdapter(firebaseCommentRecyclerAdapter);
    }
    public static class boardCommentViewHolder extends RecyclerView.ViewHolder{

        View mView;
        private TextView mBoardCommentContent;
        private TextView mBoardCommentNickname;
        private TextView mBoardCommentDate;

        public boardCommentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setContent(String content){
            mBoardCommentContent = (TextView) mView.findViewById(R.id.board_comments_content);
            mBoardCommentContent.setText(content);
        }
        public void setNickname(String nickname){
            mBoardCommentNickname = (TextView) mView.findViewById(R.id.board_comments_nickname);
            mBoardCommentNickname.setText(nickname);
        }
        public void setDate(String date){
            mBoardCommentDate = (TextView) mView.findViewById(R.id.board_comments_date);
            mBoardCommentDate.setText(date);
        }
    }
}
