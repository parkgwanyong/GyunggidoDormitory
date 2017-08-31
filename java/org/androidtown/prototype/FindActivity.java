package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindActivity extends AppCompatActivity {

    private static final String TAG = "findpassword";

    private EditText mFindEmail;
    private EditText mFindName;
    private EditText mFindBirth;
    private TextView mFindPassword;
    private LinearLayout mFindLayout;
    private Button mFindBtn;
    private Button mFindCancelBtn;

    private DatabaseReference mFindDatabase;

    private ProgressDialog mFindProgress;

    private boolean mFindBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        mFindBool = false;

        mFindProgress = new ProgressDialog(this);

        mFindEmail = (EditText) findViewById(R.id.find_email);
        mFindName = (EditText) findViewById(R.id.find_name);
        mFindBirth = (EditText) findViewById(R.id.find_birth);
        mFindPassword = (TextView) findViewById(R.id.find_password);
        mFindLayout = (LinearLayout) findViewById(R.id.find_layout);
        mFindLayout.setVisibility(View.INVISIBLE);
        mFindBtn = (Button) findViewById(R.id.find_btn);
        mFindCancelBtn = (Button) findViewById(R.id.find_cancel_btn);

        mFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mFindEmail.getText().toString();
                String name = mFindName.getText().toString();
                String birth = mFindBirth.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(FindActivity.this,"이메일을 입력해 주세요.",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(name)) {
                    Toast.makeText(FindActivity.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(birth)) {
                    Toast.makeText(FindActivity.this, "생년월일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else{
                    mFindProgress.setTitle("찾는중");
                    mFindProgress.setMessage("잠시만 기다려 주세요.");
                    mFindProgress.setCanceledOnTouchOutside(false);

                    mFindProgress.show();

                    find_user(email, name, birth);
                }
            }
        });
        mFindCancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                finish();

            }
        });
    }

    private void find_user(final String email, final String name, final String birth) {

        final User user = new User();
        mFindDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mFindDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    user.setEmail(ds.getValue(User.class).getEmail());
                    user.setName(ds.getValue(User.class).getName());
                    user.setBirth_date(ds.getValue(User.class).getBirth_date());
                    user.setPassword(ds.getValue(User.class).getPassword());
                    Log.d(TAG,StringManipulation.expandNIckname(user.getEmail()));
                    if (user.getEmail().equals(email) &&
                            user.getName().equals(name) &&
                            user.getBirth_date().equals(birth)){
                        mFindPassword.setText(user.getPassword());
                       mFindLayout.setVisibility(View.VISIBLE);
                        mFindBool = true;
                        break;
                    }
                    else{
                        mFindBool = false;
                    }
                }
                if(mFindBool == false){
                    Toast.makeText(FindActivity.this, "계정을 찾을 수 없습니다. 정보를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
                mFindProgress.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
