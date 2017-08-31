package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RepairActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRepairMainList;

    private DatabaseReference mRepairDatabase;
    private DatabaseReference mRepairStaffDatabase;
    private DatabaseReference mDatabase;

    private ProgressDialog mRepairMainProgress;

    private User user;
    private String mClass="";
    private String mNickname="";
    private boolean onDataChangedCalled = false;


    public String mStatus;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);

        mRepairMainProgress = new ProgressDialog(this);

        mRepairMainProgress.setTitle("로딩중");
        mRepairMainProgress.setMessage("잠시만 기다려주세요.");
        mRepairMainProgress.show();

        mToolbar = (Toolbar) findViewById(R.id.repair_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("수리 요청");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = current_user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        //Toast.makeText(getApplicationContext(), stat, Toast.LENGTH_SHORT).show();




        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                mNickname = user.getNickname();
                mClass = user.getmClass();
                onDataChangedCalled = true;
                if(mClass.equals("직원") || mNickname.equals("Developer")){
                    mRepairDatabase = FirebaseDatabase.getInstance().getReference().child("Repair_staff");

                }else{
                    mRepairDatabase = FirebaseDatabase.getInstance().getReference().child("Repair").child(uid);
                }
                onStart();
                //Toast.makeText(getApplicationContext(), String.valueOf(onDataChangedCalled),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(getApplicationContext(), databaseError.toException().toString(),Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(userListener);


        //Toast.makeText(getApplicationContext(), String.valueOf(onDataChangedCalled),Toast.LENGTH_SHORT).show();








        //Toast.makeText(getApplicationContext(), user.getmClass(), Toast.LENGTH_SHORT).show();


        mRepairMainList = (RecyclerView) findViewById(R.id.repair_list);
        mRepairMainList.setHasFixedSize(true);
        mRepairMainList.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(onDataChangedCalled){


            Query repairDatabaseQuery;
            repairDatabaseQuery = mRepairDatabase.orderByChild("order_time");

            FirebaseRecyclerAdapter<RepairMainDisplay,RepairViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RepairMainDisplay, RepairViewHolder>(
                    RepairMainDisplay.class,
                    R.layout.repair_single_layout,
                    RepairViewHolder.class,
                    repairDatabaseQuery
            ) {
                @Override
                protected void populateViewHolder(RepairViewHolder viewHolder, RepairMainDisplay model, int position) {

                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setStatus(model.getStatus());
                    viewHolder.setNickname(model.getNickname());
                    viewHolder.setDate(model.getTime());
                    viewHolder.setBackgroundColor(model.getStatus());

                    final String user_id = model.getUserID();
                    final String text_id = getRef(position).getKey();

                    viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent repairIntent = new Intent(RepairActivity.this,RepairSingleActivity.class);
                            repairIntent.putExtra("user_id", user_id);
                            repairIntent.putExtra("text_id", text_id);
                            startActivity(repairIntent);
                            overridePendingTransition(R.anim.left_in,R.anim.left_out);
                            finish();
                        }
                    });

                    //final String user_id = getRef(position).getKey();
                }

                @Override
                protected void onDataChanged() {
                    if (mRepairMainProgress != null && mRepairMainProgress.isShowing()) {
                        mRepairMainProgress.dismiss();
                    }
                }
            };
            mRepairMainList.setAdapter(firebaseRecyclerAdapter);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repair_bar, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(RepairActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(RepairActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                return true;
            case R.id.action_repair_write:
                Intent i = new Intent(RepairActivity.this, RepairWriteActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.below_in, R.anim.below_out);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };



    public static class RepairViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView repairTitleView;
        private TextView repairStatusView;
        private TextView repairNicknameView;
        private TextView repairDateView;
        private LinearLayout mRepairLayout;
        //private Spinner mRepairMainSpinner;


        public RepairViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setTitle(String title) {

            repairTitleView = (TextView) mView.findViewById(R.id.repair_single_title);
            repairTitleView.setText(title);

        }

        public void setStatus(String status){
            repairStatusView = (TextView) mView.findViewById(R.id.repair_single_status);
            repairStatusView.setText(status);


            /*
           mRepairMainSpinner = (Spinner) mView.findViewById(R.id.repair_main_spinner);

            ArrayAdapter<CharSequence> repairAdapter = ArrayAdapter.createFromResource(ctx,R.array.repair_array,android.R.layout.simple_spinner_item);
            repairAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mRepairMainSpinner.setAdapter(repairAdapter);
            */

        }
        public void setNickname(String nickname) {

            repairNicknameView = (TextView) mView.findViewById(R.id.repair_single_nickname);
            repairNicknameView.setText(nickname);

        }
        public void setDate(String date) {

            repairDateView = (TextView) mView.findViewById(R.id.repair_single_date);
            repairDateView.setText(date);

        }

        public void setBackgroundColor(String backgroundColor) {

            mRepairLayout = (LinearLayout) mView.findViewById(R.id.repair_main_single_layout);

            switch(backgroundColor){
                case "미확인":
                    mRepairLayout.setBackgroundColor(Color.parseColor("#fafafa"));
                    break;
                case "수리중":
                    mRepairLayout.setBackgroundColor(Color.parseColor("#FFF176"));
                    break;
                case "완료":
                    mRepairLayout.setBackgroundColor(Color.parseColor("#CCFF90"));
                    break;

            }
        }
    }
}
