package com.wanjy.dannie.rivchat.ui;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.widget.ProgressBar;
import android.view.View;
import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
//import android.annotation.NonNull;

import com.wanjy.dannie.rivchat.Home;
import android.app.Activity;
import android.content.SharedPreferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import android.util.Log;
import android.content.Context;
import com.wanjy.dannie.rivchat.R;

public class LoginActivity extends AppCompatActivity {

    EditText emailEdit,passwordEdit;
    Button LoginBtn;
    TextView SignUpText,forgetText;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_NETWORK_STATE
    };
    ProgressDialog pd;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mAuth = FirebaseAuth.getInstance();
        
        emailEdit = (EditText) findViewById(R.id.editTextEmail);
        passwordEdit = (EditText) findViewById(R.id.editTextPassword);
        
        sharedd = getApplicationContext().getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);
        emailEdit.setText(sharedd.getString("email",""));        
        passwordEdit.setText(sharedd.getString("password",""));
        
        
        LoginBtn = (Button) findViewById(R.id.cirLoginButton);        
        SignUpText = (TextView) findViewById(R.id.signupText);
        forgetText = (TextView) findViewById(R.id.forgetText);

       /* final SharedPreferences sharedd = getApplicationContext().getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);
        if(sharedd.getString("myid","").equals("")){
            Intent back = new Intent(LoginActivity.this,Home.class);
            startActivity(back);
            finish();
           }
        */

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        pd = new ProgressDialog(LoginActivity.this);
        RegisterActivity.level=0;



        forgetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emailEdit.getText().toString().isEmpty()||emailEdit.getText().toString().length()<6){
                    showMessage("من فضلك اكتب البريد الالكتروني في صندوق البريد");
                    emailEdit.setError("اكتب البريد فقط");
                }else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailEdit.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showMessage("تم ارسال كلمة السر الي بريدك الالكتروني , تصفح رسائلك");
                                    }
                                }
                            });
                }


            }
        });


        LoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                                        
                    pd.setMessage("Checking");
                    pd.show();
                    
                    LoginBtn.setEnabled(false);

                    final String log_email = emailEdit.getText().toString().trim();
                    final String log_pass = passwordEdit.getText().toString().trim();

                    if(log_email.isEmpty() || log_pass.isEmpty()){
                        showMessage("Please Enter All Fields");
                        pd.dismiss();
                        LoginBtn.setEnabled(true);
                    }else {
                        signIn(log_email,log_pass);
                       //showMessage(log_email);
                    }
        }});
                    
        SignUpText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    Intent kl = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(kl);
                }                                                
            });   
                
                
                
                
    }
    
    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

    }

    private void signIn(String log_email, final String log_pass) {

        mAuth.signInWithEmailAndPassword(log_email,log_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        pd.dismiss();
                        LoginBtn.setEnabled(true);
                        
                        
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference usersRef = rootRef.child("user");
                        DatabaseReference uidRef = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    
                                    sharedd.edit().putString("name", dataSnapshot.child("name").getValue(String.class)).commit();
                                    sharedd.edit().putString("email", dataSnapshot.child("email").getValue(String.class)).commit();
                                    sharedd.edit().putString("password", log_pass).commit();
                                    sharedd.edit().putString("avata", dataSnapshot.child("avata").getValue(String.class)).commit();
                                    sharedd.edit().putString("myid", dataSnapshot.child("myId").getValue(String.class)).commit();
                                    sharedd.edit().putString("qrcode", dataSnapshot.child("qrcode").getValue(String.class)).commit();
                                    sharedd.edit().putString("group", dataSnapshot.child("group").getValue(String.class)).commit();
                                    sharedd.edit().putString("title", dataSnapshot.child("title").getValue(String.class)).commit();
                                    sharedd.edit().putString("telegram", dataSnapshot.child("telegram").getValue(String.class)).commit();



                                    changeActivity();                                   
                                }
                                
                                
                                
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("TAG", databaseError.getMessage());
                            }
                        };
                        uidRef.addListenerForSingleValueEvent(eventListener);
                        uidRef.keepSynced(true);




                    }else {
                        showMessage("Login Error Occured : " + task.getException().getMessage());
                        pd.dismiss();
                        LoginBtn.setEnabled(true);
                    }
                }
            });
            }
    
    
    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            finish();
            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            Toast.makeText(LoginActivity.this, "Email Not Verified! Please Verify first and then Login...", Toast.LENGTH_LONG).show();
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();
            //restart this activity
        }
    }
    
    

    private void changeActivity() {
        Intent home = new Intent(LoginActivity.this,Home.class);
        startActivity(home);
        finish();
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}
