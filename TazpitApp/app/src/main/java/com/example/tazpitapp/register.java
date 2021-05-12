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
///////////////////////////

import com.google.android.material.textfield.TextInputLayout;
///////////

public class register extends AppCompatActivity {
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


//package com.example.tazpitapp;
//
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//
//
//public class register extends AppCompatActivity {
//    private static final String[] CITYS = new String[]{
//            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola"
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.register);
//        String[] citys = getResources().getStringArray(R.array.citysOnTest);




        ////////////////////////////////////////////////
        //triyng to read a json file , still in work  //
        ////////////////////////////////////////////////
        /*String json;
        try {


    InputStream is =getAssets().open("citys.json");
    int size = is.available();
    byte[] buffer=new byte[size];
    is.read();
    is.close();
    json=new String(buffer,"UTF-8");
    JSONArray jsonArray=new JSONArray(json);
            ArrayList<String> citys=new ArrayList<>();
    for(int i=0;i<jsonArray.length();i++){
        JSONObject obj=jsonArray.getJSONObject(i);
        citys.add(obj.getString("englishName"));


    }
            AutoCompleteTextView editText = findViewById(R.id.actv);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.custom_list_item, R.id.text_view_list_item, citys);
            editText.setAdapter(adapter);
}
catch (JSONException | IOException e){

            e.printStackTrace();
        }
*/



//        AutoCompleteTextView editText = findViewById(R.id.actv);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.custom_list_item, R.id.text_view_list_item, citys);
//        editText.setAdapter(adapter);



        //editText.setThreshold(1);

        //get the input like for a normal EditText
        //String input = editText.getText().toString();
//    }
//}