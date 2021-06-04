package com.example.tazpitapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
///////////////////////////
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
///////////
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



class register {

    private static String mailSTR;
    private static String passwordSTR;
    private  static String fNameSTR;
    private static   String LNameSTR;
    private static String citySTR;
    private  static String phoneNumberSTR;



    public static class register1 extends AppCompatActivity {


        //Edit test on front of the activity
        private static FirebaseAuth mAuth;//create auth file
        private TextInputLayout email;
        private TextInputLayout FName;
        private TextInputLayout LName;
        private AutoCompleteTextView city;
        private  TextInputLayout phone;
        private  TextInputLayout  password1;
        private TextInputLayout password2;
        private  static  final  Pattern PASSWORD_PATTERN=
                Pattern.compile("^" +
                        "(?=.*[0-9])" +
                        "(?=.*[a-z])" +
                        "(?=.*[A-Z])" +
                        "(?=.*[!@#$%^&+=])" +
                        "(?=\\S+$)" +
                        ".{7,}" +
                        "$");

        private Button back;
        private Button next;



        //logic funcations
        private void cleanText(){

        }
        private boolean cheakPassword(){//call 2 funcation plus minimum of 8
            String password=password1.getEditText().getText().toString();

            if(password.length()<8||!ContainSpecial(password)||!noUpper(password)){return false;}
            return true;}

        private boolean noUpper(String password) {//check if it has upper latters
            if(password.equals(password.toLowerCase())){return false;}return true;
        }

        private boolean ContainSpecial(String password) {//check if contain special latters like @!...
            Pattern p = Pattern.compile("[^A-Za-z0-9]");
            Matcher m = p.matcher(password);
            boolean b = m.find();
            if (b)
                return true;
            return false;
        }

        private boolean checkEqualPassword(){
            if(password1.getEditText().getText().toString().equals(password2.getEditText().getText().toString())){return true;}return false;



        }
        private boolean Integritycheck(){//check all fileds have context
            String mail=email.getEditText().getText().toString();
            String password=password1.getEditText().getText().toString();
            String passwordAgain=password2.getEditText().getText().toString();
            String city=this.city.getText().toString();
            String fname=FName.getEditText().getText().toString();
            String Lname=this.LName.getEditText().getText().toString();
            String phone=this.phone.getEditText().getText().toString();
            if(mail.length()<1||password.length()<1||passwordAgain.length()<1||city.length()<1||fname.length()<1||Lname.length()<1||phone.length()<1){return false;}return true;
        }
//    private boolean cheakCity(){}
        private boolean checkMailIntegrity(){//check if it has @

            String mail=email.getEditText().getText().toString();
            if(mail.contains("@")==false){return false;}
            return true;
        }



        TextInputLayout til_city;
        AutoCompleteTextView act_city;
        ArrayList <String> ArrList=new ArrayList<>();
        ArrayList<String> arrayList_city;
        ArrayAdapter<String> arrayAdapter_city;
        @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            try
            {
                this.getSupportActionBar().hide();
            }
            catch (NullPointerException e){}
            setContentView(R.layout.register);
            mAuth = FirebaseAuth.getInstance();
            til_city=(TextInputLayout)findViewById((R.id.til_city));
            act_city= (AutoCompleteTextView) findViewById((R.id.act_city));
            get_json();
            arrayAdapter_city=new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,ArrList);
            act_city.setAdapter(arrayAdapter_city);
            act_city.setThreshold(1);

            email = (TextInputLayout ) findViewById(R.id.Email);
            city = (AutoCompleteTextView) findViewById(R.id.act_city);
            FName = (TextInputLayout) findViewById(R.id.FirstName);
            LName = (TextInputLayout) findViewById(R.id.LastName);
            password1 = (TextInputLayout) findViewById(R.id.Password);
            password2 = (TextInputLayout) findViewById(R.id.passwordAgain);
            phone = (TextInputLayout) findViewById(R.id.PhoneNumber);

          // back =  (Button)findViewById(R.id.back);
            next = (Button) findViewById(R.id.next);
//
//            back.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });

            next.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onClick(View view) {
                    if(!Integritycheck()||!checkMailIntegrity()||!cheakPassword()||!checkEqualPassword()){
                        String msg="";
                        if(!Integritycheck()){msg=msg+"חובה למלא את כל השדות \n";
                       
                        }
                        if (!Patterns.EMAIL_ADDRESS.matcher( email.getEditText().getText().toString()).matches()) {//if the email is proper
                           msg=msg+"אימייל לא תקין\n";

                        }
                        //if(!mailInUse()){msg=msg+"mail in use";}
                        if(!PASSWORD_PATTERN.matcher(password1.getEditText().getText().toString()).matches()){//if the password is proper
                            msg=msg+"הסיסמה חייבת להכיל אותיות קטנות, גדולות, תווים מיוחדים ומספרים\n";

                        }
                        if(!checkEqualPassword()){msg=msg+"סיסמאות לא תואמות\n";

                        }

                        Toast.makeText(view.getContext(), msg, 5000 ).show();

                    }
                    else{
                        mailSTR=email.getEditText().getText().toString();
                        passwordSTR=password1.getEditText().getText().toString();
                        fNameSTR=FName.getEditText().getText().toString();
                        LNameSTR=LName.getEditText().getText().toString();
                        citySTR=city.getText().toString();
                        phoneNumberSTR=phone.getEditText().getText().toString();
                        Intent intent = new Intent(view.getContext(), register2.class);
                        startActivity(intent);
                    }
                }

            });

        }
        private boolean mailInUse() {
            String mail=email.getEditText().getText().toString();
            final boolean[] re = {false};
            Object object=new Object();

            mAuth.fetchSignInMethodsForEmail(mail)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                            if (isNewUser) {
                                re[0] =true;
                                System.out.println(true);
                            }
                            object.notify();

                        }

                    });
            synchronized (object){
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }            return re[0];
        }


        public  void get_json(){
            String json;
            try{
                InputStream is =getAssets().open("citys.json");
                int size =is.available();
                byte[] buffer=new byte [size];
                is.read(buffer);
                is.close();
                json =new String(buffer,"UTF-8");
                JSONArray jsonArray= new JSONArray(json);
                for(int i =0 ;i< jsonArray.length();i++){
                    JSONObject obj=jsonArray.getJSONObject(i);
                    ArrList.add(obj.getString("cityName"));

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public static class  register2 extends AppCompatActivity {
        private DocumentReference mDocRef= FirebaseFirestore.getInstance().document("contact/contact");
        private Button back;
        private Button next;
        private TextView textshow;
        private CheckBox Aggre;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register2);
//            back=(Button) findViewById(R.id.backPage2Reg);
            next=(Button) findViewById(R.id.nextPage2Reg);
            textshow=(TextView)findViewById(R.id.textViewRegPage2);
            textshow.setMovementMethod(new ScrollingMovementMethod());
            Aggre=(CheckBox)findViewById(R.id.page2RegAggre);

            //create file named mDocRef ,get instance from contact/contack ~ path to doc
            //call get ,on succeeds will save the data in dataToSave(comes in map file),then show on textView
            final Map<String, Object>[] dataToSave = new Map[]{new HashMap<String, Object>()};
            mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    dataToSave[0] = documentSnapshot.getData();
                    String str=dataToSave[0].get("contact").toString();
                    textshow.setText(str);
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Aggre.isChecked()) {
                        Intent intent = new Intent(v.getContext(), register3.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(register2.this, "אנא אשרו את הסכמתכם לחוזה על מנת להירשם"
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            });






        }
    }

    public static class register3 extends AppCompatActivity {
        private static FirebaseAuth mAuth;
        private Button test;
        private void Register(String mailSTR, String passwordSTR, String fNameSTR, String lNameSTR, String citySTR, String phoneNumberSTR) {
            //register,first create user , with email and password, if successful , it will create dataToSave object, then send it to real time database
            mAuth.createUserWithEmailAndPassword(mailSTR,passwordSTR)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final Map<String, Object>[] dataToSave = new Map[]{new HashMap<String, Object>()};
                                dataToSave[0].put("First Name:",fNameSTR);
                                dataToSave[0].put("Sec Name:",LNameSTR);
                                dataToSave[0].put("City:",citySTR);
                                dataToSave[0].put("Email:",mailSTR);
                                dataToSave[0].put("Phone:",phoneNumberSTR);
                                dataToSave[0].put("volunteer","false");


                                FirebaseDatabase.getInstance().getReference("Users")
                                        //here we creating users folder in real time data baes , getting uid from user and storing the data in folder named by id
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(dataToSave[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(register.register3.this,
                                                    R.string.register_toast_success,Toast.LENGTH_LONG).show();
                                            {//adds all new data for newely registered clients
                                                dayTime dtDEF = new dayTime(0, 0, 23, 59);//default for first time
                                                Gson gson = new Gson();
                                                SharedPreferences sp = getSharedPreferences(constants.SHARED_PREFS,
                                                        Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sp.edit();
                                                String dayDEF = gson.toJson(dtDEF);
                                                Map<String, String> docData = new HashMap<>();
                                                for(String day: constants.daysNames){
                                                    docData.put(day,dayDEF);
                                                    editor.putString(day,dayDEF);
                                                }
                                                docData.put("range",""+10.0);
                                                editor.putFloat("range",(float)10.0);

                                                docData.put("location","city");
                                                editor.putString("location","city");

                                                FirebaseAuth userIdentifier=FirebaseAuth.getInstance();
                                                String UID = userIdentifier.getCurrentUser().getUid();
                                                DocumentReference DRF = FirebaseFirestore.getInstance()
                                                        .document("Users/"+UID);
                                                final boolean[] success = {true};
                                                final Exception[] failToRet = new Exception[1];
                                                DRF.set(docData).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        success[0] = false;failToRet[0]=e;
                                                    }
                                                });
                                                if(!success[0]){
                                                    // show/make log for case of failure
                                                }
                                                editor.apply();

                                            }
                                            }
                                    }
                                });


                            }

                        }
                    });
        }



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register3);
            test=(Button) findViewById(R.id.testB);
            mAuth = FirebaseAuth.getInstance();
            test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Register(mailSTR,passwordSTR,fNameSTR,LNameSTR,citySTR,phoneNumberSTR);
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }Intent intent = new Intent(v.getContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }

    }



}




























//package com.example.testing;
//
//import android.content.SharedPreferences;
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class MainActivity extends AppCompatActivity {
//    private TextView textView;
//    private EditText editText;
//    private Button applyTextButton;
//    private Button saveButton;
//    private Switch switch1;
//
//    private Button applyTextButton2;
//    private Button saveButton2;
//    private Switch switch2;
//
//    private Button LOAD1;
//    private Button LOAD2;
//
//    public static final String SHARED_PREFS = "sharedPrefs";
//    public static final String TEXT = "text";
//    public static final String SWITCH1 = "switch1";
//    public static final String TEXT2 = "text2";
//    public static final String SWITCH2 = "switch2";
//
//    private String text;
//    private boolean switchOnOff;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        textView = (TextView) findViewById(R.id.textview);
//        editText = (EditText) findViewById(R.id.edittext);
//        applyTextButton = (Button) findViewById(R.id.apply_text_button);
//        saveButton = (Button) findViewById(R.id.save_button);
//        switch1 = (Switch) findViewById(R.id.switch1);
//
//        saveButton2 = (Button) findViewById(R.id.save2);
//        switch2 = (Switch) findViewById(R.id.switch2);
//        applyTextButton2 = (Button) findViewById(R.id.apply2);
//
//        LOAD1 = (Button) findViewById(R.id.LOAD1);
//        LOAD2 = (Button) findViewById(R.id.LOAD2);
//
//        applyTextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                textView.setText(editText.getText().toString());
//            }
//        });
//
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                saveData(TEXT,SWITCH1,switch1);
//            }
//        });
//        applyTextButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                textView.setText(editText.getText().toString());
//            }
//        });
//        saveButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                saveData(TEXT2,SWITCH2,switch2);
//            }
//        });
//        LOAD1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadData(TEXT,SWITCH1);
//            }
//        });
//
//        LOAD2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadData(TEXT2,SWITCH2);
//            }
//        });
//
//
//
//    }
//
//    public void saveData(String text,String switchD,Switch switchL) {
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        editor.putString(text, textView.getText().toString());
//        editor.putBoolean(switchD, switchL.isChecked());
//
//        editor.apply();
//
//        Toast.makeText(this, "Data saved-"+text, Toast.LENGTH_SHORT).show();
//    }
//
//    public void loadData(String texti,String switchD) {
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        text = sharedPreferences.getString(texti, "");
//        switchOnOff = sharedPreferences.getBoolean(switchD, false);
//    }
//
//    public void updateViews() {
//        textView.setText(text);
//        switch1.setChecked(switchOnOff);
//        switch2.setChecked(switchOnOff);
//    }
//}