
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

public class  SceneriosDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    String pressed_scenario = "";
    TextView type_of_event;
    TextView city_of_event;
    TextView gps_event;
    public Button button_sign_event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerios_detail);
        user = FirebaseAuth.getInstance().getCurrentUser();
        button_sign_event = (Button)findViewById(R.id.buttonScenario);
        type_of_event = (TextView)findViewById(R.id.eventType);
        city_of_event = (TextView)findViewById(R.id.cityGetScenario);
        gps_event = (TextView)findViewById(R.id.gpsLink);
        button_sign_event.setOnClickListener(this);
        System.out.println(getIntent().getStringExtra("item_id"));
        System.out.println(getIntent().getStringExtra("item_content"));
        pressed_scenario = getIntent().getStringExtra(SceneriosDetailFragment.ARG_ITEM_CONTENT);

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
        
    }
    @Override
    public void onClick(View v) {

        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
        passwordResetDialog.setTitle("מילוי דוח ?");
        passwordResetDialog.setMessage("האם אתה רוצה למלא דוח על האירוע?");
        passwordResetDialog.setPositiveButton("מלא דוח", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SceneriosDetailActivity.this,fillReport.class);
                intent.putExtra("pressed scenario", pressed_scenario);
                startActivity(intent);
            }
        });
        passwordResetDialog.setNegativeButton("בטל הרשמה", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close the dialog
                return;
            }
        });
        passwordResetDialog.create().show();
    }

}