package com.example.mena.myapplication;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;

public class PostActivity extends AppCompatActivity {

    private ImageButton imageButton ;
    private static final int GALLERY_REQ = 1;
    private EditText ed1 ;
    private EditText ed2 ;
    private EditText ed3 ;
    private Button bt ;
    private boolean imageChecked = false ;
    private ChallengeModel model=new ChallengeModel();;
    Intent cha ;
    Uri loaded ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageButton=(ImageButton)findViewById(R.id.imcamera);
        ed1=(EditText)findViewById(R.id.tit);
        ed2=(EditText)findViewById(R.id.des);
        ed3=(EditText)findViewById(R.id.foot);
        bt= (Button)findViewById(R.id.challenge);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photos = new Intent( Intent.ACTION_GET_CONTENT);
                photos.setType("image/*");
                //intent to the gallery on the phone
                startActivityForResult(photos,GALLERY_REQ);
            }
        });
        cha=new Intent(this,ChallengeBegin.class);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (imageChecked && ed1.getText().toString().length() > 5 && ed2.getText().toString().length()>10 && ed3.getText().toString().length() > 0 )
                {



                    model.setTitle(ed1.getText().toString());
                    model.setDesc(ed2.getText().toString());
                    model.setFootSteps(Integer.parseInt(ed3.getText().toString()));
                    model.setImage(loaded.toString());

                    cha.putExtra("model",model);
                    startActivity(cha);


                }
                else {
                    Toast.makeText(PostActivity.this, "Empty Fields !! ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //checking if the gallery request key = the requestcode recevied and that there was not any errros !
        if( requestCode == GALLERY_REQ && resultCode == RESULT_OK)
        {
            Uri im = data.getData();
            CropImage.activity(im)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                loaded=resultUri;
                imageButton.setImageURI(resultUri);
                imageChecked = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
