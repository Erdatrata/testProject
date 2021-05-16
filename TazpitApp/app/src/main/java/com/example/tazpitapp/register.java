package com.example.tazpitapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
///////////////////////////
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
///////////
import android.widget.Toast;



class DataRegister {
        private String mail;
        private int password;
    private  String fName;
    private   String LName;
    private String city;
    private  int phoneNumber;
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}






public class register extends AppCompatActivity {
    //logic funcations

    private void cleanText(){

    }
//    private boolean cheakPassword(String password){if(password.length()<8||!ContainSpecial(password)||)}

    private boolean ContainSpecial(String password) {
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(password);
        boolean b = m.find();
        if (b)
            return true;
        return false;
    }

//    private boolean checkEqualPassword(String password,String againPassword){}
//    private boolean Integritycheck(String mail,String password,String passwordAgain,String city,String fname,String Lname,String phone){}
//    private boolean checkMailWithDB(){}
//    private boolean cheakCity(){}
//    private boolean checkPhone(){}
//    private boolean checkMailIntegrity(){}



    TextInputLayout til_city;
    AutoCompleteTextView act_city;
    ArrayList <String> ArrList=new ArrayList<>();
    ArrayList<String> arrayList_city;
    ArrayAdapter<String> arrayAdapter_city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        til_city=(TextInputLayout)findViewById((R.id.til_city));
        act_city=(AutoCompleteTextView)findViewById((R.id.act_city));
        get_json();
        arrayAdapter_city=new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,ArrList);
        act_city.setAdapter(arrayAdapter_city);
        act_city.setThreshold(1);

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


class register2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
    }
}

class register3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
    }
}