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

public class AnonymousActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mAnonymousList;

    private ProgressDialog mAnonymousMainDialog;
    private ProgressDialog mAnonymousLoadingDialog;

    private DatabaseReference mAnonymousDatabase;
    private DatabaseReference mDatabase;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous);

        mAnonymousMainDialog = new ProgressDialog(this);

        mAnonymousMainDialog.setTitle("로딩중");
        mAnonymousMainDialog.setMessage("잠시만 기다려주세요.");
        mAnonymousMainDialog.show();

        mToolbar = (Toolbar) findViewById(R.id.anonymous_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("익명 게시판");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAnonymousList = (RecyclerView) findViewById(R.id.anonymous_list);
        mAnonymousList.setHasFixedSize(true);
        mAnonymousList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mAnonymousDatabase = FirebaseDatabase.getInstance().getReference().child("Anonymous");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query anonymousQuery;
        anonymousQuery = mAnonymousDatabase.orderByChild("order_time");

        FirebaseRecyclerAdapter<Anonymous, AnonymousViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Anonymous, AnonymousViewHolder>(
                Anonymous.class,
                R.layout.anonymous_single_layout,
                AnonymousViewHolder.class,
                anonymousQuery
        ){
            @Override
            protected void populateViewHolder(AnonymousViewHolder viewHolder, Anonymous model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setNickname("익명");
                viewHolder.setDate(model.getTime());

                final String user_id = model.getUserID();
                final String text_id = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent anonymousIntent = new Intent(AnonymousActivity.this, AnonymousReadActivity.class);
                        anonymousIntent.putExtra("user_id", user_id);
                        anonymousIntent.putExtra("text_id", text_id);
                        startActivity(anonymousIntent);
                        overridePendingTransition(R.anim.left_in,R.anim.left_out);
                        finish();
                    }
                });
            }

            @Override
            protected void onDataChanged() {
                if (mAnonymousMainDialog != null && mAnonymousMainDialog.isShowing()) {
                    mAnonymousMainDialog.dismiss();
                }
            }
        };
        mAnonymousList.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anonymous_bar, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(AnonymousActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(AnonymousActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_anonymous_write:
                Intent i = new Intent(AnonymousActivity.this, AnonymousWriteActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.below_in, R.anim.below_out);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };



    public static class AnonymousViewHolder extends RecyclerView.ViewHolder{

        View mView;
        private TextView mAnonymousTitle;
        private TextView mAnonymousNickname;
        private TextView mAnonymousDate;


        public AnonymousViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){

            mAnonymousTitle = (TextView) mView.findViewById(R.id.anonymous_single_title);
            mAnonymousTitle.setText(title);
        }
        public void setDate(String date) {

            mAnonymousDate = (TextView) mView.findViewById(R.id.anonymous_single_date);
            mAnonymousDate.setText(date);

        }

        public void setNickname(String council){

            mAnonymousNickname = (TextView) mView.findViewById(R.id.anonymous_single_position);
            mAnonymousNickname.setText(council);
        }
    }
}
