package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BoardActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mBoardList;

    private ProgressDialog mBoardMainDialog;
    private ProgressDialog mBoardLoadingDialog;

    private DatabaseReference mBoardDatabase;
    private DatabaseReference mDatabase;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        mBoardMainDialog = new ProgressDialog(this);

        mBoardMainDialog.setTitle("로딩중");
        mBoardMainDialog.setMessage("잠시만 기다려주세요.");
        mBoardMainDialog.show();

        mToolbar = (Toolbar) findViewById(R.id.board_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("자유 게시판");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBoardList = (RecyclerView) findViewById(R.id.board_list);
        mBoardList.setHasFixedSize(true);
        mBoardList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mBoardDatabase = FirebaseDatabase.getInstance().getReference().child("Board");

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query boardQuery;
        boardQuery = mBoardDatabase.orderByChild("order_time");

        FirebaseRecyclerAdapter<Board, BoardViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Board, BoardViewHolder>(
                Board.class,
                R.layout.board_single_layout,
                BoardViewHolder.class,
                boardQuery
        ) {
            @Override
            protected void populateViewHolder(BoardViewHolder viewHolder, Board model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setNickname(model.getNickname());
                viewHolder.setDate(model.getTime());

                final String user_id = model.getUserID();
                final String text_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent boardIntent = new Intent(BoardActivity.this, BoardReadActivity.class);
                        boardIntent.putExtra("user_id", user_id);
                        boardIntent.putExtra("text_id", text_id);
                        startActivity(boardIntent);
                        overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        finish();
                    }
                });

            }

            @Override
            protected void onDataChanged() {
                if (mBoardMainDialog != null && mBoardMainDialog.isShowing()) {
                    mBoardMainDialog.dismiss();
                }
            }
        };
        mBoardList.setAdapter(firebaseRecyclerAdapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_bar, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(BoardActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(BoardActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_board_write:
                Intent i = new Intent(BoardActivity.this, BoardWriteActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.below_in, R.anim.below_out);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    public static class BoardViewHolder extends RecyclerView.ViewHolder{

        View mView;
        private TextView mBoardTitle;
        private TextView mBoardNickname;
        private TextView mBoardDate;

        public BoardViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){

            mBoardTitle = (TextView) mView.findViewById(R.id.board_single_title);
            mBoardTitle.setText(title);
        }
        public void setDate(String date) {

            mBoardDate = (TextView) mView.findViewById(R.id.board_single_date);
            mBoardDate.setText(date);

        }

        public void setNickname(String council){

            mBoardNickname = (TextView) mView.findViewById(R.id.board_single_position);
            mBoardNickname.setText(council);
        }
    }
}
