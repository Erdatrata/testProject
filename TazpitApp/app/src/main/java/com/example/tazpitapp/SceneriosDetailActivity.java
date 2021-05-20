
package com.example.tazpitapp;

        import androidx.appcompat.app.AppCompatActivity;
        import android.widget.Button;
        import android.content.Intent;
        import android.view.View;
        import android.os.Bundle;

public class  SceneriosDetailActivity extends AppCompatActivity {
    public Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerios_detail);

        button = (Button)findViewById(R.id.buttonScenario);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SceneriosDetailActivity.this,fillReport.class);
                startActivity(intent);
            }
        });
    }
}