
package com.example.tazpitapp;
        import androidx.appcompat.app.AppCompatActivity;
        import android.widget.Button;
        import android.content.Intent;
        import android.view.View;
        import android.os.Bundle;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

public class  SceneriosDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;


    public Button button_sign_event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerios_detail);
        user = FirebaseAuth.getInstance().getCurrentUser();
        button_sign_event = (Button)findViewById(R.id.buttonScenario);
        button_sign_event.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonScenario:
                if(user!=null)
                {
                    String id = user.getUid();
                }
                Intent intent = new Intent(SceneriosDetailActivity.this,fillReport.class);
               startActivity(intent);
               break;
        }
    }
}