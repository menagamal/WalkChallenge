package com.example.mena.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupAccount extends AppCompatActivity {

    private EditText name ;
    private ImageButton imageButton ;
    private static final int GALLERY_REQ = 1;
    private Button setup ;
    private Uri profileimage = null ;
    private FirebaseAuth auth ;
    private DatabaseReference db ;
    private StorageReference storageReference ;
    private ProgressDialog progressDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);
        name =(EditText)findViewById(R.id.profilename);
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        imageButton=(ImageButton)findViewById(R.id.profile);
        setup=(Button)findViewById(R.id.setup);
        db = FirebaseDatabase.getInstance().getReference().child("users");
        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photos = new Intent( Intent.ACTION_GET_CONTENT);
                photos.setType("image/*");
                //intent to the gallery on the phone
                startActivityForResult(photos,GALLERY_REQ);
            }
        });

        setup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String personname = name.getText().toString();
                final String UserId = auth.getCurrentUser().getUid();
                if(!TextUtils.isEmpty(personname) && profileimage != null )
                {
                    progressDialog.setMessage("Finishing ");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    StorageReference path = storageReference.child(profileimage.getLastPathSegment());
                    path.putFile(profileimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String downloadedImage = taskSnapshot.getDownloadUrl().toString();
                            db.child(UserId).child("name").setValue(personname);
                            db.child(UserId).child("image").setValue(downloadedImage);
                            progressDialog.dismiss();
                            Intent intent = new Intent(SetupAccount.this ,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);                        }
                    });


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
                profileimage=resultUri;
              imageButton.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
