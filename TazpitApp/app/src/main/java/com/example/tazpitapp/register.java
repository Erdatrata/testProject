package com.example.tazpitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
///////////////////////////

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
///////////
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


class register {

    private static String mailSTR;
    private static String passwordSTR;
    private static String fNameSTR;
    private static String LNameSTR;
    private static String citySTR;
    private static String phoneNumberSTR;


    public static class register2 extends AppCompatActivity {


        //Edit test on front of the activity
        private static FirebaseAuth mAuth;//create auth file
        private TextInputLayout email;
        private TextInputLayout FName;
        private TextInputLayout LName;
        private AutoCompleteTextView city;
        private TextInputLayout phone;
        private TextInputLayout password1;
        private TextInputLayout password2;
        private static final Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        "(?=.*[0-9])" +
                        "(?=.*[a-z])" +
                        "(?=.*[A-Z])" +
                        "(?=.*[!@#$%^&+=])" +
                        "(?=\\S+$)" +
                        ".{7,}" +
                        "$");


        private boolean checkPassword() {//call 2 function plus minimum of 8
            String password = Objects.requireNonNull(password1.getEditText()).getText().toString();

            return password.length() >= 8 && ContainSpecial(password) && noUpper(password);
        }

        private boolean noUpper(String password) {//check if it has upper latters
            return !password.equals(password.toLowerCase());
        }

        private boolean ContainSpecial(String password) {//check if contain special latters like @!...
            Pattern p = Pattern.compile("[^A-Za-z0-9]");
            Matcher m = p.matcher(password);
            return m.find();
        }

        private boolean Integritycheck() {//check all fileds have context
            String mail = Objects.requireNonNull(email.getEditText()).getText().toString();
            String password = Objects.requireNonNull(password1.getEditText()).getText().toString();
            String passwordAgain = Objects.requireNonNull(password2.getEditText()).getText().toString();
            String city = this.city.getText().toString();
            String fname = Objects.requireNonNull(FName.getEditText()).getText().toString();
            String Lname = Objects.requireNonNull(this.LName.getEditText()).getText().toString();
            String phone = Objects.requireNonNull(this.phone.getEditText()).getText().toString();

            return mail.length() < 1 || password.length() < 1 || passwordAgain.length() < 1 || city.length() < 1 || fname.length() < 1 || Lname.length() < 1 || phone.length() < 1;
        }

        private boolean checkEqualPassword() {
            return !Objects.requireNonNull(password1.getEditText()).getText().toString()
                    .equals(Objects.requireNonNull(password2.getEditText()).getText().toString());

        }


        private boolean checkMailIntegrity() {//check if it has @

            String mail = Objects.requireNonNull(email.getEditText()).getText().toString();
            return mail.contains("@");
        }

        TextInputLayout til_city;
        AutoCompleteTextView act_city;
        ArrayList<String> ArrList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter_city;

        @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            try {
                Objects.requireNonNull(this.getSupportActionBar()).hide();
            } catch (NullPointerException ignored) {
            }
            setContentView(R.layout.register);
            mAuth = FirebaseAuth.getInstance();
            til_city = findViewById((R.id.til_city));
            act_city = findViewById((R.id.act_city));
            get_json();
            arrayAdapter_city = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, ArrList);
            act_city.setAdapter(arrayAdapter_city);
            act_city.setThreshold(1);

            email = findViewById(R.id.Email);
            city = findViewById(R.id.act_city);
            FName = findViewById(R.id.FirstName);
            LName = findViewById(R.id.LastName);
            password1 = findViewById(R.id.Password);
            password2 = findViewById(R.id.passwordAgain);
            phone = findViewById(R.id.PhoneNumber);
            Button next = findViewById(R.id.next);


            next.setOnClickListener(view -> {
                int city_eixst=0,phone_is_ok=0;
                for (int i = 0; i < ArrList.size(); i++) {
                    if (ArrList.get(i).equals(city.getText().toString())) {
                        Log.d("onComplet","input="+city);
                        Log.d("onComplet","input="+ArrList.get(i));
                        city_eixst=1;
                        break;
                    }
                }
                if( Objects.requireNonNull(phone.getEditText()).getText().toString().length()!=10){
                    phone_is_ok=1;
                }
                Log.d("onComplet","input="+ArrList.get(0));
                if (Integritycheck() || !checkMailIntegrity() || !checkPassword() || checkEqualPassword() ||city_eixst==0|| phone_is_ok==1) {
                    String msg = "";

                    if(city_eixst==0){
                       // getResources().getString(R.string.register_error_email_error)

                        msg = msg + getResources().getString(R.string.register_error_city_exists)+"\n";
                        msg = msg + getResources().getString(R.string.register_error_city_error)+"\n";
                    }
                    if(  phone_is_ok==1){
                        msg = msg +getResources().getString(R.string.register_error_phone_short)+"\n";
                    }
                    if (Integritycheck()) {
                        msg = msg +getResources().getString(R.string.register_error_fields_missing) +"\n";

                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(email.getEditText()).getText().toString()).matches()) {//if the email is proper
                        msg = msg + getResources().getString(R.string.register_error_email_error)+"\n";

                    }
                    //if(!mailInUse()){msg=msg+"mail in use";}
                    if (PASSWORD_PATTERN.matcher(Objects.requireNonNull(password1.getEditText())
                            .getText().toString()).matches()) {
                    } else {//if the password is proper
                msg = msg + getResources().getString(R.string.register_error_password_policy)+"\n";

            }
                    if (checkEqualPassword()) {
                        msg = msg + getResources().getString(R.string.register_error_password_mismatch)+"\n";

                    }
                    Toast.makeText(view.getContext(), msg, Toast.LENGTH_LONG).show();

                }
                else {
                    mailSTR = Objects.requireNonNull(email.getEditText()).getText().toString();
                    passwordSTR = Objects.requireNonNull(password1.getEditText()).getText().toString();
                    fNameSTR = Objects.requireNonNull(FName.getEditText()).getText().toString();
                    LNameSTR = Objects.requireNonNull(LName.getEditText()).getText().toString();
                    citySTR = city.getText().toString();
                    phoneNumberSTR = phone.getEditText().getText().toString();
                    try {
                        Register(mailSTR, passwordSTR, fNameSTR, LNameSTR, citySTR, phoneNumberSTR);
                        Thread.sleep(2500);
                        Toast.makeText(register2.this, R.string.login_success,
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(register2.this, R.string.register_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        private boolean mailInUse() {
            String mail = Objects.requireNonNull(email.getEditText()).getText().toString();
            final boolean[] re = {false};
            Object object = new Object();

            mAuth.fetchSignInMethodsForEmail(mail)
                    .addOnCompleteListener(task -> {

                        boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();

                        if (isNewUser) {
                            re[0] = true;
                            System.out.println(true);
                        }
                        object.notify();

                    });
            synchronized (object) {
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            return re[0];
        }


        public void get_json() {
            String json;
            try {
                InputStream is = getAssets().open(constants.CITY_LIST_FILE);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, StandardCharsets.UTF_8);
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    ArrList.add(obj.getString(constants.CITY_NAME));

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        private void Register(String mailSTR, String passwordSTR, String fNameSTR, String lNameSTR, String citySTR, String phoneNumberSTR) throws Exception {
            //register,first create user , with email and password, if successful , it will create dataToSave object, then send it to real time database
            mAuth.createUserWithEmailAndPassword(mailSTR, passwordSTR)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            final Map<String, Object>[] dataToSave = new Map[]{new HashMap<String, Object>()};
                            dataToSave[0].put(constants.FIRST_NAME, fNameSTR);
                            dataToSave[0].put(constants.SEC_NAME, lNameSTR);
                            dataToSave[0].put(constants.CITY, citySTR);
                            dataToSave[0].put(constants.EMAIL, mailSTR);
                            dataToSave[0].put(constants.PHONE, phoneNumberSTR);
                            dataToSave[0].put(constants.VOLUNTEER, "false");


                            FirebaseDatabase.getInstance().getReference(constants.DOC_REF_USERS)
                                    //here we creating users folder in real time data baes , getting uid from user and storing the data in folder named by id
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(dataToSave[0]).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(register2.this,
                                                    R.string.register_toast_success, Toast.LENGTH_LONG).show();
                                            {//adds all new data for newely registered clients
                                                dayTime dtDEF = new dayTime(0, 0, 23, 59);//default for first time
                                                Gson gson = new Gson();
                                                SharedPreferences sp = getSharedPreferences(constants.SHARED_PREFS,
                                                        Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sp.edit();
                                                String dayDEF = gson.toJson(dtDEF);
                                                Map<String, String> docData = new HashMap<>();
                                                for (String day : constants.daysNames) {
                                                    docData.put(day, dayDEF);
                                                    editor.putString(day, dayDEF);
                                                }
                                                docData.put(constants.rangeChoice, "" + 10.0);
                                                editor.putFloat(constants.rangeChoice, (float) 10.0);

                                                docData.put(constants.SHARED_PREFS_LOCATION, constants.SET_CITY);
                                                editor.putString(constants.SHARED_PREFS_LOCATION, constants.SET_CITY);

                                                FirebaseAuth userIdentifier = FirebaseAuth.getInstance();
                                                String UID = userIdentifier.getCurrentUser().getUid();
                                                DocumentReference DRF = FirebaseFirestore.getInstance()
                                                        .document(constants.DOC_REF_USERS+"/" + UID);
                                                final boolean[] success = {true};
                                                final Exception[] failToRet = new Exception[1];
                                                DRF.set(docData).addOnFailureListener(e -> {
                                                    success[0] = false;
                                                    failToRet[0] = e;
                                                });
                                                if (!success[0]) {
                                                    // show/make log for case of failure
                                                }
                                                editor.apply();

                                            }
                                        }
                                    });


                        }

                    }).addOnFailureListener(e -> {
                        try {
                            throw new Exception(constants.FAILED_REGISTER);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });


        }
    }


    public static class register1 extends AppCompatActivity {
        private final DocumentReference mDocRef = FirebaseFirestore.getInstance().document("contact/contact");
        private Button next;
        private TextView textshow;
        private CheckBox Aggre;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register2);
//            back=(Button) findViewById(R.id.backPage2Reg);
            next = findViewById(R.id.nextPage2Reg);
            textshow = findViewById(R.id.textViewRegPage2);
            textshow.setMovementMethod(new ScrollingMovementMethod());
            Aggre = findViewById(R.id.page2RegAggre);

            //create file named mDocRef ,get instance from contact/contack ~ path to doc
            //call get ,on succeeds will save the data in dataToSave(comes in map file),then show on textView
            final Map<String, Object>[] dataToSave = new Map[]{new HashMap<String, Object>()};
            mDocRef.get().addOnSuccessListener(documentSnapshot -> {
                dataToSave[0] = documentSnapshot.getData();
                String str = Objects.requireNonNull(dataToSave[0]).get(constants.CONTACT).toString();
                System.out.println(str);
                textshow.setText(str);
            }).addOnFailureListener(e -> {
                Toast.makeText(register1.this, getResources().getString(R.string.register_contract_not_Availble), Toast.LENGTH_LONG).show();
            finish();
            });
            next.setOnClickListener(v -> {
                if (Aggre.isChecked()) {
                    Intent intent = new Intent(v.getContext(), register2.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(register1.this, R.string.register_confirm_checkbox
                            , Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    public static class register3 extends AppCompatActivity {
        private static FirebaseAuth mAuth;

        private void Register(String mailSTR, String passwordSTR, String fNameSTR, String lNameSTR, String citySTR, String phoneNumberSTR) throws Exception {
            //register,first create user , with email and password, if successful , it will create dataToSave object, then send it to real time database
            mAuth.createUserWithEmailAndPassword(mailSTR, passwordSTR)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            final Map<String, Object>[] dataToSave = new Map[]{new HashMap<String, Object>()};
                            dataToSave[0].put(constants.FIRST_NAME, fNameSTR);
                            dataToSave[0].put(constants.SEC_NAME, lNameSTR);
                            dataToSave[0].put(constants.CITY, citySTR);
                            dataToSave[0].put(constants.EMAIL, mailSTR);
                            dataToSave[0].put(constants.PHONE, phoneNumberSTR);



                            FirebaseDatabase.getInstance().getReference(constants.DOC_REF_USERS)
                                    //here we creating users folder in real time data baes , getting uid from user and storing the data in folder named by id
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(dataToSave[0]).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(register3.this,
                                                    R.string.register_toast_success, Toast.LENGTH_LONG).show();
                                            {//adds all new data for newely registered clients
                                                dayTime dtDEF = new dayTime(0, 0, 23, 59);//default for first time
                                                Gson gson = new Gson();
                                                SharedPreferences sp = getSharedPreferences(constants.SHARED_PREFS,
                                                        Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sp.edit();
                                                String dayDEF = gson.toJson(dtDEF);
                                                Map<String, String> docData = new HashMap<>();
                                                for (String day : constants.daysNames) {
                                                    docData.put(day, dayDEF);
                                                    editor.putString(day, dayDEF);
                                                }
                                                docData.put(constants.rangeChoice, "" + 10.0);
                                                editor.putFloat(constants.rangeChoice, (float) 10.0);

                                                docData.put(constants.SHARED_PREFS_LOCATION, constants.CITY);
                                                editor.putString(constants.SHARED_PREFS_LOCATION, constants.CITY);
                                                editor.putBoolean("sync",true);
                                                FirebaseAuth userIdentifier = FirebaseAuth.getInstance();
                                                String UID = userIdentifier.getCurrentUser().getUid();
                                                DocumentReference DRF = FirebaseFirestore.getInstance()
                                                        .document(constants.DOC_REF_USERS+"/" + UID);
                                                final boolean[] success = {true};
                                                final Exception[] failToRet = new Exception[1];
                                                DRF.set(docData).addOnFailureListener(e -> {
                                                    success[0] = false;
                                                    failToRet[0] = e;
                                                });
                                                if (!success[0]) {
                                                    // show/make log for case of failure
                                                }
                                                editor.apply();

                                            }
                                        }
                                    });


                        }

                    }).addOnFailureListener(e -> {
                        try {
                            throw new Exception(constants.FAILED_REGISTER);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });


        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register3);
            Button test = findViewById(R.id.testB);
            mAuth = FirebaseAuth.getInstance();
            test.setOnClickListener(v -> {
                try {
                    Register(mailSTR, passwordSTR, fNameSTR, LNameSTR, citySTR, phoneNumberSTR);
                    Thread.sleep(2500);
                } catch (Exception e) {
                    Toast.makeText(register3.this, R.string.register_fail, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), register2.class);
                    startActivity(intent);
                }
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            });
        }

    }


}
