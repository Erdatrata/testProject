package com.example.testing;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainActivity2 extends AppCompatActivity {
    private DocumentReference mDocRef= FirebaseFirestore.getInstance().document("new/new");//fet from firestore new/new doc
    private EditText Name;
    private EditText Inner;
    private Button getData;
    private Button sendData;
    private TextView TextShow;
    private Button remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getData = findViewById(R.id.getAllData);
        sendData = findViewById(R.id.sendDATA);
        Name = findViewById(R.id.name);
        Inner = findViewById(R.id.inner);
        TextShow = findViewById(R.id.textShow);
        remove=findViewById(R.id.remove);

        final Map<String, Object>[] dataToSave = new Map[]{new HashMap<String, Object>()};//file comeing in map object [key,data] example [name,"moshe"] //map comes as array at size one ,cause ansync resons
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {//do get to firestore file
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dataToSave[0] =documentSnapshot.getData();//store file in dataTosave[0]
                        String str=dataToSave[0].toString();
                        TextShow.setText(str);
                    }
                });

            }
        });
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {//get the data first ,change something then reuplod the data to the server
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            dataToSave[0] =documentSnapshot.getData();
                            if(!Name.getText().toString().equals("")&&!Inner.getText().toString().equals(""))
                            {
                                dataToSave[0].put(Name.getText().toString(), Inner.getText().toString());
                                mDocRef.set(dataToSave[0]).addOnSuccessListener(new OnSuccessListener<Void>() {//set into doc new/new on the server what's in dataToSave
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("testing", "saved");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("tesing", "not saved");
                                    }
                                });
                            }
                        }
                    }
                });



            }
        });



        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Name.getText().toString().equals("")) {
                    String remove=Name.getText().toString();
                    mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                dataToSave[0] = documentSnapshot.getData();
                                dataToSave[0].remove(remove);
                                    mDocRef.set(dataToSave[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("testing", "saved");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("tesing", "not saved");
                                        }
                                    });

                            }
                        }
                    });
                }


            }
        });



    }
}