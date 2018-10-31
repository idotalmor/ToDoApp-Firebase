package com.example.idotalmor.todofirebase;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth AuthInstance;
    EditText email,password;
    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthInstance = FirebaseAuth.getInstance();
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);


        //facebook login

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.i("dsdffd","cancel");
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("dsdffd",exception.getMessage());

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        AuthInstance.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = AuthInstance.getCurrentUser();
                if(user != null){

                    Intent intent = new Intent(MainActivity.this,MainTask.class);
                    //clear stack and create new one with maintask activity as root
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);                }
            }
        });

    }


    public void LoginBtn(View view){
        String email_str = email.getText().toString();
        String password_str = password.getText().toString();

        if(!email_str.isEmpty() && !password_str.isEmpty()){
            AuthInstance.signInWithEmailAndPassword(email_str,password_str)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                //Auth listener will activated

                            }else{

                                Log.i("Login error",task.getException().getLocalizedMessage());
                                Snackbar.make(email,"Failed to login, please try again",Snackbar.LENGTH_SHORT).show();

                            }

                        }
                    });
        }
    }


    public void SignupIntent(View view){

        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("ffg", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        AuthInstance.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("cvvc", "signInWithCredential:success");
                            FirebaseUser user = AuthInstance.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MainActivity Facebook", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }
}
