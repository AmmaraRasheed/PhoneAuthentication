package com.example.rabia.phoneauthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    ProgressBar progressBar;
    Button button;
    String mVerificationId;
    private FirebaseAuth Auth;
     String number="+923365877618";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=findViewById(R.id.code);
        button=findViewById(R.id.btn);
        Auth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);

        sendVerificationCode(number);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=editText.getText().toString().trim();
                if(code.isEmpty() || code.length()<6){
                    editText.setText("Invalid");
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }


        });

    }

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendVerificationCode(String number) {

        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
    }
     private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

         @Override
         public void onVerificationCompleted(PhoneAuthCredential credential) {
             String code=credential.getSmsCode();
             if(code!=null){
                 editText.setText(code);
                 verifyCode(code);
             }
         }

         @Override
         public void onVerificationFailed(FirebaseException e) {
             // This callback is invoked in an invalid request for verification is made,
             // for instance if the the phone number format is not valid.

             if (e instanceof FirebaseAuthInvalidCredentialsException) {

                 Toast.makeText(getApplicationContext(),"Invalid Number",Toast.LENGTH_LONG).show();
                 // Invalid request
                 // ...
             } else if (e instanceof FirebaseTooManyRequestsException) {
                 Toast.makeText(getApplicationContext(),"The SMS quota for the project has been exceeded",Toast.LENGTH_LONG).show();

             }

             // Show a message and update the UI
             // ...
         }

         @Override
         public void onCodeSent(String verificationId,
                                PhoneAuthProvider.ForceResendingToken token) {


             mVerificationId = verificationId;
         }
     };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            Intent intent=new Intent(MainActivity.this,Profile.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
