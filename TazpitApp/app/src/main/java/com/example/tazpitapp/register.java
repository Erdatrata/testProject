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


public class register extends AppCompatActivity {
    private static final String[] CITYS = new String[]{
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        String[] citys = getResources().getStringArray(R.array.citysOnTest);




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



        AutoCompleteTextView editText = findViewById(R.id.actv);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.custom_list_item, R.id.text_view_list_item, citys);
        editText.setAdapter(adapter);



        //editText.setThreshold(1);

        //get the input like for a normal EditText
        //String input = editText.getText().toString();
    }
}