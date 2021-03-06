package org.androidtown.prototype;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private View mLineView;
    private LinearLayout mBuildingLayout;

    private EditText mName;
    private EditText mNickname;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mBirth;
    private EditText mBuildingNumber;
    private EditText mPhoneNumber;

    private String mclass;
    private String mBuilding;

   private ProgressDialog mRegProgress;
    private ProgressDialog mCheckProgress;

    private Button registerSignupBtn;
    private Button cancelBtn;
    private Button mExistCheckBtn;

    private Spinner spinner;
    private Spinner memberSpinner;
    ArrayAdapter<CharSequence> memberAdapter;
    ArrayAdapter<CharSequence> buildingAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mNicknameDatabase;

    private boolean existCheckBool;
    private boolean nicknameChanged;

    private static final String TAG = "Register";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nicknameChanged = false;
        existCheckBool = false;

        mLineView = (View) findViewById(R.id.register_buildingLine);
        mBuildingLayout = (LinearLayout) findViewById(R.id.register_BuildingLayout);

        mRegProgress = new ProgressDialog(this);
        mCheckProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mName = (EditText) findViewById(R.id.register_name);
        mNickname = (EditText) findViewById(R.id.register_NickName);
        mEmail = (EditText) findViewById(R.id.register_email) ;
        mPassword = (EditText) findViewById(R.id.register_password);
        mConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        mBirth = (EditText) findViewById(R.id.register_birth);
        mBuildingNumber = (EditText) findViewById(R.id.register_building_number);
        mPhoneNumber = (EditText) findViewById(R.id.register_phone);

        registerSignupBtn = (Button) findViewById(R.id.register_signup_btn);
        cancelBtn = (Button) findViewById(R.id.register_cancel_btn);
        mExistCheckBtn = (Button) findViewById(R.id.register_existCheck_btn);

        spinner = (Spinner) findViewById(R.id.register_building_spinner);
        memberSpinner = (Spinner)findViewById(R.id.register_member_spinner);

        buildingAdapter = ArrayAdapter.createFromResource(this,R.array.building_arrays,android.R.layout.simple_spinner_item);
        buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(buildingAdapter);

        memberAdapter = ArrayAdapter.createFromResource(this,R.array.member_arrays,android.R.layout.simple_spinner_item);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(memberAdapter);

        registerSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString();
                String nickname = mNickname.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();
                String birthDate = mBirth.getText().toString();
                String buildingNumber = mBuildingNumber.getText().toString();
                String phoneNumber =  mPhoneNumber.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this,"이름을 입력해 주세요.",Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(nickname)) {
                    Toast.makeText(RegisterActivity.this, "별명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if(!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(birthDate)) {
                    Toast.makeText(RegisterActivity.this, "생년월일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else if(birthDate.length() != 6 ){
                    Toast.makeText(RegisterActivity.this, "생년월일을 확인해 주세요", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(RegisterActivity.this, "핸드폰 번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if(phoneNumber.length() != 11){
                    Toast.makeText(RegisterActivity.this, "핸드폰 번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                } else if(mclass.equals("재사생") && TextUtils.isEmpty(buildingNumber)) {
                    Toast.makeText(RegisterActivity.this, "방 번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else if(mclass.equals("재사생") && buildingNumber.length() != 3){
                    Toast.makeText(RegisterActivity.this, "방 번호를 확인해 주세요", Toast.LENGTH_SHORT).show();
                }else{
                    mRegProgress.setTitle("등록중");
                    mRegProgress.setMessage("등록하는 동안 잠시만 기다려 주세요.");
                    mRegProgress.setCanceledOnTouchOutside(false);

                    mRegProgress.show();

                    register_user(view,name, nickname, email, password, birthDate, buildingNumber, mclass, mBuilding, phoneNumber);

                }
            }
        });
        mNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nicknameChanged = true;
            }
        });


        mExistCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckProgress.setTitle("확인 중");
                mCheckProgress.setMessage("잠시만 기다려 주세요.");
                mCheckProgress.setCanceledOnTouchOutside(false);
                mCheckProgress.show();
                nicknameChanged = false;

                final User user = new User();
                mNicknameDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                mNicknameDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Log.d(TAG, ds.getValue(User.class).getNickname());
                                user.setNickname(ds.getValue(User.class).getNickname());
                                if (StringManipulation.expandNIckname(user.getNickname()).equals(mNickname.getText().toString())) {
                                    existCheckBool = true;

                                    Toast.makeText(RegisterActivity.this, "별명이 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                                    break;
                                } else {

                                    existCheckBool = false;
                                }
                            }

                        mCheckProgress.dismiss();
                        if(!mNickname.getText().toString().equals("") && nicknameChanged == false && existCheckBool == false){
                            Toast.makeText(RegisterActivity.this,"사용가능한 별명입니다.",Toast.LENGTH_SHORT).show();
                        } else if(mNickname.getText().toString().equals("")){
                            Toast.makeText(RegisterActivity.this,"별명을 입력해 주세요.",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch(i) {
                    case 0: mBuilding = "율곡관";
                        break;
                    case 1: mBuilding = "다산관";
                        break;
                    case 2: mBuilding = "신사관";
                        break;
                    case 3: mBuilding = "추계관";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch(i) {
                    case 0: mclass = "재사생";
                        spinner.setVisibility(View.VISIBLE);
                        spinner.setEnabled(true);
                        mBuildingNumber.setVisibility(View.VISIBLE);
                        mBuildingNumber.setEnabled(true);
                        mBuildingLayout.setVisibility(View.VISIBLE);
                        mLineView.setVisibility(View.VISIBLE);
                        break;
                    case 1: mclass = "졸업생";
                        spinner.setVisibility(View.INVISIBLE);
                        spinner.setEnabled(false);
                        mBuildingNumber.setVisibility(View.INVISIBLE);
                        mBuildingNumber.setEnabled(false);
                        mBuildingLayout.setVisibility(View.INVISIBLE);
                        mLineView.setVisibility(View.INVISIBLE);
                        break;
                    case 2: mclass = "직원";
                        spinner.setVisibility(View.INVISIBLE);
                        spinner.setEnabled(false);
                        mBuildingNumber.setVisibility(View.INVISIBLE);
                        mBuildingNumber.setEnabled(false);
                        mBuildingLayout.setVisibility(View.INVISIBLE);
                        mLineView.setVisibility(View.INVISIBLE);
                        break;
                    case 3: mclass = "외부인";
                        spinner.setVisibility(View.INVISIBLE);
                        spinner.setEnabled(false);
                        mBuildingNumber.setVisibility(View.INVISIBLE);
                        mBuildingNumber.setEnabled(false);
                        mBuildingLayout.setVisibility(View.INVISIBLE);
                        mLineView.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                final AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("뒤로가기")
                        .setMessage("작성을 취소하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                finish();
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
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#e25049"));
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#ffffff"));
                    }
                });
                alertDialog.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        final AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle("뒤로가기")
                .setMessage("작성을 취소하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        finish();
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
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#e25049"));
            }
        });
        alertDialog.show();
    }

    private void register_user(final View view, final String name, final String nickname, final String email, final String password, final String birthDate, final String buildingNumber, final String mclass, final String mBuilding, final String phoneNumber) {
        if(nicknameChanged==false && existCheckBool == false) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = current_user.getUid();

                        mNicknameDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


                        HashMap<String, String> userMap = new HashMap<String, String>();
                        userMap.put("mClass", mclass);
                        userMap.put("name", name);
                        userMap.put("nickname", nickname);
                        userMap.put("email", email);
                        userMap.put("password", password);
                        userMap.put("birth_date", birthDate);
                        if(mclass.equals("직원")){
                            userMap.put("council","직원");
                        }else{
                            userMap.put("council","false");
                        }

                        userMap.put("phone_number",phoneNumber);
                        if(mclass.equals("재사생")) {
                            userMap.put("building", mBuilding);
                            userMap.put("building_number", buildingNumber);
                        } else {
                            userMap.put("building", "empty");
                            userMap.put("building_number", "empty");
                        }
                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mRegProgress.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "등록 오류! 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        mRegProgress.hide();
                        String error = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            error = "비밀번호를 여섯자리 이상으로 입력해 주세요.";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            error = "이메일을 확인해 주세요";
                        } catch (FirebaseAuthUserCollisionException e) {
                            error = "이미 존재하는 계정입니다.";
                        } catch (Exception e) {
                            error = "알 수 없는 오류.";
                            e.printStackTrace();

                        }
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            mRegProgress.dismiss();
            Toast.makeText(RegisterActivity.this, "별명 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
