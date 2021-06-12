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
import com.google.gson.Gson;

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
                EmailAddressInputLogin.setError(getResources().getString(R.string.login_must_mail));
                return;
            }
            if(TextUtils.isEmpty(passwordnput)){//if the field of the password is empty
                PasswordInputLogin.setError(getResources().getString(R.string.login_must_password));
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {//if the email is proper
                EmailAddressInputLogin.setError(getResources().getString(R.string.login_mail_incorect));
                return;
            }
            if(!PASSWORD_PATTERN.matcher(passwordnput).matches()){//if the password is proper
                PasswordInputLogin.setError(getResources().getString(R.string.login_password_incorect));
                return;
            }
            if( passwordnput.length()<6){//if the password is not long than 6
                PasswordInputLogin.setError(getResources().getString(R.string.login_short_password));
                return;
            }
            progressBar.setVisibility(View.VISIBLE);//show the progressbar
            //if the user exists
            FAuth.signInWithEmailAndPassword(emailInput,passwordnput).addOnCompleteListener(task -> {


                if (task.isSuccessful()) {//if response success than do
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success),
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
                    DocumentReference docRef = db.collection(constants.DOC_REF_USERS).
                            document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    docRef.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot document = task1.getResult();
                            System.out.println(document);
                            String loc = "";
                            if(document.exists()) {
                                //location
                                loc = Objects.requireNonNull(document.get(constants.SHARED_PREFS_LOCATION)).toString();
                                if(loc.equals(""))
                                    loc="city";
                                editor.putString(constants.SHARED_PREFS_LOCATION, loc);
                                //range
                                float range = Float.parseFloat((Objects.requireNonNull(document.get(constants.rangeChoice))).toString());
                                if(range==0)
                                    range=10;
                                editor.putFloat(constants.rangeChoice, range);


                                //days
                                String dtDEF = (new Gson()). //will be used in case of null strings
                                        toJson(new dayTime(0,00,23,59));
                                for (String d : constants.daysNames) {
                                    String toStore = (Objects.requireNonNull(document.get(d))).toString();
                                    if(toStore.equals(""))//if string from srv is null
                                        toStore=dtDEF;//then input default 'all day' state
                                    editor.putString(d, toStore);
                                }

                            }
                        } else {
                            Log.d("gabi_test", "settings failed with ", task1.getException());
                        }
                        //return to main
                        finish();
                    });

                }
                else {//if the response is filed
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_error) +
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
            passwordResetDialog.setTitle(getResources().getString(R.string.login_reset_password));
            passwordResetDialog.setMessage(getResources().getString(R.string.login_write_mail));
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton(getResources().getString(R.string.login_yes), (dialog, which) -> {
                // extract the email and send reset link
                String mail = resetMail.getText().toString();
                FAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_resetpass_sendtomail),
                        Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_resetpass_notsent) +
                                e.getMessage(), Toast.LENGTH_SHORT).show());

            });
            passwordResetDialog.setNegativeButton(getResources().getString(R.string.login_no), (dialog, which) -> {
                // close the dialog
            });
            passwordResetDialog.create().show();
        });

    }
}