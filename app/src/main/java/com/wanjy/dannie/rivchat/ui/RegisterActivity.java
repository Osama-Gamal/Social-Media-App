package com.wanjy.dannie.rivchat.ui;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import android.view.View;
import android.app.ProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

import com.google.android.gms.tasks.Task;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.wanjy.dannie.rivchat.R;
import com.wanjy.dannie.rivchat.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import java.util.ArrayList;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import android.content.SharedPreferences;
import android.app.Activity;
import com.wanjy.dannie.rivchat.Home;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.google.firebase.database.ChildEventListener;
import android.support.design.widget.TextInputLayout;

public class RegisterActivity extends AppCompatActivity  implements  
AdapterView.OnItemSelectedListener {

    EditText nameEdit,emailEdit,passwordEdit,repetPassword;
    Button RegisterBtn,BackBtn;
    TextView haveEmail;
    TextInputLayout inputName,inputEmail,inputPassword,inputPassword2;
    public static String QRCode="",group = "";
    public static String name,email,pass1,pass2;
    Spinner spinnerGroup;
    String[] groups = { "Choose Your Group","Group 1", "Group 2"};  
    public static int level,positionGroup;
    SharedPreferences sharedd;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    public static CheckBox enableDecodingCheckBox;
    public List<String> QRCodes = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(RegisterActivity.this);
        sharedd = getApplicationContext().getSharedPreferences("sharedUser", Activity.MODE_PRIVATE);
        
        nameEdit = (EditText) findViewById(R.id.editTextName);
        emailEdit = (EditText) findViewById(R.id.editTextEmail);
        passwordEdit = (EditText) findViewById(R.id.editTextPassword1);
        repetPassword = (EditText) findViewById(R.id.editTextPassword2);
        
        inputName = (TextInputLayout) findViewById(R.id.textInputName);
        inputEmail = (TextInputLayout) findViewById(R.id.textInputEmail);
        inputPassword = (TextInputLayout) findViewById(R.id.textInputPassword);
        inputPassword2 = (TextInputLayout) findViewById(R.id.textInputPassword2);
        
        enableDecodingCheckBox = (CheckBox)findViewById(R.id.enable_decoding_checkbox);
        RegisterBtn = (Button) findViewById(R.id.cirRegisterButton);
        BackBtn = (Button) findViewById(R.id.cirBackButton); 
        haveEmail = (TextView) findViewById(R.id.haveEmailText);
        
        spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);
        spinnerGroup.setOnItemSelectedListener(this); 
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,groups);  
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        spinnerGroup.setAdapter(aa);  
        
        
        nameEdit.setText(name);
        emailEdit.setText(email);
        passwordEdit.setText(pass1);
        repetPassword.setText(pass2);
        spinnerGroup.setSelection(positionGroup);
        
        checkLevel();
        
        enableDecodingCheckBox.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    if(QRCode.equals("")){
                        enableDecodingCheckBox.setChecked(true);
                        Intent lj = new Intent(RegisterActivity.this,DecoderActivity.class);
                        startActivity(lj);
                        
                        name = nameEdit.getText().toString();
                        email = emailEdit.getText().toString();
                        pass1 = passwordEdit.getText().toString();
                        pass2 = repetPassword.getText().toString();
                        
                    }else{
                        QRCode = "";
                        
                    }
                    
                    
                }                           
                });
        enableDecodingCheckBox.setChecked(true);

        if(QRCode.equals("")||QRCode.isEmpty()||QRCode.length()<3){
                    enableDecodingCheckBox.setChecked(false);
                }else{                    
                    enableDecodingCheckBox.setChecked(true);
                }

        haveEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(login);
                    finish();
                    return;
                }
            });

        BackBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View p1)
                {
                    level--;
                    checkLevel();
                }                            
                });    

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    final String email = emailEdit.getText().toString().trim();
                    final String password = passwordEdit.getText().toString().trim();
                    final String password2 = repetPassword.getText().toString().trim();
                    final String name = nameEdit.getText().toString().trim();

                    if(name.length()>=20||name.length()<5||name.isEmpty()||email.isEmpty()||email.length()<6){
                        nameEdit.setError("يجب ان لا يقل الاسم عن 5 احرف ولا يزيد عن 20 حرف");
                        emailEdit.setError("أدخل بريدك الالكتروني لتأمين حسابك");
                        level = 0;
                        checkLevel();
                    }else{
                        level = 1;
                        checkLevel();  
                        if(password.isEmpty()||password.length()<6||!password2.equals(password)){
                            passwordEdit.setError("أدخل كلمة مرور صحيحة لا تقل عن 6 رموز");
                            level = 1;
                            checkLevel();
                        }else{
                            level = 1;//2
                            checkLevel();  
                            /*if(group.isEmpty()||group.equals("Choose Your Group")||QRCode.isEmpty()){
                                showMessage("تأكد من اختيار الجروب ومسح بطاقة الترشيح");
                                level = 2;
                                checkLevel();
                                }else{*/
                                    
                                    int checkQR = -1;
                                    //checkQR= QRCodes.indexOf(QRCode);
                                    if(checkQR == -1){
                                        CreateUserAccount(email,name,password);
                                        RegisterBtn.setEnabled(false);                                        
                                        pd.setMessage("جاري التسجيل .....");
                                        pd.show();
                                    }else{
                                        showMessage("قد يكون البريد الالكتروني مستخدما من قبل أو خطأ في كلمة السر");
                                        level = 1;//2
                                        checkLevel();
                                    }
                                    
                                    
                                    
                                    
                                }
                            
                            
                        }
                        
                        
                    }
                    
                    
                    


            });

        // Get All of QR Codes in List to Check if Cureent QR Code had been used before or not
        /*final List<User> list = new ArrayList<>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("user");

        usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User allUsers = postSnapshot.getValue(User.class);
                        list.add(allUsers);

                        
                        QRCodes.add(String.valueOf(allUsers.getQRCode()));
                        //showMessage(QRCodes.get(0)+"");
                        
                    }}
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showMessage(databaseError.toString());
                }
			});*/
        
            
        
    }
    
    DatabaseReference databaseReference;
    
    private void CreateUserAccount(final String email,final String name, final String password) {

        //this method create user account
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Account Created
                        showMessage("Account Created");
                        
                        databaseReference = FirebaseDatabase.getInstance().getReference("user");
                        String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                        User newUser = new User(
                            name,
                            email,
                            password,
                            "default",
                            UserId,
                            "",//QRCode
                            "",//group
                            "",
                                "");
                            
                       // String UserUpId = databaseReference.push().getKey();                
                        databaseReference.child(UserId).setValue(newUser);


                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                        sharedd.edit().putString("name", name).commit();
                        sharedd.edit().putString("email", email).commit();
                        sharedd.edit().putString("password", password).commit();
                        sharedd.edit().putString("avata", "default").commit();
                        sharedd.edit().putString("myid", FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).commit();
                        sharedd.edit().putString("qrcode", "").commit(); // QRCode
                        sharedd.edit().putString("group", "").commit(); // group
                        sharedd.edit().putString("title", "").commit();
                        sharedd.edit().putString("telegram", "").commit();


                        startActivity(intent);
                        finish();
                        return;
                    }
                    else{
                        showMessage("Account Creation Failed :"+task.getException().getMessage());
                        RegisterBtn.setEnabled(true);
                        pd.dismiss();

                    }
                }
            });

    }

    private void showMessage(String message) {
        //TODO: Make generic toast message
        Toast.makeText(getApplicationContext(),message,Toast
                       .LENGTH_LONG).show();

    }
    
    @Override  
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {  
        //Toast.makeText(getApplicationContext(),groups[position] , Toast.LENGTH_LONG).show();  
        positionGroup = position;
        if(groups[position].equals("Group 1")){
            group = "g1";          
        }else{
            if(groups[position].equals("Group 2")){
                group = "g2"; 
            }
        }
        
        
        
    }  
    @Override  
    public void onNothingSelected(AdapterView<?> arg0) {  
        group = "";
    }  
    
    public void checkLevel(){
        if(level==0){
            RegisterBtn.setText("Next");
            BackBtn.setVisibility(View.GONE);
            
            inputName.setVisibility(View.VISIBLE);
            inputEmail.setVisibility(View.VISIBLE);
            inputPassword.setVisibility(View.GONE);
            inputPassword2.setVisibility(View.GONE);
            enableDecodingCheckBox.setVisibility(View.GONE);
            spinnerGroup.setVisibility(View.GONE);
        }else{
            if(level==1){
                RegisterBtn.setText("Register");
                BackBtn.setVisibility(View.VISIBLE);
                
                inputName.setVisibility(View.GONE);
                inputEmail.setVisibility(View.GONE);
                inputPassword.setVisibility(View.VISIBLE);
                inputPassword2.setVisibility(View.VISIBLE);
                enableDecodingCheckBox.setVisibility(View.GONE);
                spinnerGroup.setVisibility(View.GONE);
            }else{
                if(level==2){
                    RegisterBtn.setText("Register");
                    BackBtn.setVisibility(View.VISIBLE);
                    
                    inputName.setVisibility(View.GONE);
                    inputEmail.setVisibility(View.GONE);
                    inputPassword.setVisibility(View.GONE);
                    inputPassword2.setVisibility(View.GONE);
                    enableDecodingCheckBox.setVisibility(View.VISIBLE);
                    spinnerGroup.setVisibility(View.VISIBLE);
                }
                
            }
            
            
        }
        
        
        
    }
    
    
    
}
