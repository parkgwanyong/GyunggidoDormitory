package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NoticeActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mNoticeList;

    private ProgressDialog mNoticeMainDialog;
    private ProgressDialog mNoticeLoadingDialog;

    private DatabaseReference mNoticeDatabase;
    private DatabaseReference mDatabase;

    private MenuItem writeBtn;

    private User user;
    private boolean checkCouncil = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        mNoticeMainDialog = new ProgressDialog(this);

        mNoticeMainDialog.setTitle("로딩중");
        mNoticeMainDialog.setMessage("잠시만 기다려주세요.");
        mNoticeMainDialog.show();

        mToolbar = (Toolbar) findViewById(R.id.notice_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("공지사항");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNoticeList = (RecyclerView) findViewById(R.id.notice_list);
        mNoticeList.setHasFixedSize(true);
        mNoticeList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        ValueEventListener noticeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                    if(user.getCouncil().equals("자울회") || user.getmClass().equals("직원") || user.getNickname().equals("Developer")){
                        checkCouncil = true;
                    }else{
                        checkCouncil = false;
                    }
                    invalidateOptionsMenu();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(noticeListener);

        mNoticeDatabase = FirebaseDatabase.getInstance().getReference().child("Notice");

    }

    @Override
    protected void onStart() {
        super.onStart();



        Query noticeQuery;
        noticeQuery = mNoticeDatabase.orderByChild("order_time");

        FirebaseRecyclerAdapter<Notice, noticeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notice, noticeViewHolder>(
                Notice.class,
                R.layout.notice_single_layout,
                noticeViewHolder.class,
                noticeQuery
        ) {
            @Override
            protected void populateViewHolder(noticeViewHolder viewHolder, Notice model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setCouncil(model.getCouncil());
                viewHolder.setDate(model.getTime());

                final String user_id = model.getUserID();
                final String text_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent noticeIntent = new Intent(NoticeActivity.this,NoticeReadActivity.class);
                        noticeIntent.putExtra("user_id", user_id);
                        noticeIntent.putExtra("text_id", text_id);
                        startActivity(noticeIntent);
                        overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        finish();
                    }
                });
            }

            @Override
            protected void onDataChanged() {
                if (mNoticeMainDialog != null && mNoticeMainDialog.isShowing()) {
                    mNoticeMainDialog.dismiss();
                }
            }
        };
        mNoticeList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_bar, menu);
        writeBtn = (MenuItem) menu.findItem(R.id.action_notice_write);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(checkCouncil){
            writeBtn.setVisible(true);
            writeBtn.setEnabled(true);
        }else{
            writeBtn.setVisible(false);
            writeBtn.setEnabled(false);
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(NoticeActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(NoticeActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_notice_write:
                Intent i = new Intent(NoticeActivity.this, NoticeWriteActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.below_in, R.anim.below_out);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    public static class noticeViewHolder extends RecyclerView.ViewHolder{

        View mView;
        private TextView mNoticeTitle;
        private TextView mNoticePosition;
        private TextView mNoticeDate;


        public noticeViewHolder(View itemView){
            super(itemView);
            mView = itemView;

        }

        public void setTitle(String title){

            mNoticeTitle = (TextView) mView.findViewById(R.id.notice_single_title);
            mNoticeTitle.setText(title);
        }
        public void setDate(String date) {

            mNoticeDate = (TextView) mView.findViewById(R.id.notice_single_date);
            mNoticeDate.setText(date);

        }

        public void setCouncil(String council){

            mNoticePosition = (TextView) mView.findViewById(R.id.notice_single_position);
            mNoticePosition.setText(council);
        }

    }
}
