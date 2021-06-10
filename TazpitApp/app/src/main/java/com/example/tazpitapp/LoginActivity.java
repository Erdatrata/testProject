package com.example.tazpitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    Button login;
    TextView mCreateBtn,forgotTextLink;
    ProgressBar progressBar;
    TextInputLayout EmailAddressInputLogin, PasswordInputLogin;
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
            Toast.makeText(this, getResources().getString(R.string.login_already_in), Toast.LENGTH_SHORT).show();
            finish();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        EmailAddressInputLogin = (TextInputLayout) findViewById(R.id.EmailAddressInputLogin);
        PasswordInputLogin = (TextInputLayout) findViewById(R.id.PasswordInputLogin);
        login = (Button) findViewById(R.id.loginButton);
        mCreateBtn = findViewById(R.id.createText);
        FAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        forgotTextLink = findViewById(R.id.forgotPassword);

        login.setOnClickListener(v -> {

            String emailInput= Objects.requireNonNull(EmailAddressInputLogin.getEditText()).getText().toString();
            String passwordnput= Objects.requireNonNull(PasswordInputLogin.getEditText()).getText().toString();

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
            //if the user exists
            FAuth.signInWithEmailAndPassword(emailInput,passwordnput).addOnCompleteListener(task -> {
            if(task.isSuccessful()){//if response success than do
            Toast.makeText(LoginActivity.this, "ההתחבור הצליחה",
                Toast.LENGTH_SHORT).show();

            //                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

            //clear old sp once more
            //                            FirebaseAuth.getInstance().signOut();
            SharedPreferences sharedpreferences = getSharedPreferences(constants.SHARED_PREFS,
                Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear().apply();

            //download settings from server
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Users").
                document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document = task1.getResult();
                    System.out.println(document);
                    String loc = Objects.requireNonNull(document.get("location")).toString();
                    editor.putString("location",loc);
                    editor.putFloat("range",Float.parseFloat(Objects.requireNonNull(document.get("range")).toString()));
//                        System.out.println("location "+loc);
                    for(String d: constants.daysNames){
                        String toStore = Objects.requireNonNull(document.get(d)).toString();
                        editor.putString(d,toStore);
                    }
                    if (document.exists()) {
                        Log.d("gabi_test", "Settings updated " + document.getData());
                        editor.apply();
                    } else {
                        Log.d("gabi_test", "Settings not found");
                        }
                    } else {
                            Log.d("gabi_test", "settings failed with ", task1.getException());
                    }

                        //return to main
                        finish();

//                            startActivity(getIntent());
                    });
            } else {//if the response is filed
                Toast.makeText(LoginActivity.this, "שגיאה: " +
                        Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        });
        // if the button "הרשמה כאן"
        mCreateBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),register.register1.class)));

        // if the button "שכחתי סיסמה " pressed
        forgotTextLink.setOnClickListener(v -> {

            final EditText resetMail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("לשחזר סיסמה?");
            passwordResetDialog.setMessage("הזן מייל לקבלת לינק לשחזור הסיסמה");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("כן", (dialog, which) -> {
                // extract the email and send reset link
                String mail = resetMail.getText().toString();
                FAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, "קישור לשחזור הסיסמה נשלח למייל",
                        Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "שגיאה! קישור השחזור לא נשלח" +
                                e.getMessage(), Toast.LENGTH_SHORT).show());

            });
            passwordResetDialog.setNegativeButton("לא", (dialog, which) -> {
                // close the dialog
            });
            passwordResetDialog.create().show();
        });

    }
}