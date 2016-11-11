package com.example.mena.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ChallengeBegin extends AppCompatActivity  implements  SensorEventListener{

    private ChallengeModel model ;
    private StorageReference storage ;
    private Uri im;
    private Uri downloaded ;
    private DatabaseReference database ;
    private SensorManager mSensorManager;
    int counter = 0 ;
    private Sensor mSensor;
    Button bt ;
    private FirebaseAuth auth ;
    private FirebaseUser user ;
    private DatabaseReference databaseReference;


    private ProgressDialog dialog ;
    private  TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_begin);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener((SensorEventListener)this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
         textView = (TextView)findViewById(R.id.textView);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());


        bt=(Button)findViewById(R.id.done);
        bt.setEnabled(false);
        model=(ChallengeModel) getIntent().getSerializableExtra("model");
        im=Uri.parse(model.getImage());

        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference().child("posts");

        dialog=new ProgressDialog(this );


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.setMessage("Posting Challenge ..");
                dialog.show();
                final StorageReference path = storage.child("images").child(im.getLastPathSegment());
                path.putFile(im).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        downloaded = taskSnapshot.getDownloadUrl();
                       final DatabaseReference post = database.push();

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                post.child("title").setValue(model.getTitle());
                                post.child("desc").setValue(model.getDesc());
                                post.child("foot").setValue(model.getFootSteps());
                                post.child("image").setValue(downloaded.toString());
                                post.child("pic").setValue(dataSnapshot.child("image").getValue());
                                post.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            startActivity(new Intent(ChallengeBegin.this ,MainActivity.class));
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        dialog.dismiss();
                    }
                });
                path.putFile(im).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(ChallengeBegin.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });






    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {
            if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }

            final float x = sensorEvent.values[0];
            final float y = sensorEvent.values[1];
            final float z = sensorEvent.values[2];
            final float g = Math.abs((x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH));
            if (g > 2.5) {
                counter++;
                textView.setText("Steps : "+ counter);
                if(counter >= model.getFootSteps())
                {
                    bt.setEnabled(true);
                    Toast.makeText(ChallengeBegin.this, "Done", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
