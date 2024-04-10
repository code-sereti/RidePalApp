package com.example.ridepal;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ImageUploadActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uriImage;
    Uri downloadUri;
    ProgressBar progressBar;
    Button Choose_pic,profile_pic_upload;
    ImageView user_pic;
    FirebaseAuth authProfile;
    StorageReference storageReference;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        user_pic=findViewById(R.id.imageView_profile_dp);

        progressBar=findViewById(R.id.progressBar);
        Choose_pic=findViewById(R.id.upload_pic_choose_button);
        profile_pic_upload=findViewById(R.id.upload_pic_button);
        authProfile=FirebaseAuth.getInstance();

        //Get current user from auth profile...
        firebaseUser=authProfile.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
        Uri uri=firebaseUser.getPhotoUrl();
        // Set user's current Dp in Image view if already uploaded...
        Picasso.get().load(uri).into(user_pic);

        // Set click listener for choosing image from gallery..
        Choose_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoser();
            }
        });
        // Set click listener for uploading image..
        profile_pic_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                UploadPic();

            }
        });
    }

    private void UploadPic() {
        if(uriImage !=null){
            //Save the image with userId of current user
            StorageReference fileReference=storageReference.child(authProfile.getCurrentUser().getUid()+"."+ getFileExtension(uriImage) );
            //Upload picture to firestore storage..
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
//                            Uri downloadUri;
                            downloadUri = uri;
                            firebaseUser=authProfile.getCurrentUser();

                            // Set profile picture...
                            UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);

                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ImageUploadActivity.this, "Profile Upload successfully", Toast.LENGTH_SHORT).show();
                    // Move to setting fragment

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ImageUploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ImageUploadActivity.this, "No file selected", Toast.LENGTH_SHORT).show();

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    // Method for choosing image from gallery
    private void openFileChoser() {

        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData()!=null ){
            uriImage=data.getData();
            user_pic.setImageURI(uriImage);

        }
    }
}