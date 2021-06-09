
package com.example.tazpitapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class  SceneriosDetailActivity extends AppCompatActivity {

   // private FirebaseUser user;
    String pressed_scenario = "";
    TextView type_of_event;
    TextView city_of_event;
    Button gps_event;
    public Button button_sign_event;
    public Button btnScenarioCancel;
    public Button btnScenarioFillReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerios_detail);
        button_sign_event = findViewById(R.id.buttonScenario);
        btnScenarioCancel = findViewById(R.id.buttonScenarioCancel);
        btnScenarioFillReport = findViewById(R.id.buttonScenarioFillReport);
        type_of_event = findViewById(R.id.eventType);
        city_of_event = findViewById(R.id.cityGetScenario);
        gps_event = findViewById(R.id.gpsLink);
        button_sign_event.setVisibility(View.INVISIBLE);
           btnScenarioCancel.setVisibility(View.INVISIBLE);
         btnScenarioFillReport.setVisibility(View.INVISIBLE);
        pressed_scenario = getIntent().getStringExtra(SceneriosDetailFragment.ARG_ITEM_CONTENT);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =db.collection(constants.DOC_REF_SCENARIOS).document(pressed_scenario);
        is_user_is_accepted(docRef, user); //checks if user accepted before the Event
        showTheScenarioDetail(docRef); //decides which buttons to show according whether the user is signed as accepted or not
        //when user will click it he will be added in list of users that accepted the event
        button_sign_event.setOnClickListener(v -> {
            //pushing the user id under accepted in the scenario
            Map<String, Object> data = new HashMap<>();
            data.put(constants.ISSIGEND, true);
            docRef.collection(constants.DOC_REF_ACCEPTED).document(user.getUid()).set(data);
            finish();
            startActivity(getIntent());
        });
        //if user decides to cancel, we will remove him from the server under accepted
        btnScenarioCancel.setOnClickListener(v -> {
            //remove the user id from the accepted in the scenario
            if (user != null) {
                removeUserFromAccept(docRef, user);
            }

        });
        //the user will fill report from that point
        btnScenarioFillReport.setOnClickListener(v -> {
            //user decides to fill report and we take him to fill report activity
            String data = getIntent().getStringExtra(SceneriosDetailFragment.ARG_ITEM_CONTENT);
            Intent intent = new Intent(SceneriosDetailActivity.this,fillReport.class);
            intent.putExtra(constants.PRESSED_SCENARIO, data);
            startActivity(intent);

        });

    }

    private void is_user_is_accepted(DocumentReference docRef, FirebaseUser user)
    {

         docRef.collection(constants.DOC_REF_ACCEPTED).get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         int indicator=0;
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             if(user.getUid().toString().equals(document.getId().toString())) {
                                 indicator=1;
                                 Log.d("ISSECCSEFUL", "match");

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
                         Log.d("ISFAILURE", "Error getting documents: ", task.getException());
                     }
                 });


    }
private void showTheScenarioDetail(DocumentReference docRef){
    docRef.get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {

            DocumentSnapshot document = task.getResult();
            //the user will click on "לפרטים נוספים" to see information about the event and will see alert
        type_of_event.setText(getResources().getString(R.string.more_details)+document.get(constants.SCENARIO_TYPE_EVENT).toString());
            type_of_event.setMovementMethod(new ScrollingMovementMethod());
            //presenting the city of the event
            city_of_event.setText(getResources().getString(R.string.place_scenario_city)+document.get(getResources().getString(R.string.detail_scenario_city)).toString());
            //by clicking on "לחץ למיקום" the user can see the location in apps like waze\google maps\moovit...
            gps_event.setOnClickListener(v -> {
                GeoPoint geoPoint = document.getGeoPoint(constants.SCENARIO_LOCATION);
                String uri = constants.GEO + geoPoint.getLatitude() + ","
                        +geoPoint.getLongitude() + "?q=" + geoPoint.getLatitude()
                        + "," + geoPoint.getLongitude();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(uri)));
            });

            if (document.exists()) {
                Log.d("document_exists", "DocumentSnapshot data: " + document.getData());
            } else {
                Log.d("document not exist", "No such document");
            }
        } else {
            Log.d("notseccesful", "get failed with ", task.getException());
        }
    });
}
private void removeUserFromAccept(  DocumentReference docRef, FirebaseUser user){
    docRef.collection(constants.DOC_REF_ACCEPTED).document(user.getUid())
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d("test55", "DocumentSnapshot successfully deleted!");
                finish();
                startActivity(getIntent());
            })
            .addOnFailureListener(e -> Log.w("test56", "Error deleting document", e));
}
}