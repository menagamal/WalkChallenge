package com.example.mena.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    private EditText email,pass ;
    private Button signin,signup;
    private FirebaseAuth authin ;
    private DatabaseReference db ;
    private ProgressDialog prog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email=(EditText)findViewById(R.id.editText);
        pass=(EditText)findViewById(R.id.editText2);
        signin=(Button)findViewById(R.id.button);
        signup=(Button)findViewById(R.id.button2);
        authin=FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference().child("users");
        db.keepSynced(true);

        prog= new ProgressDialog(this);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this,SignUp.class));
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if(!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(password))
                {
                    prog.setMessage("Logging..");
                    prog.setCancelable(false);
                    prog.show();
                    authin.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                prog.dismiss();
                               final String id = authin.getCurrentUser().getUid();
                                db.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.hasChild(id)){

                                            Intent intent = new Intent(SignIn.this ,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        }else{
                                            Intent intent = new Intent(SignIn.this ,SetupAccount.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }else {
                                prog.dismiss();
                                Log.w("TAG", "signInWithEmail", task.getException());
                                Toast.makeText(SignIn.this, "Unable To log in", Toast.LENGTH_SHORT).show();
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    Log.e("a", e.getMessage());
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Log.e("aa", e.getMessage());
                                } catch(FirebaseAuthUserCollisionException e) {
                                    Log.e("aaa", e.getMessage());
                                } catch(Exception e) {
                                    Log.e("haaa", e.getMessage());
                                }
                        }}
                    });
                }
            }
        });
    }
}