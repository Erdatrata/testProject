package com.example.tazpitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.regex.Pattern;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    Button login;
    TextView mCreateBtn,forgotTextLink;
    ProgressBar progressBar;
    EditText EmailAddressInputLogin, PasswordInputLogin;
    FirebaseAuth FAuth;
    private  static  final  Pattern PASSWORD_PATTERN=
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[!@#$%^&+=])" +
                    "(?=\\S+$)" +
                    ".{7,}" +
                    "$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Toast.makeText(this, "אתם כבר מחוברים!", Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.activity_login);


        EmailAddressInputLogin = (EditText) findViewById(R.id.EmailAddressInputLogin);
        PasswordInputLogin = (EditText) findViewById(R.id.PasswordInputLogin);
        login = (Button) findViewById(R.id.loginButton);
        mCreateBtn = findViewById(R.id.createText);
        FAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        forgotTextLink = findViewById(R.id.forgotPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput=EmailAddressInputLogin.getText().toString().trim();
                String passwordnput=PasswordInputLogin.getText().toString().trim();

                if(TextUtils.isEmpty(emailInput)){//if the field of the email is empty
                    EmailAddressInputLogin.setError("המייל חייב להכיל\"");
                    return;
                }
                if(TextUtils.isEmpty(passwordnput)){//if the field of the password is empty
                    PasswordInputLogin.setError("סיסמה צריכה להכיל\"");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {//if the email is proper
                    EmailAddressInputLogin.setError("מייל לא תקין\"");
                    return;
                }
                if(!PASSWORD_PATTERN.matcher(passwordnput).matches()){//if the password is proper
                    PasswordInputLogin.setError("סיסמה לא תקינה\"");
                    return;
                }
                if( passwordnput.length()<6){//if the password is not long than 6
                    PasswordInputLogin.setError("סיסמה קצרה\"");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);//show the progressbar
                FAuth.signInWithEmailAndPassword(emailInput,passwordnput).addOnCompleteListener(new
                                            OnCompleteListener<AuthResult>() {//if the user exists
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){//if response success than do
                            Toast.makeText(LoginActivity.this, "ההתחבור הצליחה",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {//if the response is filed
                            Toast.makeText(LoginActivity.this, "שגיאה: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
        mCreateBtn.setOnClickListener(new View.OnClickListener() {// if the button "הרשמה כאן"
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),register.register1.class));
            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {// if the button "שכחתי סיסמה " pressed
            @Override
            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        FAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error ! Reset Link is Not Sent" +
                                        e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });
                passwordResetDialog.create().show();
            }
        });

    }

}

