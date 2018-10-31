package com.example.idotalmor.todofirebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.idotalmor.todofirebase.Classes.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    EditText email,password,username;
    ImageView profileImg;
    Uri localImgUri;
    FirebaseAuth firebaseAuth;
    GoogleProgressBar googleProgressBar;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username_signup);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);

        profileImg = findViewById(R.id.profile_image_signup);
        Picasso.get().load(R.drawable.placeholder).transform(new CircleTransform()).into(profileImg);

        firebaseAuth = FirebaseAuth.getInstance();

        googleProgressBar = findViewById(R.id.signup_progressbar);
        googleProgressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(this).colors(getResources().getIntArray(R.array.google_colors)) //Array of 4 colors
                .build());

    }

    @Override
    protected void onStart() {
        super.onStart();

        //auth listener
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){

                    Intent intent = new Intent(SignUpActivity.this,MainTask.class);
                    //clear stack and create new one with maintask activity as root
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case android.R.id.home:
                this.finish();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void imgPicker(View view){

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                localImgUri = result.getUri();
                Picasso.get().load(localImgUri).transform(new CircleTransform()).into(profileImg);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void signUp(View view){

        final String username_str = username.getText().toString();
        String email_str = email.getText().toString();
        String password_str = password.getText().toString();

        if(username_str.isEmpty() || email_str.isEmpty() || password_str.isEmpty()){

            Snackbar.make(email,"Please fill everything",Snackbar.LENGTH_LONG).show();
            return;
        }

        googleProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email_str,password_str)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            firebaseUser = firebaseAuth.getCurrentUser();

                            //add full name as displayname
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username_str)
                                    .build();
                            firebaseUser.updateProfile(profileUpdates);

                            if(localImgUri != null){
                                uploadProfileImage();
                            }
                        }else{

                            googleProgressBar.setVisibility(View.GONE);
                            Snackbar.make(email,"Failed to Signing Up, Please try again",Snackbar.LENGTH_LONG).show();
                            Log.i("Signin up error",task.getException().toString());

                        }


                    }
                });

    }

    public void uploadProfileImage(){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference profileImgRef = storage.getReference().child("ProfilePic").child(localImgUri.getLastPathSegment());

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), localImgUri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //compress the bitmap for uploading to server - create byte array for sending
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileImgRef.putBytes(data);
        //UploadTask uploadTask = profileImgRef.putFile(localImgUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                profileImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //add photo uri to user profile
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();
                        firebaseUser.updateProfile(profileUpdates);

                    }
                });

            }
        });






    }

}
