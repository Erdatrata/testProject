
package com.example.tazpitapp;
        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.DialogInterface;
        import android.util.Log;
        import android.widget.Button;
        import android.content.Intent;
        import android.view.View;
        import android.os.Bundle;
        import android.widget.TextView;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
        import com.google.firebase.firestore.QuerySnapshot;

public class  SceneriosDetailActivity extends AppCompatActivity {

    private FirebaseUser user;
    String pressed_scenario = "";
    TextView type_of_event;
    TextView city_of_event;
    TextView gps_event;
    public Button button_sign_event;
    public Button btnScenarioCancel;
    public Button btnScenarioFillReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerios_detail);
        user = FirebaseAuth.getInstance().getCurrentUser();
        button_sign_event = (Button)findViewById(R.id.buttonScenario);
        btnScenarioCancel = (Button)findViewById(R.id.buttonScenarioCancel);
        btnScenarioFillReport = (Button)findViewById(R.id.buttonScenarioFillReport);
        type_of_event = (TextView)findViewById(R.id.eventType);
        city_of_event = (TextView)findViewById(R.id.cityGetScenario);
        gps_event = (TextView)findViewById(R.id.gpsLink);
        System.out.println(getIntent().getStringExtra("item_id"));
        System.out.println(getIntent().getStringExtra("item_content"));
        pressed_scenario = getIntent().getStringExtra(SceneriosDetailFragment.ARG_ITEM_CONTENT);
        is_user_is_accepted();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Scenarios").document(pressed_scenario);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    document.get("עיר").toString();
                    type_of_event.setText(document.get("סוג האירוע").toString());
                     city_of_event.setText(document.get("עיר").toString());
                    // gps_event.setText(document.get("מיקום").toString());
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
//        btnScenarioFillReport.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//
//            }
//            });

    }

    private boolean is_user_is_accepted()
    {
        boolean[] temp = {false};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Scenarios").document(pressed_scenario);
        docRef.collection("accepted").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("natigabi", document.getId().toString());
                        }
                        temp[0] = true;
                    }
                    else {
                        Log.d("natigabi", "Error getting documents: ", task.getException());
                    }
                }
            });

        return temp[0];
    }

}