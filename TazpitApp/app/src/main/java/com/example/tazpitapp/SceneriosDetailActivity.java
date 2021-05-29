
package com.example.tazpitapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class  SceneriosDetailActivity extends AppCompatActivity {

   // private FirebaseUser user;
    String pressed_scenario = "";
    Button type_of_event;
    TextView city_of_event;
    Button gps_event;
    public Button button_sign_event;
    public Button btnScenarioCancel;
    public Button btnScenarioFillReport;
    public boolean userAccept=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerios_detail);
        button_sign_event = (Button)findViewById(R.id.buttonScenario);
        btnScenarioCancel = (Button)findViewById(R.id.buttonScenarioCancel);
        btnScenarioFillReport = (Button)findViewById(R.id.buttonScenarioFillReport);
        type_of_event = (Button)findViewById(R.id.eventType);
        city_of_event = (TextView)findViewById(R.id.cityGetScenario);
        gps_event = (Button)findViewById(R.id.gpsLink);
        button_sign_event.setVisibility(View.INVISIBLE);
           btnScenarioCancel.setVisibility(View.INVISIBLE);
         btnScenarioFillReport.setVisibility(View.INVISIBLE);
        pressed_scenario = getIntent().getStringExtra(SceneriosDetailFragment.ARG_ITEM_CONTENT);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =db.collection("Scenarios").document(pressed_scenario);
        is_user_is_accepted(docRef, user); //checks if user accepted before the Event
        showTheScenarioDetail(docRef); //decides which buttons to show according whether the user is signed as accepted or not
        //when user will click it he will be added in list of users that accepted the event
        button_sign_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pushing the user id under accepted in the scenario
                Map<String, Object> data = new HashMap<>();
                data.put("isSigned", true);
                docRef.collection("accepted").document(user.getUid()).set(data);
                finish();
                startActivity(getIntent());
            }
        });
        //if user decides to cancel, we will remove him from the server under accepted
        btnScenarioCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove the user id from the accepted in the scenario
               removeUserFromAccept(docRef, user);

            }
        });
        //the user will fill report from that point
        btnScenarioFillReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user decides to fill report and we take him to fill report activity
                String data = getIntent().getStringExtra(SceneriosDetailFragment.ARG_ITEM_CONTENT);
                Intent intent = new Intent(SceneriosDetailActivity.this,fillReport.class);
                intent.putExtra("pressed scenario", data);
                startActivity(intent);

            }
        });


    }



    private void is_user_is_accepted(DocumentReference docRef, FirebaseUser user)
    {

        docRef.collection("accepted").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int indicator=0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(user.getUid().toString().equals(document.getId().toString())) {
                                indicator=1;
                                Log.d("natigabi2", "hatamaaaaaa");

                            }
                        }
                        if(indicator==1){
                            button_sign_event.setVisibility(View.INVISIBLE);
                            btnScenarioCancel.setVisibility(View.VISIBLE);
                            btnScenarioFillReport.setVisibility(View.VISIBLE);
                        }
                        if(indicator==0){
                            button_sign_event.setVisibility(View.VISIBLE);
                            btnScenarioCancel.setVisibility(View.INVISIBLE);
                            btnScenarioFillReport.setVisibility(View.INVISIBLE);
                        }

                    }
                    else {
                        Log.d("natigabi", "Error getting documents: ", task.getException());
                    }
                }
            });

    }
private void showTheScenarioDetail(DocumentReference docRef){
    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                //the user will click on "לפרטים נוספים" to see information about the event and will see alert
                type_of_event.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(SceneriosDetailActivity.this).create();
                        alertDialog.setTitle("סוג האירוע");
                        alertDialog.setMessage(document.get("סוג האירוע").toString());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "הבנתי",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });
                //presenting the city of the event
                city_of_event.setText("עיר האירוע: "+document.get("עיר").toString());
                //by clicking on "לחץ למיקום" the user can see the location in apps like waze\google maps\moovit...
                gps_event.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        GeoPoint geoPoint = document.getGeoPoint("מיקום");
                        String uri = "geo:" + geoPoint.getLatitude() + ","
                                +geoPoint.getLongitude() + "?q=" + geoPoint.getLatitude()
                                + "," + geoPoint.getLongitude();
                        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(uri)));
                    }
                });

                if (document.exists()) {
                    Log.d("nati_test", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("nati_test", "No such document");
                }
            } else {
                Log.d("nati_test", "get failed with ", task.getException());
            }
        }
    });
}
private void removeUserFromAccept(  DocumentReference docRef, FirebaseUser user){
    docRef.collection("accepted").document(user.getUid())
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("test55", "DocumentSnapshot successfully deleted!");
                    finish();
                    startActivity(getIntent());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("test56", "Error deleting document", e);
                }
            });
}
}