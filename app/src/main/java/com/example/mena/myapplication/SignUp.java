package com.example.mena.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText name , email , pass ;
    private Button signup ;
    private FirebaseAuth auth ;

    private DatabaseReference databaseReference ;
    private ProgressDialog progressDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = (EditText)findViewById(R.id.personname);
        email=(EditText)findViewById(R.id.personemail);
        pass=(EditText)findViewById(R.id.personpass);
        signup=(Button)findViewById(R.id.signup);

        auth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");




        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String finalname = name.getText().toString().trim();
                final String finalemail = email.getText().toString().trim();
                final String finalpass = pass.getText().toString().trim();

                if (!TextUtils.isEmpty(finalname) && !TextUtils.isEmpty(finalemail) && !TextUtils.isEmpty(finalpass))
                {
                    progressDialog.setMessage("Signing Up ..");
                    progressDialog.show();

                    auth.createUserWithEmailAndPassword(finalemail,finalpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                String userid = auth.getCurrentUser().getUid();
                                DatabaseReference currentUser = databaseReference.child(userid);
                                currentUser.child("name").setValue(finalname);
                                currentUser.child("image").setValue("default");
                                progressDialog.dismiss();
                                startActivity(new Intent(SignUp.this,SetupAccount.class));

                            }
                        }
                    });
                }


            }
        });


    }
}
